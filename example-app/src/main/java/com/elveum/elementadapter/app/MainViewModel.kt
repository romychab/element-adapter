package com.elveum.elementadapter.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elveum.elementadapter.app.model.Cat
import com.elveum.elementadapter.app.model.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsLiveData = MutableLiveData<List<CatListItem>>()
    val catsLiveData: LiveData<List<CatListItem>> = _catsLiveData

    init {
        viewModelScope.launch {
            catsRepository.getCats().collectLatest { catsList ->
                _catsLiveData.value = mapCats(catsList)
            }
        }
    }

    fun deleteCat(cat: CatListItem.Cat) {
        catsRepository.delete(cat.originCat)
    }

    fun toggleFavorite(cat: CatListItem.Cat) {
        catsRepository.toggleIsFavorite(cat.originCat)
    }

    private fun mapCats(cats: List<Cat>): List<CatListItem> {
        val size = 10
        return cats
            .chunked(size)
            .mapIndexed { index, list ->
                val fromIndex = index * size + 1
                val toIndex = fromIndex + list.size - 1
                val header: CatListItem = CatListItem.Header(index, fromIndex, toIndex)
                listOf(header) + list.map { CatListItem.Cat(it) }
            }
            .flatten()
    }

}