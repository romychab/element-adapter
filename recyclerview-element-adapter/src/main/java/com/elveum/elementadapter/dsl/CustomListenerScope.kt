package com.elveum.elementadapter.dsl

import android.view.View
import com.elveum.elementadapter.R

interface CustomListenerIndexScope {

    /**
     * Get the current index of an element for which a callback has been called.
     */
    fun index(): Int
}

interface CustomListenerScope<T> : CustomListenerIndexScope {
    /**
     * Get the current item attached to the view
     */
    fun item(): T
}

@Suppress("UNCHECKED_CAST")
internal class CustomListenerScopeImpl<T>(
    private val view: View
) : CustomListenerScope<T> {
    override fun item(): T = view.getTag(R.id.element_entity_tag) as T
    override fun index(): Int = view.getTag(R.id.element_index_tag) as Int
}
