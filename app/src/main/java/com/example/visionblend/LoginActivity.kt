package com.example.visionblend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // Retrieve the theme from the shared preferences
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        val themeId = sharedPref.getInt("themeId", R.style.Theme_VisionBlend)
        // Set the theme
        setTheme(themeId)


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
            // Add more cases if you have more themes
        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            // using finish() to end the activity
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


//    private fun login() {
//        val email = etEmail.text.toString()
//        val pass = etPass.text.toString()
//        // calling signInWithEmailAndPassword(email, pass)
//        // function using Firebase auth object
//        // On successful response Display a Toast
//        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
//            if (it.isSuccessful) {
//                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
//                speakOut("Successfully LoggedIn")
//                val intent = Intent(this, viewprofile::class.java)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
//                speakOut("Log In failed")
//
//            }
//        }
//    }
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
            val intent = Intent(this, viewprofile::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Log In failed", Toast.LENGTH_SHORT).show()
            speakOut("Log In failed")
        }
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