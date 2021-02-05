package com.andre.chatapp.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_user_information.*

class EditUserInformationActivity : AppCompatActivity() {

    companion object{
        val TAG = "EditProfile"
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
        val user = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("/users/${user?.uid}")
        val newUsername = editProfile_username_editText.text.toString()
        val newEmail = editProfile_email_editText.text.toString()
        val newPassword = editProfile_password_editText.text.toString()

        val credential = EmailAuthProvider
                .getCredential(currentEmail_editText.text.toString(), currentPassword_editText.text.toString())
        user?.reauthenticate(credential)?.addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }

        if (newUsername.isEmpty()){
            null
        } else {
            ref.child("username").setValue(newUsername)
            Log.d(TAG, "Username updated to $newUsername")
        }
        if (newEmail.isEmpty()){
            null
        }else{
            user!!.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User email address updated.")
                            ref.child("email").setValue(newEmail)
                        }
                    }
        }
        if (newPassword.isEmpty()){
            null
        } else{
            user!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User password updated.")
                            ref.child("password").setValue(newPassword)
                        }
                    }
        }

    }
}