package com.mussrose.issueflower.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "book_mark")
data class Bookmark(
    val issueId: String,
    val projectId: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int=0
}
