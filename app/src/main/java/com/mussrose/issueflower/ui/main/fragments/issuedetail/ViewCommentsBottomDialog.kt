package com.mussrose.issueflower.ui.main.fragments.issuedetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterComment
import com.mussrose.issueflower.databinding.BottomDialogAllCommentsBinding
import com.mussrose.issueflower.ui.main.viewmodel.CommentViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class ViewCommentsBottomDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var glide:RequestManager

    private val viewModel:CommentViewModel by viewModels()


    lateinit var binding: BottomDialogAllCommentsBinding

    private lateinit var adapterComment:AdapterComment

    private var currentLikeIndex:Int?=null

    fun getInstance(issueId: String): ViewCommentsBottomDialog {
        val dialog = ViewCommentsBottomDialog()
        val bundle = Bundle()
        bundle.putString("issueIdForComment", issueId)
        dialog.arguments = bundle
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.bottom_dialog_all_comments, container, false)
        binding= BottomDialogAllCommentsBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val issueId = arguments?.getString("issueIdForComment")
        setRv()
        stateFlow(issueId!!)


        adapterComment.setOnCommentDeleteListener { comment ->
            viewModel.deleteComment(comment)
        }

        adapterComment.setOnLikeClickedListener { comment, i ->
            currentLikeIndex=i
            comment.isLiked=!comment.isLiked
            viewModel.updateTotalCommentLike(comment)
        }



    }

    private fun stateFlow(issueId: String) {
        viewModel.getCommentsForIssue(issueId)
        lifecycleScope.launchWhenCreated {
            viewModel.getCommentsStatus.collect { event->
                when(event){
                    is CommentViewModel.GetCommentsEvent.Success->{
                        val comments=event.comments
                        binding.tvAllCommentsBottomSheet.text="${comments.size} Comments"
                        binding.pbComments.isVisible=false
                        if (comments.isEmpty()){
                            binding.tvNoCommentWarning.isVisible=true
                        }
                        adapterComment.comments=comments
                    }
                    is CommentViewModel.GetCommentsEvent.Error->{
                        snackBar(event.errorText)
                        binding.pbComments.isVisible=false
                    }
                    is CommentViewModel.GetCommentsEvent.Loading->{
                        binding.pbComments.isVisible=true
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.deleteCommentsStatus.collect { event->
                when(event){
                    is CommentViewModel.DeleteCommentEvent.Success->{
                        val comment=event.comment
                        adapterComment.comments -= comment
                        binding.pbComments.isVisible=false
                        viewModel.getCommentsForIssue(issueId)
                    }
                    is CommentViewModel.DeleteCommentEvent.Error->{
                        snackBar(event.errorText)
                        binding.pbComments.isVisible=false
                    }
                    is CommentViewModel.DeleteCommentEvent.Loading->{
                        binding.pbComments.isVisible=true
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.commentLikeStatus.collect { event->
                when(event){
                    is CommentViewModel.UpdateTotalCommentLikeEvent.Success->{
                        currentLikeIndex?.let { index->
                            val uid= FirebaseAuth.getInstance().uid!!
                            adapterComment.comments[index].apply {
                                this.isLiked=event.comment
                                isLiking=false
                                if(event.comment){
                                    totalLikes+=uid
                                }else{
                                    totalLikes-=uid
                                }
                            }
                            adapterComment.notifyItemChanged(index)
                        }
                    }
                    is CommentViewModel.UpdateTotalCommentLikeEvent.Error->{
                        currentLikeIndex?.let { index->
                            adapterComment.comments[index].isLiking=false
                            adapterComment.notifyItemChanged(index)
                        }
                        snackBar(event.errorText)
                    }
                    is CommentViewModel.UpdateTotalCommentLikeEvent.Loading->{
                        currentLikeIndex?.let { index->
                            adapterComment.comments[index].isLiking=true
                            adapterComment.notifyItemChanged(index)
                        }
                    }
                }
            }
        }
    }

    private fun setRv() {
        adapterComment= AdapterComment(glide,requireContext())
        binding.rvAllComments.adapter=adapterComment
    }
}