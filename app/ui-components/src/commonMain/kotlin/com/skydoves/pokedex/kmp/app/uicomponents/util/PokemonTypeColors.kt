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

package com.skydoves.pokedex.kmp.app.uicomponents.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme

/** Maps a PokeAPI type name onto the chip colour the details screen paints it with. */
@Composable
@ReadOnlyComposable
public fun getPokemonTypeColor(type: String): Color = when (type.lowercase()) {
  "bug" -> PokedexTheme.colors.bug
  "dark" -> PokedexTheme.colors.dark
  "dragon" -> PokedexTheme.colors.dragon
  "electric" -> PokedexTheme.colors.electric
  "fairy" -> PokedexTheme.colors.fairy
  "fighting" -> PokedexTheme.colors.fighting
  "fire" -> PokedexTheme.colors.fire
  "flying" -> PokedexTheme.colors.flying
  "ghost" -> PokedexTheme.colors.ghost
  "grass" -> PokedexTheme.colors.grass
  "ground" -> PokedexTheme.colors.ground
  "ice" -> PokedexTheme.colors.ice
  "poison" -> PokedexTheme.colors.poison
  "psychic" -> PokedexTheme.colors.psychic
  "rock" -> PokedexTheme.colors.rock
  "steel" -> PokedexTheme.colors.steel
  "water" -> PokedexTheme.colors.water
  else -> PokedexTheme.colors.gray21
}
