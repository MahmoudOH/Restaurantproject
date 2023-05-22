package com.example.resturantproject.helpers

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.resturantproject.views.MainActivity
import com.example.resturantproject.views.MealsActivity


class Helpers {
    companion object {
        private var progress: Dialog? = null

        val ADMIN_USER_ID = "cMPdQwTWU3RblGuJDS76vbEneni1"

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

    }

}