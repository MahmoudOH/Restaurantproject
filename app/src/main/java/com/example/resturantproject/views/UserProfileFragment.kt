package com.example.resturantproject.views

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.resturantproject.R
import com.example.resturantproject.databinding.FragmentUserProfileBinding
import com.example.resturantproject.helpers.Helpers
import com.example.resturantproject.model.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso


class UserProfileFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable("user") ?: User()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (user.img?.isNotBlank() == true)
            Picasso.get().load(Uri.parse(user.img)).into(binding.imgProfile)

        binding.tvEmailProfile.text = user.email
        binding.tvFullnameProfile.text = user.fullname
        binding.tvBirthdateProfile.text = Helpers.getFormattedDate(user.birthdate)

        binding.userMap.onCreate(savedInstanceState)
        binding.userMap.getMapAsync(this)

        binding.btnEditProfile.setOnClickListener {
            val editProfileFragment = EditProfileFragment()
            val bundle = Bundle().apply {
                putParcelable("user", user)
            }
            editProfileFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editProfileFragment)
                .addToBackStack(null)
                .commit()
        }

    }

    override fun onMapReady(map: GoogleMap) {
        val location = LatLng(user.location?.latitude ?: 0.0, user.location?.longitude ?: 0.0)
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.addMarker(MarkerOptions().position(location))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }

}