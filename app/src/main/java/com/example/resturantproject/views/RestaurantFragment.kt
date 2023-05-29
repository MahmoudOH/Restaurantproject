package com.example.resturantproject.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resturantproject.adapters.RestaurantAdapter
import com.example.resturantproject.databinding.FragmentRestaurantBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.model.Restaurant
import com.google.firebase.firestore.ktx.toObject
import java.util.*


class RestaurantFragment : Fragment() {

    private lateinit var binding: FragmentRestaurantBinding
    private lateinit var adapter: RestaurantAdapter
    private val restaurants = ArrayList<Restaurant>()
    private lateinit var filteredRestaurants: ArrayList<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FireStoreDatabase(requireActivity().applicationContext)
        adapter = RestaurantAdapter(requireActivity(), ArrayList<Restaurant>())

        db.getAllRestaurants().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                restaurants.add(document.toObject())
            }
            adapter.setRestaurants(restaurants)
            Log.d("DB.getAllRestaurants", "Restaurants Found in Firestore!$restaurants")
        }.addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Failed to get restaurants! Try again later",
                Toast.LENGTH_SHORT
            )
                .show()
            Log.d("DB.getAllRestaurants", "Failed to get restaurants from Firestore!\n${e}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRestaurant.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRestaurant.adapter = adapter

        binding.svRestaurant.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterRestaurants(query)
                return false
            }
        })
        binding.svRestaurant.setOnCloseListener {
            adapter.setRestaurants(restaurants)

            false
        }
    }

    private fun filterRestaurants(query: String?) {
        filteredRestaurants = ArrayList<Restaurant>()
        for (restaurant in restaurants) {
            if (query?.lowercase(Locale.getDefault())?.let {
                    restaurant.name.lowercase(Locale.getDefault())
                        .contains(it)
                } == true
            ) {
                filteredRestaurants.add(restaurant)
            }
        }
        adapter.setRestaurants(filteredRestaurants)
    }

}