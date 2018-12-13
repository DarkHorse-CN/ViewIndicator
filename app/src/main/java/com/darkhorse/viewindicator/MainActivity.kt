package com.darkhorse.viewindicator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.AbsListView
import com.darkhorse.viewindicator.R.id.mIndicator
import com.darkhorse.viewindicator.R.id.mViewPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        mIndicator.init(arrayOf("tab1", "tab2", "tab3", "tab4", "tab5", "tab6", "tab7"), object : TabItemClickListener {
//            override fun onTabItemClickListener(position: Int) {
//                mViewPager.setCurrentItem(position, false)
//            }
//        })
        mIndicator.init(arrayOf("tab1", "tab2", "tab3", "tab4", "tab5", "tab6", "tab7"), mViewPager)

        val arrayList = ArrayList<TabFragment>()
        for (i in 1 .. 7) {
            arrayList.add(TabFragment("Tab$i"))
        }
        mViewPager.adapter = VpAdapter(supportFragmentManager, arrayList)

    }
}
