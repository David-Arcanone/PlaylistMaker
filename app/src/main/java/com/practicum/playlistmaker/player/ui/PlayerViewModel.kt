package com.practicum.playlistmaker.player.ui

import android.os.Looper
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities

class PlayerViewModel(private val myPlayerInteractor: PlayerInteractor) : ViewModel() {
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    //трек на экране
    private var playerInitLiveData =
        MutableLiveData<PlayerInitializationState>(PlayerInitializationState.NotInitState)
    private var playerMediaLiveData = MutableLiveData<PlayerMediaState>(PlayerMediaState.Default)

    private val myPlayingTimeUpdateRunnable = object : Runnable {
        override fun run() {
            if (playerMediaLiveData.value is PlayerMediaState.Playing) {//обновляю таймер только в играющем плеере
                val currentTime =
                    AndroidUtilities.getTimeTransformedToString(myPlayerInteractor.getCurrentMediaPosition())
                playerMediaLiveData.postValue(PlayerMediaState.Playing(currentTime))
                mainThreadHandler.postDelayed(
                    this,
                    REFRESH_TIMER_DELAY_MILLIS,
                )
            }
        }
    }

    init {
        myPlayerInteractor.getSavedTrack(
            consumer = object : TracksConsumer<Track> {
                override fun consume(foundSavedTrack: Track) {//запустится только если есть такой трек, записываем показания трека в окно
                    playerInitLiveData.postValue(
                        PlayerInitializationState.DoneInitState(
                            foundSavedTrack
                        )
                    )

                    //подготовка плеера имеет смысл если у нас есть трек иначе активити закроется
                    //в iTunes превью музыка есть только у треков.
                    if (foundSavedTrack.previewUrl != null) preparePlayer(foundSavedTrack.previewUrl) else {
                        playerInitLiveData.postValue(PlayerInitializationState.NoSaveFound)//добавлял в запросе фильтрацию по типу музыка, значит должна быть, иначе ошибка, а значит надо закрыть
                    }
                }
            },
            doIfNoMatch = fun() { playerInitLiveData.postValue(PlayerInitializationState.NoSaveFound) })
    }

    private fun preparePlayer(urlSong: String) {//получены данные трека, готовим медиа
        myPlayerInteractor.preparePlayer(urlSong,
            {
                playerMediaLiveData.postValue(PlayerMediaState.Prepared(ZERO_TIME))
            },
            {
                playerMediaLiveData.postValue(PlayerMediaState.Prepared(ZERO_TIME))
                mainThreadHandler.removeCallbacksAndMessages(myPlayingTimeUpdateRunnable)
            })
    }

    fun getInitStateLiveData(): LiveData<PlayerInitializationState> = playerInitLiveData
    fun getMediaStateLiveData(): LiveData<PlayerMediaState> = playerMediaLiveData
    override fun onCleared() {
        super.onCleared()
        myPlayerInteractor.finishPlayer()
        mainThreadHandler.removeCallbacks(myPlayingTimeUpdateRunnable)
    }

    fun playbackControl() {//нажали по центральной кнопке
        when (playerMediaLiveData.value) {
            is PlayerMediaState.Playing -> {//останавливаю плеер
                pausePlayer()
            }

            is PlayerMediaState.Prepared, is PlayerMediaState.Paused -> {//запускаю плеер
                startPlayer()
            }

            else -> {//Default состояние, можно вывод ошибки организовать, гипотетически невозможно нажать, из за недоступности кнопки
            }
        }
    }

    fun pausePlayer() {
        if (playerMediaLiveData.value is PlayerMediaState.Playing) {//его могут вызвать с onPause Activity, надо убедиться что актуально
            myPlayerInteractor.pausePlayer()
            val currentTime =
                AndroidUtilities.getTimeTransformedToString(myPlayerInteractor.getCurrentMediaPosition())
            playerMediaLiveData.postValue(PlayerMediaState.Paused(currentTime))
            mainThreadHandler.removeCallbacks(myPlayingTimeUpdateRunnable)
        }
    }

    private fun startPlayer() {
        mainThreadHandler.postDelayed(
            myPlayingTimeUpdateRunnable,
            REFRESH_TIMER_DELAY_MILLIS
        )
        myPlayerInteractor.startPlayer()
        val currentTime =
            AndroidUtilities.getTimeTransformedToString(myPlayerInteractor.getCurrentMediaPosition())
        playerMediaLiveData.postValue(PlayerMediaState.Playing(currentTime))
    }

    companion object {
        private const val REFRESH_TIMER_DELAY_MILLIS = 500L //2 раза в секунду обновление таймера
        private const val ZERO_TIME = "0:00"

    }
}