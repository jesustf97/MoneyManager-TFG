package com.calleja.jesus.moneymanager.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.calleja.jesus.moneymanager.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        buttonGoBackLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        buttonRegistro.setOnClickListener{
            val email = editTextEmailSignUp.text.toString()
            val password = editTextPasswordSignUp.text.toString()

            if(verify(email, password)){

                create(email,password)
            }
            else{
                Toast.makeText(this, "Faltan campos de datos por rellenar o las contraseñas no coinciden",Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun create(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Se ha enviado email de confirmación",Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Ha ocurrido un error inesperado",Toast.LENGTH_SHORT).show()
                }
            }

    }



    private fun verify(email: String, password: String): Boolean {
        return !email.isNullOrEmpty() &&
                !password.isNullOrEmpty() &&
                (password.compareTo(editTextConfirmPasswordSignUp.toString())) == 0


    }



}
