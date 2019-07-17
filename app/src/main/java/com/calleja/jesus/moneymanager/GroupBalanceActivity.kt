package com.calleja.jesus.moneymanager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.calleja.jesus.moneymanager.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_group_balance.*

class GroupBalanceActivity : AppCompatActivity() {

    private lateinit var paymentDBRef: CollectionReference
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_balance)
        setUpPaymentDB()
        setUpSendPaymentToGroupButton()
        setSupportActionBar(toolbarAdmin)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpSendPaymentToGroupButton() {
        send_payment_group_button.setOnClickListener {
            if (editTextPaymentGroup.text.toString().isNotEmpty()) {
                pendingNotifications()
            } else {
                toast("Introduzca la cantidad que desea enviar al grupo")
            }
        }

    }

    private fun setUpPaymentDB() {
        paymentDBRef = store.collection("payments")
    }

    private fun pendingNotifications() {
        val ibans: ArrayList<String> = intent.extras!!.getSerializable("ibans") as ArrayList<String>
        val iterator = ibans.iterator()

        var senderName = "Usuario administrador"
        var amount = editTextPaymentGroup.text.toString()
        var message = "Transferencia a grupo"
        val paymentNotification = HashMap<String, String?>()
        paymentNotification.put("senderName", senderName)
        paymentNotification.put("amount", amount)
        paymentNotification.put("message", message)

        var ibanBeneficiary = iterator.next()


        paymentDBRef.document(ibanBeneficiary).set(paymentNotification)
            .addOnSuccessListener {
                toast("Se ha enviado el pago correctamente")
            }
            .addOnFailureListener {
                toast("Error al enviar el pago")
            }
            .addOnCompleteListener {

                if(iterator.hasNext()){
                    ibanBeneficiary = iterator.next()
                    paymentDBRef.document(ibanBeneficiary).set(paymentNotification)
                        .addOnSuccessListener {
                            toast("Se ha enviado el pago correctamente")
                        }
                        .addOnFailureListener {
                            toast("Error al enviar el pago")
                        }
                        .addOnCompleteListener {
                            if(iterator.hasNext()) {
                                ibanBeneficiary = iterator.next()
                                paymentDBRef.document(ibanBeneficiary).set(paymentNotification)
                                    .addOnSuccessListener {
                                        //toast("Se ha enviado el pago correctamente")
                                    }
                                    .addOnFailureListener {
                                        toast("Error al enviar el pago")
                                    }
                                    .addOnCompleteListener {
                                        toast("Se ha enviado el pago a todos los miembros del grupo")
                                        editTextPaymentGroup.setText("")
                                    }
                            } else {
                                toast("Se ha enviado el pago a todos los miembros del grupo")
                                editTextPaymentGroup.setText("")
                            }
                        }

                } else {
                    toast("Se ha enviado el pago a todos los miembros del grupo")
                    editTextPaymentGroup.setText("")
                }
        }

    }


}


