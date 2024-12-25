package com.practicum.playlistmaker.playlistOverview.domain.db

import com.practicum.playlistmaker.playlistOverview.domain.models.TrackAddedToPlaylist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistOverviewInteractor {
    suspend fun getTrackData(trackId: Int): Flow<TrackAddedToPlaylist?>
    fun getDataOfTracksFromListOfIds(listOfTrackIds: List<Int>): Flow<List<Track>>
    fun getFullDataOfTracksFromListOfIds(listOfTrackIds: List<Int>): Flow<List<TrackAddedToPlaylist>>
    suspend fun saveNewTrackAddedToPlaylistData(track:Track,listOfPlaylistsIds:List<Int>)
    suspend fun updateTrackAddedToPlaylistData(newData:TrackAddedToPlaylist)
    suspend fun deleteTrackAddedToPlaylistData(redundantDataId:Int)
}