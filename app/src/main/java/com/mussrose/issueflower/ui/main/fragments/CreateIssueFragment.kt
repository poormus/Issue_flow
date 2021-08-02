package com.mussrose.issueflower.ui.main.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterTag
import com.mussrose.issueflower.databinding.FragmentCreateIssueBinding
import com.mussrose.issueflower.entities.Issue
import com.mussrose.issueflower.entities.updateentities.IssueUpdate
import com.mussrose.issueflower.ui.main.fragments.dialog.DialogAddTag
import com.mussrose.issueflower.ui.main.viewmodel.IssueViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CreateIssueFragment:Fragment(R.layout.fragment_create_issue) {

    lateinit var binding:FragmentCreateIssueBinding
    private val args:CreateIssueFragmentArgs by navArgs()
    private val viewModel:IssueViewModel by viewModels()

    private lateinit var adapterTag: AdapterTag

    val tags: MutableList<String> = mutableListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentCreateIssueBinding.bind(view)
        setRv()
        stateFlow()
        args.issueToUpdate?.let{
            setViewForUpdate(it)
        }
        binding.btCreateIssueTag.setOnClickListener {
            DialogAddTag().apply {
                setPositiveListener { tag->
                    tags.add(tag)
                    adapterTag.notifyDataSetChanged()
                }
            }.show(parentFragmentManager, "AddTag")
            adapterTag.notifyDataSetChanged()
        }
        binding.btCreateIssue.setOnClickListener {
            val title = binding.etCreateIssueTitle.text.toString()
            val description = binding.etCreateIssueDesc.text.toString()
            viewModel.createIssue(title,description,tags.toList(),"open",args.projectId)
        }
        binding.btUpdateIssue.setOnClickListener {
            val title=binding.etCreateIssueTitle.text.toString()
            val description=binding.etCreateIssueDesc.text.toString()
            val state=if(binding.cbIssueClosed.isChecked) "closed" else "open"
            val issueUpdate=IssueUpdate(args.issueToUpdate!!.id,title,description,state)
            viewModel.updateIssue(issueUpdate)
        }

        binding.etCreateIssueDesc.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
        object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(v?.id==R.id.etCreateIssueDesc){
                    v.parent.requestDisallowInterceptTouchEvent(true)


                }
                return false
            }


        })
    }

    private fun setViewForUpdate(issue:Issue) {
        binding.btCreateIssue.visibility = View.GONE
        binding.btCreateIssueTag.visibility = View.GONE
        binding.rvCreateIssue.visibility = View.GONE
        binding.btUpdateIssue.visibility = View.VISIBLE
        binding.cbIssueClosed.visibility = View.VISIBLE
        binding.etCreateIssueDesc.setText(issue.description)
        binding.etCreateIssueTitle.setText(issue.title)
        if (issue.state=="open"){
            binding.cbIssueClosed.isChecked=false
        }else if(issue.state=="closed"){
            binding.cbIssueClosed.isChecked=true
        }
    }

    private fun stateFlow() {
        lifecycleScope.launchWhenCreated {
            viewModel.createIssueStatus.collect { event->
                when(event){
                    is IssueViewModel.CreateIssueEvent.Success->{
                        binding.pbCreateIssue.isVisible=false
                        binding.btCreateIssue.isEnabled=true
                        findNavController().popBackStack()
                    }
                    is IssueViewModel.CreateIssueEvent.Error->{
                        snackBar(event.errorText)
                        binding.pbCreateIssue.isVisible=false
                        binding.btCreateIssue.isEnabled=true
                    }
                    is IssueViewModel.CreateIssueEvent.Loading->{
                        binding.pbCreateIssue.isVisible=true
                        binding.btCreateIssue.isEnabled=false
                    }
                    else -> {

                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.updateIssueStatus.collect { event->
                when(event){
                    is IssueViewModel.UpdateIssueEvent.Success->{
                        binding.pbUpdate.isVisible=false
                        findNavController().popBackStack()
                    }
                    is IssueViewModel.UpdateIssueEvent.Error->{
                        binding.pbUpdate.isVisible=false
                        binding.btUpdateIssue.isEnabled=true
                        snackBar(event.errorText)
                    }
                    is IssueViewModel.UpdateIssueEvent.Loading->{
                        binding.pbUpdate.isVisible=true
                        binding.btUpdateIssue.isEnabled=false
                    }
                    else->{}
                }
            }
        }

    }

    private fun setRv() {
        adapterTag = AdapterTag(tags)
        binding.rvCreateIssue.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterTag

        }
    }
}