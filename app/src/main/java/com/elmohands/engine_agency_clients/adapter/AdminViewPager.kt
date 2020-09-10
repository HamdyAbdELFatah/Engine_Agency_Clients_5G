package com.elmohands.engine_agency_clients.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elmohands.engine_agency_clients.fragments.*

class AdminViewPager (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments:ArrayList<Fragment> = arrayListOf(
        AdminClientsFragment(),
        AdminPaidFragments(),
        AdminTaxFragments()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}