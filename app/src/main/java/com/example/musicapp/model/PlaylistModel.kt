package com.example.musicapp.model

data class PlaylistModel (
    var playlistId: String,          // Unique playlist ID
    var name: String,        // Playlist name
    var description: String?, // Optional description
    var coverImageUrl: String?, // URL for cover image
    var tracks: List<SongModel>, // List of tracks in the playlist
)