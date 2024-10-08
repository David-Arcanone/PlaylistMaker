package com.practicum.playlistmaker.medialibrary.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMediaFavoriteTracksBinding
import com.practicum.playlistmaker.medialibrary.domain.models.MediaFavoritesState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaFavoritesFragment : Fragment() {
    private lateinit var myBinding: FragmentMediaFavoriteTracksBinding
    private val myViewModel by viewModel<MediaFavoritesFragmentViewModel> {
        parametersOf(requireArguments().getBoolean(FLAG_SAVE))
    }

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
            }
        }
    }

    private fun renderNoMatch(isVisible: Boolean) {
        myBinding.emptyMediaText.isVisible = isVisible
        myBinding.notFoundPic.isVisible = isVisible
    }

    companion object {
        //заготовка на будущее
        const val FLAG_SAVE = "flagsavefavorites"
        fun newInstance(isEmpty: Boolean) = MediaFavoritesFragment().apply {
            arguments = Bundle().apply {
                putBoolean(FLAG_SAVE, isEmpty) //на случай кастомизации
            }
        }
    }
}