package com.practicum.playlistmaker.search.ui

import android.content.res.Resources
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackShowcaseBinding
import com.practicum.playlistmaker.utils.AndroidUtilities
import com.practicum.playlistmaker.search.domain.models.Track

class TrackViewHolder(private val myBinding:TrackShowcaseBinding) : RecyclerView.ViewHolder(myBinding.root) {

    fun bind(item: Track) {
        myBinding.trackAuthor.maxWidth=Resources.getSystem().getDisplayMetrics().widthPixels/2
        myBinding.trackAuthor.text=item.artistName
        myBinding.trackName.text=item.trackName
        myBinding.trackTime.text=item.trackLengthText
        Glide.with(itemView)
            .load(item.artWorkUrl100)
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
            .into(myBinding.trackImage)
    }

}