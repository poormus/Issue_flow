package com.mussrose.issueflower.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterBookMarkedIssues
import com.mussrose.issueflower.databinding.FragmentBookmarkBinding
import com.mussrose.issueflower.ui.main.viewmodel.BookMarkViewModel
import com.mussrose.issueflower.ui.main.viewmodel.IssueViewModel
import com.mussrose.issueflower.ui.main.viewmodel.UserViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BookMarkFragment : Fragment(R.layout.fragment_bookmark) {

    lateinit var binding: FragmentBookmarkBinding
    private lateinit var adapterBookmark: AdapterBookMarkedIssues
    private val bookMarkViewModel: BookMarkViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBookmarkBinding.bind(view)
        setRv()
        stateFlow()
        adapterBookmark.setOnIssueClickedListener { issue ->
            findNavController().navigate(
                AllIssuesPagerFragmentDirections.actionAllIssuesPagerFragmentToIssueDetailFragment(
                    issue
                )
            )
        }
        adapterBookmark.setOnBookMarkClickListener { issue ->
            adapterBookmark.issues-=issue
            bookMarkViewModel.toggleBookMark(issue.id)
            adapterBookmark.notifyDataSetChanged()
        }
    }

    private fun setRv() {
        adapterBookmark = AdapterBookMarkedIssues(requireContext())
        binding.rvBookMark.adapter = adapterBookmark
    }

    private fun stateFlow() {
        val uid = FirebaseAuth.getInstance().uid!!
        bookMarkViewModel.getBookMark(uid)
        lifecycleScope.launchWhenCreated {
            bookMarkViewModel.getBookMarkStatus.collect { event ->
                when (event) {
                    is BookMarkViewModel.BookMarkIssueEvent.Success -> {
                        val issues = event.issues
                        binding.pbBookmark.isVisible = false
                        adapterBookmark.issues = issues
                        if(issues.isEmpty()){
                            binding.ivNoBookmark.isVisible=true
                            binding.tvNoBookmark.isVisible=true
                        }else{
                            binding.ivNoBookmark.isVisible=false
                            binding.tvNoBookmark.isVisible=false
                        }
                    }
                    is BookMarkViewModel.BookMarkIssueEvent.Error -> {
                        binding.pbBookmark.isVisible = false
                        snackBar(event.errorText)
                    }
                    is BookMarkViewModel.BookMarkIssueEvent.Loading -> {
                        binding.pbBookmark.isVisible = true
                    }
                }
            }
        }
    }
}