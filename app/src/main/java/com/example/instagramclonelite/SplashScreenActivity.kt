package com.example.instagramclonelite

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import com.example.instagramclonelite.databinding.ActivitySplashScreenBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // App Check
        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )


        val animFadeIn = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.fade_in_up);

        Handler().postDelayed({
            binding.splashTextView.visibility = View.VISIBLE
            binding.splashTextView.startAnimation(animFadeIn)
        }, 500)


        // Intent animation options
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.splashTextView, "instagramIcon")

        Handler().postDelayed({

            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = Firebase.auth.currentUser
            if (currentUser != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent, options.toBundle())
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent, options.toBundle())
            }
        }, 2000)

        Handler().postDelayed({
            finish()
        }, 2750)
    }
}