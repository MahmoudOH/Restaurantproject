package com.example.resturantproject.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.resturantproject.R
import com.example.resturantproject.databinding.MealItemBinding
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.Restaurant
import com.example.resturantproject.views.MealDetailsActivity
import com.squareup.picasso.Picasso


class MealAdapter(private var activity: Activity, var data: ArrayList<Meal>) :
    RecyclerView.Adapter<MealAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun setMeals(meals: ArrayList<Meal>) {
        data = meals
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MealItemBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    private fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(meal: Meal) {
        data.add(meal)
        notifyItemInserted(data.size)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvDetailsMeal.text = item.details
        holder.binding.tvPricreMeal.text = "${item.price}$"
        holder.binding.rating.rating = item.rate

        item.restaurant.get().addOnSuccessListener {
            if (it.exists()) {
                holder.binding.tvMealRestaurant.text =
                    it.toObject(Restaurant::class.java)?.name ?: "Deleted Restaurant"
            }
        }

        Picasso.get().load(item.image).into(holder.binding.ivMeal);

        holder.binding.root.setOnClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Buy Meal")
            alertDialog.setMessage("Are you sure you want to buy this meal?")
            alertDialog.setIcon(R.drawable.ic_add_to_cart)

            alertDialog.setPositiveButton("Yes") { _, _ ->
                // TODO (Add meal to bought_meals collection)
            }
            alertDialog.setNegativeButton("No") { _, _ ->
            }

            alertDialog.create().show()
        }
    }

}