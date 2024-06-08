package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText:EditText
    companion object {
        const val EDIT_VALUE = "EDIT_VALUE"
        const val EDIT_DEFAULT = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val btBack = findViewById<Button>(R.id.bt_back)
        inputEditText = findViewById<EditText>(R.id.edit_text_search)
        val btClear = findViewById<ImageButton>(R.id.bt_clear)
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
        }
        val myTextWatcher = object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // пока небыло задания
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btClear.visibility = clearButtonVisibility(s)
            }
            override fun afterTextChanged(s: Editable?) {
                // пока небыло задания
            }
        }
        inputEditText.addTextChangedListener(myTextWatcher)
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
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}