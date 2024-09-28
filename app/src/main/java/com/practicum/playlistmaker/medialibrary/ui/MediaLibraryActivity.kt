package com.practicum.playlistmaker.medialibrary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var myBinding: ActivityMediaLibraryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding= ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(myBinding.root)
    }
}