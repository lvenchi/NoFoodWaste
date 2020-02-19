package com.example.nofoodwaste.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nofoodwaste.R
import com.example.nofoodwaste.models.ExpireAlert
import com.example.nofoodwaste.models.Product
import com.example.nofoodwaste.utils.Utils
import com.example.nofoodwaste.viewholders.ProductViewHolder

class ProductsAdapter( var productList: List<Product>, private val expireAlert: ExpireAlert): RecyclerView.Adapter<ProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.product_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.setExpiration( Utils.formatDay(productList[position].dateAsLong), Utils.getColorByAlert(expireAlert, holder.itemView.context))
        holder.setProductName(productList[position].productName ?: "Not Valid")
    }

    fun updateList( updatedList: List<Product>){
        productList = updatedList
        notifyDataSetChanged()
    }

}