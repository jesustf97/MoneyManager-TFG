package com.calleja.jesus.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.calleja.jesus.moneymanager.activities.LoginActivity
import com.calleja.jesus.mylibrary.interfaces.ToolbarActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_admin.*
import java.util.ArrayList


class MainActivityAdmin : ToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)
        setUpCreateGroupButton()
        setSupportActionBar(toolbarAdmin)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToActivity<LoginActivity>{
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }

        }
        return super.onOptionsItemSelected(item)
}
    private fun setUpCreateGroupButton() {
        create_group_button.setOnClickListener{
        val iban1 = editTextCreateGroup1.text.toString()
        val iban2 = editTextCreateGroup2.text.toString()
        val iban3 = editTextCreateGroup3.text.toString()
        val groupIbans : ArrayList<String> = ArrayList()

        if (iban1.isNotEmpty()){
            groupIbans.add(iban1)
        }
        if (iban2.isNotEmpty()){
            groupIbans.add(iban2)
        }
        if (iban3.isNotEmpty()){
            groupIbans.add(iban3)
        }

        if (groupIbans.size == 3) {
            val intent = Intent(this, GroupBalanceActivity::class.java)
            intent.putExtra("ibans", groupIbans)
            startActivity(intent)
        } else {
            toast("Debe introducir el número de cuenta de los participantes para crear un grupo")
        }
        }
    }

}
