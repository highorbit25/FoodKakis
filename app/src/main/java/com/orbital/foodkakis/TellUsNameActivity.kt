package com.orbital.foodkakis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityTellUsNameBinding
import kotlinx.android.synthetic.main.activity_tell_us_name.*

class TellUsNameActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityTellUsNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTellUsNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore

        binding.nextButton.setOnClickListener {
            val nameFill = tellUsNameFill.text.toString()
            if (nameFill != "") {
                val name = hashMapOf("name" to nameFill,
                                     "id" to currentUserUid)
                // Add a new document with a generated ID
                db.collection("users").document(currentUserUid)
                    .set(name)
                    .addOnSuccessListener {
                        Log.d("TellUsName", "Name updated for: $currentUserUid")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TellUsName", "Error updating name", e)
                    }

                // move to UploadPhotoActivity
                val intent = Intent(this, UploadPhotoActivity::class.java)
                startActivity(intent)
                finish()
            }



        }
    }
}