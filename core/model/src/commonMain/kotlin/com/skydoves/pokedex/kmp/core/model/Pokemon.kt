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

package com.skydoves.pokedex.kmp.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
public data class Pokemon(
  val page: Int = 0,
  @SerialName(value = "name")
  val nameField: String,
  @SerialName(value = "url")
  val url: String,
) {

  val name: String
    get() = nameField.replaceFirstChar { it.uppercase() }

  /**
   * The trailing path segment of [url] is the Pokédex index.
   *
   * It is the only ordering the API gives us, and SQLite guarantees nothing about row order without
   * an `ORDER BY`, so this is what the cache sorts on.
   */
  val index: Int
    get() = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0

  /** The official artwork is served from the PokeAPI sprite repository under the Pokédex index. */
  val imageUrl: String
    get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
      "pokemon/other/official-artwork/$index.png"
}
