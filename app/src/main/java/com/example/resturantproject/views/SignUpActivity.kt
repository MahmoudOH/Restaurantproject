package com.example.resturantproject.views

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.databinding.ActivitySignUpBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var txtBirthDate: EditText
    private lateinit var calendar: Calendar
    private lateinit var binding: ActivitySignUpBinding
    private var imageUri: Uri? = null
    private var imageId: String? = null
    private val GALLERY_REQUEST_CODE = 100
    private val DATE_FORMAT = "dd/MM/yyyy"
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()
        storage = Firebase.storage

        val db = FireStoreDatabase()
        val prefs = Prefs(this)

        txtBirthDate = binding.txtBirthdateSignup

        binding.txtBirthdateSignup.setOnClickListener {
            showDatePickerDialog()
            Helpers.hideKeyboard(this)
        }

        binding.imageSignup.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        GALLERY_REQUEST_CODE
                    )
                } else {
                    openGallery()
                }
            } else {
                openGallery()
            }
        }

        binding.btnSignup.setOnClickListener {
            // Start loading
            val fullName = binding.txtFullnameSignup.text.toString()
            val email = binding.txtEmailSignup.text.toString()
            val birthdate = binding.txtBirthdateSignup.text.toString()
            val password = binding.txtPasswordSignup.text.toString()
            val location = binding.txtLocationSignup.text.toString()

            var isValid = true

            if (fullName.isEmpty()) {
                binding.txtFullnameSignup.error = "Full name is required!"
                isValid = false
            } else if (fullName.split("\\s+".toRegex()).size < 2) {
                binding.txtFullnameSignup.error = "Full name must consist of at least two words!"
                isValid = false
            }

            if (email.isEmpty()) {
                binding.txtEmailSignup.error = "Email is required!"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.txtPasswordSignup.error = "Password is required!"
                isValid = false
            } else if (password.length < 8) {
                binding.txtPasswordSignup.error = "Password must be at least 8 characters"
                isValid = false
            }

            if (birthdate.isEmpty()) {
                binding.txtBirthdateSignup.error = "Birthdate is required!"
                isValid = false
            } else if (!birthdate.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}".toRegex())) {
                binding.txtBirthdateSignup.error =
                    "Birthdate must match this format: ${DATE_FORMAT}"
                isValid = false
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show()
                isValid = false
            }

            if (isValid) {
                Helpers.showLoading(this)
                db.emailExists(email, { exsists ->
                    if (exsists) {
                        Helpers.hideLoading()
                        binding.txtEmailSignup.error = "Email is already used! Try another"

                        Toast.makeText(
                            this, "Email is already used! Try another", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        db.insertUser(
                            User(
                                "",
                                email,
                                password,
                                fullName,
                                Date(birthdate),
                                imageId
                            ),
                            {
                                Log.d("Signup.ClickListener", "User inserted successfully!")
                                Helpers.hideLoading()
                                prefs.emailPref = email
                                finish()
                            },
                            { e ->
                                Log.d("Signup.ClickListener", "Failed to insert user!\n${e}")
                                Helpers.hideLoading()
                                Toast.makeText(
                                    this,
                                    "Something went wrong! Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }, { e ->
                    Log.d("db.emailExists", "Failed!\n${e}")
                    Helpers.hideLoading()
                    Toast.makeText(
                        this, "Something went wrong! Please try again", Toast.LENGTH_SHORT
                    ).show()
                })
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            binding.imageSignup.setImageURI(data.data)


            // TODO: NOT IMPORTANT only upload when sign up
            val storageRef = storage.reference
            val imagesRef = storageRef.child("user_images")
            // Upload to firebase
            val bitmap = (binding.imageSignup.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imageId = System.currentTimeMillis().toString()
            val mountainsRef = imagesRef.child("${imageId}.jpg")
            val uploadTask = mountainsRef.putBytes(data)
            Helpers.showLoading(this)

            uploadTask.addOnFailureListener {
                Helpers.hideLoading()
                Toast.makeText(
                    this, "Upload Error! Couldn't update profile image", Toast.LENGTH_SHORT
                ).show()
            }.addOnSuccessListener { taskSnapshot ->
                Helpers.hideLoading()
                Toast.makeText(
                    this, "Image upload successfully!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    finish()
                }
            }
        }

    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Do something with the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                // Format the selected date as desired
                val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                // Update the UI or perform other actions with the formatted date
                txtBirthDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

}
