package com.calleja.jesus.moneymanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.adapters.ExpensesAdapter
import com.calleja.jesus.moneymanager.models.Expense
import com.calleja.jesus.moneymanager.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_expenses.view.*
import java.util.ArrayList
import java.util.EventListener

class ExpensesFragment : Fragment() {

    private lateinit var _view: View
    private lateinit var adapter: ExpensesAdapter
    private var expensesList: ArrayList<Expense> = ArrayList()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var expensesDBRef: CollectionReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_expenses, container, false)

        setUpExpensesDB()
        setUpCurrentUser()

        setUpRecyclerView()

        subscribeToRatings()

        return _view
    }


    private fun setUpExpensesDB(){
        expensesDBRef = store.collection("expenses")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = ExpensesAdapter(expensesList)
        _view.recyclerViewExpensesFragment.setHasFixedSize(true)
        _view.recyclerViewExpensesFragment.layoutManager = layoutManager
        _view.recyclerViewExpensesFragment.itemAnimator = DefaultItemAnimator()
        _view.recyclerViewExpensesFragment.adapter = adapter

    }

    private fun subscribeToRatings() {
          expensesDBRef
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        activity!!.toast("Excepción")
                        return
                    }

                    snapshot?.let {
                        expensesList.clear()
                        val expenses = it.toObjects(Expense::class.java)
                        val privateExpenses = removePublicExpenses(expenses)
                        expensesList.addAll(privateExpenses)
                        adapter.notifyDataSetChanged()
                        _view.recyclerViewExpensesFragment.smoothScrollToPosition(0)
                    }
                }
            })
    }

    private fun removePublicExpenses(publicExpensesList :List<Expense>): List<Expense> {
        val privateExpensesList :ArrayList<Expense> = ArrayList()
        val iterator = publicExpensesList.iterator()
        while(iterator.hasNext()){
            val expense: Expense = iterator.next()
            if(expense.id == currentUser.uid){
                    privateExpensesList.add(expense)
                }
        }
        return privateExpensesList
    }
}
