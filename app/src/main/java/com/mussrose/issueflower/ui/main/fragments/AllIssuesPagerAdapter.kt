package com.mussrose.issueflower.ui.main.fragments

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AllIssuesPagerAdapter(fa: FragmentActivity, private val projectId: String) :
    FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AllProjectIssuesFragment().getInstance(projectId)
            }
            1 -> {
                AllIssuesByUserFragment().getInstance(projectId)
            }
            2 -> {
                BookMarkFragment()
            }

            else -> {
                AllProjectIssuesFragment().getInstance(projectId)
            }
        }


    }


}