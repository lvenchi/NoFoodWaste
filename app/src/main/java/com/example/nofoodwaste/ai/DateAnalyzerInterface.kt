package com.example.nofoodwaste.ai

import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.util.regex.Pattern

interface DateAnalyzerInterface {
    fun drawTextRects(textBlockList: MutableList<FirebaseVisionText.TextBlock>, rotation: Int )
    fun stopDateAnalysis( date: String, textBlocks: FirebaseVisionText.TextBlock, pattern: Pattern)
}