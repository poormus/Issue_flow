package com.mussrose.issueflower.others

inline fun<T> safeCall(action:()-> Resource<T>): Resource<T> {
    return try {
        action()
    }catch (e:Exception){
        Resource.Error(e.message?:"unknown error occurred")
    }
}