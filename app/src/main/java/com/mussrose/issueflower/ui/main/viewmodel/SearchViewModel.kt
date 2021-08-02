package com.mussrose.issueflower.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mussrose.issueflower.entities.Issue
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.repo.IssueRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
    private val repo: IssueRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    sealed class SearchIssueEvent {
        class Success(val issues: List<Issue>) : SearchIssueEvent()
        class Error(val errorText: String) : SearchIssueEvent()
        object Loading : SearchIssueEvent()
        object Empty : SearchIssueEvent()
    }


    private val _searchIssueStatus = MutableStateFlow<SearchIssueEvent>(SearchIssueEvent.Empty)
    val searchIssueStatus:StateFlow<SearchIssueEvent> = _searchIssueStatus

     fun  searchIssue(query:String,projectId:String){
        _searchIssueStatus.value=SearchIssueEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.searchIssue(query, projectId)
            when(result){
                is Resource.Success->{
                    val issues=result.data
                    _searchIssueStatus.value=SearchIssueEvent.Success(issues!!)
                }
                is Resource.Error->{
                    _searchIssueStatus.value=SearchIssueEvent.Error(result.message?:"search could not succeed")
                }
                else->{

                }
            }
        }
    }
}