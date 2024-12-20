package com.practicum.playlistmaker.newPlaylist.domain.db

import android.graphics.Bitmap
import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface NewPlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getListOfPlaylistsNames(): Flow<List<String>>
    fun getPlaylist(name: String): Flow<Playlist?>
    suspend fun deletePlaylist(name:String)
    suspend fun addPlaylist(playlist:Playlist)
    suspend fun updatePlaylist(playlist:Playlist)
    fun saveImgToPrivateStorage(newUri:Uri,newName: String):Uri?
    fun getUriOfImgFromPrivateStorage(name:String):Uri?
}