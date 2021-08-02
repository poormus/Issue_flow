package com.mussrose.issueflower.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterIssuesByUser
import com.mussrose.issueflower.databinding.FragmentAllIssuesByUserBinding
import com.mussrose.issueflower.entities.Issue
import com.mussrose.issueflower.others.Extension.startAnimation
import com.mussrose.issueflower.ui.main.viewmodel.IssueViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AllIssuesByUserFragment : Fragment(R.layout.fragment_all_issues_by_user) {

    lateinit var binding: FragmentAllIssuesByUserBinding

    fun getInstance(projectId: String): AllIssuesByUserFragment {
        val fragment = AllIssuesByUserFragment()
        val bundle = Bundle()
        bundle.putString("projectId", projectId)
        fragment.arguments = bundle
        return fragment
    }

    private lateinit var adapterIssuesByUser: AdapterIssuesByUser
    private val viewModel: IssueViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllIssuesByUserBinding.bind(view)
        val projectId = arguments?.getString("projectId")

        setRv()
        stateFlow(projectId)

        binding.strIssueByUser.setOnRefreshListener {
            viewModel.getAllIssuesByUser(projectId!!)
        }

        adapterIssuesByUser.setOnUpdateIssueClickListener { issue ->
            findNavController().navigate(
                AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToCreateIssueFragment(
                    projectId!!,
                    issue
                )
            )
        }
        binding.fabCreateIssue.setOnClickListener {
            findNavController().navigate(
                AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToCreateIssueFragment(
                    projectId!!,
                    null
                )
            )
        }
        adapterIssuesByUser.setOnDeleteIssueClickListener { issue ->
            viewModel.deleteIssue(issue)
        }
        adapterIssuesByUser.setOnIssueClickedListener {
            findNavController().navigate(
                AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToIssueDetailFragment(
                    it
                )
            )
        }
    }

    private fun setRv() {
        adapterIssuesByUser = AdapterIssuesByUser(requireContext())
        binding.rvIssueByUser.apply {
            adapter = adapterIssuesByUser
            addOnScrollListener(onScrollListener)
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_bottom)
                binding.fabCreateIssue.startAnimation(animation) {
                    binding.fabCreateIssue.visibility = View.GONE
                }
            }
            if (dy < 0) {
                val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up)
                binding.fabCreateIssue.startAnimation(animation) {
                    binding.fabCreateIssue.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun stateFlow(projectId: String?) {
        viewModel.getAllIssuesByUser(projectId!!)
        lifecycleScope.launchWhenCreated {
            viewModel.getAllIssueByStatus.collect { event ->
                when (event) {
                    is IssueViewModel.GetIssueEvent.Success -> {
                        binding.strIssueByUser.isRefreshing=false
                        val issues = event.issues
                        adapterIssuesByUser.issues = issues
                        binding.progressbarUserIssues.isVisible = false
                        if (issues.isEmpty()) {
                            binding.ivNoIssuesByUser.isVisible=true
                            binding.tvAllIssuesUserWarning.isVisible = true
                            binding.tvAllIssuesUserWarning.text = "you have no issues yet"
                        }else{
                            binding.tvAllIssuesUserWarning.isVisible = false
                            binding.ivNoIssuesByUser.isVisible=false
                        }

                    }
                    is IssueViewModel.GetIssueEvent.Loading -> {

                        binding.progressbarUserIssues.isVisible = true
                    }
                    is IssueViewModel.GetIssueEvent.Error -> {
                        binding.progressbarUserIssues.isVisible = false
                        binding.tvAllIssuesUserWarning.isVisible = true
                        binding.tvAllIssuesUserWarning.text = "an error occurred"
                        snackBar(event.errorText)
                    }
                    else -> {
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.deleteIssueStatus.collect { event ->
                when (event) {
                    is IssueViewModel.DeleteIssueEvent.Success -> {
                        adapterIssuesByUser.issues -= event.issue
                        snackBar("issue deleted")
                    }
                    is IssueViewModel.DeleteIssueEvent.Error -> {
                        snackBar(event.errorText)
                    }
                    is IssueViewModel.DeleteIssueEvent.Loading -> {

                    }
                    else -> {
                    }
                }
            }
        }
    }
}