package com.example.visionblend

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class viewprofile : AppCompatActivity() {

    private lateinit var showname: TextView
    private lateinit var showemail: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_viewprofile)

        showname = findViewById(R.id.showname)
        showemail = findViewById(R.id.showemail)

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