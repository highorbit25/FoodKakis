package com.orbital.foodkakis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityDashboardNoMoreMatchesBinding

class DashboardNoMoreMatches : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardNoMoreMatchesBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardNoMoreMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelButton.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            builder.setMessage("Are you sure you want to cancel request?")
                .setCancelable(false)
                .setPositiveButton("Yes") {
                        _, _ ->
                    mAuth = FirebaseAuth.getInstance()
                    val db = Firebase.firestore
                    val currentUserUid = mAuth.currentUser?.uid.toString()
                    db.collection("users").document(currentUserUid).update("active_request", false)
                    val dashboardIntent= Intent(this, DashboardActivity::class.java)
                    startActivity(dashboardIntent)
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }


        // Logic for Navigation Bar
        binding.bottomNavigationView.selectedItemId = R.id.dashboard
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
}