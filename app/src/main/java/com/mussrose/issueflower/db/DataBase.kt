package com.mussrose.issueflower.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mussrose.issueflower.entities.Bookmark

@Database(entities = [Bookmark::class],version = 1)
abstract class DataBase:RoomDatabase(){

    abstract fun issueDao():Dao
}