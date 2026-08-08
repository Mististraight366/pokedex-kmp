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

package com.skydoves.pokedex.kmp.core.data.repository

import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.model.PokemonInfo

/**
 * The outcome of asking for one page of the Pokédex.
 *
 * This used to be reported through `onStart`/`onComplete`/`onError` callbacks alongside a
 * `Flow<List<Pokemon>>`. That could not express failure: `onError` fired from inside the flow and
 * `onComplete` fired from `onCompletion` immediately after, so the caller's error state was always
 * overwritten by the completion state one instant later and the UI could never show it. Returning a
 * single value makes success and failure mutually exclusive by construction.
 */
public sealed interface PokemonPageResult {

  public data class Success(val pokemons: List<Pokemon>, val isLastPage: Boolean) :
    PokemonPageResult

  public data class Failure(val message: String?) : PokemonPageResult
}

/** The outcome of asking for one Pokémon's detail payload. */
public sealed interface PokemonInfoResult {

  public data class Success(val pokemonInfo: PokemonInfo) : PokemonInfoResult

  public data class Failure(val message: String?) : PokemonInfoResult
}
