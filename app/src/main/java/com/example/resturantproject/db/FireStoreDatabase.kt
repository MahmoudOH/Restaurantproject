package com.example.resturantproject.db

import android.util.Log
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FireStoreDatabase {
    val USERS_COLLECTION_NAME = "users"
    val MEALS_COLLECTION_NAME = "meals"
    val RESTAURANTS_COLLECTION_NAME = "restaurants"

    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection(USERS_COLLECTION_NAME)
    private val mealsCollection = db.collection(MEALS_COLLECTION_NAME)
    private val restaurantsCollection = db.collection(RESTAURANTS_COLLECTION_NAME)

    private fun insertAuthUser(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun insertUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        insertAuthUser(user.email, user.password)
            .addOnSuccessListener {
                Log.d("DB.insertUser", "User inserted successfully to Auth!\n${it}")
                usersCollection.add(user)
                    .addOnSuccessListener {
                        Log.d("DB.insertUser", "User inserted successfully to Firestore!\n${it}")
                        onSuccess.invoke()
                    }
                    .addOnFailureListener {
                        Log.d("DB.insertUser", "Failed to insert new user to Firestore!\n${it}")
                        onFailure.invoke(it)
                    }
            }
            .addOnFailureListener { e ->
                Log.d("DB.insertUser", "Failed to insert new user to Auth!\n${e}")
            }
    }

//    fun updateUser(username: String, user: User): Boolean {
//        val cv = ContentValues()
//        cv.put(User.COL_USERNAME, user.username)
//        cv.put(User.COL_PASSWORD, user.password)
//        cv.put(User.COL_FULLNAME, user.fullname)
//        cv.put(User.COL_PHONE_NUMBER, user.phoneNumber)
//        cv.put(User.COL_IMG, user.img)
//        return db.update(User.TABLE_NAME, cv, "${User.COL_USERNAME} = '$username'", null) > 0
//    }

    fun emailExists(email: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        return getUserByEmail(email, { user ->
            Log.d("DB.emailExists", "email found in Firestore!\n${user}")
            onSuccess.invoke(user != null)
        }, { e ->
            Log.d("DB.getUserByEmail", "User Found in Firestore!\n${e}")
            onFailure.invoke(e)
        })
    }

    fun getUserByEmail(email: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.whereEqualTo("email", email).get().addOnSuccessListener { querySnapshot ->
            Log.d("DB.getUserByEmail", "User Found in Firestore!")

            for (document in querySnapshot) {
                onSuccess.invoke(document.toObject())
            }
            onSuccess.invoke(null)
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

//    fun insertMeal(meal: Meal): Long {
//        val cv = ContentValues()
//        cv.put(Meal.COL_NAME, meal.name)
//        cv.put(Meal.COL_IMG, meal.image)
//        cv.put(Meal.COL_INGREDIENTS, meal.ingredients)
//        cv.put(Meal.COL_ORIGIN, meal.origin)
//        cv.put(Meal.COL_PRICE, meal.price)
//        cv.put(Meal.COL_RATE, meal.rate)
//        cv.put(Meal.COL_CATEGORY, meal.category)
//        return db.insert(Meal.TABLE_NAME, null, cv)
//    }
//
//
//    fun deleteMeal(meal: Meal): Boolean {
//        return db.delete(Meal.TABLE_NAME, "${Meal.COL_ID} = '${meal.id}'", null) > 0
//    }

    fun getAllMeals(): ArrayList<Meal> {
        val data = ArrayList<Meal>()


        return data
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