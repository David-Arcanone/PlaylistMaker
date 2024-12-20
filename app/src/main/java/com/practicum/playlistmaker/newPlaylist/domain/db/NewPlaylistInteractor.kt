package com.practicum.playlistmaker.newPlaylist.domain.db

import android.graphics.Bitmap
import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface NewPlaylistInteractor {
    suspend fun saveNewPlaylist(newName:String, newDescription:String?,newPicture:Uri?, listOfTracks:List<Track>)
    suspend fun updatePlaylist(newName:String, newDescription:String?,newPicture:Uri?, listOfTracks:List<Track>)
    suspend fun getListOfPlaylistsName(): Flow<List<String>>//for verification no need for 2 playlists with the same name
    fun getAllSavedPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(name:String):Flow<Playlist?>
    fun savePictureToStorage(newUri: Uri, newName:String):Uri?
    fun getUriOfPictureFromStorage(name:String):Uri?

}