package com.mussrose.issueflower.ui.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mussrose.issueflower.ui.auth.fragment.LogInFragment
import com.mussrose.issueflower.ui.auth.fragment.SignUpFragment

class AuthPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int =2

    override fun createFragment(position: Int): Fragment {
         return when(position){
            0->{
                LogInFragment()
            }
            1->{
                SignUpFragment()
            }
            else -> {
                LogInFragment()
            }
        }

    }


}