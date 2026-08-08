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

/**
 * Sample data for previews, screenshot goldens, and tests.
 *
 * It lives in the model module rather than in `core:test` on purpose. Previews are main source set
 * code, so whatever they render ships in the app; this is inert data, whereas `core:test` also
 * holds fake repository implementations that must never reach a real dependency graph.
 */
public object PokemonSamples {

  public val bulbasaur: Pokemon = Pokemon(
    page = 0,
    nameField = "bulbasaur",
    url = "https://pokeapi.co/api/v2/pokemon/1/",
  )

  public val pokemons: List<Pokemon> = listOf(
    "bulbasaur" to 1,
    "charmander" to 4,
    "squirtle" to 7,
    "pikachu" to 25,
    "gengar" to 94,
    "eevee" to 133,
    "snorlax" to 143,
    "mewtwo" to 150,
  ).map { (name, index) ->
    Pokemon(page = 0, nameField = name, url = "https://pokeapi.co/api/v2/pokemon/$index/")
  }

  public val bulbasaurInfo: PokemonInfo = PokemonInfo(
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
}
