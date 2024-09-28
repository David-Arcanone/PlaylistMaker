package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.data.dto.ITunesSearchRequest
import com.practicum.playlistmaker.search.data.dto.ITunesSearchResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.SearchedTracks
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Utilities


class SearchRepositoryImpl(private val networkClient: NetworkClient, private val context: Context) :
    SearchRepository {
    companion object {
        private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
        private const val SEARCH_HISTORY_KEY = "key_for_search_history"
    }

    private val sharedPref = context.getSharedPreferences(
        Utilities.PLAYLIST_SAVED_PREFERENCES,
        Context.MODE_PRIVATE
    )

    override fun searchTracks(expression: String): SearchedTracks {
        try {
            val response = networkClient.doRequest(ITunesSearchRequest(expression))

            when (response.resultCode) {
                in 200..299 -> {//успешный запрос
                    val foundTracks = (response as ITunesSearchResponse).results
                    if (foundTracks.isNotEmpty()) {//есть совпадения
                        return SearchedTracks(foundTracks.map {
                            Track(
                                trackName = it.trackName,
                                artistName = it.artistName,
                                trackTime = it.trackTime,
                                artWorkUrl100 = it.artWorkUrl100,
                                trackId = it.trackId,
                                country = it.country,
                                primaryGenreName = it.primaryGenreName,
                                collectionName = it.collectionName,
                                previewUrl = it.previewUrl,
                                coverImg = it.getCoverImg(),
                                releaseYear = it.getReleaseYear(),
                                trackLengthText = it.showTrackTime()
                            )
                        }, isSucceded = true)
                    } else {//нет совпадений
                        return SearchedTracks(emptyList(), isSucceded = true)
                    }
                }

                else -> {//ошибка
                    //Log.d("error-type",response.resultCode.toString())
                    return SearchedTracks(emptyList(), isSucceded = false)
                }
            }
        } catch (e: Exception) {//неуспешный запрос
            //Log.d("error-type","exception")
            return SearchedTracks(emptyList(), isSucceded = false)
        }
    }

    override fun saveTrackHistory(newValue: List<Track>) {
        val json = Gson().toJson(newValue)
        sharedPref.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun getSavedTrackHistory(): List<Track> {
        val trackType = object : TypeToken<List<Track>>() {}
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, trackType)
    }

    override fun getSavedTrack(): Track? {
        val trackType = object : TypeToken<Track>() {}
        val json = sharedPref.getString(PLAYLIST_CURRENT_TRACK, null) ?: return null
        return Gson().fromJson(json, trackType)
    }

    override fun saveCurrentTrack(newValue: Track) {
        val json = Gson().toJson(newValue)
        sharedPref.edit()
            .putString(PLAYLIST_CURRENT_TRACK, json)
            .apply()
    }

    override fun startPlayerActivity() {
        val playerIntent = Intent(
            context,
            PlayerActivity::class.java
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)//без этого выскакивала ошибка
        context.startActivity(playerIntent)
    }
}