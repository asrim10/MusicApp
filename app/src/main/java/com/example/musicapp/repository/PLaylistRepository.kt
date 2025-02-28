package com.example.musicapp.repository

import android.content.Context
import android.net.Uri
import com.example.musicapp.model.PlaylistModel

interface PLaylistRepository {

    fun addPlaylist(playlistModel: PlaylistModel,
                   callback:(Boolean,String) -> Unit
    )

    fun updatePlaylist(playlistId : String,
                      data: MutableMap<String,Any>,
                      callback: (Boolean, String) -> Unit)

    fun deletePlaylist(playlistId:String,
                      callback: (Boolean, String) -> Unit)

    fun getPlaylistById(playlistId:String,
                       callback: (PlaylistModel?, Boolean,
                                  String) -> Unit)

    fun getAllPlaylist(callback:
                          (List<PlaylistModel>?,Boolean,
                           String) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri): String?
}