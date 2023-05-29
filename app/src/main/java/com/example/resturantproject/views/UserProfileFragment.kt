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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso


class UserProfileFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var user: User
    private lateinit var mMap: GoogleMap

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

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.user_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (user.img?.isNotBlank() == true)
            Picasso.get().load(Uri.parse(user.img)).into(binding.imgProfile)

        binding.tvEmailProfile.text = user.email
        binding.tvFullnameProfile.text = user.fullname
        binding.tvBirthdateProfile.text = Helpers.getFormattedDate(user.birthdate)

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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val location = LatLng(user.location?.latitude ?: 0.0, user.location?.longitude ?: 0.0)
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.addMarker(MarkerOptions().position(location).title("${user.fullname} location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }

}