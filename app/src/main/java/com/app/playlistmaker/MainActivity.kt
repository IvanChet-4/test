package com.app.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val mainFind = findViewById<Button>(R.id.button_Find)
        val mainSettings = findViewById<Button>(R.id.button_Settings)
        val mainMedia = findViewById<Button>(R.id.button_Media)

        mainFind.setOnClickListener {
            val displayIntent = Intent(this, FinderActivity::class.java)
            startActivity(displayIntent)
            finish()
        }

        mainSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
            finish()
        }

        mainMedia.setOnClickListener {
            val displayIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(displayIntent)
            finish()
        }
    }
}