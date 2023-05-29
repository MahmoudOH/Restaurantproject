package com.example.resturantproject.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.resturantproject.R
import com.example.resturantproject.databinding.RestaurantItemBinding
import com.example.resturantproject.model.Restaurant
import com.example.resturantproject.views.MealDetailsActivity
import com.example.resturantproject.views.RestaurantDetailsActivity
import com.squareup.picasso.Picasso


class RestaurantAdapter(private var activity: Activity, var data: ArrayList<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: RestaurantItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun setRestaurants(restaurants: ArrayList<Restaurant>) {
        data = restaurants
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RestaurantItemBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    private fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(restaurant: Restaurant) {
        data.add(restaurant)
        notifyItemInserted(data.size)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.binding.tvNameRestaurant.text = item.name
        holder.binding.tvAboutRestaurant.text = item.about
        holder.binding.rating.rating = item.rate

        Picasso.get().load(item.image).into(holder.binding.ivRestaurant);


        holder.binding.root.setOnClickListener {
            val intent = Intent(activity, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant", data[position])
            activity.startActivity(intent)
        }

        // TODO (Only allow admin to delete restaurant)
        holder.binding.root.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle("Delete Meal")
            alertDialog.setMessage("Are you sure?")
            alertDialog.setIcon(R.drawable.ic_delete)
            alertDialog.setCancelable(false)

            alertDialog.setPositiveButton("Yes") { _, _ ->
                removeItem(position)
            }
            alertDialog.setNegativeButton("No") { _, _ ->
            }

            alertDialog.create().show()
            false
        }
    }

}