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

class BookMarkViewModel @ViewModelInject constructor(
    private val repo: IssueRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    sealed class BookMarkIssueEvent {
        class Success(val issues: List<Issue>) : BookMarkIssueEvent()
        class Error(val errorText: String) : BookMarkIssueEvent()
        object Loading : BookMarkIssueEvent()
        object Empty : BookMarkIssueEvent()
    }

    sealed class ToggleBookmarkEvent {
        class Success(val isBookMarked: Boolean) : ToggleBookmarkEvent()
        class Error(val errorText: String) : ToggleBookmarkEvent()
        object Loading : ToggleBookmarkEvent()
        object Empty : ToggleBookmarkEvent()
    }


    private val _toggleBookMarkStatus = MutableStateFlow<ToggleBookmarkEvent>(ToggleBookmarkEvent.Empty)
    val toggleBookMarkStatus: StateFlow<ToggleBookmarkEvent> = _toggleBookMarkStatus

    fun toggleBookMark(issueId:String){
        _toggleBookMarkStatus.value=ToggleBookmarkEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.toggleBookMark(issueId)
            when(result){
                is Resource.Success->{
                    _toggleBookMarkStatus.value=ToggleBookmarkEvent.Success(result.data?:false)
                }
                is Resource.Error->{
                    _toggleBookMarkStatus.value=ToggleBookmarkEvent.Error(result.message?:"could not bookmark")
                }
            }
        }
    }



    private val _getBookMarkStatus = MutableStateFlow<BookMarkIssueEvent>(BookMarkIssueEvent.Empty)
    val getBookMarkStatus: StateFlow<BookMarkIssueEvent> = _getBookMarkStatus


    fun getBookMark(uid:String){
        _getBookMarkStatus.value=BookMarkIssueEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.getUserBookMarks(uid)
            when(result){
                is Resource.Success->{
                    _getBookMarkStatus.value=BookMarkIssueEvent.Success(result.data?: listOf())
                }
                is Resource.Error->{
                    _getBookMarkStatus.value=BookMarkIssueEvent.Error(result.message?:"error occurred")
                }
            }
        }
    }
}