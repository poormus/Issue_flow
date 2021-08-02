package com.mussrose.issueflower.others

sealed class EventFlow{
    class Success(val event:List<Any>):EventFlow()
    class Fail(val errorText:String):EventFlow()
    object Loading : EventFlow()
    object Empty : EventFlow()
}