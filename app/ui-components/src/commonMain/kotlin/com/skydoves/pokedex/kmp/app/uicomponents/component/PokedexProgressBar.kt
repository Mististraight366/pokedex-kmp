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

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme

/**
 * A stat bar that animates from empty to [progress] and prints its label inside the filled part
 * when there is room, outside it when there is not.
 *
 * The original read `LocalConfiguration.screenWidthDp`, which only exists on Android.
 * [BoxWithConstraints] gives the same measurement on every platform and, unlike a screen width, it
 * reports the width this bar actually got rather than the width of the window.
 */
@Composable
public fun PokedexProgressBar(
  progress: Float,
  color: Color,
  label: String,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .height(BAR_HEIGHT)
      .background(PokedexTheme.colors.absoluteWhite, RoundedCornerShape(64.dp))
      .clip(RoundedCornerShape(64.dp)),
  ) {
    // animateFloatAsState seeds its Animatable with the first target it sees. The bar is only
    // composed once the stats have loaded, so a target derived from `progress` is already final on
    // the first pass and nothing would ever animate. Starting from zero on the next frame is what
    // makes the fill visible.
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val animatedProgress by animateFloatAsState(
      targetValue = if (started) progress.coerceIn(0f, 1f) else 0f,
      animationSpec = tween(durationMillis = 950, easing = LinearOutSlowInEasing),
      label = "progress",
    )
    val filledWidth = maxWidth * animatedProgress
    val labelFitsInside = filledWidth > (label.length * 7).dp + 32.dp

    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .width(filledWidth)
        .height(BAR_HEIGHT)
        .background(color, RoundedCornerShape(64.dp)),
    )

    Text(
      modifier = if (labelFitsInside) {
        Modifier.align(Alignment.CenterStart).width(filledWidth).padding(end = 12.dp)
      } else {
        Modifier.align(Alignment.CenterStart).padding(start = filledWidth + 8.dp)
      },
      text = label,
      color = if (labelFitsInside) {
        PokedexTheme.colors.absoluteWhite
      } else {
        PokedexTheme.colors.absoluteBlack
      },
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      textAlign = if (labelFitsInside) androidx.compose.ui.text.style.TextAlign.End else null,
      maxLines = 1,
    )
  }
}

private val BAR_HEIGHT = 18.dp
