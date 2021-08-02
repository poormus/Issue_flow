package com.mussrose.issueflower.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.mussrose.issueflow.repo.AuthRepo
import com.mussrose.issueflower.entities.Project
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.repo.IssueRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectViewModel @ViewModelInject constructor(
    private val repo: IssueRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    sealed class CreateEntityEvent {
        class Success(val event: Any) : CreateEntityEvent()
        class Error(val errorText: String) : CreateEntityEvent()
        object Loading : CreateEntityEvent()
        object Empty : CreateEntityEvent()
    }

    sealed class GetEntityEvent {
        class Success(val event: List<Project>) : GetEntityEvent()
        class Error(val errorText: String) : GetEntityEvent()
        object Loading : GetEntityEvent()
        object Empty : GetEntityEvent()
    }


    private val _createProjectStatus = MutableStateFlow<CreateEntityEvent>(CreateEntityEvent.Empty)
    val createProjectStatus: StateFlow<CreateEntityEvent> = _createProjectStatus

    private val _getAllProjectStatus = MutableStateFlow<GetEntityEvent>(GetEntityEvent.Empty)
    val getAllProjectStatus: StateFlow<GetEntityEvent> = _getAllProjectStatus

    fun createProject(name: String, description: String, state: String) {
       viewModelScope.launch(dispatcher){
           repo.createProject(name, description, state)
       }
    }

    fun getAllProjects(){
            viewModelScope.launch(dispatcher){
                _getAllProjectStatus.value=GetEntityEvent.Loading
                val result=repo.getAllProjects()
                when(result){
                    is Resource.Success->{
                        _getAllProjectStatus.value=GetEntityEvent.Success(result.data!!)
                    }
                    is Resource.Error->{
                        _getAllProjectStatus.value=GetEntityEvent.Error(result.message?:"error")
                    }
                    else -> {}
                }
            }
    }
}