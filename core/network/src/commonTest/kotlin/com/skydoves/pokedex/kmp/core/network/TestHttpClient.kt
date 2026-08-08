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

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Captures the requests the client issued so a test can assert on the built URL. */
class RequestRecorder {
  private val urls = mutableListOf<String>()

  internal fun record(request: HttpRequestData) {
    urls += request.url.toString()
  }

  fun lastUrl(): String = urls.lastOrNull() ?: error("no request was recorded")
}

fun MockRequestHandleScope.respondJson(body: String): HttpResponseData = respond(
  content = body,
  status = HttpStatusCode.OK,
  headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
)

fun MockRequestHandleScope.respondNotFound(): HttpResponseData =
  respondError(HttpStatusCode.NotFound, """{"detail":"Not found."}""")

/**
 * Builds a client with the same plugin stack as production, but backed by [MockEngine] so the tests
 * exercise the real serialization and the real URL building without touching the network.
 */
fun testHttpClient(
  recorder: RequestRecorder = RequestRecorder(),
  handler: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
): HttpClient = HttpClient(
  MockEngine { request ->
    recorder.record(request)
    handler(request)
  },
) {
  expectSuccess = false
  install(ContentNegotiation) {
    json(Json { ignoreUnknownKeys = true })
  }
  defaultRequest {
    url("https://pokeapi.co/api/v2/")
  }
}

const val POKEMON_LIST_JSON: String = """
{
  "count": 1351,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "previous": null,
  "results": [
    { "name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/" },
    { "name": "ivysaur", "url": "https://pokeapi.co/api/v2/pokemon/2/" }
  ]
}
"""

const val LAST_PAGE_JSON: String = """
{
  "count": 1351,
  "next": null,
  "previous": "https://pokeapi.co/api/v2/pokemon?offset=1320&limit=20",
  "results": [
    { "name": "pecharunt", "url": "https://pokeapi.co/api/v2/pokemon/1025/" }
  ]
}
"""

const val BULBASAUR_JSON: String = """
{
  "id": 1,
  "name": "bulbasaur",
  "height": 7,
  "weight": 69,
  "base_experience": 64,
  "order": 1,
  "is_default": true,
  "types": [
    { "slot": 1, "type": { "name": "grass", "url": "https://pokeapi.co/api/v2/type/12/" } },
    { "slot": 2, "type": { "name": "poison", "url": "https://pokeapi.co/api/v2/type/4/" } }
  ],
  "stats": [
    { "base_stat": 45, "effort": 0, "stat": { "name": "hp", "url": "x" } },
    { "base_stat": 49, "effort": 0, "stat": { "name": "attack", "url": "x" } },
    { "base_stat": 49, "effort": 0, "stat": { "name": "defense", "url": "x" } },
    { "base_stat": 45, "effort": 0, "stat": { "name": "speed", "url": "x" } }
  ]
}
"""
