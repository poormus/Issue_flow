package com.mussrose.issueflower.ui.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mussrose.issueflower.R
import com.mussrose.issueflower.ui.auth.AuthPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthViewPagerFragment : Fragment(R.layout.activity_auth) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabLayout = requireActivity().findViewById<TabLayout>(R.id.tabLayoutLogIn)
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
        val pagerAdapter = AuthPagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab, position -> // Styling each tab here
            tab.text = when (position) {
                0 -> {
                    "Log-In"
                }
                1 -> {
                    "Sign-Up"
                }
                else -> ""
            }
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_baseline_login_24)
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_baseline_person_24)
                }
            }


        }.attach()
    }

}

