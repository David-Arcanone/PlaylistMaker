package com.practicum.playlistmaker.presentation.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val mySettingsInteractor = Creator.provideGetSettingsInteractor()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //кнопка назад
        val btBack = findViewById<Button>(R.id.bt_back)
        btBack.setOnClickListener {
            finish()
        }
        //кнопка поделиться
        val btShare = findViewById<Button>(R.id.bt_share)
        btShare.setOnClickListener { mySettingsInteractor.openShare() }
        //кнопка поддержка
        val btSupport = findViewById<Button>(R.id.bt_support)
        btSupport.setOnClickListener { mySettingsInteractor.openSupport() }
        //кнопка соглашения об использовании
        val btUserAgreement = findViewById<Button>(R.id.bt_user_agreement)
        btUserAgreement.setOnClickListener { mySettingsInteractor.openUserAgreement() }
        //поменять тему
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val savedNightTheme = mySettingsInteractor.getSavedNightTheme()
        themeSwitcher.isChecked = savedNightTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}