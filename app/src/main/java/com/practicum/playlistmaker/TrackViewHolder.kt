package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val vhAuthor: TextView = itemView.findViewById(R.id.track_author)
    private val vhName: TextView = itemView.findViewById(R.id.track_name)
    private val vhImage: ImageView = itemView.findViewById(R.id.track_image)
    private val vhTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(item: Track) {
        val trackName = item.trackName
        val artistName = item.artistName
        val trackTime = item.showTrackTime()
        val imageUrl = item.artWorkUrl100
        vhAuthor.text = artistName
        vhName.text = trackName
        vhTime.text = trackTime
        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    Utilities.dpToPx(
                        2f,
                        this.itemView.context
                    )
                )
            ) //работает, сравнил поставив 20f
            .into(vhImage)
    }

}