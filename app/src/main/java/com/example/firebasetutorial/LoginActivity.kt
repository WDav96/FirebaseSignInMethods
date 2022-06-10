package com.example.firebasetutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebasetutorial.databinding.LoginActivityBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity: AppCompatActivity() {

    /** Private Properties */

    private lateinit var binding: LoginActivityBinding

    private val GOOGLE_SIGN_IN = 100

    private val callbackManager = CallbackManager.Factory.create()

    /** Life Cycle Activity Override Methods */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    /** Override Methods */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode,  resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Google account is logged", Toast.LENGTH_SHORT).show()
                            goToHome(it.result?.user?.email ?: "", "Google")
                        } else {
                            Toast.makeText(this, "Google log in error!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error: No se pudo obtener la cuenta", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /** Private Methods */

    private fun initUI() {
        binding.logInButton.setOnClickListener {
            loginEmail()
        }
        binding.registerButton.setOnClickListener {
            register()
        }
        binding.googleButton.setOnClickListener {
            loginGoogle()
        }
        binding.facebookButton.setOnClickListener {
            loginFacebook()
        }
        binding.cellPhoneButton.setOnClickListener {
            goToCellPhoneRegister()
        }
    }

    private fun loginEmail() {
        if (binding.mailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.mailEditText.text.toString(), binding.passwordEditText.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Email account is logged", Toast.LENGTH_SHORT).show()
                    goToHome(it.result?.user?.email ?: "", "Email")
                } else {
                    Toast.makeText(this, "Email log in error!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Campo de usuario o contraseña vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun register() {
        if (binding.mailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.mailEditText.text.toString(), binding.passwordEditText.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Register account error!", Toast.LENGTH_SHORT).show()
                }
            }
        }  else {
            Toast.makeText(this, "Campo de usuario o contraseña vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginGoogle() {
        // Configuration
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Auth Client
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        // show google screen auth
        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    private fun loginFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

         LoginManager.getInstance().registerCallback(callbackManager,
             object : FacebookCallback<LoginResult> {

                 override fun onSuccess(result: LoginResult?) {

                     result?.let {

                         val token = it.accessToken

                         val credential = FacebookAuthProvider.getCredential(token.token)
                         FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                             if (it.isSuccessful) {
                                 Toast.makeText(this@LoginActivity, "Facebook account is logged", Toast.LENGTH_SHORT).show()
                                 goToHome(it.result?.user?.email ?: "", "Facebook")
                             } else {
                                 Toast.makeText(this@LoginActivity, "Facebook log in error!", Toast.LENGTH_SHORT).show()
                             }
                         }
                     }
                 }

                 override fun onCancel() {
                     Toast.makeText(this@LoginActivity, "Facebook log in was cancelled!", Toast.LENGTH_SHORT).show()
                 }

                 override fun onError(error: FacebookException?) {
                     Toast.makeText(this@LoginActivity, "Facebook log in error!", Toast.LENGTH_SHORT).show()
                 }
         })
    }

    private fun goToHome(email: String, provider: String) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }

    private fun goToCellPhoneRegister() {
        val intent = Intent(this,CellPhoneActivity::class.java).apply {

        }
        startActivity(intent)
    }

}