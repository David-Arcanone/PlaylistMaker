package com.practicum.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaFavoriteTracksBinding
import com.practicum.playlistmaker.medialibrary.domain.models.MediaFavoritesState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaFavoritesFragment : Fragment() {
    private lateinit var myBinding: FragmentMediaFavoriteTracksBinding
    private val myViewModel by viewModel<MediaFavoritesFragmentViewModel> {
        parametersOf(requireArguments().getBoolean(FLAG_SAVE))
    }
    private val myFavHistoryTracks = mutableListOf<Track>()
    private val favHistoryTrackAdapter = TrackAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentMediaFavoriteTracksBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.updateFavoritesIfInitialized()
        myViewModel.getMediaFavoritesLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is MediaFavoritesState.NoFavorites -> {
                    renderNoMatch(true)
                    myBinding.progressBar.isVisible = false
                }

                is MediaFavoritesState.Default -> {//как заглушка
                    renderNoMatch(false)
                    myBinding.progressBar.isVisible = true
                }

                is MediaFavoritesState.Favorites -> {
                    renderMatch(it.favTracks)
                    myBinding.progressBar.isVisible = false
                }
            }
        }
        favHistoryTrackAdapter.tracks=myFavHistoryTracks
        myBinding.rvHistoryTracks.adapter = favHistoryTrackAdapter
        favHistoryTrackAdapter.setOnClickListener(mytrackClicker)
    }
    val mytrackClicker = { track: Track ->
        myViewModel.showClickOnTrack(newTrack = track,//добавление в архив
            fragmentOpener = {
                findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment)
            })
    }

    private fun renderNoMatch(isVisible: Boolean) {
        myBinding.emptyMediaText.isVisible = isVisible
        myBinding.notFoundPic.isVisible = isVisible
        myBinding.rvHistoryTracks.isVisible=false
    }

    private fun renderMatch(currentFavTracks:List<Track>) {
        myBinding.emptyMediaText.isVisible = false
        myBinding.notFoundPic.isVisible = false
        myBinding.rvHistoryTracks.isVisible=true
        myFavHistoryTracks.clear()
        myFavHistoryTracks.addAll(currentFavTracks)
        favHistoryTrackAdapter.notifyDataSetChanged()
    }

    companion object {
        const val FLAG_SAVE = "flagsavefavorites"
        fun newInstance(isEmpty: Boolean) = MediaFavoritesFragment().apply {
            arguments = Bundle().apply {
                putBoolean(FLAG_SAVE, isEmpty) //на случай кастомизации
            }
        }
    }
}