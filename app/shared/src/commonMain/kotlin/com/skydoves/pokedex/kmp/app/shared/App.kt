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

package com.skydoves.pokedex.kmp.app.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.skydoves.pokedex.kmp.app.shared.di.AppGraph
import com.skydoves.pokedex.kmp.app.shared.navigation.PokedexNavHost
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import com.skydoves.pokedex.kmp.core.model.UiTheme
import com.skydoves.pokedex.kmp.core.model.UserData
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

/**
 * The single entry point every platform renders. Android, iOS, desktop, and the browser each build
 * their own [AppGraph] and then hand it to this one composable.
 */
@Composable
public fun App(appGraph: AppGraph) {
  val userData by appGraph.userDataRepository.userData
    .collectAsState(initial = UserData(UiTheme.FOLLOW_SYSTEM))

  CompositionLocalProvider(
    LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
  ) {
    PokedexTheme(darkTheme = userData.uiTheme.shouldUseDarkTheme()) {
      PokedexNavHost()
    }
  }
}

@Composable
private fun UiTheme.shouldUseDarkTheme(): Boolean = when (this) {
  UiTheme.FOLLOW_SYSTEM -> isSystemInDarkTheme()
  UiTheme.DARK -> true
  UiTheme.LIGHT -> false
}
