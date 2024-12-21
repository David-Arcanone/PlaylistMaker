package com.practicum.playlistmaker.newPlaylist.data.converters

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist

class PlaylistsDbConvertor(private val myGson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id=playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistPicture = playlist.imgSrc.toString(),
            listOfTracks = map(playlist.listOfTracks),
        )
    }
    fun map(playlist: PlaylistEntity): Playlist {
        val picUri:Uri?=if(playlist.playlistPicture!="")Uri.parse(playlist.playlistPicture) else null
        val listType = object : TypeToken<List<Int>>() {}.type
        return Playlist(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription ?:"",
            imgSrc=picUri,
            listOfTracks = myGson.fromJson(playlist.listOfTracks,listType),
        )
    }
    fun map(listOfTracks:List<Int>):String{
        return myGson.toJson(listOfTracks)
    }
}