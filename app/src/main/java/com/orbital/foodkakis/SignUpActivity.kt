package com.orbital.foodkakis

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.orbital.foodkakis.databinding.ActivitySignUpBinding
import android.widget.Toast
import com.google.firebase.auth.ktx.userProfileChangeRequest

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser


        if(user != null){
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
            finish()
        }

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.signUpEmail.text.toString()
            val pass = binding.signUpPassword.text.toString()
            val confirmPass = binding.confirmPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val profileUpdates = userProfileChangeRequest {
                                displayName = "Jane Q. User"
//                                photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                            }

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("DefaultProfile", "User profile updated.")
                                    }
                                }
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}