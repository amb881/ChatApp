package com.andre.chatapp.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.messages.LatestMessagesActivity.Companion.currentUserEmail
import com.andre.chatapp.messages.LatestMessagesActivity.Companion.currentUserProfileImageUrl
import com.andre.chatapp.messages.LatestMessagesActivity.Companion.currentUserUsername
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {

    companion object{
        const val TAG = "Testes"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        Log.d(TAG, "O URL é $currentUserProfileImageUrl")
        profile_email_textView.text = currentUserEmail
        profile_username_textView.text = currentUserUsername
        Picasso.get().load(currentUserProfileImageUrl).into(ProfilePicture_imageView)

        editProfile_button.setOnClickListener {
            val intent = Intent(this, EditUserInformationActivity::class.java)
            startActivity(intent)
        }
    }
}