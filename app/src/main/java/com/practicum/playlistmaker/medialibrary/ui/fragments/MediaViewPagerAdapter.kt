package com.practicum.playlistmaker.medialibrary.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediaViewPagerAdapter(myFragmentManager: FragmentManager,myLifecycle:Lifecycle):FragmentStateAdapter(myFragmentManager,myLifecycle) {
    override fun getItemCount(): Int {
        return NUMBER_OF_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->MediaFavoritesFragment.newInstance(true)//можно поставить false чтоб поглядеть на заглушку
            else->MediaPlaylistsFragment.newInstance(true)//можно поставить false чтоб поглядеть на заглушку
        }
    }
    companion object{
        private const val NUMBER_OF_TABS=2
    }
}