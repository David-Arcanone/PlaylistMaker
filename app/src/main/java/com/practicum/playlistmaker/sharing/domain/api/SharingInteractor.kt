package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SharingInteractor {
    fun openShare()
    fun openUserAgreement()
    fun openSupport()
    fun openSharePlaylist(name:String, description:String, listOfTracks:List<Track>)
}