package com.orbital.foodkakis

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.orbital.foodkakis.databinding.ActivitySignInBinding
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignInBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_in)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestIdToken("319959386482-fn880e8p7u7hjv83hcbgen6r4hhhs0eb.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()

        binding.createAccText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.signInBtnEmail.setOnClickListener {
            val email = binding.signInEmail.text.toString()
            val pass = binding.signInPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val isNewUser: Boolean? = it.result?.additionalUserInfo?.isNewUser
                        if (isNewUser!!) {
                            val intent = Intent(this, TellUsNameActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // existing user
                            signInComet()
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

        sign_in_btn.setOnClickListener {
            signIn()
        }

        binding.forgotBtn.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signInComet() {
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val UID = currentUserUid // Replace with the UID of the user to login
        val AUTH_KEY = "6681d7867030ba5820064c057e2bbca034e2d2a0" // Replace with your App Auth Key
        CometChat.login(UID, AUTH_KEY, object : CometChat.CallbackListener<User?>() {
            override fun onSuccess(user: User?) {
                Log.d(ContentValues.TAG, "Login Successful : "+user.toString())
            }

            override fun onError(e: CometChatException) {
                Log.d(ContentValues.TAG, "Login failed with exception: " + e.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            } else {
                Log.w("SignInActivity", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val isNewUser: Boolean? = task.getResult()?.additionalUserInfo?.isNewUser
                    if (isNewUser!!) {
                        val intent = Intent(this, TellUsNameActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        signInComet()
                        // existing user
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("SignInActivity", "signInWithCredential:failure")
                }
            }
    }

}