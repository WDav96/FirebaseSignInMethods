package com.example.firebasetutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasetutorial.databinding.HomeActivityBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class HomeActivity: AppCompatActivity() {

    /** Private Properties */

    private lateinit var binding: HomeActivityBinding

    /** Life Cycle Activity Override Methods */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Activity
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        setupActivity(email ?: "", provider ?: "")

        if (provider != null) {
            initUI(provider)
        }
    }

    /** Private Methods */

    private fun initUI(provider: String) {
        binding.logOutButton.setOnClickListener {
            if (provider == "Facebook") {
                logOutFacebook()
            } else {
                logOutGoogle()
            }
        }
    }

    private fun setupActivity(email: String, provider: String) {
        binding.mailTextView.text = email
        binding.providerTextView.text = provider

    }

    private fun logOutGoogle() {
        FirebaseAuth.getInstance().signOut()
        goToLogin()
    }

    private fun logOutFacebook() {
        LoginManager.getInstance().logOut()
        goToLogin()
    }

    private fun goToLogin() {
        val homeIntent = Intent(this,LoginActivity::class.java).apply {

        }
        startActivity(homeIntent)
    }

}