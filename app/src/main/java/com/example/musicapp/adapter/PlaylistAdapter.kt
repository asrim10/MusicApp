package com.example.musicapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.model.PlaylistModel
import com.example.musicapp.ui.activity.UpdatePlaylistActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.ArrayList

class PlaylistAdapter(val context: Context, var data: ArrayList<PlaylistModel>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.getImage)
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar2)
        val editButton: TextView = itemView.findViewById(R.id.lblEdit)
        val pName: TextView = itemView.findViewById(R.id.displayName)
        val pDesc: TextView = itemView.findViewById(R.id.displayDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView: View = LayoutInflater.from(context)
            .inflate(R.layout.playlist_layout, parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = data[position]

        holder.pName.text = playlist.playlistName
        holder.pDesc.text = playlist.playlistDesc

        Picasso.get().load(playlist.playlistImageUrl).into(holder.imageView, object : Callback {
            override fun onSuccess() {
                holder.loading.visibility = View.GONE
            }

            override fun onError(e: Exception?) {}
        })

        holder.editButton.setOnClickListener {
            val intent = Intent(context, UpdatePlaylistActivity::class.java)
            intent.putExtra("playlist", playlist) // Pass entire PlaylistModel
            context.startActivity(intent)
        }
    }

    fun updateData(newPlaylists: List<PlaylistModel>) {
        data.clear()
        data.addAll(newPlaylists)
        notifyDataSetChanged()
    }
}
