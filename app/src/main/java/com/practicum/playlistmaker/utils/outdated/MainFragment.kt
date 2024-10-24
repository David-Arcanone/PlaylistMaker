package com.practicum.playlistmaker.utils.outdated


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentMainBinding
/*import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.medialibrary.ui.MediaLibraryFragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.settings.ui.SettingsFragment
import com.practicum.playlistmaker.search.ui.SearchFragment*/

class MainFragment : Fragment() {
    private lateinit var myBinding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding=FragmentMainBinding.inflate(inflater,container,false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // черновики оставшиеся от тренировался с переходом во фрагменты
        /*myBinding.btSettings.setOnClickListener {
            //findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
            /*parentFragmentManager.commit {
                replace(R.id.rootFragmentContainerView,
                    SettingsFragment.newInstance(),
                    SettingsFragment.TAG)
                addToBackStack(SettingsFragment.TAG)
            }*/

            /*val settingsIntent = Intent(this, SettingsFragment::class.java)
            startActivity(settingsIntent)*/
        }

        myBinding.btMedia.setOnClickListener {
            //findNavController().navigate(R.id.action_mainFragment_to_mediaLibraryFragment)
        }

        myBinding.btSearch.setOnClickListener {
            //findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }*/
    }
    companion object{
        const val TAG="MainFragment"
        /*fun newInstance():Fragment{
            return MainFragment().apply {
                //на случай кастомизации
            }
        }*/
    }

}