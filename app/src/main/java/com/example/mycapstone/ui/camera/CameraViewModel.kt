package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycapstone.data.BoundingBox
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel(), ModelHelper.DetectorListener{
    private var _signLanguangeWords = MutableLiveData<String>()
    val signLanguangeWords: LiveData<String> = _signLanguangeWords

    private val words = StringBuilder()
    private var lastDetectionTime = 0L

    private var _delegate: Int = HandLandMarkerHelper.DELEGATE_CPU
    private var _minHandDetectionConfidence: Float =
        HandLandMarkerHelper.DEFAULT_HAND_DETECTION_CONFIDENCE
    private var _minHandTrackingConfidence: Float = HandLandMarkerHelper
        .DEFAULT_HAND_TRACKING_CONFIDENCE
    private var _minHandPresenceConfidence: Float = HandLandMarkerHelper
        .DEFAULT_HAND_PRESENCE_CONFIDENCE
    private var _maxHands: Int = HandLandMarkerHelper.DEFAULT_NUM_HANDS

    val currentDelegate: Int get() = _delegate
    val currentMinHandDetectionConfidence: Float
        get() =
            _minHandDetectionConfidence
    val currentMinHandTrackingConfidence: Float
        get() =
            _minHandTrackingConfidence
    val currentMinHandPresenceConfidence: Float
        get() =
            _minHandPresenceConfidence
    val currentMaxHands: Int get() = _maxHands

    private val _detectionResults = MutableLiveData<List<BoundingBox>>()
    val detectionResults: LiveData<List<BoundingBox>> = _detectionResults

    private val _inferenceTime = MutableLiveData<Long>()
    val inferenceTime: LiveData<Long> = _inferenceTime


    private var isProcessing = false
    private lateinit var modelHelper: ModelHelper

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setMinHandDetectionConfidence(confidence: Float) {
        _minHandDetectionConfidence = confidence
    }
    fun setMinHandTrackingConfidence(confidence: Float) {
        _minHandTrackingConfidence = confidence
    }
    fun setMinHandPresenceConfidence(confidence: Float) {
        _minHandPresenceConfidence = confidence
    }

    fun setMaxHands(maxResults: Int) {
        _maxHands = maxResults
    }

    fun initializeDetector(context: Context) {
        modelHelper = ModelHelper(context, MODEL_PATH, LABEL_PATH, this)
        modelHelper.setup()
    }



    private fun clearDetection() {
        _detectionResults.postValue(emptyList())
        _inferenceTime.postValue(0)
        words.clear()
        _signLanguangeWords.postValue("")
        lastDetectionTime = 0L
    }

    fun detect(bitmap: Bitmap, landmarks: List<NormalizedLandmark>) {
        try {
            if (::modelHelper.isInitialized && !isProcessing) {
                val currTime = System.currentTimeMillis()
                if(currTime - lastDetectionTime >= DETECTION_DELAY){
                    isProcessing = true
                    viewModelScope.launch {
                        modelHelper.detect(bitmap, landmarks)
                        isProcessing = false
                        lastDetectionTime = currTime
                    }
                }
            } else if(!::modelHelper.isInitialized){
                Log.e(TAG,"ModelHelper is not initialized")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during detection: ${e.message}")
            clearDetection()
        }
    }



    override fun onEmptyDetect() {
    }
    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        _detectionResults.postValue(boundingBoxes)
        _inferenceTime.postValue(inferenceTime)

        val detectedWord = boundingBoxes.firstOrNull()?.clsName ?: ""
        if (detectedWord.isNotEmpty()) {
            // Append new word with space
            if (words.isNotEmpty()) {
                words.append(" ")
            }

            words.append(detectedWord)

            Log.d(TAG, "Accumulated words: $words")
            _signLanguangeWords.postValue(words.toString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        modelHelper.clear()
    }


    companion object {
        private const val MODEL_PATH = "model_v2.tflite"
        private const val LABEL_PATH = "labels.txt"
        private const val TAG = "CameraViewModel"
        private const val DETECTION_DELAY = 1000L
    }
}
