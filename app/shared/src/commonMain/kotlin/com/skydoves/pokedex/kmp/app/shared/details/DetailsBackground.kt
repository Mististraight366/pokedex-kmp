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

package com.skydoves.pokedex.kmp.app.shared.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import com.skydoves.pokedex.kmp.app.uicomponents.component.ArtworkColors
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme

/**
 * Builds the header gradient out of the artwork's own colours: the dominant swatch on top, the
 * light vibrant one underneath. When a swatch is missing the gradient collapses to a flat colour
 * rather than falling back to something unrelated to the sprite.
 */
@Composable
internal fun ArtworkColors.headerBrush(): State<Brush> {
  val defaultBackground = PokedexTheme.colors.background
  return remember(this, defaultBackground) {
    derivedStateOf {
      val top = dominant ?: defaultBackground
      val bottom = lightVibrant ?: top
      Brush.verticalGradient(0f to top, 1f to bottom)
    }
  }
}
