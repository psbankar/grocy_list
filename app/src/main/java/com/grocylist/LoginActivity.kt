package com.grocylist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    lateinit var signInWithGoogleButton: SignInButton
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true
    lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
//    lateinit var emailInput: TextInputEditText
//    lateinit var passwordInput: TextInputEditText
//    lateinit var loginButtom: Button
//    lateinit var registerButtom: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
//        emailInput = findViewById(R.id.email_input)
//        passwordInput = findViewById(R.id.password_input)
//        loginButtom = findViewById(R.id.button_login)
//        registerButtom = findViewById(R.id.button_register)
        signInWithGoogleButton = findViewById(R.id.sign_in_button)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        auth = Firebase.auth


        signInWithGoogleButton.setOnClickListener {
            signIn()
        }

//        registerButtom.setOnClickListener {
//            val tempEmail = emailInput.text.toString().trim()
//            val tempPassword = passwordInput.text.toString().trim()
//
//            if (tempEmail.length == 0) {
//                Toast.makeText(
//                    baseContext, "Enter Email",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (tempPassword.length == 0) {
//                Toast.makeText(
//                    baseContext, "Enter Password",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                auth.createUserWithEmailAndPassword(tempEmail, tempPassword)
//                    .addOnCompleteListener(this) { task ->
//                        if (task.isSuccessful) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success")
//                            val user = auth.currentUser
//                            updateUI(user)
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                            Toast.makeText(baseContext, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show()
//                            updateUI(null)
//                        }
//                    }
//            }
//        }
//
//        loginButtom.setOnClickListener {
//            val tempEmail = emailInput.text.toString().trim()
//            val tempPassword = passwordInput.text.toString().trim()
//
//            if (tempEmail.length == 0) {
//                Toast.makeText(
//                    baseContext, "Enter Email",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (tempPassword.length == 0) {
//                Toast.makeText(
//                    baseContext, "Enter Password",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                auth.signInWithEmailAndPassword(tempEmail, tempPassword)
//                    .addOnCompleteListener(this) { task ->
//                        if (task.isSuccessful) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success")
//                            val user = auth.currentUser
//                            updateUI(user)
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.exception)
//                            Toast.makeText(
//                                baseContext, task.exception?.localizedMessage,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            updateUI(null)
//                        }
//                    }
//            }
//        }


    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Welcome " + user.displayName, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}

