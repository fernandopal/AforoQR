package es.fernandopal.aforoqr.ui.activity
/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.fernandopal.aforoqr.MainActivity
import es.fernandopal.aforoqr.extensions.*
import es.fernandopal.aforoqr.databinding.ActivityLoginBinding
import java.lang.Exception


class LoginActivity : AppCompatActivity() {

    private val LOGIN = "LOGIN"
    private val REGISTER = "REGISTER"

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private var action: String = LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email
        val password = binding.password
        val confirmPassword = binding.confirmPassword
        val name = binding.name

        binding.btnSend.setOnClickListener {

            validateName(name)
            validateEmail(email)
            validatePassword(password)

            if(action == REGISTER) {
                confirmPassword(password, confirmPassword)

                if (name.error == null && email.error == null && password.error == null && confirmPassword.error == null) {
                    try {
                        createAccount(email.text.toString(), password.text.toString(), name.text.toString())
                    } catch (exception: Exception) {
                        password.error = "Datos de inicio de sesión inválidos"
                    }
                }
            }

            if (action == LOGIN && email.error == null && password.error == null) {
                signIn(email.text.toString(), password.text.toString())
            }
        }

        binding.btnToggleAction.setOnClickListener {
            action = if (action == LOGIN) REGISTER else LOGIN

            if(action == LOGIN) {
                name.visibility = View.GONE
                confirmPassword.visibility = View.GONE
                binding.btnSend.text = "Iniciar sesión"
                binding.btnToggleAction.text = "¿Aún no tienes una cuenta? Registrate aquí"

            } else {
                name.visibility = View.VISIBLE
                confirmPassword.visibility = View.VISIBLE
                binding.btnSend.text = "Crear cuenta"
                binding.btnToggleAction.text = "¿Ya dispones de un usuario? Inicia sesión"

            }
        }
    }

    public override fun onStart() {
        super.onStart()
        if(auth.currentUser != null) {
            openMainActivity()
        }
    }

    /** fpalomo - Validaciones para los campos de login/registro **/
    private fun validateName(name: EditText) {
        name.validate("Debes introducir tu nombre") {
            it.isNotBlank()
        }
    }

    private fun validateEmail(email: EditText) {
        email.validate("Por favor introduce una dirección de correo electrónico") {
            it.isNotBlank()
        }
    }
    private fun validatePassword(password: EditText) {
        password.validate("Por favor introduce una contraseña") {
            it.isNotBlank()
        }
        if(password.error == null) {
            password.validate("La contraseña debe tener al menos 6 cáracteres") {
                password.text.length >= 6
            }
        }
    }
    private fun confirmPassword(password: EditText, confirmPassword: EditText) {
        confirmPassword.validate("Las contraseñas no coinciden") {
            it.isNotBlank() && password.text.toString() == confirmPassword.text.toString()
        }
    }

    /** fpalomo - Metodo encargado de registrar un nuevo usuario **/
    private fun createAccount(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                // fpalomo 24/04/2022 - Actualizamos el perfil del usuario para guardar su nombre
                val profileBuilder = UserProfileChangeRequest.Builder()
                profileBuilder.displayName = name

                auth.currentUser?.updateProfile(profileBuilder.build())

                // fpalomo - Abrimos la actividad principal y destruimos la de login
                sendEmailVerification()
            } else {
                binding.email.error = it.exception?.message ?: "Error desconocido al registrar tu cuenta"
            }
        }
    }


    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { authResult ->
            if (authResult.isSuccessful) {
                openMainActivity()
            } else {
                binding.password.error = authResult.exception?.message ?: "Error desconocido al iniciar sesión"
            }
        }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser!!
        user.sendEmailVerification().addOnCompleteListener(this) {
            openMainActivity()
        }
    }



    private fun openMainActivity() {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener { getTokenResult ->
            Log.e("TOKEN", getTokenResult.token.toString())
        }

        val myIntent = Intent(this@LoginActivity, MainActivity::class.java)
        this@LoginActivity.startActivity(myIntent)
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}