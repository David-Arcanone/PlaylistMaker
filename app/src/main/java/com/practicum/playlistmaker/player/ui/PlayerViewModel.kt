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
            playerMediaLiveData.postValue(PlayerMediaState.Paused(getTimerValue()))
        }
    }

    private fun startPlayer() {
        myPlayerInteractor.startPlayer()
        playerMediaLiveData.postValue(PlayerMediaState.Playing(getTimerValue()))
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
                processMediaInit(foundLikedId, foundSavedTrack)
            }
        }
    }

    private fun checkIfLiked(likedIds: List<Int>, currentId: Int): Boolean {
        var likedFlag = false
        likedIds.forEach { if (it == currentId) likedFlag = true }
        return likedFlag
    }

    private fun processMediaInit(foundLikedId:List<Int>,foundSavedTrack: Track) {
        val myUrl = foundSavedTrack.previewUrl ?: ""
        if (myUrl.isNotEmpty()) {
            myPlayerInteractor.preparePlayer(myUrl,
                onPlayerPreparedFunction = {
                    if(checkIfLiked(foundLikedId,foundSavedTrack.trackId)){
                        playerInitLiveData.postValue(
                            PlayerInitializationState.DoneInitStateLiked(foundSavedTrack)
                        )
                    }else{
                        playerInitLiveData.postValue(
                            PlayerInitializationState.DoneInitState(foundSavedTrack)
                        )
                    }
                    playerMediaLiveData.postValue(PlayerMediaState.Prepared(ZERO_TIME))
                },
                onPlayerCompletedFunction = {
                    playerMediaLiveData.postValue(
                        PlayerMediaState.Prepared(
                            ZERO_TIME
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
                        getTimerValue()
                    )
                )
                delay(REFRESH_TIMER_DELAY_MILLIS)
            }
        }
    }

    fun likeClick() {
        if (playerInitLiveData.value is PlayerInitializationState.DoneInitState || playerInitLiveData.value is PlayerInitializationState.DoneInitStateLiked) {//проверяем, что есть данные трека
            viewModelScope.launch {
                if (playerInitLiveData.value is PlayerInitializationState.DoneInitStateLiked) {
                    myFavoritesHistoryInteractor.deleteLike(currentTrack)

                } else {
                    myFavoritesHistoryInteractor.addLike(currentTrack)
                }
            }
            processLike()
        }
    }

    private fun processLike() {
        val previousValue=playerInitLiveData.value
        if (previousValue is PlayerInitializationState.DoneInitStateLiked) {
            playerInitLiveData.postValue(PlayerInitializationState.DoneInitState(previousValue.currentTrack))
        } else  {
            playerInitLiveData.postValue(PlayerInitializationState.DoneInitStateLiked((previousValue as PlayerInitializationState.DoneInitState).currentTrack))
        }
    }

    companion object {
        private const val REFRESH_TIMER_DELAY_MILLIS = 500L //2 раза в секунду обновление таймера
        private const val ZERO_TIME = "0:00"
    }
}