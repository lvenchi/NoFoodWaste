package com.example.nofoodwaste.ui.main.content


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nofoodwaste.R
import kotlinx.android.synthetic.main.fragment_products_page.*

/**
 * A simple [Fragment] subclass.
 */
class ProductsPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_products_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        products_list

        super.onViewCreated(view, savedInstanceState)
    }


}
