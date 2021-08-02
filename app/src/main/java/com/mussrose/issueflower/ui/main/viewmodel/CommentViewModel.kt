package com.mussrose.issueflower.ui.main.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mussrose.issueflower.entities.Comment
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.repo.IssueRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel @ViewModelInject constructor(
    private val repo: IssueRepo,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {


    sealed class CreateCommentEvent {
        class Success(val comment:Comment) : CreateCommentEvent()
        class Error(val errorText: String) : CreateCommentEvent()
        object Loading : CreateCommentEvent()
        object Empty : CreateCommentEvent()
    }

    sealed class GetCommentsEvent {
        class Success(val comments:List<Comment>) : GetCommentsEvent()
        class Error(val errorText: String) : GetCommentsEvent()
        object Loading : GetCommentsEvent()
        object Empty : GetCommentsEvent()
    }

    sealed class DeleteCommentEvent {
        class Success(val comment:Comment) : DeleteCommentEvent()
        class Error(val errorText: String) : DeleteCommentEvent()
        object Loading : DeleteCommentEvent()
        object Empty : DeleteCommentEvent()
    }

    sealed class UpdateTotalCommentLikeEvent {
        class Success(val comment:Boolean) : UpdateTotalCommentLikeEvent()
        class Error(val errorText: String) : UpdateTotalCommentLikeEvent()
        object Loading : UpdateTotalCommentLikeEvent()
        object Empty : UpdateTotalCommentLikeEvent()
    }

    private val _createCommentStatus= MutableStateFlow<CreateCommentEvent>(CreateCommentEvent.Empty)
    val createCommentStatus:StateFlow<CreateCommentEvent> = _createCommentStatus

    private val _getCommentsStatus= MutableStateFlow<GetCommentsEvent>(GetCommentsEvent.Empty)
    val getCommentsStatus:StateFlow<GetCommentsEvent> = _getCommentsStatus


    private val _deleteCommentsStatus= MutableStateFlow<DeleteCommentEvent>(DeleteCommentEvent.Empty)
    val deleteCommentsStatus:StateFlow<DeleteCommentEvent> = _deleteCommentsStatus

    private val _commentLikeStatus = MutableStateFlow<UpdateTotalCommentLikeEvent>(UpdateTotalCommentLikeEvent.Empty)
    val commentLikeStatus : StateFlow<UpdateTotalCommentLikeEvent> = _commentLikeStatus


    fun updateTotalCommentLike(comment:Comment){
        _commentLikeStatus.value=UpdateTotalCommentLikeEvent.Loading
        viewModelScope.launch(dispatcher){
            val result= repo.toggleTotalLikesForComment(comment)
            when(result){
                is Resource.Success->{
                    _commentLikeStatus.value=UpdateTotalCommentLikeEvent.Success(result.data!!)
                }
                is Resource.Error->{
                    _commentLikeStatus.value=UpdateTotalCommentLikeEvent.Error(result.message?:"check internet connection")
                }
                else->{

                }
            }
        }
    }


    fun deleteComment(comment: Comment){
        _deleteCommentsStatus.value=DeleteCommentEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.deleteComment(comment)
            when(result){
                is Resource.Success->{
                    val commentToDelete=result.data
                    _deleteCommentsStatus.value=DeleteCommentEvent.Success(commentToDelete!!)
                }
                is Resource.Error->{
                    _deleteCommentsStatus.value=DeleteCommentEvent.Error(result.message?:"could not fetch comments")
                }
                else->{

                }
            }
        }
    }

    fun getCommentsForIssue(issueId: String){
        _getCommentsStatus.value=GetCommentsEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.getCommentsForIssue(issueId)
            when(result){
                is Resource.Success->{
                    val comments=result.data
                    _getCommentsStatus.value=GetCommentsEvent.Success(comments!!)
                }
                is Resource.Error->{
                    _getCommentsStatus.value=GetCommentsEvent.Error(result.message?:"could not fetch comments")
                }
                else->{

                }
            }
        }
    }

    fun createComment(commentText:String,issueId:String){
        if(commentText.trim().isEmpty()){
            _createCommentStatus.value=CreateCommentEvent.Error("comment can not be empty")
            return
        }
        _createCommentStatus.value=CreateCommentEvent.Loading
        viewModelScope.launch(dispatcher){
            val result=repo.createComment(commentText,issueId)
            when(result){
                is Resource.Success->{
                    _createCommentStatus.value=CreateCommentEvent.Success(result.data!!)
                }
                is Resource.Error->{
                    _createCommentStatus.value=CreateCommentEvent.Error(result.message?:"comment could not be added")
                }
                else->{}
            }
        }
    }
}