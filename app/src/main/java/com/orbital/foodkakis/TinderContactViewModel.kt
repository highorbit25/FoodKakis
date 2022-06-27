package com.orbital.foodkakis

import android.content.Intent.getIntent
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//class TinderContactViewModel(private val dataArray: ArrayList<TinderContactCardModel>): ViewModel() {
class TinderContactViewModel(): ViewModel() {

    val db = Firebase.firestore

    private val stream = MutableLiveData<TinderContactModel>()

    val modelStream: LiveData<TinderContactModel>
        get() = stream

//    private val data = dataArray
//    private val data = listOf(
//    private val data = getIntent().getSerializableExtra("key") as ArrayList<TinderContactCardModel?>?
    private val data = arrayListOf<TinderContactCardModel>(
        TinderContactCardModel(
            name = "Alice Tan", age = 22, description = "I love prata and everything curry related!", backgroundColor = Color.parseColor("#c50e29")
        ),
        TinderContactCardModel(
            name = "Timothy Lee", age = 21, description = "Food is life", backgroundColor = Color.parseColor("#c60055")
        ),
        TinderContactCardModel(
            name = "Beatrice Ho", age = 23, description = "Studying gets me really hungry, looking for a friend who eats as much as me!", backgroundColor = Color.parseColor("#aa00c7")
        ),
        TinderContactCardModel(
            name = "Nicholas Wang", age = 22, description = "Hit me up for supper jios! Enjoy late night adventures!", backgroundColor = Color.parseColor("#3f1dcb")
        ),
        TinderContactCardModel(
            name = "Cindy Low", age = 19, description = "New to Singapore! Excited to try all the local cuisines!", backgroundColor = Color.parseColor("#0043ca")
        )
    )
    private var currentIndex = 0

    private val topCard
        get() = data[currentIndex % data.size]
    private val bottomCard
        get() = data[(currentIndex + 1) % data.size]

    init {
        updateCards()
    }

    fun swipe() {
        currentIndex += 1
        updateCards()
    }

    private fun updateCards() {
        stream.value = TinderContactModel(
            cardTop = topCard,
            cardBottom = bottomCard
        )
    }

//    public fun setData(matchesArray: ArrayList<QueryDocumentSnapshot>) {
//        for (doc in matchesArray) {
//            // get matched profile using doc.id
//            val docRef = db.collection("users").document(doc.id)
//            var name
//            var desc
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//                        name = document.get("name")
//                        desc = document.get("description")
//                        Log.d("CardCreation", "Retrieved matching user data: ${document.data}")
//                    } else {
//                        Log.d("CardCreation", "Cannot retrieve matching user data")
//                    }
//                }
//
//            dataArray.add(
//                TinderContactCardModel(
//                    name = name, age = 27, description = desc, backgroundColor = Color.parseColor("#c50e29")
//                )
//            )
//        }
//    }

}