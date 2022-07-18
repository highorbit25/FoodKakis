package com.orbital.foodkakis

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.DashboardSwipeActivity
import kotlinx.coroutines.launch


private lateinit var mAuth: FirebaseAuth
//private lateinit var data: ArrayList<TinderContactCardModel>
private lateinit var dataArray: ArrayList<TinderContactCardModel>
private lateinit var list: List<TinderContactCardModel>
//class TinderContactViewModel(private val dataArray: ArrayList<TinderContactCardModel>): ViewModel() {
class TinderContactViewModel(): ViewModel() {

    val repository = TinderContactRepository()
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

//    private val topCard
//        get() = data[currentIndex % data.size]
//    private val bottomCard
//        get() = data[(currentIndex + 1) % data.size]
    private val topCard
        get() = dataArray[currentIndex % dataArray.size]
    private val bottomCard
        get() = dataArray[(currentIndex + 1) % dataArray.size]


    fun swipe() {
        currentIndex += 1
        updateCards()
    }

    private fun updateCards() {
        if (currentIndex >= dataArray.size) {
            // no more matches to display
            Log.d("UpdateCards", "No more matches to display")
//            stream.value = TinderContactModel(
//                cardTop = TinderContactCardModel("",
//                    "",
//                    "",
//                    "https://st2.depositphotos.com/1008768/8271/i/950/depositphotos_82711600-stock-photo-no-more-sign.jpg",
//                    Color.parseColor("#205375")),
//                cardBottom = TinderContactCardModel("",
//                    "",
//                    "",
//                    "https://st2.depositphotos.com/1008768/8271/i/950/depositphotos_82711600-stock-photo-no-more-sign.jpg",
//                    Color.parseColor("#205375"))
//            )
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
            mAuth = FirebaseAuth.getInstance()
            val currentUserUid = mAuth.currentUser?.uid.toString()
            val docRef = db.collection("users").document(currentUserUid)
            var selectedMode: String? = null
            var date: String? = null
            var timeSlot: String? = null
            val matchesArray = ArrayList<QueryDocumentSnapshot>()

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.get("active_request") == true) {
                        selectedMode = document.get("selected_mode") as String?
                        date = document.get("date") as String?
                        timeSlot = document.get("timeslot") as String?
                        Log.d("GetActiveReq", "Retrieved active request: ${document.data}")
//                        list = FirebaseProfileService.getMatches(selectedMode, date, timeSlot)
                    }
                }

//            list = FirebaseProfileService.getMatches(selectedMode, date, timeSlot)
            list = FirebaseProfileService.getMatches()
            dataArray = ArrayList<TinderContactCardModel>(list)
//            dataArray = FirebaseProfileService.getMatches()
            Log.w("viewModelScope", "got list from Firebase ")
            println(list.size.toString())
            for (card in list) {
                println(card.name)
                println(card.age)
                println(card.description)
            }
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