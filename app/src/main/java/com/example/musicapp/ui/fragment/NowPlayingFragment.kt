package com.example.musicapp.ui.fragment

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.adapter.SongAdapter
import com.example.musicapp.model.SongModel
import com.example.musicapp.ui.activity.PlayerActivity
import com.example.musicapp.viewModel.MediaPlayerViewModel
import com.google.firebase.database.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class NowPlayingFragment : Fragment() {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<SongModel>()
    private lateinit var databaseRef: DatabaseReference
    private val mediaPlayerViewModel: MediaPlayerViewModel by activityViewModels()

    private lateinit var songImageNP: ImageView
    private lateinit var songTitleNP: TextView
    private lateinit var nowPlayingLayout: View
    private lateinit var playPauseBtnNP: ExtendedFloatingActionButton
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        songImageNP = view.findViewById(R.id.songImgNP)
        songTitleNP = view.findViewById(R.id.songNameNP)
        nowPlayingLayout = view.findViewById(R.id.nowPlayingFrame)
        playPauseBtnNP = view.findViewById(R.id.playPauseBtnNP)

        // RecyclerView setup
        songRecyclerView = view.findViewById(R.id.songList)
        songRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        songAdapter = SongAdapter(songList) { song ->
            playAudio(song) // Play audio when a song is selected
            mediaPlayerViewModel.setCurrentSong(song) // Set the song in ViewModel
        }
        songRecyclerView.adapter = songAdapter

        // Fetch all songs from Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("songs")
        fetchSongsFromDatabase()

        // Observe currently playing song
        mediaPlayerViewModel.currentSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                songTitleNP.text = it.title
                Glide.with(this).load(it.imageUrl).into(songImageNP)
                nowPlayingLayout.visibility = View.VISIBLE // Show Now Playing frame
                playPauseBtnNP.setIconResource(if (mediaPlayer?.isPlaying == true) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24)
            }
        }

        // Handle play/pause button click
        playPauseBtnNP.setOnClickListener {
            togglePlayPause()
        }

        // Open PlayerActivity when Now Playing frame is clicked
        nowPlayingLayout.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            startActivity(intent)
        }
    }

    // Fetch songs from Firebase
    private fun fetchSongsFromDatabase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                songList.clear()
                for (songSnapshot in snapshot.children) {
                    val song = songSnapshot.getValue(SongModel::class.java)
                    song?.let { songList.add(it) }
                }
                songAdapter.notifyDataSetChanged() // Notify adapter that data has changed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Play the audio for the selected song
    private fun playAudio(song: SongModel) {
        if (song.audioUrl.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Audio URL is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Release any existing media player and create a new one
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(song.audioUrl)
            prepareAsync()
            setOnPreparedListener {
                start() // Start playing once prepared
                playPauseBtnNP.setIconResource(R.drawable.baseline_pause_24) // Change to pause icon
                mediaPlayerViewModel.setPlayingStatus(true) // Set playback status to playing
            }
            setOnCompletionListener {
                mediaPlayer?.release()
                mediaPlayerViewModel.setPlayingStatus(false) // Set playback status to not playing
                playPauseBtnNP.setIconResource(R.drawable.baseline_play_arrow_24) // Reset to play icon
            }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show()
                mediaPlayer?.release()
                mediaPlayerViewModel.setPlayingStatus(false)
                playPauseBtnNP.setIconResource(R.drawable.baseline_play_arrow_24)
                false
            }
        }
    }

    // Toggle between play and pause
    private fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            mediaPlayerViewModel.setPlayingStatus(false)
            playPauseBtnNP.setIconResource(R.drawable.baseline_play_arrow_24) // Set to play icon
        } else {
            mediaPlayer?.start()
            mediaPlayerViewModel.setPlayingStatus(true)
            playPauseBtnNP.setIconResource(R.drawable.baseline_pause_24) // Set to pause icon
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Release media player when fragment is destroyed
        mediaPlayerViewModel.setPlayingStatus(false) // Set playing status to false
    }
}
