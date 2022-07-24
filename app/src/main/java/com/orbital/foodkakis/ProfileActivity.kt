package com.orbital.foodkakis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orbital.foodkakis.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore

        val docRef = db.collection("users").document(currentUserUid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    name_txt.text = document.get("name") as CharSequence?
                    Log.d("GetUserData", "Retrieved user data: ${document.data}")
                } else {
                    Log.d("GetUserData", "Cannot retrieve user data")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("GetUserData", "get failed with ", exception)
            }

        email_txt.text = currentUser?.email

        getImage()

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        sign_out_btn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Logic for Navigation Bar
        binding.bottomNavigationView.selectedItemId = R.id.me
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

    private fun getImage() {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "users/ $currentUserUid/profile.jpg"
        )
        sRef.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(profile_image)
        }
    }
}
