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

import com.skydoves.pokedex.kmp.core.data.repository.HomeRepository
import com.skydoves.pokedex.kmp.core.data.repository.PokemonPageResult
import com.skydoves.pokedex.kmp.core.model.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

  private val dispatcher = StandardTestDispatcher()

  @BeforeTest
  fun setUp() = Dispatchers.setMain(dispatcher)

  @AfterTest
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `the first page loads without anyone asking`() = runTest {
    val repository = RecordingHomeRepository()
    val viewModel = HomeViewModel(repository)
    testScheduler.advanceUntilIdle()

    assertEquals(listOf(0), repository.requestedPages)
    assertEquals(HomeUiState.Idle, viewModel.uiState.value)
    assertEquals(20, viewModel.pokemonList.value.size)
  }

  /**
   * The regression this exists for: the prefetch used to run from inside a LazyGrid item body, so
   * every trailing item composed in one frame called it, and each saw a state that had not yet
   * flipped to Loading. The page counter jumped by as many items as were on screen and every page
   * in between was never requested.
   */
  @Test
  fun `a burst of prefetch calls advances exactly one page`() = runTest {
    val repository = RecordingHomeRepository()
    val viewModel = HomeViewModel(repository)
    testScheduler.advanceUntilIdle()

    repeat(8) { viewModel.fetchNextPokemonList() }
    testScheduler.advanceUntilIdle()

    assertEquals(listOf(0, 1), repository.requestedPages)
  }

  @Test
  fun `a failure surfaces an error instead of being erased by completion`() = runTest {
    val repository = RecordingHomeRepository(failFrom = 0)
    val viewModel = HomeViewModel(repository)
    testScheduler.advanceUntilIdle()

    val state = assertIs<HomeUiState.Error>(viewModel.uiState.value)
    assertEquals("offline", state.message)
  }

  /**
   * A failed page used to be skipped for good: the counter advanced before the outcome was known,
   * so the twenty Pokemon in that page could never be fetched again.
   */
  @Test
  fun `a failed page is retried rather than skipped`() = runTest {
    val repository = RecordingHomeRepository(failFrom = 1)
    val viewModel = HomeViewModel(repository)
    testScheduler.advanceUntilIdle()

    viewModel.fetchNextPokemonList()
    testScheduler.advanceUntilIdle()
    assertIs<HomeUiState.Error>(viewModel.uiState.value)

    repository.failFrom = null
    viewModel.retry()
    testScheduler.advanceUntilIdle()

    assertEquals(listOf(0, 1, 1), repository.requestedPages)
    assertEquals(HomeUiState.Idle, viewModel.uiState.value)
    assertEquals(40, viewModel.pokemonList.value.size)
  }

  @Test
  fun `paging stops once the api reports the last page`() = runTest {
    val repository = RecordingHomeRepository(lastPage = 0)
    val viewModel = HomeViewModel(repository)
    testScheduler.advanceUntilIdle()

    repeat(5) { viewModel.fetchNextPokemonList() }
    testScheduler.advanceUntilIdle()

    assertEquals(listOf(0), repository.requestedPages)
  }

  private class RecordingHomeRepository(
    var failFrom: Int? = null,
    private val lastPage: Int = Int.MAX_VALUE,
  ) : HomeRepository {

    val requestedPages = mutableListOf<Int>()

    override fun fetchPokemonList(page: Int): Flow<PokemonPageResult> = flow {
      requestedPages += page
      if (failFrom != null && page >= failFrom!!) {
        emit(PokemonPageResult.Failure("offline"))
        return@flow
      }
      val pokemons = (0..page).flatMap { p ->
        List(20) { i ->
          val index = p * 20 + i + 1
          Pokemon(
            page = p,
            nameField = "p$index",
            url = "https://pokeapi.co/api/v2/pokemon/$index/",
          )
        }
      }
      emit(PokemonPageResult.Success(pokemons = pokemons, isLastPage = page >= lastPage))
    }
  }
}
