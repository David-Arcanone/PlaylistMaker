package com.practicum.playlistmaker.newPlaylist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistGridShowcaseBinding
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist

class PlaylistGridAdapter : RecyclerView.Adapter<PlaylistGridViewHolder>() {
    var myPlaylists = mutableListOf<Playlist>()
    private var onClick: (Playlist) -> Unit = {}
    fun setOnClickListener(currentClickListener: (Playlist) -> Unit) {
        onClick = currentClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistGridViewHolder {
        val viewBiding =PlaylistGridShowcaseBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaylistGridViewHolder(viewBiding)
    }

    override fun onBindViewHolder(holder: PlaylistGridViewHolder, position: Int) {
        holder.bind(myPlaylists[position])
        holder.itemView.setOnClickListener { onClick(myPlaylists[position]) }
    }

    override fun getItemCount(): Int {
        return myPlaylists.size
    }
}