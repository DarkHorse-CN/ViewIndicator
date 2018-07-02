package com.darkhorse.viewindicator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.AbsListView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mIndicator.init(arrayOf("tab1", "tab2", "tab3", "tab4", "tab5", "tab6", "tab7","tab8","tab9","tab10"),object : IViewClickListener{
            override fun onViewClickListener(position: Int) {
                mViewPager.setCurrentItem(position,false)
            }
        })

        val arrayList = ArrayList<TabFragment>()
        for (i in 0 until 10) {
            arrayList.add(TabFragment("Tab$i"))
        }
        mViewPager.adapter = VpAdapter(supportFragmentManager, arrayList)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                mIndicator.invalidate(p0, p1)
            }

            override fun onPageSelected(p0: Int) {
            }
        })
    }
}
