package com.practicum.playlistmaker.newPlaylist.domain.db

import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface NewPlaylistInteractor {
    suspend fun saveNewPlaylist(newName:String, newDescription:String?,newPicture:Uri?)
    suspend fun updatePlaylist(newPlaylist:Playlist)
    suspend fun getListOfPlaylistsIds(): Flow<List<Int>>//for verification no need for 2 playlists with the same name
    suspend fun getAllSavedPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylist(id:Int):Flow<Playlist?>
    fun savePictureToStorage(newUri: Uri, newName:String):Uri?

}