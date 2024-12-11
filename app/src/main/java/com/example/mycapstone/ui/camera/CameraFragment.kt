package com.example.mycapstone.ui.camera


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mycapstone.BuildConfig
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentCameraBinding
import com.example.mycapstone.ui.camera.HandLandMarkerHelper.Companion.TAG
import com.google.ai.client.generativeai.GenerativeModel
import com.google.mediapipe.tasks.vision.core.RunningMode
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(), HandLandMarkerHelper.LandmarkerListener {


    private  var _fragmentCameraBinding: FragmentCameraBinding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

    private val viewModel: CameraViewModel by viewModels()

    private val geminiKey = BuildConfig.GEMINI_API_KEY


    private lateinit var handLandmarkerHelper: HandLandMarkerHelper

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    private var loadingDialog: Dialog? = null

    private val cameraPermissionReqLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted) {
                setupCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding =
            FragmentCameraBinding.inflate(inflater, container, false)

        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (shouldRequestCameraPermission()) {
            cameraPermissionReqLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            // If permission is already granted, set up the camera
            setupCamera()
        }

        backgroundExecutor = Executors.newSingleThreadExecutor()

        fragmentCameraBinding.viewFinder.post{
            setupCamera()
        }

        fragmentCameraBinding.switchCameraButton.setOnClickListener {
            switchCamera()
        }

        viewModel.initializeDetector(requireContext())

        setupObservers()
        finishButton()

        fragmentCameraBinding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }


        backgroundExecutor.execute {
            handLandmarkerHelper = HandLandMarkerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                maxNumHands = viewModel.currentMaxHands,
                currentDelegate = viewModel.currentDelegate,
                handLandmarkerHelperListener = this
            )
        }


    }

    private fun shouldRequestCameraPermission(): Boolean {
        return requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }






    override fun onError(error: String, errorCode: Int) {
        Log.e(TAG, "Hand Landmarker Error ($errorCode): $error")
    }

    override fun onResults(resultBundle: HandLandMarkerHelper.ResultBundle) {
        viewLifecycleOwner.lifecycleScope.launch {

            _fragmentCameraBinding?.let { binding ->
                if (resultBundle.results.isEmpty()) {
                    binding.overlay.clear()
                    binding.overlay.invalidate()

                    Log.d("CameraOverlay", "No hand results detected.")
                    return@launch
                }

                val result = resultBundle.results[0]
                Log.d("CameraOverlay", "Detected hands: ${result.landmarks().size}")

                val landmarks = result.landmarks().firstOrNull()
                if (landmarks != null) {
                    val bitmap = resultBundle.inputBitmap
                    viewModel.detect(bitmap, landmarks)
                }

                // Log detailed information about each hand
                // Log detailed information about each hand
                result.landmarks().forEachIndexed { index, landmarks ->
                    Log.d("CameraOverlay", "Hand $index landmarks count: ${landmarks.size}")

                    // Optional: Log a few key landmarks
                    if (landmarks.isNotEmpty()) {
                        Log.d("CameraOverlay", "First landmark: ${landmarks[0]}")
                    }
                }

                fragmentCameraBinding.overlay.setResults(
                    result,
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )
                fragmentCameraBinding.overlay.invalidate()
        }
            }

    }

    private fun finishButton(){
        fragmentCameraBinding.btnFinishDetecting.setOnClickListener {
            Toast.makeText(requireContext(),"Loading.. Please wait",
                Toast.LENGTH_LONG).show()

            showLoadingDialog()

            lifecycleScope.launch {
                val res = viewModel.autoCorrect(fragmentCameraBinding.predictedTextView.text.toString())
                hideLoadingDialog()

                viewModel.clearDetection()
                Log.d("CameraFragment", "From gemini revised: $res")

                val currentTime = System.currentTimeMillis()
                val bundle = Bundle().apply {
                    putString("generatedText", res)
                    putLong("timestamp", currentTime)
                }

                findNavController().navigate(R.id.action_nav_camera_to_resultsFragment, bundle)
            }
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = Dialog(requireContext()).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.loading_dialog)
                setCancelable(false)


            }
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

//    private suspend fun gemini(text: String): String? {
//        val generativeModel = GenerativeModel(
//            modelName = "gemini-pro",
//            apiKey = geminiKey
//        )
//
//        val prompt = "Revisi kesalahan penulisan ini: $text"
//        val response = generativeModel.generateContent(prompt)
//
//        return response.text
//    }

    private fun setupObservers() {
//        viewModel.detectionResults.observe(viewLifecycleOwner, Observer { boundingBoxes ->
//            fragmentCameraBinding.overlayBounding.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        })

        viewModel.signLanguangeWords.observe(viewLifecycleOwner) { word ->
            Log.d("CameraFragment", "Detected word: $word")
            fragmentCameraBinding.predictedTextView.text = word
        }
    }

    private fun switchCamera() {
        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        // Re-bind use cases to update selected camera
        bindCameraUseCases()
    }


    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    private fun setupCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onPause() {
        super.onPause()
        if(this::handLandmarkerHelper.isInitialized) {
            viewModel.setMaxHands(handLandmarkerHelper.maxNumHands)
            viewModel.setMinHandDetectionConfidence(handLandmarkerHelper.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(handLandmarkerHelper.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(handLandmarkerHelper.minHandPresenceConfidence)
            viewModel.setDelegate(handLandmarkerHelper.currentDelegate)

            // Close the HandLandmarkerHelper and release resources
            backgroundExecutor.execute { handLandmarkerHelper.clearHandLandmarker() }
        }
    }

    override fun onResume() {
        super.onResume()
//        // Make sure that all permissions are still present, since the
//        // user could have removed them while the app was in paused state.
//        if (!PermissionsFragment.hasPermissions(requireContext())) {
//            Navigation.findNavController(
//                requireActivity(), R.id.fragment_container
//            ).navigate(R.id.action_camera_to_permissions)
//        }

        // Start the HandLandmarkerHelper again when users come back
        // to the foreground.
        backgroundExecutor.execute {
            if (handLandmarkerHelper.isClose()) {
                handLandmarkerHelper.setupHandLandmarker()
            }
        }
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        detectHand(image)

                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.surfaceProvider = fragmentCameraBinding.viewFinder.surfaceProvider
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectHand(imageProxy: ImageProxy) {
        handLandmarkerHelper.detectLivestream(
            imageProxy = imageProxy,
            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        )

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }



}
