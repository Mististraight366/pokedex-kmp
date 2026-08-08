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

package com.skydoves.pokedex.kmp.app.uicomponents.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads a Pokémon's official artwork and reports the colours found in it.
 *
 * The colours come out of [extractArtworkColors] rather than a palette plugin, so Android, iOS,
 * desktop, and the browser all tint the same sprite the same way.
 *
 * The [needsExtraction] latch matters more than it looks. `CoilImageState.Success.imageBitmap` is a
 * `@Composable` getter that decodes a fresh [androidx.compose.ui.graphics.ImageBitmap] on every
 * call and does not remember it. Keying an effect on that value restarts the effect on every
 * composition, the effect writes state, the write invalidates this composable, and the result is a
 * loop that re-decodes roughly a megabyte per card per frame and never lets the composition go
 * idle. Reading the getter only while extraction is still outstanding breaks that cycle.
 */
@Composable
public fun PokemonArtwork(
  imageUrl: String,
  modifier: Modifier = Modifier,
  onColorsExtracted: (ArtworkColors) -> Unit = {},
) {
  val currentOnColorsExtracted by rememberUpdatedState(onColorsExtracted)
  val isPreview = LocalInspectionMode.current
  var needsExtraction by remember(imageUrl) { mutableStateOf(!isPreview) }

  // Replay whatever the grid already worked out for this sprite, so a screen that opens onto an
  // already seen Pokémon is tinted on its very first frame.
  LaunchedEffect(imageUrl) {
    ArtworkColorCache[imageUrl]?.let {
      currentOnColorsExtracted(it)
      needsExtraction = false
    }
  }

  CoilImage(
    modifier = modifier,
    imageModel = { imageUrl },
    imageOptions = ImageOptions(contentScale = ContentScale.Inside),
    success = { state, painter ->
      Image(
        painter = painter,
        contentDescription = null,
        // Not `matchParentSize()`: while a shared element is in flight it is drawn in the
        // transition overlay, where there is no parent to match and the image would collapse.
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Inside,
      )

      if (needsExtraction) {
        val decoded = state.imageBitmap
        LaunchedEffect(decoded) {
          if (decoded != null) {
            val colors = withContext(Dispatchers.Default) { decoded.extractArtworkColors() }
            ArtworkColorCache.put(imageUrl, colors)
            currentOnColorsExtracted(colors)
            needsExtraction = false
          }
        }
      }
    },
    failure = {
      // Without this the card is an unexplained blank rectangle when a sprite 404s or the device
      // is offline.
      PokemonArtworkFallback(modifier = Modifier.fillMaxSize())
    },
  )
}
