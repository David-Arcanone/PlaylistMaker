package com.practicum.playlistmaker.newPlaylist.domain.impl

import android.graphics.Bitmap
import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistRepository
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class NewPlaylistInteractorImpl(private val myRepository: NewPlaylistRepository) :
    NewPlaylistInteractor {
    override suspend fun getListOfPlaylistsName(): Flow<List<String>> {
        return myRepository.getListOfPlaylistsNames()
    }

    override suspend fun getPlaylist(name: String): Flow<Playlist?> {
        return myRepository.getPlaylist(name)
    }

    override fun getAllSavedPlaylists(): Flow<List<Playlist>> {
        return myRepository.getAllPlaylists()
    }

    override suspend fun saveNewPlaylist(
        newName: String,
        newDescription: String?,
        newPicture: Uri?,
        listOfTracks: List<Track>,
    ) {
        val newUri = if (newPicture != null) {
            myRepository.saveImgToPrivateStorage(newPicture, newName)
        } else {
            null
        }
        myRepository.addPlaylist(
            Playlist(
                newName,
                newDescription,
                newUri,
                emptyList()
            )
        )
    }

    override fun savePictureToStorage(newUri: Uri, newName: String): Uri? {
        return myRepository.saveImgToPrivateStorage(newUri,newName)
    }

    override fun getUriOfPictureFromStorage(name: String): Uri? {
        return myRepository.getUriOfImgFromPrivateStorage(name)
    }

    override suspend fun updatePlaylist(
        newName: String,
        newDescription: String?,
        newPicture: Uri?,
        listOfTracks: List<Track>
    ) {
        myRepository.updatePlaylist(Playlist(newName,newDescription,newPicture,listOfTracks))
    }

}