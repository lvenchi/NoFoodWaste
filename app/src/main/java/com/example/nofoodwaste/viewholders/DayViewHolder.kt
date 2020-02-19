package com.example.nofoodwaste.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nofoodwaste.R
import com.example.nofoodwaste.adapters.ProductsAdapter
import com.example.nofoodwaste.models.ExpireAlert
import com.example.nofoodwaste.models.Product

class DayViewHolder(itemView: View ): RecyclerView.ViewHolder(itemView) {


    private var expireAlert: ExpireAlert = ExpireAlert.LOW

    fun setAlert( expire: ExpireAlert ){
        expireAlert = expire
    }

    fun setTitle( title: String){
        itemView.findViewById<TextView>(R.id.tvTitle).text = title
    }

    fun getAlert(): ExpireAlert{
        return expireAlert
    }

    fun setRecyclerView( productList: List<Product>){
        itemView.findViewById<RecyclerView>(R.id.products_day_list).also {
            it.adapter = ProductsAdapter(productList, expireAlert)
            it.layoutManager = LinearLayoutManager(itemView.context)
            it.setHasFixedSize(false)
        }
    }
}
