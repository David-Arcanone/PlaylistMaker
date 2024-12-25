package com.practicum.playlistmaker.sharing.domain.api

interface SharingRepository {
    fun openSupport()
    fun openShare()
    fun openUserAgreement()
    fun openSharePlaylist(playlistText:String,title: String, number: Int)
}