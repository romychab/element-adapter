package com.elveum.elementadapter

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun ImageView.setTintColor(@ColorRes colorRes: Int) {
    imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            context,
            colorRes
        )
    )
}