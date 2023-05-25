package com.app.playlistmaker

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ConfigurationInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import java.security.AccessController.getContext

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsHome = findViewById<ImageView>(R.id.button_settings_Home)
        val themeSwitch = findViewById<Switch>(R.id.switch_Widget)
        val shareToOtherApp = findViewById<Button>(R.id.button_settings_Share)
        val writeToSupport = findViewById<Button>(R.id.button_settings_Support)
        val settingsConfirm = findViewById<Button>(R.id.button_settings_Confirm)

        settingsHome.setOnClickListener {
            finish()
        }

        var switchStatus: Boolean = false
        themeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchStatus = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchStatus = false
                }
            }




        shareToOtherApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.site_course_Android))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        writeToSupport.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myMail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subj_mes))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
                startActivity(this)
            }
        }

        settingsConfirm.setOnClickListener {
            val openSite = Intent(Intent.ACTION_VIEW);
            openSite.setData(Uri.parse(getString(R.string.urlConfirm)));
            startActivity(openSite);
        }
    }


}