package com.example.musicapp.model

data class PlaylistModel (
    var playlistId: String = "",         // Unique playlist ID
    var playlistName: String = "",        // Playlist name
    var playlistDesc: String = "", // Optional description
    var playlistImageUrl: String="", // URL for cover image
    var songs: List<SongModel> ? = null// List of tracks in the playlist
)