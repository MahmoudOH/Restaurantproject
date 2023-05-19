package com.example.resturantproject.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.databinding.ActivitySignInBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

class SignInActivity : AppCompatActivity() {

    val ADMIN_USER_ID = "cMPdQwTWU3RblGuJDS76vbEneni1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = Prefs(this)
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()

        val db = FireStoreDatabase()

        if (prefs.emailPref != null) {
            val user = db.getUserByEmail(prefs.emailPref!!)
            Log.d("ASDFASDFASDF", user.toString())
            if (user != null) {
                startActivityForUser(user.id == ADMIN_USER_ID)
            }
        }

        binding.toSignUp.setOnClickListener {
            val user = db.getUserById(ADMIN_USER_ID)
            if (user == null) {
                Toast.makeText(
                    this, "Network Error!", Toast.LENGTH_SHORT
                ).show()
            } else {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.txtEmailSignin.text.toString()
            val password = binding.txtPasswordSignin.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please Fill The Fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        prefs.emailPref = email

                        startActivityForUser(task.result.user?.uid == ADMIN_USER_ID)
                    } else {
                        if (task.exception.toString().contains("FirebaseNetworkException")) {
                            Toast.makeText(
                                this, "Network Error!", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this, "Username or Password is incorrect", Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }
        }
    }

    private fun startActivityForUser(isAdmin: Boolean = false) {
        // TODO: change activities
        if (isAdmin) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, MealsActivity::class.java))
        }
        finish()
    }
}
