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

package com.skydoves.pokedex.kmp.core.network.service

import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import com.skydoves.pokedex.kmp.core.network.model.PokemonResponse
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ktor.getApiResponse
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter

/**
 * The two PokeAPI endpoints this app consumes. Both return a Sandwich [ApiResponse] rather than a
 * bare payload, so a caller sees success, an HTTP error, and a transport exception as three
 * distinct branches instead of a thrown exception.
 */
public interface PokedexService {

  public suspend fun fetchPokemonList(
    limit: Int = 20,
    offset: Int = 0,
  ): ApiResponse<PokemonResponse>

  public suspend fun fetchPokemonInfo(name: String): ApiResponse<PokemonInfo>
}

@Inject
@ContributesBinding(dev.zacsweers.metro.AppScope::class)
public class KtorPokedexService(private val httpClient: HttpClient) : PokedexService {

  override suspend fun fetchPokemonList(limit: Int, offset: Int): ApiResponse<PokemonResponse> =
    httpClient.getApiResponse("pokemon") {
      parameter("limit", limit)
      parameter("offset", offset)
    }

  override suspend fun fetchPokemonInfo(name: String): ApiResponse<PokemonInfo> =
    httpClient.getApiResponse("pokemon/${name.lowercase()}")
}
