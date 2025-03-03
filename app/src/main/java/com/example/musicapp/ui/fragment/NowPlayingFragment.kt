package com.example.musicapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.adapter.SongAdapter
import com.example.musicapp.model.SongModel
import com.example.musicapp.service.MediaPlayerService
import com.example.musicapp.ui.activity.PlayerActivity
import com.example.musicapp.viewModel.MediaPlayerViewModel
import com.google.firebase.database.*

class NowPlayingFragment : Fragment() {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<SongModel>()
    private lateinit var databaseRef: DatabaseReference
    private val mediaPlayerViewModel: MediaPlayerViewModel by activityViewModels()

    private lateinit var songImageNP: ImageView
    private lateinit var songTitleNP: TextView
    private lateinit var playPauseBtn: View
    private lateinit var nowPlayingLayout: View

    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        songImageNP = view.findViewById(R.id.songImgNP)
        songTitleNP = view.findViewById(R.id.songNameNP)
        playPauseBtn = view.findViewById(R.id.playPauseBtnNP)
        nowPlayingLayout = view.findViewById(R.id.nowPlayingFrame)

        // RecyclerView setup
        songRecyclerView = view.findViewById(R.id.songList)
        songRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        songAdapter = SongAdapter(songList) { song ->
            mediaPlayerViewModel.setCurrentSong(song)
            playSong(song)
        }
        songRecyclerView.adapter = songAdapter

        // Fetch songs from Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("songs")
        fetchSongsFromDatabase()

        // Observe the current song and update the UI
        mediaPlayerViewModel.currentSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                songTitleNP.text = it.title
                Glide.with(this).load(it.imageUrl).into(songImageNP)
                nowPlayingLayout.visibility = View.VISIBLE
                playPauseBtn.setOnClickListener {
                    togglePlayPause(song)
                }
            }
        }

        // Open PlayerActivity when Now Playing frame is clicked
        nowPlayingLayout.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playSong(song: SongModel) {
        // Stop any currently playing song before starting the new one
        val stopIntent = Intent(requireContext(), MediaPlayerService::class.java).apply {
            putExtra("action", "stop")
        }
        requireContext().startService(stopIntent)

        // Now play the new song
        val playIntent = Intent(requireContext(), MediaPlayerService::class.java).apply {
            putExtra("song", song)
            putExtra("action", "play")
        }
        requireContext().startService(playIntent)

        isPlaying = true
        playPauseBtn.setBackgroundResource(R.drawable.baseline_pause_24) // Update icon to Pause
    }


    private fun pauseSong() {
        val pauseIntent = Intent(requireContext(), MediaPlayerService::class.java).apply {
            putExtra("action", "pause")
        }
        requireContext().startService(pauseIntent)
        isPlaying = false
        playPauseBtn.setBackgroundResource(R.drawable.baseline_play_arrow_24) // Update icon to Play
    }

    private fun togglePlayPause(song: SongModel) {
        if (isPlaying) {
            pauseSong()
        } else {
            playSong(song)
        }
    }

    private fun fetchSongsFromDatabase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                songList.clear()
                for (songSnapshot in snapshot.children) {
                    val song = songSnapshot.getValue(SongModel::class.java)
                    song?.let { songList.add(it) }
                }
                songAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
