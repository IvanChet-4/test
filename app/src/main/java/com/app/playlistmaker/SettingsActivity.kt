package com.app.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsHome = findViewById<Button>(R.id.button_settings_Home)

        val shareToOtherApp = findViewById<Button>(R.id.button_settings_Share)

        val writeToSupport = findViewById<Button>(R.id.button_settings_Support)

        val settingsConfirm = findViewById<Button>(R.id.button_settings_Confirm)

        settingsHome.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
            finish()
        }

        shareToOtherApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.site_course_Android))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            finish()
        }

        writeToSupport.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myMail)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subj_mes))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
            startActivity(shareIntent)
            finish()
        }

        settingsConfirm.setOnClickListener {
            val openSite = Intent(Intent.ACTION_VIEW);
            openSite.setData(Uri.parse(getString(R.string.urlConfirm)));
            startActivity(openSite);
            finish()
        }
    }
}