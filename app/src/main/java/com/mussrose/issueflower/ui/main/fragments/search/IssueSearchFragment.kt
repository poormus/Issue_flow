package com.mussrose.issueflower.ui.main.fragments.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterIssuesForProject
import com.mussrose.issueflower.databinding.FragmentSearchIssueBinding
import com.mussrose.issueflower.ui.main.fragments.dialog.DialogReportIssue
import com.mussrose.issueflower.ui.main.viewmodel.BookMarkViewModel
import com.mussrose.issueflower.ui.main.viewmodel.IssueViewModel
import com.mussrose.issueflower.ui.main.viewmodel.SearchViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class IssueSearchFragment: Fragment(R.layout.fragment_search_issue),AdapterIssuesForProject.OnTagClicked {

    lateinit var binding: FragmentSearchIssueBinding

    private val viewModel:SearchViewModel by viewModels()
    private val args:IssueSearchFragmentArgs by navArgs()
    private lateinit var adapterIssuesForSearch:AdapterIssuesForProject
    private val bookMarkViewModel: BookMarkViewModel by viewModels()
    private val issueViewModel: IssueViewModel by viewModels()
    private var currentUpVoteIndex: Int? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.issue_search,menu)
        val searchItem=menu.findItem(R.id.miIssueSearch)
        val searchView=searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint="search..."
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let{
                    viewModel.searchIssue(query,args.projectId)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentSearchIssueBinding.bind(view)
        setHasOptionsMenu(true)
        setRv()
        viewModel.searchIssue(args.query,args.projectId)
        stateFlow()
        adapterIssuesForSearch.setOnIssueClickedListener { issue->
            findNavController().navigate(
                IssueSearchFragmentDirections.actionIssueSearchFragmentToIssueDetailFragment(
                    issue
                )
            )
        }

        adapterIssuesForSearch.setOnUpVoteClickListener { issue, i ->
            currentUpVoteIndex = i
            issue.isUpVoted = !issue.isUpVoted
            issueViewModel.toggleUpVote(issue)
        }

        adapterIssuesForSearch.setOnMoreClickedListener { s, v ->
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

    private fun setRv() {
        adapterIssuesForSearch= AdapterIssuesForProject(requireContext(),this)
        binding.rvSearchIssue.apply {
            adapter=adapterIssuesForSearch
        }
    }

    private fun stateFlow() {

        lifecycleScope.launchWhenCreated {
            viewModel.searchIssueStatus.collect { event->
                when(event){
                    is SearchViewModel.SearchIssueEvent.Success->{
                        binding.pbSearchIssue.isVisible=false
                        val issues=event.issues

                        if(issues.isEmpty()){
                            binding.ivSearchNoResult.isVisible=true
                            binding.tvSearchNoResult.isVisible=true
                        }else{
                            binding.ivSearchNoResult.isVisible=false
                            binding.tvSearchNoResult.isVisible=false
                        }
                        adapterIssuesForSearch.issues=issues

                    }
                    is SearchViewModel.SearchIssueEvent.Error->{
                        binding.pbSearchIssue.isVisible=false
                        binding.tvSearchNoResult.isVisible=true
                        snackBar(event.errorText)
                    }
                    is SearchViewModel.SearchIssueEvent.Loading->{
                        binding.pbSearchIssue.isVisible=true
                    }
                    else -> {

                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            issueViewModel.toggleUpVoteStatus.collect { event ->
                when (event) {
                    is IssueViewModel.ToggleTotalUpVotesEvent.Success -> {
                        val isUpVoted = event.isUpVoted
                        currentUpVoteIndex?.let { index ->
                            val uid = FirebaseAuth.getInstance().uid!!
                            adapterIssuesForSearch.issues[index].apply {
                                this.isUpVoted = isUpVoted
                                this.isUpVoting = false
                                if (isUpVoted) {
                                    totalUpVotes += uid
                                } else {
                                    totalUpVotes -= uid
                                }
                            }
                            adapterIssuesForSearch.notifyItemChanged(index)
                        }

                    }
                    is IssueViewModel.ToggleTotalUpVotesEvent.Error -> {
                        currentUpVoteIndex?.let { index ->
                            adapterIssuesForSearch.issues[index].isUpVoting = false
                            adapterIssuesForSearch.notifyItemChanged(index)
                        }
                    }
                    is IssueViewModel.ToggleTotalUpVotesEvent.Loading -> {
                        currentUpVoteIndex?.let { index ->
                            adapterIssuesForSearch.issues[index].isUpVoting = true
                            adapterIssuesForSearch.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }



    override fun setOnTagClickForSearchListener(query: String) {
    }


}