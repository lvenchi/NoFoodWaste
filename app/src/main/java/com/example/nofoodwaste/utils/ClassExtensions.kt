package com.example.nofoodwaste.utils

import android.graphics.RectF
import com.google.firebase.ml.vision.text.FirebaseVisionText



fun FirebaseVisionText.TextBlock.floatBoundingBox() : RectF
{
    return RectF(boundingBox)
}


var FirebaseVisionText.TextBlock.boundingBoxFloat: RectF
    get() {
        return RectF(boundingBox)
    }
    set(value) {}

