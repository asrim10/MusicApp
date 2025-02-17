package com.example.musicapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musicapp.R
import com.example.musicapp.ui.activity.AddSongActivity // Import your AddSongActivity
import androidx.cardview.widget.CardView

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)

        // Find the "Add Song" CardView and set click listener
        val addSongCard = view.findViewById<CardView>(R.id.addSong)
        addSongCard.setOnClickListener {
            val intent = Intent(requireContext(), AddSongActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
