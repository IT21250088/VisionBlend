package com.example.visionblend

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class viewprofile : AppCompatActivity() {

    private lateinit var txtFullName: TextView
    private lateinit var txtEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_viewprofile)

        txtFullName = findViewById(R.id.txtfullname)
        txtEmail = findViewById(R.id.txtEmail)

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
                        txtFullName.text = name ?: "N/A"
                        txtEmail.text = email ?: "N/A"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                }
            })
        }
    }
}
