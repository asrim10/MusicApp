package com.example.musicapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapp.R
import com.example.musicapp.ui.activity.PlayerActivity

class NowPlayingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nowPlayingLayout = view.findViewById<View>(R.id.nowPlayingFrame)

        nowPlayingLayout.setOnClickListener {
            // Open a new Activity or Fragment
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            startActivity(intent)
        }
    }

}