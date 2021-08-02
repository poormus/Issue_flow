package com.mussrose.issueflower.ui.main.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterIssuesForProject
import com.mussrose.issueflower.databinding.FragmentAllIssuesProjectBinding
import com.mussrose.issueflower.entities.Bookmark
import com.mussrose.issueflower.ui.main.fragments.dialog.DialogReportIssue
import com.mussrose.issueflower.ui.main.viewmodel.BookMarkViewModel
import com.mussrose.issueflower.ui.main.viewmodel.IssueViewModel
import com.mussrose.issueflower.ui.main.viewmodel.SortIssueBy
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AllProjectIssuesFragment : Fragment(R.layout.fragment_all_issues_project),
    AdapterIssuesForProject.OnTagClicked {

    lateinit var binding: FragmentAllIssuesProjectBinding
    lateinit var adapterIssuesForProject: AdapterIssuesForProject
    private val viewModel: IssueViewModel by viewModels()
    private val bookMarkViewModel: BookMarkViewModel by viewModels()
    private var currentUpVoteIndex: Int? = null
    private var currentSort=SortIssueBy.DATEASCENDING


    fun getInstance(projectId: String): AllProjectIssuesFragment {
        val fragment = AllProjectIssuesFragment()
        val bundle = Bundle()
        bundle.putString("projectId", projectId)
        fragment.arguments = bundle
        return fragment
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllIssuesProjectBinding.bind(view)
        val projectId = arguments?.getString("projectId")
        setRv()
        stateFlow(projectId)
        setSpinner()
        binding.ibSearch.setOnClickListener {
            findNavController().navigate(
                AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToIssueSearchFragment(
                    "aa",""
                )
            )
        }
        binding.spSort.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0->{

                        currentSort=SortIssueBy.DATEASCENDING
                        viewModel.getAllIssuesForProject(projectId!!,currentSort)
                    }
                    1->{

                        currentSort=SortIssueBy.DATEDESCENDING
                        viewModel.getAllIssuesForProject(projectId!!,currentSort)
                    }
                    2->{

                        currentSort=SortIssueBy.MOSTUPVOTED
                        viewModel.getAllIssuesForProject(projectId!!,currentSort)
                    }
                    3->{

                        currentSort=SortIssueBy.MOSTCOMMENTED
                        viewModel.getAllIssuesForProject(projectId!!,currentSort)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?)=Unit

        }
        binding.strAllIssues.setOnRefreshListener {
            viewModel.getAllIssuesForProject(projectId!!,currentSort)
        }
        adapterIssuesForProject.setOnIssueClickedListener {
            findNavController().navigate(
                AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToIssueDetailFragment(
                    it
                )
            )
        }
        adapterIssuesForProject.setOnUpVoteClickListener { issue, i ->
            currentUpVoteIndex = i
            issue.isUpVoted = !issue.isUpVoted
            viewModel.toggleUpVote(issue)
        }

        adapterIssuesForProject.setOnMoreClickedListener { s, v ->
            val popUp = PopupMenu(requireContext(), v)
            popUp.inflate(R.menu.project_issue_popup_menu)
            popUp.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.miReport -> {
                        DialogReportIssue().show(childFragmentManager, null)
                    }
                    R.id.miBookMArk -> {

                        bookMarkViewModel.toggleBookMark(s)
                    }
                }
                true
            }
            popUp.show()
        }
    }

    private fun setSpinner() {
        val spinnerAdapter=ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,
        resources.getStringArray(R.array.sort)
        )
        binding.spSort.adapter=spinnerAdapter
    }

    private fun setRv() {
        adapterIssuesForProject = AdapterIssuesForProject(requireContext(), this)
        binding.rvAllIssuesForProject.apply {
            adapter = adapterIssuesForProject
        }
    }

    private fun stateFlow(projectId: String?) {
        viewModel.getAllIssuesForProject(projectId!!,currentSort)
        lifecycleScope.launchWhenCreated {
            viewModel.getAllIssueStatus.collect { event ->
                when (event) {
                    is IssueViewModel.GetIssueEvent.Success -> {
                        val issues = event.issues
                        adapterIssuesForProject.issues = issues
                        binding.strAllIssues.isRefreshing = false
                        binding.progressAllIssuesForProject.isVisible = false
                        if (issues.isEmpty()) {
                            binding.tvNoIssueWarningProject.isVisible = true
                            binding.ivNoIssueWarningProject.isVisible = true

                        }else{
                            binding.tvNoIssueWarningProject.isVisible = false
                            binding.ivNoIssueWarningProject.isVisible = false
                        }
                    }
                    is IssueViewModel.GetIssueEvent.Loading -> {
                        binding.progressAllIssuesForProject.isVisible = true
                        binding.strAllIssues.isRefreshing = true
                    }
                    is IssueViewModel.GetIssueEvent.Error -> {
                        binding.strAllIssues.isRefreshing = false
                        binding.progressAllIssuesForProject.isVisible = false
                        binding.tvNoIssueWarningProject.isVisible = true
                        binding.tvNoIssueWarningProject.text = "an error occurred"
                        snackBar(event.errorText)
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.toggleUpVoteStatus.collect { event ->
                when (event) {
                    is IssueViewModel.ToggleTotalUpVotesEvent.Success -> {
                        val isUpVoted = event.isUpVoted
                        currentUpVoteIndex?.let { index ->
                            val uid = FirebaseAuth.getInstance().uid!!
                            adapterIssuesForProject.issues[index].apply {
                                this.isUpVoted = isUpVoted
                                this.isUpVoting = false
                                if (isUpVoted) {
                                    totalUpVotes += uid
                                } else {
                                    totalUpVotes -= uid
                                }
                            }
                            adapterIssuesForProject.notifyItemChanged(index)
                        }

                    }
                    is IssueViewModel.ToggleTotalUpVotesEvent.Error -> {
                        currentUpVoteIndex?.let { index ->
                            adapterIssuesForProject.issues[index].isUpVoting = false
                            adapterIssuesForProject.notifyItemChanged(index)
                        }
                    }
                    is IssueViewModel.ToggleTotalUpVotesEvent.Loading -> {
                        currentUpVoteIndex?.let { index ->
                            adapterIssuesForProject.issues[index].isUpVoting = true
                            adapterIssuesForProject.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }

    override fun setOnTagClickForSearchListener(query: String) {
        val projectId = arguments?.getString("projectId")
        findNavController().navigate(
            AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToIssueSearchFragment(
                projectId!!,
                query
            )
        )
    }
}