package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 * Entry point of the application.
 * Displays a branded splash screen for a short duration before navigating to the main dashboard.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the splash screen layout
        setContentView(R.layout.activity_splash)

        // Artificial delay of 2 seconds to allow the logo to be seen
        // before transitioning to the MainActivity.
        Handler(Looper.getMainLooper()).postDelayed({
            // Launch the Main Activity
            startActivity(Intent(this, MainActivity::class.java))
            // Finish this activity so the user can't navigate back to it
            finish()
        }, 2000)
    }
}