package com.practicum.playlistmaker.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var myBinding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(myBinding.root)
        val myNavHostFragment =
            supportFragmentManager.findFragmentById((R.id.rootFragmentContainerView)) as NavHostFragment
        val myNavController = myNavHostFragment.navController
        val myBottomNavigationView = myBinding.bottomNavigationView
        myBottomNavigationView.setupWithNavController(myNavController)
    }

}