package com.andre.chatapp.registerlogin

import android.content.Intent
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
        val TAG = "Testes"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        Log.d(TAG,"Email fectch")
        fetchCurrentUser()

        editProfile_button.setOnClickListener {
            val intent = Intent(this, EditUserInformationActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            //reloadActivity()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                Log.d(TAG, "YOOOOOOOOOOOOOOOOOOO: ${user?.username},${user?.email}")
                val username = user?.username
                profile_username_textView.text = username
                profile_email_textView.text = user?.email
                Picasso.get().load(user?.profileImageUrl).into(EditProfilePicture_imageView)
                Log.d(TAG, "YIIIIIIIIIIIIIIII: ${user?.username},${user?.email}")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}