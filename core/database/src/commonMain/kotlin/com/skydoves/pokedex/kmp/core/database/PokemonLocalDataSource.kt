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

/**
 * The offline cache the repositories read through, expressed without a word about SQLite.
 *
 * Room backs this on Android, iOS, and desktop. The browser has no native SQLite driver to bind to,
 * so wasm binds the in-memory implementation instead and the repositories never notice.
 */
public interface PokemonLocalDataSource {

  /** Every Pokémon cached up to and including [page], which is what the grid renders. */
  public suspend fun getPokemonList(page: Int): List<Pokemon>

  /** Only the page that was requested, used to decide whether a network call is needed. */
  public suspend fun getPokemonListOfPage(page: Int): List<Pokemon>

  public suspend fun insertPokemonList(pokemonList: List<Pokemon>)

  public suspend fun getPokemonInfo(name: String): PokemonInfo?

  public suspend fun insertPokemonInfo(pokemonInfo: PokemonInfo)
}
