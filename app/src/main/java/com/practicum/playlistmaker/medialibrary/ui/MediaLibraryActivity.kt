package com.practicum.playlistmaker.medialibrary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.practicum.playlistmaker.medialibrary.ui.fragments.MediaViewPagerAdapter

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var myBinding: ActivityMediaLibraryBinding
    private lateinit var myTabMediator: TabLayoutMediator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(myBinding.root)
        myBinding.btBack.setOnClickListener { finish() }
        myBinding.mediaViewPager.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)
        myTabMediator =
            TabLayoutMediator(myBinding.tabLayout, myBinding.mediaViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getResources().getString(R.string.favorite_tracks)
                    1 -> tab.text = getResources().getString(R.string.playlists)
                }
            }
        myTabMediator.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        myTabMediator.detach()
    }
}