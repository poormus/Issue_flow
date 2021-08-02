package com.mussrose.issueflower.entities.updateentities

import android.net.Uri

data class ProfileUpdate(
    val uidToUpdate:String="",
    val username:String,
    val profilePictureUri:Uri?=null
)
