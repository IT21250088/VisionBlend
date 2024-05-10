package com.example.visionblend.Magnification

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.TextView

class magnify(private val textView: TextView) {

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private var mScaleFactor = 1.0f

    init {
        initScaleGestureDetector()
    }

    private fun initScaleGestureDetector() {
        val context: Context = textView.context
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    fun magnifyTextView() {
        textView.setOnTouchListener { _, event ->
            mScaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    fun onTouchEvent(event: MotionEvent?) {
        if (event != null) {
            mScaleGestureDetector.onTouchEvent(event)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = mScaleFactor.coerceIn(0.1f, 5.0f)
            return true
        }
    }
}