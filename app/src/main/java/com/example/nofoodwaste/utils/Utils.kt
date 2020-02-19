package com.example.nofoodwaste.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import com.example.nofoodwaste.R
import com.example.nofoodwaste.models.ExpireAlert
import com.example.nofoodwaste.models.Product
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Utils {

    companion object {

        private val today = Calendar.getInstance()

        lateinit var currentLocale: Locale

        private val patternList = ArrayList<Pattern>().apply {
            add(Pattern.compile("^.*?(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d.*?"))
            add(Pattern.compile("^.*?(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])\$*?"))
            add(Pattern.compile("^.*?(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d\$.*?"))
        }


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

            //inImage.compress(Bitmap.CompressFormat.JPEG, 100, BAOS)
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

        fun formatDay( date: Long) : String{
            val formatter = SimpleDateFormat("dd MMMM", currentLocale)
            return formatter.format(date)
            //val formatter = SimpleDateFormat("EEEE dd MMMM", currentLocale)
            //return formatter.format(date)
        }

        fun formatLongDay( date: Long) : String{
            val formatter = SimpleDateFormat("EEEE dd MMMM", currentLocale)
            return formatter.format(date)
        }

        fun isToday( date: Long ) : Boolean {
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            return today.get( Calendar.DATE) == cal.get( Calendar.DATE )
        }

        fun isTomorrow( date: Long ) : Boolean{
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            cal.add(Calendar.DATE, -1)
            return today.get( Calendar.DATE) == cal.get( Calendar.DATE )
        }

        fun isThisWeek( date: Long) : Boolean {
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            return if(cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) cal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) in 2..6
            else cal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) in -359 downTo -365
        }

        fun isPast( date: Long ): Boolean{
            val cal = Calendar.getInstance()
            cal.timeInMillis = date

            return if(cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) cal.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)
            else cal.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)
        }

        fun isEqualDay(index: Int, productList: List<Product>): Boolean{
            if(index == 0 ) return false

            val date1 = Calendar.getInstance()
            date1.timeInMillis = (productList[index - 1].expirationDate?.time?: 0 )
            val date2 = Calendar.getInstance()
            date2.timeInMillis = (productList[index].expirationDate?.time ?: 0)
            val r2 = date2.get(Calendar.DATE)
            val r1 = date1.get(Calendar.DATE)

            return r1 == r2
        }

        fun getColorByAlert( alert: ExpireAlert, context: Context ): Int{
            return when(alert){
                ExpireAlert.EXPIRED -> context.getColor(R.color.colorDelete)
                ExpireAlert.MEDIUM -> context.getColor(R.color.color_warning)
                ExpireAlert.LOW -> context.getColor(R.color.green_box)
                ExpireAlert.HIGH -> context.getColor(R.color.color_orange)

            }
        }

        fun getDateFormatPatterns(): List<Pattern>{
            return patternList
        }



    }

}