package com.elveum.elementadapter.dsl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.delegate.AdapterDelegate
import com.elveum.elementadapter.delegate.AdapterDelegateImpl

interface AdapterScope<T : Any> {

    /**
     * A callback for checking whether items are the same or not.
     * Usually the callback should compare identifiers
     */
    var defaultAreItemsSame: CompareItemCallback<T>

    /**
     * A callback for checking whether items' contents are equal or
     * not.
     */
    var defaultAreContentsSame: CompareItemCallback<T>

    /**
     * A callback for creating payloads which indicate the concrete difference
     * between an old item and a new item. May be useful for animation,
     * optimizations, etc.
     */
    var defaultChangePayload: ChangePayloadCallback<T>

}

class AdapterScopeImpl<T : Any> internal constructor() : AdapterScope<T> {

    private val concreteTypeScopes = mutableListOf<ConcreteItemTypeScopeImpl<T, ViewBinding>>()

    override var defaultAreItemsSame: CompareItemCallback<T> = { oldItem, newItem -> oldItem === newItem }
    override var defaultAreContentsSame: CompareItemCallback<T> = { oldItem, newItem -> oldItem == newItem }
    override var defaultChangePayload: ChangePayloadCallback<T> = { _, _ -> null }

    fun <Subtype : T, B : ViewBinding> addBinding(
        clazz: Class<B>,
        predicate: (T) -> Boolean,
        block: ConcreteItemTypeScope<Subtype, B>.() -> Unit
    ) {
        val concreteItemTypeScopeImpl = ConcreteItemTypeScopeImpl<Subtype, B>(
            areItemsSame = this.defaultAreItemsSame as CompareItemCallback<Subtype>,
            areContentsSame = this.defaultAreContentsSame as CompareItemCallback<Subtype>,
            changePayload = this.defaultChangePayload as ChangePayloadCallback<Subtype>,
            bindingCreator = { inflater, parent ->
                instantiateBinding(clazz, inflater, parent)
            },
            predicate = predicate
        )
        concreteItemTypeScopeImpl.block()
        concreteTypeScopes.add(
            concreteItemTypeScopeImpl as ConcreteItemTypeScopeImpl<T, ViewBinding>
        )
    }

    fun toAdapterDelegate(): AdapterDelegate<T> {
        if (concreteTypeScopes.isEmpty()) {
            throw IllegalStateException("Have you added at least one addBinding { ... } / universalBinding { ... } section?")
        }

        return AdapterDelegateImpl(
            concreteTypeScopes,
            ItemCallbackDelegate(this, concreteTypeScopes)
        )
    }

    private fun <B : ViewBinding> instantiateBinding(
        clazz: Class<B>,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): B {
        val method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return method.invoke(null, inflater, parent, false) as B
    }

}
