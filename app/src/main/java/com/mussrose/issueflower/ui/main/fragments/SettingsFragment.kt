package com.mussrose.issueflower.ui.main.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.FragmentSettingsBinding
import com.mussrose.issueflower.ui.auth.AuthActivity
import com.mussrose.issueflower.ui.main.viewmodel.UserViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var glide: RequestManager

    private var curImageUri: Uri? = null
    private val viewModel: UserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        stateFlow()
        val resultLauncher = resultLauncher()
        subToObservers()
        binding.btSelectPic.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            resultLauncher.launch(intent)
        }
        binding.btUpdateUserName.setOnClickListener {
            val uid=FirebaseAuth.getInstance().uid!!

            curImageUri?.let {
                val username=binding.etUserNameSettings.text.toString()
                viewModel.updateUser(uid,username,it)
                curImageUri=null
                binding.btUpdateUserName.isEnabled=false
            }
        }

        binding.btLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun stateFlow(){
        val uid=FirebaseAuth.getInstance().uid
        viewModel.getUser(uid!!)
        lifecycleScope.launchWhenCreated {
            viewModel.curUserStatus.collect { event->
                when(event){
                    is UserViewModel.GetUserEvent.Success->{
                        val user=event.user
                        binding.etUserNameSettings.setText(user.username)
                        glide.load(user.profilePicUrl).into(binding.tvSettingUserInitial)
                        binding.tvTotalUserIssues.text=user.totalIssuesByUser.toString()
                        binding.tvTotalUserComments.text=user.totalCommentsByUser.toString()
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
        lifecycleScope.launchWhenCreated {
            viewModel.updateUserStatus.collect { event->
                when(event){
                    is UserViewModel.UpdateUserEvent.Success->{
                        snackBar("updated")
                        binding.pbSettings.isVisible=false
                    }
                    is UserViewModel.UpdateUserEvent.Error->{
                        snackBar(event.errorText)
                        binding.pbSettings.isVisible=false
                    }
                    is UserViewModel.UpdateUserEvent.Loading->{
                        binding.pbSettings.isVisible=true
                    }
                    else->{}
                }
            }
        }
    }
    private fun subToObservers() {
        viewModel.curImageUri.observe(viewLifecycleOwner, {
            curImageUri = it
            binding.tvSettingUserInitial.background=null
            glide.load(curImageUri).into(binding.tvSettingUserInitial)
        })
    }


    private fun resultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val uri = result.data?.data
                uri?.let {
                    binding.btUpdateUserName.isEnabled=true
                    viewModel.setCurrentImageUri(it)
                }

            }
        }
    }
}