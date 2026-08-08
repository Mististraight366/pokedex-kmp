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

package com.skydoves.pokedex.kmp.core.network.di

import com.skydoves.pokedex.kmp.core.network.httpClientEngineFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@BindingContainer
@ContributesTo(AppScope::class)
public object NetworkBindings {

  /**
   * PokeAPI returns far more fields than the models declare, so unknown keys are dropped instead of
   * failing the whole response.
   */
  @Provides
  @SingleIn(AppScope::class)
  public fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }

  @Provides
  @SingleIn(AppScope::class)
  public fun provideHttpClient(json: Json): HttpClient = HttpClient(httpClientEngineFactory()) {
    expectSuccess = false

    install(ContentNegotiation) {
      json(json)
    }

    install(Logging) {
      level = LogLevel.INFO
    }

    defaultRequest {
      url(BASE_URL)
    }
  }

  public const val BASE_URL: String = "https://pokeapi.co/api/v2/"
}
