package com.example.resturantproject.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.resturantproject.databinding.FragmentEditProfileBinding
import com.example.resturantproject.db.FireStoreDatabase
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.helpers.Helpers.Companion.GALLERY_REQUEST_CODE
import com.example.resturantproject.helpers.Prefs
import com.example.resturantproject.model.User
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.util.Date


class EditProfileFragment : Fragment() {

    private lateinit var user: User
    private lateinit var binding: FragmentEditProfileBinding
    private var location: GeoPoint? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable("user") ?: User()
            location = user.location
            imageUri = Uri.parse(user.img)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FireStoreDatabase()
        val prefs = Prefs(requireContext())
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("user_images")

        if (user.img?.isNotBlank() == true)
            Picasso.get().load(Uri.parse(user.img)).into(binding.imgEditProfile)

        binding.txtEmailEditProfile.setText(user.email)
        binding.txtFullnameEditProfile.setText(user.fullname)
        binding.txtBirthdateEditProfile.setText(Helpers.getFormattedDate(user.birthdate))
        binding.txtLocationEditProfile.setText(user.locationString())

        binding.txtLocationEditProfile.setOnTouchListener { view, e ->
            if (e.action == MotionEvent.ACTION_DOWN) {
                binding.pBEditProfile.visibility = View.VISIBLE
                Helpers.getLastLocation(requireActivity(), {
                    binding.pBEditProfile.visibility = View.INVISIBLE
                    location = GeoPoint(it.latitude, it.longitude)
                }, {
                    binding.pBEditProfile.visibility = View.INVISIBLE
                    Log.e("getLastLocation", it.toString())
                })
                binding.txtLocationEditProfile.setText("Lat: ${location?.latitude}, Long: ${location?.longitude}")
                true
            } else {
                false
            }
        }

        binding.imgEditProfile.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    val permissionLauncher = registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            openGallery()
                        }
                    }

                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    openGallery()
                }
            } else {
                openGallery()
            }
        }

        binding.txtBirthdateEditProfile.setOnClickListener {
            Helpers.showDatePickerDialog(requireContext(), binding.txtBirthdateEditProfile)
            Helpers.hideKeyboard(requireActivity())
        }

        binding.btnSaveEditProfile.setOnClickListener {
            val fullName = binding.txtFullnameEditProfile.text.toString()
            var password = binding.txtPasswordEditProfile.text.toString()
            val email = binding.txtEmailEditProfile.text.toString()
            val birthdate = binding.txtBirthdateEditProfile.text.toString()

            var isValid = true

            if (fullName.isBlank()) {
                binding.txtFullnameEditProfile.error = "Full name is required!"
                isValid = false
            } else if (fullName.split("\\s+".toRegex()).size < 2) {
                binding.txtFullnameEditProfile.error =
                    "Full name must consist of at least two words!"
                isValid = false
            }

            if (email.isBlank()) {
                binding.txtEmailEditProfile.error = "Email is required!"
                isValid = false
            }

            if (password.isBlank()) {
                password = user.password
            } else if (password.length < 8) {
                binding.txtPasswordEditProfile.error = "Password must be at least 8 characters"
                isValid = false
            }

            if (birthdate.isBlank()) {
                binding.txtBirthdateEditProfile.error = "Birthdate is required!"
                isValid = false
            } else if (!birthdate.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}".toRegex())) {
                binding.txtBirthdateEditProfile.error =
                    "Birthdate must match this format: ${Helpers.DATE_FORMAT}"
                isValid = false
            }

            if (imageUri.toString().isBlank()) {
                Toast.makeText(context, "Please select an image", Toast.LENGTH_LONG).show()
                isValid = false
            }

            if (isValid) {
                Helpers.showLoading(requireContext())

                // Upload to firebase
                val bitmap = (binding.imgEditProfile.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val mountainsRef = imagesRef.child("${System.currentTimeMillis()}.jpg")
                val uploadTask = mountainsRef.putBytes(data)

                Helpers.showLoading(requireContext())
                db.emailExists(email, { exists ->
                    if (user.email != email && exists) {
                        Helpers.hideLoading()
                        binding.txtEmailEditProfile.error = "Email is already used! Try another"
                    } else {
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            Toast.makeText(
                                context, "Image upload successfully!", Toast.LENGTH_SHORT
                            ).show()

                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                                val user = User(
                                    this.user.id,
                                    email,
                                    password,
                                    fullName,
                                    Date(birthdate),
                                    GeoPoint(
                                        location?.latitude ?: 0.0,
                                        location?.longitude ?: 0.0
                                    ),
                                    uri.toString()
                                )
                                db.updateUser(user, {
                                    Helpers.hideLoading()
                                    prefs.emailPref = email
                                    this.user = user
                                    requireActivity().finish()
                                }, {
                                    Helpers.hideLoading()
                                    Toast.makeText(
                                        context,
                                        "Something went wrong! Please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }.addOnFailureListener {
                                Helpers.hideLoading()
                                Toast.makeText(
                                    context,
                                    "Something went wrong! Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener {
                            Helpers.hideLoading()
                            Toast.makeText(
                                context,
                                "Upload Error! Couldn't upload profile image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }, {
                    Helpers.hideLoading()
                    Toast.makeText(
                        context, "Something went wrong! Please try again", Toast.LENGTH_SHORT
                    ).show()
                })

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ADSFASDF.data", data?.data.toString())
        Log.d("ADSFASDF.requestCode", requestCode.toString())
        Log.d("ADSFASDF.resultCode", resultCode.toString())

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Log.d("ADSFASDF", data.data.toString())
            imageUri = data.data
            binding.imgEditProfile.setImageURI(data.data)
        }
    }

    private  fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
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
                    Toast.makeText(
                        context,
                        "Permission required to access photos!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}