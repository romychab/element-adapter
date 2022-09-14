package com.elveum.elementadapter

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding

fun ImageView.setTintColor(@ColorRes colorRes: Int) {
    imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            context,
            colorRes
        )
    )
}

fun <B : ViewBinding> B.context(): Context = this.root.context
