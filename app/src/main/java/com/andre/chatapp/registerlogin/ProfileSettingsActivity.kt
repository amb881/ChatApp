package com.andre.chatapp.registerlogin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
        val TAG = "Profile Settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        fetchCurrentUser()
    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d(TAG,"Currente user: ${currentUser?.username}")
                val username = currentUser?.username
                profile_username_textView.text = username

                val email = currentUser?.email
                profile_email_textView.text = email

                val password = currentUser?.password
                profile_password_textView.text = password


                Picasso.get().load(currentUser?.profileImageUrl).into(profilePicture_imageView)

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}