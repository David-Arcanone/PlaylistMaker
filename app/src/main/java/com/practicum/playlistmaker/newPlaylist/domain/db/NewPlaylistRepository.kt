package com.practicum.playlistmaker.newPlaylist.domain.db

import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface NewPlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getListOfPlaylistsIds(): Flow<List<Int>>
    fun getPlaylist(chousenId: Int): Flow<Playlist?>
    suspend fun deletePlaylist(chousenId:Int)
    suspend fun addPlaylist(newName:String, newDescription:String?, newPic:Uri?, listOfTracks:List<Int>)
    suspend fun updatePlaylist(playlist:Playlist)
    fun saveImgToPrivateStorage(newUri:Uri,newName: String):Uri
    fun getUriOfImgFromPrivateStorage(name:String):Uri?
}