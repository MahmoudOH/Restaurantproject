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
import com.example.resturantproject.adapters.MealAdapter
import com.example.resturantproject.databinding.FragmentMealsBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.Restaurant
import com.example.resturantproject.model.User
import com.google.firebase.firestore.ktx.toObject
import java.util.*


class MealsFragment : Fragment() {

    private lateinit var binding: FragmentMealsBinding
    private lateinit var adapter: MealAdapter
    private var restaurant: Restaurant? = null
    private val meals = ArrayList<Meal>()
    private lateinit var filteredMeals: ArrayList<Meal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurant = it.getParcelable("restaurant")
        }

        val db = FireStoreDatabase(requireActivity().applicationContext)
        adapter = MealAdapter(requireActivity(), ArrayList<Meal>())

        if (restaurant != null) {
            db.getRestaurantMeals(restaurant!!).addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    meals.add(document.toObject())
                }
                adapter.setMeals(meals)
                Log.d("DB.getRestaurantMeals", "RestaurantMeals Found in Firestore! $meals")
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to get meals! Try again later", Toast.LENGTH_SHORT)
                    .show()
                Log.d("DB.getRestaurantMeals", "Failed to get RestaurantMeals from Firestore! ${e}")
            }
        } else {
            db.getAllMeals().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    meals.add(document.toObject())
                }
                adapter.setMeals(meals)
                Log.d("DB.getAllMeals", "Meals Found in Firestore!$meals")
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Failed to get meals! Try again later", Toast.LENGTH_SHORT)
                    .show()
                Log.d("DB.getAllMeals", "Failed to get meals from Firestore!\n${e}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMealsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMeals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeals.adapter = adapter

        binding.svMeals.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterMeals(query)
                return false
            }
        })
        binding.svMeals.setOnCloseListener {
            adapter.setMeals(meals)

            false
        }
    }

    private fun filterMeals(query: String?) {
        filteredMeals = ArrayList<Meal>()
        for (meal in meals) {
            if (query?.lowercase(Locale.getDefault())?.let {
                    (meal.name + meal.price).lowercase(Locale.getDefault())
                        .contains(it)
                } == true
            ) {
                filteredMeals.add(meal)
            }
        }
        adapter.setMeals(filteredMeals)
    }

}