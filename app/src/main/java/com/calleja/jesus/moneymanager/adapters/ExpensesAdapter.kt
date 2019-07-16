package com.calleja.jesus.moneymanager.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.calleja.jesus.moneymanager.R
import com.calleja.jesus.moneymanager.inflate
import com.calleja.jesus.moneymanager.models.Expense
import kotlinx.android.synthetic.main.fragment_expenses_fragment.view.*


class ExpensesAdapter(private val items: List<Expense> ) : RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_expenses_fragment))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(expense: Expense) = with(itemView){
            textViewExpenseDescription.text = expense.message
            textViewExpenseCategory.text = expense.category
            textViewExpenseAmount.text = expense.amount + " EUR"
        }
    }
}