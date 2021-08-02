package com.mussrose.issueflower.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.ui.main.IssueActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity_1)

        if(FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this,IssueActivity::class.java))
            finish()
        }

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_auth)
        val navController = navHostFragment!!.findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.authViewPagerFragment, R.id.tourFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_auth)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}