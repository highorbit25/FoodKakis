package com.orbital.foodkakis

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.devstune.searchablemultiselectspinner.SearchableItem
import com.devstune.searchablemultiselectspinner.SearchableMultiSelectSpinner
import com.devstune.searchablemultiselectspinner.SelectionCompleteListener
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orbital.foodkakis.databinding.ActivityDashboardBinding
import java.text.SimpleDateFormat
import java.util.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mAuth: FirebaseAuth
    private val currentDate: Calendar = Calendar.getInstance()
    private var year = currentDate[Calendar.YEAR]
    private var month = currentDate[Calendar.MONTH]
    private var day = currentDate[Calendar.DAY_OF_MONTH]
    private var chipsCounter = 3
    private val cravingsArray = ArrayList<String>()
    private val swipedRightOnArray = ArrayList<String>()
    private val swipedLeftOnArray = ArrayList<String>()
    private val interestedArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser
        var selectedMode = "null"

        val currentUserUid = mAuth.currentUser?.uid.toString()
        val db = Firebase.firestore
        val activeReq = db.collection("users").document(currentUserUid)

//        readCSV()

//         check if there is existing request, if there is redirect to DashboardSwipeActivity
        activeReq.get()
            .addOnSuccessListener { document ->
                if (document.get("active_request") == true) {
                    Toast.makeText(this, "Active Request detected, Swipe Away!", Toast.LENGTH_SHORT).show()
                    val swipeIntent = Intent(this, DashboardSwipeActivity::class.java)
                    startActivity(swipeIntent)
                    Log.d("DashboardActivity", "Active Request detected, goto DashboardSwipeActivity")
                } else {
                    Log.d("DashboardActivity", "No Active Request detected, allow user to create new request")
                }
        }

        binding.selectFoodkakis.setOnClickListener {
            selectedMode = "FoodKaki"
            binding.selectFoodkakis.setBackgroundColor(resources.getColor(R.color.blue_50))
            binding.modeFoodkakis.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.selectCorpconnect.setBackgroundColor(resources.getColor(R.color.white))
            binding.modeCorpconnect.setTextColor(resources.getColor(R.color.black))
            binding.selectSurprise.setBackgroundColor(resources.getColor(R.color.white))
            binding.modeSurpriseme.setTextColor(resources.getColor(R.color.black))
            Log.d("DashboardActivity", "FoodKakis mode selected")
        }

        binding.selectCorpconnect.setOnClickListener {
            selectedMode = "CorpConnect"
            binding.selectCorpconnect.setBackgroundColor(resources.getColor(R.color.blue_50))
            binding.modeCorpconnect.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.selectSurprise.setBackgroundColor(resources.getColor(R.color.white))
            binding.modeSurpriseme.setTextColor(resources.getColor(R.color.black))
            binding.selectFoodkakis.setBackgroundColor(resources.getColor(R.color.white))
            binding.modeFoodkakis.setTextColor(resources.getColor(R.color.black))
            Log.d("DashboardActivity", "CorpConnect mode selected")
        }

        binding.selectSurprise.setOnClickListener {
            selectedMode = "Surprise"
            binding.selectSurprise.setBackgroundColor(resources.getColor(R.color.blue_50))
            binding.modeSurpriseme.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.selectCorpconnect.setBackgroundColor(resources.getColor(R.color.white))
            binding.modeCorpconnect.setTextColor(resources.getColor(R.color.black))
            binding.selectFoodkakis.setBackgroundColor(resources.getColor(R.color.white))
            binding.modeFoodkakis.setTextColor(resources.getColor(R.color.black))
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


        // Android Searchable Multi Select Spinner
//        val items = ArrayList<SearchableItem>()
        val items = arrayListOf<SearchableItem>(
            SearchableItem("Western Cuisine", "0"),
            SearchableItem("Indian Cuisine", "1"),
            SearchableItem("Chinese Cuisine", "2"),
            SearchableItem("Malay Cuisine", "3"),
            SearchableItem("Japanese Cuisine", "4"),
            SearchableItem("Korean Cuisine", "5"),
            SearchableItem("Italian Cuisine", "6"),
            SearchableItem("Mexican Cuisine", "7"),
            SearchableItem("Thai Cuisine", "8"),
            SearchableItem("Indo Cuisine", "9"),
            SearchableItem("Spicy Food", "10"),
            SearchableItem("Soups", "11"),
            SearchableItem("Rice", "12"),
            SearchableItem("Noodles", "13"),
            SearchableItem("Dessert", "14"),
            SearchableItem("Coffee", "15"),
            SearchableItem("Bubble Tea", "16"),
            SearchableItem("Deep Fried", "17"),
            SearchableItem("Vegetarian", "18"),
            SearchableItem("Seafood", "19`"),
            SearchableItem("Chicken", "20"),
            SearchableItem("Duck", "21"),
            SearchableItem("Beef", "22"),
            SearchableItem("Mutton", "23"),
            SearchableItem("Fast Food", "24"),
            SearchableItem("Cakes", "25")
        )
//        for (i in 0..20) {
//            items.add(SearchableItem("Item $i", "$i"))
//        }
        binding.cravingsSelection.setOnClickListener {
            SearchableMultiSelectSpinner.show(this, "Select Items","Done", items, object :
                SelectionCompleteListener {
                override fun onCompleteSelection(selectedItems: ArrayList<SearchableItem>) {
                    if (selectedItems.size > 3) {
                        Toast.makeText(applicationContext, "Max of 3 cravings", Toast.LENGTH_SHORT).show()
                        Log.w("Cravings", "Exceeded 3 cravings quota")
                    } else {
                        Log.e("data", selectedItems.toString())
                        for(item in selectedItems) {
                            if(chipsCounter > 0) {
                                createMyChips(item.text)
                                cravingsArray.add(item.text)
                            }
                            else {
                                Toast.makeText(applicationContext, "Max of 3 cravings", Toast.LENGTH_SHORT).show()
                                Log.w("Cravings", "Exceeded 3 cravings quota")
                            }
                        }
                    }

                }

            })
        }





        fun isEmpty(etText: EditText ): Boolean {
            return etText.getText().toString().trim().length == 0;
        }



        binding.findBtn.setOnClickListener {
            if (emptySelection) {
                Toast.makeText(applicationContext, "Select Timeslot!", Toast.LENGTH_SHORT).show()
                Log.d("FindButton", "Timeslot not selected")
            }
            else if (selectedMode == "null") {
                Toast.makeText(applicationContext, "Select Mode!", Toast.LENGTH_SHORT).show()
                Log.d("FindButton", "Mode not selected")
            }
            else if (isEmpty(binding.availDateFill)) {
                Toast.makeText(applicationContext, "Select Date!", Toast.LENGTH_SHORT).show()
                Log.d("FindButton", "Date not selected")
            }
            else if (!emptySelection && selectedMode != "null") {
                val date = binding.availDateFill.text.toString().replace('/','.')
                val storeAt = db.collection(selectedMode).document(date)
                    .collection(timeSlot.toString()).document(currentUserUid)
                val request = hashMapOf(
                    "swiped_right_on" to swipedRightOnArray,
                    "swiped_left_on" to swipedLeftOnArray,
                    "interested_list" to interestedArray,
                    "successful" to false,
                    "cravings" to cravingsArray
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

                // save current active request within 'user'
                val userRequest = hashMapOf(
                    "selected_mode" to selectedMode,
                    "date" to date,
                    "timeslot" to timeSlot,
                    "cravings" to cravingsArray,
                    "active_request" to true
                )
                db.collection("users").document(currentUserUid)
                        // merge to existing data
                    .set(userRequest, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(
                            "DashboardActivity",
                            "Active request stored for: $currentUserUid\" "
                        )

                        // Wipe swiped_on array for user when new request is created
                        val deleteSwipedArray = hashMapOf<String, Any>(
                            "swiped_on" to arrayListOf<String>()
                        )
                        db.collection("users").document(currentUserUid).update(deleteSwipedArray).addOnCompleteListener {
                            Log.d("Wiped swiped_on", "Removed swiped_on array for $currentUserUid")
                        }

                        // Wipe swiped_right & swiped_left sub-collections for user when new request is created
                        db.collection("users").document(currentUserUid).collection("swiped_right").get()
                            .addOnSuccessListener { docs ->
                                for(doc in docs.documents) {
                                    doc.reference.delete()
                                }
                                Log.d("Wiped swiped_right", "Removed swiped_right collection for $currentUserUid")
                            }
                        db.collection("users").document(currentUserUid).collection("swiped_left").get()
                            .addOnSuccessListener { docs ->
                                for(doc in docs.documents) {
                                    doc.reference.delete()
                                }
                                Log.d("Wiped swiped_left", "Removed swiped_left collection for $currentUserUid")
                            }

                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "DashboardActivity",
                            "Error writing active request",
                            e
                        )
                    }

                // move to DashboardSwipeActivity
                val swipeIntent= Intent(this, DashboardSwipeActivity::class.java)
                startActivity(swipeIntent)
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

        mDatePicker.datePicker.minDate = currentDate.timeInMillis

        mDatePicker.show()
        mDatePicker.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        mDatePicker.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun createMyChips(txt: String) {
        val chip = Chip(this)
        chip.setEnsureMinTouchTargetSize(true)
        chip.apply {
            chipsCounter--
            text = txt
            chipIcon = ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.ic_launcher_background)
            isChipIconVisible = false
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            binding.cravingEntries.addView(chip as View)
            setOnCloseIconClickListener {
                binding.cravingEntries.removeView(chip as View)
                cravingsArray.remove(chip.text)
                chipsCounter++
            }
        }
    }

//    fun readCSV() {
//
//
//        csvReader().open("res/assets/food.csv") {
//
//            readAllAsSequence().forEach { row ->
//
//                for (e in row) {
//                    print("$e ")
//                }
//
//                println()
//            }
//        }
//    }

    // Old method for manual input of cravings
//    private fun entryChips() {
//        binding.addButton.setOnClickListener {
//            if (chipsCounter > 0) {
//                val cravings = binding.cravingSearch.text.toString()
//                createMyChips(cravings)
//                cravingsArray.add(cravings)
//                binding.cravingSearch.text.clear()
////                chipsCounter--
//            } else {
//                Toast.makeText(this, "Max of 3 cravings", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }



}
