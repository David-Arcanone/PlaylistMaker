package com.practicum.playlistmaker.newPlaylist.data.converters

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistsDbConvertor(private val myGson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistPicture = playlist.imgSrc.toString(),
            listOfTracks = myGson.toJson(playlist.listOfTracks),
        )
    }
    fun map(playlist: PlaylistEntity): Playlist {
        val listType = object : TypeToken<List<Track>>() {}.type
        val picUri:Uri?=if(playlist.playlistPicture!="")Uri.parse(playlist.playlistPicture) else null
        return Playlist(
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription ?:"",
            imgSrc=picUri,
            listOfTracks = myGson.fromJson(playlist.listOfTracks,listType),
        )
    }
}