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

package com.skydoves.pokedex.kmp.app.uicomponents.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The full palette, including one colour per Pokémon type.
 *
 * Light and dark are two instances of the same shape rather than two lookups against a resource
 * qualifier, which is what lets the browser and desktop builds theme themselves without an Android
 * resource system underneath.
 */
@Immutable
public data class PokedexColors(
  val primary: Color,
  val background: Color,
  val backgroundLight: Color,
  val backgroundDark: Color,
  val absoluteWhite: Color,
  val absoluteBlack: Color,
  val white: Color,
  val white12: Color,
  val white56: Color,
  val white70: Color,
  val black: Color,
  val gray21: Color,
  val bug: Color,
  val dark: Color,
  val dragon: Color,
  val electric: Color,
  val fairy: Color,
  val fire: Color,
  val fighting: Color,
  val flying: Color,
  val ghost: Color,
  val steel: Color,
  val ice: Color,
  val poison: Color,
  val psychic: Color,
  val rock: Color,
  val water: Color,
  val grass: Color,
  val ground: Color,
  val orange: Color,
  val green: Color,
  val blue: Color,
)

private val PokedexRed = Color(0xFFD53A47)
private val Bug = Color(0xFF179A55)
private val Dark = Color(0xFF040706)
private val Dragon = Color(0xFF378A94)
private val Gray21 = Color(0xFFB1A5A5)
private val Electric = Color(0xFFE0E64B)
private val Fairy = Color(0xFF9E1A44)
private val Fire = Color(0xFFB22328)
private val Fighting = Color(0xFF9F422A)
private val Flying = Color(0xFF90B1C5)
private val Ghost = Color(0xFF363069)
private val Steel = Color(0xFF5C756D)
private val Ice = Color(0xFF7ECFF2)
private val Poison = Color(0xFF642785)
private val Psychic = Color(0xFFAC296B)
private val Rock = Color(0xFF4B190E)
private val Water = Color(0xFF2648DC)
private val Grass = Color(0xFF007C42)
private val Ground = Color(0xFFAD7235)
private val Orange = Color(0xFFFFA726)
private val Green = Color(0xFF388E3C)
private val Blue = Color(0xFF0091EA)

public fun defaultLightColors(): PokedexColors = PokedexColors(
  primary = PokedexRed,
  background = Color(0xFFF4EBF4),
  backgroundLight = Color(0xFFD0C7C7),
  backgroundDark = Color(0xFFFFFFFF),
  absoluteWhite = Color(0xFFFFFFFF),
  absoluteBlack = Color(0xFF000000),
  white = Color(0xFFFFFFFF),
  white12 = Color(0x1F000000),
  white56 = Color(0x8F000000),
  white70 = Color(0xB3000000),
  black = Color(0xFF000000),
  gray21 = Gray21,
  bug = Bug,
  dark = Dark,
  dragon = Dragon,
  electric = Electric,
  fairy = Fairy,
  fire = Fire,
  fighting = Fighting,
  flying = Flying,
  ghost = Ghost,
  steel = Steel,
  ice = Ice,
  poison = Poison,
  psychic = Psychic,
  rock = Rock,
  water = Water,
  grass = Grass,
  ground = Ground,
  orange = Orange,
  green = Green,
  blue = Blue,
)

/**
 * `white` and `black` deliberately swap in dark mode: they name the role a colour plays against the
 * current background, not the ink itself, so a call site never has to branch on the theme.
 */
public fun defaultDarkColors(): PokedexColors = PokedexColors(
  primary = PokedexRed,
  background = Color(0xFF2B292B),
  backgroundLight = Color(0xFF424242),
  backgroundDark = Color(0xFF212121),
  absoluteWhite = Color(0xFFFFFFFF),
  absoluteBlack = Color(0xFF000000),
  white = Color(0xFF000000),
  white12 = Color(0xFFFAFAFA),
  white56 = Color(0x8EFFFFFF),
  white70 = Color(0xB2FFFFFF),
  black = Color(0xFFFFFFFF),
  gray21 = Gray21,
  bug = Bug,
  dark = Dark,
  dragon = Dragon,
  electric = Electric,
  fairy = Fairy,
  fire = Fire,
  fighting = Fighting,
  flying = Flying,
  ghost = Ghost,
  steel = Steel,
  ice = Ice,
  poison = Poison,
  psychic = Psychic,
  rock = Rock,
  water = Water,
  grass = Grass,
  ground = Ground,
  orange = Orange,
  green = Green,
  blue = Blue,
)
