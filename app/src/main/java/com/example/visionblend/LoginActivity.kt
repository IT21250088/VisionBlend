package com.example.visionblend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.GestureDetector
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat


class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetectorCompat
    private var mScaleFactor = 0.5f


    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // Retrieve the theme from the shared preferences
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        val themeId = sharedPref.getInt("themeId", R.style.Theme_VisionBlend)
        // Set the theme
        setTheme(themeId)


        // Create a ScaleGestureDetector
        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())


        // Create a GestureDetector
        mGestureDetector = GestureDetectorCompat(this, GestureListener())


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View Binding
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            login()
        }

        // Set the button color based on the current theme
        when (themeId) {
            R.style.Theme_VisionBlend -> btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorDefault)
            R.style.Theme_VisionBlend_Monochromatism -> btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorMono)
            R.style.Theme_VisionBlend_Tritanopia -> btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorTritan)
            R.style.Theme_VisionBlend_Deuteranopia -> btnLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorDeuteran)

        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //   voice out put
    private lateinit var tts: TextToSpeech
    override fun onStart() {
        super.onStart()
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
            }
        }
    }


    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        // Check if email field is empty
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            speakOut("Please enter your email address")
            return
        }

        // Check if password field is empty
        if (pass.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            speakOut("Please enter your password")
            return
        }

        // Check if email is empty or does not contain '@' symbol
        if (!email.contains('@')) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            speakOut("Please enter a valid email address")
            return
        }

        // Check if password is less than 6 characters
        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            speakOut("Password must be at least 6 characters long")
            return
        }

        // Perform Firebase authentication only if all fields pass validation
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                speakOut("Successfully Logged In")
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Log In failed", Toast.LENGTH_SHORT).show()
                speakOut("Log In failed")
            }
        }
    } // This is the missing closing brace

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events
        mScaleGestureDetector.onTouchEvent(event)

        // Let the GestureDetector inspect all events
        mGestureDetector.onTouchEvent(event)

        return true
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // Only allow movement if the view is magnified
            if (mScaleFactor > 1.0f) {
                // Get the ConstraintLayout
                val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_login)

                // Iterate over all child views of the ConstraintLayout
                for (i in 0 until constraintLayout.childCount) {
                    val child = constraintLayout.getChildAt(i)

                    // Calculate the new translations
                    val newTranslationX = child.translationX - distanceX
                    val newTranslationY = child.translationY - distanceY


                    // Limit the translations to certain bounds
                    val maxTranslation = 200.0f // Change this value to set the maximum allowed movement
                    child.translationX = Math.max(-maxTranslation, Math.min(newTranslationX, maxTranslation))
                    child.translationY = Math.max(-maxTranslation, Math.min(newTranslationY, maxTranslation))
                }
            }

            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Toggle between actual size and magnified size
            mScaleFactor = if (mScaleFactor > 1.0f) {
                // Get the ConstraintLayout
                val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_login)

                // Iterate over all child views of the ConstraintLayout
                for (i in 0 until constraintLayout.childCount) {
                    val child = constraintLayout.getChildAt(i)

                    // Reset the translations of the child view
                    child.translationX = 0f
                    child.translationY = 0f
                }

                1.0f
            } else {
                1.5f // Change this value to control the magnification level
            }

            // Get the ConstraintLayout
            val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_login)

            // Iterate over all child views of the ConstraintLayout
            for (i in 0 until constraintLayout.childCount) {
                val child = constraintLayout.getChildAt(i)

                // Apply the scaling to the child view
                child.scaleX = mScaleFactor
                child.scaleY = mScaleFactor
            }

            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 1.5f)) // Set the minimum scale factor to 1.0f

            // Get the ConstraintLayout
            val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_login)

            // Iterate over all child views of the ConstraintLayout
            for (i in 0 until constraintLayout.childCount) {
                val child = constraintLayout.getChildAt(i)

                // Apply the scaling to the child view
                child.scaleX = mScaleFactor
                child.scaleY = mScaleFactor
            }

            return true
        }
    }

    fun resetPassword(view: View) {
        // getting email from the user
        val email = etEmail.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            speakOut("Please enter your email")
        } else {
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    speakOut("Email sent. Please check your email for further instructions.")
                } else {
                    Toast.makeText(this, "Error sending email", Toast.LENGTH_SHORT).show()
                    speakOut("Error sending email")
                }
            }
        }
    }

    private fun speakOut(text: String) {
        val speechRate = 1f
        tts.setSpeechRate(speechRate)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    override fun onDestroy() {
        // Shutdown Text-to-Speech engine
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }

}