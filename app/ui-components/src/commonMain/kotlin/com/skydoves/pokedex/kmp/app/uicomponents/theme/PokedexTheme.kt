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

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LocalColors = compositionLocalOf<PokedexColors> {
  error("No PokedexColors provided, wrap your content in PokedexTheme")
}

private val LocalBackgroundTheme = staticCompositionLocalOf<PokedexBackground> {
  error("No PokedexBackground provided, wrap your content in PokedexTheme")
}

@androidx.compose.runtime.Immutable
public data class PokedexBackground(
  val color: Color = Color.Unspecified,
  val tonalElevation: Dp = Dp.Unspecified,
)

public fun defaultBackground(darkTheme: Boolean): PokedexBackground = PokedexBackground(
  color = if (darkTheme) defaultDarkColors().background else defaultLightColors().background,
  tonalElevation = 0.dp,
)

/**
 * The app theme, deliberately not a `MaterialTheme`.
 *
 * Pokedex uses a fixed palette and sizes its own text at every call site, so wiring a Material
 * colour scheme in would add a layer that nothing reads.
 */
@Composable
public fun PokedexTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  colors: PokedexColors = if (darkTheme) defaultDarkColors() else defaultLightColors(),
  background: PokedexBackground = defaultBackground(darkTheme),
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalColors provides colors,
    LocalBackgroundTheme provides background,
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(background.color)
        .enableTestTagsAsResourceId(),
    ) {
      content()
    }
  }
}

public object PokedexTheme {

  public val colors: PokedexColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

  public val background: PokedexBackground
    @Composable
    @ReadOnlyComposable
    get() = LocalBackgroundTheme.current
}
