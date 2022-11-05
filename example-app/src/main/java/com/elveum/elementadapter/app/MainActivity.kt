package com.elveum.elementadapter.app

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.elveum.elementadapter.*
import com.elveum.elementadapter.app.databinding.ActivityMainBinding
import com.elveum.elementadapter.app.databinding.ItemCatBinding
import com.elveum.elementadapter.app.databinding.ItemHeaderBinding
import com.elveum.elementadapter.app.model.Cat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = createCatsAdapter()
        (binding.catsRecyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
        binding.catsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.catsRecyclerView.adapter = adapter
        viewModel.catsLiveData.observe(this, adapter::submitList)
    }

    // Example of adapter { ... } usage
    private fun createCatsAdapter() = adapter<CatListItem> {
        addBinding<CatListItem.Cat, ItemCatBinding> {
            areItemsSame = { oldCat, newCat -> oldCat.id == newCat.id }
            changePayload = { oldCat, newCat ->
                if (!oldCat.isFavorite && newCat.isFavorite) {
                    FAVORITE_FLAG_CHANGED
                } else {
                    NO_ANIMATION
                }
            }
            stableId = { cat ->
                cat.id
            }

            bindWithPayloads { cat, payloads ->
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
                if (payloads.any { it == FAVORITE_FLAG_CHANGED }) {
                    favoriteImageView.startAnimation(animationForFavoriteFlag)
                }
            }

            listeners {
                deleteImageView.onClick(viewModel::deleteCat)
                favoriteImageView.onClick(viewModel::toggleFavorite)
                root.onClick { cat ->
                    Toast.makeText(context(), "${cat.name} meow-meows", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addBinding<CatListItem.Header, ItemHeaderBinding> {
            areItemsSame = { oldHeader, newHeader -> oldHeader.headerId == newHeader.headerId }
            stableId = { header ->
                header.headerId.toLong()
            }
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
                Toast.makeText(context(), "${cat.name} meow-meows", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val animationForFavoriteFlag by lazy(LazyThreadSafetyMode.NONE) {
        val toSmall = ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        val smallToLarge = ScaleAnimation(1f, 1.5f, 1f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        val largeToNormal = ScaleAnimation(1f, 0.83f, 1f, 0.83f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        val animationSet = AnimationSet(true).apply {
            addAnimation(toSmall)
            addAnimation(smallToLarge)
            addAnimation(largeToNormal)
        }
        animationSet.animations.forEachIndexed { index, animation ->
            animation.duration = 100L
            animation.startOffset = index * 100L
        }
        animationSet
    }

    private companion object {
        val FAVORITE_FLAG_CHANGED = Any()
        val NO_ANIMATION = Any()
    }
}