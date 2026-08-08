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
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PokemonInfoTest {

  @Test
  fun `getIdString pads the pokedex index to three digits`() {
    assertEquals("#001", bulbasaur.copy(id = 1).getIdString())
    assertEquals("#025", bulbasaur.copy(id = 25).getIdString())
    assertEquals("#150", bulbasaur.copy(id = 150).getIdString())
    assertEquals("#1025", bulbasaur.copy(id = 1025).getIdString())
  }

  @Test
  fun `getWeightString converts hectograms to one fraction digit kilograms`() {
    assertEquals("6.9 KG", bulbasaur.getWeightString())
    assertEquals("0.1 KG", bulbasaur.copy(weight = 1).getWeightString())
    assertEquals("100.0 KG", bulbasaur.copy(weight = 1000).getWeightString())
  }

  @Test
  fun `getHeightString converts decimetres to one fraction digit metres`() {
    assertEquals("0.7 M", bulbasaur.getHeightString())
    assertEquals("2.0 M", bulbasaur.copy(height = 20).getHeightString())
  }

  @Test
  fun `stat accessors read the matching base stat`() {
    assertEquals(45, bulbasaur.hp)
    assertEquals(49, bulbasaur.attack)
    assertEquals(49, bulbasaur.defense)
    assertEquals(45, bulbasaur.speed)
  }

  @Test
  fun `stat accessors fall back to zero when the api omits the stat`() {
    val empty = bulbasaur.copy(stats = emptyList())

    assertEquals(0, empty.hp)
    assertEquals(0, empty.attack)
    assertEquals(0, empty.defense)
    assertEquals(0, empty.speed)
  }

  @Test
  fun `stat label strings render against the maximum`() {
    assertEquals(" 45/300", bulbasaur.getHpString())
    assertEquals(" 49/300", bulbasaur.getAttackString())
    assertEquals(" 49/300", bulbasaur.getDefenseString())
    assertEquals(" 45/300", bulbasaur.getSpeedString())
  }

  @Test
  fun `exp is derived from the id so it is identical on every platform and launch`() {
    // A random default would have put a nondeterministic value into equals() and into the
    // serial descriptor; deriving it from the id keeps the bar stable across restarts.
    assertEquals(bulbasaur.exp, bulbasaur.exp)
    assertEquals(bulbasaur.exp, bulbasaur.copy(name = "renamed").exp)
    assertEquals(" ${bulbasaur.exp}/1000", bulbasaur.getExpString())
    assertTrue(bulbasaur.exp in 0 until PokemonInfo.MAX_EXP)
    assertNotEquals(bulbasaur.exp, bulbasaur.copy(id = 2).exp)
  }

  @Test
  fun `two instances decoded from the same payload are equal`() {
    val first = json.decodeFromString<PokemonInfo>(BULBASAUR_JSON)
    val second = json.decodeFromString<PokemonInfo>(BULBASAUR_JSON)

    assertEquals(first, second)
    assertEquals(first.hashCode(), second.hashCode())
  }

  @Test
  fun `deserializes the pokeapi payload while ignoring unmodelled fields`() {
    val info = json.decodeFromString<PokemonInfo>(BULBASAUR_JSON)

    assertEquals(1, info.id)
    assertEquals("bulbasaur", info.name)
    assertEquals(7, info.height)
    assertEquals(69, info.weight)
    assertEquals(64, info.experience)
    assertEquals(listOf("grass", "poison"), info.types.map { it.type.name })
    assertEquals(45, info.hp)
  }

  private companion object {
    val json = Json { ignoreUnknownKeys = true }

    val bulbasaur = PokemonInfo(
      id = 1,
      name = "bulbasaur",
      height = 7,
      weight = 69,
      experience = 64,
      types = listOf(
        PokemonInfo.TypeResponse(slot = 1, type = PokemonInfo.Type("grass")),
        PokemonInfo.TypeResponse(slot = 2, type = PokemonInfo.Type("poison")),
      ),
      stats = listOf(
        PokemonInfo.StatsResponse(45, 0, PokemonInfo.Stat("hp")),
        PokemonInfo.StatsResponse(49, 0, PokemonInfo.Stat("attack")),
        PokemonInfo.StatsResponse(49, 0, PokemonInfo.Stat("defense")),
        PokemonInfo.StatsResponse(45, 0, PokemonInfo.Stat("speed")),
      ),
    )

    val BULBASAUR_JSON = """
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
    """.trimIndent()
  }
}
