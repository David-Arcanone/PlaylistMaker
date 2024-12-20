package com.practicum.playlistmaker.newPlaylist.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistSmallShowcaseBinding
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist

class PlaylistSmallAdapter : RecyclerView.Adapter<PlaylistSmallViewHolder>() {
    var myPlaylists = mutableListOf<Playlist>()
    private var onClick: (Playlist) -> Unit = {}
    fun setOnClickListener(currentClickListener: (Playlist) -> Unit) {
        onClick = currentClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistSmallViewHolder {
        val viewBiding =PlaylistSmallShowcaseBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaylistSmallViewHolder(viewBiding)
    }

    override fun onBindViewHolder(holder: PlaylistSmallViewHolder, position: Int) {
        holder.bind(myPlaylists[position])
        holder.itemView.setOnClickListener { onClick(myPlaylists[position]) }
    }

    override fun getItemCount(): Int {
        return myPlaylists.size
    }
}