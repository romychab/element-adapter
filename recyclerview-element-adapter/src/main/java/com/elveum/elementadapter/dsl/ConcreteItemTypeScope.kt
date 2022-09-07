package com.elveum.elementadapter.dsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface ConcreteItemTypeScope<T : Any, B : ViewBinding> {

    /**
     * A callback for checking whether items are the same or not.
     * Usually the callback should compare identifiers
     */
    var areItemsSame: CompareItemCallback<T>

    /**
     * A callback for checking whether items' contents are equal or
     * not.
     */
    var areContentsSame: CompareItemCallback<T>

    /**
     * Start a binding section where you can assign data from your model
     * list item to the view binding.
     */
    fun bind(block: B.(T) -> Unit)

    /**
     * Start a listeners section where you can assign click listeners. Now
     * [onClick] and [onLongClick] are supported.
     */
    fun listeners(block: B.() -> Unit)

    fun View.onClick(listener: (T) -> Unit)

    fun View.onLongClick(listener: (T) -> Boolean)

}

internal class ConcreteItemTypeScopeImpl<T : Any, B : ViewBinding>(
    val bindingCreator: (inflater: LayoutInflater, parent: ViewGroup) -> B,
    val predicate: (T) -> Boolean
) : ConcreteItemTypeScope<T, B> {

    private val defaultCompareItemsSameCallback: CompareItemCallback<T> =
        { oldItem, newItem -> oldItem == newItem }

    private val defaultCompareContentsCallback: CompareItemCallback<T> =
        { oldItem, newItem -> oldItem == newItem }


    override var areContentsSame: CompareItemCallback<T> = defaultCompareContentsCallback
    override var areItemsSame: CompareItemCallback<T> = defaultCompareItemsSameCallback

    var bindBlock: (B.(T) -> Unit)? = null
    var listenersBlock: (B.() -> Unit)? = null
    var uponCreating: Boolean = false

    val viewsWithListeners = mutableSetOf<Int>()

    override fun bind(block: B.(T) -> Unit) {
        this.bindBlock = block
    }

    override fun listeners(block: B.() -> Unit) {
        this.listenersBlock = block
    }

    override fun View.onClick(listener: (T) -> Unit) {
        assertListenerCall()
        setOnClickListener {
            val item: T = it.tag as T
            listener(item)
        }
    }

    override fun View.onLongClick(listener: (T) -> Boolean) {
        assertListenerCall()
        setOnLongClickListener {
            val item: T = it.tag as T
            listener(item)
        }
    }

    private fun View.assertListenerCall() {
        if (!uponCreating) throw IllegalStateException("View.onClick() should be called only within listeners { ... } section.")
        viewsWithListeners.add(id)
    }

}
