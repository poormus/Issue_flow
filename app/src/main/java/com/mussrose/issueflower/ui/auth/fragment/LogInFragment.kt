package com.mussrose.issueflower.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.FragmentLogInBinding
import com.mussrose.issueflower.ui.auth.AuthViewModel
import com.mussrose.issueflower.ui.main.IssueActivity
import com.mussrose.issueflower.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LogInFragment: Fragment(R.layout.fragment_log_in) {

    lateinit var binding:FragmentLogInBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentLogInBinding.bind(view)
        stateFlow()
        binding.btLogIn.setOnClickListener {
            val email=binding.etEmailLogIn.text.toString()
            val password=binding.etPassWordLogin.text.toString()
            viewModel.logIn(email, password)
        }
        binding.btTakeTour.setOnClickListener {
            findNavController().navigate(
                AuthViewPagerFragmentDirections.actionAuthViewPagerFragmentToTourFragment()
            )
        }
    }
    private fun stateFlow() {
        lifecycleScope.launchWhenCreated {
            viewModel.loginStatus.collect { event ->
                when (event) {
                    is AuthViewModel.AuthEvent.Success -> {
                        binding.progressbarLogIn.isVisible = false
                        snackBar("login success success")
                        requireActivity().startActivity(Intent(requireActivity(),IssueActivity::class.java))
                        requireActivity().finish()

                    }
                    is AuthViewModel.AuthEvent.Loading -> {
                        binding.progressbarLogIn.isVisible = true

                    }
                    is AuthViewModel.AuthEvent.Error -> {
                        binding.progressbarLogIn.isVisible = false
                        snackBar(event.errorText)
                    }
                    else -> {
                    }
                }

            }
        }
    }
}