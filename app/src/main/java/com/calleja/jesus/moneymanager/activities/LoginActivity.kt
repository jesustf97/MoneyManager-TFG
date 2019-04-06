package com.calleja.jesus.moneymanager.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.calleja.jesus.moneymanager.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val mAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        buttonShowLicenses.setOnClickListener{
            startActivity(Intent(this, ShowLicensesActivity::class.java))
        }

        loginButton.setOnClickListener{
            val email: String = editTextEmailLogin.text.toString()
            val password: String = editTextPasswordLogin.text.toString()
            if(verify(email, password)){
                loginEmail(email,password)
            }
        }

        textViewForgotPassword.setOnClickListener{
            startActivity(Intent(this, ForgetPassActivity::class.java))
        }

        buttonSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun loginEmail(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "El usuario ha iniciado sesión",Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Ha ocurrido un error inesperado",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verify(email: String, password: String): Boolean {
        return !email.isNullOrEmpty() && !password.isNullOrEmpty()
    }
}