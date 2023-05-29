package com.example.resturantproject.views

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.example.resturantproject.R
import com.example.resturantproject.databinding.ActivityRestaurantDetailsBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.model.Restaurant
import com.squareup.picasso.Picasso
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions


class RestaurantDetailsActivity : AppCompatActivity() {

    private lateinit var restaurant: Restaurant
    private lateinit var binding: ActivityRestaurantDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FireStoreDatabase(applicationContext)

        restaurant = intent.getParcelableExtra("restaurant") ?: Restaurant()

        binding.tvNameDetails.text = restaurant.name
        binding.rbRestaurantDetails.rating = restaurant.rate
        binding.tvRestaurantAbout.text = restaurant.about


        val mealsFragment = MealsFragment()
        val bundle = Bundle().apply {
            putParcelable("restaurant", restaurant)
        }
        mealsFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, mealsFragment)
            .commit()

        Picasso.get().load(restaurant.image).into(binding.imgRestaurantDetails)

        binding.btnRestaurantLocation.setOnClickListener {
            Helpers.showLoading(this)

            db.getCurrentUser({
                val userLocation = LatLng(
                    it.location?.latitude ?: 0.0,
                    it.location?.longitude ?: 0.0
                )
                val resLocation = LatLng(
                    restaurant.location?.latitude ?: 0.0,
                    restaurant.location?.longitude ?: 0.0
                )


                val intent = Intent(this, MapsActivity::class.java)

                val polylines = ArrayList<PolylineOptions>()
                polylines.add(
                    PolylineOptions()
                        .add(userLocation)
                        .add(resLocation)
                        .color(Color.RED)
                        .visible(true)
                )

                intent.putExtra("polylines", polylines)
                intent.putExtra("camera", resLocation)

                startActivity(intent)
                Helpers.hideLoading()
            }, {
                Helpers.hideLoading()
                AlertDialog.Builder(this)
                    .setTitle("Something went wrong")
                    .setMessage("Unable to fetch user from the database. It may be because there's no internet connection.\nPlease try again later")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                        finish()
                    }
                    .show()
            })


        }


    }
}