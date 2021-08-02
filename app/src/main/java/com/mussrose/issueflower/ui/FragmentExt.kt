package com.mussrose.issueflower.ui

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackBar(message: String) {
    Snackbar.make(requireView().rootView, message, Snackbar.LENGTH_SHORT).show()
}