package com.example.musicapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicapp.model.SongModel

class MediaPlayerViewModel : ViewModel() {
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun setPlayingStatus(status: Boolean) {
        _isPlaying.value = status
    }

    private val _currentSong = MutableLiveData<SongModel?>()
    val currentSong: LiveData<SongModel?> = _currentSong

    fun setCurrentSong(song: SongModel) {
        _currentSong.value = song
    }
}
