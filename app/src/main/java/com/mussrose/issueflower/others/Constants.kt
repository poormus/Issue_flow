package com.mussrose.issueflower.others

import java.util.regex.Pattern

object Constants {

    const val DEFAULT_PROFILE_PIC_URL="https://firebasestorage.googleapis.com/v0/b/issue-flower.appspot.com/o/img_avatar.png?alt=media&token=e56deedc-2119-470c-b0df-f07556b4b4b2"

    val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                //"(?=.*[a-zA-Z])" +  //any letter
                "(?=\\S+$)" +  //no white spaces
                ".{8,}"  //at least 8 characters

    )
}