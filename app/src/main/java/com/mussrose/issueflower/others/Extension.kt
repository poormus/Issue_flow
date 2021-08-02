package com.mussrose.issueflower.others

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.animation.Animation

object Extension {

    fun View.startAnimation(animation: Animation, onEnd: () -> Unit) {
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                onEnd()
            }

            override fun onAnimationRepeat(animation: Animation?) = Unit
        })
        this.startAnimation(animation)
    }


}