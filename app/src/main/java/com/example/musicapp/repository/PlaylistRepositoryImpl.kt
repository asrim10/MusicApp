package com.example.musicapp.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.musicapp.model.PlaylistModel
import com.google.firebase.database.*
import java.io.InputStream
import java.util.concurrent.Executors
import com.example.musicapp.repository.PlaylistRepository

class PlaylistRepositoryImpl : PlaylistRepository {

    private val databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("playlists")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dnx39dnum",
            "api_key" to "129898661198246",
            "api_secret" to "sb0kbl8_vsADUjdMfviLbO1MCTQ"
        )
    )

    override fun addPlaylist(
        playlistModel: PlaylistModel,
        imageUri: Uri,
        context: Context,
        callback: (Boolean, String) -> Unit
    ) {
        val playlistId =
            databaseRef.push().key ?: return callback(false, "Failed to generate playlist ID")
        playlistModel.playlistId = playlistId

        // Upload cover image
        uploadImage(context, imageUri) { imageUrl ->
            if (imageUrl != null) {
                playlistModel.playlistImageUrl = imageUrl

                // Save to Firebase
                databaseRef.child(playlistId).setValue(playlistModel)
                    .addOnSuccessListener { callback(true, "Playlist added successfully") }
                    .addOnFailureListener { callback(false, it.message ?: "Error adding playlist") }
            } else {
                callback(false, "Failed to upload image")
            }
        }
    }

    override fun updatePlaylist(
        playlistId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        databaseRef.child(playlistId).updateChildren(data)
            .addOnSuccessListener { callback(true, "Playlist updated successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Error updating playlist") }
    }

    override fun deletePlaylist(playlistId: String, callback: (Boolean, String) -> Unit) {
        databaseRef.child(playlistId).removeValue()
            .addOnSuccessListener { callback(true, "Playlist deleted successfully") }
            .addOnFailureListener { callback(false, it.message ?: "Error deleting playlist") }
    }

    override fun getPlaylistById(
        playlistId: String,
        callback: (PlaylistModel?, Boolean, String) -> Unit
    ) {
        databaseRef.child(playlistId).get().addOnSuccessListener { snapshot ->
            val playlist = snapshot.getValue(PlaylistModel::class.java)
            callback(playlist, true, "Playlist fetched successfully")
        }.addOnFailureListener {
            callback(null, false, it.message ?: "Error fetching playlist")
        }
    }

    override fun getAllPlaylist(callback: (List<PlaylistModel>?, Boolean, String) -> Unit) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlists = mutableListOf<PlaylistModel>()
                for (playlistSnapshot in snapshot.children) {
                    val playlist = playlistSnapshot.getValue(PlaylistModel::class.java)
                    playlist?.let { playlists.add(it) }
                }
                callback(playlists, true, "Playlists fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                // Remove file extension for Cloudinary public_id
                fileName = fileName?.substringBeforeLast(".") ?: "playlist_cover"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", "playlists/$fileName", // Store under 'playlists/' folder
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["secure_url"] as String? // Get HTTPS URL

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
