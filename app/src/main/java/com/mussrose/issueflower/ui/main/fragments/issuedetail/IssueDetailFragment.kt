package com.mussrose.issueflower.ui.main.fragments.issuedetail

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.FragmentIssueDetailBinding
import com.mussrose.issueflower.ui.main.viewmodel.UserViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class IssueDetailFragment : Fragment(R.layout.fragment_issue_detail) {


    private var isClicked = false
    lateinit var binding: FragmentIssueDetailBinding
    private val args: IssueDetailFragmentArgs by navArgs()
    private val viewModel: UserViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIssueDetailBinding.bind(view)

        stateFlow()
        setView()

        binding.btAddComment.setOnClickListener {
            val issueId = args.issue.id
            InsertCommentBottomDialog().getInstance(issueId).show(childFragmentManager, null)
        }
        binding.btShowComments.setOnClickListener {
            ViewCommentsBottomDialog().getInstance(args.issue.id).show(childFragmentManager,null)
        }
        binding.btViewMoreIssueDescp.setOnClickListener {
            if (isClicked) {
                binding.btViewMoreIssueDescp.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                binding.tvIssueDetail.maxLines = 8
                isClicked = false
                TransitionManager.beginDelayedTransition(
                    binding.clIssueDetailDescp,
                    AutoTransition()
                )
            } else {
                TransitionManager.beginDelayedTransition(
                    binding.clIssueDetailDescp,
                    AutoTransition()
                )
                binding.btViewMoreIssueDescp.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                binding.tvIssueDetail.maxLines = Int.MAX_VALUE
                isClicked = true
            }
        }

    }

    private fun stateFlow() {
        val uid = FirebaseAuth.getInstance().uid
        viewModel.getUser(uid!!)
        lifecycleScope.launchWhenCreated {
            viewModel.curUserStatus.collect { event ->
                when (event) {
                    is UserViewModel.GetUserEvent.Success -> {
                        glide.load(event.user.profilePicUrl).into(binding.ivUserPic)
                    }
                    is UserViewModel.GetUserEvent.Error -> {
                        snackBar(event.errorText)
                    }
                    is UserViewModel.GetUserEvent.Loading -> {

                    }
                    else -> {
                    }
                }
            }
        }
    }


    private fun setView() {
        if (args.issue.state == "closed") {
            binding.tvIssueClosed.visibility = View.VISIBLE
            binding.ivUserPicBackGround.visibility = View.GONE
            binding.ivUserPic.visibility = View.GONE
            binding.btAddComment.visibility = View.GONE
        }
        val issue = args.issue
        binding.tvIssueDetailTitle.text = issue.title
        setMArkDownText(issue.description)
    }

    private fun setMArkDownText(text:String){
        val markWon=Markwon.create(requireContext())
        val markDown=markWon.toMarkdown(text)
        markWon.setParsedMarkdown(binding.tvIssueDetail,markDown)
    }

}