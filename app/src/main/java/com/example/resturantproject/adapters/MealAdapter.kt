package com.example.resturantproject.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.resturantproject.R
import com.example.resturantproject.databinding.MealItemBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.Meal
import com.example.resturantproject.views.MealDetailsActivity

class MealAdapter(private var activity: Activity, var data: ArrayList<Meal>) :
    RecyclerView.Adapter<MealAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val db = FireStoreDatabase()
    private val prefs = Prefs(activity)

    private var favData: ArrayList<Meal> = db.getAllFavouriteMeals(prefs.emailPref!!)

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MealItemBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    private fun removeItem(position: Int) {
//        val isDeleted = db.deleteMeal(data[position])
//        if (isDeleted) {
//            data.removeAt(position)
//            notifyItemRemoved(position)
//            Toast.makeText(activity, "Deleted successfully!", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(activity, "Can't delete meal! Try again later", Toast.LENGTH_LONG).show()
//        }
    }

    fun addItem(meal: Meal) {
//        val id = db.insertMeal(meal)
//        if (id > 0) {
//            meal.id = id
//            data.add(meal)
//            notifyItemInserted(data.size)
//            Toast.makeText(activity, "Added successfully!", Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(activity, "Can't add new Meal! Try again later", Toast.LENGTH_LONG)
//                .show()
//        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvCategory.text = item.details
        holder.binding.rating.rating = item.rate
        holder.binding.ivMeal.setImageURI(if (item.image == null) null else Uri.parse(item.image))

        if (favData.contains(data[position]))
            holder.binding.imgFavItem.setImageResource(R.drawable.ic_star)

        holder.binding.imgFavItem.setOnClickListener {
            val msg: String
            if (holder.binding.imgFavItem.drawable.toBitmap().sameAs(
                    ResourcesCompat.getDrawable(
                        activity.resources,
                        R.drawable.ic_star_gray,
                        activity.theme
                    )?.toBitmap()
                )
            ) {
//                val isAdded = db.addToFavourite(prefs.emailPref!!, data[position].id)
//
//                msg = if (isAdded) {
//                    holder.binding.imgFavItem.setImageResource(R.drawable.ic_star)
//                    "Added to Favourite!"
//                } else {
//                    "Can't add to Favourite! Try again later"
//                }
            } else {
//                val isRemoved = db.removeFromFavourite(prefs.emailPref!!, data[position].id)
//
//                msg = if (isRemoved) {
//                    holder.binding.imgFavItem.setImageResource(R.drawable.ic_star)
//                    "Removed from Favourite!"
//                } else {
//                    "Can't remove from Favourite! Try again later"
//                }
//                holder.binding.imgFavItem.setImageResource(R.drawable.ic_star_gray)
            }
//            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(activity, MealDetailsActivity::class.java)
            intent.putExtra("mealId", data[position].id)
            activity.startActivity(intent)
        }

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