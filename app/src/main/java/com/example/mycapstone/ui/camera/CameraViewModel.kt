package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycapstone.api.ApiConfig
import com.example.mycapstone.api.ModelConfig
import com.example.mycapstone.data.BoundingBox
import com.example.mycapstone.utils.ImageConvert
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CameraViewModel : ViewModel(), ModelHelper.DetectorListener {
    private var _signLanguangeWords = MutableLiveData<String>()
    val signLanguangeWords: LiveData<String> = _signLanguangeWords

    private val words = StringBuilder()
    private var lastDetectionTime = 0L
    private var isProcessing = false

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
        get() = _minHandDetectionConfidence
    val currentMinHandTrackingConfidence: Float
        get() = _minHandTrackingConfidence
    val currentMinHandPresenceConfidence: Float
        get() = _minHandPresenceConfidence
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

     fun clearDetection() {
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
                if (currTime - lastDetectionTime >= DETECTION_DELAY) {
                    isProcessing = true
                    viewModelScope.launch {
                        detectWithCloudModel(bitmap)
                        isProcessing = false
                        lastDetectionTime = currTime
                    }
                }
            } else if (!::modelHelper.isInitialized) {
                Log.e(TAG, "ModelHelper is not initialized")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during detection: ${e.message}")
            clearDetection()
        }
    }

    private suspend fun detectWithCloudModel(bitmap: Bitmap) {
        try {
            val tempFile = ImageConvert.bitmapToMultipart(bitmap)

            val startTime = System.currentTimeMillis()
            val res = withContext(Dispatchers.IO) {
                ModelConfig.getModelService().detectSignLanguange(tempFile)
            }

            val inferenceTime = System.currentTimeMillis() - startTime

            if (res.isSuccessful) {
                val resBody = res.body()
                val detectedWord = resBody?.detection ?: ""
                val message = resBody?.message ?: ""
                val timestampString = resBody?.timestamp?.toString() ?: ""
                val image = resBody?.image ?: ""

                Log.d(TAG, "Detected word from API: $detectedWord")

                if (detectedWord.isNotEmpty()) {
                    if (words.isNotEmpty()) {
                        words.append(" ")
                    }
                    words.append(detectedWord)
                    Log.d(TAG, "Accumulated words: $words")
                    _signLanguangeWords.postValue(words.toString())
                    Log.d(TAG, "Posted sign language words: ${words.toString()}")
                } else {
                    Log.d(TAG, "Detected word is empty")
                }

                Log.d(TAG, "API Message: $message")
                Log.d(TAG, "API Timestamp: $timestampString")
            } else {
                Log.e(TAG, "API Error:  ${res.code()} - ${res.message()}")
                clearDetection()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cloud API detection Error: ${e.message}")
            clearDetection()
        }
    }

    override fun onEmptyDetect() {}

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        _detectionResults.postValue(boundingBoxes)
        _inferenceTime.postValue(inferenceTime)

        val detectedWord = boundingBoxes.firstOrNull()?.clsName ?: ""
        if (detectedWord.isNotEmpty()) {
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
        private const val MODEL_PATH = "model_v4.tflite"
        private const val LABEL_PATH = "labels.txt"
        private const val TAG = "CameraViewModel"
        private const val DETECTION_DELAY = 1000L
    }
}