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
     * A callback for creating payloads which indicate the concrete difference
     * between an old item and a new item. May be useful for animation,
     * optimizations, etc.
     */
    var changePayload: ChangePayloadCallback<T>

    /**
     * Start a binding section where you can assign data from your model
     * list item to the view binding.
     */
    fun bind(block: B.(T) -> Unit)

    /**
     * Start a binding section where you can:
     * 1) assign data from your model list item to the view binding.
     * 2) use RecyclerView payloads for updating/animating views
     */
    fun bindWithPayloads(block: B.(item: T, payloads: List<Any>) -> Unit)

    /**
     * Start a listeners section where you can assign click listeners. Now
     * [onClick], [onLongClick] and [onCustomListener] are supported.
     */
    fun listeners(block: B.() -> Unit)

    fun View.onClick(listener: CustomListenerIndexScope.(T) -> Unit)

    fun View.onLongClick(listener: CustomListenerIndexScope.(T) -> Boolean)

    /**
     * Setup custom view listener.
     *
     * Usage example:
     *
     * ```
     * view.onCustomListener {
     *     view.setOnDoubleTapListener {
     *         val item = item() // get the data attached to this view
     *         // do something here
     *     }
     * }
     * ```
     */
    fun View.onCustomListener(block: CustomListenerScope<T>.() -> Unit)

    /**
     * Get the index of an element being rendered.
     * This method can be called within bind { ... } block.
     *
     * Please note that you need to customize `areContentsSame` block and
     * take into account index changes there if you decide to use this method
     * in your `bind { ... }` blocks.
     */
    fun index(): Int

}

internal class ConcreteItemTypeScopeImpl<T : Any, B : ViewBinding>(
    override var areContentsSame: CompareItemCallback<T>,
    override var areItemsSame: CompareItemCallback<T>,
    override var changePayload: ChangePayloadCallback<T>,
    val bindingCreator: (inflater: LayoutInflater, parent: ViewGroup) -> B,
    val predicate: (T) -> Boolean
) : ConcreteItemTypeScope<T, B> {

    var bindBlock: (B.(item: T, payloads: List<Any>) -> Unit)? = null
    var listenersBlock: (B.() -> Unit)? = null
    var uponCreating: Boolean = false
    var currentIndex: Int? = null

    val viewsWithListeners = mutableSetOf<Int>()

    override fun bind(block: B.(T) -> Unit) {
        this.bindBlock = { item, _ ->
            block(item)
        }
    }

    override fun bindWithPayloads(block: B.(item: T, payloads: List<Any>) -> Unit) {
        this.bindBlock = block
    }

    override fun listeners(block: B.() -> Unit) {
        this.listenersBlock = block
    }

    override fun index(): Int {
        return currentIndex ?: throw IllegalStateException("index() can be called only within bind { ... } block")
    }

    override fun View.onClick(listener: CustomListenerIndexScope.(T) -> Unit) {
        onCustomListener {
            setOnClickListener {
                listener(item())
            }
        }
    }

    override fun View.onLongClick(listener: CustomListenerIndexScope.(T) -> Boolean) {
        onCustomListener {
            setOnLongClickListener {
                listener(item())
            }
        }
    }

    override fun View.onCustomListener(block: CustomListenerScope<T>.() -> Unit) {
        assertListenerCall()
        val scope = CustomListenerScopeImpl<T>(this)
        scope.block()
    }

    private fun View.assertListenerCall() {
        if (!uponCreating) throw IllegalStateException("View.onClick() should be called only within listeners { ... } section.")
        viewsWithListeners.add(id)
    }

}
