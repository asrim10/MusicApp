package com.example.musicapp.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityPlayerBinding
import com.example.musicapp.model.SongModel
import com.example.musicapp.service.MediaPlayerService

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var isPlaying = false
    private var currentSong: SongModel? = null
    private var mediaPlayerService: MediaPlayerService? = null

    private val rotateAnimation by lazy {
        RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 5000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private val handler = Handler()

    private val mediaPlayerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            // Handle broadcast updates from MediaPlayerService (e.g., song progress)
            intent?.let {
                val currentPosition = it.getIntExtra("current_position", 0)
                val duration = it.getIntExtra("duration", 0)
                binding.seekBar.progress = currentPosition
                binding.seekBar.max = duration
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize ViewBinding
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve song data from the Intent
        currentSong = intent.getParcelableExtra("song")
        currentSong?.let {
            // Display the song image using Glide
            Glide.with(this).load(it.imageUrl).into(binding.songImage)
        }

        // Initialize the SeekBar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Optional: Pause music when user starts dragging the SeekBar
                // If you want to stop the music temporarily while seeking, you can add logic here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Update the playback position after user drags the SeekBar
                val newPosition = seekBar?.progress ?: 0
                val serviceIntent = Intent(this@PlayerActivity, MediaPlayerService::class.java).apply {
                    putExtra("action", "seek")
                    putExtra("position", newPosition)
                }
                startService(serviceIntent)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the progress while dragging the SeekBar (optional)
            }
        })

        // Play/Pause button click listener
        binding.playPauseBtn.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                binding.songImage.startAnimation(rotateAnimation)
                binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
                // Start song playback via MediaPlayerService
                currentSong?.let { song ->
                    val serviceIntent = Intent(this, MediaPlayerService::class.java).apply {
                        putExtra("song", song)
                        putExtra("action", "play")
                    }
                    startService(serviceIntent)
                }
            } else {
                binding.songImage.clearAnimation()
                binding.playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24)
                // Pause the song playback via MediaPlayerService
                val serviceIntent = Intent(this, MediaPlayerService::class.java).apply {
                    putExtra("action", "pause")
                }
                startService(serviceIntent)
            }
        }

        // Next button click listener
        binding.nextBtn.setOnClickListener {
            // Logic to skip to the next song (example)
            currentSong = getNextSong()
            currentSong?.let {
                Glide.with(this).load(it.imageUrl).into(binding.songImage)
                // Start playing the next song via MediaPlayerService
                val serviceIntent = Intent(this, MediaPlayerService::class.java).apply {
                    putExtra("song", it)
                    putExtra("action", "play")
                }
                startService(serviceIntent)
            }
        }

        // Previous button click listener
        binding.prevBtn.setOnClickListener {
            // Logic to go to the previous song (example)
            currentSong = getPreviousSong()
            currentSong?.let {
                Glide.with(this).load(it.imageUrl).into(binding.songImage)
                // Start playing the previous song via MediaPlayerService
                val serviceIntent = Intent(this, MediaPlayerService::class.java).apply {
                    putExtra("song", it)
                    putExtra("action", "play")
                }
                startService(serviceIntent)
            }
        }

        // Apply insets to the root view for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Function to get the next song (implement your logic here)
    private fun getNextSong(): SongModel? {
        // Logic to get the next song (e.g., from a playlist)
        return null  // Replace with your actual logic
    }

    // Function to get the previous song (implement your logic here)
    private fun getPreviousSong(): SongModel? {
        // Logic to get the previous song (e.g., from a playlist)
        return null  // Replace with your actual logic
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister any receivers or other cleanup tasks
    }
}
