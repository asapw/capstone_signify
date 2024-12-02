package com.example.mycapstone.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.example.mycapstone.data.BoundingBox
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ModelHelper(
    private val context: Context,
    private val modelPath: String,
    private val labelPath: String,
    private val detectorListener: DetectorListener
) {

    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()

    private var tensorWidth = 0
    private var tensorHeight = 0
    private var numChannel = 0

    private val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    init {
        try {
            Log.d("ModelHelper", "Model loaded successfully.")
        } catch (e: Exception) {
            Log.e("ModelHelper", "Failed to load model: ${e.message}")
        }
    }

    fun setup() {
        val model = FileUtil.loadMappedFile(context, modelPath)
        val options = Interpreter.Options()
        options.numThreads = 4
        interpreter = Interpreter(model, options)

        val inputShape = interpreter?.getInputTensor(0)?.shape() ?: return
        val outputShape = interpreter?.getOutputTensor(0)?.shape() ?: return

        tensorWidth = inputShape[1]
        tensorHeight = inputShape[2]
        numChannel = outputShape[1]

        // Load labels from asset file
        try {
            val inputStream: InputStream = context.assets.open(labelPath)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = reader.readLine()
            while (line != null && line != "") {
                labels.add(line)
                line = reader.readLine()
            }

            reader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun clear() {
        interpreter?.close()
        interpreter = null
    }



    fun detect(frame: Bitmap, landmarks: List<NormalizedLandmark>) {
        interpreter ?: return

        var inferenceTime = SystemClock.uptimeMillis()

        try {
            // Crop the hand region first before resizing
            val handRegion = cropHandRegion(frame, landmarks)

            // Resize the cropped region to model input size
            val resizedBitmap = Bitmap.createScaledBitmap(handRegion, 32, 32, true)

            // Convert the bitmap into TensorImage
            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(resizedBitmap)

            // Adjust normalization parameters if needed
            val processedImage = imageProcessor.process(tensorImage)

            val output = TensorBuffer.createFixedSize(intArrayOf(1, 26), DataType.FLOAT32)
            interpreter?.run(processedImage.buffer, output.buffer)

            val bestBoxes = bestBox(landmarks, output.floatArray)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            if (bestBoxes == null) {
                detectorListener.onEmptyDetect()
                return
            }

            detectorListener.onDetect(bestBoxes, inferenceTime)
        } catch (e: Exception) {
            Log.e("ModelHelper", "Error during detection: ${e.message}")
            detectorListener.onEmptyDetect()
        }
    }

    private fun cropHandRegion(frame: Bitmap, landmarks: List<NormalizedLandmark>): Bitmap {
        // Calculate hand bounding box with padding
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE

        for (landmark in landmarks) {
            minX = minOf(minX, landmark.x())
            minY = minOf(minY, landmark.y())
            maxX = maxOf(maxX, landmark.x())
            maxY = maxOf(maxY, landmark.y())
        }

        // Add padding (20%)
        val padding = 0.2f
        val width = maxX - minX
        val height = maxY - minY
        val paddingX = width * padding
        val paddingY = height * padding

        // Convert normalized coordinates to pixel coordinates
        val frameWidth = frame.width
        val frameHeight = frame.height

        val x1 = maxOf(0f, (minX - paddingX) * frameWidth).toInt()
        val y1 = maxOf(0f, (minY - paddingY) * frameHeight).toInt()
        val x2 = minOf(frameWidth.toFloat(), (maxX + paddingX) * frameWidth).toInt()
        val y2 = minOf(frameHeight.toFloat(), (maxY + paddingY) * frameHeight).toInt()

        // Crop the hand region
        return Bitmap.createBitmap(
            frame,
            x1, y1,
            x2 - x1, y2 - y1
        )
    }

    private fun bestBox(
        landmarks: List<NormalizedLandmark>,
        array: FloatArray
    ): List<BoundingBox>? {
        val boundingBoxes = mutableListOf<BoundingBox>()

        try {
            // Use palm center (landmark 0) as the center point
            val palmCenter = landmarks[0]

            // Calculate the bounding box dimensions by finding min/max coordinates
            var minX = Float.MAX_VALUE
            var minY = Float.MAX_VALUE
            var maxX = Float.MIN_VALUE
            var maxY = Float.MIN_VALUE

            // Check all landmarks to find the extremes
            for (landmark in landmarks) {
                minX = minOf(minX, landmark.x())
                minY = minOf(minY, landmark.y())
                maxX = maxOf(maxX, landmark.x())
                maxY = maxOf(maxY, landmark.y())
            }

            // Find the class with maximum confidence from the 26 outputs
            var maxConf = -1.0f
            var maxIdx = -1

            // Iterate through the 26 class probabilities
            for (i in array.indices) {
                if (array[i] > maxConf) {
                    maxConf = array[i]
                    maxIdx = i
                }
            }

            if (maxConf > CONFIDENCE_THRESHOLD && maxIdx < labels.size) {
                val clsName = labels[maxIdx]

                // Add padding to the box (20% of width/height)
                val padding = 0.2f
                val width = maxX - minX
                val height = maxY - minY
                val paddingX = width * padding
                val paddingY = height * padding

                val x1 = maxOf(0f, minX - paddingX)
                val y1 = maxOf(0f, minY - paddingY)
                val x2 = minOf(1f, maxX + paddingX)
                val y2 = minOf(1f, maxY + paddingY)

                // Calculate final width and height
                val w = x2 - x1
                val h = y2 - y1

                boundingBoxes.add(
                    BoundingBox(
                        x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                        cx = palmCenter.x(), cy = palmCenter.y(),
                        w = w, h = h,
                        cnf = maxConf, cls = maxIdx, clsName = clsName
                    )
                )
            }

            return boundingBoxes.ifEmpty { null }

        } catch (e: Exception) {
            Log.e("ModelHelper", "Error in bestBox: ${e.message}")
            return null
        }
    }
    private fun applyNMS(boxes: List<BoundingBox>): MutableList<BoundingBox> {
        val sortedBoxes = boxes.sortedByDescending { it.cnf }.toMutableList()
        val selectedBoxes = mutableListOf<BoundingBox>()

        while (sortedBoxes.isNotEmpty()) {
            val first = sortedBoxes.first()
            selectedBoxes.add(first)
            sortedBoxes.remove(first)

            val iterator = sortedBoxes.iterator()
            while (iterator.hasNext()) {
                val nextBox = iterator.next()
                val iou = calculateIoU(first, nextBox)
                if (iou >= IOU_THRESHOLD) {
                    iterator.remove()
                }
            }
        }

        return selectedBoxes
    }

    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        val x1 = maxOf(box1.x1, box2.x1)
        val y1 = maxOf(box1.y1, box2.y1)
        val x2 = minOf(box1.x2, box2.x2)
        val y2 = minOf(box1.y2, box2.y2)
        val intersectionArea = maxOf(0F, x2 - x1) * maxOf(0F, y2 - y1)
        val box1Area = box1.w * box1.h
        val box2Area = box2.w * box2.h
        return intersectionArea / (box1Area + box2Area - intersectionArea)
    }

    interface DetectorListener {
        fun onEmptyDetect()
        fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long)
    }

    companion object {
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 255f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.3F
        private const val IOU_THRESHOLD = 0.5F
    }
}
