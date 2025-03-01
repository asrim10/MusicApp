package com.example.musicapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.model.PlaylistModel
import com.example.musicapp.repository.PlaylistRepository

class PlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {

    private val _playlists = MutableLiveData<List<PlaylistModel>>()
    val playlists: LiveData<List<PlaylistModel>> get() = _playlists

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchPlaylists() {
        repository.getAllPlaylist { playlists, success, message ->
            if (success) {
                _playlists.postValue(playlists)
            } else {
                _errorMessage.postValue(message)
            }
        }
    }

    fun updatePlaylist(playlistId: String, data: MutableMap<String, Any>) {
        repository.updatePlaylist(playlistId, data) { success, message ->
            if (success) {
                fetchPlaylists()  // Refresh data after update
            } else {
                _errorMessage.postValue(message)
            }
        }
    }

    fun deletePlaylist(playlistId: String) {
        repository.deletePlaylist(playlistId) { success, message ->
            if (success) {
                fetchPlaylists()  // Refresh data after deletion
            } else {
                _errorMessage.postValue(message)
            }
        }
    }
}
