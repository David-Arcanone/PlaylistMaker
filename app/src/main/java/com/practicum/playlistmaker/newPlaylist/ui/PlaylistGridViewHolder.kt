package com.practicum.playlistmaker.newPlaylist.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistGridShowcaseBinding
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.utils.AndroidUtilities

class PlaylistGridViewHolder(private val myBinding: PlaylistGridShowcaseBinding) :
    RecyclerView.ViewHolder(myBinding.root) {

    fun bind(item: Playlist) {
        myBinding.playlistName.text = item.playlistName
        myBinding.tracksNumber.text = item.listOfTracks.size.toString()
        //0 -треков/tracks, 1 -трек/track, 2+ -треков/tracks
        val tracksPronounce = if (item.listOfTracks.size == 1) R.string.track else R.string.tracks
        myBinding.tracksPronounce.setText(tracksPronounce)
        Glide.with(itemView)
            .load(item.imgSrc)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    AndroidUtilities.dpToPx(
                        2f,
                        this.itemView.context //myBinding.root.context
                    )
                )
            )
            .into(myBinding.playlistImage)
    }

}