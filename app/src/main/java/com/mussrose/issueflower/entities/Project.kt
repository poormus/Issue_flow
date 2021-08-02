package com.mussrose.issueflower.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



data class Project(
    val id:String="",
    val name:String="",
    val description:String="",
    val state:String="",
    val date:Long=0L
)