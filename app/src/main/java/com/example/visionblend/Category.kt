package com.example.visionblend

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class Category : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val VOICE_RECOGNITION_REQUEST_CODE = 100
    private lateinit var tts: TextToSpeech
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var mic: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        textView1 = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        mic = findViewById(R.id.mic)


        // Initialize the mic button
        mic.setOnClickListener {
            startVoiceRecognition()
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
        if (text.contains("You selected one.")) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        if (text.contains("You selected two.")) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        if (text.contains("You selected three.")) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

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
                        " tritanopia people! number 3 for deuteranopia and protanopia people. for example, say number 1 to select monochromatism. click on the mic button to start.")

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