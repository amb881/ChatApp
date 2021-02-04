package com.andre.chatapp.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_user_information.*

class EditUserInformationActivity : AppCompatActivity() {

    companion object{
        val TAG = "EditProfile"
        val currentUser = FirebaseAuth.getInstance().currentUser
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_information)

        editProfile_save_button.setOnClickListener {
            editUser()
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun editUser() {

        val ref = FirebaseDatabase.getInstance().getReference("/users/${currentUser?.uid}")
        Log.d(TAG, "${currentUser?.uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val newUsername = editProfile_username_editText.text.toString()
                if (newUsername.isEmpty()){
                    null
                } else {
                    ref.child("username").setValue(newUsername)
                    Log.d(TAG, "Username updated to $newUsername")
                }

                val newEmail = editProfile_email_editText.text.toString()
                if (newEmail.isEmpty()){
                    null
                }else{
                    ref.child("email").setValue(newEmail)
                }

                val newPassword = editProfile_password_editText.text.toString()
                if (newEmail.isEmpty()){
                    null
                }else{
                    ref.child("password").setValue(newPassword)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}