package com.example.musicapp.ui.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.adapter.SongAdapter
import com.example.musicapp.databinding.FragmentHomeBinding
import com.example.musicapp.model.SongModel
import com.example.musicapp.viewModel.MediaPlayerViewModel
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<SongModel>()
    private lateinit var databaseRef: DatabaseReference
    private var mediaPlayer: MediaPlayer? = null
    private val mediaPlayerViewModel: MediaPlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.songList.layoutManager = LinearLayoutManager(requireContext())
        songAdapter = SongAdapter(songList) { song ->
            playAudio(song)
        }
        binding.songList.adapter = songAdapter

        // Setup Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("songs")
        fetchSongsFromDatabase()
        attachSwipeToDelete()

        // Observe MediaPlayer status from ViewModel
        mediaPlayerViewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            if (isPlaying) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
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
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun playAudio(song: SongModel) {
        mediaPlayerViewModel.setCurrentSong(song)

        if (song.audioUrl.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Audio URL is missing", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(song.audioUrl)
            prepareAsync()
            setOnPreparedListener {
                start()
                mediaPlayerViewModel.setPlayingStatus(true)
            }
            setOnCompletionListener {
                release()
                mediaPlayerViewModel.setPlayingStatus(false)
            }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show()
                release()
                mediaPlayerViewModel.setPlayingStatus(false)
                false
            }
        }
    }

    private fun attachSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Ensure the position is valid and the list is not empty
                if (position == RecyclerView.NO_POSITION || position >= songList.size || songList.isEmpty()) {
                    return // Ignore invalid position or if the list is empty
                }

                val songToDelete = songList[position]

                if (songToDelete.songId.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Error: Song ID is missing", Toast.LENGTH_SHORT).show()
                    songAdapter.notifyItemChanged(position)
                    return
                }

                // Delete song from Firebase and the list
                databaseRef.child(songToDelete.songId!!).removeValue().addOnSuccessListener {
                    songList.removeAt(position)
                    songAdapter.notifyItemRemoved(position)
                    Toast.makeText(requireContext(), "Song deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete song", Toast.LENGTH_SHORT).show()
                    songAdapter.notifyItemChanged(position)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.songList)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayerViewModel.setPlayingStatus(false)
    }
}
