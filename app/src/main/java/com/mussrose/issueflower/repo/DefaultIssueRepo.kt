package com.mussrose.issueflower.repo

import android.net.Uri
import com.mussrose.issueflower.others.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mussrose.issueflower.entities.*
import com.mussrose.issueflower.entities.updateentities.IssueUpdate
import com.mussrose.issueflower.entities.updateentities.ProfileUpdate
import com.mussrose.issueflower.others.Constants.DEFAULT_PROFILE_PIC_URL
import com.mussrose.issueflower.others.Resource
import com.mussrose.issueflower.ui.main.viewmodel.SortIssueBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*

class DefaultIssueRepo : IssueRepo {

    private val auth = FirebaseAuth.getInstance()

    //auth.uid return the current logged in user
    private val fireStore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = fireStore.collection("users")
    private val projects = fireStore.collection("projects")
    private val issues = fireStore.collection("issues")
    private val comments = fireStore.collection("comments")

    override suspend fun createProject(
        name: String,
        description: String,
        state: String
    ): Resource<Any> =
        withContext(Dispatchers.IO) {
            safeCall {
                val projectId = UUID.randomUUID().toString()
                val project = Project(
                    projectId, name, description, state, System.currentTimeMillis()
                )
                projects.document(projectId).set(project).await()
                Resource.Success(Any())
            }
        }

    override suspend fun createIssue(
        title: String,
        description: String,
        labels: List<String>,
        state: String,
        projectId: String
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val user = getUser(uid).data!!
            val issueId = UUID.randomUUID().toString()
            val issue = Issue(
                issueId,
                title, description, labels, state, projectId,
                uid = uid,
                openedBy = user.username
            )
            issues.document(issueId).set(issue).await()
            updateTotalIssueByUser(UpdateTotalIssue.CREATE)
            Resource.Success(issue)
        }
    }

    override suspend fun createComment(commentText: String, issueId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = auth.uid!!
                val commentId = UUID.randomUUID().toString()
                val user = getUser(uid).data!!
                val comment = Comment(
                    commentId,
                    issueId,
                    uid,
                    user.username,
                    user.profilePicUrl,
                    commentText
                )

                comments.document(commentId).set(comment).await()
                updateTotalCommentNumberForIssue(issueId)
                updateTotalCommentByUser(UpdateTotalComment.CREATE)
                Resource.Success(comment)
            }
        }

    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            comments.document(comment.commentId).delete().await()
            updateTotalCommentNumberForIssueOnDeleteComment(comment.issueId)
            updateTotalCommentByUser(UpdateTotalComment.DELETE)
            Resource.Success(comment)
        }
    }

    override suspend fun getCommentsForIssue(issueId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentsForIssue = comments.whereEqualTo("issueId", issueId)
                .orderBy("date", Query.Direction.DESCENDING).get().await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = getUser(comment.uid).data!!
                    comment.userName = user.username
                    comment.profilePictureUrl = user.profilePicUrl
                }
            Resource.Success(commentsForIssue)
        }
    }

    override suspend fun getIssuesByUser(projectId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val issuesByUser =
                issues.whereEqualTo("projectId", projectId).whereEqualTo("uid", uid)
                    .orderBy("date", Query.Direction.DESCENDING).get()
                    .await().toObjects(Issue::class.java)
            Resource.Success(issuesByUser)
        }
    }

    override suspend fun getAllIssues(projectId: String, sortIssueBy: SortIssueBy) =
        withContext(Dispatchers.IO) {
            safeCall {
                val allIssuesForProject = when (sortIssueBy) {
                    SortIssueBy.DATEASCENDING -> issues.whereEqualTo("projectId", projectId)
                        .orderBy("date", Query.Direction.ASCENDING).get()
                        .await().toObjects(Issue::class.java).onEach { issue ->
                            val user = getUser(issue.uid).data!!
                            issue.openedBy = user.username
                        }
                    SortIssueBy.DATEDESCENDING -> issues.whereEqualTo("projectId", projectId)
                        .orderBy("date", Query.Direction.DESCENDING).get()
                        .await().toObjects(Issue::class.java).onEach { issue ->
                            val user = getUser(issue.uid).data!!
                            issue.openedBy = user.username
                        }
                    SortIssueBy.MOSTCOMMENTED -> issues.whereEqualTo("projectId", projectId)
                        .orderBy("totalCommentNumber", Query.Direction.DESCENDING).get()
                        .await().toObjects(Issue::class.java).onEach { issue ->
                            val user = getUser(issue.uid).data!!
                            issue.openedBy = user.username
                        }
                    SortIssueBy.MOSTUPVOTED -> issues.whereEqualTo("projectId", projectId)
                        .orderBy("totalUpVotes", Query.Direction.DESCENDING).get()
                        .await().toObjects(Issue::class.java).onEach { issue ->
                            val user = getUser(issue.uid).data!!
                            issue.openedBy = user.username
                        }
                }
                Resource.Success(allIssuesForProject)
            }
        }

    override suspend fun deleteIssue(issue: Issue) = withContext(Dispatchers.IO) {
        safeCall {
            issues.document(issue.id).delete().await()
            deleteCommentsForIssueOnIssueDelete(issue.id)
            updateTotalIssueByUser(UpdateTotalIssue.DELETE)
            Resource.Success(issue)
        }
    }

    override suspend fun getUser(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            Resource.Success(user)
        }
    }

    override suspend fun updateProfile(profileUpdate: ProfileUpdate) = withContext(Dispatchers.IO) {
        safeCall {
            val imageUrl = profileUpdate.profilePictureUri?.let { uri ->
                updateProfilePic(profileUpdate.uidToUpdate, uri).toString()
            }

            val map = mutableMapOf(
                "username" to profileUpdate.username
            )
            imageUrl?.let { url ->
                map["profilePicUrl"] = url
            }

            users.document(profileUpdate.uidToUpdate).update(map.toMap()).await()
            Resource.Success(Any())
        }
    }

    override suspend fun updateProfilePic(uid: String, imageUri: Uri) =
        withContext(Dispatchers.IO) {
            val storageRef = storage.getReference(uid)
            val user = getUser(uid).data!!
            if (user.profilePicUrl != DEFAULT_PROFILE_PIC_URL) {
                storage.getReferenceFromUrl(user.profilePicUrl).delete().await()
            }
            storageRef.putFile(imageUri).await().metadata?.reference?.downloadUrl?.await()
        }

    override suspend fun getAllProjects() = withContext(Dispatchers.IO) {
        safeCall {
            val allProjects = projects.orderBy("date", Query.Direction.ASCENDING).get().await()
                .toObjects(Project::class.java)
            Resource.Success(allProjects)
        }
    }

    override suspend fun updateTotalCommentNumberForIssue(issueId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                fireStore.runTransaction { transaction ->
                    val uid = FirebaseAuth.getInstance().uid!!
                    val issueResult = transaction.get(issues.document(issueId))
                    val currentComments =
                        issueResult.toObject(Issue::class.java)?.totalCommentNumber ?: listOf()
                    transaction.update(
                        issues.document(issueId), "totalCommentNumber",
                        currentComments + uid
                    )
                }.await()
                Resource.Success(Any())
            }
        }

    override suspend fun updateTotalCommentByUser(updateTotalComment: UpdateTotalComment) =
        withContext(Dispatchers.IO) {
            safeCall {

                fireStore.runTransaction { transaction ->
                    val uid = FirebaseAuth.getInstance().uid!!
                    val userResult = transaction.get(users.document(uid))
                    val currentComments =
                        userResult.toObject(User::class.java)?.totalCommentsByUser!!

                    when (updateTotalComment) {
                        UpdateTotalComment.CREATE -> {
                            transaction.update(
                                users.document(uid), "totalCommentsByUser",
                                currentComments + 1
                            )
                        }
                        UpdateTotalComment.DELETE -> {
                            transaction.update(
                                users.document(uid), "totalCommentsByUser",
                                currentComments - 1
                            )
                        }
                    }

                }.await()
                Resource.Success(Any())
            }
        }


    override suspend fun updateTotalIssueByUser(updateTotalIssue: UpdateTotalIssue) =
        withContext(Dispatchers.IO) {
            safeCall {
                fireStore.runTransaction { transaction ->
                    val uid = FirebaseAuth.getInstance().uid!!
                    val userResult = transaction.get(users.document(uid))
                    val currentIssues = userResult.toObject(User::class.java)?.totalIssuesByUser!!

                    when (updateTotalIssue) {
                        UpdateTotalIssue.CREATE -> {
                            transaction.update(
                                users.document(uid), "totalIssuesByUser",
                                currentIssues + 1
                            )
                        }
                        UpdateTotalIssue.DELETE -> {
                            transaction.update(
                                users.document(uid), "totalIssuesByUser",
                                currentIssues - 1
                            )
                        }
                    }

                }.await()
                Resource.Success(Any())
            }
        }

    override suspend fun toggleTotalLikesForComment(comment: Comment) =
        withContext(Dispatchers.IO) {
            safeCall {
                var isLiked = false
                fireStore.runTransaction { transaction ->
                    val uid = FirebaseAuth.getInstance().uid!!
                    val commentResult = transaction.get(comments.document(comment.commentId))
                    val currentLikes =
                        commentResult.toObject(Comment::class.java)?.totalLikes ?: listOf()
                    transaction.update(
                        comments.document(comment.commentId), "totalLikes",
                        if (uid in currentLikes) currentLikes - uid else {
                            isLiked = true
                            currentLikes + uid
                        }
                    )
                }.await()
                Resource.Success(isLiked)
            }
        }

    override suspend fun toggleTotalUpVotesForIssue(issue: Issue) = withContext(Dispatchers.IO) {
        safeCall {
            var isUpVoted = false
            fireStore.runTransaction { transaction ->
                val uid = FirebaseAuth.getInstance().uid!!
                val issueResult = transaction.get(issues.document(issue.id))
                val currentUpVotes =
                    issueResult.toObject(Issue::class.java)?.totalUpVotes ?: listOf()
                transaction.update(
                    issues.document(issue.id), "totalUpVotes",
                    if (uid in currentUpVotes) currentUpVotes - uid else {
                        isUpVoted = true
                        currentUpVotes + uid
                    }
                )
            }.await()
            Resource.Success(isUpVoted)
        }
    }


    override suspend fun updateTotalCommentNumberForIssueOnDeleteComment(issueId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                fireStore.runTransaction { transaction ->
                    val uid = FirebaseAuth.getInstance().uid!!
                    val issueResult = transaction.get(issues.document(issueId))
                    val currentComments =
                        issueResult.toObject(Issue::class.java)?.totalCommentNumber ?: listOf()
                    transaction.update(
                        issues.document(issueId), "totalCommentNumber",
                        currentComments - uid
                    )
                }.await()
                Resource.Success(Any())
            }
        }

    override suspend fun deleteCommentsForIssueOnIssueDelete(issueId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val allCommentsOfIssue = getCommentsForIssue(issueId)
                allCommentsOfIssue.data?.onEach { comment ->
                    deleteComment(comment)
                }
                Resource.Success(Any())
            }
        }

    override suspend fun updateIssue(issueUpdate: IssueUpdate) = withContext(Dispatchers.IO) {
        safeCall {
            val map = mutableMapOf(
                "title" to issueUpdate.titleToUpdate,
                "description" to issueUpdate.descriptionToUpdate,
                "state" to issueUpdate.stateToUpdate
            )
            issues.document(issueUpdate.issueIdToUpdate).update(map.toMap()).await()
            Resource.Success(Any())
        }
    }

    override suspend fun searchIssue(query: String, projectId: String) =
        withContext(Dispatchers.IO) {
            val array = listOf(query)

            val allIssuesForProject = issues.whereEqualTo("projectId", projectId)
                .whereGreaterThanOrEqualTo("title", query)
                .get().await().toObjects(Issue::class.java).onEach { issue ->
                    val user = getUser(issue.uid).data!!
                    issue.openedBy = user.username
                }
            Resource.Success(allIssuesForProject)
        }


    override suspend fun toggleBookMark(issueId: String) = withContext(Dispatchers.IO) {
        safeCall {
            var isBookMarked = false
            fireStore.runTransaction { transaction ->
                val uid = FirebaseAuth.getInstance().uid!!
                val issueResult = transaction.get(issues.document(issueId))
                val curBookMarkedBy =
                    issueResult.toObject(Issue::class.java)?.bookMarkedBy ?: listOf()
                transaction.update(
                    issues.document(issueId), "bookMarkedBy",
                    if (uid in curBookMarkedBy) curBookMarkedBy - uid else {
                        isBookMarked = true
                        curBookMarkedBy + uid
                    }
                )
            }
            Resource.Success(isBookMarked)
        }
    }

    override suspend fun getUserBookMarks(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val issueList = issues.whereArrayContains("bookMarkedBy", uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .get().await().toObjects(Issue::class.java)
            Resource.Success(issueList)
        }
    }
}

