package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var myBinding: ActivitySettingsBinding
    private val myViewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(myBinding.root)
        myViewModel.getNightModStateLiveData().observe(this){
            nightState->
                myBinding.themeSwitcher.isChecked=nightState
        }
        //кнопка назад
        myBinding.btBack.setOnClickListener { finish()
            /*val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                this.startActivity(intent)*/}
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