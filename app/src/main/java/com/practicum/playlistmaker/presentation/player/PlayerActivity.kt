package com.practicum.playlistmaker.presentation.player

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.utils.Utilities
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIMER_DELAY_MILLIS = 500L //2 раза в секунду обновление таймера
    }

    private var playerState = STATE_DEFAULT
    private lateinit var btPlay: ImageButton
    private lateinit var titleTime: TextView
    private var mainThreadHandler: Handler? = null
    private lateinit var myPlayerInteractor: PlayerInteractor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mainThreadHandler = Handler(Looper.getMainLooper())
        myPlayerInteractor = Creator.provideGetPlayerInteractor(
            this.getSharedPreferences(
                Utilities.PLAYLIST_SAVED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        )
        //кнопки и текст
        val btBack = findViewById<Button>(R.id.bt_back)
        val ivPoster = findViewById<ImageView>(R.id.imageViewAlbum)
        btPlay = findViewById<ImageButton>(R.id.bt_play)
        val btAddToPlaylist = findViewById<ImageButton>(R.id.bt_add_to_playlist)
        val btLike = findViewById<ImageButton>(R.id.bt_like)
        val titleName = findViewById<TextView>(R.id.title_name)
        val titleAuthor = findViewById<TextView>(R.id.title_author)
        titleTime = findViewById<TextView>(R.id.title_time)
        val trackGenre = findViewById<TextView>(R.id.track_genre)
        val trackAlbum = findViewById<TextView>(R.id.track_album)
        val trackAlbumGroup = findViewById<Group>(R.id.album_group)
        val trackDuration = findViewById<TextView>(R.id.track_duration)
        val trackCountry = findViewById<TextView>(R.id.track_country)
        val trackYear = findViewById<TextView>(R.id.track_year)
        val activityContext = this
        //загрузка текущего трека через интерактор

        myPlayerInteractor.getSavedTrack(consumer = object : TracksConsumer<Track> {
            override fun consume(foundTrack: Track) {//запустится только если есть такой трек, записываем показания трека в окно
                //постер
                Glide.with(applicationContext)
                    .load(foundTrack.coverImg)
                    .placeholder(R.drawable.placeholder)
                    .fitCenter()
                    .transform(RoundedCorners(Utilities.dpToPx(8f, activityContext)))
                    .into(ivPoster)
                //текст
                titleName.setText(foundTrack.trackName)
                titleAuthor.setText(foundTrack.artistName)
                trackDuration.setText(foundTrack.trackLengthText)
                trackCountry.setText(foundTrack.country)
                trackGenre.setText(foundTrack.primaryGenreName)
                trackYear.setText(foundTrack.releaseYear)
                //подготовка плеера имеет смысл если у нас есть трек иначе активити закроется
                //в iTunes превью музыка есть только у треков.
                if (foundTrack.previewUrl != null) preparePlayer(foundTrack.previewUrl) else titleTime.setText(
                    R.string.not_track_error
                )
                //по условию альбома может и не быть у песни
                if (foundTrack.collectionName?.isNotEmpty() == true) {
                    trackAlbumGroup.isVisible = true
                    trackAlbum.setText(foundTrack.collectionName)
                } else {
                    trackAlbumGroup.isVisible = false
                }
            }
        },//на случай ошибки
            doIfNoMatch = fun() { finish() })
        //активность кнопок
        btBack.setOnClickListener {
            finish()
        }

        btPlay.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mainThreadHandler?.removeCallbacksAndMessages(null)
        myPlayerInteractor.finishPlayer()
        super.onDestroy()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                mainThreadHandler?.removeCallbacksAndMessages(null) //выключаю
                updateTimeTitle()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                mainThreadHandler?.postDelayed(
                    object : Runnable {
                        override fun run() {
                            updateTimeTitle()//обновляю таймер
                            // И снова планируем то же действие через 0,5 секунд
                            mainThreadHandler?.postDelayed(
                                this,
                                REFRESH_TIMER_DELAY_MILLIS,
                            )
                        }
                    },
                    REFRESH_TIMER_DELAY_MILLIS
                )
            }
        }
    }

    private fun preparePlayer(urlSong: String) {
        myPlayerInteractor.preparePlayer(urlSong,
            {
                btPlay.isEnabled = true
                playerState = STATE_PREPARED
                titleTime.setText("00:00")
            },
            {
                btPlay.setImageResource(R.drawable.bt_play_lm)
                playerState = STATE_PREPARED
                mainThreadHandler?.removeCallbacksAndMessages(null)
                titleTime.setText("00:00")
            })
    }

    private fun startPlayer() {
        myPlayerInteractor.startPlayer()
        btPlay.setImageResource(R.drawable.bt_pause_lm)
        playerState = STATE_PLAYING
        updateTimeTitle()
    }

    private fun pausePlayer() {
        myPlayerInteractor.pausePlayer()
        btPlay.setImageResource(R.drawable.bt_play_lm)
        playerState = STATE_PAUSED
    }

    private fun updateTimeTitle() {
        titleTime.setText(
            SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(myPlayerInteractor.getCurrentMediaPosition())
        )
    }
}