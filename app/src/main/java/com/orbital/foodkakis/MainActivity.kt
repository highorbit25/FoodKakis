package com.orbital.foodkakis

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.TinderScene10Activity

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        val appID = "214584e6db7d255c"
        val region = "us"
        val appSettings = AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(region).build()

        CometChat.init(this, appID, appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(successMessage: String) {
                Log.d(TAG, "Initialization completed successfully")
            }

            override fun onError(e: CometChatException) {
                Log.d(TAG, "Initialization failed with exception: "+e.message)
            }
        })


        val currentUserUid = mAuth.currentUser?.uid.toString()
        val UID = currentUserUid // Replace with the UID of the user to login
        val AUTH_KEY = "6681d7867030ba5820064c057e2bbca034e2d2a0" // Replace with your App Auth Key
        CometChat.login(UID, AUTH_KEY, object : CometChat.CallbackListener<User?>() {
            override fun onSuccess(user: User?) {
                Log.d(TAG, "Login Successful : "+user.toString())
            }

            override fun onError(e: CometChatException) {
                Log.d(TAG, "Login failed with exception: " + e.message);
            }
        })

        // Test swipe cards
//        val signInIntent = Intent(this, TinderScene10Activity::class.java)
//                startActivity(signInIntent)


        /**If user is not authenticated, send him to SignInActivity to authenticate first.
         * Else send him to DashboardActivity*/
        Handler().postDelayed({
            if(user != null){
                val dashboardIntent = Intent(this, DashboardActivity::class.java)
                startActivity(dashboardIntent)
                finish()

//                val profileIntent = Intent(this, ProfileActivity::class.java)
//                startActivity(profileIntent)
//                finish()
            }else{
                val signInIntent = Intent(this, SignInActivity::class.java)
                startActivity(signInIntent)
                finish()
            }
        }, 2000)

    }
}