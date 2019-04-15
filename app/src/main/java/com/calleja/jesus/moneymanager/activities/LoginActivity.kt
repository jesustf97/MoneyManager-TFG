package com.calleja.jesus.moneymanager.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.validate
import com.calleja.jesus.moneymanager.validateEmail
import com.calleja.jesus.moneymanager.validatePass
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
            val pass: String = editTextPasswordLogin.text.toString()
            if(validateEmail(email) && validatePass(pass)) {
                loginWithEmail(email,pass)
            }
            else{
                Toast.makeText(this,
                    "Faltan campos de datos por rellenar o las contraseñas no coinciden",Toast.LENGTH_SHORT).show()
            }
        }

        textViewForgotPassword.setOnClickListener{
            startActivity(Intent(this, ForgotPassActivity::class.java))
        }

        buttonSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        editTextEmailLogin.validate {
            editTextEmailLogin.error = if (validateEmail(it)) null else
                "El email no es válido"
        }

        editTextPasswordLogin.validate {
            editTextPasswordLogin.error = if (validatePass(it)) null else
                "La contraseña debe contener al menos 6 carácteres con al menos una minúscula, una mayúscula y un número"
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (mAuth.currentUser!!.isEmailVerified) {
                    Toast.makeText(this, "El usuario ha iniciado sesión", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "El usuario aún no ha verificado su email", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(this, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}