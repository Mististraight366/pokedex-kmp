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

import com.skydoves.pokedex.kmp.core.network.service.KtorPokedexService
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PokedexServiceTest {

  @Test
  fun `fetchPokemonList maps the paged payload`() = runTest {
    val recorder = RequestRecorder()
    val service = KtorPokedexService(
      testHttpClient(recorder) { respondJson(POKEMON_LIST_JSON) },
    )

    val response = service.fetchPokemonList(limit = 20, offset = 0)

    val success = assertIs<ApiResponse.Success<*>>(response)
    val data = success.data as com.skydoves.pokedex.kmp.core.network.model.PokemonResponse
    assertEquals(1351, data.count)
    assertEquals(2, data.results.size)
    assertEquals("bulbasaur", data.results[0].nameField)
    assertEquals("Bulbasaur", data.results[0].name)
  }

  @Test
  fun `fetchPokemonList sends limit and offset derived from the page`() = runTest {
    val recorder = RequestRecorder()
    val service = KtorPokedexService(testHttpClient(recorder) { respondJson(POKEMON_LIST_JSON) })

    PokedexClient(service).fetchPokemonList(page = 3)

    val url = recorder.lastUrl()
    assertTrue(url.contains("limit=20"), "expected limit=20 in $url")
    assertTrue(url.contains("offset=60"), "expected offset=60 in $url")
  }

  @Test
  fun `fetchPokemonInfo maps the detail payload`() = runTest {
    val recorder = RequestRecorder()
    val service = KtorPokedexService(testHttpClient(recorder) { respondJson(BULBASAUR_JSON) })

    val response = service.fetchPokemonInfo("Bulbasaur")

    val success = assertIs<ApiResponse.Success<*>>(response)
    val info = success.data as com.skydoves.pokedex.kmp.core.model.PokemonInfo
    assertEquals(1, info.id)
    assertEquals("bulbasaur", info.name)
    assertEquals(7, info.height)
    assertEquals(69, info.weight)
    assertEquals(64, info.experience)
    assertTrue(recorder.lastUrl().endsWith("pokemon/bulbasaur"))
  }

  @Test
  fun `a 404 becomes a failure rather than a thrown exception`() = runTest {
    val recorder = RequestRecorder()
    val service = KtorPokedexService(testHttpClient(recorder) { respondNotFound() })

    val response = service.fetchPokemonInfo("missingno")

    assertIs<ApiResponse.Failure<*>>(response)
  }

  @Test
  fun `a transport failure becomes an exception response`() = runTest {
    val recorder = RequestRecorder()
    val service = KtorPokedexService(
      testHttpClient(recorder) { throw kotlinx.io.IOException("offline") },
    )

    val response = service.fetchPokemonInfo("bulbasaur")

    assertIs<ApiResponse.Failure.Exception>(response)
  }
}
