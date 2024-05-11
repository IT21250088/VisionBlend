package com.example.visionblend

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
//    private lateinit var tvRedirectLogin: TextView
    private lateinit var etName: EditText

    // Create Firebase authentication object
    private lateinit var auth: FirebaseAuth

    //private lateinit var database: DatabaseReference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        // Retrieve the theme from the shared preferences
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        val themeId = sharedPref.getInt("themeId", R.style.Theme_VisionBlend)
        // Set the theme
        setTheme(themeId)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // View Bindings
        etEmail = findViewById(R.id.etSEmailAddress)
        etConfPass = findViewById(R.id.etSConfPassword)
        etPass = findViewById(R.id.etSPassword)
        btnSignUp = findViewById(R.id.btnSSigned)
        etName = findViewById(R.id.etSUsername)

        // Initialize auth object
        auth = FirebaseAuth.getInstance()

        // Initialize database object
        database = FirebaseDatabase.getInstance().reference

        // Set the button color based on the current theme
        when (themeId) {
            R.style.Theme_VisionBlend -> btnSignUp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorDefault)
            R.style.Theme_VisionBlend_Monochromatism -> btnSignUp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorMono)
            R.style.Theme_VisionBlend_Tritanopia -> btnSignUp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorTritan)
            R.style.Theme_VisionBlend_Deuteranopia -> btnSignUp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.buttonColorDeuteran)
            // Add more cases if you have more themes
        }

        btnSignUp.setOnClickListener {
            signUpUser()
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

    private fun signUpUser() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()

        // Check if email,name, password fields are blank
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Fields cannot be blank", Toast.LENGTH_SHORT).show()
            speakOut("Fields cannot be blank")
            return
        }
        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            speakOut("Please enter a valid email address")
            return
        }
        // Ensure the password is at least 6 characters long
        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            speakOut("Password must be at least 6 characters")
            return
        }

        // Check if password and confirm password match
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            speakOut("Password and Confirm Password do not match")
            return
        }

        // If all credentials are correct, attempt to sign up
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Save name and email to the database
                val userId = auth.currentUser?.uid
                userId?.let {
                    val user = hashMapOf("name" to name, "email" to email)
                    database.child("users").child(it).setValue(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Signed Up and user info saved", Toast.LENGTH_SHORT).show()
                            speakOut("Successfully Signed Up and user info saved")
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to save user info", Toast.LENGTH_SHORT).show()
                            speakOut("Failed to save user info")
                        }
                    }
                } ?: run {
                    Toast.makeText(this, "Sign Up Successful but failed to save user info", Toast.LENGTH_SHORT).show()
                    speakOut("Sign Up Successful but failed to save user info")
                }
            } else {
                Toast.makeText(this, "Sign Up Failed!", Toast.LENGTH_SHORT).show()
                speakOut("Sign Up Failed!")
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
