package com.example.musicapp.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.adapter.SongAdapter
import com.example.musicapp.databinding.FragmentSearchBinding
import com.example.musicapp.model.SongModel
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<SongModel>()
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        songAdapter = SongAdapter(songList) { song ->
            // Handle song selection here (e.g., play song)
        }
        binding.searchResultsRecyclerView.adapter = songAdapter

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("songs")

        // Add text change listener for search input
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    searchSongs(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return binding.root
    }

    private fun searchSongs(query: String) {
        if (query.isEmpty()) {
            songList.clear()
            songAdapter.notifyDataSetChanged()
            return
        }

        // Query Firebase Database for songs
        databaseRef.orderByChild("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    songList.clear()
                    for (songSnapshot in snapshot.children) {
                        val song = songSnapshot.getValue(SongModel::class.java)
                        song?.let { songList.add(it) }
                    }
                    songAdapter.notifyDataSetChanged()  // Update adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
