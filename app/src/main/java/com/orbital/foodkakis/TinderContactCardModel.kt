package com.orbital.foodkakis

import android.graphics.Color
import android.os.Parcelable
import android.util.Log
import androidx.annotation.ColorInt
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.tasks.await

@Parcelize
data class TinderContactCardModel(
    val name: String,
    val age: Long,
    val description: String,
    @ColorInt val backgroundColor: Int) : Parcelable {


    companion object {
        fun DocumentSnapshot.toCard(): TinderContactCardModel? {
            try {
                val name = getString("name")!!
                //            val imageUrl = getString("profile_image")!!
                val age = get("age") as Long
                val description = getString("description")!!
                return TinderContactCardModel(name, age, description, Color.parseColor("#c60055"))
            } catch (e: Exception) {
                Log.e(TAG, "Error converting user profile", e)
                //            FirebaseCrashlytics.getInstance().log("Error converting user profile")
                //            FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                //            FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }

        private const val TAG = "TinderContactCardModel"
    }



}
