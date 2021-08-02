package com.mussrose.issueflower.ui.auth

import android.util.Patterns
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.javafaker.Faker
import com.google.firebase.auth.AuthResult
import com.mussrose.issueflow.repo.AuthRepo
import com.mussrose.issueflower.others.Constants.PASSWORD_PATTERN
import com.mussrose.issueflower.others.EventFlow
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.repo.DefaultAuthRepo

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class AuthViewModel @ViewModelInject constructor(
    private val repo: AuthRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {


    private val _registerStatus = MutableStateFlow<AuthEvent>(AuthEvent.Empty)
    val registerStatus: StateFlow<AuthEvent> = _registerStatus

    private val _loginStatus = MutableStateFlow<AuthEvent>(AuthEvent.Empty)
    val loginStatus: StateFlow<AuthEvent> = _loginStatus


    sealed class AuthEvent {
        class Success(val event: AuthResult) : AuthEvent()
        class Error(val errorText: String) : AuthEvent()
        object Loading : AuthEvent()
        object Empty : AuthEvent()
    }

    fun logIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.value = AuthEvent.Error("fields can not be empty")
            return
        }
        _loginStatus.value = AuthEvent.Loading
        viewModelScope.launch(dispatcher) {
            val response = repo.login(email, password)
            when (response) {
                is Resource.Success -> {
                    _loginStatus.value = AuthEvent.Success(response.data!!)
                }
                is Resource.Error -> {
                    _loginStatus.value = AuthEvent.Error(response.message ?: "an error occurred")
                }
            }

        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        repeatedPassword: String
    ) {
        if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            _registerStatus.value = AuthEvent.Error("please fill all the fields")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerStatus.value = AuthEvent.Error("enter a valid e-mail")
            return
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            _registerStatus.value =
                AuthEvent.Error("password must contains at least a capital/lower case letter,number and be longer than 7")
            return
        }
        if (password != repeatedPassword) {
            _registerStatus.value = AuthEvent.Error("passwords do not match")
            return
        }
        if (username.isEmpty()) {
            _registerStatus.value = AuthEvent.Error("User name can not be empty")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _registerStatus.value = AuthEvent.Loading
            val response = repo.register(email, username, password)
            when (response) {
                is Resource.Success -> {
                    val authResult = response.data
                    _registerStatus.value = AuthEvent.Success(authResult!!)
                }
                is Resource.Error -> {
                    _registerStatus.value = AuthEvent.Error(response.message ?: "error occurred")
                }
            }
        }

    }

    fun generateUserName(): String {
        val fake = Faker()
        return fake.name().firstName() + " " + fake.artist().name()
    }
}