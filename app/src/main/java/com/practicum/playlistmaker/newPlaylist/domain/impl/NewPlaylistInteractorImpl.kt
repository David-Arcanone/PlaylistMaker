package com.practicum.playlistmaker.newPlaylist.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistRepository
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class NewPlaylistInteractorImpl(private val myRepository: NewPlaylistRepository) :
    NewPlaylistInteractor {
    override suspend fun getListOfPlaylistsIds(): Flow<List<Int>> {
        return myRepository.getListOfPlaylistsIds()
    }

    override suspend fun getPlaylist(id: Int): Flow<Playlist?> {
        return myRepository.getPlaylist(id)
    }

    override suspend fun getAllSavedPlaylists(): Flow<List<Playlist>> {
        return myRepository.getAllPlaylists()
    }

    override suspend fun saveNewPlaylist(
        newName: String,
        newDescription: String?,
        newPicture: Uri?,
    ) {
        myRepository.addPlaylist(
            newName=newName,
            newDescription=newDescription,
            newPic=newPicture,
            listOfTracks = emptyList()
        )
    }

    override fun savePictureToStorage(newUri: Uri, newName: String): Uri? {
        return myRepository.saveImgToPrivateStorage(newUri,newName)
    }

    override suspend fun updatePlaylist(
        newPlaylist:Playlist
    ) {
        myRepository.updatePlaylist(newPlaylist)
    }

}