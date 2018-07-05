package com.darkhorse.viewindicator

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_rcv_tab.view.*

@SuppressLint("ValidFragment")
/**
 * Description:
 * Created by DarkHorse on 2018/6/28.
 */
class TabFragment @SuppressLint("ValidFragment") constructor
(private var tab: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.item_rcv_tab,null,false)
        view.tv_title.text = tab
        view.tv_title.setOnClickListener{
            startActivity(Intent(activity,SecondActivity::class.java))
        }
        return view
    }
}
