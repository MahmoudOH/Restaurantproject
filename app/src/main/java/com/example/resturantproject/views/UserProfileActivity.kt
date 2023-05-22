package com.example.resturantproject.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.R
import com.example.resturantproject.databinding.ActivityUserProfileBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.User

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var user: User
    private var image: String? = null
    private var isViewing = true
    private val GALLERY_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FireStoreDatabase()
        val prefs = Prefs(this)

        Helpers.showLoading(this)
        db.getUserByEmail(prefs.emailPref!!, { user ->
            Helpers.hideLoading()

            image = user?.img
            if (image != null)
                binding.imgProfile.setImageURI(Uri.parse(image))
            binding.tvUsernameProfile.text = "@${user?.email}"

            // TODO: fill the data UI with this user
        }, { e ->
            Helpers.hideLoading()

        })



        binding.tvFullnameProfile.text = user.fullname ?: "No name"
//        binding.tvPhoneProfile.text = user.birthdate ?: "+xxx - xxx - xxx - xxxx"

        binding.btnEdit.setOnClickListener {
            Helpers.hideKeyboard(this)
            toggleVisibility()
        }

        binding.imgProfile.setOnClickListener {
            if (!isViewing) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, GALLERY_REQUEST_CODE)
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val fullName = binding.txtFullnameProfile.text.toString()
            val phone = binding.txtPhoneProfile.text.toString()
            val password = binding.txtPasswordProfile.text.toString()
            val confirmPassword = binding.txtPasswordConfirmProfile.text.toString()

            var isValid = true

            if (password.isEmpty()) {
                binding.txtPasswordProfile.error = "Password is required!"
                isValid = false
            } else if (password.length < 8) {
                binding.txtPasswordProfile.error = "Password must be at least 8 characters"
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                binding.txtPasswordConfirmProfile.error = "Please confirm your password!"
                isValid = false
            } else if (confirmPassword != password) {
                binding.txtPasswordConfirmProfile.error = "Passwords doesn't match"
                isValid = false
            }

//            if (isValid) {
//                if (db.updateUser(
//                        user.email,
//                        User(
//                            user.email,
//                            password,
//                            fullName,
//                            phone,
//                            image
//                        )
//                    )
//                ) {
//                    user = db.getUserByEmail(user.email)
//                    toggleVisibility()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "Something went wrong! Please try again",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }

        }

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

    private fun toggleVisibility() {
        if (isViewing) {
            // Hide textview
            binding.tvFullnameProfile.visibility = View.INVISIBLE
            binding.tvPhoneProfile.visibility = View.INVISIBLE

            // Show edit text
            binding.txtFullnameProfile.visibility = View.VISIBLE
            binding.txtPhoneProfile.visibility = View.VISIBLE
            binding.txtPasswordProfile.visibility = View.VISIBLE
            binding.txtPasswordConfirmProfile.visibility = View.VISIBLE
            binding.btnSaveProfile.visibility = View.VISIBLE

            binding.txtFullnameProfile.setText(user.fullname)
//            binding.txtPhoneProfile.setText(user.birthdate)
            binding.txtPasswordProfile.setText(user.password)
            binding.txtPasswordConfirmProfile.setText(user.password)

            binding.btnEdit.setImageResource(R.drawable.ic_close)
        } else {
            // Hide edit text
            binding.txtFullnameProfile.visibility = View.INVISIBLE
            binding.txtPhoneProfile.visibility = View.INVISIBLE
            binding.txtPasswordProfile.visibility = View.INVISIBLE
            binding.txtPasswordConfirmProfile.visibility = View.INVISIBLE
            binding.btnSaveProfile.visibility = View.INVISIBLE

            // Show textview
            binding.tvFullnameProfile.visibility = View.VISIBLE
            binding.tvPhoneProfile.visibility = View.VISIBLE

            binding.tvFullnameProfile.text = user.fullname
//            binding.tvPhoneProfile.text = user.birthdate

            binding.btnEdit.setImageResource(R.drawable.ic_edit)
        }
        isViewing = !isViewing
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            image = Helpers.getPath(contentResolver, data.data)
            binding.imgProfile.setImageURI(data.data)
        }
    }

}