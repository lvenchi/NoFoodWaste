package com.example.nofoodwaste.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor() : ViewModel() {

    var date: MutableLiveData<Date?> = MutableLiveData()

    var dateRange: MutableLiveData<androidx.core.util.Pair<Long, Long>> = MutableLiveData()

    var filterString: MutableLiveData<String> = MutableLiveData()

    var registeredProductName = MutableLiveData<String?>()

}
