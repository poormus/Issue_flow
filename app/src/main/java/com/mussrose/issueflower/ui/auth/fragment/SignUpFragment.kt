package com.mussrose.issueflower.ui.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.FragmentSignUpBinding
import com.mussrose.issueflower.ui.auth.AuthViewModel
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    lateinit var binding: FragmentSignUpBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        stateFlow()
        binding.btGenerateUserName.setOnClickListener {
            val userName = viewModel.generateUserName()
            binding.etUserNameSignUp.setText(userName)
        }
        binding.btSignUp.setOnClickListener {
            val email = binding.etEmailSignUp.text.toString()
            val passWord = binding.etPassWordSignUp.text.toString()
            val repeatedPassWord = binding.etPassWordRepeatSignUp.text.toString()
            val userName = binding.etUserNameSignUp.text.toString()
            viewModel.register(userName, email, passWord, repeatedPassWord)
        }
    }

    private fun stateFlow() {
        lifecycleScope.launchWhenCreated {
            viewModel.registerStatus.collect { event ->
                when (event) {
                    is AuthViewModel.AuthEvent.Success -> {
                        binding.signUpProgressBar.isVisible = false
                        snackBar("register success")
                    }
                    is AuthViewModel.AuthEvent.Loading -> {
                        binding.signUpProgressBar.isVisible = true

                    }
                    is AuthViewModel.AuthEvent.Error -> {
                        binding.signUpProgressBar.isVisible = false
                        snackBar(event.errorText)
                    }
                    else -> {
                    }
                }

            }
        }
    }
}