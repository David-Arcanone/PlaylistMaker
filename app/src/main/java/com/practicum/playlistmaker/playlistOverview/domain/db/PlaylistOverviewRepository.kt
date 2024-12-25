package com.practicum.playlistmaker.playlistOverview.domain.db

import com.practicum.playlistmaker.playlistOverview.domain.models.TrackAddedToPlaylist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistOverviewRepository {
    fun getTrackOverview(id:Int): Flow<TrackAddedToPlaylist?>
    fun getDataOfTracksFromListOfIds(listOfTrackIds:List<Int>): Flow<List<Track>>
    fun getAllDataOfTracksFromListOfIds(listOfTrackIds:List<Int>): Flow<List<TrackAddedToPlaylist>>
    suspend fun updateTrackOverviewInStorage(trackData:TrackAddedToPlaylist)
    suspend fun createTrackOverviewInStorage(trackData:TrackAddedToPlaylist)
    suspend fun deleteTrackOverviewInStorage(trackId:Int)
}