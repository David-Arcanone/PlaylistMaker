package com.practicum.playlistmaker.playlistOverview.ui

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryInteractor
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.playlistOverview.domain.db.PlaylistOverviewInteractor
import com.practicum.playlistmaker.playlistOverview.domain.models.PlaylistOverviewInitializationState
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities
import com.practicum.playlistmaker.utils.Utilities
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistOverviewViewModel(
    private val myNewPlaylistOverviewInteractor: PlaylistOverviewInteractor,
    private val myPlaylistInteractor: NewPlaylistInteractor
) : ViewModel() {
    private var timerUpdateJob: Job? = null

    //трек на экране
    private var playlistLiveData =
        MutableLiveData<PlaylistOverviewInitializationState>(PlaylistOverviewInitializationState.NotInitState)
    private lateinit var currentPlaylist: Playlist

    init {/*
        myPlayerInteractor.getSavedTrack(
            consumer = object : TracksConsumer<Track> {
                override fun consume(foundSavedTrack: Track) {
                    //подготовка плеера имеет смысл если у нас есть трек, иначе закроется
                    //в iTunes превью музыка есть только у треков.
                    if (foundSavedTrack.previewUrl != null) {
                        currentTrack = foundSavedTrack
                        startInit(foundSavedTrack)
                    } else {
                        playerInitLiveData.postValue(PlayerInitializationState.NoSaveFound)//добавлял в запросе фильтрацию по типу музыка, значит должна быть, иначе ошибка, а значит надо закрыть
                    }
                }
            },
            doIfNoMatch = fun() { playerInitLiveData.postValue(PlayerInitializationState.NoSaveFound) })*/
    }

    fun getPlaylistOverviewStateLiveData(): LiveData<PlaylistOverviewInitializationState> =
        playlistLiveData

    override fun onCleared() {
        super.onCleared()
    }

    private fun startInit(id: Int) {
        viewModelScope.launch {
            myPlaylistInteractor.getPlaylist(id).collect { playlist ->
                processInit(playlist)
            }
        }
    }

    private fun processInit(playlist: Playlist?) {
        if (playlist != null) {

        } else {

        }
    }

    fun openBottomSheetPropertiesButtonClick() {
        val prevInitState = playlistLiveData.value
        when (prevInitState) {
            is PlaylistOverviewInitializationState.DoneInitState -> {
                playlistLiveData.postValue(
                    PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet(prevInitState.currentPlaylist)
                )
            }

            is PlaylistOverviewInitializationState.DoneInitStateTracksSheet -> {
                playlistLiveData.postValue(
                    PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet(prevInitState.currentPlaylist)
                )
            }

            else -> {}
        }
    }

    fun openBottomSheetTracksButtonClick() {
        val prevInitState = playlistLiveData.value
        when (prevInitState) {
            is PlaylistOverviewInitializationState.DoneInitState -> {
                PlaylistOverviewInitializationState.DoneInitStateTracksSheet(
                    prevInitState.currentPlaylist,
                    listOfTracks = emptyList()//todo
                )
            }

            is PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet -> {
                PlaylistOverviewInitializationState.DoneInitStateTracksSheet(
                    prevInitState.currentPlaylist,
                    listOfTracks = emptyList()//todo
                )
            }

            else -> {}
        }
    }


    fun closeBottomSheetAddToPlaylistButtonClick() {
        val prevInitState = playlistLiveData.value
        when (prevInitState) {
            is PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet -> {
                playlistLiveData.postValue(
                    PlaylistOverviewInitializationState.DoneInitState(prevInitState.currentPlaylist)
                )
            }

            is PlaylistOverviewInitializationState.DoneInitStateTracksSheet -> {
                playlistLiveData.postValue(
                    PlaylistOverviewInitializationState.DoneInitState(prevInitState.currentPlaylist)
                )
            }

            else -> {}
        }
    }

    companion object {
    }
}