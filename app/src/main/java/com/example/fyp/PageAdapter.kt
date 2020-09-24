package com.example.fyp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {return DeliveryFragment()}
            1 -> {return RestaurantFragment()}
            2 -> {return CommunityFragment()}
            else -> {return DeliveryFragment()}
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> (return "Delivery")
            1 -> (return "Restaurants")
            2 -> (return "Community")
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 3
    }
}