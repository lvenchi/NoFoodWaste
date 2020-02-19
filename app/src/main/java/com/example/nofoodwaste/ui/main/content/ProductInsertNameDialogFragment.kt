package com.example.nofoodwaste.ui.main.content

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.nofoodwaste.R

class ProductInsertNameDialogFragment<T>(private val listener: T) : DialogFragment(), DialogInterface.OnClickListener
        where T: ProductInsertNameDialogFragment.ProductNameInterface, T: DialogInterface.OnDismissListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(activity!!)
            .setPositiveButton(getString(R.string.ok), this)
            .setNegativeButton(getString(R.string.cancel), this)
            .setView(R.layout.fragment_insert_product_name)
            .setTitle(getString(R.string.product_name)).create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onDismiss(dialog)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which == AlertDialog.BUTTON_POSITIVE) listener.onNameSelected(getDialog()?.findViewById<EditText>(R.id.product_name)?.text.toString())
    }

    interface ProductNameInterface{
        fun onNameSelected( productName: String )
    }

}