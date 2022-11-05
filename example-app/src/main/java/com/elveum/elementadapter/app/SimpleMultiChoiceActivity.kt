package com.elveum.elementadapter.app

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.elveum.elementadapter.app.databinding.ActivitySimpleMultichoiceBinding
import com.elveum.elementadapter.app.databinding.ItemSelectableBinding
import com.elveum.elementadapter.getColor
import com.elveum.elementadapter.simpleAdapter

/*
  This is a very simple example of multi-choice list.

  Please note that in real projects it's better to:
  - use immutable entities ('val isChecked' instead of 'var isChecked')
  - implement multi-choice logic e.g. in the view-model
  - hold data list at least in the view-model, not in the activity
 */

data class SelectableItem(
    val id: Long,
    val name: String,
    var isChecked: Boolean = false
)

class SimpleMultiChoiceActivity : AppCompatActivity() {

    private val items = listOf(
        SelectableItem(1, "Charlie"),
        SelectableItem(2, "Millie"),
        SelectableItem(3, "Lucky"),
        SelectableItem(4, "Poppy"),
        SelectableItem(5, "Oliver"),
        SelectableItem(6, "Sam"),
        SelectableItem(7, "Tiger"),
    )

    private val adapter: SimpleBindingAdapter<SelectableItem> by lazy { createAdapter() }

    private val binding by lazy {
        ActivitySimpleMultichoiceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            multiChoiceRecyclerView.layoutManager = LinearLayoutManager(this@SimpleMultiChoiceActivity)
            (multiChoiceRecyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
            multiChoiceRecyclerView.adapter = adapter
        }
        adapter.submitList(items)
        updateTotalSelected()
    }

    private fun updateTotalSelected() {
        val count = items.count { it.isChecked }
        binding.totalSelectedTextView.text = getString(R.string.total_selected, count)
    }

    private fun createAdapter() = simpleAdapter<SelectableItem, ItemSelectableBinding> {
        areContentsSame = { oldItem, newItem -> oldItem == newItem } // this works fine for data classes
        areItemsSame = { oldItem, newItem -> oldItem.id == newItem.id }

        bind { item ->
            nameTextView.text = item.name
            checkbox.isChecked = item.isChecked
            if (item.isChecked) {
                root.background = ColorDrawable(getColor(R.color.selected_background))
            } else {
                root.background = null
            }
        }

        listeners {
            checkbox.onClick { item ->
                item.isChecked = !item.isChecked
                val indexToUpdate = adapter.currentList.indexOfFirst { item.id == it.id }
                adapter.notifyItemChanged(indexToUpdate)
                updateTotalSelected()
            }
        }
    }
}