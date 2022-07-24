package com.orbital.foodkakis

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orbital.foodkakis.databinding.ActivityUploadPhotoBinding
import kotlinx.android.synthetic.main.activity_upload_photo.*

class UploadPhotoActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var currentUserUid: String
    private lateinit var binding: ActivityUploadPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        currentUserUid = mAuth.currentUser?.uid.toString()

        binding.profileImage.setOnClickListener {
            val openGalleryIntent = Intent(Intent.ACTION_PICK)
            openGalleryIntent.type = "image/*"
            startActivityForResult(openGalleryIntent, 1000)
        }

        binding.nextButton.setOnClickListener {
            // move to WhatGenderActivity
            val intent = Intent(this, WhatGenderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mSelectedImageFileUri = data.data!!
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage() {
        if (mSelectedImageFileUri != null) {
            binding.progressBar.visibility = View.VISIBLE
            binding.profileImage.visibility = View.INVISIBLE
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "users/ $currentUserUid/profile.jpg"
            )
            val db = Firebase.firestore
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            Glide.with(this).load(url).into(profile_image)
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.profileImage.visibility = View.VISIBLE
                            binding.nextButton.visibility = View.VISIBLE
                            Toast.makeText(this, "Photo updated", Toast.LENGTH_SHORT).show()

                            // Set the "profile_image" field of the user
                            val userRef = db.collection("users").document(currentUserUid)
                            userRef
                                .update("profile_image", url)
                                .addOnSuccessListener { Log.d("UploadPhoto", "Photo uri updated for: $currentUserUid") }
                                .addOnFailureListener { e -> Log.w("UploadPhoto", "Error updating photo uri", e) }

                            // Update photo for Comet here
                            val updatedUser = User()
                            updatedUser.avatar = url.toString()
                            CometChat.updateCurrentUserDetails(updatedUser, object : CometChat.CallbackListener<User>() {
                                override fun onSuccess(user: User) {
                                    Log.d("CometPhoto", "$user Photo updated" )
                                }

                                override fun onError(e: CometChatException) {
                                    Log.e("CometPhoto", e.message!!)
                                }
                            })
                        }
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.profileImage.visibility = View.VISIBLE
                    Toast.makeText(this, "Photo upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
}