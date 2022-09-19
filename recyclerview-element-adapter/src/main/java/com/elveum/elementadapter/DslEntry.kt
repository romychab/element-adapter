package com.elveum.elementadapter

import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.elveum.elementadapter.delegate.adapterDelegate
import com.elveum.elementadapter.dsl.*
import com.elveum.elementadapter.dsl.AdapterScopeImpl


typealias SimpleBindingAdapter<T> = ListAdapter<T, BindingHolder>

/**
 * Crate an instance of a [ListAdapter] for the specified base type [T].
 * This method should be used when you need to support more than 1 view binding type.
 * Otherwise it's better to use [simpleAdapter] instead.
 *
 * Usage example. Let's image you have a base class `BaseListItem` and the following
 * subclasses:
 * - `ListHeaderItem`
 * - `ListContentItem`
 *
 * And you want to create a list which displays those items in a different way: like
 * you are going to use 2 layouts:
 * - one for header items (`R.layout.item_header` aka `ItemHeaderBinding`)
 * - and one for content items (`R.layout.item_content` aka `ItemContentBinding`)

 * Let's do this:
 *
 * ```
 * val adapter = adapter<BaseListItem> {
 *     addBinding<ListHeaderItem, ItemHeaderBinding> {
 *         areItemsSame = { oldHeader, newHeader -> oldHeader.id == newHeader.id }
 *         areContentsSame = { oldHeader, newHeader -> oldHeader == newHeader }
 *         bind { header ->
 *             titleTextView.text = header.title
 *             descriptionTextView.text = header.description
 *         }
 *         listeners {
 *             root.onClick { header ->
 *                 viewModel.toggle(header)
 *             }
 *         }
 *     }
 *
 *     addBinding<ListContentItem, ItemContentBinding> {
 *         areItemsSame = { oldContent, newContent -> oldContent.id == newContent.id }
 *         areContentsSame = { oldContent, newContent -> oldContent == newContent }
 *         bind { contentItem ->
 *             contentTextView.text = contentItem.content
 *             previewImageView.setImageResource(contentItem.previewImageRes)
 *         }
 *         listeners {
 *             deleteButton.onClick { contentItem ->
 *                 viewModel.delete(contentItem)
 *             }
 *             root.onClick { contentItem ->
 *                 viewModel.openDetails(contentItem)
 *             }
 *         }
 *     }
 * }
 * ```
 */
fun <T : Any> adapter(block: AdapterScope<T>.() -> Unit): SimpleBindingAdapter<T> {
    return MultiAdapter(adapterDelegate(block))
}

/**
 * Add a new view binding type to the adapter.
 */
inline fun <reified T : Any, reified B : ViewBinding> AdapterScope<in T>.addBinding(
    noinline block: ConcreteItemTypeScope<T, B>.() -> Unit
) {
    (this as AdapterScopeImpl<in T>).addBinding(
        B::class.java,
        { item -> item is T },
        block
    )
}

/**
 * Create a [ListAdapter] which binds items of the type [T] to
 * the specified view binding of the type [B].
 * Usage example:
 * ```
 * val adapter = simpleAdapter<Cat, ItemCatBinding> {
 *     areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
 *     areContentsSame = { oldCat, newCat -> oldCat == newCat }
 *     bind { cat ->
 *         catNameTextView.text = cat.name
 *         catDescriptionTextView.text = cat.description
 *     }
 *     listeners {
 *         root.onClick { cat ->
 *             showCatDetails(cat)
 *         }
 *     }
 * }
 *
 * recyclerView.adapter = adapter
 *
 * viewModel.catsLiveData.observer(viewLifecycleOwner) { list ->
 *     adapter.submitList(list)
 * }
 * ```
 */
inline fun <reified T : Any, reified B : ViewBinding> simpleAdapter(
    noinline block: ConcreteItemTypeScope<T, B>.() -> Unit
): SimpleBindingAdapter<T> {
    return adapter { addBinding(block) }
}
