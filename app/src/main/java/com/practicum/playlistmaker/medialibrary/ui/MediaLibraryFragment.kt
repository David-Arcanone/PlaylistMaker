package com.practicum.playlistmaker.medialibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.practicum.playlistmaker.medialibrary.ui.fragments.MediaViewPagerAdapter

class MediaLibraryFragment : Fragment() {
    private lateinit var myBinding: FragmentMediaLibraryBinding
    private lateinit var myTabMediator: TabLayoutMediator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myBinding.mediaViewPager.adapter = MediaViewPagerAdapter(childFragmentManager, lifecycle)
        myTabMediator =
            TabLayoutMediator(myBinding.tabLayout, myBinding.mediaViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getResources().getString(R.string.favorite_tracks)
                    1 -> tab.text = getResources().getString(R.string.playlists)
                }
            }
        myTabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myTabMediator.detach()
    }

}