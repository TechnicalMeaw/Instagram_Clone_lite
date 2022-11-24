package com.example.instagramclonelite

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instagramclonelite.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    lateinit var mSignInClient: GoogleSignInClient
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottieAnimationView2.visibility = View.VISIBLE
        binding.lottieAnimationView2.playAnimation()

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("228287684122-179ikmggr2aqbp10sk44qab4vp3gc0gs.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Initialize sign in client
        mSignInClient = GoogleSignIn.getClient(
            this, googleSignInOptions
        );


        val animFadeInDown = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.fade_in_down);

        Handler().postDelayed({

            binding.usernameEditTextLabel.visibility = View.VISIBLE
            binding.passwordEditTextLabel.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.VISIBLE
            binding.signInWithGoogleBtn.visibility = View.VISIBLE

            binding.usernameEditTextLabel.startAnimation(animFadeInDown)
            binding.passwordEditTextLabel.startAnimation(animFadeInDown)
            binding.loginBtn.startAnimation(animFadeInDown)
            binding.signInWithGoogleBtn.startAnimation(animFadeInDown)

        }, 500)

        binding.signInWithGoogleBtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mSignInClient!!.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                }
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this, account.displayName, Toast.LENGTH_LONG).show()
            // Signed in successfully, show authenticated UI.

            if (account != null) {
                //                val personName: String = account.displayName!!
                //                val personGivenName: String = account.givenName!!
                //                val personFamilyName: String = account.familyName!!
                //                val personEmail: String = account.email!!
                //                val personId: String = account.id!!
                //                val personPhoto: Uri = account.photoUrl!!

                val token = account.idToken
                if (token != null) {
                    firebaseAuthWithGoogle(token)
                }
            } else {
                Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show()

            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
}