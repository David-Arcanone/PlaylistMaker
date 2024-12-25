package com.practicum.playlistmaker.player.ui

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.newPlaylist.ui.PlaylistSmallAdapter
import com.practicum.playlistmaker.player.domain.models.PlayerInitializationState
import com.practicum.playlistmaker.player.domain.models.PlayerMediaState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.AndroidUtilities
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    private lateinit var myBinding: FragmentPlayerBinding
    private val myViewModel by viewModel<PlayerViewModel>()
    private val myBottomSheetListOfPlaylists = mutableListOf<Playlist>()
    private val myBottomSheetPlaylistsTrackAdapter = PlaylistSmallAdapter()
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
        val myBottomSheetBehaviour =
            BottomSheetBehavior.from(myBinding.addToPlaylistBottomSheet)
        myBottomSheetBehaviour.peekHeight = Resources.getSystem().getDisplayMetrics().heightPixels*2/3
        myBottomSheetBehaviour.apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        myBottomSheetBehaviour.addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        myViewModel.closeBottomSheetAddToPlaylistButtonClick()
                    }
                    else -> {
                        // Остальные состояния не обрабатываем
                    }
                }
            }
        }
        )

        myViewModel.getInitStateLiveData().observe(viewLifecycleOwner) { initState ->
            when (initState) {
                is PlayerInitializationState.DoneInitState -> {
                    renderTrackInfo(initState.currentTrack)
                    myBinding.btLike.setImageResource(if (!initState.isLiked) R.drawable.bt_like_lm else R.drawable.bt_like_ok_lm)
                    myBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                    showBottomSheet(false)
                }

                is PlayerInitializationState.DoneInitStateBottomSheet -> {
                    renderTrackInfo(initState.currentTrack)
                    myBinding.btLike.setImageResource(if (!initState.isLiked) R.drawable.bt_like_lm else R.drawable.bt_like_ok_lm)
                    myBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                    renderBottomSheet(initState.listOfPlaylists)
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
        myBinding.btAddToPlaylist.setOnClickListener {
            myViewModel.openBottomSheetAddToPlaylistButtonClick()
        }
        myBinding.bottomSheetCurtain.setOnClickListener {
            myViewModel.closeBottomSheetAddToPlaylistButtonClick()
        }
        myBinding.btNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
        //recycler view
        myBottomSheetPlaylistsTrackAdapter.myPlaylists = myBottomSheetListOfPlaylists
        myBinding.rvPlaylists.adapter = myBottomSheetPlaylistsTrackAdapter
        myBottomSheetPlaylistsTrackAdapter.setOnClickListener {
            myViewModel.addToThisPlaylist(it,
                { it ->
                    Toast.makeText(
                        context,
                        getString(R.string.added_to_playlist) + it,
                        Toast.LENGTH_SHORT
                    ).show()
                }, onDupliucationCallback = {
                    Toast.makeText(
                        context,
                        getString(R.string.track_is_already_present_in_a_playlist) + it,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
    }

    override fun onPause() {
        myViewModel.pausePlayer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        myViewModel.checkForUpdates()
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

    private fun renderBottomSheet(listOfPlaylists: List<Playlist>) {
        myBottomSheetListOfPlaylists.clear()
        myBottomSheetListOfPlaylists.addAll(listOfPlaylists)
        myBottomSheetPlaylistsTrackAdapter.notifyDataSetChanged()
        showBottomSheet(true)
    }

    private fun showBottomSheet(isVisable: Boolean) {
        myBinding.bottomSheetCurtain.isVisible = isVisable
    }

    private fun renderTime(newTime: String) {
        myBinding.titleTime.setText(newTime)
    }

}