package com.elveum.elementadapter.dsl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.delegate.AdapterDelegate
import com.elveum.elementadapter.delegate.AdapterDelegateImpl

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

    fun toAdapterDelegate(): AdapterDelegate<T> {
        if (concreteTypeScopes.isEmpty()) {
            throw IllegalStateException("Have you added at least one addBinding { ... } / universalBinding { ... } section?")
        }

        return AdapterDelegateImpl(
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
