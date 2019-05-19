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
import com.google.firebase.firestore.SetOptions


class InfoFragment : Fragment() {

    private lateinit var _view: View
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var balanceDBRef: CollectionReference
    private lateinit var ibanDBRef: CollectionReference
    private lateinit var paymentDBRef: CollectionReference
    private val REQ_CODE_SECOND_FRAGMENT = 90
    private val INTENT_KEY_SECOND_FRAGMENT_DATA = "INTENT_KEY_SECOND_FRAGMENT_DATA"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _view =  inflater.inflate(R.layout.fragment_info, container, false)
        setUpCurrentUser()
        setUpCurrentUserInfoUI()
        setUpBalanceDB()
        setUpIbanDB()
        setUpPaymentDB()
        linkIban()
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
    private fun linkIban() {
        var flagNotInitializedIban = false
        ibanDBRef.document("ibanDocument").get().addOnSuccessListener {
            if(it.data != null) {
                try {
                    var iban = it.data!!.getValue(currentUser.uid).toString()
                    _view.userIban.text = iban
                } catch (e:NoSuchElementException) {
                    flagNotInitializedIban = true
                }
            } else {
                flagNotInitializedIban = true
            }
        } .addOnCompleteListener {
            if(flagNotInitializedIban) {
                initializeIban()
            } else {
                updateBalance()
            }
    }
    }

    private fun updateBalance() {
        var increasedBalance = 0.0
       var test = _view.userIban.text
        paymentDBRef.document(_view.userIban.text.toString()).get().addOnSuccessListener {
            if(it.data!=null){
                var amount = it.data!!.getValue("amount").toString()
                var senderName = it.data!!.getValue("senderName").toString()
                var message = it.data!!.getValue("message").toString()
                activity!!.toast("Ha recibido un pago de: $senderName con importe: $amount y concepto: $message")
                increasedBalance = amount.toDouble()
                paymentDBRef.document(_view.userIban.text.toString()).delete()
            }
        }
            .addOnCompleteListener {

                balanceDBRef.document(currentUser.uid).get().addOnSuccessListener {
                    if (it.data != null) {
                        try {
                            var currentBalance = it.data!!.getValue(currentUser.uid).toString().toDouble()
                            if(increasedBalance != 0.0) {
                                currentBalance += increasedBalance
                                saveBalance(currentBalance.toString())
                            }
                            if (currentBalance == 0.0) {
                                _view.userBalance.text = "0 EUR"
                            } else {
                                _view.userBalance.text = "$currentBalance EUR"
                            }
                        } catch (e: NoSuchElementException) {
                            _view.userBalance.text = "0 EUR"
                            saveBalance("0")
                        }
                    } else {
                        _view.userBalance.text = "0 EUR"
                        saveBalance("0")
                    }
                }
            }
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpBalanceDB(){
        balanceDBRef = store.collection("balances")
    }

    private fun setUpIbanDB(){
        ibanDBRef = store.collection("ibans")
    }

    private fun setUpPaymentDB(){
        paymentDBRef = store.collection("payments")
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
                                activity!!.toast("Saldo actualizado correctamente")
                            }
                            .addOnFailureListener {
                                activity!!.toast("Error al actualizar el saldo")
                            }
                            .addOnCompleteListener {
                                updateBalance()
                            }
                }

    private fun initializeIban() {
        val newIban = IbanGenerator.generateIban("ES", "IBAN")
        activity!!.toast("Su IBAN ES: $newIban")
        val iban = HashMap<String, String>()
        iban[mAuth.currentUser!!.uid] = newIban
        ibanDBRef.document("ibanDocument").set(iban, SetOptions.merge())
            .addOnSuccessListener {
                _view.userIban.text = newIban
            }
            .addOnFailureListener {
              //  activity!!.toast("Error al guardar el iban")
            }
            .addOnCompleteListener {
                updateBalance()
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
}
