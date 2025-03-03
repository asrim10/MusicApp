package com.example.musicapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.databinding.ActivityUpdatePlaylistBinding
import com.example.musicapp.model.PlaylistModel
import com.example.musicapp.repository.PlaylistRepositoryImpl

class UpdatePlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePlaylistBinding
    private val repository = PlaylistRepositoryImpl()
    private var playlistId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve PlaylistModel from intent
        val playlist: PlaylistModel? = intent.getParcelableExtra("playlist")

        playlist?.let {
            playlistId = it.playlistId
            binding.updatePlaylistName.setText(it.playlistName)
            binding.updatePlaylistDesc.setText(it.playlistDesc)
        }

        // Handle Update Button Click
        binding.btnUpdatePlaylist.setOnClickListener {
            val updatedName = binding.updatePlaylistName.text.toString().trim()
            val updatedDesc = binding.updatePlaylistDesc.text.toString().trim()

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Playlist name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (playlistId != null) {
                updatePlaylist(playlistId!!, updatedName, updatedDesc)
            }
        }
    }

    private fun updatePlaylist(id: String, name: String, desc: String) {
        val updateData = mutableMapOf<String, Any>(
            "playlistName" to name,
            "playlistDesc" to desc
        )

        repository.updatePlaylist(id, updateData) { success, message ->
            if (success) {
                Toast.makeText(this, "Playlist updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, message ?: "Failed to update playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
