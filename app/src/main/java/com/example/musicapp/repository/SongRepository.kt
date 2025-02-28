package com.example.musicapp.repository

import android.content.Context
import android.net.Uri
import com.example.musicapp.model.SongModel

interface SongRepository {

    fun addSong(
        songModel: SongModel,
        imageUri: Uri,
        audioUri: Uri,
        context: Context,
        callback: (Boolean, String) -> Unit
    )

    fun updateSong(
        songId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )

    fun deleteSong(songId: String, callback: (Boolean) -> Unit)

    fun getAllSongs(callback: (List<SongModel>) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun uploadAudio(context: Context, audioUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri): String?

}
