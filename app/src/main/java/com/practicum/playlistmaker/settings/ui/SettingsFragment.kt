package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var myBinding: FragmentSettingsBinding
    private val myViewModel by viewModel<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.getNightModStateLiveData().observe(viewLifecycleOwner) { nightState ->
            myBinding.themeSwitcher.isChecked = nightState
        }
        //кнопка поделиться
        myBinding.btShare.setOnClickListener { myViewModel.share() }
        //кнопка поддержка
        myBinding.btSupport.setOnClickListener { myViewModel.support() }
        //кнопка соглашения об использовании
        myBinding.btUserAgreement.setOnClickListener { myViewModel.userAgreement() }
        //поменять тему
        myBinding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            myViewModel.saveNightTheme(checked)
        }
    }

}