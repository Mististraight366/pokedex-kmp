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

package com.skydoves.pokedex.kmp.app.shared.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.skydoves.pokedex.kmp.app.uicomponents.component.ArtworkColors
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme

/** Tints a card with the dominant colour of the artwork it just finished loading. */
@Composable
internal fun ArtworkColors.cardBackgroundColor(): State<Color> {
  val defaultBackground = PokedexTheme.colors.background
  return remember(this, defaultBackground) {
    derivedStateOf { dominant ?: defaultBackground }
  }
}
