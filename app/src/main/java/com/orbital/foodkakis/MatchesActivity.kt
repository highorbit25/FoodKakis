package com.orbital.foodkakis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.orbital.foodkakis.databinding.ActivityDashboardSwipeBinding
import com.orbital.foodkakis.databinding.ActivityMatchesBinding

class MatchesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logic for Navigation Bar
        binding.bottomNavigationView.selectedItemId = R.id.matches
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