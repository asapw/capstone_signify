package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycapstone.data.BoundingBox
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

    private val _handSignResult = MutableLiveData<String>()
    val handSignResult: LiveData<String> = _handSignResult

    private lateinit var modelHelper: ModelHelper
    private var originalBitmap: Bitmap? = null

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

    fun detectHandSign(
        handLandmarkerResult: HandLandmarkerResult,
        inputBitmap: Bitmap
    ) {
        originalBitmap = inputBitmap

        if (handLandmarkerResult.landmarks().isNotEmpty()) {
            val landmarks = handLandmarkerResult.landmarks()[0]

            // Adjust landmarks based on bitmap dimensions
            val width = inputBitmap.width
            val height = inputBitmap.height

            // Create matrix for potential transformations
            val matrix = Matrix()
            matrix.postScale(1f, 1f)

            // Crop and resize to 32x32 for model input
            val handBitmap = Bitmap.createScaledBitmap(
                inputBitmap, 32, 32, true
            )

            // Detect hand sign
            modelHelper.detect(handBitmap)
        }
    }
    override fun onEmptyDetect() {
        _detectionResults.postValue(emptyList())
        _handSignResult.postValue("")
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        _detectionResults.postValue(boundingBoxes)
        _inferenceTime.postValue(inferenceTime)

        boundingBoxes.firstOrNull()?.let {
            _handSignResult.postValue(it.clsName)
        }
    }

    override fun onCleared() {
        super.onCleared()
        modelHelper.clear()
    }


    companion object {
        private const val MODEL_PATH = "model.tflite"
        private const val LABEL_PATH = "labels.txt"
    }
}
