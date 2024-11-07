package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>, Boolean>> {
        return repository.searchTracks(expression).map { results ->
            Pair(results.tracks, results.isSucceded)
        }
    }

    //работа с панелькой истории
    override fun getSavedTrackHistory(): List<Track> {
        return repository.getSavedTrackHistory()
    }

    override fun saveTrackHistory(newValue: List<Track>) {
        repository.saveTrackHistory(newValue)
    }

    override fun clearHistory() {
        repository.saveTrackHistory(emptyList())//записываю пустую историю
    }

    override fun getSavedTrack(): Track? {
        return repository.getSavedTrack()
    }

    override fun saveCurrentTrack(newValue: Track) {
        repository.saveCurrentTrack(newValue)
    }

    override fun updateHistoryWithNewTrack(newAddedTrack: Track): List<Track> {
        val oldHistoryTracks = mutableListOf<Track>()
        oldHistoryTracks.addAll(getSavedTrackHistory())
        val positionOfDublicateInHistory =
            oldHistoryTracks.indexOfFirst { it.trackId == newAddedTrack.trackId }

        if (positionOfDublicateInHistory != -1) {
            oldHistoryTracks.removeAt(positionOfDublicateInHistory)
        } else if (oldHistoryTracks.size >= 10) {
            oldHistoryTracks.removeAt(9)
        }
        oldHistoryTracks.add(0, newAddedTrack)
        saveTrackHistory(oldHistoryTracks)
        saveCurrentTrack(newAddedTrack)
        return oldHistoryTracks
    }
}