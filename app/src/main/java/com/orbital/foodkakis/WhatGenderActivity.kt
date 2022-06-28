package com.orbital.foodkakis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityTellUsNameBinding
import com.orbital.foodkakis.databinding.ActivityWhatGenderBinding
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_tell_us_name.*
import kotlinx.android.synthetic.main.activity_what_gender.*
import kotlinx.android.synthetic.main.activity_what_gender.view.*

class WhatGenderActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityWhatGenderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhatGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore



        binding.nextButton.setOnClickListener {
            val userRef = db.collection("users").document(currentUserUid)
            val selectedGenderChip = genderChipGrp.findViewById<Chip>(genderChipGrp.checkedChipId).text.toString()
            // Set the "gender" field of the user
            userRef
                .update("gender", selectedGenderChip)
                .addOnSuccessListener { Log.d("WhatGender", "Gender updated for: ${currentUserUid}") }
                .addOnFailureListener { e -> Log.w("WhatGender", "Error updating gender", e) }

            // move to TellUsBdayActivity
            val intent = Intent(this, TellUsBdayActivity::class.java)
            startActivity(intent)
            finish()
        }
//        binding.nextButton.setOnClickListener {
//            val userRef = db.collection("users").document(currentUserUid)
//
//            // Set the "gender" field of the user
//            userRef
//                .update("gender", whatGenderFill.text.toString())
//                .addOnSuccessListener { Log.d("WhatGender", "Gender updated for: ${currentUserUid}") }
//                .addOnFailureListener { e -> Log.w("WhatGender", "Error updating gender", e) }
//
//            // move to TellUsBdayActivity
//        }
    }
}