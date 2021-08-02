package com.mussrose.issueflower.ui.main.fragments.issuedetail

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.BottomDialogInsertCommentBinding
import com.mussrose.issueflower.ui.main.viewmodel.CommentViewModel
import com.mussrose.issueflower.ui.main.viewmodel.UserViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class InsertCommentBottomDialog :
    BottomSheetDialogFragment() {


    lateinit var binding: BottomDialogInsertCommentBinding

    @Inject
    lateinit var glide:RequestManager

    private val commentViewModel:CommentViewModel by viewModels()
    private val userViewModel:UserViewModel by viewModels()

    fun getInstance(issueId: String): InsertCommentBottomDialog {
        val dialog = InsertCommentBottomDialog()
        val bundle = Bundle()
        bundle.putString("issueId", issueId)
        dialog.arguments = bundle
        return dialog
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.bottom_dialog_insert_comment, container, false)
        binding= BottomDialogInsertCommentBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val issueId=arguments?.getString("issueId")
        stateFlow()
        binding.btBottomDialogSend.setOnClickListener {
            val commentText=binding.etBottomDialogAddComment.text.toString()
            commentViewModel.createComment(commentText,issueId!!)
        }
    }

    private fun stateFlow() {
        lifecycleScope.launchWhenCreated {
            commentViewModel.createCommentStatus.collect { event->
                when(event){
                    is CommentViewModel.CreateCommentEvent.Success->{
                        dialog?.dismiss()
                    }
                    is CommentViewModel.CreateCommentEvent.Error->{
                        snackBar(event.errorText)
                        binding.pbCreateComment.isVisible=false
                        binding.btBottomDialogSend.isVisible=true
                    }
                    is CommentViewModel.CreateCommentEvent.Loading->{
                        binding.pbCreateComment.isVisible=true
                        binding.btBottomDialogSend.isVisible=false
                    }
                    else->{}

                }
            }
        }
        val uid= FirebaseAuth.getInstance().uid
        userViewModel.getUser(uid!!)
        lifecycleScope.launchWhenCreated {
            userViewModel.curUserStatus.collect { event->
                when(event){
                    is UserViewModel.GetUserEvent.Success->{
                        glide.load(event.user.profilePicUrl).into(binding.ivBottomDialogUserPic)
                    }
                    is UserViewModel.GetUserEvent.Error->{
                        snackBar(event.errorText)
                    }
                    is UserViewModel.GetUserEvent.Loading->{

                    }
                    else->{}
                }
            }
        }
    }
}