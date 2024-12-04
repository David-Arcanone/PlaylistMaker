package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val myPlayerInteractor: PlayerInteractor,
    private val myFavoritesHistoryInteractor: FavoritesHistoryInteractor
) : ViewModel() {
    private var timerUpdateJob: Job? = null

    //трек на экране
    private var playerInitLiveData =
        MutableLiveData<PlayerInitializationState>(PlayerInitializationState.NotInitState)
    private var playerMediaLiveData = MutableLiveData<PlayerMediaState>(PlayerMediaState.Default)
    private var isLiked = false
    private lateinit var currentTrack: Track

    init {
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
            doIfNoMatch = fun() { playerInitLiveData.postValue(PlayerInitializationState.NoSaveFound) })
    }

    fun getInitStateLiveData(): LiveData<PlayerInitializationState> = playerInitLiveData
    fun getMediaStateLiveData(): LiveData<PlayerMediaState> = playerMediaLiveData
    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun playbackControl() {//нажали по центральной кнопке
        when (playerMediaLiveData.value) {
            is PlayerMediaState.Playing -> {//останавливаю плеер
                pausePlayer()
            }

            is PlayerMediaState.Prepared, is PlayerMediaState.Paused -> {//запускаю плеер
                startPlayer()
            }

            else -> {}
        }
    }

    fun pausePlayer() {
        if (playerMediaLiveData.value is PlayerMediaState.Playing) {//его могут вызвать с onPause Activity, надо убедиться что актуально
            myPlayerInteractor.pausePlayer()
            timerUpdateJob?.cancel()//снимаю корутину
            playerMediaLiveData.postValue(PlayerMediaState.Paused(getTimerValue(), isLiked))
        }
    }

    private fun startPlayer() {
        myPlayerInteractor.startPlayer()
        playerMediaLiveData.postValue(PlayerMediaState.Playing(getTimerValue(), isLiked))
        startTimer()
    }

    private fun releasePlayer() {
        myPlayerInteractor.finishPlayer()
        playerMediaLiveData.postValue(PlayerMediaState.Default)
        playerInitLiveData.postValue(PlayerInitializationState.NotInitState)
    }

    private fun startInit(foundSavedTrack: Track) {
        viewModelScope.launch {
            myFavoritesHistoryInteractor.getListOfLikedId().collect { foundLikedId ->
                isLiked = checkIfLiked(foundLikedId, foundSavedTrack.trackId)
                processInit(isLiked, foundSavedTrack)
            }
        }
    }

    private fun checkIfLiked(likedIds: List<Int>, currentId: Int): Boolean {
        var likedFlag = false
        likedIds.forEach { if (it == currentId) likedFlag = true }
        return likedFlag
    }

    private fun processInit(likedFlag: Boolean, foundSavedTrack: Track) {
        val myUrl = foundSavedTrack.previewUrl ?: ""
        if(myUrl.isNotEmpty()){
            myPlayerInteractor.preparePlayer(myUrl,
                onPlayerPreparedFunction = {
                    playerInitLiveData.postValue(
                        PlayerInitializationState.DoneInitState(foundSavedTrack)
                    )
                    playerMediaLiveData.postValue(PlayerMediaState.Prepared(ZERO_TIME, likedFlag))
                },
                onPlayerCompletedFunction = {
                    playerMediaLiveData.postValue(
                        PlayerMediaState.Prepared(
                            ZERO_TIME, likedFlag
                        )
                    )
                })
        }
    }

    private fun getTimerValue(): String {
        return AndroidUtilities.getTimeTransformedToString(myPlayerInteractor.getCurrentMediaPosition())
    }

    private fun startTimer() {
        timerUpdateJob = viewModelScope.launch {
            delay(REFRESH_TIMER_DELAY_MILLIS)
            while (playerMediaLiveData.value is PlayerMediaState.Playing) {
                playerMediaLiveData.postValue(
                    PlayerMediaState.Playing(
                        getTimerValue(), isLiked
                    )
                )
                delay(REFRESH_TIMER_DELAY_MILLIS)
            }
        }
    }

    fun likeClick() {
        if (playerInitLiveData.value is PlayerInitializationState.DoneInitState) {//проверяем, что есть данные трека
            viewModelScope.launch {
                if (!isLiked) {
                    myFavoritesHistoryInteractor.addLike(currentTrack)
                } else {
                    myFavoritesHistoryInteractor.deleteLike(currentTrack)
                }
            }
            isLiked = !isLiked
            processLike(isLiked)

        }
    }

    private fun processLike(isLikedFlag: Boolean) {
        when (playerMediaLiveData.value) {
            is PlayerMediaState.Playing -> {
                playerMediaLiveData.postValue(
                    PlayerMediaState.Playing(
                        getTimerValue(),
                        isLikedFlag
                    )
                )
            }

            is PlayerMediaState.Paused -> {
                playerMediaLiveData.postValue(PlayerMediaState.Paused(getTimerValue(), isLiked))
            }

            is PlayerMediaState.Prepared -> {
                playerMediaLiveData.postValue(
                    PlayerMediaState.Prepared(
                        ZERO_TIME, isLikedFlag
                    )
                )
            }

            is PlayerMediaState.Default -> {
                playerMediaLiveData.postValue(PlayerMediaState.Default)
            }

            else -> {}
        }
    }

    companion object {
        private const val REFRESH_TIMER_DELAY_MILLIS = 500L //2 раза в секунду обновление таймера
        private const val ZERO_TIME = "0:00"
    }
}