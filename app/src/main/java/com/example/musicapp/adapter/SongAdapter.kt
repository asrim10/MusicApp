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

class SongAdapter(private val songs: List<SongModel>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songImage: ImageView = itemView.findViewById(R.id.songImage)
        val songTitle: TextView = itemView.findViewById(R.id.songTitle)
        val songArtist: TextView = itemView.findViewById(R.id.songArtist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songTitle.text = song.title
        holder.songArtist.text = song.artist

        // Load image using Picasso
        if (!song.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(song.imageUrl).into(holder.songImage)
        } else {
            holder.songImage.setImageResource(R.drawable.music) // Default image if no URL
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
