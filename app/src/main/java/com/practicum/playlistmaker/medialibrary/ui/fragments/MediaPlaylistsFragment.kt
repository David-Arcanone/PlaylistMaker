package com.practicum.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaPlaylistsBinding
import com.practicum.playlistmaker.medialibrary.domain.models.MediaPlaylistsState
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.newPlaylist.ui.PlaylistGridAdapter
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
    private val myPlaylistsAdapter = PlaylistGridAdapter()
    private val myPlaylists = mutableListOf<Playlist>()

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
                    renderNoMatch(true, true, false)
                    myBinding.progressBar.isVisible = false
                }

                is MediaPlaylistsState.Default -> {//заглушка
                    renderNoMatch(false, false, false)
                    myBinding.progressBar.isVisible = true
                }

                is MediaPlaylistsState.PlaylistsFound -> {
                    renderNoMatch(false, true, true)
                    myBinding.progressBar.isVisible = false
                    updateRv(it.listOfPlaylists)
                }
            }
        }
        //Grid
        myBinding.rvHistoryPlaylists.layoutManager = GridLayoutManager(context, 2)
        myPlaylistsAdapter.myPlaylists = myPlaylists
        myPlaylistsAdapter.setOnClickListener { playlist: Playlist ->
            myViewModel.showClickOnPlaylist(currentPlaylist = playlist, fragmentOpener = {
                //здесь будет вызов навигации на новый фрагмент плейлиста (если такое задение появится)
            })
        }
        myBinding.rvHistoryPlaylists.adapter = myPlaylistsAdapter
        //кнопки
        myBinding.btNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }
    }

    private fun renderNoMatch(isVisible: Boolean, isVisibleBt: Boolean, isVisibleRv: Boolean) {
        myBinding.btNewPlaylist.isVisible = isVisibleBt
        myBinding.emptyPlaylistsText.isVisible = isVisible
        myBinding.notFoundPic.isVisible = isVisible
        myBinding.rvHistoryPlaylists.isVisible = isVisibleRv
    }

    private fun updateRv(list: List<Playlist>) {
        myPlaylists.clear()
        myPlaylists.addAll(list)
        myPlaylistsAdapter.notifyDataSetChanged()
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