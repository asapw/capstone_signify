package com.example.mycapstone.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentCameraBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val cameraViewModel: CameraViewModel by viewModels()

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Initialize detector in ViewModel
        cameraViewModel.initializeDetector(requireContext())

        // Observe LiveData
        setupObservers()

        // Request camera permission and start camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide bottom navigation when camera fragment is displayed
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        // Set up back button listener
        binding.backButton.setOnClickListener {
            // Navigate back using the NavController
            findNavController().popBackStack()  // Pop the current fragment off the back stack
        }
    }


    private fun setupObservers() {
        cameraViewModel.detectionResults.observe(viewLifecycleOwner, Observer { results ->
            // Handle detection results (e.g., display recognized sign language)
            binding.textOutput.text = "Detected: ${results.joinToString()}"
        })

        cameraViewModel.inferenceTime.observe(viewLifecycleOwner, Observer { time ->
            // Display inference time for performance monitoring
            Log.d("InferenceTime", "Inference time: $time ms")
        })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview = Preview.Builder()
            .setTargetRotation(rotation)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            processImage(imageProxy)
        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
            preview?.surfaceProvider = binding.viewFinder.surfaceProvider
        } catch (exc: Exception) {
            Log.e("CameraFragment", "Use case binding failed", exc)
        }
    }

    private fun processImage(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy) // Safe conversion to Bitmap
            cameraViewModel.detect(bitmap) // Send the Bitmap to the detection model
        } catch (e: Exception) {
            Log.e("CameraFragment", "Error processing image", e)
        } finally {
            // Close ImageProxy only after processing
            imageProxy.close()
        }
    }


    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val format = imageProxy.format
        Log.d("CameraFragment", "Image format: $format")

        return when (format) {
            ImageFormat.YUV_420_888 -> convertYUV420ToBitmap(imageProxy)
            ImageFormat.NV21 -> convertNV21ToBitmap(imageProxy)
            else -> throw IllegalStateException("Unsupported image format: $format")
        }
    }

    // Convert NV21 to Bitmap (existing logic)
    private fun convertNV21ToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer: ByteBuffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        // Convert YUV to JPEG
        val yuvImage = YuvImage(bytes, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val outStream = ByteArrayOutputStream()
        val rect = Rect(0, 0, imageProxy.width, imageProxy.height)
        yuvImage.compressToJpeg(rect, 100, outStream)

        // Convert JPEG byte array to Bitmap
        val jpegByteArray = outStream.toByteArray()
        return BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)
    }

    // Convert YUV_420_888 to Bitmap (as added earlier)
    private fun convertYUV420ToBitmap(imageProxy: ImageProxy): Bitmap {
        val planeY = imageProxy.planes[0]
        val planeU = imageProxy.planes[1]
        val planeV = imageProxy.planes[2]

        val bufferY = planeY.buffer
        val bufferU = planeU.buffer
        val bufferV = planeV.buffer

        // Process YUV_420_888 to NV21 or another format suitable for Bitmap conversion
        val yuvData = ByteArray(bufferY.remaining() + bufferU.remaining() + bufferV.remaining())
        bufferY.get(yuvData, 0, bufferY.remaining())
        bufferU.get(yuvData, bufferY.remaining(), bufferU.remaining())
        bufferV.get(yuvData, bufferY.remaining() + bufferU.remaining(), bufferV.remaining())

        // Convert YUV to JPEG or directly to Bitmap using YUV data
        val yuvImage = YuvImage(yuvData, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val outStream = ByteArrayOutputStream()
        val rect = Rect(0, 0, imageProxy.width, imageProxy.height)
        yuvImage.compressToJpeg(rect, 100, outStream)

        val jpegByteArray = outStream.toByteArray()
        return BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)
    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Restore the Bottom Navigation visibility
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE

        // Clean up camera resources
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()

        // Nullify the binding to avoid memory leaks
        _binding = null
    }





    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
