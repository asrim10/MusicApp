package com.example.musicapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        songAdapter = SongAdapter(songList)
        songRecyclerView.adapter = songAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("songs") // Make sure this path matches your database
        fetchSongsFromDatabase()
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
                // Handle error (log or show a toast)
            }
        })
    }
}
