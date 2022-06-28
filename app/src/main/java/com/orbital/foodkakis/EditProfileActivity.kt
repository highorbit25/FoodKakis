package com.orbital.foodkakis

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityEditProfileBinding
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding
    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore
        val docRef = db.collection("users").document(currentUserUid)

        Glide.with(this).load(currentUser?.photoUrl).into(profile_image)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    edit_profile_name_fill.text = document.get("name") as CharSequence?
                    edit_profile_gender_fill.text = document.get("gender") as CharSequence?
                    edit_profile_bday_fill.setText(document.get("birthday") as CharSequence?)
                    edit_profile_desc_fill.setText(document.get("description") as CharSequence?)
                    Log.d("GetUserData", "Retrieved user data: ${document.data}")
                } else {
                    Log.d("GetUserData", "Cannot retrieve user data")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("GetUserData", "get failed with ", exception)
            }

        binding.updateProfileBtn.setOnClickListener {
            val birthday = binding.editProfileBdayFill.text.toString()
            val desc = binding.editProfileDescFill.text.toString()

            if (birthday != null && desc != null) {
                // Set the "birthday" & 'description" field of the user
                docRef
                    .update("birthday", birthday)
                    .addOnSuccessListener { Log.d("EditBday", "Birthday updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("EditBday", "Error updating birthday", e) }
                docRef
                    .update("description", desc)
                    .addOnSuccessListener { Log.d("EditDesc", "Description updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("EditDesc", "Error updating description", e) }

                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            }

        }

        binding.editProfileBdayFill.setOnClickListener {
            selectDate()
        }

        // Logic for Navigation Bar
        binding.bottomNavigationView.setSelectedItemId(R.id.me)
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

    private fun selectDate() {
        val mDatePicker = DatePickerDialog(
            this, R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                day = selectedDay
                month = selectedMonth
                year = selectedYear
                currentDate.set(year, month, day)
                binding.editProfileBdayFill.text = Editable.Factory.getInstance().newEditable(sdf.format(currentDate.time))
            }, year, month, day
        )

        mDatePicker.show()
        mDatePicker.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        mDatePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }
}