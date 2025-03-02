package com.example.musicapp.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.musicapp.model.SongModel

class MediaPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentSong: SongModel? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val song = intent?.getParcelableExtra<SongModel>("song")
        val action = intent?.getStringExtra("action")
        if (action == "play" && song != null) {
            playSong(song)
        } else if (action == "pause") {
            pauseSong()
        }

        return START_STICKY
    }

    private fun playSong(song: SongModel) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    stopSelf()
                }
            }
        } else {
            mediaPlayer?.start()
        }
        currentSong = song
    }

    private fun pauseSong() {
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
