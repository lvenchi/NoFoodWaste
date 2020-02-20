package com.example.nofoodwaste.ai

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Size
import android.view.View
import com.example.nofoodwaste.utils.boundingBoxFloat
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlin.math.absoluteValue

class OverlayView( context: Context, attrs: AttributeSet) : View(context, attrs){

    lateinit var size: Size
    var currRotation = 0
    var savedTextBlocks : MutableList<FirebaseVisionText.TextBlock> = ArrayList()
    var marginHorizontal: Float = 0F
    var marginVertical: Float = 0F
    var horizontalScreenRatio = 0F
    var verticalScreenRatio = 0F
    var found = false
    val inverse: Matrix = Matrix()
    var rec : SizeChangedInformer? = null

    lateinit var matchingValue : FirebaseVisionText.TextBlock

    /*private val rectMargin: RectF by lazy {
        RectF(marginHorizontal - 50, marginVertical - 70, size.width - marginHorizontal + 50 , size.height - marginVertical + 70)
    }*/

    private val matchingPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 3F
    }

    private val rectPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3F
    }

    fun <T> setReceiver( receiver : T ) where T: SizeChangedInformer {
        receiver.inFormSizeChanged(width, height)
        rec = receiver
    }

    override fun onDraw(canvas: Canvas?) {
        if( !found ){
            for( textBlock in savedTextBlocks ){
                val rect = textBlock.boundingBoxFloat.apply {
                    left *= horizontalScreenRatio
                    right *= horizontalScreenRatio
                    top *= verticalScreenRatio
                    bottom *= verticalScreenRatio
                }
                //inverse.setRotate(-currRotation.toFloat(), rect.centerX(), rect.centerY())
                //inverse.mapRect(rect)
                //rotationMatrix.mapRect(rect)
                //}
                //rotationMatrix.setRotate(currRotation.toFloat(), width/2F, height/2F )
                //canvas?.setMatrix(rotationMatrix)
                canvas?.drawRect(rect, rectPaint)
                if(currRotation.absoluteValue in 89..180 || currRotation.absoluteValue in 269..360) rotation = currRotation.toFloat() + 180 else rotation = currRotation.toFloat()
            }
        }
        else
            canvas?.drawRect(matchingValue.boundingBoxFloat.apply {
                left *= horizontalScreenRatio
                right *= horizontalScreenRatio
                top *= verticalScreenRatio
                bottom *= verticalScreenRatio
            }, matchingPaint)

        super.onDraw(canvas)
    }

    fun drawMatchingDate( textBlocks: FirebaseVisionText.TextBlock){
        found = true
        matchingValue = textBlocks
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        size = Size(w, h)
        marginHorizontal = (w - 720)/2F
        marginVertical = (h - 720F)/2F
        horizontalScreenRatio = w/720F
        verticalScreenRatio = h/720F
        rec?.inFormSizeChanged(w, h)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun drawRectangles( textBlocks: MutableList<FirebaseVisionText.TextBlock>, rotation: Int){
        currRotation = rotation
        savedTextBlocks = textBlocks
        invalidate()
    }


    interface SizeChangedInformer {
        fun inFormSizeChanged( width: Int, height: Int)
    }

}