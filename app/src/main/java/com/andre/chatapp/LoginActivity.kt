package com.andre.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        //Fazer login
        login_button_login.setOnClickListener {
            val email = email_editText_login.text.toString()
            val password = password_editText_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if(!it.isSuccessful) return@addOnCompleteListener
                        Log.d("Login", "Login feito")
                        val intent = Intent(this, LatestMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener{
                        Log.d("Main", "Failed to create user: ${it.message}")
                    }
        }

        //redirecionar para a página de login
        backToRegister_textView.setOnClickListener {
            finish()
        }
    }
}