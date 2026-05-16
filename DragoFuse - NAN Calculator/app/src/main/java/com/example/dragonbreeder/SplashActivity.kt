package com.example.dragonbreeder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


/**
 * SplashActivity: The very first thing players see when they open DragoFuse.
 *
 * It's a simple welcome screen that displays the logo and a little "loading" 
 * animation with dots to build a bit of excitement before jumping into the game.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Find the TextView where we'll show the "..." loading animation.
        // It's a nice little touch to show the user that the app is active and loading.
        val dots = findViewById<TextView>(R.id.loadingDots)
        val handler = Handler(Looper.getMainLooper())
        var count = 0
        
        // This little loop adds a dot every half-second: . -> .. -> ... -> (reset)
        val runnable = object : Runnable {
            override fun run() {
                count = (count + 1) % 4
                dots.text = ".".repeat(count)
                // Schedule the next dot update in 500ms to keep the animation going
                handler.postDelayed(this, 500)
            }
        }
        handler.post(runnable)

        // We'll hang out on the splash screen for 3 seconds (3000ms).
        // It's long enough to show the logo but short enough that users won't get impatient.
        Handler(Looper.getMainLooper()).postDelayed({
            // Launch the main game screen
            startActivity(Intent(this, MainActivity::class.java))
            
            // We call finish() so the user can't hit 'Back' and return to the splash screen.
            finish()
        }, 3000)
    }
}
