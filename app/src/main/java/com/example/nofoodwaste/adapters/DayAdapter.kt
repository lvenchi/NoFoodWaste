package com.example.nofoodwaste.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nofoodwaste.R
import com.example.nofoodwaste.models.ExpireAlert
import com.example.nofoodwaste.models.Product
import com.example.nofoodwaste.utils.Utils.Companion.formatLongDay
import com.example.nofoodwaste.utils.Utils.Companion.isPast
import com.example.nofoodwaste.utils.Utils.Companion.isThisWeek
import com.example.nofoodwaste.utils.Utils.Companion.isToday
import com.example.nofoodwaste.utils.Utils.Companion.isTomorrow
import com.example.nofoodwaste.viewholders.DayViewHolder

import java.util.*
import kotlin.collections.ArrayList

open class DayAdapter : RecyclerView.Adapter<DayViewHolder>() {

    var today = Calendar.getInstance()
    var dayHashMap: HashMap<String, MutableList<Product>> = HashMap()

    init {
        dayHashMap["past"] = ArrayList()
        dayHashMap["today"] = ArrayList()
        dayHashMap["tomorrow"] = ArrayList()
        dayHashMap["thisweek"] = ArrayList()
        dayHashMap["afterweek"] = ArrayList()
    }

    fun updateList( productList: List<Product>){
        dayHashMap.values.forEach { prodList -> prodList.clear() }
        productList.forEach { prod ->
            when {
                isPast(prod.dateAsLong) -> dayHashMap["past"]?.add(prod)
                isToday(prod.dateAsLong) -> dayHashMap["today"]?.add(prod)
                isTomorrow(prod.dateAsLong) -> dayHashMap["tomorrow"]?.add(prod)
                isThisWeek(prod.dateAsLong) -> dayHashMap["thisweek"]?.add(prod)
                else -> dayHashMap["afterweek"]?.add(prod)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dayHashMap.size
    }

    //+ " ${Utils.formatDay(today.time.time)}"
    //+ " ${Utils.formatDay(productList[indexList[position]].expirationDate?.time?: 0)}"

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {

        when (position) {
            0 -> {
                holder.setAlert(ExpireAlert.EXPIRED)
                holder.setTitle( holder.itemView.context.getString(R.string.expired ))
                holder.setRecyclerView(dayHashMap["past"]!!)
            }
            1 -> {
                holder.setAlert(ExpireAlert.HIGH)
                holder.setTitle( holder.itemView.context.getString(R.string.today_expire) )
                holder.setRecyclerView(dayHashMap["today"]!! )
            }
            2 -> {
                holder.setAlert(ExpireAlert.HIGH)
                holder.setTitle(  holder.itemView.context.getString(R.string.tomorrow_expire) )
                holder.setRecyclerView(dayHashMap["tomorrow"]!!)
            }
            3 -> {
                holder.setAlert(ExpireAlert.MEDIUM)
                holder.setTitle( formatLongDay(today.timeInMillis + 86400000 * 2) + " - " + formatLongDay(today.timeInMillis + 86400000 * 5) )
                holder.setRecyclerView(dayHashMap["thisweek"]!!)
            }
            4 -> {
                holder.setAlert(ExpireAlert.LOW)
                holder.setTitle(holder.itemView.context.getString(R.string.after_a_week_expire))
                holder.setRecyclerView(dayHashMap["afterweek"]!!)
            }
        }

        /*when {
            isToday(productList[indexList[position]].expirationDate?.time ?: 0) -> {
                holder.setAlert(ExpireAlert.HIGH)
                holder.setTitle( holder.itemView.context.getString(R.string.today_elapse) )
            }
            isTomorrow(productList[indexList[position]].expirationDate?.time ?: 0) -> {
                holder.setAlert(ExpireAlert.HIGH)
                holder.setTitle( holder.itemView.context.getString(R.string.tomorrow_expire) )
            }
            else -> {
                when{
                    isPast(productList[indexList[position]].expirationDate) -> holder.setAlert(ExpireAlert.EXPIRED)
                    isThisWeek(productList[indexList[position]].expirationDate) -> holder.setAlert(ExpireAlert.MEDIUM)
                    else -> holder.setAlert(ExpireAlert.LOW)
                }
                val cal = Calendar.getInstance()
                cal.timeInMillis = productList[indexList[position]].expirationDate?.time ?: 0
                if(holder.getAlert() == ExpireAlert.EXPIRED) holder.setTitle("Expired")
                else holder.setTitle(Utils.formatDay(productList[indexList[position]].expirationDate?.time?: 0 ))
            }
        }
        if(position == 0 )
            if(indexList.size == 1)
                holder.setRecyclerView(productList.subList(fromIndex = 0, toIndex = productList.count()  ))
            else holder.setRecyclerView(productList.subList(fromIndex = 0, toIndex = indexList[1]  ))
        else {
            if( position  == indexList.size -1 ){
                holder.setRecyclerView(productList.subList(fromIndex = indexList[position], toIndex = productList.size ))
            } else {
                if(position < indexList.size - 1 && position > 0) holder.setRecyclerView(productList.subList(fromIndex = indexList[position] , toIndex = indexList[position + 1] ))
            }
        }*/
    }

}