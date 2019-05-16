package com.calleja.jesus.moneymanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_make_payment.view.*

class MakePaymentFragment : Fragment() {

    private lateinit var _view: View
    private lateinit var ibanDBRef: CollectionReference
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_make_payment, container, false)
        setUpPaymentButton()
        setUpIbanDB()
        return _view
    }

    private fun setUpPaymentButton() {
        _view.makePaymentButton.setOnClickListener {
            makePayment()

        }
    }

    private fun makePayment(){
        if(validateFields()){
            var ibanValidated = false
            activity!!.toast("Los campos estan completos")
            ibanDBRef.document("ibanDocument").get().addOnSuccessListener{
                if(it.data != null) {
                    try {
                        // var iban = it.data!!.getValue(currentUser.uid).toString()
                        var ibanList = it.data!!.values
                        ibanList.forEach {
                            if (it.toString().equals(_view.editTextAccountNumber.text.toString())){
                                    ibanValidated = true
                            }
                        }
                    } catch (e: NoSuchElementException) {
                        activity!!.toast("El número de cuenta no pertenece a ningún usuario de la aplicación")
                    }
                }
            }
                .addOnCompleteListener {
                    if(ibanValidated){
                        activity!!.toast("El IBAN es válido")
                    }
                    else {
                        activity!!.toast("El número de cuenta no pertenece a ningún usuario de la aplicación")
                    }
                }

        } else {
            activity!!.toast("Faltan campos por rellenar")

        }
    }

    private fun validateFields(): Boolean {
        var validated = true
        if(_view.editTextBeneficiaryName.text.toString().isNullOrEmpty()){
            validated = false
            activity!!.toast("Debe introducir el nombre del beneficiario para realizar el pago")
        }
        if(_view.editTextAccountNumber.text.toString().isNullOrEmpty()){
            validated = false
            activity!!.toast("Debe introducir el número de cuenta del beneficiario para realizar el pago")
        }
        if(_view.editTextAmount.text.toString().isNullOrEmpty()){
            validated = false
            activity!!.toast("Debe introducir la cantidad en euros para realizar el pago")
        }
        if(_view.editTextConcept.text.toString().isNullOrEmpty()){
            validated = false
            activity!!.toast("Debe introducir el concepto del pago")
        }
        return validated
    }

    private fun setUpIbanDB(){
        ibanDBRef = store.collection("ibans")
    }

}