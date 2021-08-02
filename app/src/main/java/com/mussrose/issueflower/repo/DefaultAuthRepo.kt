package com.mussrose.issueflower.repo

import com.mussrose.issueflower.others.safeCall
import com.mussrose.issueflower.others.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mussrose.issueflow.repo.AuthRepo
import com.mussrose.issueflower.entities.User
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@ActivityScoped
class DefaultAuthRepo: AuthRepo {

    private val auth= FirebaseAuth.getInstance()
    private val users= FirebaseFirestore.getInstance().collection("users")

    override suspend fun register(
        email: String,
        userName: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO){
            safeCall {
                val result=auth.createUserWithEmailAndPassword(email, password).await()
                val uid=result.user?.uid!!
                val user=User(uid,userName)
                users.document(uid).set(user).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return withContext(Dispatchers.IO){
            safeCall {
                val result=auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            }
        }
    }


}