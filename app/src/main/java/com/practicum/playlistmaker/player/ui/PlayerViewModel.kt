package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val myPlayerInteractor: PlayerInteractor) : ViewModel() {
    private var timerUpdateJob: Job? = null
    private var myMediaPlayer: MediaPlayer = MediaPlayer()

    //трек на экране
    private var playerInitLiveData =
        MutableLiveData<PlayerInitializationState>(PlayerInitializationState.NotInitState)
    private var playerMediaLiveData = MutableLiveData<PlayerMediaState>(PlayerMediaState.Default)

    init {
        myPlayerInteractor.getSavedTrack(
            consumer = object : TracksConsumer<Track> {
                override fun consume(foundSavedTrack: Track) {
                    //подготовка плеера имеет смысл если у нас есть трек, иначе закроется
                    //в iTunes превью музыка есть только у треков.
                    if (foundSavedTrack.previewUrl != null) {
                        initPlayer(foundSavedTrack)
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
            myMediaPlayer.pause()
            timerUpdateJob?.cancel()//снимаю корутину
            playerMediaLiveData.postValue(PlayerMediaState.Paused(getTimerValue()))
        }
    }

    private fun startPlayer() {
        myMediaPlayer.start()
        playerMediaLiveData.postValue(PlayerMediaState.Playing(getTimerValue()))
        startTimer()
    }

    private fun releasePlayer() {
        myMediaPlayer.stop()
        myMediaPlayer.release()
        playerMediaLiveData.postValue(PlayerMediaState.Default)
        playerInitLiveData.postValue(PlayerInitializationState.NotInitState)
    }

    private fun initPlayer(foundSavedTrack: Track) {
        myMediaPlayer.setDataSource(foundSavedTrack.previewUrl)
        myMediaPlayer.prepareAsync()
        myMediaPlayer.setOnPreparedListener {
            playerInitLiveData.postValue(
                PlayerInitializationState.DoneInitState(foundSavedTrack)
            )
            playerMediaLiveData.postValue(PlayerMediaState.Prepared(ZERO_TIME))
        }
        myMediaPlayer.setOnCompletionListener {
            playerMediaLiveData.postValue(PlayerMediaState.Prepared(ZERO_TIME))
        }
    }

    private fun getTimerValue(): String {
        return AndroidUtilities.getTimeTransformedToString(myMediaPlayer.currentPosition)
    }

    private fun startTimer() {
        timerUpdateJob = viewModelScope.launch {
            while (myMediaPlayer.isPlaying) {
                delay(REFRESH_TIMER_DELAY_MILLIS)
                if(myMediaPlayer.isPlaying)playerMediaLiveData.postValue(PlayerMediaState.Playing(getTimerValue()))
            }
        }
    }

    companion object {
        private const val REFRESH_TIMER_DELAY_MILLIS = 500L //2 раза в секунду обновление таймера
        private const val ZERO_TIME = "0:00"
    }
}