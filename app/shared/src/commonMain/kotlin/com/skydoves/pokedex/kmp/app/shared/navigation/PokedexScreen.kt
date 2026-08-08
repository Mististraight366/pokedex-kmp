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

package com.skydoves.pokedex.kmp.app.shared.navigation

import com.github.skydoves.navgraph.annotations.NavGraphRoot
import com.skydoves.pokedex.kmp.core.model.Pokemon
import kotlinx.serialization.Serializable

/**
 * The three destinations, as serializable routes.
 *
 * [Details] carries the two fields it needs rather than a whole [Pokemon], so the route stays a
 * flat set of primitives that every platform's back stack can serialize without a custom NavType.
 */
public sealed interface PokedexScreen {

  @NavGraphRoot
  @Serializable
  public data object Home : PokedexScreen

  @Serializable
  public data class Details(val name: String, val url: String) : PokedexScreen {
    public fun toPokemon(): Pokemon = Pokemon(nameField = name, url = url)
  }

  @Serializable
  public data object Settings : PokedexScreen
}

public fun Pokemon.toDetailsRoute(): PokedexScreen.Details =
  PokedexScreen.Details(name = nameField, url = url)
