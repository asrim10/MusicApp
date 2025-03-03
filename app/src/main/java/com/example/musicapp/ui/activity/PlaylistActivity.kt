package com.example.musicapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.adapter.PlaylistAdapter
import com.example.musicapp.databinding.ActivityPlaylistBinding
import com.example.musicapp.model.PlaylistModel
import com.example.musicapp.repository.PlaylistRepositoryImpl

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistAdapter
    private val repository = PlaylistRepositoryImpl()
    private val playlists = ArrayList<PlaylistModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        adapter = PlaylistAdapter(this, playlists)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        // Floating Button to Add Playlist
        binding.floatingAddPlaylist.setOnClickListener {
            val intent = Intent(this, AddPlaylistActivity::class.java)
            startActivity(intent)
        }

        fetchPlaylists()
    }

    private fun fetchPlaylists() {
        // Show ProgressBar while loading
        binding.progressBar.visibility = View.VISIBLE

        repository.getAllPlaylist { fetchedPlaylists, success, _ ->
            // Hide ProgressBar after loading is complete
            binding.progressBar.visibility = View.GONE

            if (success && fetchedPlaylists != null) {
                playlists.clear()
                playlists.addAll(fetchedPlaylists)
                adapter.notifyDataSetChanged()
            } else {
                // Optionally show a toast or handle error here
                Toast.makeText(this, "Failed to fetch playlists", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
