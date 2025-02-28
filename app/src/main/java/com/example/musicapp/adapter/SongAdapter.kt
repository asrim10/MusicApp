package com.example.musicapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.model.SongModel
import com.squareup.picasso.Picasso

class SongAdapter(private val songList: List<SongModel>) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.songTitle)
        val songArtist: TextView = view.findViewById(R.id.songArtist)
        val songImage: ImageView = view.findViewById(R.id.songImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist

        // Load image from URL using Picasso
        Picasso.get()
            .load(song.imageUrl)
            .placeholder(R.drawable.placeholder_image) // Optional placeholder
            .error(R.drawable.error_image) // Optional error fallback
            .into(holder.songImage)
    }


    override fun getItemCount(): Int = songList.size
}
