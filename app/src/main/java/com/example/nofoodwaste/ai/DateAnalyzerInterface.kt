package com.example.nofoodwaste.ai

import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.text.SimpleDateFormat
import java.util.regex.Pattern

interface DateAnalyzerInterface {
    fun drawTextRects(textBlockList: MutableList<FirebaseVisionText.TextBlock>, rotation: Int )
    fun stopDateAnalysis( date: String, textBlocks: FirebaseVisionText.TextBlock, patternFormat: Pair<Pattern, SimpleDateFormat>)
}