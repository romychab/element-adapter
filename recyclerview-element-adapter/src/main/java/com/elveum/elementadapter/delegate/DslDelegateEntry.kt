package com.elveum.elementadapter.delegate

import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.adapter
import com.elveum.elementadapter.addBinding
import com.elveum.elementadapter.dsl.AdapterScope
import com.elveum.elementadapter.dsl.AdapterScopeImpl
import com.elveum.elementadapter.dsl.ConcreteItemTypeScope
import com.elveum.elementadapter.simpleAdapter

/**
 * Create an adapter delegate which can be used as a bridge between this library
 * and either other third-party libraries or your own custom adapters.
 *
 * This method is similar to [adapter] but it returns a delegate instead of adapter.
 */
fun <T : Any> adapterDelegate(block: AdapterScope<T>.() -> Unit): AdapterDelegate<T> {
    val adapterScope = AdapterScopeImpl<T>()
    adapterScope.block()
    return adapterScope.toAdapterDelegate()
}

/**
 * Create an adapter delegate which can be used as a bridge between this library
 * and either other third-party libraries or your own custom adapters.
 *
 * This method is similar to [simpleAdapter] but it returns a delegate instead of adapter.
 */
inline fun <reified T : Any, reified B : ViewBinding> simpleAdapterDelegate(
    noinline block: ConcreteItemTypeScope<T, B>.() -> Unit
): AdapterDelegate<T> {
    return adapterDelegate { addBinding(block) }
}
