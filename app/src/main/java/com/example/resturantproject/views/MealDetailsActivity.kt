package com.example.resturantproject.views

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.R
import com.example.resturantproject.databinding.ActivityMealDetailsBinding
import com.example.resturantproject.databinding.DialogAddMealBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.User

class MealDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealDetailsBinding
    private lateinit var user: User
    private lateinit var dialogBinding: DialogAddMealBinding
    private lateinit var dialog: AlertDialog
    private var image: String? = null
    private val GALLERY_REQUEST_CODE = 100
    private var meal: Meal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FireStoreDatabase()
        val prefs = Prefs(this)
//        user = db.getUserByEmail(prefs.emailPref!!)!!

        val mealId = intent.getLongExtra("mealId", -1)
//        meal = db.getMealById(mealId)
//        if (mealId < 0 || meal?.id!! < 0) {
//            Toast.makeText(this, "Can't view meal details", Toast.LENGTH_SHORT).show()
//            finish()
//        }

        if (meal?.image != null) {
            image = meal!!.image
            binding.imgMealDetails.setImageURI(Uri.parse(image))
        }


        binding.tvNameDetails.text = meal?.name
        binding.tvIngredientsDetails.text = meal?.details
//        binding.tvOriginDetails.text = meal?.origin
        binding.tvPriceDetails.text = meal?.price.toString()
//        binding.tvCategoryDetails.text = meal?.category
        binding.rbMealDetails.rating = meal?.rate!!

        val builder = AlertDialog.Builder(this)
        dialogBinding = DialogAddMealBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        builder.setPositiveButton("Save") { _, _ -> }
        builder.setNegativeButton("Cancel") { _, _ -> }

        dialog = builder.create()
        binding.btnBackMealDetails.setOnClickListener {
            finish()
        }

        dialogBinding.imgMealDialog.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        binding.btnEditMeal.setOnClickListener {
            val name = dialogBinding.txtNameDialog
            val ingredients = dialogBinding.txtIngredientsDialog
            val origin = dialogBinding.txtOriginDialog
            val price = dialogBinding.txtPriceDialog
            val rating = dialogBinding.rbMealDialog
            val category = dialogBinding.txtCategoryDialog

            dialogBinding.txtNameDialog.setText(meal?.name)
            dialogBinding.txtIngredientsDialog.setText(meal?.details)
//            dialogBinding.txtOriginDialog.setText(meal?.origin)
            dialogBinding.txtPriceDialog.setText(meal?.price.toString())
            dialogBinding.rbMealDialog.rating = meal?.rate!!
//            dialogBinding.txtCategoryDialog.setText(meal?.category)
            if (meal?.image != null)
                dialogBinding.imgMealDialog.setImageURI(Uri.parse(meal?.image))

            dialog.show()

            dialog.setOnDismissListener {
                name.text.clear()
                ingredients.text.clear()
                origin.text.clear()
                price.text.clear()
                rating.rating = 0f
                category.text.clear()
                dialogBinding.imgMealDialog.setImageResource(R.drawable.ic_launcher_foreground)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                var isValid = true
                if (name.text.toString().isEmpty()) {
                    name.error = "Name is required!"
                    isValid = false
                }

                if (ingredients.text.toString().isEmpty()) {
                    ingredients.error = "Ingredients is required!"
                    isValid = false
                }

                if (origin.text.toString().isEmpty()) {
                    origin.error = "Origin is required!"
                    isValid = false
                }

                if (rating.rating <= 0) {
                    Toast.makeText(
                        this,
                        "Rating is required!",
                        Toast.LENGTH_LONG
                    ).show()
                    isValid = false
                }

                if (category.text.toString().isEmpty()) {
                    category.error = "Category is required!"
                    isValid = false
                }

                if (isValid) {
//                    val updatedMeal = Meal(
//                        mealId,
//                        name.text.toString(),
//                        image,
//                        ingredients.text.toString(),
//                        origin.text.toString(),
//                        price.text.toString().toDouble(),
//                        rating.rating,
//                        category.text.toString(),
//                    )
//                    val isUpdated = db.updateMeal(updatedMeal)
//                    if (isUpdated) {
//                        setView(updatedMeal)
//                        Toast.makeText(this, "Meal updated successfully!", Toast.LENGTH_SHORT)
//                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "Can't update Meal! Try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dialog.dismiss()
                }
            }
        }

    }

//    private fun setView(meal: Meal?) {
//        binding.tvNameDetails.text = meal?.name
//        binding.tvIngredientsDetails.text = meal?.details
//        binding.tvOriginDetails.text = meal?.origin
//        binding.tvPriceDetails.text = meal?.price.toString()
//        binding.rbMealDetails.rating = meal?.rate!!
//        binding.tvCategoryDetails.text = meal.category
//        if (meal.image != null)
//            binding.imgMealDetails.setImageURI(Uri.parse(meal.image))
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            image = Helpers.getPath(contentResolver, data.data)
//            dialogBinding.imgMealDialog.setImageURI(data.data)
//        }
//    }
//}