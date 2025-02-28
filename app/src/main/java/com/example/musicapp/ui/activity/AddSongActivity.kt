package com.example.musicapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.databinding.ActivityAddSongBinding
import com.example.musicapp.model.SongModel
import com.example.musicapp.repository.SongRepositoryImpl

class AddSongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSongBinding
    private lateinit var imageUri: Uri
    private lateinit var audioUri: Uri

    private val songRepository = SongRepositoryImpl()

    // Register audio picker
    private val pickAudioLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                audioUri = it
                binding.tvSelectedAudio.text = "Audio Selected: ${it.lastPathSegment}"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityAddSongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listeners for image and audio buttons
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"  // Pick any image
            startActivityForResult(intent, REQUEST_CODE)
        }

        binding.songImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"  // Pick any image
            startActivityForResult(intent, REQUEST_CODE)
        }

        binding.btnSelectAudio.setOnClickListener {
            pickAudioLauncher.launch("audio/*")
        }

        // Set listener for "Add Song" button
        binding.btnAddSong.setOnClickListener {
            val title = binding.editSongTitle.text.toString().trim()
            val artist = binding.editArtistName.text.toString().trim()

            if (title.isNotEmpty() && artist.isNotEmpty() && ::imageUri.isInitialized && ::audioUri.isInitialized) {
                val songModel = SongModel(
                    songId = "", // Firestore will generate the ID
                    title = title,
                    artist = artist,
                    imageUrl = "",
                    audioUrl = ""
                )

                // Upload image and audio, then add the song to Firestore
                songRepository.addSong(songModel, imageUri, audioUri, this) { success, message ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Song added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields and select an image and audio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Override onActivityResult to handle the image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected image
            data?.data?.let { uri ->
                imageUri = uri
                binding.songImageView.setImageURI(uri)  // Display the image in the ImageView
            } ?: run {
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1001  // This can be any constant
    }
}
