package com.orbital.foodkakis

import android.graphics.Color
import android.util.Log
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObjects
import com.orbital.foodkakis.FirebaseProfileService.getProfileData
import com.orbital.foodkakis.TinderContactCardModel.Companion.toCard
import kotlinx.coroutines.tasks.await
private lateinit var mAuth: FirebaseAuth
object FirebaseProfileService {
    private const val TAG = "FirebaseProfileService"
    suspend fun getProfileData(userId: String): TinderContactCardModel? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(userId).get().await().toCard()
        } catch (e: Exception) {
//            Log.e(TAG, "Error getting user details", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user details")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun getMatches(): List<TinderContactCardModel> {
//    suspend fun getMatches(): ArrayList<TinderContactCardModel> {
        val db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        // retrieve details of the request
        val docRef = db.collection("users").document(currentUserUid)
//        var matchesArray = ArrayList<QueryDocumentSnapshot>()
//        docRef.get().addOnSuccessListener { doc -> matchesArray = doc.get("matchesArray") as ArrayList<QueryDocumentSnapshot> }
        return try {
//            db.collection("users").get().await().documents.mapNotNull { it.toCard() }

            db.collection("users")
                .whereEqualTo("selected_mode", getDoc(currentUserUid)!!.get("selected_mode") as String)
                .whereEqualTo("date", getDoc(currentUserUid)!!.get("date") as String)
                .whereEqualTo("timeslot", getDoc(currentUserUid)!!.get("timeslot") as String)
                .get().await().documents.mapNotNull { getProfileData(it.id) }
        } catch (e: Exception) {
            Log.e("TinderContactCardModel", "Error getting list of matches", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user friends")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            ArrayList<TinderContactCardModel>()
//            emptyList<TinderContactCardModel>()
        }
    }


    suspend fun getMatches(selectedMode: String?, date: String?, timeSlot: String?): List<TinderContactCardModel> {
//    suspend fun getMatches(): ArrayList<TinderContactCardModel> {
        val db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
        return try {
            db.collection(selectedMode.toString())
                        .document(date.toString())
                        .collection(timeSlot.toString())
                        .whereEqualTo("successful", false)
                        .get().await().documents.mapNotNull { getProfileData(it.id) }


//            db.collection("users").get().await().documents.mapNotNull { it.toCard() }
        } catch (e: Exception) {
            Log.e("TinderContactCardModel", "Error getting list of matches", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user friends")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            ArrayList<TinderContactCardModel>()
//            emptyList<TinderContactCardModel>()
        }
    }


    suspend fun getDoc(userId: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(userId).get().await()
        } catch (e: Exception) {
//            Log.e(TAG, "Error getting user details", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user details")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun getMode(userId: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(userId).get().await()
        } catch (e: Exception) {
//            Log.e(TAG, "Error getting user details", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user details")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun getDate(userId: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(userId).get().await()
        } catch (e: Exception) {
//            Log.e(TAG, "Error getting user details", e)
//            FirebaseCrashlytics.getInstance().log("Error getting user details")
//            FirebaseCrashlytics.getInstance().setCustomKey("user id", xpertSlug)
//            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }


}