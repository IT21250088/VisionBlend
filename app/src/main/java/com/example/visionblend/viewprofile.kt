package com.example.visionblend

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class viewprofile : AppCompatActivity() {

    private lateinit var showname: TextView
    private lateinit var showemail: TextView
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetectorCompat

    private var mScaleFactor = 0.5f

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_viewprofile)

        showname = findViewById(R.id.showname)
        showemail = findViewById(R.id.showemail)

        // Create a ScaleGestureDetector
        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())


        // Create a GestureDetector
        mGestureDetector = GestureDetectorCompat(this, GestureListener())

        // Get current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Reference to the user's data in Firebase
        currentUser?.uid?.let { uid ->
            val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the user data exists
                    if (dataSnapshot.exists()) {
                        // Extract user details
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        val email = dataSnapshot.child("email").getValue(String::class.java)

                        // Update the UI
                        showname.text = name ?: "N/A"
                        showemail.text = email ?: "N/A"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                }
            })
        }

        // Add logout functionality
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }

        val updateButton = findViewById<Button>(R.id.updatebutton)
        updateButton.setOnClickListener {
            updateUser()
        }
        findViewById<Button>(R.id.deleteProfileButton).setOnClickListener {
            deleteProfile()
        }

        val emailButton = findViewById<Button>(R.id.emailbutton)
        emailButton.setOnClickListener {
            sendEmail()
        }

    }



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
                val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_viewprofile)

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
                val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_viewprofile)

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
            val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_viewprofile)

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
            val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_viewprofile)

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

    private lateinit var tts: TextToSpeech
    override fun onStart() {
        super.onStart()
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
            }
        }
    }


    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Firebase sign out
        startActivity(Intent(this, LoginActivity::class.java)) // Redirect to login activity
        finish() // Finish current activity
        showToast("Logged out successfully")
        speakOut("Logged out successfully")
    }
    private fun updateUser() {
        val newName = showname.text.toString().trim()
        if (newName.isEmpty()) {
            speakOut("Name cannot be empty")
            showname.error = "Name cannot be empty"
            return
        }
        val newEmail = showemail.text.toString().trim() // Ensure email format validation in real app scenarios

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
            val updates = mapOf(
                "name" to newName,
                "email" to newEmail
            )

            userRef.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showname.setText(newName)
                    showemail.setText(newEmail)
                    // Optionally, show a success message to the user
                    showToast("Profile updated successfully")
                    speakOut("Profile updated successfully")
                } else {
                    // Optionally, show an error message to the user
                    showToast("Failed to update profile")
                    speakOut("Profile updated successfully")
                }
            }
        }
    }

    private fun deleteProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Delete user data from Firebase Database if necessary
            val uid = user.uid
            FirebaseDatabase.getInstance().getReference("users/$uid").removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Delete the user from Firebase Authentication
                    user.delete().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Profile deleted successfully")
                            speakOut("Profile deleted successfully")
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            showToast("Failed to delete profile")
                            speakOut("Failed to delete profile")
                        }
                    }
                } else {
                    showToast("Failed to delete user data")
                    speakOut("Failed to delete user data")
                }
            }
        }
    }
    private fun sendEmail() {
        val recipient = "visionblend@gmail.com"
        val subject = "Feedback/Inquiry"

        // Intent to send an email
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        // Attempt to directly open Gmail if installed
        val packageManager = packageManager
        val resolvedInfoList = packageManager.queryIntentActivities(emailIntent, 0)
        var gmailIntent: Intent? = null

        for (resolveInfo in resolvedInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName.contains("com.google.android.gm")) {
                emailIntent.setPackage(packageName)
                gmailIntent = emailIntent
                break
            }
        }

        // Start the Gmail intent if found, otherwise fallback to chooser
        if (gmailIntent != null && gmailIntent.resolveActivity(packageManager) != null) {
            startActivity(gmailIntent)
        } else {
            // Show a chooser to select an email client
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client:"))
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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