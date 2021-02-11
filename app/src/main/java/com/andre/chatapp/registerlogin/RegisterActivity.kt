package com.andre.chatapp.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.messages.LatestMessagesActivity
import com.andre.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    var database = FirebaseDatabase.getInstance().reference

    companion object{
        const val TAG = "Testes"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //botão registar
        registerButton_register.setOnClickListener {

            performRegister()
        }

        //selecionar foto
        selectphoto_button.setOnClickListener {
            //abrir galeria de fotos
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        //Ir para a página de login
        haveAccount_textView.setOnClickListener {
            Log.d(TAG, "Try to show login activity")
            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    var selectedPhotoUri: Uri? = null
    //colocação da foto no registo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceder e verificar a imagem selecionada
            Log.d(TAG, "Foto selecionada")
            selectedPhotoUri = data.data //uri localização da foto
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button.alpha = 0f
        }
    }

    private fun performRegister() {
        registerButton_register.setOnClickListener {
            val username = username_editText_register.text.toString()
            val email = email_editText_register.text.toString()
            val password = password_editText_register.text.toString()

            if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
                Toast.makeText(this, "Enter text in all fields", Toast.LENGTH_SHORT ).show()
                return@setOnClickListener
            }
            //Firebase Authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if(!it.isSuccessful) return@addOnCompleteListener
                        Log.d(TAG, "Utilizador $username com email $email foi autenticado")
                        uploadImageToFirebaseStorage()
                    }
                    .addOnFailureListener{
                        Log.d(TAG, "O novo utilizador não foi autenticado")
                        Toast.makeText(this, "O novo utilizador não foi autenticado: ${it.message}", Toast.LENGTH_SHORT ).show()
                    }
        }
    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null)return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("imagens/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Upload de imagem bem sucessido: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG,"Localização da fotografia: $it")
                        saveUserToFirebaseDatabase(it)
                    }
                }
                .addOnFailureListener{
                    Log.d(TAG, "Upload de imagem falhou")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: Uri) {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val user = User(uid, username_editText_register.text.toString(), email_editText_register.text.toString(), password_editText_register.text.toString(), profileImageUrl.toString())

        database.child("/users/$uid").setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Finally we saved the user to Firebase Database")
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")
                }
    }


}