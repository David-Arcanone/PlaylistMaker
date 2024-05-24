package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btMedia = findViewById<Button>(R.id.bt_media)
        val btSettings = findViewById<Button>(R.id.bt_settings)
        val btSearch = findViewById<Button>(R.id.bt_search)

        btSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

        btMedia.setOnClickListener {
            //согласно заданию-6 просили убрать Toast
            //Toast.makeText(this@MainActivity, "Нажали на Медиатеку!", Toast.LENGTH_SHORT).show()
            val mediaLibraryIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(mediaLibraryIntent)
        }
        val searchIntent = Intent(this, SearchActivity::class.java)
        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                //согласно заданию-6 просили убрать Toast
                //Toast.makeText(this@MainActivity, "Нажали на Поиск!", Toast.LENGTH_SHORT).show()
                startActivity(searchIntent)
            }
        }
        btSearch.setOnClickListener(searchClickListener)

    }
}