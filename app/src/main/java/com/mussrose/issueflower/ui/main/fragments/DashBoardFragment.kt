package com.mussrose.issueflower.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterProject
import com.mussrose.issueflower.databinding.FragmentDashboardBinding
import com.mussrose.issueflower.entities.Project
import com.mussrose.issueflower.ui.main.viewmodel.ProjectViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DashBoardFragment:Fragment(R.layout.fragment_dashboard) {

    lateinit var binding:FragmentDashboardBinding

    private val viewModel:ProjectViewModel by viewModels()

    lateinit var adapterProject: AdapterProject

    override fun onResume() {
        super.onResume()
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentDashboardBinding.bind(view)
        createProjects()
        setRv()
        adapterProject.setOnProjectAdapterClick { project->
            if (project.state=="closed"){
                snackBar("opening soon")
                return@setOnProjectAdapterClick

            }
            findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToAllIssuesPagerFragment(
                project.id
            ))
        }
        stateFlow()

    }

    private fun createProjects() {

    }

    private fun stateFlow() {
        viewModel.getAllProjects()
        lifecycleScope.launchWhenCreated {
            viewModel.getAllProjectStatus.collect { event->

                when(event){
                    is ProjectViewModel.GetEntityEvent.Success->{
                        adapterProject.projects=event.event
                        binding.progressbarDashBoard.isVisible=false
                        binding.tvDashBoardWarning.isVisible=false

                    }
                    is ProjectViewModel.GetEntityEvent.Loading->{

                        binding.progressbarDashBoard.isVisible=true
                    }
                    is ProjectViewModel.GetEntityEvent.Error->{
                        snackBar(event.errorText)
                        binding.tvDashBoardWarning.text=event.errorText
                        binding.tvDashBoardWarning.isVisible=true
                        binding.progressbarDashBoard.isVisible=false

                    }
                    else -> {}
                }

            }
        }
    }

    private fun setRv() {
        adapterProject= AdapterProject()
        binding.rvAllProjects.apply {
            adapter=adapterProject
        }
    }
}