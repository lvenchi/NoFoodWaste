package com.example.nofoodwaste.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.util.*

class Utils {

    companion object {

        fun isEmail( inputMail: String? ): Boolean {
            if( inputMail == null ) return false
            return android.util.Patterns.EMAIL_ADDRESS.matcher(inputMail).matches()
        }

        fun getImageUri(
            inContext: Context,
            inImage: Bitmap,
            locale: Locale?
        ): Uri? {
            val BAOS = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.PNG, 100, BAOS)
            val cal = Calendar.getInstance()
            val path = MediaStore.Images.Media.insertImage(
                inContext.contentResolver, inImage,
                java.lang.String.format(
                    locale!!,
                    "%1\$tA %1\$td %1\$tb %1\$tY %1\$tI:%1\$tM %1\$Tp",
                    cal
                ), null
            )
            return Uri.parse(path)
        }

    }

}