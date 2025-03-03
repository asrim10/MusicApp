package com.example.musicapp.ui.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.databinding.ActivityAddSongToPlaylistBinding
import com.example.musicapp.model.PlaylistModel
import com.example.musicapp.model.SongModel
import com.example.musicapp.repository.PlaylistRepositoryImpl
import com.example.musicapp.repository.SongRepositoryImpl

class AddSongToPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSongToPlaylistBinding
    private val playlistRepository = PlaylistRepositoryImpl()
    private val songRepository = SongRepositoryImpl()
    private var playlists = mutableListOf<PlaylistModel>()
    private var songs = mutableListOf<SongModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSongToPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPlaylists()
//        loadSongs()
//
//        binding.btnAddSongToPlaylist.setOnClickListener {
//            val selectedPlaylistIndex = binding.spinnerPlaylist.selectedItemPosition
//            val selectedSongIndex = binding.spinnerSongs.selectedItemPosition
//
//            if (selectedPlaylistIndex != -1 && selectedSongIndex != -1) {
//                val playlist = playlists[selectedPlaylistIndex]
//                val song = songs[selectedSongIndex]
//
//                addSongToPlaylist(playlist, song)
//            } else {
//                Toast.makeText(this, "Please select a playlist and a song", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun loadPlaylists() {
        playlistRepository.getAllPlaylist { fetchedPlaylists, success, _ ->
            if (success && fetchedPlaylists != null) {
                playlists.clear()
                playlists.addAll(fetchedPlaylists)

                val playlistNames = playlists.map { it.playlistName }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, playlistNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerPlaylist.adapter = adapter
            }
        }
    }



//    private fun addSongToPlaylist(playlist: PlaylistModel, song: SongModel) {
//        // Check if song ID exists in the playlist's song IDs
//        if (playlist.songs.any { it.songId == song.songId }) {
//            Toast.makeText(this, "Song is already in this playlist", Toast.LENGTH_SHORT).show()
//            return
//        }
//        // Rest of the code remains the same
//        playlistRepository.addSongToPlaylist(playlist.playlistId, song.songId) { success, message ->
//            // Handle callback
//        }
//    }
}
