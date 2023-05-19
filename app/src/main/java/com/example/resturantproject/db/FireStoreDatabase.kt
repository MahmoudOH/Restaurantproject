package com.example.resturantproject.db

import android.util.Log
import com.example.resturantproject.model.Meal
import com.example.resturantproject.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FireStoreDatabase {
    val USERS_COLLECTION_NAME = "users"
    val MEALS_COLLECTION_NAME = "meals"
    val RESTAURANTS_COLLECTION_NAME = "restaurants"

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection(USERS_COLLECTION_NAME)
    private val mealsCollection = db.collection(MEALS_COLLECTION_NAME)
    private val restaurantsCollection = db.collection(RESTAURANTS_COLLECTION_NAME)


    fun insertUser(user: User): Boolean {
        var inserted = false

        usersCollection
            .add(user)
            .addOnSuccessListener {
                inserted = true
                Log.d("DB.insertUser", "User inserted successfully!")
            }.addOnFailureListener {
                Log.d("DB.insertUser", "Failed to insert user!")
            }
        return inserted
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

    fun userExists(email: String): Boolean {
        val user = getUserByEmail(email, { user: User? ->
            
        })
        return user != null
    }

    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        usersCollection.whereEqualTo("email", email).get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                Log.d("ASDFASDFASDF", document.toString())
                callback(document.toObject())
            }
        }.addOnFailureListener { e ->
            callback(null)
        }
    }

    fun getUserById(userId: String): User? {
        var user: User? = null

        usersCollection.document(userId).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                user = document.toObject<User>()
            }
        }
        return user
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