package com.mussrose.issueflower.repo

import android.net.Uri
import com.mussrose.issueflower.entities.*
import com.mussrose.issueflower.entities.updateentities.IssueUpdate
import com.mussrose.issueflower.entities.updateentities.ProfileUpdate
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.ui.main.viewmodel.SortIssueBy

interface IssueRepo {

    suspend fun createProject(
        name: String,
        description: String,
        state: String
    ): Resource<Any>

    suspend fun getAllProjects(): Resource<List<Project>>

    suspend fun getAllIssues(projectId: String,sortIssueBy: SortIssueBy):Resource<List<Issue>>


    suspend fun getIssuesByUser(projectId: String):Resource<List<Issue>>

    suspend fun createIssue(
        title: String,
        description: String,
        labels: List<String>,
        state: String,
        projectId: String
    ): Resource<Issue>

    suspend fun createComment(commentText:String,issueId:String):Resource<Comment>

    suspend fun deleteIssue(issue:Issue):Resource<Issue>

    suspend fun getUser(uid:String):Resource<User>

    suspend fun updateProfile(profileUpdate: ProfileUpdate):Resource<Any>

    suspend fun updateProfilePic(uid:String,imageUri: Uri): Uri?

    suspend fun deleteComment(comment: Comment):Resource<Comment>

    suspend fun getCommentsForIssue(issueId:String):Resource<List<Comment>>


    suspend fun updateTotalIssueByUser(updateTotalIssue: UpdateTotalIssue):Resource<Any>

    suspend fun updateTotalCommentByUser(updateTotalComment: UpdateTotalComment):Resource<Any>

    suspend fun updateTotalCommentNumberForIssue(issueId: String):Resource<Any>

    suspend fun toggleTotalLikesForComment(comment:Comment):Resource<Boolean>

    suspend fun toggleTotalUpVotesForIssue(issue: Issue):Resource<Boolean>

    suspend fun updateTotalCommentNumberForIssueOnDeleteComment(issueId: String):Resource<Any>

    suspend fun deleteCommentsForIssueOnIssueDelete(issueId: String):Resource<Any>

    suspend fun updateIssue(issueUpdate: IssueUpdate):Resource<Any>

    suspend fun searchIssue(query:String,projectId: String):Resource<List<Issue>>


    suspend fun toggleBookMark(issueId: String):Resource<Boolean>

    suspend fun getUserBookMarks(uid: String):Resource<List<Issue>>


}

enum class UpdateTotalComment{
    CREATE,DELETE
}

enum class UpdateTotalIssue{
    CREATE,DELETE
}