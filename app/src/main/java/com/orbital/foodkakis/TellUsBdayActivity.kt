package com.orbital.foodkakis

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityTellUsBdayBinding
import java.text.SimpleDateFormat
import java.time.Period
import java.time.ZonedDateTime
import java.util.*

class TellUsBdayActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityTellUsBdayBinding

    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var curYear = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]
    private var age = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTellUsBdayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore

        binding.tellUsBdayFill.setOnClickListener {
            selectDate()
        }

        binding.nextButton.setOnClickListener {
            val userRef = db.collection("users").document(currentUserUid)
            val birthday = binding.tellUsBdayFill.text.toString()

            if (birthday != null) {
                // Set the "birthday" field of the user
                userRef
                    .update("birthday", birthday)
                    .addOnSuccessListener { Log.d("TellUsBday", "Birthday updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("TellUsBday", "Error updating birthday", e) }

                // Set the "age" field of the user
                userRef
                    .update("age", age)
                    .addOnSuccessListener { Log.d("TellUsBday", "Age updated for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("TellUsBday", "Error updating age", e) }

                // Initialise the "swiped_on" field of the user
                userRef
                    .update("swiped_on", ArrayList<String>())
                    .addOnSuccessListener { Log.d("TellUsBday", "Init swiped_on array for: $currentUserUid") }
                    .addOnFailureListener { e -> Log.w("TellUsBday", "Error initialising swiped_on array", e) }

                // move to DescribeYourselfActivity
                val intent = Intent(this, DescribeYourselfActivity::class.java)
                startActivity(intent)
                finish()
            }

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
                binding.tellUsBdayFill.text = Editable.Factory.getInstance().newEditable(sdf.format(currentDate.time))
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