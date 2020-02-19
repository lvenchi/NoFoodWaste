package com.example.nofoodwaste.ui.main.content

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.viewmodels.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import java.util.*
import javax.inject.Inject

class DatepickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInjector.component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val date = cal.get(Calendar.DAY_OF_MONTH)
        //return MaterialStyledDatePickerDialog(activity!!, )
        return DatePickerDialog(activity!!, this, year, month, date)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, dayOfMonth)
        mainViewModel.date.postValue(cal.time)
    }

}