package com.example.resturantproject.views

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.databinding.ActivitySignInBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Prefs
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.Help

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = Prefs(this)
        val db = FireStoreDatabase()
        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()

        binding.toSignUp.setOnClickListener {
            Helpers.showLoading(this)

            db.getUserById(Helpers.ADMIN_USER_ID, { user ->
                Helpers.hideLoading()
                if (user == null) {
                    Toast.makeText(
                        this, "Network Error!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
            }, {
                Helpers.hideLoading()
                Toast.makeText(
                    this, "Network Error!", Toast.LENGTH_SHORT
                ).show()
            })
        }

        binding.btnSignIn.setOnClickListener {
            Helpers.showLoading(this)

            val email = binding.txtEmailSignin.text.toString()
            val password = binding.txtPasswordSignin.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Helpers.hideLoading()
                Toast.makeText(this, "Please Fill The Fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    Helpers.hideLoading()
                    if (task.isSuccessful) {
                        prefs.emailPref = email

                        startActivity(Intent(this, Helpers.getUserActivity(task.result.user?.uid == Helpers.ADMIN_USER_ID)))
                        finish()
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


}
