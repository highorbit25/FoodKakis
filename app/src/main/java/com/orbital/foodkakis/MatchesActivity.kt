package com.orbital.foodkakis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants
import com.orbital.foodkakis.databinding.ActivityMatchesBinding
import kotlinx.android.synthetic.main.activity_matches.*


class MatchesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchesBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val intent = Intent(
//            this,
//            CometChatMessageListActivity::class.java
//        )
//        intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid())
//        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar())
//        intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.getStatus())
//        intent.putExtra(UIKitConstants.IntentStrings.NAME, user.getName())
//        intent.putExtra(UIKitConstants.IntentStrings.LINK, user.getLink())
//        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER)
//        startActivity(intent)

//        // Start chat between matched users
//        val receiverID:String="0KdVBNMH4dUPnKnw8VPMZg2Ij8l1"
//        val messageText:String="Hello world!"
//        val receiverType:String= CometChatConstants.RECEIVER_TYPE_USER
//
//        val textMessage = TextMessage(receiverID, messageText,receiverType)
//
//        CometChat.sendMessage(textMessage, object : CometChat.CallbackListener<TextMessage>() {
//            override fun onSuccess(p0: TextMessage?) {
//                Log.d(TAG, "Message sent successfully: " + p0?.toString())
//            }
//
//            override fun onError(p0: CometChatException?) {
//                Log.d(TAG, "Message sending failed with exception: " + p0?.message)          }
//
//        })

//        startActivity(Intent(this, CometChatConversationList::class.java))
        startActivity(Intent(this, CometChatUI::class.java))

//        CometChatConversationList.setItemClickListener(object : OnItemClickListener<Any>(){
//            override fun OnItemClick(t: Any, position: Int) {
//                Log.d("MatchesActivity", "Convo selected: $position")
//            }
//            override fun OnItemLongClick(t: Any, position: Int) {
//                super.OnItemLongClick(t, position)
//
//            }
//        })


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