package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        //кнопки и текст
        val btBack = findViewById<Button>(R.id.bt_back)
        val ivPoster = findViewById<ImageView>(R.id.imageViewAlbum)
        val btPlay = findViewById<ImageView>(R.id.bt_play)
        val btAddToPlaylist = findViewById<ImageView>(R.id.bt_add_to_playlist)
        val btLike = findViewById<ImageView>(R.id.bt_like)
        val titleName = findViewById<TextView>(R.id.title_name)
        val titleAuthor = findViewById<TextView>(R.id.title_author)
        val titleTime = findViewById<TextView>(R.id.title_time)
        val trackGenre = findViewById<TextView>(R.id.track_genre)
        val trackAlbum = findViewById<TextView>(R.id.track_album)
        val trackAlbumGroup = findViewById<Group>(R.id.album_group)
        val trackDuration = findViewById<TextView>(R.id.track_duration)
        val trackCountry = findViewById<TextView>(R.id.track_country)
        val trackYear = findViewById<TextView>(R.id.track_year)
        //загрузка текущего трека
        val myPref=getSharedPreferences(SharedPreferenceManager.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE)
        val currentTrack= SharedPreferenceManager.getSavedTrack(myPref)
        //записываем показания трека в окно
        if(currentTrack!=null) {
            //постер
            Glide.with(this.applicationContext)
                .load(currentTrack?.getCoverImg())
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .transform(RoundedCorners(Utilities.dpToPx(8f, this)))
                .into(ivPoster)
            //текст
            titleName.setText(currentTrack?.trackName)
            titleAuthor.setText(currentTrack?.artistName)
            trackDuration.setText(currentTrack?.showTrackTime())
            trackCountry.setText(currentTrack?.country)
            trackGenre.setText(currentTrack.primaryGenreName)
            trackYear.setText(currentTrack.releaseDate.take(4))//год это первые 4 цифры
            //по условию альбома может и не быть у песни
            if(currentTrack.collectionName?.isNotEmpty()==true){
                trackAlbumGroup.isVisible=true
                trackAlbum.setText(currentTrack?.collectionName)
            } else {trackAlbumGroup.isVisible=false}
        }
        else{
            //на случай ошибки
            Toast.makeText(this,"ошибка записи трека", Toast.LENGTH_SHORT)
        }
        //активность кнопок кроме назад пока не нужна
        btBack.setOnClickListener {
            finish()
        }
    }
}