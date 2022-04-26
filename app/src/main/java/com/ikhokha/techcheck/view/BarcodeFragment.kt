package com.ikhokha.techcheck.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.databinding.FragmentBarcodeBinding
import com.ikhokha.techcheck.utilits.CheckPermissions
import com.ikhokha.techcheck.utilits.Constants
import com.ikhokha.techcheck.viewModel.BarcodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias barcodeScannerCallback = (scannedResult: String) -> Unit

@AndroidEntryPoint
class BarcodeFragment : Fragment(){

    private val TAG = "brcodFrgmnt"
    private lateinit var binding: FragmentBarcodeBinding
    private val viewModel: BarcodeViewModel by viewModels()
    private lateinit var barcodeOptions: BarcodeScannerOptions
    private lateinit var scanner: BarcodeScanner
    lateinit var cameraExecutor: ExecutorService
    private lateinit var permissionsClass: CheckPermissions

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var preview: Preview
    private lateinit var analysis: ImageAnalysis
    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_barcode, container, false)

        barcodeOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_CODE_128)
            .build()

        permissionsClass = CheckPermissions(requireContext())
        if(permissionsClass.hasPermission()){
            startCamera()
        }else{
            permissionsClass.requestPermissions()
        }

        scanner = BarcodeScanning.getClient(barcodeOptions)
        cameraExecutor = Executors.newSingleThreadExecutor()

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.REQUEST_PERMISSION_CODE && permissionsClass.hasPermission()){
            if(grantResults.isNotEmpty()) {
                startCamera()
            }else{
                Snackbar.make(binding.root,"Please accept requested permissions",Snackbar.LENGTH_LONG).show()
            }
        }else{
            permissionsClass.requestPermissions()
        }
    }

    private fun startCamera(){
        ProcessCameraProvider.getInstance(requireContext()).apply {
            addListener({
                cameraProvider = this.get()

                analysis = ImageAnalysis.Builder()
                    .build()
                    .also { analyzer->
                        analyzer.setAnalyzer(cameraExecutor,ImageAnalyzer(scanner){
                            increaseCounter(it)
                            vibrate()
                            unbind()
                        })
                    }

                bindProvider()

            },ContextCompat.getMainExecutor(requireContext()))
        }
    }

    private fun vibrate(){
        (requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).let { vibrator ->
            vibrator.vibrate(500)
        }
    }

    private fun increaseCounter(scannedValue: String){
        viewModel.onItemScanned(scannedValue)
    }

    private fun bindProvider(){
        preview = Preview.Builder()
            .build()
            .also { _preview->
                _preview.setSurfaceProvider(binding.barcodePreview.createSurfaceProvider())
            }
        try{
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this@BarcodeFragment,cameraSelector,preview,analysis
            )
        }catch(ex: Exception){
            Log.v(TAG,"Exception: ${ex.message}")
        }
    }

    private fun unbind(){
        cameraProvider.unbindAll()
        startCamera()
    }

    private class ImageAnalyzer(private val scanner: BarcodeScanner, private val scannerCallback: barcodeScannerCallback): ImageAnalysis.Analyzer{

        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val image = imageProxy.image

            fun closeImage(){
                imageProxy.close()
                image?.close()
            }

            image?.let { _image->
                scanner.process(InputImage.fromMediaImage(_image,imageProxy.imageInfo.rotationDegrees))
                    .addOnSuccessListener {
                        for(barcode in it){
                            scannerCallback(barcode.rawValue!!)
                        }

                    }
                    .addOnFailureListener { ex->
                        scannerCallback("Failed: ${ex.message}")
                    }
                    .addOnCompleteListener {
                        closeImage()
                    }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cartButton.setOnClickListener {
            gotoCartView()
        }

        viewModel.initViewModel()
        viewModel.getItemCounter().observe(viewLifecycleOwner,{
            binding.counterContainer.text = it.toString()
        })

        viewModel.itemUnrecognised().observe(viewLifecycleOwner,{
            productRecognizedMotion(!it)
        })
    }

    private fun gotoCartView(){
        findNavController().navigate(R.id.cartFragment,null,optionsNav())
    }
    private fun optionsNav() = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    private fun productRecognizedMotion(recognised: Boolean){

        when(recognised){
            true->{

                MotionToast.createColorToast(this.requireActivity(),
                    "Success!",
                    "Item is added successfully",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this.requireContext(),R.font.helvetica_regular))
            }
            false->{

                MotionToast.createColorToast(this.requireActivity(),
                    "Error!",
                    "Unrecognised item please check your qr code",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this.requireContext(),R.font.helvetica_regular))
            }
        }


    }

}