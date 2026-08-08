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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.skydoves.pokedex.kmp.app.shared.details.PokedexDetails
import com.skydoves.pokedex.kmp.app.shared.home.PokedexHome
import com.skydoves.pokedex.kmp.app.shared.settings.PokedexSettings
import com.skydoves.pokedex.kmp.app.uicomponents.component.LocalNavAnimatedVisibilityScope
import com.skydoves.pokedex.kmp.app.uicomponents.component.LocalSharedTransitionScope

/**
 * The whole graph sits inside one [SharedTransitionLayout], which is what lets a grid card and the
 * details header be treated as the same element across a destination change. Each entry publishes
 * its own [androidx.compose.animation.AnimatedVisibilityScope], so a screen can opt into the
 * transition without the nav host knowing which of its children do.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
public fun PokedexNavHost() {
  val navController = rememberNavController()
  val navigator = remember(navController) { NavHostPokedexNavigator(navController) }

  CompositionLocalProvider(LocalComposeNavigator provides navigator) {
    SharedTransitionLayout {
      CompositionLocalProvider(LocalSharedTransitionScope provides this) {
        NavHost(
          navController = navController,
          startDestination = PokedexScreen.Home,
          // A short cross fade only. The shared bounds carries the motion, and a slide on top of
          // it would pull the card away from the header it is supposed to be growing into.
          enterTransition = { fadeIn(tween(NAV_FADE_MILLIS)) },
          exitTransition = { fadeOut(tween(NAV_FADE_MILLIS)) },
          popEnterTransition = { fadeIn(tween(NAV_FADE_MILLIS)) },
          popExitTransition = { fadeOut(tween(NAV_FADE_MILLIS)) },
        ) {
          composable<PokedexScreen.Home> {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
              PokedexHome()
            }
          }

          composable<PokedexScreen.Details> { backStackEntry ->
            val details = backStackEntry.toRoute<PokedexScreen.Details>()
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
              PokedexDetails(pokemon = details.toPokemon())
            }
          }

          // A dialog destination, not a full screen: it keeps Home rendered underneath and gives
          // the card a scrim and a tap-outside dismiss for free.
          dialog<PokedexScreen.Settings> {
            PokedexSettings()
          }
        }
      }
    }
  }
}

private const val NAV_FADE_MILLIS = 380
