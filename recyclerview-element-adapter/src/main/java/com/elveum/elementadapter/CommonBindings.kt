package com.elveum.elementadapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
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

fun <B : ViewBinding> B.resources(): Resources = context().resources

fun <B : ViewBinding> B.getString(@StringRes stringRes: Int, vararg formatArgs: Any?): String {
    return context().getString(stringRes, formatArgs)
}

fun <B : ViewBinding> B.getColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(context(), colorRes)
}