package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycapstone.data.BoundingBox
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class CameraViewModel : ViewModel(), ModelHelper.DetectorListener{
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
    }

    fun detect(bitmap: Bitmap, landmarks: List<NormalizedLandmark>) {
        try {
            if (::modelHelper.isInitialized) {
                modelHelper.detect(bitmap, landmarks)
            } else {
                Log.e(TAG, "ModelHelper not initialized")
                clearDetection() // Clear results if detector isn't initialized
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during detection: ${e.message}")
            clearDetection() // Clear results on error
        }
    }

    override fun onEmptyDetect() {
        clearDetection() // Use the new clearDetection method
    }
    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        _detectionResults.postValue(boundingBoxes)
        _inferenceTime.postValue(inferenceTime)

    }

    override fun onCleared() {
        super.onCleared()
        modelHelper.clear()
    }


    companion object {
        private const val MODEL_PATH = "model_v2.tflite"
        private const val LABEL_PATH = "labels.txt"
        private const val TAG = "CameraViewModel"
    }
}
