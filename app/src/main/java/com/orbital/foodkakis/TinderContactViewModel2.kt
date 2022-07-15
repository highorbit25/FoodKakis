package com.orbital.foodkakis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User




private lateinit var mAuth: FirebaseAuth
//private lateinit var mRepo: TinderContactRepository
//private val matchesArray = ArrayList<QueryDocumentSnapshot>()
private lateinit var data: LiveData<ArrayList<TinderContactCardModel>>


//class TinderContactViewModel2(private var dataArray: ArrayList<TinderContactCardModel>): ViewModel() {
class TinderContactViewModel2(): ViewModel() {


    var stream = MutableLiveData<TinderContactModel>()
    val modelStream: LiveData<TinderContactModel>
        get() = stream

//    var data: MutableLiveData<ArrayList<TinderContactCardModel>>? = null
//    var tinderRepo: MutableLiveData<ArrayList<TinderContactCardModel>>? = null

    var tinderRepo: TinderContactRepository? = null

//    fun queryRepo() {
//        tinderRepo = TinderContactRepository.getInstance()
//        data = tinderRepo!!.getData()
//    }


//
    fun getData(): LiveData<ArrayList<TinderContactCardModel>> {
        return data
    }


//    init {
//        val db = Firebase.firestore
//        mAuth = FirebaseAuth.getInstance()
//
//        val currentUserUid = mAuth.currentUser?.uid.toString()
//        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
//        var selectedMode: String?
//        var date: String?
//        var timeSlot: String?
//        val dataArray = ArrayList<TinderContactCardModel>()
//        val matchesArray = ArrayList<QueryDocumentSnapshot>()
//
//
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document.get("active_request") == true) {
//                    selectedMode = document.get("selected_mode") as String?
//                    date = document.get("date") as String?
//                    timeSlot = document.get("timeslot") as String?
//                    Log.d("GetActiveReq", "Retrieved active request: ${document.data}")
//
//                    val matchesRef = db.collection(selectedMode.toString())
//                        .document(date.toString())
//                        .collection(timeSlot.toString())
//                    matchesRef
//                        .whereEqualTo("successful", false)
//                        .get()
//                        .addOnSuccessListener { documents ->
//                            for (document in documents) {
//                                if (document.id == currentUserUid.toString()) {
//                                    // skip ownself
//                                    continue
//                                }
//                                matchesArray.add(document)
//                                Log.d(
//                                    "DashboardSwipeActivity Match found",
//                                    "${document.id} => ${document.data}"
//                                )
//                            }
//                            // check if matchesArray is properly updated
//                            println("......print matchesArray......")
//                            println(matchesArray.size.toString())
//                            for (doc in matchesArray) {
//                                println(doc.id)
//                            }
//
//                            for (doc in matchesArray) {
//                                // get matched profile using doc.id
//                                val docu = db.collection("users").document(doc.id)
//                                var name: String = "null"
//                                var desc: String = "null"
//                                docu.get()
//                                    .addOnSuccessListener { document ->
//                                        if (document != null) {
//                                            name = document.get("name").toString()
//                                            desc = document.get("description").toString()
//                                            Log.d("CardCreation", "Retrieved matching user data: ${document.data}")
//                                            // add the card to dataArray
//                                            dataArray.add(
//                                                TinderContactCardModel(
//                                                    name = name, age = 27, description = desc, backgroundColor = Color.parseColor("#c50e29")
//                                                )
//                                            )
//                                            Log.d("Add to dataArray", "Card added for: ${name}")
//
//                                        } else {
//                                            Log.w("CardCreation", "Cannot retrieve matching user data")
//                                        }
//                                    }
//                            }
//
//                            // check if arrayList is properly updated
//                            println("......print dataArray......")
//                            println(dataArray.size.toString())
//                            for (card in dataArray) {
//                                println(card.name)
//                                println(card.age)
//                                println(card.description)
//                            }
//
//
//
//                        }
//                        .addOnFailureListener { exception ->
//                            Log.w("DashboardSwipeActivity", "Error getting documents: ", exception)
//                        }
//
//                } else {
//                    Log.w("GetActiveReq", "Cannot retrieve active request")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("GetActiveReq", "getting active request failed with ", exception)
//            }
////        val mData = MutableLiveData<TinderContactCardModel>()
////        mData.value = dataArray
////        stream = dataArray
//        updateCards()
//
//
//    }



//    private val data = TinderContactRepository().getData().value!!
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

    init {
//        data = TinderContactRepository().getData().value!!
//        private val data = TinderContactRepository().getData().value!!
//        queryRepo()
        updateCards()
    }

    private var currentIndex = 0

    private val topCard
        get() = data.value!![currentIndex % data.value!!.size]
    private val bottomCard
        get() = data.value!![(currentIndex + 1) % data.value!!.size]


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

//    class Factory(var dataArray: ArrayList<TinderContactCardModel>) : ViewModelProvider.Factory {
//        @Suppress("UNCHECKED_CAST")
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return TinderContactViewModel2(dataArray) as T
//        }
//    }

}