package com.orbital.foodkakis

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityDashboardBinding
import java.text.SimpleDateFormat
import java.util.*


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mAuth: FirebaseAuth
    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser
        var selectedMode = "null"

        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore



        binding.selectFoodkakis.setOnClickListener {
            selectedMode = "FoodKaki"
            Log.d("DashboardActivity", "FoodKaki mode selected")
        }

        binding.selectCorpconnect.setOnClickListener {
            selectedMode = "CorpConnect"
            Log.d("DashboardActivity", "CorpConnect mode selected")
        }

        binding.selectSurprise.setOnClickListener {
            selectedMode = "Surprise"
            Log.d("DashboardActivity", "SurpriseMe mode selected")
        }


        binding.availDateFill.setOnClickListener {
            selectDate()
        }

        val spinnerTimeFrom: Spinner = binding.timeFromSpinner
        var emptySelection = true
        var timeSlot: String? = null
        val timeFromAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(
                this,
                R.array.one_hour_intervals,
                android.R.layout.simple_spinner_item
            )
        timeFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeFrom.adapter = timeFromAdapter


        spinnerTimeFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    timeSlot = parent.getItemAtPosition(position).toString()
                }
                emptySelection = false
            }
        }

        binding.findBtn.setOnClickListener {
            if (!emptySelection && selectedMode != "null") {
                val date = binding.availDateFill.text.toString().replace('/','.')
                val storeAt = db.collection(selectedMode).document(date)
                    .collection(timeSlot.toString()).document(currentUserUid)
                val request = hashMapOf(
                    "swiped_right_on" to null,
                    "swiped_left_on" to null,
                    "successful" to false
                )
                storeAt
                    .set(request)
                    .addOnSuccessListener {
                        Log.d(
                            "DashboardActivity",
                            "Request stored for: $currentUserUid\" "
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "DashboardActivity",
                            "Error writing request",
                            e
                        )
                    }

                // move to MatchesActivity
                val intent = Intent(this, MatchesActivity::class.java)
                startActivity(intent)
                finish()
            }
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
                binding.availDateFill.text = Editable.Factory.getInstance().newEditable(sdf.format(currentDate.time))
            }, year, month, day
        )

        mDatePicker.show()
        mDatePicker.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        mDatePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }


}
