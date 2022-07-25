package com.orbital.foodkakis

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch



private lateinit var dataArray: ArrayList<TinderContactCardModel>
private lateinit var list: List<TinderContactCardModel>
class TinderContactViewModel(): ViewModel() {
    val db = Firebase.firestore
    private val stream = MutableLiveData<TinderContactModel>()
    val modelStream: LiveData<TinderContactModel>
        get() = stream
    private val _finish = MutableLiveData<Boolean>()
    val finish: LiveData<Boolean>
        get() = _finish


//    private val data = arrayListOf<TinderContactCardModel>(
//        TinderContactCardModel(
//            name = "Alice Tan", age = 22, description = "I love prata and everything curry related!", backgroundColor = Color.parseColor("#c50e29")
//        ),
//        TinderContactCardModel(
//            name = "Timothy Lee", age = 21, description = "Food is life", backgroundColor = Color.parseColor("#c60055")
//        ),
//        TinderContactCardModel(
//            name = "Beatrice Ho", age = 23, description = "Studying gets me really hungry, looking for a friend who eats as much as me!", backgroundColor = Color.parseColor("#aa00c7")
//        ),
//        TinderContactCardModel(
//            name = "Nicholas Wang", age = 22, description = "Hit me up for supper jios! Enjoy late night adventures!", backgroundColor = Color.parseColor("#3f1dcb")
//        ),
//        TinderContactCardModel(
//            name = "Cindy Low", age = 19, description = "New to Singapore! Excited to try all the local cuisines!", backgroundColor = Color.parseColor("#0043ca")
//        )
//    )

    private var currentIndex = 0

    private val topCard
        get() = dataArray[currentIndex % dataArray.size]
    private val bottomCard
        get() = dataArray[(currentIndex + 1) % dataArray.size]

    fun swipe() {
        currentIndex += 1
        updateCards()
        Log.d("TEST", "update cards called")
    }

    private fun updateCards() {
        if (currentIndex >= dataArray.size) {
            // no more matches to display
            Log.d("UpdateCards", "No more matches to display")
            _finish.value = true
        } else {
            stream.value = TinderContactModel(
                cardTop = topCard,
                cardBottom = bottomCard
            )
        }
    }

    init {
        viewModelScope.launch {
            list = FirebaseProfileService.getMatches()
            dataArray = ArrayList<TinderContactCardModel>(list)
            Log.w("viewModelScope", "got list from Firebase ")
            println(list.size.toString())
            println(dataArray.size.toString())
            for (card in dataArray) {
                println(card.name)
                println(card.age)
                println(card.description)
            }
            updateCards()
        }
    }
}