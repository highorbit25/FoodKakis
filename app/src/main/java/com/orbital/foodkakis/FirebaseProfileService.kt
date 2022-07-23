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
            null
        }
    }

    suspend fun getMatches(): List<TinderContactCardModel> {
        val db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        // retrieve details of the request
        val docRef = db.collection("users").document(currentUserUid)
        return try {
            db.collection("users")
                .whereEqualTo("active_request", true)
                .whereEqualTo("selected_mode", getDoc(currentUserUid)!!.get("selected_mode") as String)
                .whereEqualTo("date", getDoc(currentUserUid)!!.get("date") as String)
                .whereEqualTo("timeslot", getDoc(currentUserUid)!!.get("timeslot") as String)
                .whereNotEqualTo("name", getDoc(currentUserUid)!!.get("name") as String)    // exclude self
                .get().await().documents.mapNotNull {
                    if (swiped(it.id)!!) {
                        null
                    } else { getProfileData(it.id) }}

        } catch (e: Exception) {
            Log.e("TinderContactCardModel", "Error getting list of matches", e)
            ArrayList<TinderContactCardModel>()
        }
    }



    suspend fun getDoc(userId: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(userId).get().await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun swiped(userId: String): Boolean? {
        val db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        return try {
            db.collection("users")
                .whereEqualTo("name", getDoc(currentUserUid)!!.get("name") as String)
                .whereArrayContains("swiped_on", userId).limit(1).get().await().documents.isNotEmpty()
        } catch (e: Exception) {
            null
        }
    }



}