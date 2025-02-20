package com.example.musicapp.ui.activity

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var songImage: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var prevBtn: ImageButton
    private lateinit var shuffleBtn: ImageButton

    private var isPlaying = false
    private val rotateAnimation by lazy {
        RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 5000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songImage = binding.songImage
        seekBar = binding.seekBar
        playPauseBtn = binding.playPauseBtn
        nextBtn = binding.nextBtn
        prevBtn = binding.prevBtn
        shuffleBtn = binding.shuffleBtn

        playPauseBtn.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                songImage.startAnimation(rotateAnimation)
                playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
            } else {
                songImage.clearAnimation()
                playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        }

        // Apply insets to the correct root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
