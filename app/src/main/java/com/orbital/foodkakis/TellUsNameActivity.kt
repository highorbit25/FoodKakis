package com.orbital.foodkakis

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
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

                        // create user for comet chat
                        val apiKey = "6681d7867030ba5820064c057e2bbca034e2d2a0" // Replace with your API Key.
                        val user = User()
                        user.uid = currentUserUid // Replace with your uid for the user to be created.
                        user.name = nameFill // Replace with the name of the user

                        CometChat.createUser(user, apiKey, object : CometChat.CallbackListener<User>() {
                            override fun onSuccess(user: User) {
                                Log.d("createUser", "${user.toString()} on Comet Chat")
                            }

                            override fun onError(e: CometChatException) {
                                Log.e("createUser", e.message!!)
                            }
                        })

                        val currentUserUid = mAuth.currentUser?.uid.toString()
                        val UID = currentUserUid // Replace with the UID of the user to login
                        val AUTH_KEY = "6681d7867030ba5820064c057e2bbca034e2d2a0" // Replace with your App Auth Key
                        CometChat.login(UID, AUTH_KEY, object : CometChat.CallbackListener<User?>() {
                            override fun onSuccess(user: User?) {
                                Log.d(ContentValues.TAG, "Login Successful : "+user.toString())
                            }

                            override fun onError(e: CometChatException) {
                                Log.d(ContentValues.TAG, "Login failed with exception: " + e.message);
                            }
                        })
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