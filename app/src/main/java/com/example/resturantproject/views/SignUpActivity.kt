package com.example.resturantproject.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.databinding.ActivitySignUpBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Helpers.Companion.GALLERY_REQUEST_CODE
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.User
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.Date


class SignUpActivity : AppCompatActivity() {

    private lateinit var txtBirthDate: EditText
    private lateinit var binding: ActivitySignUpBinding
    private var imageUri: Uri? = null
    private var location: GeoPoint? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val checkPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it)
                    Log.e("UserProfile.Location", "Please Enable Location Permission")
            }

        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("user_images")

        val db = FireStoreDatabase()
        val prefs = Prefs(this)

        txtBirthDate = binding.txtBirthdateSignup

        binding.txtBirthdateSignup.setOnClickListener {
            Helpers.showDatePickerDialog(this, binding.txtBirthdateSignup)
            Helpers.hideKeyboard(this)
        }

        binding.txtLocationSignup.setOnTouchListener { view, e ->
            if (e.action == MotionEvent.ACTION_DOWN) {
                binding.pBSignup.visibility = View.VISIBLE
                if (Helpers.isLocationEnabled(applicationContext)) {
                    checkPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    Helpers.getLastLocation(this, {
                        binding.pBSignup.visibility = View.INVISIBLE
                        location = GeoPoint(it.latitude, it.longitude)
                    }, {
                        binding.pBSignup.visibility = View.INVISIBLE
                        Log.e("getLastLocation", it.toString())
                    })
                    binding.txtLocationSignup.setText("Lat: ${location?.latitude}, Long: ${location?.longitude}")
                }
                true
            } else {
                false
            }
        }

        binding.imageSignup.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        GALLERY_REQUEST_CODE
                    )
                } else {
                    Helpers.openGallery(this)
                }
            } else {
                Helpers.openGallery(this)
            }
        }

        binding.btnSignup.setOnClickListener {
            val fullName = binding.txtFullnameSignup.text.toString()
            val email = binding.txtEmailSignup.text.toString()
            val birthdate = binding.txtBirthdateSignup.text.toString()
            val password = binding.txtPasswordSignup.text.toString()

            var isValid = true

            if (fullName.isBlank()) {
                binding.txtFullnameSignup.error = "Full name is required!"
                isValid = false
            } else if (fullName.split("\\s+".toRegex()).size < 2) {
                binding.txtFullnameSignup.error = "Full name must consist of at least two words!"
                isValid = false
            }

            if (email.isBlank()) {
                binding.txtEmailSignup.error = "Email is required!"
                isValid = false
            }

            if (password.isBlank()) {
                binding.txtPasswordSignup.error = "Password is required!"
                isValid = false
            } else if (password.length < 8) {
                binding.txtPasswordSignup.error = "Password must be at least 8 characters"
                isValid = false
            }

            if (birthdate.isBlank()) {
                binding.txtBirthdateSignup.error = "Birthdate is required!"
                isValid = false
            } else if (!birthdate.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}".toRegex())) {
                binding.txtBirthdateSignup.error =
                    "Birthdate must match this format: ${Helpers.DATE_FORMAT}"
                isValid = false
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show()
                isValid = false
            }

            if (isValid) {
                Helpers.showLoading(this)

                // Upload to firebase
                val bitmap = (binding.imageSignup.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val mountainsRef = imagesRef.child("${System.currentTimeMillis()}.jpg")
                val uploadTask = mountainsRef.putBytes(data)

                Helpers.showLoading(this)
                db.emailExists(email, { exists ->
                    if (exists) {
                        Helpers.hideLoading()
                        binding.txtEmailSignup.error = "Email is already used! Try another"
                    } else {
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            Toast.makeText(
                                this, "Image upload successfully!", Toast.LENGTH_SHORT
                            ).show()

                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                                db.insertUser(User(
                                    "",
                                    email,
                                    password,
                                    fullName,
                                    Date(birthdate),
                                    GeoPoint(
                                        location?.latitude ?: 0.0,
                                        location?.longitude ?: 0.0
                                    ),
                                    uri.toString()
                                ), {
                                    Helpers.hideLoading()
                                    prefs.emailPref = email
                                    finish()
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
                        }.addOnFailureListener {
                            Helpers.hideLoading()
                            Toast.makeText(
                                this,
                                "Upload Error! Couldn't upload profile image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }, {
                    Helpers.hideLoading()
                    Toast.makeText(
                        this, "Something went wrong! Please try again", Toast.LENGTH_SHORT
                    ).show()
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            binding.imageSignup.setImageURI(data.data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // TODO(handle location permission result)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Helpers.openGallery(this)
                } else {
                    finish()
                }
            }
        }
    }


}
