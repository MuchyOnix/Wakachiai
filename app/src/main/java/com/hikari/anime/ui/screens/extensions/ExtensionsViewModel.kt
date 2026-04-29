package com.hikari.anime.ui.screens.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hikari.anime.data.repository.ExtensionRepository
import com.hikari.anime.extension.api.AnimeSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExtensionsUiState(
    val sources: List<AnimeSource> = emptyList(),
    val activeId: Long = 4815162342L
)

@HiltViewModel
class ExtensionsViewModel @Inject constructor(
    private val repository: ExtensionRepository
) : ViewModel() {
    val state: StateFlow<ExtensionsUiState> = repository.activeSourceId
        .map { ExtensionsUiState(repository.getAllSources(), it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExtensionsUiState(repository.getAllSources()))

    fun setActiveSource(id: Long) {
        viewModelScope.launch { repository.setActiveSource(id) }
    }
}
