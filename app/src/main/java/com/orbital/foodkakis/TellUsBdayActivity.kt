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
import java.util.*

class TellUsBdayActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityTellUsBdayBinding

    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]

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
                currentDate.set(year, month, day)
                binding.tellUsBdayFill.text = Editable.Factory.getInstance().newEditable(sdf.format(currentDate.time))
            }, year, month, day
        )

        mDatePicker.datePicker.maxDate = currentDate.timeInMillis
        mDatePicker.show()
        mDatePicker.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        mDatePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }


}