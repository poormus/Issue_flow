package com.mussrose.issueflower.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.mussrose.issueflower.others.Constants.DEFAULT_PROFILE_PIC_URL



@IgnoreExtraProperties
data class User(
    val uid:String="",
    val username:String="",
    val profilePicUrl:String=DEFAULT_PROFILE_PIC_URL,
    val totalCommentsByUser:Int=0,
    val totalIssuesByUser:Int=0,


)