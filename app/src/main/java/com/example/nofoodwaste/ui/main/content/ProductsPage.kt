package com.example.nofoodwaste.ui.main.content


import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nofoodwaste.R
import com.example.nofoodwaste.adapters.DayAdapter
import com.example.nofoodwaste.databinding.FragmentProductsPageBinding
import com.example.nofoodwaste.di.ComponentInjector
import com.example.nofoodwaste.models.Product
import com.example.nofoodwaste.viewmodels.MainViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_products_page.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ProductsPage : Fragment() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    private var productList: ArrayList<Product> = ArrayList()

    private lateinit var materialDatePicker: MaterialDatePicker <androidx.core.util.Pair<Long,Long>>

    override fun onCreate(savedInstanceState: Bundle?) {
        ComponentInjector.component.inject(this)
        materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.materialCalendarStyle).build()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProductsPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_products_page, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = mainViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        materialDatePicker.addOnPositiveButtonClickListener { res ->
            mainViewModel.dateRange.postValue(res)
        }

        mainViewModel.dateRange.observe( viewLifecycleOwner, Observer {

            if(it == null ){
                calendar_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.color_green_text))
                calendar_button.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_icons8_calendar)
            } else {
                calendar_button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.color_google_red))
                calendar_button.icon = ContextCompat.getDrawable(context!!, R.drawable.ic_icons8_cancel)
            }
            updateFilteredList(mainViewModel.filterString.value, it)
        })

        products_list.also {

            it.adapter = DayAdapter().also { adapter ->
                adapter.updateList(productList)
            }
            it.layoutManager = LinearLayoutManager(view.context)
            it.setHasFixedSize(false)

            mainViewModel.productList.observe(viewLifecycleOwner, Observer { list ->
                (it.adapter as DayAdapter).updateList(list)
            })
        }

        calendar_button.setOnClickListener {
            if(!materialDatePicker.isVisible && mainViewModel.dateRange.value == null){
                materialDatePicker.show(activity?.supportFragmentManager!!, "datePicker")
            } else {
                mainViewModel.dateRange.postValue(null)
            }
        }

        mainViewModel.filterString.observe( viewLifecycleOwner, Observer {
            updateFilteredList(it, mainViewModel.dateRange.value)
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateFilteredList( textFilter: String?, dateFilter: androidx.core.util.Pair<Long, Long>?){
        if(textFilter.isNullOrEmpty() && dateFilter == null){
            (products_list.adapter as DayAdapter).updateList(productList)
        } else if( textFilter == null) (products_list.adapter as DayAdapter).updateList(productList.filter {
                product ->  product.expirationDate?.time ?: 0 in dateFilter?.first!! .. dateFilter.second!! }
        )
        else if( dateFilter == null) (products_list.adapter as DayAdapter).updateList(productList.filter {
                product ->  product.productName?.contains(mainViewModel.filterString.value ?: "") ?: false }
        )
        else (products_list.adapter as DayAdapter).updateList( productList.filter {
                product ->  product.productName?.contains(textFilter) ?: false
                && product.expirationDate?.time ?: 0 in dateFilter.first!! .. dateFilter.second!! } )
    }



}
