package com.practicum.playlistmaker.playlistOverview.data.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.playlistOverview.data.db.entity.TrackAddedToPlaylistEntity
import com.practicum.playlistmaker.playlistOverview.domain.models.TrackAddedToPlaylist
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAddedToPlaylistsDbConvertor(private val myGson: Gson) {
    fun map(trackData: TrackAddedToPlaylist): TrackAddedToPlaylistEntity {
        return TrackAddedToPlaylistEntity(
            trackId = trackData.trackId,
            trackName = trackData.track.trackName,
            track = myGson.toJson(trackData.track),
            listOfPlaylistsIds = myGson.toJson(trackData.listOfPlaylistsIds)
        )
    }
    fun map(trackData: TrackAddedToPlaylistEntity): TrackAddedToPlaylist {

        val listType = object : TypeToken<List<Int>>() {}.type
        return TrackAddedToPlaylist (
            trackId = trackData.trackId,
            track = map(trackData.track),
            listOfPlaylistsIds = myGson.fromJson(trackData.listOfPlaylistsIds,listType),
        )
    }
    fun map(trackString: String):Track{
        val trackType = object : TypeToken<Track>() {}.type
        return myGson.fromJson(trackString,trackType)
    }
}