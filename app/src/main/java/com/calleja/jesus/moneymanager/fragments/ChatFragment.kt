package com.calleja.jesus.moneymanager.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.calleja.jesus.moneymanager.R

class ChatFragment : Fragment() {

    private lateinit var _view: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _view =  inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatBtn()

        return _view
    }

    private fun setUpChatDB(){

    }

    private fun setUpCurrentUser(){

    }

    private fun setUpRecyclerView(){

    }

    private fun setUpChatBtn(){

    }

}


