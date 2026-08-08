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

import com.skydoves.pokedex.kmp.core.data.repository.DetailsRepository
import com.skydoves.pokedex.kmp.core.data.repository.PokemonInfoResult
import com.skydoves.pokedex.kmp.core.test.MockUtil
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

  private val dispatcher = StandardTestDispatcher()

  @BeforeTest
  fun setUp() = Dispatchers.setMain(dispatcher)

  @AfterTest
  fun tearDown() = Dispatchers.resetMain()

  @Test
  fun `loading a pokemon publishes it and settles on idle`() = runTest {
    val viewModel = DetailsViewModel(RecordingDetailsRepository())

    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()

    assertEquals(DetailsUiState.Idle, viewModel.uiState.value)
    assertEquals("bulbasaur", viewModel.pokemonInfo.value?.name)
  }

  /**
   * The regression this exists for: `onError` set Error and the flow's `onCompletion` set Idle one
   * instant later, so the screen showed a spinner that never resolved and never explained itself.
   */
  @Test
  fun `a failure ends on error rather than on a silent idle`() = runTest {
    val viewModel = DetailsViewModel(RecordingDetailsRepository(failWith = "offline"))

    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()

    val state = assertIs<DetailsUiState.Error>(viewModel.uiState.value)
    assertEquals("offline", state.message)
    assertNull(viewModel.pokemonInfo.value)
  }

  @Test
  fun `the same pokemon is not refetched`() = runTest {
    val repository = RecordingDetailsRepository()
    val viewModel = DetailsViewModel(repository)

    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()
    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()

    assertEquals(listOf("bulbasaur"), repository.requestedNames)
  }

  /** After a failure the guard must not block a second attempt at the same Pokemon. */
  @Test
  fun `retry works after a failure`() = runTest {
    val repository = RecordingDetailsRepository(failWith = "offline")
    val viewModel = DetailsViewModel(repository)

    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()
    assertIs<DetailsUiState.Error>(viewModel.uiState.value)

    repository.failWith = null
    viewModel.retry()
    testScheduler.advanceUntilIdle()

    assertEquals(listOf("bulbasaur", "bulbasaur"), repository.requestedNames)
    assertEquals(DetailsUiState.Idle, viewModel.uiState.value)
    assertEquals("bulbasaur", viewModel.pokemonInfo.value?.name)
  }

  @Test
  fun `load after a failure retries rather than being swallowed by the guard`() = runTest {
    val repository = RecordingDetailsRepository(failWith = "offline")
    val viewModel = DetailsViewModel(repository)

    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()

    repository.failWith = null
    viewModel.load("bulbasaur")
    testScheduler.advanceUntilIdle()

    assertEquals(listOf("bulbasaur", "bulbasaur"), repository.requestedNames)
    assertEquals(DetailsUiState.Idle, viewModel.uiState.value)
  }

  private class RecordingDetailsRepository(var failWith: String? = null) : DetailsRepository {

    val requestedNames = mutableListOf<String>()

    override fun fetchPokemonInfo(name: String): Flow<PokemonInfoResult> = flow {
      requestedNames += name
      val failure = failWith
      emit(
        if (failure != null) {
          PokemonInfoResult.Failure(failure)
        } else {
          PokemonInfoResult.Success(MockUtil.mockPokemonInfo().copy(name = name))
        },
      )
    }
  }
}
