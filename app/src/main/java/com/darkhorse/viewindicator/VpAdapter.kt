package com.darkhorse.viewindicator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Description:
 * Created by DarkHorse on 2018/6/28.
 */
class VpAdapter(fm: FragmentManager, private var mFragments: ArrayList<TabFragment>) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment = mFragments[p0]

    override fun getCount(): Int = mFragments.size


}