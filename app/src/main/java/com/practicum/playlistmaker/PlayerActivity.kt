package com.practicum.playlistmaker

import android.media.MediaPlayer
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
    private var mediaPlayer = MediaPlayer()
    private lateinit var titleTime: TextView
    private var mainThreadHandler: Handler? = null
    //private var url: String? = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mainThreadHandler = Handler(Looper.getMainLooper())
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
        //загрузка текущего трека
        val myPref =
            getSharedPreferences(SharedPreferenceManager.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE)
        val currentTrack = SharedPreferenceManager.getSavedTrack(myPref)
        //if(currentTrack!=null) preparePlayer(currentTrack.previewUrl)
        //записываем показания трека в окно
        if (currentTrack != null) {
            //постер
            Glide.with(this.applicationContext)
                .load(currentTrack.getCoverImg())
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .transform(RoundedCorners(Utilities.dpToPx(8f, this)))
                .into(ivPoster)
            //текст
            titleName.setText(currentTrack.trackName)
            titleAuthor.setText(currentTrack.artistName)
            trackDuration.setText(currentTrack.showTrackTime())
            trackCountry.setText(currentTrack.country)
            trackGenre.setText(currentTrack.primaryGenreName)
            trackYear.setText(currentTrack.releaseDate.take(4))//год это первые 4 цифры
            //подготовка плеера имеет смысл если у нас есть трек иначе активити закроется
            //в iTunes превью музыка есть только у треков.
            if (currentTrack.previewUrl != null) preparePlayer(currentTrack.previewUrl) else titleTime.setText(
                R.string.not_track_error
            )

            //по условию альбома может и не быть у песни
            if (currentTrack.collectionName?.isNotEmpty() == true) {
                trackAlbumGroup.isVisible = true
                trackAlbum.setText(currentTrack.collectionName)
            } else {
                trackAlbumGroup.isVisible = false
            }
        } else {
            //на случай ошибки
            finish()
        }
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
        super.onDestroy()
        mediaPlayer.release()
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
        mediaPlayer.setDataSource(urlSong)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            btPlay.isEnabled = true
            playerState = STATE_PREPARED
            titleTime.setText("00:00")
        }
        mediaPlayer.setOnCompletionListener {
            btPlay.setImageResource(R.drawable.bt_play_lm)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btPlay.setImageResource(R.drawable.bt_pause_lm)
        playerState = STATE_PLAYING
        updateTimeTitle()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btPlay.setImageResource(R.drawable.bt_play_lm)
        playerState = STATE_PAUSED
    }

    private fun updateTimeTitle() {
        titleTime.setText(
            SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayer.currentPosition)
        )
    }
}