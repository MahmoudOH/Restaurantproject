package com.example.resturantproject.views

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.R
import com.example.resturantproject.databinding.ActivityUserProfileBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FireStoreDatabase()
        val prefs = Prefs(this)

        val checkPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it)
                    Log.e("UserProfile.Location", "Please Enable Location Permission")
            }

        db.getUserByEmail(prefs.emailPref!!, {
            binding.progressBar.visibility = View.INVISIBLE

            if (Helpers.isLocationEnabled(applicationContext)) {
                checkPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)

                val userProfileFragment = UserProfileFragment()
                val bundle = Bundle().apply {
                    putParcelable("user", it)
                }
                userProfileFragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, userProfileFragment)
                    .addToBackStack(null)
                    .commit()
            }

        }, { e ->
            Log.d("Profile.getFile", e.toString())
            binding.progressBar.visibility = View.INVISIBLE
            AlertDialog.Builder(this)
                .setTitle("Can't get user")
                .setMessage("Unable to fetch user from the database. It may be because there's no internet connection.\nPlease try again later")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                    finish()
                }
                .show()
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}