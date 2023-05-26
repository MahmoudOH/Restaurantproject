package com.example.resturantproject.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.location.LocationManagerCompat
import com.example.resturantproject.views.MainActivity
import com.example.resturantproject.views.MealsActivity
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Helpers {
    companion object {
        private var progress: Dialog? = null

        val GALLERY_REQUEST_CODE = 100
        val ADMIN_USER_ID = "cMPdQwTWU3RblGuJDS76vbEneni1"
        val DATE_FORMAT = "dd/MM/yyyy"

        fun getFormattedDate(date: Date?): String? {
            return date?.let {
                SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(
                    it
                )
            }
        }

        fun getUserActivity(isAdmin: Boolean): Class<out AppCompatActivity> {
            // TODO: change activities
            return if (isAdmin) MainActivity::class.java else MealsActivity::class.java
        }

        fun getPath(contentResolver: ContentResolver, uri: Uri?): String? {
            if (uri == null) {
                return null
            }
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
            val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()

            return if (columnIndex == null) null else cursor.getString(columnIndex)
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view: View? = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }

        fun showLoading(context: Context) {
            hideLoading() // Make sure to hide any existing loading dialog before showing a new one

            progress = ProgressDialog(context)
            progress?.setCancelable(false); // disable dismiss by tapping outside of the dialog

            progress?.show()
        }

        fun hideLoading() {
            progress?.dismiss()
            progress = null
        }

        fun showDatePickerDialog(context: Context, view: EditText) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    // Do something with the selected date
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    // Format the selected date as desired
                    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)

                    // Update the UI or perform other actions with the formatted date
                    view.setText(formattedDate)
                }, year, month, day
            )
            datePickerDialog.show()
        }

        fun openGallery(activity: Activity) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        @SuppressLint("MissingPermission")
        fun getLastLocation(
            activity: Activity, onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit
        ) {
            val locationProvider =
                LocationServices.getFusedLocationProviderClient(activity.applicationContext)
            locationProvider.lastLocation.addOnSuccessListener {
                    onSuccess.invoke(it)
                }.addOnFailureListener {
                    onFailure.invoke(it)
                }
        }

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return LocationManagerCompat.isLocationEnabled(locationManager)
        }
    }


}