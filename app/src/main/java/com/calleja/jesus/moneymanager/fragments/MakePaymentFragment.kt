package com.calleja.jesus.moneymanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_make_payment.view.*

class MakePaymentFragment : Fragment() {

    private lateinit var _view: View
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private lateinit var ibanDBRef: CollectionReference
    private lateinit var balanceDBRef: CollectionReference
    private lateinit var paymentDBRef: CollectionReference
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_make_payment, container, false)
        setUpPaymentButton()
        setUpCurrentUser()
        setUpIbanDB()
        setUpBalanceDB()
        setUpPaymentDB()
        return _view
    }

    private fun setUpPaymentButton() {
        _view.makePaymentButton.setOnClickListener {
            makePayment()

        }
    }

    private fun makePayment() {
        if (validateFields()) {
            activity!!.toast("Los campos estan completos")
            var amountValidated = false
            var currentBalance = 0.0
            var amount = 0.0
            //Validación de la cantidad
            balanceDBRef.document(currentUser.uid).get().addOnSuccessListener {
                if (it.data != null) {
                    try {
                        currentBalance = (it.data!!.getValue(currentUser.uid).toString().toDouble())
                        amount = (_view.editTextAmount.text.toString().toDouble())
                        activity!!.toast("El saldo actual es de: $currentBalance")
                        activity!!.toast("La cantidad del pago es de: $amount")
                        if (currentBalance >= amount) {
                            amountValidated = true
                        }
                    } catch (e: NoSuchElementException) {
                        activity!!.toast("No tiene suficiente saldo para realizar el pago")
                    }
                } else {
                    activity!!.toast("No tiene suficiente saldo para realizar el pago")
                }
            }
                .addOnCompleteListener {
                    if (amountValidated) {
                        activity!!.toast("Cantidad valida correctamente")
                        // Validación del IBAN del beneficiario
                        var ibanValidated = false

                        ibanDBRef.document("ibanDocument").get().addOnSuccessListener {
                            if (it.data != null) {
                                try {
                                    // var iban = it.data!!.getValue(currentUser.uid).toString()
                                    var ibanList = it.data!!.values
                                    ibanList.forEach {
                                        if (it.toString().equals(_view.editTextAccountNumber.text.toString())) {
                                            ibanValidated = true
                                        }
                                    }
                                } catch (e: NoSuchElementException) {
                                    activity!!.toast("El número de cuenta no pertenece a ningún usuario de la aplicación")
                                }
                            }
                        }
                            .addOnCompleteListener {
                                if (ibanValidated) {
                                    activity!!.toast("El IBAN es válido")
                                    var decreasedBalance = (currentBalance - amount).toString()
                                    //Decrease Sender Balance
                                    decreaseSenderBalance(decreasedBalance)
                                    sendPaymentNotification()
                                    activity!!.toast("El saldo modificado es de: $decreasedBalance")

                                } else {
                                    activity!!.toast("El número de cuenta no pertenece a ningún usuario de la aplicación")
                                }
                            }
                    } else {
                        activity!!.toast("No tiene suficiente saldo para realizar el pago")
                    }
                }
        }

        else {
            activity!!.toast("Faltan campos por rellenar")

        }
    }

    private fun sendPaymentNotification() {
        var senderName = mAuth.currentUser!!.displayName
        var amount = _view.editTextAmount.text.toString()
        var message =_view.editTextConcept.text.toString()
       // var paymentNotification = PaymentNotification(senderName, amount, message)
        val paymentNotification = HashMap<String, String?>()
        paymentNotification.put("senderName", senderName)
        paymentNotification.put("amount", amount)
        paymentNotification.put("message", message)
        var iban = _view.editTextAccountNumber.text.toString()
        paymentDBRef.document(iban).set(paymentNotification)
            .addOnSuccessListener {
                activity!!.toast("Se ha enviado el pago correctamente")
            }
            .addOnFailureListener {
                activity!!.toast("Error al enviar el pago")
            }
    }

    private fun decreaseSenderBalance(decreasedBalance: String) {
        val newBalance = HashMap<String, String>()
        newBalance[currentUser.uid] = decreasedBalance
        balanceDBRef.document(currentUser.uid).set(newBalance)
            .addOnSuccessListener {
                activity!!.toast("El saldo total ha sido actualizado")
            }
            .addOnFailureListener {
                activity!!.toast("Error al actualizar el saldo total")
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

}