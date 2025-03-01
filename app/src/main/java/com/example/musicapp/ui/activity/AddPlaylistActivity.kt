package com.example.musicapp.ui.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.databinding.ActivityAddPlaylistBinding
import com.example.musicapp.model.PlaylistModel
import com.example.musicapp.repository.PlaylistRepositoryImpl

class AddPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlaylistBinding
    private val repository = PlaylistRepositoryImpl()
    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.playlistImg.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistImg.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddPlaylist.setOnClickListener {
            val name = binding.editPlaylistName.text.toString().trim()
            val desc = binding.editPlaylistDesc.text.toString().trim()

            if (name.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Name and Image are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newPlaylist = PlaylistModel("", name, desc, "", emptyList())

            repository.addPlaylist(newPlaylist, imageUri!!, this) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (success) finish()
            }
        }
    }
}
