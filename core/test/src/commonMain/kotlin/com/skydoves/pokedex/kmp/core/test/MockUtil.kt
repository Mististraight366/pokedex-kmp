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

package com.skydoves.pokedex.kmp.core.test

import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import com.skydoves.pokedex.kmp.core.model.PokemonSamples

/**
 * The test facing name for [PokemonSamples]. Tests and previews render the same fixtures, so a
 * golden image and a unit test can never disagree about what "Bulbasaur" looks like.
 */
public object MockUtil {

  public fun mockPokemon(): Pokemon = PokemonSamples.bulbasaur

  public fun mockPokemonList(): List<Pokemon> = PokemonSamples.pokemons

  public fun mockPokemonInfo(): PokemonInfo = PokemonSamples.bulbasaurInfo
}
