package com.orbital.foodkakis

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orbital.foodkakis.databinding.ActivityEditProfileBinding
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.text.SimpleDateFormat
import java.time.Period
import java.time.ZonedDateTime
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var currentUserUid: String
    private var mSelectedImageFileUri: Uri? = null
    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var curYear = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]
    private var age = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser
        currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore
        val docRef = db.collection("users").document(currentUserUid)

        getImage()

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

        binding.updatePhotoBtn.setOnClickListener {
            val openGalleryIntent = Intent(Intent.ACTION_PICK)
            openGalleryIntent.type = "image/*"
            startActivityForResult(openGalleryIntent, 1000)
        }

        binding.updateProfileBtn.setOnClickListener {
            val birthday = binding.editProfileBdayFill.text.toString()
            val desc = binding.editProfileDescFill.text.toString()

            if (birthday != "" && desc != "") {
                // Set the "birthday" & 'description" field of the user
                docRef
                    .update("birthday", birthday)
                    .addOnSuccessListener { Log.d("EditBday", "Birthday updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("EditBday", "Error updating birthday", e) }
                // Set the "age" field of the user
                docRef
                    .update("age", age)
                    .addOnSuccessListener { Log.d("TellUsBday", "Age updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("TellUsBday", "Error updating age", e) }
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
            binding.progressBar2.visibility = View.VISIBLE
            binding.profileImage.visibility = View.INVISIBLE
            val db = Firebase.firestore
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "users/ $currentUserUid/profile.jpg"
            )
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            Glide.with(this).load(url).into(profile_image)
                            binding.progressBar2.visibility = View.INVISIBLE
                            binding.profileImage.visibility = View.VISIBLE
                            Toast.makeText(this, "Photo updated", Toast.LENGTH_SHORT).show()

                            // Set the "profile_image" field of the user
                            val userRef = db.collection("users").document(currentUserUid)
                            userRef
                                .update("profile_image", url)
                                .addOnSuccessListener { Log.d("UploadPhoto", "Photo uri updated for: $currentUserUid") }
                                .addOnFailureListener { e -> Log.w("UploadPhoto", "Error updating photo uri", e) }
                        }
                }.addOnFailureListener {
                    binding.progressBar2.visibility = View.INVISIBLE
                    binding.profileImage.visibility = View.VISIBLE
                    Toast.makeText(this, "Photo upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
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

    private fun selectDate() {
        val mDatePicker = DatePickerDialog(
            this, R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                day = selectedDay
                month = selectedMonth
                year = selectedYear
                age = curYear - year
                currentDate.set(year, month, day)
                binding.editProfileBdayFill.text = Editable.Factory.getInstance().newEditable(sdf.format(currentDate.time))
            }, year, month, day
        )

        // Restrict at least 18 YO
        mDatePicker.datePicker.maxDate = (ZonedDateTime.now() - Period.ofYears(18)).toInstant().toEpochMilli()
        mDatePicker.show()
        mDatePicker.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        mDatePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }
}