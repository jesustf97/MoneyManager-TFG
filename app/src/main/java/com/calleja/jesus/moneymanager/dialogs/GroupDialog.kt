package com.calleja.jesus.moneymanager.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.dialog_create_group.view.*
import java.util.ArrayList

class GroupDialog : DialogFragment() {

    private lateinit var groupDBRef: CollectionReference
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_create_group
            , null)

        setUpGroupDB()
        setUpCurrentUser()

        return AlertDialog.Builder(context)
            .setTitle( getString(R.string.dialog_create_group_title))
            .setView(view)
            .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                val iban1 = view.editTextCreateGroup1.text.toString()
                val iban2 = view.editTextCreateGroup2.text.toString()
                val iban3 = view.editTextCreateGroup3.text.toString()
                val iban4 = view.editTextCreateGroup4.text.toString()
                val iban5 = view.editTextCreateGroup5.text.toString()

                val groupIbans :ArrayList<String> = ArrayList()

                if (iban1.isNotEmpty()){
                    groupIbans.add(iban1)
                }
                if (iban2.isNotEmpty()){
                    groupIbans.add(iban2)
                }
                if (iban2.isNotEmpty()){
                    groupIbans.add(iban3)
                }
                if (iban4.isNotEmpty()){
                    groupIbans.add(iban4)
                }
                if (iban5.isNotEmpty()){
                groupIbans.add(iban5)
            }

                if(groupIbans.size != 0) {
                    createGroup(groupIbans)
                }
            }
            .setNegativeButton( getString(R.string.dialog_cancel)) { _, _ ->
                // activity!!.toast("Se ha pulsado Cancelar")
            }
            .create()
    }


    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpGroupDB(){
        groupDBRef = store.collection("group")
    }

    private fun createGroup(groupList :ArrayList<String>) {
        val groupInfo = HashMap<String, ArrayList<String>>()
        groupInfo.put(currentUser.uid, groupList)
        groupDBRef.document("groupsDocument").set(groupInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity!!.toast("Se ha creado el grupo correctamente")
            }
            .addOnFailureListener {
                activity!!.toast("Error al crear grupo")
            }
    }

}
