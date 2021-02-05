package com.andre.chatapp.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
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
        editProfile_button.setOnClickListener {
            val intent = Intent(this, EditUserInformationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        button3.setOnClickListener {
            reloadActivity()
        }

    }

    private fun reloadActivity(){
        val intent = Intent(this, ProfileSettingsActivity::class.java)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun fetchCurrentUser() {

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d(EditUserInformationActivity.TAG, "o meu email é ${user.email}")
        } else {
            Log.d(EditUserInformationActivity.TAG,"Não funciona")
        }



//        val uid = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                currentUser = snapshot.getValue(User::class.java)
//                Log.d(TAG, "Currente user: ${currentUser?.username}")
//                val username = currentUser?.username
//                profile_username_textView.text = username
//
//                val email = currentUser?.email
//                profile_email_textView.text = email
//
//                val password = currentUser?.password
//                profile_password_textView.text = password
//
//
//                Picasso.get().load(currentUser?.profileImageUrl).into(profilePicture_imageView)
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//        ref.addChildEventListener(object: ChildEventListener{
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val user = snapshot.getValue(User::class.java) ?: return
//                Log.d(TAG, "Currente user: ${currentUser?.username}")
//                val username = currentUser?.username
//                profile_username_textView.text = username
//
//                val email = currentUser?.email
//                profile_email_textView.text = email
//
//                val password = currentUser?.password
//                profile_password_textView.text = password
//
//                Picasso.get().load(currentUser?.profileImageUrl).into(profilePicture_imageView)
//
//                hash[snapshot.key!!] = user
//                refresh()
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                currentUser = snapshot.getValue(User::class.java)
//                Log.d(TAG, "Currente user: ${currentUser?.username}")
//                val username = currentUser?.username
//                profile_username_textView.text = username
//
//                val email = currentUser?.email
//                profile_email_textView.text = email
//
//                val password = currentUser?.password
//                profile_password_textView.text = password
//
//                Picasso.get().load(currentUser?.profileImageUrl).into(profilePicture_imageView)
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })
    }
}