package com.mussrose.issueflower.db

import androidx.room.*
import androidx.room.Dao
import com.mussrose.issueflower.entities.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark)

    @Delete
    suspend fun delete(bookmark: Bookmark)


    @Query("SELECT * FROM book_mark")
    fun getAllBookMarks(): Flow<List<Bookmark>>
}