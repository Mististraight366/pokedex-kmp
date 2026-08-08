/*
 * Designed and developed by 2026 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.pokedex.kmp.app.shared.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.pokedex.kmp.core.data.repository.DetailsRepository
import com.skydoves.pokedex.kmp.core.data.repository.PokemonInfoResult
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The Pokémon to show arrives through [load] rather than through the constructor, which keeps the
 * ViewModel constructible from a test with nothing but a repository.
 */
@Inject
@ContributesIntoMap(AppScope::class)
@ViewModelKey(DetailsViewModel::class)
public class DetailsViewModel(private val detailsRepository: DetailsRepository) : ViewModel() {

  private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
  public val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

  private val _pokemonInfo = MutableStateFlow<PokemonInfo?>(null)
  public val pokemonInfo: StateFlow<PokemonInfo?> = _pokemonInfo.asStateFlow()

  private var loadedName: String? = null

  public fun load(name: String) {
    if (loadedName == name && _uiState.value !is DetailsUiState.Error) return
    loadedName = name
    fetch(name)
  }

  /** Retries the last requested Pokémon. The [load] guard does not block this path. */
  public fun retry() {
    loadedName?.let(::fetch)
  }

  private fun fetch(name: String) {
    _uiState.value = DetailsUiState.Loading
    viewModelScope.launch {
      detailsRepository.fetchPokemonInfo(name).collect { result ->
        when (result) {
          is PokemonInfoResult.Success -> {
            _pokemonInfo.value = result.pokemonInfo
            _uiState.value = DetailsUiState.Idle
          }

          is PokemonInfoResult.Failure -> {
            _uiState.value = DetailsUiState.Error(result.message)
          }
        }
      }
    }
  }
}

public sealed interface DetailsUiState {
  public data object Idle : DetailsUiState
  public data object Loading : DetailsUiState
  public data class Error(val message: String?) : DetailsUiState
}
