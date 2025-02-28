package com.example.musicapp.ui.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapter.SongAdapter
import com.example.musicapp.model.SongModel
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<SongModel>()
    private lateinit var databaseRef: DatabaseReference
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songRecyclerView = view.findViewById(R.id.songList)
        songRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        songAdapter = SongAdapter(songList) { song ->
            playAudio(song.audioUrl) // Play audio when song is clicked
        }
        songRecyclerView.adapter = songAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("songs")
        fetchSongsFromDatabase()
        attachSwipeToDelete()
    }

    private fun fetchSongsFromDatabase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                songList.clear()
                for (songSnapshot in snapshot.children) {
                    val song = songSnapshot.getValue(SongModel::class.java)
                    song?.let { songList.add(it) }
                }
                songAdapter.notifyDataSetChanged() // Refresh RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun playAudio(audioUrl: String?) {
        if (audioUrl.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Audio URL is missing", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer?.release() // Stop previous audio if playing
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener { start() }
            setOnCompletionListener { release() }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show()
                release()
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
                val songToDelete = songList[position]

                databaseRef.child(songToDelete.songId).removeValue().addOnSuccessListener {
                    songList.removeAt(position)
                    songAdapter.notifyItemRemoved(position)
                    Toast.makeText(requireContext(), "Song deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete song", Toast.LENGTH_SHORT).show()
                    songAdapter.notifyItemChanged(position) // Restore item if deletion fails
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(songRecyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Release media player when fragment is destroyed
    }
}
