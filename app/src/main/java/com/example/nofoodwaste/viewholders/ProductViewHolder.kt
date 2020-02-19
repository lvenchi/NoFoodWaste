package com.example.nofoodwaste.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nofoodwaste.R


class ProductViewHolder(itemView: View): RecyclerView.ViewHolder( itemView ) {

    fun setExpiration(expirationDate: String, colorId: Int ){
        itemView.findViewById<TextView>( R.id.expiry_date).also {
            it.text = expirationDate
            it.setTextColor(colorId)
        }
    }

    fun setProductName(productName: String){
        itemView.findViewById<TextView>(R.id.productNameList).text = productName
    }

}