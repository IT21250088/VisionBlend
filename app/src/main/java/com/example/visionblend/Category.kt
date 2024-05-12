package com.example.visionblend
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.GestureDetector
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat


class Category : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val VOICE_RECOGNITION_REQUEST_CODE = 100
    private lateinit var tts: TextToSpeech
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var mic: ImageButton
    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetectorCompat

    private var mScaleFactor = 0.5f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Create a ScaleGestureDetector
        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())


        // Create a GestureDetector
        mGestureDetector = GestureDetectorCompat(this, GestureListener())


        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        textView1 = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        mic = findViewById(R.id.mic)

        // Find the button by its id
        val otherButton = findViewById<Button>(R.id.other)

        // Set OnClickListener to the button
        otherButton.setOnClickListener {
            // Create an Intent to navigate to the login activity
            val intent = Intent(this, LoginActivity::class.java)

            // Start the login activity
            startActivity(intent)
        }


        // Initialize the mic button
        mic.setOnClickListener {
            startVoiceRecognition()
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
                val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_category)

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
                val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_category)

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
            val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_category)

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
            val constraintLayout = findViewById<ConstraintLayout>(R.id.activity_category)

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



    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now...")
//            speakOut("Please speak now...")
        }
        try {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE)
        } catch (a: Exception) {
            Toast.makeText(applicationContext, "Your device does not support Speech Recognition", Toast.LENGTH_SHORT).show()
            speakOut("Your device does not support Speech Recognition")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0)?.toLowerCase(Locale.getDefault())

            when (spokenText) {
                "number one","one","number 1","1"-> {
                    textView1.performClick()
                    speakOut("You selected one.")
                }
                "number two","two","number 2","2"-> {
                    textView2.performClick()
                    speakOut("You selected two.")
                }
                "number three","three","number 3","3" -> {
                    textView3.performClick()
                    speakOut("You selected three.")
                }

                else -> speakOut("Unrecognized command: $spokenText")
            }
        }
    }

    private fun speakOut(text: String) {
        val speechRate = 1f // Adjust this value to change the speech rate
        tts.setSpeechRate(speechRate)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

        // Check the spoken text and navigate to the appropriate activity
        val themeId = when {
            text.contains("You selected one.") -> {
                navigateToLoginPage(R.style.Theme_VisionBlend_Monochromatism)
                R.style.Theme_VisionBlend_Monochromatism
            }
            text.contains("You selected two.") -> {
                navigateToLoginPage(R.style.Theme_VisionBlend_Tritanopia)
                R.style.Theme_VisionBlend_Tritanopia
            }
            text.contains("You selected three.") -> {
                navigateToLoginPage(R.style.Theme_VisionBlend_Deuteranopia)
                R.style.Theme_VisionBlend_Deuteranopia
            }

            else -> {}
        }
    }

    private fun navigateToLoginPage(themeId: Int) {
        // Store the selected theme in a shared preference
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt("themeId", themeId)
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                speakOut("Language not supported")
            }else {
                // If the TTS engine is successfully initialized, greet the user
                speakOut("Hi, welcome to Vision Blend! Please select a category by saying the number. number 1 for monochromatism people. number 2 for" +
                        " tritanopia people! number 3 for deuteranopia and protanopia people. for example, say number 1 to select monochromatism.Others can use other button. click on the mic button to start.")

            }
        } else {
            Toast.makeText(this, "TTS Initialization failed!", Toast.LENGTH_SHORT).show()
            speakOut("TTS Initialization failed!")
        }
    }

    override fun onDestroy() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }
}