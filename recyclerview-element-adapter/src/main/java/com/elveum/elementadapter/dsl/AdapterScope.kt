package com.elveum.elementadapter.dsl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

interface AdapterScope<T : Any>

class AdapterScopeImpl<T : Any> internal constructor() : AdapterScope<T> {

    private val concreteTypeScopes = mutableListOf<ConcreteItemTypeScopeImpl<T, ViewBinding>>()

    fun <Subtype : T, B : ViewBinding> addBinding(
        clazz: Class<B>,
        predicate: (T) -> Boolean,
        block: ConcreteItemTypeScope<Subtype, B>.() -> Unit
    ) {
        val concreteItemTypeScopeImpl = ConcreteItemTypeScopeImpl<Subtype, B>(
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

    fun toAdapter(): ListAdapter<T, BindingHolder> {
        if (concreteTypeScopes.isEmpty()) {
            throw IllegalStateException("Have you added at least one addBinding { ... } / universalBinding { ... } section?")
        }

        return MultiAdapter(
            concreteTypeScopes,
            ItemCallbackDelegate(concreteTypeScopes)
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
