package com.example.nofoodwaste.ui.main.content


import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.nofoodwaste.R
import com.example.nofoodwaste.ai.MyImageTextAnalyzer
import com.example.nofoodwaste.databinding.FragmentScanPageBinding
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.ai.DateAnalyzerInterface
import com.example.nofoodwaste.ai.OverlayView
import com.example.nofoodwaste.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.fragment_scan_page.*
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.regex.Pattern
import javax.inject.Inject


const val RECORD_PERMISSION_REQUEST = 333

class ScanPage : Fragment(), OverlayView.SizeChangedInformer, DialogInterface.OnDismissListener, ProductInsertNameDialogFragment.ProductNameInterface,
    DateAnalyzerInterface {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    lateinit var executor: Executor

    lateinit var analyzer: ImageAnalysis
    lateinit var imageTextAnalyzer: MyImageTextAnalyzer<ScanPage>
    var marginHorizontal = 0F
    var marginVertical = 0F

    @Inject
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInjector.component.inject(this)
        executor = Executors.newSingleThreadExecutor()
        if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ){
            cameraProviderFuture = ProcessCameraProvider.getInstance(activity!!)
            cameraProviderFuture.addListener( Runnable {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(activity) )

        } else {
            val CAMERA_REQUEST = 123
            requestPermissions( arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentScanPageBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_scan_page, container, false)
        binding.viewmodel = mainViewModel
        binding.lifecycleOwner = this
        return binding.root
    }


    fun bindPreview(cameraProvider : ProcessCameraProvider) {

        val preview : Preview = Preview.Builder()
            .setTargetName("Preview")
            .setTargetResolution(Size( 640, 640))
            .build()

        preview.setSurfaceProvider(preview_view.previewSurfaceProvider)


        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        analyzer = ImageAnalysis.Builder()
            .setImageQueueDepth(10)
            .setTargetResolution(Size(640, 640))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageTextAnalyzer = MyImageTextAnalyzer(this)

        analyzer.setAnalyzer(
            executor,
            imageTextAnalyzer
        )

        view_to_draw.setReceiver(this)
        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector,  analyzer, preview )
    }

    override fun stopDateAnalysis( date: String, textBlocks: FirebaseVisionText.TextBlock, pattern: Pattern ){
        view_to_draw?.drawMatchingDate(textBlocks)

        if(hasRecordPermission()) {
            recordSpeech()
        } else askPermission()
    }

    fun hasRecordPermission() : Boolean{
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    fun askPermission(){
        requestPermissions( arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_PERMISSION_REQUEST)
    }

    override fun drawTextRects( textBlockList: MutableList<FirebaseVisionText.TextBlock>, rotation: Int ){
        view_to_draw?.drawRectangles(textBlockList, rotation)

    }

    fun recordSpeech(){

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALIAN)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nome Prodotto")
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test")
        startActivityForResult(intent, 666)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == 666) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null) {
                val stringona = StringBuilder()
                for (text in result) {
                    stringona.append(text)
                }
                mainViewModel.registeredProductName.postValue(stringona.toString())
                imageTextAnalyzer.found = false
                imageTextAnalyzer.isAnalyzing = false
                Snackbar.make(preview_view, stringona.toString(), Snackbar.LENGTH_LONG).show()
            }
        } else if(resultCode != Activity.RESULT_OK && requestCode == 666) {
            ProductInsertNameDialogFragment(this).show(childFragmentManager, "insert_product_name")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if( requestCode == RECORD_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) recordSpeech()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun inFormSizeChanged(width: Int, height: Int) {

        marginHorizontal = (width - 720F) / 2
        marginVertical = (height - 720F) / 2

        imageTextAnalyzer.updateRectSize(RectF(marginHorizontal - 50, marginVertical - 70,
            width - marginHorizontal + 50 , height - marginVertical + 70))

    }

    override fun onNameSelected(productName: String) {
        Snackbar.make(preview_view, productName, Snackbar.LENGTH_LONG).show()
        mainViewModel.registeredProductName.postValue(productName)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        //Snackbar.make(preview_view, "Failed to register product name", Snackbar.LENGTH_LONG).show()
        view_to_draw.found = false
        imageTextAnalyzer.isAnalyzing = false
        imageTextAnalyzer.found = false
        mainViewModel.registeredProductName.postValue(null)

    }

}
