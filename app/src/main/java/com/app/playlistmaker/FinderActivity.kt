package com.app.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView

class FinderActivity : AppCompatActivity() {
       private val KEY_TEXT = ""
        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_finder)

            val inputEditText = findViewById<EditText>(R.id.inputEditText)
            val clearButton = findViewById<ImageView>(R.id.clearIcon)

            clearButton.setOnClickListener {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                inputEditText.clearFocus()
                inputEditText.setText("")
            }

            val simpleTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){
                    // empty
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearButton.visibility = clearButtonVisibility(s)
                }

                override fun afterTextChanged(s: Editable?) {
                    // empty
                }
            }
            inputEditText.addTextChangedListener(simpleTextWatcher)
            val arrowButton = findViewById<Button>(R.id.button_finder_Home)
            arrowButton.setOnClickListener {
                val displayIntent = Intent(this, MainActivity::class.java)
                startActivity(displayIntent)
                finish()
            }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            val inputEditText = findViewById<EditText>(R.id.inputEditText)
            outState.putString(KEY_TEXT, inputEditText.text.toString())
        }

        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            super.onRestoreInstanceState(savedInstanceState)
            savedInstanceState.getString(KEY_TEXT, "")
        }

        private fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }