package com.calleja.jesus.moneymanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.dialogs.BalanceDialog
import com.calleja.jesus.moneymanager.toast
import com.calleja.jesus.moneymanager.utils.CircleTransform
import com.calleja.jesus.moneymanager.utils.IbanGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_info.view.*
import android.app.Activity
import android.content.Intent


class InfoFragment : Fragment() {

    private lateinit var _view: View
    private var flagBalanceInitialized = false
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var balanceDBRef: CollectionReference
    private val REQ_CODE_SECOND_FRAGMENT = 90
    private val INTENT_KEY_SECOND_FRAGMENT_DATA = "INTENT_KEY_SECOND_FRAGMENT_DATA"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _view =  inflater.inflate(R.layout.fragment_info, container, false)
        setUpCurrentUser()
        setUpCurrentUserInfoUI()
        setUpBalanceDB()
        updateBalance()
        setUpEditBalanceButton()
        return _view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_SECOND_FRAGMENT) {
                val secondFragmentData = intent!!.getStringExtra(INTENT_KEY_SECOND_FRAGMENT_DATA)
                saveBalance(secondFragmentData)
            }
        }

    }

    private fun updateBalance() {
        if(!flagBalanceInitialized) {
            balanceDBRef.document(currentUser.uid).get().addOnSuccessListener {
                if(it.data != null) {
                    try {
                        var currentBalance = it.data!!.getValue(currentUser.uid).toString()
                        _view.editTextTotalBalance.text = "$currentBalance EUR"
                        flagBalanceInitialized = true
                    } catch (e: NoSuchElementException) {
                        initializeBalance()
                    }
                } else {
                    initializeBalance()
                }
            }
        }
    }

    private fun initializeBalance(){
        _view.editTextTotalBalance.text = "0 EUR"
        flagBalanceInitialized = true
    }
    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpBalanceDB(){
        balanceDBRef = store.collection("balances")
    }

    private fun setUpEditBalanceButton() {
        _view.editBalanceButton.setOnClickListener {
            val dialog = BalanceDialog()
            dialog.setTargetFragment(this, REQ_CODE_SECOND_FRAGMENT)
            dialog.show(fragmentManager, "")
        }
    }

    private fun saveBalance(userBalance: String) {
        val newBalance = HashMap<String, String>()
        newBalance[currentUser.uid] = userBalance
                        balanceDBRef.document(currentUser.uid).set(newBalance)
                            .addOnSuccessListener {
                                _view.editTextTotalBalance.text = "$userBalance EUR"
                            }
                            .addOnFailureListener {
                                activity!!.toast("Error al actualizar el saldo")
                            }
                }

    private fun setUpCurrentUserInfoUI() {
        _view.textViewInfoEmail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let {currentUser.displayName}
            ?: run {getString(R.string.info_no_name)}

        currentUser.photoUrl?.let {
            Picasso.get().load(currentUser.photoUrl).resize(300,300)
                .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        } ?: run {
            Picasso.get().load(R.drawable.ic_person).resize(300,300)
                .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}
