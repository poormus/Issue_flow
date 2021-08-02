package com.mussrose.issueflow.repo

import com.mussrose.issueflower.others.Resource
import com.google.firebase.auth.AuthResult

interface AuthRepo {

    suspend fun register(email:String,userName:String,password:String): Resource<AuthResult>
    suspend fun login(email:String,password:String): Resource<AuthResult>
}