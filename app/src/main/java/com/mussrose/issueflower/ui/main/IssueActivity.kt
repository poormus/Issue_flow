package com.mussrose.issueflower.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.ActivityIssueBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssueActivity : AppCompatActivity() {

    lateinit var binding: ActivityIssueBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        navView.visibility = View.VISIBLE

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_issues)
        val navController = navHostFragment!!.findNavController()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashBoardFragment, R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_issues)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}