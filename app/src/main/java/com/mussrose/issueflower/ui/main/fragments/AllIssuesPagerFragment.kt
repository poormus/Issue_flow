package com.mussrose.issueflower.ui.main.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.FragmentPagerAllIssuesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllIssuesPagerFragment : Fragment(R.layout.fragment_pager_all_issues) {
    lateinit var binding: FragmentPagerAllIssuesBinding
    private val args: AllIssuesPagerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPagerAllIssuesBinding.bind(view)
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.INVISIBLE
        val tabLayout = requireActivity().findViewById<TabLayout>(R.id.tablayoutAllIssues)
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPageAllIssues)
        val pagerAdapter = AllIssuesPagerAdapter(requireActivity(), args.projectId)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab, position ->

            tab.text = when (position) {
                0 -> {
                    "All issues"
                }
                1 -> {
                    "Your issues"
                }
                2 -> {

                    "Bookmarks"
                }

                else -> ""
            }
            viewPager.isUserInputEnabled = false
        }.attach()

    }
}