package com.elveum.elementadapter.dsl

import android.view.View

interface CustomListenerScope<T> {
    /**
     * Get the current item attached to the view
     */
    fun item(): T
}

internal class CustomListenerScopeImpl<T>(
    private val view: View
) : CustomListenerScope<T> {
    override fun item(): T = view.tag as T
}
