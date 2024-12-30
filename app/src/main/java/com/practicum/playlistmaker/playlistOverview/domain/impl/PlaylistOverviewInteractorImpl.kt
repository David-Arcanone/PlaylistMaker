package com.practicum.playlistmaker.playlistOverview.domain.impl

import com.practicum.playlistmaker.playlistOverview.domain.db.PlaylistOverviewInteractor
import com.practicum.playlistmaker.playlistOverview.domain.db.PlaylistOverviewRepository
import com.practicum.playlistmaker.playlistOverview.domain.models.TrackAddedToPlaylist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
class PlaylistOverviewInteractorImpl(private val myRepository: PlaylistOverviewRepository) :
    PlaylistOverviewInteractor {
    override suspend fun getTrackData(trackId: Int): Flow<TrackAddedToPlaylist?> {
        return myRepository.getTrackOverview(trackId)
    }
    override fun getDataOfTracksFromListOfIds(listOfTrackIds: List<Int>): Flow<List<Track>>{
        return myRepository.getDataOfTracksFromListOfIds(listOfTrackIds)
    }

    override fun getFullDataOfTracksFromListOfIds(listOfTrackIds: List<Int>): Flow<List<TrackAddedToPlaylist>> {
        return myRepository.getAllDataOfTracksFromListOfIds(listOfTrackIds)
    }
    override suspend fun saveNewTrackAddedToPlaylistData(
        track: Track,
        listOfPlaylistsIds: List<Int>
    ) {
        myRepository.createTrackOverviewInStorage(
            TrackAddedToPlaylist(
            trackId = track.trackId,
            track=track,
            listOfPlaylistsIds=listOfPlaylistsIds)
        )
    }
    override suspend fun updateTrackAddedToPlaylistData(newData: TrackAddedToPlaylist) {
        myRepository.updateTrackOverviewInStorage(newData)
    }

    override suspend fun deleteTrackAddedToPlaylistData(dataId: Int) {
        myRepository.deleteTrackOverviewInStorage(dataId)
    }

}