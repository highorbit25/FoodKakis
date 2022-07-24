package com.orbital.foodkakis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.orbital.foodkakis.databinding.ActivityFnbBinding

class FnbActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFnbBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var postAdapter: PostAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFnbBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.postRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        postArrayList = arrayListOf()

        postAdapter = PostAdapter(postArrayList)

        recyclerView.adapter = postAdapter

        eventChangeListener()

        // Logic for Navigation Bar
        binding.bottomNavigationView.selectedItemId = R.id.fnb
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

    private fun eventChangeListener() {
        //  Fetch data from Firestore and put inside arraylist
        db = FirebaseFirestore.getInstance()
        db.collection("posts").whereEqualTo("spotlight", true).limit(1)
            .get().addOnSuccessListener {
                // populate the spotlight card
                for(doc in it) {
                    Glide.with(this).load(doc.getString("image")!!.toUri()).into(binding.spotlightImage)
                    binding.spotlightHeader.text = doc.getString("header")
                    binding.spotlightValidity.text = doc.getString("validity")
                }


            }
        db.collection("posts").whereEqualTo("spotlight", false).whereEqualTo("active", true)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            // something went wrong
                            Log.e("Firestore Error", error.message.toString())
                            return
                        }
                        for (docChange : DocumentChange in value?.documentChanges!!) {
                            if (docChange.type == DocumentChange.Type.ADDED) {
                                // add to arraylist
                                postArrayList.add(docChange.document.toObject(Post::class.java))
                            }
                        }

                        postAdapter.notifyDataSetChanged()
                    }

                })
    }


}