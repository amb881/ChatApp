package com.andre.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        var database = FirebaseDatabase.getInstance().reference


        //botão registar
        registerButton_register.setOnClickListener {
            var username = username_editText_register.text.toString()
            var email = email_editText_register.text.toString()
            var password = password_editText_register.text.toString()

            database.child("/users/$username").setValue(Users(email,password))
        }

        //selecionar foto
        selectphoto_button.setOnClickListener {
            //abrir galeria de fotos
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    //colocação da foto no registo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var selectedPhotoUri: Uri? = null
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceder e verificar a imagem selecionada
            Log.d("Registo", "Foto selecionada")
            selectedPhotoUri = data.data //uri localização da foto
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button.alpha = 0f
        }
    }


}