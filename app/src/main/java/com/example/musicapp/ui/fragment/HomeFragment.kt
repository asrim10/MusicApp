package com.example.musicapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapter.SongAdapter
import com.example.musicapp.model.SongModel

class HomeFragment : Fragment() {

     lateinit var songRecyclerView: RecyclerView
     lateinit var songAdapter: SongAdapter
     val songList = mutableListOf<SongModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songRecyclerView = view.findViewById(R.id.songList)
        songRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adding sample songs
        songList.add(SongModel("Blinding Lights", "The Weeknd", R.drawable.music))
        songList.add(SongModel("Shape of You", "Ed Sheeran", R.drawable.music))
        songList.add(SongModel("Levitating", "Dua Lipa", R.drawable.music))
        songList.add(SongModel("Save Your Tears", "The Weeknd", R.drawable.music))

        songAdapter = SongAdapter(songList)
        songRecyclerView.adapter = songAdapter
    }
}
