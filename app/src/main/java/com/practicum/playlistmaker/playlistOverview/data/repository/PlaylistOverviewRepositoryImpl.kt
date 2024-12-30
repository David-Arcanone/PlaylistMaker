package com.practicum.playlistmaker.playlistOverview.data.repository


import com.practicum.playlistmaker.root.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.playlistOverview.data.converters.TrackAddedToPlaylistsDbConvertor
import com.practicum.playlistmaker.playlistOverview.domain.db.PlaylistOverviewRepository
import com.practicum.playlistmaker.playlistOverview.domain.models.TrackAddedToPlaylist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistOverviewRepositoryImpl(
    private val myDatabase: AppPlaylistMakerDatabase,
    private val myTrackAddedToPlaylistsDbConvertor: TrackAddedToPlaylistsDbConvertor
) : PlaylistOverviewRepository {
    val myDatabaseDao = myDatabase.tracksAddedToPlaylistDao()
    override fun getTrackOverview(id: Int): Flow<TrackAddedToPlaylist?> = flow {
        val trackData = myDatabaseDao.getSelectedTrackData(id)
        if (trackData.isEmpty()) {
            emit(null)
        } else {
            emit(myTrackAddedToPlaylistsDbConvertor.map(trackData[0]))
        }
    }

    override fun getDataOfTracksFromListOfIds(listOfTrackIds: List<Int>): Flow<List<Track>> = flow {
        val myTracksData = myDatabaseDao.getSelectedTracksDataFromList(listOfTrackIds)
        if (myTracksData.isEmpty()) {
            emit(emptyList())
        } else {
            emit(myTracksData.map { stringtrack ->
                myTrackAddedToPlaylistsDbConvertor.map(stringtrack)
            })
        }
    }

    override fun getAllDataOfTracksFromListOfIds(listOfTrackIds: List<Int>): Flow<List<TrackAddedToPlaylist>> =
        flow {
            val myTracksData = myDatabaseDao.getAllSelectedTracksDataFromList(listOfTrackIds)

            emit(myTracksData.map { entityData ->
                myTrackAddedToPlaylistsDbConvertor.map(entityData)
            })

        }

    override suspend fun createTrackOverviewInStorage(trackData: TrackAddedToPlaylist) {
        val myTrackData = myTrackAddedToPlaylistsDbConvertor.map(trackData)
        myDatabaseDao.insertTrackAddedToPlaylistToStorage(myTrackData)
    }

    override suspend fun deleteTrackOverviewInStorage(trackId: Int) {
        myDatabaseDao.deleteTrackAddedToPlaylistFromStorage(trackId)
    }

    override suspend fun updateTrackOverviewInStorage(trackData: TrackAddedToPlaylist) {
        val newTrackData = myTrackAddedToPlaylistsDbConvertor.map(trackData)
        myDatabaseDao.updateTrackAddedToPlaylist(newTrackData)
    }
}