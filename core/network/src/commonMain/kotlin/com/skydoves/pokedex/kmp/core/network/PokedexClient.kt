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

package com.skydoves.pokedex.kmp.core.network

import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import com.skydoves.pokedex.kmp.core.network.model.PokemonResponse
import com.skydoves.pokedex.kmp.core.network.service.PokedexService
import com.skydoves.sandwich.ApiResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * Translates a zero based page index into the limit and offset pair PokeAPI expects, so nothing
 * above this class has to know the page size.
 */
@Inject
@SingleIn(AppScope::class)
public class PokedexClient(private val pokedexService: PokedexService) {

  public suspend fun fetchPokemonList(page: Int): ApiResponse<PokemonResponse> =
    pokedexService.fetchPokemonList(
      limit = PAGING_SIZE,
      offset = page * PAGING_SIZE,
    )

  public suspend fun fetchPokemonInfo(name: String): ApiResponse<PokemonInfo> =
    pokedexService.fetchPokemonInfo(name = name)

  public companion object {
    public const val PAGING_SIZE: Int = 20
  }
}
