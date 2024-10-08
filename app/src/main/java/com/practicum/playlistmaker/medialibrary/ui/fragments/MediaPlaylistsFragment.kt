package com.practicum.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMediaPlaylistsBinding
import com.practicum.playlistmaker.medialibrary.domain.models.MediaPlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaPlaylistsFragment : Fragment() {
    private lateinit var myBinding: FragmentMediaPlaylistsBinding
    private val myViewModel by viewModel<MediaPlaylistsFragmentViewModel> {
        parametersOf(
            requireArguments().getBoolean(
                FLAG_SAVE
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentMediaPlaylistsBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.getMediaPlaylistsLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is MediaPlaylistsState.NoPlaylists -> {
                    renderNoMatch(true)
                    myBinding.progressBar.isVisible=false
                }

                is MediaPlaylistsState.Default -> {//заглушка
                    renderNoMatch(false)
                    myBinding.progressBar.isVisible=true
                }
            }
        }
    }
    private fun renderNoMatch(isVisible:Boolean){
        myBinding.btNewPlaylist.isVisible = isVisible
        myBinding.emptyPlaylistsText.isVisible = isVisible
        myBinding.notFoundPic.isVisible = isVisible
    }

    companion object {
        const val FLAG_SAVE = "flagsaveplaylists"
        fun newInstance(isEmpty: Boolean) = MediaPlaylistsFragment().apply {
            arguments = Bundle().apply {
                putBoolean(FLAG_SAVE, isEmpty) //на случай кастомизации
            }
        }
    }
}