package com.andre.chatapp.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.messages.LatestMessagesActivity.Companion.currentUserEmail
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_user_information.*
import java.util.*

class EditUserInformationActivity : AppCompatActivity() {

    companion object{
        const val TAG = "Testes"
        private var photoSelected: Boolean = false
        val user = FirebaseAuth.getInstance().currentUser
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


        profile_uploadPicture_button.setOnClickListener {
            //abrir galeria de fotos
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        editProfile_cancel_button.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceder e verificar a imagem selecionada
            Log.d(TAG, "Foto selecionada")
            selectedPhotoUri = data.data //uri localização da foto
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            EditProfilePicture_imageView.setImageBitmap(bitmap)
            photoSelected = true
            if (photoSelected){
                if (selectedPhotoUri == null)return
                val filename = UUID.randomUUID().toString()
                val refImage = FirebaseStorage.getInstance().getReference("imagens/$filename")
                refImage.putFile(selectedPhotoUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            Log.d(RegisterActivity.TAG, "Upload de imagem bem sucessido: ${taskSnapshot.metadata?.path}")
                            refImage.downloadUrl.addOnSuccessListener {
                                Log.d(RegisterActivity.TAG,"Localização da fotografia: $it")
                                editUser(it)
                            }
                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Upload de imagem falhou")
                        }
            }
        }
    }


    private fun editUser(profileImageUrl: Uri? = null) {

        val ref = FirebaseDatabase.getInstance().getReference("/users/${user?.uid}")
        val newUsername = editProfile_username_editText.text.toString()
        val newEmail = editProfile_email_editText.text.toString()
        val newPassword = editProfile_password_editText.text.toString()

        val credential = EmailAuthProvider
                .getCredential(currentUserEmail, currentPassword_editText.text.toString())
        user?.reauthenticate(credential)?.addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }

        if (newUsername.isEmpty()){
            null
        } else {
            ref.child("username").setValue(newUsername)
            Log.d(TAG, "Username updated to $newUsername")
        }
        if (newEmail.isEmpty()){
            Log.d(TAG, "Email not updated")
        }else{
            user!!.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            ref.child("email").setValue(newEmail)
                            Log.d(TAG, "User email address updated to ${user?.email}")
                            currentUserEmail  = newEmail
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
        if (profileImageUrl != null){
            ref.child("profileImageUrl").setValue(profileImageUrl.toString())
        } else {
            null
        }


    }




}