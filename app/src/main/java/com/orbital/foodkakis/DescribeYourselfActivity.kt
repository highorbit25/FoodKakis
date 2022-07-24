package com.orbital.foodkakis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityDescribeYourselfBinding

class DescribeYourselfActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityDescribeYourselfBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescribeYourselfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore

        binding.nextButton.setOnClickListener {
            val userRef = db.collection("users").document(currentUserUid)
            val describeYourself = binding.descriptionFill.text.toString()
            if (describeYourself != "") {
                // Set the "description" field of the user
                userRef
                    .update("description", describeYourself)
                    .addOnSuccessListener { Log.d("DescribeYourself", "Description updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("DescribeYourself", "Error updating description", e) }

                // move to ProfileActivity
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}