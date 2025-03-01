package com.example.musicapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.musicapp.model.SongModel
import com.example.musicapp.repository.SongRepository

class SongViewModel(val repository: SongRepository) {

    fun addSong(
        songModel: SongModel,
        imageUri: Uri,
        audioUri: Uri,
        context: Context,
        callback: (Boolean, String) -> Unit
    ) {
        repository.addSong(songModel, imageUri, audioUri, context, callback)
    }

    fun updateSong(songId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        repository.updateSong(songId, data, callback)
    }

    fun deleteSong(songId: String, callback: (Boolean) -> Unit) {
        repository.deleteSong(songId, callback)
    }

    var _song = MutableLiveData<SongModel?>()
    var song = MutableLiveData<SongModel?>()
        get() = _song

    var _allSongs = MutableLiveData<List<SongModel>?>()
    var allSongs = MutableLiveData<List<SongModel>?>()
        get() = _allSongs

    fun getSongById(songId: String) {
        repository.getSongById(songId) { song, success, message ->
            if (success) {
                _song.value = song
            }
        }
    }

    var _loadingState = MutableLiveData<Boolean>()
    var loadingState = MutableLiveData<Boolean>()
        get() = _loadingState

    fun getAllSongs() {
        _loadingState.value = true
        repository.getAllSongs { songs ->
            _allSongs.value = songs
            _loadingState.value = false
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repository.uploadImage(context, imageUri, callback)
    }
}
