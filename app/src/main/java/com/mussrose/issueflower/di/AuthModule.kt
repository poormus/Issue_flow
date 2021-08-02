package com.mussrose.issueflower.di

import com.mussrose.issueflow.repo.AuthRepo
import com.mussrose.issueflower.repo.DefaultAuthRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @Provides
    @ActivityScoped
    fun provideAuthRepository()= DefaultAuthRepo() as AuthRepo
}