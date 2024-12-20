package com.practicum.playlistmaker.newPlaylist.domain.models

import android.net.Uri
import com.practicum.playlistmaker.search.domain.models.Track

data class Playlist(
    val playlistName:String,
    val playlistDescription:String?,
    val imgSrc:Uri?,
    val listOfTracks:List<Track>,
    )
