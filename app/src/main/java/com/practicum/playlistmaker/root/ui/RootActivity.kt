package com.practicum.playlistmaker.root.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding
import com.practicum.playlistmaker.root.domain.models.IMessageForwardInterface
import com.practicum.playlistmaker.search.ui.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.scope.activityScope
import org.koin.androidx.scope.scope

class RootActivity : AppCompatActivity(),IMessageForwardInterface {

    private lateinit var myBinding: ActivityRootBinding
    private var messageJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(myBinding.root)
        val myNavHostFragment =
            supportFragmentManager.findFragmentById((R.id.rootFragmentContainerView)) as NavHostFragment
        val myNavController = myNavHostFragment.navController
        val myBottomNavigationView = myBinding.bottomNavigationView
        myBottomNavigationView.setupWithNavController(myNavController)
        myNavController.addOnDestinationChangedListener{_,destination,_ ->
            when(destination.id){
                R.id.playerFragment , R.id.newPlaylistFragment, R.id.playlistOverviewFragment ->{
                    myBinding.bottomNavigationView.isVisible=false
                    myBinding.navSeparator.isVisible=false
                }
                else -> {
                    myBinding.bottomNavigationView.isVisible=true
                    myBinding.navSeparator.isVisible=true
                }
            }
        }

    }
    override fun makeMessage(myMessage: String){
        myBinding.messageGlobal.setText(myMessage)
        myBinding.messageGlobal.isVisible=true
        messageJob?.cancel()
        messageJob=lifecycleScope.launch {
            delay(MESSAGE_TIME)
            myBinding.messageGlobal.isVisible=false
        }
    }

    companion object{
        const val MESSAGE_TIME=3000L
    }
}