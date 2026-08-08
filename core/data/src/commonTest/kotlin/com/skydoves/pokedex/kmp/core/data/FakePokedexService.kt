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

import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import com.skydoves.pokedex.kmp.core.network.model.PokemonResponse
import com.skydoves.pokedex.kmp.core.network.service.PokedexService
import com.skydoves.sandwich.ApiResponse

/**
 * Serves two Pokémon per page from a deterministic generator, so a test can assert on exact names
 * and on how many pages were actually requested.
 */
class FakePokedexService(
  private val lastPage: Int = Int.MAX_VALUE,
  private val failing: Boolean = false,
) : PokedexService {

  var listCallCount: Int = 0
    private set
  var infoCallCount: Int = 0
    private set

  override suspend fun fetchPokemonList(limit: Int, offset: Int): ApiResponse<PokemonResponse> {
    listCallCount++
    if (failing) return ApiResponse.exception(IllegalStateException("offline"))

    val page = offset / limit
    val names = listOf("bulbasaur", "ivysaur", "venusaur", "charmander", "charmeleon", "charizard")
    // Honour the limit the client asked for, so `results.size < PAGING_SIZE` means the same thing
    // here as it does against the real API.
    val results = List(limit) { index ->
      val globalIndex = offset + index
      Pokemon(
        nameField = names.getOrElse(globalIndex) { "pokemon-$globalIndex" },
        url = "https://pokeapi.co/api/v2/pokemon/${globalIndex + 1}/",
      )
    }
    return ApiResponse.of {
      PokemonResponse(
        count = 1351,
        next = if (page >= lastPage) null else "https://pokeapi.co/api/v2/pokemon?offset=x",
        previous = null,
        results = results,
      )
    }
  }

  override suspend fun fetchPokemonInfo(name: String): ApiResponse<PokemonInfo> {
    infoCallCount++
    if (failing) return ApiResponse.exception(IllegalStateException("offline"))

    return ApiResponse.of {
      PokemonInfo(
        id = 1,
        name = name,
        height = 7,
        weight = 69,
        experience = 64,
        types = listOf(PokemonInfo.TypeResponse(1, PokemonInfo.Type("grass"))),
        stats = listOf(PokemonInfo.StatsResponse(45, 0, PokemonInfo.Stat("hp"))),
      )
    }
  }
}
