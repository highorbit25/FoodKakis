package com.orbital.foodkakis

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private lateinit var mAuth: FirebaseAuth
private var instance: TinderContactRepository? = null

class TinderContactRepository {

    private val dataArray = ArrayList<TinderContactCardModel>()

    fun getData(): ArrayList<TinderContactCardModel> {
        return dataArray
    }


    companion object{
        fun getInstance(): TinderContactRepository? {
            if (instance == null) {
                instance = TinderContactRepository()
            }
            return instance
        }
    }

    private val _list = MutableLiveData<ArrayList<String>>()
    val list: LiveData<ArrayList<String>> = _list

    init {
//        getMatches()
        // check if arrayList is properly updated
        println("......print dataArray......")
        println(dataArray.size.toString())
        for (card in dataArray) {
            println(card.name)
            println(card.age)
            println(card.description)
        }
    }

    fun getMatches(){
        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore
        // retrieve details of the request
        val docRef = db.collection("users").document(currentUserUid)
        var selectedMode: String?
        var date: String?
        var timeSlot: String?
        val matchesArray = ArrayList<QueryDocumentSnapshot>()


        docRef.get()
            .addOnSuccessListener { document ->
                if (document.get("active_request") == true) {
                    selectedMode = document.get("selected_mode") as String?
                    date = document.get("date") as String?
                    timeSlot = document.get("timeslot") as String?
                    Log.d("GetActiveReq", "Retrieved active request: ${document.data}")

                    val matchesRef = db.collection(selectedMode.toString())
                        .document(date.toString())
                        .collection(timeSlot.toString())
                    matchesRef
                        .whereEqualTo("successful", false)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (doc in documents) {
                                if (doc.id == currentUserUid.toString()) {
                                    // skip ownself
                                    continue
                                }
                                matchesArray.add(doc)
                                Log.d(
                                    "DashboardSwipeActivity Match found",
                                    "${doc.id} => ${doc.data}"
                                )
                            }
                            for (doc in matchesArray) {
                                // get matched profile using doc.id
                                val profileRef = db.collection("users").document(doc.id)
                                var name: String
                                var desc: String
                                profileRef.get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            name = document.get("name").toString()
                                            desc = document.get("description").toString()
                                            Log.d("CardCreation", "Retrieved matching user data: ${document.data}")
                                            // add the card to data
                                            dataArray.add(
                                                TinderContactCardModel(
                                                    name = name, age = 27, description = desc, backgroundColor = Color.parseColor("#c50e29")
                                                )
                                            )
                                            Log.d("Add to data", "Card added for: ${name}")
                                            // check if arrayList is properly updated
                                            println("......print dataArray2......")
                                            println(dataArray.size.toString())
                                            for (card in dataArray) {
                                                println(card.name)
                                                println(card.age)
                                                println(card.description)
                                            }


                                        } else {
                                            Log.w("CardCreation", "Cannot retrieve matching user data")
                                        }
                                    }
                            }



                        }
                        .addOnFailureListener { exception ->
                            Log.w("DashboardSwipeActivity", "Error getting documents: ", exception)
                        }

                } else {
                    Log.w("GetActiveReq", "Cannot retrieve active request")
                }

            }
            .addOnFailureListener { exception ->
                Log.w("GetActiveReq", "getting active request failed with ", exception)
            }



    }

//    fun getData(): LiveData<ArrayList<TinderContactCardModel>> {
//        val data: MutableLiveData<ArrayList<TinderContactCardModel>> = MutableLiveData<ArrayList<TinderContactCardModel>>()
//
//
//        val db = Firebase.firestore
//        mAuth = FirebaseAuth.getInstance()
//        val currentUserUid = mAuth.currentUser?.uid.toString()
//        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
//        var selectedMode: String?
//        var date: String?
//        var timeSlot: String?
//        val dataArray = ArrayList<TinderContactCardModel>()
//        val matchesArray = ArrayList<QueryDocumentSnapshot>()
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
//
//
//        data.setValue(dataArray)
//        return data
//    }


//    fun getData(): ArrayList<TinderContactCardModel> {
//        var data: ArrayList<TinderContactCardModel> = ArrayList<TinderContactCardModel>()
//
//
//        val db = Firebase.firestore
//        mAuth = FirebaseAuth.getInstance()
//        val currentUserUid = mAuth.currentUser?.uid.toString()
//        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
//        var selectedMode: String?
//        var date: String?
//        var timeSlot: String?
//        val dataArray = ArrayList<TinderContactCardModel>()
//        val matchesArray = ArrayList<QueryDocumentSnapshot>()
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
//
//            return dataArray
////        data = dataArray
////        return data
//    }

//    val dataArray = ArrayList<TinderContactCardModel>()

    private var currentIndex = 0

    private val topCard
        get() = dataArray[currentIndex % dataArray.size]
    private val bottomCard
        get() = dataArray[(currentIndex + 1) % dataArray.size]

    fun swipe() {
        currentIndex += 1
//        updateCards()
    }

//    private fun updateCards() {
//        data = repository.getData()
//
//        stream.value = TinderContactModel(
//            cardTop = topCard,
//            cardBottom = bottomCard
//        )
//    }

    fun getList(): ArrayList<String> {
        val db = Firebase.firestore
        mAuth = FirebaseAuth.getInstance()

        val currentUserUid = mAuth.currentUser?.uid.toString()
        // retrieve details of the request
        val docRef = db.collection("users").document(currentUserUid)
        var selectedMode: String?
        var date: String?
        var timeSlot: String?
//        val matchesArray = ArrayList<QueryDocumentSnapshot>()
        val matchesArray = ArrayList<String>()


        docRef.get()
            .addOnSuccessListener { document ->
                if (document.get("active_request") == true) {
                    selectedMode = document.get("selected_mode") as String?
                    date = document.get("date") as String?
                    timeSlot = document.get("timeslot") as String?
                    Log.d("GetActiveReq", "Retrieved active request: ${document.data}")

                    val matchesRef = db.collection(selectedMode.toString())
                        .document(date.toString())
                        .collection(timeSlot.toString())
                    matchesRef
                        .whereEqualTo("successful", false)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                if (document.id == currentUserUid) {
                                    // skip ownself
                                    continue
                                }
//                                matchesArray.add(document)
                                matchesArray.add(document.id)
                                Log.d(
                                    "DashboardSwipeActivity Match found",
                                    "${document.id} => ${document.data}"
                                )
                            }

                            // check if matchesArray is properly updated
                            println("......print matchesArray......")
                            println(matchesArray.size.toString())
                            for (doc in matchesArray) {
                                println(doc)
//                                println(doc.id)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("DashboardSwipeActivity", "Error getting documents: ", exception)
                        }

                } else {
                    Log.w("GetActiveReq", "Cannot retrieve active request")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("GetActiveReq", "getting active request failed with ", exception)
            }
        _list.value = matchesArray
        return matchesArray
    }

//    fun getData(): LiveData<TinderContactModel> {
//        val data: MutableLiveData<TinderContactModel> = MutableLiveData<TinderContactModel>()
//
//
//        val db = Firebase.firestore
//        mAuth = FirebaseAuth.getInstance()
//        val currentUserUid = mAuth.currentUser?.uid.toString()
//        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
//        var selectedMode: String?
//        var date: String?
//        var timeSlot: String?
////        val dataArray = ArrayList<TinderContactCardModel>()
//        val matchesArray = ArrayList<QueryDocumentSnapshot>()
//
//        docRef.get().addOnSuccessListener {
//            document ->
//            selectedMode = document.get("selected_mode") as String?
//            date = document.get("date") as String?
//            timeSlot = document.get("timeslot") as String?
//            Log.d("GetActiveReq", "Retrieved active request: ${document.data}")
//            dataArray.add(
//                TinderContactCardModel(
//                    name = "name", age = 27, description = "desc", backgroundColor = Color.parseColor("#c50e29")
//                )
//            )
//        }
//
////        docRef.get()
////            .addOnSuccessListener { document ->
////                if (document.get("active_request") == true) {
////                    selectedMode = document.get("selected_mode") as String?
////                    date = document.get("date") as String?
////                    timeSlot = document.get("timeslot") as String?
////                    Log.d("GetActiveReq", "Retrieved active request: ${document.data}")
////
////                    val matchesRef = db.collection(selectedMode.toString())
////                        .document(date.toString())
////                        .collection(timeSlot.toString())
////                    matchesRef
////                        .whereEqualTo("successful", false)
////                        .get()
////                        .addOnSuccessListener { documents ->
////                            for (document in documents) {
////                                if (document.id == currentUserUid.toString()) {
////                                    // skip ownself
////                                    continue
////                                }
////                                matchesArray.add(document)
////                                Log.d(
////                                    "DashboardSwipeActivity Match found",
////                                    "${document.id} => ${document.data}"
////                                )
////                            }
////                            // check if matchesArray is properly updated
////                            println("......print matchesArray......")
////                            println(matchesArray.size.toString())
////                            for (doc in matchesArray) {
////                                println(doc.id)
////                            }
////
////                            for (doc in matchesArray) {
////                                // get matched profile using doc.id
////                                val docu = db.collection("users").document(doc.id)
////                                var name: String = "null"
////                                var desc: String = "null"
////                                docu.get()
////                                    .addOnSuccessListener { document ->
////                                        if (document != null) {
////                                            name = document.get("name").toString()
////                                            desc = document.get("description").toString()
////                                            Log.d("CardCreation", "Retrieved matching user data: ${document.data}")
////                                            // add the card to dataArray
////                                            dataArray.add(
////                                                TinderContactCardModel(
////                                                    name = name, age = 27, description = desc, backgroundColor = Color.parseColor("#c50e29")
////                                                )
////                                            )
////                                            Log.d("Add to dataArray", "Card added for: ${name}")
////
////                                        } else {
////                                            Log.w("CardCreation", "Cannot retrieve matching user data")
////                                        }
////                                    }
////                            }
////
////                            // check if arrayList is properly updated
////                            println("......print dataArray......")
////                            println(dataArray.size.toString())
////                            for (card in dataArray) {
////                                println(card.name)
////                                println(card.age)
////                                println(card.description)
////                            }
////
////                        }
////                        .addOnFailureListener { exception ->
////                            Log.w("DashboardSwipeActivity", "Error getting documents: ", exception)
////                        }
////
////                } else {
////                    Log.w("GetActiveReq", "Cannot retrieve active request")
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.w("GetActiveReq", "getting active request failed with ", exception)
////            }
//
//        data.setValue(TinderContactModel(
//            cardTop = topCard,
//            cardBottom = bottomCard
//        ))
//        return data
//    }


    //    fun getData(): LiveData<ArrayList<TinderContactCardModel>> {
//        val data: MutableLiveData<ArrayList<TinderContactCardModel>> = MutableLiveData<ArrayList<TinderContactCardModel>>()
//
//
//        val db = Firebase.firestore
//        mAuth = FirebaseAuth.getInstance()
//        val currentUserUid = mAuth.currentUser?.uid.toString()
//        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
//        var selectedMode: String?
//        var date: String?
//        var timeSlot: String?
//        val dataArray = ArrayList<TinderContactCardModel>()
//        val matchesArray = ArrayList<QueryDocumentSnapshot>()
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
//
//
//        data.setValue(dataArray)
//        return data
//    }

}