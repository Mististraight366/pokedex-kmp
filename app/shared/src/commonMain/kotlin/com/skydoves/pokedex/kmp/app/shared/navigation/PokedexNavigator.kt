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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

/**
 * Navigation as a plain interface, so a screen can be driven from a test or a preview without a
 * NavHost anywhere in sight.
 */
public interface PokedexNavigator {
  public fun navigate(screen: PokedexScreen)
  public fun navigateUp(): Boolean
}

public class NavHostPokedexNavigator(private val navController: NavHostController) :
  PokedexNavigator {

  override fun navigate(screen: PokedexScreen) {
    navController.navigate(screen)
  }

  override fun navigateUp(): Boolean = navController.popBackStack()
}

/** A navigator that records where it was asked to go. Previews and UI tests bind this one. */
public class RecordingPokedexNavigator : PokedexNavigator {

  public val navigated: MutableList<PokedexScreen> = mutableListOf()
  public var upCount: Int = 0
    private set

  override fun navigate(screen: PokedexScreen) {
    navigated += screen
  }

  override fun navigateUp(): Boolean {
    upCount++
    return true
  }
}

public val LocalComposeNavigator: ProvidableCompositionLocal<PokedexNavigator> =
  compositionLocalOf { RecordingPokedexNavigator() }

public val currentComposeNavigator: PokedexNavigator
  @Composable
  @ReadOnlyComposable
  get() = LocalComposeNavigator.current
