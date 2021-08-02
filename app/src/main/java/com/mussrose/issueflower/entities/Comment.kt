package com.mussrose.issueflower.entities

import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.*

@IgnoreExtraProperties
data class Comment(
    val commentId:String="",
    val issueId:String="",
    val uid:String="",
    @get:Exclude
    var userName:String="",
    @get:Exclude
    var profilePictureUrl:String="",
    val comment:String="",
    @get:Exclude var isLiked:Boolean=false,
    @get:Exclude var isLiking:Boolean=false,
    var totalLikes:List<String> = listOf(),
    val date:Long=System.currentTimeMillis()
){
    val commentDate:String
    get() =timeAgo(date)


    private fun timeAgo(date: Long): String {
        val localeByLanguageTag = Locale.forLanguageTag("en");
        val messages = TimeAgoMessages.Builder().withLocale(localeByLanguageTag).build();
        return TimeAgo.using(date, messages)
    }
}