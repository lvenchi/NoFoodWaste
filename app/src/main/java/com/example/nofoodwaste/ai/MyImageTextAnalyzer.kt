package com.example.nofoodwaste.ai

import android.graphics.RectF
import android.view.OrientationEventListener
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.graphics.toRect
import androidx.fragment.app.Fragment
import com.example.nofoodwaste.utils.Utils
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import kotlin.math.absoluteValue

class MyImageTextAnalyzer<T> (private val analyzerOwner: T? ) :
    ImageAnalysis.Analyzer, OrientationEventListener(analyzerOwner!!.activity!!) where T: Fragment, T: DateAnalyzerInterface {

    init {
        this.enable()
    }

    var rectMargin: RectF? = null
    var currentOrientation: Int = 0
    var found = false
    var isAnalyzing = false
    private val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

    private fun degreesToFirebaseRotation(degrees: Int): Int = when ( degrees ) {
        in 0..89 -> FirebaseVisionImageMetadata.ROTATION_0
        in 90..179 -> FirebaseVisionImageMetadata.ROTATION_90
        in 180..269 -> FirebaseVisionImageMetadata.ROTATION_180
        in 270..359 -> FirebaseVisionImageMetadata.ROTATION_270
        /*in -1 downTo -90 -> FirebaseVisionImageMetadata.ROTATION_180
        in -91 downTo -179 -> FirebaseVisionImageMetadata.ROTATION_0
        in -180 downTo -269 -> FirebaseVisionImageMetadata.ROTATION_90
        in -270 downTo 359 -> FirebaseVisionImageMetadata.ROTATION_270*/
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    fun updateRectSize( rect: RectF){
        rectMargin = rect
    }

    override fun analyze(imageProxy: ImageProxy) {
        if (imageProxy.image != null && !isAnalyzing) {
            val currentDegrees = (currentOrientation.absoluteValue + 90) % 360
            val imageRotation = degreesToFirebaseRotation(currentDegrees)
            val firebaseVisonImage = FirebaseVisionImage.fromMediaImage(imageProxy.image!!, imageRotation)

            isAnalyzing = true
            detector.processImage(firebaseVisonImage)
                .addOnSuccessListener { firebaseVisionText ->
                    if( !found ) {
                        analyzerOwner?.drawTextRects(
                            firebaseVisionText.textBlocks,
                            currentOrientation
                        )
                        val txt = firebaseVisionText.textBlocks
                        imageProxy.close()
                        isAnalyzing = false

                        for (text in txt) {

                            for (patternFormat in Utils.getDateFormatPatterns()) {

                                val matcher = patternFormat.key.matcher(text.text.replace("\n", " "))

                                if (matcher.find()) {
                                    found = true
                                    isAnalyzing = true
                                    val date = matcher.group(2)

                                    analyzerOwner?.stopDateAnalysis(date ?: "", text, patternFormat.toPair())

                                    break
                                }
                                if (found) break
                                matcher.reset()
                            }
                        }
                    } else imageProxy.close()
                }
                .addOnFailureListener { e ->
                    imageProxy.close()
                    println("error\n$e")
                }
        } else imageProxy.close()
    }

    override fun onOrientationChanged(orientation: Int) {
        currentOrientation = orientation
    }


}