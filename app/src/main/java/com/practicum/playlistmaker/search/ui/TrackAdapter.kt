package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackShowcaseBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {
    var tracks = mutableListOf<Track>()//TODO
    private var onClick: (Track) -> Unit = {}
    fun setOnClickListener(currentClickListener: (Track) -> Unit) {
        onClick = currentClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val viewBiding =TrackShowcaseBinding.inflate(LayoutInflater.from(parent.context),parent,false)//TODO LayoutInflater.from(parent.context).inflate(R.layout.track_showcase, parent, false)
        return TrackViewHolder(viewBiding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { onClick(tracks[position]) }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}