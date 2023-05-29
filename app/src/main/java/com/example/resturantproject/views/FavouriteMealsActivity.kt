package com.example.resturantproject.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resturantproject.adapters.MealAdapter
import com.example.resturantproject.databinding.ActivityFavouriteMealsBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Prefs

class FavouriteMealsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFavouriteMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter: MealAdapter
        val db = FireStoreDatabase(applicationContext)
        val prefs = Prefs(this)

        adapter = MealAdapter(this, db.getAllFavouriteMeals(prefs.emailPref!!))

        binding.rvMeals.layoutManager = LinearLayoutManager(this)

        binding.rvMeals.adapter = adapter
    }
}
