package com.orbital.foodkakis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetAccount.setOnClickListener {
            val email = binding.emailForgot.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ForgetActivity", "Email sent.")
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}