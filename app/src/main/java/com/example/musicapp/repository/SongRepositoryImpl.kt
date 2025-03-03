package com.example.musicapp.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.musicapp.model.SongModel
import com.google.firebase.database.*
import java.io.InputStream
import java.util.concurrent.Executors

class SongRepositoryImpl : SongRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val songsRef: DatabaseReference = database.reference.child("songs")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dnx39dnum",
            "api_key" to "129898661198246",
            "api_secret" to "sb0kbl8_vsADUjdMfviLbO1MCTQ"
        )
    )


    override fun addSong(
        songModel: SongModel,
        imageUri: Uri,
        audioUri: Uri,
        context: Context,
        callback: (Boolean, String) -> Unit
    ) {
        val songId = songsRef.push().key ?: return callback(false, "Failed to generate song ID")
        songModel.songId = songId

        // Upload image first
        uploadImage(context, imageUri) { imageUrl ->
            if (imageUrl != null) {
                songModel.imageUrl = imageUrl

                // Then upload audio with context
                uploadAudio(context, audioUri) { audioUrl ->
                    if (audioUrl != null) {
                        songModel.audioUrl = audioUrl

                        // Save song data to Firebase Realtime Database
                        songsRef.child(songId).setValue(songModel)
                            .addOnSuccessListener { callback(true, "Song added successfully") }
                            .addOnFailureListener { callback(false, it.message ?: "Error adding song") }
                    } else {
                        callback(false, "Failed to upload audio")
                    }
                }
            } else {
                callback(false, "Failed to upload image")
            }
        }
    }


    override fun getAllSongs(callback: (List<SongModel>) -> Unit) {
        songsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songList = mutableListOf<SongModel>()
                for (songSnapshot in snapshot.children) {
                    try {
                        // Attempt to get SongModel from Firebase
                        val song = songSnapshot.getValue(SongModel::class.java)
                        if (song != null) {
                            songList.add(song)
                        } else {
                            Log.e("Firebase", "Failed to deserialize song: ${songSnapshot.value}")
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error deserializing song: ${e.message}")
                    }
                }
                callback(songList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching songs: ${error.message}")
                callback(emptyList())
            }
        })
    }



    override fun updateSong(
        songId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        val songRef = FirebaseDatabase.getInstance().getReference("songs").child(songId)

        songRef.updateChildren(data)
            .addOnSuccessListener {
                callback(true, "Song updated successfully")
            }
            .addOnFailureListener { exception ->
                callback(false, "Failed to update song: ${exception.message}")
            }
    }

    override fun deleteSong(songId: String, callback: (Boolean) -> Unit) {
        songsRef.child(songId).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    override fun getSongById(productId: String, callback: (SongModel?, Boolean, String) -> Unit) {
        songsRef.child(productId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var model = snapshot.getValue(SongModel::class.java)
                        callback(model, true, "Data fetched")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, false, error.message.toString())
                }

            })

    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }


    override fun uploadAudio(context: Context, audioUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                // Open InputStream for the audio file
                val inputStream: InputStream? = context.contentResolver.openInputStream(audioUri)

                // Get the file name from the URI
                var fileName = getFileNameFromUri(context, audioUri)
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_audio"

                // Upload the audio file to Cloudinary
                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "video"  // Cloudinary treats audio as "video"
                    )
                )

                // Get the secure URL for the uploaded audio
                var audioUrl = response["secure_url"] as String?

                // Replace http with https in the URL
                audioUrl = audioUrl?.replace("http://", "https://")

                // Pass the result back to the main thread
                Handler(Looper.getMainLooper()).post {
                    callback(audioUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}
