package com.orbital.foodkakis

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.FirebaseProfileService.getDoc
import com.orbital.foodkakis.FirebaseProfileService.getMatches
import com.orbital.foodkakis.databinding.ActivityDashboardSwipeBinding
import kotlinx.android.synthetic.main.activity_dashboard_swipe.*
import kotlinx.android.synthetic.main.activity_dashboard_swipe.view.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.coroutines.tasks.await

//import kotlinx.android.synthetic.main.activity_tinder_scene9.*

//import kotlinx.android.synthetic.main.activity_tinder_scene9.*



class DashboardSwipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardSwipeBinding
    private lateinit var mAuth: FirebaseAuth
//    private val matchesArray = ArrayList<String>()
    private lateinit var viewModel: TinderContactViewModel
    private lateinit var currentProfile: TinderContactCardModel
    private var curMatchId = "Null"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardSwipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        currentProfile = model.cardTop
        containerCardOne.setBackgroundColor(model.cardTop.backgroundColor)
        name.text = "${model.cardTop.name}, ${model.cardTop.age}"
        description.text = model.cardTop.description
        curMatchId = model.cardTop.id
        Glide.with(this).load(model.cardTop.imageUrl.toUri()).into(imageView2)
        containerCardTwo.setBackgroundColor(model.cardBottom.backgroundColor)
//        containerCardTwo.imageView2.setImageURI()
    }

    private fun generateViewModel() {
        Log.i("DashboardSwipeActivity", "Called ViewModelProvider.get")
        viewModel = ViewModelProvider(this).get(TinderContactViewModel::class.java)



        viewModel
            .modelStream
            .observe(this, Observer {
                // update UI, bind configs to top and btm card
                bindCard(it)
            })

        viewModel
            .finish
            .observe(this, Observer {
                if (it) {
                    mAuth = FirebaseAuth.getInstance()
                    val db = Firebase.firestore
                    val currentUserUid = mAuth.currentUser?.uid.toString()
                    // check if it is still an active request
                    var active = false
                    db.collection("users").document(currentUserUid).get().addOnSuccessListener {
                        doc -> active = doc.get("active_request") as Boolean
                        Log.d("NoMoreMatches", "Active Request: $active")
                        if (active) {
                            // redirect to show no more matches
                            Log.d("NoMoreMatches", "Still an Active Request, but out of cards to display")
                            Toast.makeText(this, "No more matches!", Toast.LENGTH_SHORT).show()
                            val noMoreMatchesIntent = Intent(this, DashboardNoMoreMatches::class.java)
                            startActivity(noMoreMatchesIntent)
                        }
                    }




                }
            })

        motionLayout.setTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.id.offScreenUnlike,
                    R.id.offScreenLike -> {
                        if (currentId == R.id.offScreenUnlike) {
                            Toast.makeText(getApplicationContext(), "Swiped Left on ${name.text} $curMatchId", Toast.LENGTH_SHORT).show()
                            // Just add curMatch to user's swiped left list
                            mAuth = FirebaseAuth.getInstance()
                            val db = Firebase.firestore
                            val currentUserUid = mAuth.currentUser?.uid.toString()
                            val curMatch = curMatchId
                            val docRef = db.collection("users").document(currentUserUid)
                                .collection("swiped_left").document(curMatchId)
                            val swipedLeft = hashMapOf(
                                "swiped_left_by" to currentUserUid,
                                "swiped_left_on" to curMatchId)
                            docRef
                                .set(swipedLeft)
                                .addOnSuccessListener {
                                    Log.d("SwipedLeftOn", "Swiped Left on: $curMatch")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("SwipedLeftOn", "Error adding to swiped left list", e)
                                }

                            // let us also add the curMatchId to the user's swiped_on array -> ensure user not shown
                            // the same profile on subsequent visits
                            db.collection("users").document(currentUserUid).update("swiped_on", FieldValue.arrayUnion(curMatchId))
                            motionLayout.progress = 0f
                            motionLayout.setTransition(R.id.start, R.id.detail)
                            viewModel.swipe()
                        } else if (currentId == R.id.offScreenLike) {
                            Toast.makeText(getApplicationContext(), "Swiped Right on ${name.text} $curMatchId", Toast.LENGTH_SHORT).show()
                            // Add curMatch to user's swiped right list
                            mAuth = FirebaseAuth.getInstance()
                            val db = Firebase.firestore
                            val currentUserUid = mAuth.currentUser?.uid.toString()
                            val docRef = db.collection("users").document(currentUserUid)
                                .collection("swiped_right").document(curMatchId)
                            val swipedRight = hashMapOf(
                                "swiped_right_by" to currentUserUid,
                                "swiped_right_on" to curMatchId)
                            docRef
                                .set(swipedRight)
                                .addOnSuccessListener {
                                    Log.d("SwipedRightOn", "Swiped Right on: $curMatchId")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("SwipedRightOn", "Error adding to swiped right list", e)
                                }
                            // let us also add the curMatchId to the user's swiped_on array -> ensure user not shown
                            // the same profile on subsequent visits
                            db.collection("users").document(currentUserUid).update("swiped_on", FieldValue.arrayUnion(curMatchId))

                            // Check if curUser has been mutually swiped by curMatch by looking at curMatch's swiped right list
                            db.collection("users").document(curMatchId).collection("swiped_right")
                                .whereEqualTo("swiped_right_on", currentUserUid).limit(1)
                                .get().addOnSuccessListener {
                                    if (it.size() == 1) {
                                        // Mutual swipe -> Successful Match found! Add to chat collection, close request and redirect to Matches Activity
                                        Log.d("MutualSwipe", "Mutual Swipe between $currentUserUid & $curMatchId")

                                        // Add to Chats Collection
                                        val chatA = hashMapOf("user_a_id" to currentUserUid,
                                            "user_b_id" to curMatchId)
                                        db.collection("chats").add(chatA)
                                        val chatB = hashMapOf("user_a_id" to curMatchId,
                                            "user_b_id" to currentUserUid)
                                        db.collection("chats").add(chatB)

                                        // Remove active request for both matched users
                                        db.collection("users").document(currentUserUid).update("active_request", false)
                                        db.collection("users").document(curMatchId).update("active_request", false)
                                        Log.d("MutualSwipe", "Removed active_request for $currentUserUid & $curMatchId")



                                        motionLayout.progress = 0f
                                        motionLayout.setTransition(R.id.start, R.id.detail)
                                        viewModel.swipe()
                                        // Redirect to Matches Activity
                                        goMatches()
                                    } else {
                                        Log.d("MutualSwipe", "No Mutual Swipe between $currentUserUid & $curMatchId as of now")
                                    }

                                }
                        }
                    }
                }
            }
        })

        likeFloating.setOnClickListener {
            Toast.makeText(this, "Swiped Right on ${name.text} $curMatchId", Toast.LENGTH_SHORT).show()

            // Add curMatch to user's swiped right list
            mAuth = FirebaseAuth.getInstance()
            val db = Firebase.firestore
            val currentUserUid = mAuth.currentUser?.uid.toString()
            val docRef = db.collection("users").document(currentUserUid)
                .collection("swiped_right").document(curMatchId)
            val swipedRight = hashMapOf(
                "swiped_right_by" to currentUserUid,
                "swiped_right_on" to curMatchId)
            docRef
                .set(swipedRight)
                .addOnSuccessListener {
                    Log.d("SwipedRightOn", "Swiped Right on: $curMatchId")
                }
                .addOnFailureListener { e ->
                    Log.w("SwipedRightOn", "Error adding to swiped right list", e)
                }
            // let us also add the curMatchId to the user's swiped_on array -> ensure user not shown
            // the same profile on subsequent visits
            db.collection("users").document(currentUserUid).update("swiped_on", FieldValue.arrayUnion(curMatchId))

            // Check if curUser has been mutually swiped by curMatch by looking at curMatch's swiped right list
            db.collection("users").document(curMatchId).collection("swiped_right")
                .whereEqualTo("swiped_right_on", currentUserUid).limit(1)
                .get().addOnSuccessListener {
                    if (it.size() == 1) {
                        // Mutual swipe -> Successful Match found! Add to chat collection, close request and redirect to Matches Activity
                        Log.d("MutualSwipe", "Mutual Swipe between $currentUserUid & $curMatchId")

                        // Add to Chats Collection
                        val chatA = hashMapOf("user_a_id" to currentUserUid,
                            "user_b_id" to curMatchId)
                        db.collection("chats").add(chatA)
                        val chatB = hashMapOf("user_a_id" to curMatchId,
                            "user_b_id" to currentUserUid)
                        db.collection("chats").add(chatB)

                        // Remove active request for both matched users
                        db.collection("users").document(currentUserUid).update("active_request", false)
                        db.collection("users").document(curMatchId).update("active_request", false)
                        Log.d("MutualSwipe", "Removed active_request for $currentUserUid & $curMatchId")

                        // Redirect to Matches Activity
                        val MatchesIntent = Intent(this, MatchesActivity::class.java)
                        startActivity(MatchesIntent)
                    } else {
                        Log.d("MutualSwipe", "No Mutual Swipe between $currentUserUid & $curMatchId as of now")
                    }

                }

            motionLayout.transitionToState(R.id.like)

        }

        unlikeFloating.setOnClickListener {
            Toast.makeText(this, "Swiped Left on ${name.text} $curMatchId", Toast.LENGTH_SHORT).show()
            // Just add curMatch to user's swiped left list
            mAuth = FirebaseAuth.getInstance()
            val db = Firebase.firestore
            val currentUserUid = mAuth.currentUser?.uid.toString()
            val docRef = db.collection("users").document(currentUserUid)
                .collection("swiped_left").document(curMatchId)
            val swipedLeft = hashMapOf(
                "swiped_left_by" to currentUserUid,
                "swiped_left_on" to curMatchId)
            docRef
                .set(swipedLeft)
                .addOnSuccessListener {
                    Log.d("SwipedLeftOn", "Swiped Left on: $curMatchId")
                }
                .addOnFailureListener { e ->
                    Log.w("SwipedLeftOn", "Error adding to swiped left list", e)
                }

            // let us also add the curMatchId to the user's swiped_on array -> ensure user not shown
            // the same profile on subsequent visits
            db.collection("users").document(currentUserUid).update("swiped_on", FieldValue.arrayUnion(curMatchId))


            motionLayout.transitionToState(R.id.unlike)


        }
    }

    fun goMatches() {
        // Redirect to Matches Activity
        val MatchesIntent = Intent(this, MatchesActivity::class.java)
        startActivity(MatchesIntent)
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