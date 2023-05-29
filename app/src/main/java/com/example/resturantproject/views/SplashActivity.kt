package com.example.resturantproject.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.databinding.ActivitySplashBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Prefs
import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)

        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var hasNotStartedActivity = true

        val prefs = Prefs(this)
        val db = FireStoreDatabase(applicationContext)
        if (prefs.emailPref != null) {
            db.getUserByEmail(prefs.emailPref!!, { user ->
                if (user != null) {
                    hasNotStartedActivity = false
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
            }, { e ->
            })
        }

        // Delay for the splash screen
        Handler().postDelayed({
            if (hasNotStartedActivity) {
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }, SPLASH_DELAY)
    }
}