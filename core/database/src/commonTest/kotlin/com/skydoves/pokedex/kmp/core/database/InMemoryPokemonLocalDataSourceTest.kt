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

package com.skydoves.pokedex.kmp.core.database

import com.skydoves.pokedex.kmp.core.model.Pokemon
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * This is the cache the web build runs on and the fake the repository tests run against, so its
 * observable behaviour has to match the Room implementation. Every assertion here is one the Room
 * DAO must also satisfy.
 */
class InMemoryPokemonLocalDataSourceTest {

  @Test
  fun `getPokemonList accumulates every page up to the one requested`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonList(listOf(pokemon(1, page = 0), pokemon(2, page = 0)))
    dataSource.insertPokemonList(listOf(pokemon(3, page = 1)))

    assertEquals(2, dataSource.getPokemonList(page = 0).size)
    assertEquals(3, dataSource.getPokemonList(page = 1).size)
  }

  @Test
  fun `getPokemonListOfPage returns only the page asked for`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonList(listOf(pokemon(1, page = 0), pokemon(3, page = 1)))

    assertEquals(listOf(1), dataSource.getPokemonListOfPage(page = 0).map { it.index })
    assertEquals(listOf(3), dataSource.getPokemonListOfPage(page = 1).map { it.index })
  }

  /**
   * The Room query orders by the Pokédex index. `INSERT OR REPLACE` moves a replaced row to the
   * end of SQLite's natural scan order while `LinkedHashMap.put` keeps its position, so without an
   * explicit sort on both sides the two implementations disagree and the grid reshuffles.
   */
  @Test
  fun `results are ordered by pokedex index rather than insertion order`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonList(listOf(pokemon(150), pokemon(1), pokemon(25)))

    assertEquals(listOf(1, 25, 150), dataSource.getPokemonList(page = 0).map { it.index })
  }

  @Test
  fun `re-inserting a pokemon replaces it without changing its position`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonList(listOf(pokemon(1), pokemon(2), pokemon(3)))
    dataSource.insertPokemonList(listOf(pokemon(2)))

    assertEquals(listOf(1, 2, 3), dataSource.getPokemonList(page = 0).map { it.index })
    assertEquals(3, dataSource.getPokemonList(page = 0).size)
  }

  @Test
  fun `stored names keep the capitalized display form`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonList(listOf(pokemon(1, name = "bulbasaur")))

    assertEquals("Bulbasaur", dataSource.getPokemonList(page = 0).single().name)
  }

  @Test
  fun `pokemon info lookup ignores case like the COLLATE NOCASE query`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonInfo(pokemonInfo(name = "Bulbasaur"))

    assertNotNull(dataSource.getPokemonInfo("bulbasaur"))
    assertNotNull(dataSource.getPokemonInfo("BULBASAUR"))
    assertNull(dataSource.getPokemonInfo("ivysaur"))
  }

  @Test
  fun `inserting pokemon info twice replaces rather than duplicates`() = runTest {
    val dataSource = InMemoryPokemonLocalDataSource()
    dataSource.insertPokemonInfo(pokemonInfo(name = "Bulbasaur", height = 7))
    dataSource.insertPokemonInfo(pokemonInfo(name = "Bulbasaur", height = 9))

    assertEquals(9, dataSource.getPokemonInfo("Bulbasaur")?.height)
  }

  private fun pokemon(index: Int, page: Int = 0, name: String = "p$index") = Pokemon(
    page = page,
    nameField = name,
    url = "https://pokeapi.co/api/v2/pokemon/$index/",
  )

  private fun pokemonInfo(name: String, height: Int = 7) =
    com.skydoves.pokedex.kmp.core.model.PokemonInfo(
      id = 1,
      name = name,
      height = height,
      weight = 69,
      experience = 64,
      types = emptyList(),
      stats = emptyList(),
    )
}
