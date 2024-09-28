package com.practicum.playlistmaker.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities

class PlayerActivity : AppCompatActivity() {
    private lateinit var myBinding:ActivityPlayerBinding
    private val myViewModel by viewModels<PlayerViewModel> { PlayerViewModel.getPlayerViewModel() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding= ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(myBinding.root)

        myViewModel.getInitStateLiveData().observe(this){initState->
            when(initState){
                is PlayerInitializationState.DoneInitState ->{
                    renderTrackInfo(initState.currentTrack)
                }
                is PlayerInitializationState.NoSaveFound ->{
                    finish()
                }
                is PlayerInitializationState.NotInitState ->{//состояние по умолчанию делать ничего не надо ждем инициализацию в ViewModel, который переведет его в DoneInitState
                    Unit
                }
            }
        }
        myViewModel.getMediaStateLiveData().observe(this){mediaState->
            when(mediaState){
                is PlayerMediaState.Prepared->{
                    myBinding.btPlay.isEnabled = true//чтоб при первом prepared разлочил кнопку
                    renderTime(mediaState.timeValue)
                    myBinding.btPlay.setImageResource(R.drawable.bt_play_lm)
                }
                is PlayerMediaState.Paused->{
                    renderTime(mediaState.timeValue)
                    myBinding.btPlay.setImageResource(R.drawable.bt_play_lm)
                }
                is PlayerMediaState.Playing->{
                    renderTime(mediaState.timeValue)
                    myBinding.btPlay.setImageResource(R.drawable.bt_pause_lm)
                }
                else->{//статус Default, ждем инициализации трека
                    Unit
                }
            }
        }

        //активность кнопок
        myBinding.btBack.setOnClickListener {
            finish()
        }

        myBinding.btPlay.setOnClickListener {
            myViewModel.playbackControl()
        }
    }

    override fun onPause() {
        myViewModel.pausePlayer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun renderTrackInfo(foundTrack:Track){
        //постер
        Glide.with(applicationContext)
            .load(foundTrack.coverImg)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(AndroidUtilities.dpToPx(8f, this)))
            .into(myBinding.imageViewAlbum)
        //текст
        myBinding.titleName.setText(foundTrack.trackName)
        myBinding.titleAuthor.setText(foundTrack.artistName)
        myBinding.trackDuration.setText(foundTrack.trackLengthText)
        myBinding.trackCountry.setText(foundTrack.country)
        myBinding.trackGenre.setText(foundTrack.primaryGenreName)
        myBinding.trackYear.setText(foundTrack.releaseYear)
        //по условию альбома может и не быть у песни
        if (foundTrack.collectionName?.isNotEmpty() == true) {
            myBinding.albumGroup.isVisible = true
            myBinding.trackAlbum.setText(foundTrack.collectionName)
        } else {
            myBinding.albumGroup.isVisible = false
        }
    }
    private fun renderTime(newTime:String){myBinding.titleTime.setText(newTime)}
}