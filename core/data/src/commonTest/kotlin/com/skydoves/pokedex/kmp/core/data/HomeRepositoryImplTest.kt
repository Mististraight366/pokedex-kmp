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

package com.skydoves.pokedex.kmp.core.data

import app.cash.turbine.test
import com.skydoves.pokedex.kmp.core.data.repository.HomeRepositoryImpl
import com.skydoves.pokedex.kmp.core.data.repository.PokemonPageResult
import com.skydoves.pokedex.kmp.core.database.InMemoryPokemonLocalDataSource
import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.network.PokedexClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HomeRepositoryImplTest {

  @Test
  fun `an empty cache falls through to the network and stores what came back`() = runTest {
    val localDataSource = InMemoryPokemonLocalDataSource()
    val repository = HomeRepositoryImpl(
      pokedexClient = PokedexClient(FakePokedexService()),
      localDataSource = localDataSource,
    )

    repository.fetchPokemonList(page = 0).test {
      val result = assertIs<PokemonPageResult.Success>(awaitItem())
      assertEquals(PokedexClient.PAGING_SIZE, result.pokemons.size)
      assertEquals("Bulbasaur", result.pokemons[0].name)
      assertEquals("https://pokeapi.co/api/v2/pokemon/1/", result.pokemons[0].url)
      awaitComplete()
    }

    assertEquals(PokedexClient.PAGING_SIZE, localDataSource.getPokemonList(page = 0).size)
  }

  @Test
  fun `a warm cache is served without touching the network`() = runTest {
    val localDataSource = InMemoryPokemonLocalDataSource()
    localDataSource.insertPokemonList(
      listOf(
        Pokemon(page = 0, nameField = "Pikachu", url = "https://pokeapi.co/api/v2/pokemon/25/"),
      ),
    )
    val service = FakePokedexService()
    val repository = HomeRepositoryImpl(PokedexClient(service), localDataSource)

    repository.fetchPokemonList(page = 0).test {
      val result = assertIs<PokemonPageResult.Success>(awaitItem())
      assertEquals(listOf("Pikachu"), result.pokemons.map { it.name })
      awaitComplete()
    }

    assertEquals(0, service.listCallCount)
  }

  @Test
  fun `pages accumulate so the grid always receives the full list`() = runTest {
    val localDataSource = InMemoryPokemonLocalDataSource()
    val repository = HomeRepositoryImpl(PokedexClient(FakePokedexService()), localDataSource)

    repository.fetchPokemonList(page = 0).test {
      assertEquals(
        PokedexClient.PAGING_SIZE,
        assertIs<PokemonPageResult.Success>(awaitItem()).pokemons.size,
      )
      awaitComplete()
    }
    repository.fetchPokemonList(page = 1).test {
      assertEquals(
        PokedexClient.PAGING_SIZE * 2,
        assertIs<PokemonPageResult.Success>(awaitItem()).pokemons.size,
      )
      awaitComplete()
    }
  }

  @Test
  fun `the accumulated list is ordered by pokedex index rather than insertion order`() = runTest {
    val localDataSource = InMemoryPokemonLocalDataSource()
    val repository = HomeRepositoryImpl(PokedexClient(FakePokedexService()), localDataSource)

    // Seed a later Pokemon first so insertion order and index order disagree.
    localDataSource.insertPokemonList(
      listOf(
        Pokemon(page = 1, nameField = "Mewtwo", url = "https://pokeapi.co/api/v2/pokemon/150/"),
      ),
    )
    repository.fetchPokemonList(page = 0).test {
      val result = assertIs<PokemonPageResult.Success>(awaitItem())
      assertEquals((1..PokedexClient.PAGING_SIZE).toList(), result.pokemons.map { it.index })
      awaitComplete()
    }

    repository.fetchPokemonList(page = 1).test {
      val result = assertIs<PokemonPageResult.Success>(awaitItem())
      assertEquals((1..PokedexClient.PAGING_SIZE).toList() + 150, result.pokemons.map { it.index })
      awaitComplete()
    }
  }

  @Test
  fun `the last page is reported once the api stops offering a next link`() = runTest {
    val repository = HomeRepositoryImpl(
      pokedexClient = PokedexClient(FakePokedexService(lastPage = 1)),
      localDataSource = InMemoryPokemonLocalDataSource(),
    )

    repository.fetchPokemonList(page = 0).test {
      assertFalse(assertIs<PokemonPageResult.Success>(awaitItem()).isLastPage)
      awaitComplete()
    }

    repository.fetchPokemonList(page = 1).test {
      assertTrue(assertIs<PokemonPageResult.Success>(awaitItem()).isLastPage)
      awaitComplete()
    }
  }

  @Test
  fun `a failed request surfaces a failure result instead of completing empty`() = runTest {
    val repository = HomeRepositoryImpl(
      pokedexClient = PokedexClient(FakePokedexService(failing = true)),
      localDataSource = InMemoryPokemonLocalDataSource(),
    )

    repository.fetchPokemonList(page = 0).test {
      val result = assertIs<PokemonPageResult.Failure>(awaitItem())
      assertTrue(result.message != null, "expected a failure message")
      awaitComplete()
    }
  }

  @Test
  fun `a failure leaves the cache untouched so the page can be retried`() = runTest {
    val localDataSource = InMemoryPokemonLocalDataSource()
    val repository = HomeRepositoryImpl(
      pokedexClient = PokedexClient(FakePokedexService(failing = true)),
      localDataSource = localDataSource,
    )

    repository.fetchPokemonList(page = 0).test {
      assertIs<PokemonPageResult.Failure>(awaitItem())
      awaitComplete()
    }

    assertEquals(0, localDataSource.getPokemonListOfPage(page = 0).size)
  }
}
