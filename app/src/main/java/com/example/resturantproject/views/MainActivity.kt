package com.example.resturantproject.views

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.R
import com.example.resturantproject.adapters.MyPagerAdapter
import com.example.resturantproject.databinding.ActivityMainBinding
import com.example.resturantproject.databinding.DialogAddMealBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.Restaurant
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dBinding: DialogAddMealBinding
    private lateinit var dialog: AlertDialog
    private var imageUri: Uri? = null
    private val GALLERY_REQUEST_CODE = 100

    private var fragments = listOf(MealsFragment(), RestaurantFragment())
    private var tabTitles = listOf("Meals", "Restaurants")

    private var restaurants = ArrayList<Restaurant>()
    private var restaurantNames = ArrayList<String>()
    private var selectedRestaurant: Restaurant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FireStoreDatabase(applicationContext)

        val pagerAdapter = MyPagerAdapter(this, fragments)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()


        val builder = AlertDialog.Builder(this)
        dBinding = DialogAddMealBinding.inflate(layoutInflater)
        builder.setView(dBinding.root)

        builder.setPositiveButton("Add") { _, _ ->
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }


        db.getAllRestaurants().addOnSuccessListener {
            for (document in it) {
                restaurants.add(document.toObject())
            }
            restaurantNames = restaurants.map { r -> r.name } as ArrayList<String>
            dBinding.actvAddMealRestaurant.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_selectable_list_item,
                    restaurantNames
                )
            )
        }



        dBinding.actvAddMealRestaurant.threshold = 1

        dBinding.actvAddMealRestaurant.setOnFocusChangeListener { v, hasFocus ->
            val enteredText = dBinding.actvAddMealRestaurant.text.toString()
            if (!hasFocus) {
                if (!restaurants.map { r -> r.name }.contains(enteredText)) {
                    // Clear the input if text is not in options
                    dBinding.actvAddMealRestaurant.text = null
                }
            }

        }

        dBinding.actvAddMealRestaurant.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val enteredText = textView.text.toString()
                if (!restaurants.map { r -> r.name }.contains(enteredText)) {
                    // Clear the input if text is not in options
                    textView.text = null
                }
                true
            } else {
                false
            }
        }

        dBinding.actvAddMealRestaurant.setOnItemClickListener { parent, view, position, id ->
            selectedRestaurant = restaurants[restaurantNames.indexOf(dBinding.actvAddMealRestaurant.text.toString())]
        }


        dBinding.imgMealDialog.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        dialog = builder.create()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (FireStoreDatabase(applicationContext).getCurrentUserID() == Helpers.ADMIN_USER_ID) {
            menuInflater.inflate(R.menu.main_menu, menu)
        } else {
            menuInflater.inflate(R.menu.user_main_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val db = FireStoreDatabase(applicationContext)
        when (item.itemId) {
            R.id.memu_add_restaurant -> {
                // TODO(add restaurant)
            }
            R.id.memu_add_meal -> {
                val tvName = dBinding.txtNameDialog
                val tvDetails = dBinding.txtDetailsDialog
                val tvPrice = dBinding.txtPriceDialog
                val rbRate = dBinding.rbMealDialog
                val actvRestaurant = dBinding.actvAddMealRestaurant

                dialog.show()

                dialog.setOnDismissListener {
                    // Clear previous input
                    dBinding.txtNameDialog.text.clear()
                    dBinding.txtDetailsDialog.text.clear()
                    dBinding.txtPriceDialog.text.clear()
                    dBinding.rbMealDialog.rating = 0f
                    dBinding.imgMealDialog.setImageResource(R.drawable.ic_launcher_foreground)
                }

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val name = tvName.text.toString()
                    val details = tvDetails.text.toString()
                    val price = tvPrice.text.toString()
                    val rate = rbRate.rating

                    var isValid = true
                    if (imageUri == null) {
                        Toast.makeText(
                            this,
                            "Image is required!",
                            Toast.LENGTH_LONG
                        ).show()
                        isValid = false
                    }

                    if (name.isEmpty()) {
                        tvName.error = "Name is required!"
                        isValid = false
                    }

                    if (details.isEmpty()) {
                        tvDetails.error = "Details is required!"
                        isValid = false
                    }

                    if (price.isEmpty()) {
                        tvPrice.error = "Price is required!"
                        isValid = false
                    }

                    if (rate <= 0) {
                        Toast.makeText(
                            this,
                            "Rating is required!",
                            Toast.LENGTH_LONG
                        ).show()
                        isValid = false
                    }

                    if (selectedRestaurant == null) {
                        actvRestaurant.error = "Please choose a restaurant"
                        isValid = false
                    }

                    if (isValid) {
                        dialog.dismiss()
                        Helpers.showLoading(this)

                        db.uploadImage(
                            dBinding.imgMealDialog.drawable as BitmapDrawable,
                            Firebase.storage.reference.child("meal_images")
                        ).addOnSuccessListener {
                            Toast.makeText(
                                this, "Image upload successfully!", Toast.LENGTH_SHORT
                            ).show()

                            it.storage.downloadUrl.addOnSuccessListener { uri ->
                                db.insertMeal(
                                    Meal(
                                        "",
                                        name,
                                        uri.toString(),
                                        details,
                                        price.toDouble(),
                                        rate,
                                        selectedRestaurant!!.id,
                                    ), {
                                        Helpers.hideLoading()
                                        Toast.makeText(
                                            this, "Meal added successfully!", Toast.LENGTH_SHORT
                                        ).show()
                                    }, {
                                        Helpers.hideLoading()
                                        Toast.makeText(
                                            this,
                                            "Something went wrong! Please try again",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            }.addOnFailureListener {
                                Helpers.hideLoading()
                                Toast.makeText(
                                    this,
                                    "Something went wrong! Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
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
            dBinding.imgMealDialog.setImageURI(data.data)
        }
    }


}