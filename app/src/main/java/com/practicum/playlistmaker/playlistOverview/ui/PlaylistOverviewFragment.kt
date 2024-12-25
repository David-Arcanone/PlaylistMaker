package com.practicum.playlistmaker.playlistOverview.ui

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistOverviewBinding
import com.practicum.playlistmaker.editPlaylist.ui.EditPlaylistFragment
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.playlistOverview.domain.models.PlaylistOverviewInitializationState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.utils.AndroidUtilities
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistOverviewFragment : Fragment() {
    private lateinit var myBinding: FragmentPlaylistOverviewBinding
    private val myViewModel by viewModel<PlaylistOverviewViewModel> {
        parametersOf(requireArguments().getInt(ARGS_PLAYLIST_ID))
    }
    private val myBottomSheetListOfTracks = mutableListOf<Track>()
    private val myBottomSheetTrackAdapter = TrackAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentPlaylistOverviewBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myBottomSheetTrackBehavior = BottomSheetBehavior.from(myBinding.playlistBottomSheet)
        val myBottomSheetSettingBehavior = BottomSheetBehavior.from(myBinding.settingsBottomSheet)
        var currentId=-1
        myBottomSheetTrackBehavior.apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        myBottomSheetSettingBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        myBottomSheetTrackBehavior.peekHeight =
            Resources.getSystem().getDisplayMetrics().heightPixels * 1 / 3
        myBottomSheetSettingBehavior.peekHeight =
            Resources.getSystem().getDisplayMetrics().heightPixels * 47 / 100
        myBottomSheetSettingBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        myViewModel.openBottomSheetTracksClick()
                    }
                    else -> {
                        // Остальные состояния не обрабатываем
                    }
                }
            }
        }
        )
        myViewModel.getPlaylistOverviewStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistOverviewInitializationState.NotInitState -> {
                    //еще не подгрузились данные
                }

                is PlaylistOverviewInitializationState.NoSaveFound -> {
                    findNavController().navigateUp()
                }

                is PlaylistOverviewInitializationState.DoneInitStateTracksSheet -> {
                    currentId=state.currentPlaylist.id
                    renderPlaylistInfo(state.currentPlaylist, false)
                    myBottomSheetTrackBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    myBottomSheetSettingBehavior.state=BottomSheetBehavior.STATE_HIDDEN
                    myBottomSheetListOfTracks.clear()
                    myBottomSheetListOfTracks.addAll(state.listOfTracks)
                    myBottomSheetTrackAdapter.notifyDataSetChanged()
                    myBinding.bottomSheetCurtainForBottomsheet.isVisible=false
                    myBinding.playlistBottomSheet.isVisible=true
                    myBinding.emptyMessage.isVisible=if(state.listOfTracks.size==0)true else false
                }

                is PlaylistOverviewInitializationState.DoneInitStatePropertiesSheet -> {
                    currentId=state.currentPlaylist.id
                    renderPlaylistInfo(state.currentPlaylist, true)
                    myBottomSheetTrackBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    myBottomSheetSettingBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
                    myBinding.bottomSheetCurtainForBottomsheet.isVisible=true
                    myBinding.playlistBottomSheet.isVisible=false
                }
            }
        }

        //активность кнопок
        myBinding.bottomSheetCurtainForBottomsheet.setOnClickListener { myViewModel.openBottomSheetTracksClick() }
        myBinding.btBack.setOnClickListener { findNavController().navigateUp() }
        myBinding.btTitleOpenSettings.setOnClickListener { myViewModel.openBottomSheetPropertiesButtonClick() }
        myBinding.btTitleShare.setOnClickListener { myViewModel.shareClick{showNoList()} }
        myBinding.btShare.setOnClickListener { myViewModel.shareClick{showNoList()} }
        myBinding.btEditInfo.setOnClickListener {
            findNavController().navigate(R.id.action_playlistOverviewFragment_to_editPlaylistFragment,EditPlaylistFragment.createArgs(currentId))
        }
        myBinding.btDelete.setOnClickListener {
            myBinding.bottomSheetCurtainForMessage.isVisible = true
            val currentContext = this.requireActivity()
            /*val messageTitleText =//на Figma указан другой текст, чем в условиях задачи, если на фигма правильно, то можно этот текст использовать
                getString(R.string.do_you_wish_to_delete1) + myBinding.titleName.text.toString() + getString(
                    R.string.do_you_wish_to_delete2
                )*/
            MaterialAlertDialogBuilder(currentContext, R.style.MyCustomDialogStyle)
                .setTitle(R.string.delete_playlist)
                .setMessage(R.string.delete_playlist2)
                .setNegativeButton(R.string.no) { dialog, which -> // «Нет»
                    myBinding.bottomSheetCurtainForMessage.isVisible = false
                }
                .setPositiveButton(R.string.yes) { dialog, which -> // «Да»
                    myBinding.bottomSheetCurtainForMessage.isVisible = false
                    myViewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
                .show()
        }
        //recycler view
        myBottomSheetTrackAdapter.tracks = myBottomSheetListOfTracks
        myBinding.rvPlaylist.adapter = myBottomSheetTrackAdapter
        myBottomSheetTrackAdapter.setOnClickListener {
            myViewModel.trackWasClicked(track = it,
                fragmentOpener = { findNavController().navigate(R.id.action_playlistOverviewFragment_to_playerFragment) })
        }
        myBottomSheetTrackAdapter.setOnLongClickListener {
            myBinding.bottomSheetCurtainForMessage.isVisible=true
            MaterialAlertDialogBuilder(this.requireActivity(), R.style.MyCustomDialogStyle)
                .setTitle(R.string.do_you_wish_to_delete_track)
                .setMessage(R.string.do_you_wish_to_delete_track2)
                .setNegativeButton(R.string.cancel) { dialog, which -> // «Нет»
                    myBinding.bottomSheetCurtainForMessage.isVisible = false
                }
                .setPositiveButton(R.string.delete) { dialog, which -> // «Да»
                    myBinding.bottomSheetCurtainForMessage.isVisible = false
                    myViewModel.trackDelete(it)
                }
                .show()
        }
    }

    private fun renderPlaylistInfo(playlist: Playlist, renderSettingsInfo: Boolean) {
        Glide.with(this.requireContext())
            .load(playlist.imgSrc)
            .placeholder(R.drawable.placeholder)
            .fitCenter()
            .into(myBinding.imageViewAlbum)
        myBinding.titleName.setText(playlist.playlistName)
        val minutes = playlist.totalSeconds / 60 + if(playlist.totalSeconds%60 ==0)0 else 1
        val minProunounce = when (minutes) {
            1 -> R.string.minutes1
            2, 3, 4 -> R.string.minutes234
            else -> R.string.minutes0
        }
        myBinding.titleTime.setText(minutes.toString() + getString(minProunounce))
        val numberOfTracks = playlist.listOfTracks.size.toString()
        val tracksPronounce =if (playlist.listOfTracks.size == 1) numberOfTracks+getString(R.string.track) else numberOfTracks+getString(R.string.tracks)

        myBinding.titleNumberOfTracks.setText(tracksPronounce)
        myBinding.titleDescription.setText(playlist.playlistDescription)


        if (renderSettingsInfo) renderSmallPlaylistInfo(
            playlist.imgSrc,
            playlist.playlistName,
            tracksPronounce
        )
    }

    private fun renderSmallPlaylistInfo(picUri: Uri?, name: String, number: String) {
        Glide.with(this.requireContext())
            .load(picUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    AndroidUtilities.dpToPx(
                        2f,
                        this.requireContext()
                    )
                )
            )
            .into(myBinding.playlistSmallImage)
        myBinding.playlistNameSmall.setText(name)
        myBinding.tracksNumberSmall.setText(number)
    }
    private fun showNoList(){
        Toast.makeText(context, R.string.no_tracks, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        myViewModel.checkValues()
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

}