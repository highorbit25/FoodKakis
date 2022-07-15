package com.orbital.foodkakis

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.FirebaseProfileService.getMatches
import com.orbital.foodkakis.databinding.ActivityDashboardSwipeBinding
import kotlinx.android.synthetic.main.activity_dashboard_swipe.*

//import kotlinx.android.synthetic.main.activity_tinder_scene9.*

//import kotlinx.android.synthetic.main.activity_tinder_scene9.*



class DashboardSwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardSwipeBinding
    private lateinit var mAuth: FirebaseAuth
    private val matchesArray = ArrayList<String>()
    private lateinit var viewModel: TinderContactViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardSwipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        getMatches()

        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser

        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore
        // retrieve details of the request
        val docRef = db.collection("users").document(currentUserUid)
        var selectedMode: String?
        var date: String?
        var timeSlot: String?

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
                                if (document.id == currentUserUid.toString()) {
                                    // skip ownself
                                    continue
                                }
                                matchesArray.add(document.id)
                                Log.d(
                                    "DashboardSwipeActivity Match found",
                                    "${document.id} => ${document.data}"
                                )
                            }
                            // save current matchArray within 'user'
                            val matches = hashMapOf(
                                "matchesArray" to matchesArray,
                            )
                            db.collection("users").document(currentUserUid)
                                // merge to existing data
                                .set(matches, SetOptions.merge())
                                .addOnSuccessListener {
                                    Log.d(
                                        "DashboardSwipe",
                                        "matchesArray stored for: $currentUserUid\" "
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "DashboardSwipe",
                                        "Error writing matchesArray within user",
                                        e
                                    )
                                }
//                            for (doc in matchesArray) {
//                                // get matched profile using doc.id
//                                val docRef = db.collection("users").document(doc.id)
//                                var name: String
//                                var desc: String
//                                docRef.get()
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
//                                            // check if arrayList is properly updated
//                                            println("......print dataArray......")
//                                            println(dataArray.size.toString())
//                                            for (card in dataArray) {
//                                                println(card.name)
//                                                println(card.age)
//                                                println(card.description)
//                                            }
//
//
//
//                                        } else {
//                                            Log.w("CardCreation", "Cannot retrieve matching user data")
//                                        }
//                                    }
//                            }

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
        generateViewModel()



        // Logic for Navigation Bar
        binding.bottomNavigationView.selectedItemId = R.id.dashboard
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard-> {
                    val dashboardIntent= Intent(this, DashboardActivity::class.java)
                    startActivity(dashboardIntent)
                }
                R.id.matches -> {
                    val matchesIntent= Intent(this, MatchesActivity::class.java)
                    startActivity(matchesIntent)
                }
                R.id.fnb -> {
                    val fnbIntent = Intent(this, FnbActivity::class.java)
                    startActivity(fnbIntent)
                }
                R.id.me -> {
                    val profileIntent = Intent(this, ProfileActivity::class.java)
                    startActivity(profileIntent)
                }
            }
            true
        }
    }


    private fun bindCard(model: TinderContactModel) {
        containerCardOne.setBackgroundColor(model.cardTop.backgroundColor)
        name.text = "${model.cardTop.name}, ${model.cardTop.age}"
        description.text = model.cardTop.description
        containerCardTwo.setBackgroundColor(model.cardBottom.backgroundColor)
    }

    private fun generateViewModel() {

//        val viewModel = ViewModelProvider(this)
//            .get(TinderContactViewModel2::class.java)

        //        val viewModel: TinderContactViewModel by viewModels()

//        val viewModel: TinderContactViewModel2 by viewModels { TinderContactViewModel2.Factory(
//            dataArray) }

        Log.i("GameFragment", "Called ViewModelProvider.get")
        viewModel = ViewModelProvider(this).get(TinderContactViewModel::class.java)



        viewModel
            .modelStream
            .observe(this, Observer {
                // update UI, bind configs to top and btm card
                bindCard(it)
            })

        motionLayout.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.id.offScreenUnlike,
                    R.id.offScreenLike -> {
                        motionLayout.progress = 0f
                        motionLayout.setTransition(R.id.start, R.id.detail)
                        viewModel.swipe()
                    }
                }
            }
        })

        likeFloating.setOnClickListener {
            motionLayout.transitionToState(R.id.like)
        }

        unlikeFloating.setOnClickListener {
            motionLayout.transitionToState(R.id.unlike)
        }
    }

//    fun getMatches() {
//        mAuth = FirebaseAuth.getInstance()
//        val currentUserUid = mAuth.currentUser?.uid.toString()
//        val db = Firebase.firestore
//        // retrieve details of the request
//        val docRef = db.collection("users").document(currentUserUid)
//        var selectedMode: String?
//        var date: String?
//        var timeSlot: String?
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
//                            for (doc in documents) {
//                                if (doc.id == currentUserUid.toString()) {
//                                    // skip ownself
//                                    continue
//                                }
//                                matchesArray.add(doc)
//                                Log.d(
//                                    "DashboardSwipeActivity Match found",
//                                    "${doc.id} => ${doc.data}"
//                                )
//                            }
//                            for (doc in matchesArray) {
//                                // get matched profile using doc.id
//                                val profileRef = db.collection("users").document(doc.id)
//                                var name: String
//                                var desc: String
//                                profileRef.get()
//                                    .addOnSuccessListener { document ->
//                                        if (document != null) {
//                                            name = document.get("name").toString()
//                                            desc = document.get("description").toString()
//                                            Log.d("CardCreation", "Retrieved matching user data: ${document.data}")
//                                            // add the card to data
//                                            dataArray.add(
//                                                TinderContactCardModel(
//                                                    name = name, age = 27, description = desc, backgroundColor = Color.parseColor("#c50e29")
//                                                )
//                                            )
//                                            Log.d("Add to data", "Card added for: ${name}")
//                                            // check if arrayList is properly updated
//                                            println("......print dataArray......")
//                                            println(dataArray.size.toString())
//                                            for (card in dataArray) {
//                                                println(card.name)
//                                                println(card.age)
//                                                println(card.description)
//                                            }
//
//                                        } else {
//                                            Log.w("CardCreation", "Cannot retrieve matching user data")
//                                        }
//                                    }
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
////        return dataArray
//
//    }

}