package com.mussrose.issueflower.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mussrose.issueflower.entities.Bookmark
import com.mussrose.issueflower.entities.Issue
import com.mussrose.issueflower.entities.updateentities.IssueUpdate
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.repo.IssueRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IssueViewModel @ViewModelInject constructor(
    private val repo: IssueRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    sealed class CreateIssueEvent {
        class Success(val issue: Issue) : CreateIssueEvent()
        class Error(val errorText: String) : CreateIssueEvent()
        object Loading : CreateIssueEvent()
        object Empty : CreateIssueEvent()
    }

    sealed class GetIssueEvent {
        class Success(val issues: List<Issue>) : GetIssueEvent()
        class Error(val errorText: String) : GetIssueEvent()
        object Loading : GetIssueEvent()
        object Empty : GetIssueEvent()
    }

    sealed class DeleteIssueEvent {
        class Success(val issue: Issue) : DeleteIssueEvent()
        class Error(val errorText: String) : DeleteIssueEvent()
        object Loading : DeleteIssueEvent()
        object Empty : DeleteIssueEvent()
    }

    sealed class UpdateIssueEvent {
        class Success(val issue: Any) : UpdateIssueEvent()
        class Error(val errorText: String) : UpdateIssueEvent()
        object Loading : UpdateIssueEvent()
        object Empty : UpdateIssueEvent()
    }

    sealed class ToggleTotalUpVotesEvent {
        class Success(val isUpVoted:Boolean) : ToggleTotalUpVotesEvent()
        class Error(val errorText: String) : ToggleTotalUpVotesEvent()
        object Loading : ToggleTotalUpVotesEvent()
        object Empty : ToggleTotalUpVotesEvent()
    }


    private val _toggleUpVoteStatus= MutableStateFlow<ToggleTotalUpVotesEvent>(ToggleTotalUpVotesEvent.Empty)
    val toggleUpVoteStatus: StateFlow<ToggleTotalUpVotesEvent> = _toggleUpVoteStatus

    fun toggleUpVote(issue:Issue){
        _toggleUpVoteStatus.value=ToggleTotalUpVotesEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.toggleTotalUpVotesForIssue(issue)
            when(result){
                is Resource.Success->{
                    _toggleUpVoteStatus.value=ToggleTotalUpVotesEvent.Success(result.data!!)
                }
                is Resource.Error->{
                    _toggleUpVoteStatus.value=ToggleTotalUpVotesEvent.Error(result.message?:"could not upvote")
                }
            }
        }
    }

    private val _createIssueStatus = MutableStateFlow<CreateIssueEvent>(CreateIssueEvent.Empty)
    val createIssueStatus: StateFlow<CreateIssueEvent> = _createIssueStatus

    private val _getAllIssueStatus = MutableStateFlow<GetIssueEvent>(GetIssueEvent.Empty)
    val getAllIssueStatus: StateFlow<GetIssueEvent> = _getAllIssueStatus

    private val _getAllIssueByUserStatus = MutableStateFlow<GetIssueEvent>(GetIssueEvent.Empty)
    val getAllIssueByStatus: StateFlow<GetIssueEvent> = _getAllIssueByUserStatus


    private val _deleteIssueStatus = MutableStateFlow<DeleteIssueEvent>(DeleteIssueEvent.Empty)
    val deleteIssueStatus: StateFlow<DeleteIssueEvent> = _deleteIssueStatus

    private val _updateIssueStatus = MutableStateFlow<UpdateIssueEvent>(UpdateIssueEvent.Empty)
    val updateIssueStatus:StateFlow<UpdateIssueEvent> = _updateIssueStatus

    fun updateIssue(issueToUpdate: IssueUpdate){

        if(issueToUpdate.titleToUpdate.trim().isEmpty() || issueToUpdate.descriptionToUpdate.trim().isEmpty()){
            _updateIssueStatus.value=UpdateIssueEvent.Error("Add title and description")
            return
        }

        _updateIssueStatus.value=UpdateIssueEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.updateIssue(issueToUpdate)
            when(result){
                is Resource.Success->{
                    _updateIssueStatus.value=UpdateIssueEvent.Success(result)
                }
                is Resource.Error->{
                    _updateIssueStatus.value=UpdateIssueEvent.Error(result.message?:"could not update")
                }
                else->{}
            }
        }
    }


    fun createIssue(
        title: String,
        description: String,
        labels: List<String>,
        state: String,
        projectId: String
    ) {
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            _createIssueStatus.value =
                CreateIssueEvent.Error("title and description can not be empty")
            return
        }
        viewModelScope.launch(dispatcher) {
            _createIssueStatus.value = CreateIssueEvent.Loading
            val result = repo.createIssue(title, description, labels, state, projectId)
            when (result) {
                is Resource.Success -> {
                    _createIssueStatus.value = CreateIssueEvent.Success(result.data!!)
                }
                is Resource.Error -> {
                    _createIssueStatus.value =
                        CreateIssueEvent.Error(result.message ?: "something went wrong")
                }
            }
        }
    }

    fun getAllIssuesForProject(projectId: String,sortIssueBy: SortIssueBy) {
        _getAllIssueStatus.value = GetIssueEvent.Loading
        viewModelScope.launch(dispatcher) {
            val result = when(sortIssueBy){
                SortIssueBy.DATEDESCENDING->repo.getAllIssues(projectId,sortIssueBy)
                SortIssueBy.DATEASCENDING->repo.getAllIssues(projectId,sortIssueBy)
                SortIssueBy.MOSTUPVOTED->repo.getAllIssues(projectId,sortIssueBy)
                SortIssueBy.MOSTCOMMENTED->repo.getAllIssues(projectId,sortIssueBy)
            }
            when (result) {
                is Resource.Success -> {
                    _getAllIssueStatus.value = GetIssueEvent.Success(result.data!!)
                }
                is Resource.Error -> {
                    _getAllIssueStatus.value = GetIssueEvent.Error(
                        result.message ?: "an error occurred while fetching data"
                    )
                }
            }
        }
    }

    fun getAllIssuesByUser(projectId: String) {
        _getAllIssueByUserStatus.value = GetIssueEvent.Loading
        viewModelScope.launch(dispatcher) {
            val result = repo.getIssuesByUser(projectId)
            when (result) {
                is Resource.Success -> {
                    _getAllIssueByUserStatus.value = GetIssueEvent.Success(result.data!!)
                }
                is Resource.Error -> {
                    _getAllIssueByUserStatus.value = GetIssueEvent.Error(
                        result.message ?: "an error occurred while fetching data"
                    )
                }
            }
        }
    }

    fun deleteIssue(issue: Issue) {
        _deleteIssueStatus.value = DeleteIssueEvent.Loading
        viewModelScope.launch(dispatcher) {
            val result = repo.deleteIssue(issue)
            when (result) {
                is Resource.Success -> {
                    _deleteIssueStatus.value = DeleteIssueEvent.Success(result.data!!)
                }
                is Resource.Error -> {
                    _deleteIssueStatus.value =
                        DeleteIssueEvent.Error(result.message ?: "could not delete")
                }
            }
        }
    }
}

enum class SortIssueBy{
    DATEASCENDING,DATEDESCENDING,MOSTUPVOTED,MOSTCOMMENTED
}