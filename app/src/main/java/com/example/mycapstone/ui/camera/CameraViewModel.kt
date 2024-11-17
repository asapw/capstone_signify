package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycapstone.data.BoundingBox

class CameraViewModel : ViewModel(), Detector.DetectorListener {

    private val _detectionResults = MutableLiveData<List<String>>()
    val detectionResults: LiveData<List<String>> = _detectionResults

    private val _inferenceTime = MutableLiveData<Long>()
    val inferenceTime: LiveData<Long> = _inferenceTime

    private lateinit var detector: Detector

    fun initializeDetector(context: Context) {
        // Initialize the detector with the model
        detector = Detector(context, "model.tflite", "labels.txt", this)
        detector.setup()
    }

    fun detect(bitmap: Bitmap) {
        // Detect hand sign from the camera feed
        detector.detect(bitmap)
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        _detectionResults.postValue(boundingBoxes.map { it.clsName })  // Show class names
        _inferenceTime.postValue(inferenceTime)
    }

    override fun onEmptyDetect() {
        _detectionResults.postValue(emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        detector.clear()
    }
}
