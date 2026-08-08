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
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The cache the web build runs on, and the fake the repository tests run against.
 *
 * It mirrors the Room implementation, including the upsert on primary key and the Pokédex index
 * ordering, so a test that passes here describes the same behaviour the native platforms get.
 * Ordering has to be explicit on both sides: `INSERT OR REPLACE` moves a replaced row to the end of
 * SQLite's natural scan order, while `LinkedHashMap.put` keeps its original position.
 */
public class InMemoryPokemonLocalDataSource : PokemonLocalDataSource {

  private val mutex = Mutex()
  private val pokemonByName = LinkedHashMap<String, Pokemon>()
  private val pokemonInfoById = LinkedHashMap<Int, PokemonInfo>()

  override suspend fun getPokemonList(page: Int): List<Pokemon> = mutex.withLock {
    pokemonByName.values.filter { it.page <= page }.sortedBy { it.index }
  }

  override suspend fun getPokemonListOfPage(page: Int): List<Pokemon> = mutex.withLock {
    pokemonByName.values.filter { it.page == page }.sortedBy { it.index }
  }

  override suspend fun insertPokemonList(pokemonList: List<Pokemon>): Unit = mutex.withLock {
    pokemonList.forEach { pokemon ->
      pokemonByName[pokemon.name] =
        pokemon.copy(nameField = pokemon.name)
    }
  }

  override suspend fun getPokemonInfo(name: String): PokemonInfo? = mutex.withLock {
    pokemonInfoById.values.firstOrNull { it.name.equals(name, ignoreCase = true) }
  }

  override suspend fun insertPokemonInfo(pokemonInfo: PokemonInfo): Unit = mutex.withLock {
    pokemonInfoById[pokemonInfo.id] = pokemonInfo
  }
}
