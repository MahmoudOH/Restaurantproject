package com.example.resturantproject.db

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.Restaurant
import com.example.resturantproject.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class FireStoreDatabase(appContext: Context) {
    val USERS_COLLECTION_NAME = "users"
    val MEALS_COLLECTION_NAME = "meals"
    val RESTAURANTS_COLLECTION_NAME = "restaurants"

    init {
        FirebaseApp.initializeApp(appContext)
    }

    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection(USERS_COLLECTION_NAME)
    private val mealsCollection = db.collection(MEALS_COLLECTION_NAME)
    private val restaurantsCollection = db.collection(RESTAURANTS_COLLECTION_NAME)

    private fun insertAuthUser(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUserID(): String? {
        val currentUser = auth.currentUser
        return currentUser?.uid
    }

    fun insertUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        insertAuthUser(user.email, user.password)
            .addOnSuccessListener {
                user.id = it.user?.uid.toString()
                Log.d("DB.insertUser", "User inserted successfully to Auth! $it")
                usersCollection.add(user)
                    .addOnSuccessListener {
                        Log.d("DB.insertUser", "User inserted successfully to Firestore! $it")
                        onSuccess.invoke()
                    }
                    .addOnFailureListener {
                        Log.d("DB.insertUser", "Failed to insert new user to Firestore! $it")
                        onFailure.invoke(it)
                    }
            }
            .addOnFailureListener { e ->
                Log.d("DB.insertUser", "Failed to insert new user to Auth!\n${e}")
            }
    }


    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        val authUser = auth.currentUser

        // Update email first
        authUser!!.updateEmail(user.email)
            .addOnCompleteListener { emailUpdateTask ->
                if (emailUpdateTask.isSuccessful) {
                    if (user.password.isNotBlank()) {
                        // Update password
                        authUser.updatePassword(user.password)

                        usersCollection.document(user.id).set(user)
                            .addOnSuccessListener {
                                Log.d(
                                    "DB.updateUser",
                                    "User inserted successfully to Firestore! $it"
                                )
                                onSuccess.invoke()
                            }
                            .addOnFailureListener {
                                Log.d(
                                    "DB.updateUser",
                                    "Failed to insert new user to Firestore! $it"
                                )
                                onFailure.invoke(it)
                            }
                    }
                } else {
                    onFailure.invoke(null)
                }
            }.addOnFailureListener {
                Log.d("DB.updateUser", "Failed to insert new user to Firestore! $it")
                onFailure.invoke(it)
            }
    }

    fun emailExists(email: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        return getUserByEmail(email, {
            Log.d("DB.emailExists", "email: $email found in Firestore! $it}")
            onSuccess.invoke(it != null)
        }, {
            Log.d("DB.getUserByEmail", "User Found in Firestore! $it")
            onFailure.invoke(it)
        })
    }

    fun getUserByEmail(email: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.whereEqualTo("email", email).get().addOnSuccessListener { querySnapshot ->
            var user: User? = null

            for (document in querySnapshot) {
                user = document.toObject()
            }
            Log.d("DB.getUserByEmail", "User Found in Firestore!$user")
            onSuccess.invoke(user)
        }.addOnFailureListener { e ->
            Log.d("DB.getUserByEmail", "Failed to get user from Firestore!\n${e}")
            onFailure.invoke(e)
        }
    }

    fun getUserById(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(userId).get().addOnSuccessListener { document ->
            Log.d("DB.getUserById", "User Found in Firestore!")
            onSuccess.invoke(document.toObject())
        }.addOnFailureListener { e ->
            Log.d("DB.getUserById", "Failed to get user from Firestore!\n${e}")
            onFailure.invoke(e)
        }
    }

    fun getCurrentUser(onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = getCurrentUserID()
        if (userId == null) {
            Log.d("DB.getCurrentUser", "Failed to get user from FireAuth! userId=$userId")
            onFailure.invoke(Exception("Failed to get user from FireAuth! userId=$userId"))
        } else {
            usersCollection.document(userId).get().addOnSuccessListener { document ->
                Log.d("DB.getCurrentUser", "User Found in Firestore!")
                val user = document.toObject<User>()
                if (user != null) {
                    onSuccess.invoke(user)
                } else {
                    onFailure.invoke(Exception("Failed to get user from FireAuth! user=$user"))
                }
            }.addOnFailureListener { e ->
                Log.d("DB.getCurrentUser", "Failed to get user from Firestore!\n${e}")
                onFailure.invoke(e)
            }
        }
    }


//    fun deleteMeal(meal: Meal): Boolean {
//        return db.delete(Meal.TABLE_NAME, "${Meal.COL_ID} = '${meal.id}'", null) > 0
//    }

    fun insertMeal(meal: Meal, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        mealsCollection.add(meal)
            .addOnSuccessListener {
                Log.d("DB.insertMeal", "Meal inserted successfully to Firestore! $it")
                onSuccess.invoke()
            }
            .addOnFailureListener {
                Log.d("DB.insertMeal", "Failed to insert new meal to Firestore! $it")
                onFailure.invoke(it)
            }
    }

    fun updateMeal(meal: Meal, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        mealsCollection.document(meal.id).set(meal)
            .addOnSuccessListener {
                Log.d(
                    "DB.updateUser",
                    "User inserted successfully to Firestore! $it"
                )
                onSuccess.invoke()
            }
            .addOnFailureListener {
                Log.d(
                    "DB.updateUser",
                    "Failed to insert new user to Firestore! $it"
                )
                onFailure.invoke(it)
            }

    }

    fun getAllMeals(): Task<QuerySnapshot> {
        return mealsCollection.get()
    }

    fun getRestaurantMeals(restaurant: Restaurant): Task<QuerySnapshot> {
        return mealsCollection.whereEqualTo(
            "restaurant",
            db.document("$RESTAURANTS_COLLECTION_NAME/${restaurant.id}")
        ).get()
    }

    fun getAllRestaurants(): Task<QuerySnapshot> {
        return restaurantsCollection.get()
    }

//    fun addToFavourite(username: String, mealId: Long): Boolean {
//        val cv = ContentValues()
//        cv.put(UserMeal.COL_USERNAME, username)
//        cv.put(UserMeal.COL_MEAL_ID, mealId)
//        return db.insert(UserMeal.TABLE_NAME, null, cv) > 0
//    }
//
//    fun removeFromFavourite(username: String, mealId: Long): Boolean {
//        return db.delete(
//            UserMeal.TABLE_NAME,
//            "${UserMeal.COL_USERNAME} = '$username' AND ${UserMeal.COL_MEAL_ID} = '$mealId'",
//            null
//        ) > 0
//    }

    fun getAllFavouriteMeals(username: String): ArrayList<Meal> {
        val data = ArrayList<Meal>()

        return data
    }

    fun uploadImage(drawable: BitmapDrawable, imagesRef: StorageReference): UploadTask {
        // Upload to firebase storage
        val bitmap = drawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val mountainsRef = imagesRef.child("${System.currentTimeMillis()}.jpg")
        return mountainsRef.putBytes(data)
    }


//    fun getMealById(mealId: Long): Meal {
//        val c =
//            db.rawQuery(
//                "SELECT * FROM ${Meal.TABLE_NAME} WHERE ${Meal.COL_ID} = '$mealId'",
//                null
//            )
//        c.moveToFirst()
//        val res = Meal(
//            c.getLong(0),
//            c.getString(1),
//            c.getString(2),
//            c.getString(3),
//            c.getString(4),
//            c.getDouble(5),
//            c.getFloat(6),
//            c.getString(7),
//        )
//        c.close()
//        return res
//    }
//
//    fun updateMeal(meal: Meal): Boolean {
//        val cv = ContentValues()
//        cv.put(Meal.COL_NAME, meal.name)
//        cv.put(Meal.COL_IMG, meal.image)
//        cv.put(Meal.COL_INGREDIENTS, meal.ingredients)
//        cv.put(Meal.COL_ORIGIN, meal.origin)
//        cv.put(Meal.COL_PRICE, meal.price)
//        cv.put(Meal.COL_RATE, meal.rate)
//        cv.put(Meal.COL_CATEGORY, meal.category)
//        return db.update(Meal.TABLE_NAME, cv, "${Meal.COL_ID} = '${meal.id}'", null) > 0
//
//    }

}