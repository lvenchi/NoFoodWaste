package com.example.nofoodwaste.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nofoodwaste.models.Product
import com.example.nofoodwaste.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor() : ViewModel() {

    var productList = MutableLiveData( ArrayList<Product>())

    init {
        populateList()
    }

    var date: MutableLiveData<Date?> = MutableLiveData()

    var dateRange: MutableLiveData<androidx.core.util.Pair<Long, Long>> = MutableLiveData()

    var filterString: MutableLiveData<String> = MutableLiveData()

    var registeredProductName = MutableLiveData<String?>()

    var productExpireDate: Pair<Pattern, SimpleDateFormat>? = null

    var lastProdName: String? = null

    fun insertProduct(){
        if( productExpireDate != null && !lastProdName.isNullOrEmpty())
            productList.value?.add(
                Product().apply {
                    this.productName = lastProdName
                    this.expirationDate =  Calendar.getInstance().time
                    this.dateAsLong = Calendar.getInstance().timeInMillis
                }
            )
    }

    private fun populateList(){

        for( i in -2..10){
            val now = Calendar.getInstance()
            now.add(Calendar.DATE, i)
            productList.value?.add(
                Product().also {
                    it.dateAsLong = now.timeInMillis
                    it.expirationDate = now.time
                    it.productName = "Prodotto $i"
                }
            )

            productList.value?.add(
                Product().also {
                    it.dateAsLong = now.timeInMillis
                    it.expirationDate = now.time
                    it.productName = "Prodotto $i"
                }
            )
        }
    }
}
