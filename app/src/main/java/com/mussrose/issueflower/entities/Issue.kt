package com.mussrose.issueflower.entities

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Issue(
    val id: String="",
    val title: String="",
    val description: String="",
    val labels: List<String> = listOf(),
    val state: String="",
    val projectId:String="",
    val uid:String="",
    var openedBy:String=" ",
    val totalCommentNumber:List<String> = listOf(),

    @get:Exclude var isUpVoted:Boolean=false,
    @get:Exclude var isUpVoting:Boolean=false,
    var totalUpVotes:List<String> = listOf(),

    @get:Exclude var isBookmarked:Boolean=false,
    @get:Exclude var isBookMarking:Boolean=false,
    var bookMarkedBy:List<String> = listOf(),

    val date:Long=System.currentTimeMillis()
):Parcelable{

}

