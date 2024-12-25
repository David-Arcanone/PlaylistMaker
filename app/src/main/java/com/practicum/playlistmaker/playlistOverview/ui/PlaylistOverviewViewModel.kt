package com.practicum.playlistmaker.playlistOverview.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.playlistOverview.domain.db.PlaylistOverviewInteractor
import com.practicum.playlistmaker.playlistOverview.domain.models.PlaylistOverviewInitializationState
import com.practicum.playlistmaker.playlistOverview.domain.models.TrackAddedToPlaylist
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.utils.Utilities
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistOverviewViewModel(
    private val myNewPlaylistOverviewInteractor: PlaylistOverviewInteractor,
    private val myPlaylistInteractor: NewPlaylistInteractor,
    private val mySearchInteractor: SearchInteractor,
    private val mySharingInteractor: SharingInteractor,
    private val myPlaylistId: Int
) : ViewModel() {
    private var isClickOnTrackAllowed = true
    private var clickDebounceJob: Job? = null

    //трек на экране
    private var overviewLiveData =
        MutableLiveData<PlaylistOverviewInitializationState>(PlaylistOverviewInitializationState.NotInitState)

    init {
        checkValues()
    }

    fun checkValues() {
        viewModelScope.launch {
            myPlaylistInteractor.getPlaylist(myPlaylistId).collect { playlist ->
                processInit(playlist)
            }
        }
    }

    private suspend fun processInit(playlist: Playlist?) {
        if (playlist != null) {
            myNewPlaylistOverviewInteractor.getDataOfTracksFromListOfIds(playlist.listOfTracks)
                .collect { listOfTracks ->
                    val prevLiveData = overviewLiveData.value
                    when (prevLiveData) {
                        is PlaylistOverviewInitializationState.DoneInitStateTracksSheet -> {
                            overviewLiveData.postValue(
                                PlaylistOverviewInitializationState.DoneInitStateTracksSheet(
                                    playlist,
                                    listOfTracks
                                )
                            )
                        }

                        is PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet -> {
                            overviewLiveData.postValue(
                                PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet(
                                    playlist
                                )
                            )
                        }

                        else -> {//PlaylistOverviewInitializationState.NotInitState,PlaylistOverviewInitializationState.NoSaveFound,PlaylistOverviewInitializationState.DoneInitState
                            overviewLiveData.postValue(
                                PlaylistOverviewInitializationState.DoneInitStateTracksSheet(
                                    playlist,
                                    listOfTracks
                                )
                            )
                        }
                    }
                }
        } else {
            overviewLiveData.postValue(PlaylistOverviewInitializationState.NoSaveFound)
        }
    }


    fun openBottomSheetPropertiesButtonClick() {
        val prevInitState = overviewLiveData.value
        when (prevInitState) {
            is PlaylistOverviewInitializationState.DoneInitStateTracksSheet -> {
                overviewLiveData.postValue(
                    PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet(prevInitState.currentPlaylist)
                )
            }

            else -> {}
        }
    }

    fun openBottomSheetTracksClick() {
        viewModelScope.launch {
            val prevState = overviewLiveData.value
            when (prevState) {
                is PlaylistOverviewInitializationState.DoneInitStateTracksSheet -> {
                    myNewPlaylistOverviewInteractor.getDataOfTracksFromListOfIds(prevState.currentPlaylist.listOfTracks)
                        .collect { listOfTracks ->
                            overviewLiveData.postValue(
                                PlaylistOverviewInitializationState.DoneInitStateTracksSheet(
                                    prevState.currentPlaylist,
                                    listOfTracks
                                )
                            )
                        }
                }

                is PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet -> {
                    myNewPlaylistOverviewInteractor.getDataOfTracksFromListOfIds(prevState.currentPlaylist.listOfTracks)
                        .collect { listOfTracks ->
                            overviewLiveData.postValue(
                                PlaylistOverviewInitializationState.DoneInitStateTracksSheet(
                                    prevState.currentPlaylist,
                                    listOfTracks
                                )
                            )
                        }
                }

                else -> {
                    myPlaylistInteractor.getPlaylist(myPlaylistId).collect { playlist ->
                        processInit(playlist)
                    }
                }
            }
        }
    }

    fun trackWasClicked(track: Track, fragmentOpener: () -> Unit) {
        if (isClickOnTrackAllowed) {//клик разрешен аналогично с дебоунсом
            isClickOnTrackAllowed = false
            //по условиям запрет на активацию по первому нажатию на трек и отменять старый Job не нужно
            clickDebounceJob = viewModelScope.launch {
                delay(SearchViewModel.SEARCH_DEBOUNCE_DELAY)
                isClickOnTrackAllowed = true
            }
            mySearchInteractor.updateHistoryWithNewTrack(track)//чтобы в истории обновился список
            fragmentOpener()
        }
    }

    fun trackDelete(track: Track) {
        viewModelScope.launch {
            val prevState = overviewLiveData.value
            if (prevState is PlaylistOverviewInitializationState.DoneInitStateTracksSheet) {
                Log.d("MY_DELETE","1-RIGHT_STATE")
                myPlaylistInteractor.getPlaylist(myPlaylistId).collect { playlist ->
                    Log.d("MY_DELETE","2-GOT_PLAYLIST_FROM_STORAGE")
                    if (playlist != null) {
                        val newList = playlist.listOfTracks.filter { it!=track.trackId }
                        myPlaylistInteractor.updatePlaylist(
                            Playlist(
                                id = playlist.id,
                                playlistName = playlist.playlistName,
                                playlistDescription = playlist.playlistDescription,
                                imgSrc = playlist.imgSrc,
                                listOfTracks = newList,
                                totalSeconds = playlist.totalSeconds - Utilities.getSecondsFromText_mm_ss(
                                    track.trackLengthText ?: "00:00"
                                )
                            )
                        )
                        processDeleteForTrackData(playlist.id, track.trackId)
                    }
                }
            }
            checkValues()
        }
    }

    private suspend fun processDeleteForTrackData(playlistId: Int, trackId: Int) {
        myNewPlaylistOverviewInteractor.getTrackData(trackId).collect { trackData ->
            if (trackData != null) {
                val newList = trackData.listOfPlaylistsIds.filter { it != playlistId }
                if (newList.size > 0) {
                    myNewPlaylistOverviewInteractor.updateTrackAddedToPlaylistData(
                        TrackAddedToPlaylist(
                            trackId = trackData.trackId,
                            track = trackData.track,
                            listOfPlaylistsIds = newList
                        )
                    )
                }
                else{
                    myNewPlaylistOverviewInteractor.deleteTrackAddedToPlaylistData(trackData.trackId)
                }
            }
        }
    }

    fun shareClick(messageIfEmpty:()->Unit) {
        viewModelScope.launch {
            val previousState=overviewLiveData.value
            when(previousState){
                is PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet->{
                    myNewPlaylistOverviewInteractor.getDataOfTracksFromListOfIds(previousState.currentPlaylist.listOfTracks).collect{
                        if(it.size>0)
                        shareClickProcess(
                            name = previousState.currentPlaylist.playlistName,
                            description = previousState.currentPlaylist.playlistDescription ?:"",
                            listOfTracks = it)
                        else messageIfEmpty()
                    }
                }
                is PlaylistOverviewInitializationState.DoneInitStateTracksSheet->{//не надо искать треки
                    if(previousState.listOfTracks.size>0)
                    shareClickProcess(
                        name = previousState.currentPlaylist.playlistName,
                        description = previousState.currentPlaylist.playlistDescription ?:"",
                        listOfTracks = previousState.listOfTracks)
                    else messageIfEmpty()
                }
                else->{}
            }
        }
    }
    private fun shareClickProcess(name:String,description:String,listOfTracks:List<Track>){
        mySharingInteractor.openSharePlaylist(name,description,listOfTracks)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            myPlaylistInteractor.getPlaylist(myPlaylistId)
                .collect { playlist ->
                    if (playlist != null) {
                        myNewPlaylistOverviewInteractor.getFullDataOfTracksFromListOfIds(playlist.listOfTracks)
                            .collect { listOfTracksData ->//удаляю упоминания этого плейлиста
                                listOfTracksData.forEach {
                                    val newList =
                                        it.listOfPlaylistsIds.filter { it != myPlaylistId }
                                    if (newList.size > 0) {
                                        myNewPlaylistOverviewInteractor.updateTrackAddedToPlaylistData(
                                            TrackAddedToPlaylist(
                                                trackId = it.trackId,
                                                track = it.track,
                                                listOfPlaylistsIds = newList
                                            )
                                        )
                                    } else {//данные трека больше не нужно хранить, тк его нет
                                        myNewPlaylistOverviewInteractor.deleteTrackAddedToPlaylistData(
                                            it.trackId
                                        )
                                    }
                                }
                            }
                        myPlaylistInteractor.deletePlaylist(myPlaylistId)
                    }
                }
        }
    }

    fun getPlaylistOverviewStateLiveData(): LiveData<PlaylistOverviewInitializationState> =
        overviewLiveData

}