package com.practicum.playlistmaker.medialibrary.data.converters

import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track

class MusicFavoriteDbConvertor {
    fun map(track: Track): MusicEntity {
        return MusicEntity(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artWorkUrl100 = track.artWorkUrl100,
            trackId = track.trackId,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            collectionName = track.collectionName,
            previewUrl = track.previewUrl,
            coverImg = track.coverImg,
            releaseYear = track.releaseYear,
            trackLengthText = track.trackLengthText,
            whenAdded = System.currentTimeMillis()
        )
    }
    fun map(track: MusicEntity): Track {
        return Track(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artWorkUrl100 = track.artWorkUrl100,
            trackId = track.trackId,
            country = track.country,
            primaryGenreName = track.primaryGenreName,
            collectionName = track.collectionName,
            previewUrl = track.previewUrl,
            coverImg = track.coverImg,
            releaseYear = track.releaseYear,
            trackLengthText = track.trackLengthText
        )
    }
}