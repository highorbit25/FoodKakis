package com.orbital.foodkakis

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.TextMessage
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
import kotlinx.android.synthetic.main.activity_profile.*
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

        binding.cancelButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to cancel request?")
                .setCancelable(false)
                .setPositiveButton("Yes") {
                        dialog, id ->
                    mAuth = FirebaseAuth.getInstance()
                    val db = Firebase.firestore
                    val currentUserUid = mAuth.currentUser?.uid.toString()
                    db.collection("users").document(currentUserUid).update("active_request", false)
                    val dashboardIntent= Intent(this, DashboardActivity::class.java)
                    startActivity(dashboardIntent)
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }



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
                            db.collection("users").document(currentUserUid)
                                .update("swiped_on", FieldValue.arrayUnion(curMatchId))
                                .addOnCompleteListener {
                                    Log.d("Swiped_On", "Added: $curMatchId to swiped_on array")
                                }

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

                                        startConvo(curMatchId)
                                        Thread.sleep(1_000)

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
            db.collection("users").document(currentUserUid)
                .update("swiped_on", FieldValue.arrayUnion(curMatchId))
                .addOnCompleteListener {
                    Log.d("Swiped_On", "Added: $curMatchId to swiped_on array")
                }

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

                        startConvo(curMatchId)
                        Thread.sleep(1_000)

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

                    // let us also add the curMatchId to the user's swiped_on array -> ensure user not shown
                    // the same profile on subsequent visits
                    db.collection("users").document(currentUserUid)
                        .update("swiped_on", FieldValue.arrayUnion(curMatchId))
                        .addOnCompleteListener {
                            Log.d("Swiped_On", "Added: $curMatchId to swiped_on array")
                        }
                }
                .addOnFailureListener { e ->
                    Log.w("SwipedLeftOn", "Error adding to swiped left list", e)
                }
            motionLayout.transitionToState(R.id.unlike)
        }
    }

    fun goMatches() {
        // Redirect to Matches Activity
        val matchesIntent = Intent(this, MatchesActivity::class.java)
        startActivity(matchesIntent)
    }

    fun startConvo(curMatchId: String) {
        val db = Firebase.firestore
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val userARef = db.collection("users").document(currentUserUid)
        val userBRef = db.collection("users").document(curMatchId)
        var nameOfA: String
        var nameOfB: String
        val cravingsOfA = ArrayList<String>()
        val cravingsOfB = ArrayList<String>()
        var timeslot: String
        var date: String


        userARef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    nameOfA = document.get("name") as String
                    Log.d("GetUserAData", "Retrieved user A data: ${document.data}")
                } else {
                    Log.d("GetUserAData", "Cannot retrieve user A data")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("GetUserAData", "get failed with ", exception)
            }

        userBRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    nameOfB = document.get("name") as String
                    date = document.get("date") as String
                    timeslot = document.get("timeslot") as String
                    Log.d("GetUserBData", "Retrieved user B data: ${document.data}")

                    // Start chat between matched users
                    val receiverID: String = curMatchId
                    val messageText: String = "Hello $nameOfB, we have matched as FoodKakis for $date at" +
                            " $timeslot!!! Let's discuss the details of our meetup here! "
                    val receiverType: String = CometChatConstants.RECEIVER_TYPE_USER

                    val textMessage = TextMessage(receiverID, messageText,receiverType)

                    CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
                        override fun onSuccess(p0: TextMessage?) {
                            Log.d(ContentValues.TAG, "Starting message sent successfully: " + p0?.toString())
                        }

                        override fun onError(p0: CometChatException?) {
                            Log.d(ContentValues.TAG, "Starting message failed with exception: " + p0?.message)          }

                    })

                } else {
                    Log.d("GetUserBData", "Cannot retrieve user B data")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("GetUserBData", "get failed with ", exception)
            }


    }

}