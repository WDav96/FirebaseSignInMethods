package com.example.firebasetutorial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetutorial.databinding.CellPhoneRegisterActivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class CellPhoneActivity: AppCompatActivity()  {

    /** Internal Properties */

    var storedVerificationId = ""

    /** Private Properties */

    private lateinit var binding: CellPhoneRegisterActivityBinding

    private lateinit var auth: FirebaseAuth

    private val TAG = "CellPhoneActivity"

    /** Life Cycle Activity Override Methods */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CellPhoneRegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        initUI()
    }


    /** Private Methods */

    private fun initUI() {
        binding.sendCodeButton.setOnClickListener {
            sendCode()
        }
        binding.verifyCodeButton.setOnClickListener {
            verifiyCode()
        }
    }

    private fun sendCode() {
        if (binding.cellPhoneEditText.text.isNotEmpty()) {
            val phoneNumber = binding.cellPhoneEditText.text.toString()
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+57$phoneNumber")       // Phone number to verify
                .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            auth.setLanguageCode("es")
            PhoneAuthProvider.verifyPhoneNumber(options)
            Toast.makeText(this, "The code has been sent, in a few moments you will receive it", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Campo de teléfono vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifiyCode() {
        if (binding.codeEditText.text.isNotEmpty()) {
            val code = binding.codeEditText.text.toString()
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)

            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result?.user

                        Toast.makeText(this, "Cell phone number account is logged", Toast.LENGTH_SHORT).show()
                        goToHome(task.result?.user?.phoneNumber.toString(), "Cell phone")
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, "The verification of the code was failed", Toast.LENGTH_SHORT).show()
                        }
                        // Update UI
                    }
                }

        } else {
            Toast.makeText(this, "Debe ingresar un código de verificación", Toast.LENGTH_SHORT).show()
        }
    }

    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")

            //signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            // resendToken = token
        }
    }

    private fun goToHome(email: String, provider: String) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }

}