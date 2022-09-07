package com.elveum.elementadapter.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.adapter
import com.elveum.elementadapter.addBinding
import com.elveum.elementadapter.app.databinding.ActivityMainBinding
import com.elveum.elementadapter.app.databinding.ItemCatBinding
import com.elveum.elementadapter.app.databinding.ItemHeaderBinding
import com.elveum.elementadapter.app.model.Cat
import com.elveum.elementadapter.setTintColor
import com.elveum.elementadapter.simpleAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = createCatsAdapter()
        (binding.catsRecyclerView.itemAnimator as? DefaultItemAnimator)!!.supportsChangeAnimations = false
        binding.catsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.catsRecyclerView.adapter = adapter
        viewModel.catsLiveData.observe(this, adapter::submitList)
    }

    // Example of adapter { ... } usage
    private fun createCatsAdapter() = adapter<CatListItem> {

        addBinding<CatListItem.Cat, ItemCatBinding> {
            areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }

            bind { cat ->
                catNameTextView.text = cat.name
                catDescriptionTextView.text = cat.description
                catImageView.load(cat.photoUrl) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.circle)
                }
                favoriteImageView.setImageResource(
                    if (cat.isFavorite) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_not
                )
                favoriteImageView.setTintColor(
                    if (cat.isFavorite) R.color.highlighted_action
                    else R.color.action
                )
            }

            listeners {
                deleteImageView.onClick(viewModel::deleteCat)
                favoriteImageView.onClick(viewModel::toggleFavorite)
                root.onClick { cat ->
                    Toast.makeText(this@MainActivity, "${cat.name} meow-meows", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addBinding<CatListItem.Header, ItemHeaderBinding> {
            areItemsSame = { oldHeader, newHeader -> oldHeader.headerId == newHeader.headerId }
            bind { header ->
                titleTextView.text = getString(R.string.cats, header.fromIndex, header.toIndex)
            }
        }

    }

    // Example of simpleAdapter { ... } usage:
    private fun createOnlyCatsAdapter() = simpleAdapter<Cat, ItemCatBinding> {
        areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
        areContentsSame = { oldCat, newCat -> oldCat == newCat }
        bind { cat ->
            catNameTextView.text = cat.name
            catDescriptionTextView.text = cat.description
        }
        listeners {
            deleteImageView.onClick { cat ->
                // delete the cat here
            }
            root.onClick { cat ->
                Toast.makeText(this@MainActivity, "${cat.name} meow-meows", Toast.LENGTH_SHORT).show()
            }
        }
    }

}