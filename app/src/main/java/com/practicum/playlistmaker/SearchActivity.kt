package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText:EditText
    private companion object {
        const val EDIT_VALUE = "EDIT_VALUE"
        const val EDIT_DEFAULT = ""
    }
    private val trackAdapter= TrackAdapter()
    private val myTracks=ArrayList<Track>()
    private var lastRequest=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val btBack = findViewById<Button>(R.id.bt_back)
        inputEditText = findViewById<EditText>(R.id.edit_text_search)
        val btClear = findViewById<ImageButton>(R.id.bt_clear)
        val recyclerView=findViewById<RecyclerView>(R.id.rv_tracks)
        val btRefresh = findViewById<Button>(R.id.bt_refresh)
        val searchErrorBox = findViewById<LinearLayout>(R.id.error_box)
        val searchErrorPic = findViewById<ImageView>(R.id.error_pic)
        val searchNotFoundPic = findViewById<ImageView>(R.id.not_found_pic)
        val searchErrorText= findViewById<TextView>(R.id.error_text)
        val retrofit=Retrofit.Builder()
            .baseUrl(Utilities.iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val iTunesApiService= retrofit.create<ITunesApiService>()
        trackAdapter.tracks=myTracks
        //2 вспомогательные функции:
        // popupManager помогает с работой заглушек
        // makeSearch запуск поисковых запросов
        fun popupManager(currentMod: Int){
            ///3 режима: 1-очистка, 2-заглушка "нет совпадений", 3-заглушка "ошибка"
            when(currentMod){
                0->{//чистый экран
                    searchErrorBox.isVisible=false
                    trackAdapter.notifyDataSetChanged()
                }
                1->{//нет сопадений
                    searchErrorBox.isVisible=true
                    btRefresh.isVisible=false
                    searchErrorPic.isVisible=false
                    searchNotFoundPic.isVisible=true
                    searchErrorText.setText(R.string.search_error_not_found)
                    trackAdapter.notifyDataSetChanged()
                }
                2->{//ошибка связи
                    searchErrorBox.isVisible=true
                    btRefresh.isVisible=true
                    searchErrorPic.isVisible=true
                    searchNotFoundPic.isVisible=false
                    searchErrorText.setText(R.string.search_error_connection)
                    lastRequest=inputEditText.text.toString()
                    trackAdapter.notifyDataSetChanged()
                }
            }
        }

        fun makeSearch(text:String){
            //очищаем поисковый список
            myTracks.clear()
            popupManager(0)
            //делаем запрос
            iTunesApiService
                .search(text)
                .enqueue(object: Callback<iTunesSearchResponse> {
                    override fun onResponse(call: Call<iTunesSearchResponse>,
                                            response: Response<iTunesSearchResponse>) {
                        if(response.code()==200){
                            myTracks.clear()
                            if(response.body()?.results?.isNotEmpty()==true){
                                //записаны данные и они не null
                                myTracks.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if(myTracks.isEmpty()){//нет совпадений
                                popupManager(1)
                            } else{//есть совпадения ничего менять не надо
                                //popupManager(0)
                            }
                        } else{//произошла ошибка
                            popupManager(2)
                        }
                    }
                    override fun onFailure(call: Call<iTunesSearchResponse>, t: Throwable) {
                        //произошла ошибка
                        myTracks.clear()
                        popupManager(2)
                    }
                })
        }

        if (savedInstanceState != null) {
            val myText=savedInstanceState.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
            inputEditText.setText(myText)
        }
        btBack.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        btClear.setOnClickListener {
            inputEditText.setText("")
            val view = this.currentFocus
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if(view!=null){inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)}
            inputEditText.clearFocus()
            myTracks.clear()
            trackAdapter.notifyDataSetChanged()
        }
        btRefresh.setOnClickListener {
            //Кнопка обновить запускает последний запрос
            if(lastRequest.isNotEmpty())makeSearch(lastRequest)
        }
        val myTextWatcher = object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // пока небыло задания
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btClear.isVisible=!s.isNullOrEmpty()
            }
            override fun afterTextChanged(s: Editable?) {
                // пока небыло задания
            }
        }
        inputEditText.addTextChangedListener(myTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ -> if(actionId==EditorInfo.IME_ACTION_DONE){
            //Запуск Поискового Запроса
            if(inputEditText.text.isNotEmpty())makeSearch(inputEditText.text.toString())
            true}
            false}
        recyclerView.adapter=trackAdapter
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val myText=inputEditText.text.toString()
        outState.putString(EDIT_VALUE, myText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val myText=savedInstanceState.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
        inputEditText.setText(myText)
    }
}