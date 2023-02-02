package com.elveum.elementadapter.dsl

class ElementWithIndex<T>(
    val index: Int,
    val element: T,
)

interface IndexScope<T> {
    /**
     * Get an index of `oldItem` or `newItem`.
     */
    fun index(item: T): Int
}

internal class IndexScopeImpl<T>(
    private val oldElementWithIndex: ElementWithIndex<T>,
    private val newElementWithIndex: ElementWithIndex<T>,
) : IndexScope<T> {
    override fun index(item: T): Int {
        if (item === oldElementWithIndex.element) return oldElementWithIndex.index
        if (item === newElementWithIndex.element) return newElementWithIndex.index
        throw IllegalArgumentException("Unknown item! You can pass only newItem or oldItem as an argument for index() call")
    }
}
