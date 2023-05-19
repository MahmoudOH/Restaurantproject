package com.example.resturantproject.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.databinding.ActivityMainBinding
import com.example.resturantproject.helpers.Prefs


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = Prefs(this)
        binding.button.setOnClickListener {
            prefs.emailPref = null
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

    }
}