package com.calleja.jesus.moneymanager.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.toast
import kotlinx.android.synthetic.main.dialog_balance.view.*
import android.app.Activity
import android.content.Intent

class BalanceDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_balance
            , null)

        return AlertDialog.Builder(context!!)
            .setTitle( getString(R.string.update_balance_button))
            .setView(view)
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
             //   activity!!.toast("Se ha pulsado Ok")
                val textBalance = view.editTextBalance.text.toString()
                if (textBalance.isNotEmpty()) {
                    val intent = Intent()
                    intent.putExtra("INTENT_KEY_SECOND_FRAGMENT_DATA", textBalance)
                    val fragment = targetFragment
                    fragment!!.onActivityResult(90, Activity.RESULT_OK, intent)
                }
            }
            .setNegativeButton( getString(R.string.dialog_cancel)) { _, _ ->
               // activity!!.toast("Se ha pulsado Cancelar")
            }
            .create()
    }

}
