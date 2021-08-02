package com.mussrose.issueflower.ui.main.viewmodel

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.repo.IssueRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.mussrose.issueflower.entities.User
import com.mussrose.issueflower.entities.updateentities.ProfileUpdate

class UserViewModel @ViewModelInject constructor(
    private val repo: IssueRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {


    sealed class GetUserEvent {
        class Success(val user:User) : GetUserEvent()
        class Error(val errorText: String) : GetUserEvent()
        object Loading : GetUserEvent()
        object Empty : GetUserEvent()
    }

    sealed class UpdateUserEvent {
        class Success(val any: Any) : UpdateUserEvent()
        class Error(val errorText: String) : UpdateUserEvent()
        object Loading : UpdateUserEvent()
        object Empty : UpdateUserEvent()
    }

    private val _curImageUri= MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri

    private val _curUserStatus= MutableStateFlow<GetUserEvent>(GetUserEvent.Empty)
    val curUserStatus:StateFlow<GetUserEvent> = _curUserStatus

    private val _updateUserStatus = MutableStateFlow<UpdateUserEvent>(UpdateUserEvent.Empty)
    val updateUserStatus:StateFlow<UpdateUserEvent> = _updateUserStatus


    fun updateUser(uid: String,username:String,profilePicUri: Uri?){
        if(username.trim().isEmpty()){
            _updateUserStatus.value=UpdateUserEvent.Error("user name is empty!")
            return
        }
        val profileUpdate=ProfileUpdate(uid,username,profilePicUri)
        _updateUserStatus.value=UpdateUserEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.updateProfile(profileUpdate)
            when(result){
                is Resource.Success->{
                    _updateUserStatus.value=UpdateUserEvent.Success(result)
                }
                is Resource.Error->{
                    _updateUserStatus.value=UpdateUserEvent.Error(result.message?:"can not update right now")
                }else->{

                }
            }
        }

    }

    fun getUser(uid:String){
        _curUserStatus.value=GetUserEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.getUser(uid)
            when(result){
                is Resource.Success->{
                    _curUserStatus.value=GetUserEvent.Success(result.data!!)
                }
                is Resource.Error->{
                    _curUserStatus.value=GetUserEvent.Error(result.message?:"user not found")
                }else->{}
            }
        }
    }



    fun setCurrentImageUri(uri: Uri){
        _curImageUri.postValue(uri)
    }

}