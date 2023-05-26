package com.example.resturantproject.views

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resturantproject.R
import com.example.resturantproject.adapters.MealAdapter
import com.example.resturantproject.databinding.ActivityMealsBinding
import com.example.resturantproject.databinding.DialogAddMealBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.Meal

class MealsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealsBinding
    private lateinit var dialogBinding: DialogAddMealBinding
    private lateinit var dialog: AlertDialog
    private lateinit var adapter: MealAdapter
    private var imageUri: Uri? = null
    private val GALLERY_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FireStoreDatabase()

        adapter = MealAdapter(this, db.getAllMeals())

        binding.rvMeals.layoutManager = LinearLayoutManager(this)

        binding.rvMeals.adapter = adapter

        val builder = AlertDialog.Builder(this)
        dialogBinding = DialogAddMealBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        builder.setPositiveButton("Add") { _, _ ->
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }

        dialogBinding.imgMealDialog.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        dialog = builder.create()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                val name = dialogBinding.txtNameDialog
                val ingredients = dialogBinding.txtIngredientsDialog
                val origin = dialogBinding.txtOriginDialog
                val price = dialogBinding.txtPriceDialog
                val rating = dialogBinding.rbMealDialog
                val category = dialogBinding.txtCategoryDialog

                dialog.show()

                dialog.setOnDismissListener {
                    // Clear previous input
                    dialogBinding.txtNameDialog.text.clear()
                    dialogBinding.txtIngredientsDialog.text.clear()
                    dialogBinding.txtOriginDialog.text.clear()
                    dialogBinding.txtPriceDialog.text.clear()
                    dialogBinding.rbMealDialog.rating = 0f
                    dialogBinding.txtCategoryDialog.text.clear()
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
                        adapter.addItem(
                            Meal()
                        )
                        dialog.dismiss()
                    }
                }
            }
            R.id.favourite -> {
                startActivity(Intent(this, FavouriteMealsActivity::class.java))
            }
            R.id.profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
            }
            R.id.logout -> {
                val prefs = Prefs(applicationContext)
                prefs.emailPref = null
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            dialogBinding.imgMealDialog.setImageURI(data.data)
        }
    }


}