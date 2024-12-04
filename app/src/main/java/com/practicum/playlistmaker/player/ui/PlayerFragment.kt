package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private lateinit var myBinding: FragmentPlayerBinding
    private val myViewModel by viewModel<PlayerViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentPlayerBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.getInitStateLiveData().observe(viewLifecycleOwner) { initState ->
            when (initState) {
                is PlayerInitializationState.DoneInitState -> {
                    renderTrackInfo(initState.currentTrack)
                }

                is PlayerInitializationState.NoSaveFound -> {
                    findNavController().navigateUp()
                }

                is PlayerInitializationState.NotInitState -> {//состояние по умолчанию делать ничего не надо ждем инициализацию в ViewModel, который переведет его в DoneInitState
                    Unit
                }
            }
        }
        myViewModel.getMediaStateLiveData().observe(viewLifecycleOwner) { mediaState ->
            when (mediaState) {
                is PlayerMediaState.Prepared -> {
                    myBinding.btPlay.isEnabled = true//чтоб при первом prepared разлочил кнопку
                    myBinding.btLike.isEnabled = true
                    myBinding.btAddToPlaylist.isEnabled = true
                    renderTime(mediaState.timeValue)
                    myBinding.btPlay.setImageResource(R.drawable.bt_play_lm)

                }

                is PlayerMediaState.Paused -> {
                    renderTime(mediaState.timeValue)
                    myBinding.btPlay.setImageResource(R.drawable.bt_play_lm)
                }

                is PlayerMediaState.Playing -> {
                    renderTime(mediaState.timeValue)
                    myBinding.btPlay.setImageResource(R.drawable.bt_pause_lm)
                }

                else -> {//статус Default, ждем инициализации трека
                    Unit
                }
            }
            if (mediaState.isLikedFlag) {
                myBinding.btLike.setImageResource(R.drawable.bt_like_ok_lm)
            } else {
                myBinding.btLike.setImageResource(R.drawable.bt_like_lm)
            }
        }
        //активность кнопок

        myBinding.btPlay.setOnClickListener {
            myViewModel.playbackControl()
        }
        myBinding.btBack.setOnClickListener {
            findNavController().navigateUp()
        }
        myBinding.btLike.setOnClickListener {
            myViewModel.likeClick()
        }
    }

    override fun onPause() {
        myViewModel.pausePlayer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun renderTrackInfo(foundTrack: Track) {
        //постер
        Glide.with(this.requireContext())
            .load(foundTrack.coverImg)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .transform(RoundedCorners(AndroidUtilities.dpToPx(8f, this.requireContext())))
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

    private fun renderTime(newTime: String) {
        myBinding.titleTime.setText(newTime)
    }

}
