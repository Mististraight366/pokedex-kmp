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

package com.skydoves.pokedex.kmp.app.shared.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.pokedex.kmp.core.data.repository.HomeRepository
import com.skydoves.pokedex.kmp.core.data.repository.PokemonPageResult
import com.skydoves.pokedex.kmp.core.model.Pokemon
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ContributesIntoMap(AppScope::class)
@ViewModelKey(HomeViewModel::class)
public class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

  private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
  public val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
  public val pokemonList: StateFlow<List<Pokemon>> = _pokemonList.asStateFlow()

  /**
   * The page to request next, and a latch that closes for the duration of a request.
   *
   * Both are [MutableStateFlow] rather than plain `var`s because the request completes on
   * [com.skydoves.pokedex.kmp.core.common.ioDispatcher] while the scroll listener reads them on the
   * main thread, and a plain field gives no visibility guarantee between the two.
   */
  private val nextPage = MutableStateFlow(0)
  private val isLoading = MutableStateFlow(false)
  private val isLastPageReached = MutableStateFlow(false)

  init {
    loadNextPage()
  }

  /**
   * Requests the next page, unless one is already in flight or the Pokédex has run out.
   *
   * The latch is closed synchronously here rather than waiting for the repository to report that
   * it started. A scroll can bring several trailing items into view in a single frame, and every
   * one of them calls this; the state flow only turns to `Loading` a dispatch later, so a guard
   * based on it would let all of them through and skip that many pages at once.
   */
  public fun fetchNextPokemonList() {
    if (!isLoading.compareAndSet(expect = false, update = true)) return
    if (isLastPageReached.value) {
      isLoading.value = false
      return
    }
    loadPage(nextPage.value)
  }

  /** Retries the page that failed. Safe to call from an error button. */
  public fun retry() {
    if (!isLoading.compareAndSet(expect = false, update = true)) return
    loadPage(nextPage.value)
  }

  private fun loadNextPage() {
    isLoading.value = true
    loadPage(nextPage.value)
  }

  private fun loadPage(page: Int) {
    _uiState.value = HomeUiState.Loading
    viewModelScope.launch {
      homeRepository.fetchPokemonList(page).collect { result ->
        when (result) {
          is PokemonPageResult.Success -> {
            _pokemonList.value = result.pokemons
            isLastPageReached.value = result.isLastPage
            // Only advance past a page that actually landed, so a failure retries the same page
            // instead of leaving a permanent hole in the list.
            nextPage.update { page + 1 }
            _uiState.value = HomeUiState.Idle
          }

          is PokemonPageResult.Failure -> {
            _uiState.value = HomeUiState.Error(result.message)
          }
        }
      }
      isLoading.value = false
    }
  }
}

public sealed interface HomeUiState {
  public data object Idle : HomeUiState
  public data object Loading : HomeUiState
  public data class Error(val message: String?) : HomeUiState
}
