package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandLandMarkerHelper(
    var minHandDetectionConfidence: Float = DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = DEFAULT_HAND_PRESENCE_CONFIDENCE,
    var maxNumHands: Int = DEFAULT_NUM_HANDS,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.IMAGE,
    val context: Context,
    // this listener is only used when running in RunningMode.LIVE_STREAM
    val handLandmarkerHelperListener: LandmarkerListener? = null
)  {

    private var handLandmarker: HandLandmarker? = null

    init {
        setupHandLandmarker()
    }

    fun clearHandLandmarker() {
        handLandmarker?.close()
        handLandmarker = null
    }

    fun isClose(): Boolean {
        return handLandmarker == null
    }

    fun setupHandLandmarker() {
        val baseOptionBuilder = BaseOptions.builder()

        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionBuilder.setDelegate(Delegate.CPU)
            }

            DELEGATE_GPU -> {
                baseOptionBuilder.setDelegate(Delegate.GPU)
            }
        }

        baseOptionBuilder.setModelAssetPath(MP_HAND_LANDMARKER_TASK)

        when (runningMode) {

            RunningMode.LIVE_STREAM -> {
                if(handLandmarkerHelperListener == null) {
                    throw IllegalStateException(
                        "handlandmarkerHGelperListener must not be null when running in RunningMode.LIVE_STREAM"
                    )
                }
            }
            else ->  {
                // no-op
            }

        }

        try  {
            val baseOptions = baseOptionBuilder.build()

            val optionsBuilder =
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(baseOptions)
                    .setMinHandDetectionConfidence(minHandDetectionConfidence)
                    .setMinTrackingConfidence(minHandTrackingConfidence)
                    .setMinHandPresenceConfidence(minHandPresenceConfidence)
                    .setNumHands(maxNumHands)
                    .setRunningMode(runningMode)

            // The ResultListener and ErrorListener only use for LIVE_STREAM mode
            if (runningMode == RunningMode.LIVE_STREAM) {
                optionsBuilder
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
            }

            val options = optionsBuilder.build()
            handLandmarker =
                HandLandmarker.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            handLandmarkerHelperListener?.onError(
                "Hand Landmarker failed to initialize. See error logs for details"
            )
            e.printStackTrace()
        } catch (e: RuntimeException) {
            handLandmarkerHelperListener?.onError(
                "Hand Landmarker failed to initialize. See error logs for " +
                        "details", GPU_ERROR
            )
            Log.e(
                TAG,
                "Image classifier failed to load model with error: " + e.message
            )
        }
    }

    fun detectLivestream(
        imageProxy: ImageProxy,
        isFrontCamera: Boolean
    ){
        if(runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalStateException(
                "Attempting to call detectLivestream() while not using RunningMode.LIVE_STREAM")
        }
        val frameTime = SystemClock.uptimeMillis()

        // copy the RGB bits from frame to a bitmap buffer
        val bitmapBuffer =
            Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix= Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            if(isFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        detectAsync(mpImage, frameTime, rotatedBitmap)  // Pass rotatedBitmap here
    }

    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long, bitmap: Bitmap){  // Add bitmap parameter
        handLandmarker?.detectAsync(mpImage, frameTime)
        currentBitmap = bitmap  // Store the bitmap
        Log.d("HandLandmarkerHelper", "Running inference")
    }

    // Add property to store current bitmap
    private var currentBitmap: Bitmap? = null

    private fun returnLivestreamResult(
        result: HandLandmarkerResult,
        input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        currentBitmap?.let { bitmap ->
            handLandmarkerHelperListener?.onResults(
                ResultBundle(
                    listOf(result),
                    inferenceTime,
                    input.height,
                    input.width,
                    bitmap
                )
            )
        }
    }

    // Return errors thrown during detection to this HandLandmarkerHelper's
    // caller
    private fun returnLivestreamError(error: RuntimeException) {
        handLandmarkerHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }



    companion object {
        const val TAG = "HandLandmarkerHelper"
        private const val MP_HAND_LANDMARKER_TASK = "hand_landmarker.task"
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_HAND_DETECTION_CONFIDENCE = 0.2F
        const val DEFAULT_HAND_TRACKING_CONFIDENCE = 0.2F
        const val DEFAULT_HAND_PRESENCE_CONFIDENCE = 0.2F
        const val DEFAULT_NUM_HANDS = 1
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
    }

    data class ResultBundle(
        val results: List<HandLandmarkerResult>,
        val inferenceTime: Long,
        val inputImageHeight: Int,
        val inputImageWidth: Int,
        val inputBitmap: Bitmap

    )

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(resultBundle: ResultBundle)
    }

}