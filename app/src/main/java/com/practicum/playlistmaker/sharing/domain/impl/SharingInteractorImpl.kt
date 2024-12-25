package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository

class SharingInteractorImpl(private val repository: SharingRepository) : SharingInteractor {
    override fun openShare() {
        repository.openShare()
    }

    override fun openSupport() {
        repository.openSupport()
    }

    override fun openUserAgreement() {
        repository.openUserAgreement()
    }


    override fun openSharePlaylist(name: String, description: String, listOfTracks: List<Track>) {
        var newMessageTitle="$name\n"
        if(description.isNotEmpty())newMessageTitle=newMessageTitle +"$description\n"
        var newMessageText: String = ""
        listOfTracks.forEachIndexed { index,
                                      track ->
            newMessageText = newMessageText + "\n" + (1+index) + ". " + track.artistName + " - " + track.trackName + " (" + track.trackLengthText + ")"
        }
        repository.openSharePlaylist(
            playlistText = newMessageText,
            title = newMessageTitle,
            number = listOfTracks.size)
    }
}