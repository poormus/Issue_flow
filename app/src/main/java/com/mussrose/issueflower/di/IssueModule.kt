package com.mussrose.issueflower.di

import android.content.Context
import androidx.room.Room
import com.mussrose.issueflow.repo.AuthRepo
import com.mussrose.issueflower.db.DataBase
import com.mussrose.issueflower.repo.DefaultAuthRepo
import com.mussrose.issueflower.repo.DefaultIssueRepo
import com.mussrose.issueflower.repo.IssueRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object IssueModule {

    @Provides
    @ActivityScoped
    fun provideIssueRepository()= DefaultIssueRepo() as IssueRepo


    @Provides
    @ActivityScoped
    fun provideDb(@ApplicationContext context: Context)= Room.databaseBuilder(
        context,
        DataBase::class.java,
        "bookmark_db"
    ).build()


    @Provides
    @ActivityScoped
    fun provideDao(db:DataBase)=db.issueDao()
}