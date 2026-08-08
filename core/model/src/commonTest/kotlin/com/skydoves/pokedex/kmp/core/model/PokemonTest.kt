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

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PokemonTest {

  @Test
  fun `name capitalizes the first character of the raw api name`() {
    val pokemon = Pokemon(nameField = "bulbasaur", url = POKEMON_URL)

    assertEquals("Bulbasaur", pokemon.name)
  }

  @Test
  fun `name leaves an already capitalized value untouched`() {
    val pokemon = Pokemon(nameField = "Bulbasaur", url = POKEMON_URL)

    assertEquals("Bulbasaur", pokemon.name)
  }

  @Test
  fun `imageUrl resolves the official artwork from the trailing index of the api url`() {
    val pokemon = Pokemon(nameField = "bulbasaur", url = POKEMON_URL)

    assertEquals(
      "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
        "pokemon/other/official-artwork/1.png",
      pokemon.imageUrl,
    )
  }

  @Test
  fun `imageUrl handles a url without a trailing slash`() {
    val pokemon = Pokemon(nameField = "mewtwo", url = "https://pokeapi.co/api/v2/pokemon/150")

    assertEquals(
      "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
        "pokemon/other/official-artwork/150.png",
      pokemon.imageUrl,
    )
  }

  @Test
  fun `deserializes the api field names`() {
    val json = """{"name":"ivysaur","url":"https://pokeapi.co/api/v2/pokemon/2/"}"""

    val pokemon = Json.decodeFromString<Pokemon>(json)

    assertEquals("ivysaur", pokemon.nameField)
    assertEquals("Ivysaur", pokemon.name)
    assertEquals(0, pokemon.page)
  }

  private companion object {
    const val POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/1/"
  }
}
