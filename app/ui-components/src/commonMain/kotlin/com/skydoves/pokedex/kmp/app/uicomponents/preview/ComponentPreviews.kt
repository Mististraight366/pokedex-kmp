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

package com.skydoves.pokedex.kmp.app.uicomponents.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexAppBar
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexCircularProgress
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexErrorMessage
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexProgressBar
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexText
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme

@Preview
@Composable
internal fun PokedexAppBarPreview() {
  PokedexPreviewTheme(padded = false) {
    PokedexAppBar()
  }
}

@Preview
@Composable
internal fun PokedexTextPreview() {
  PokedexPreviewTheme {
    PokedexText(
      text = "Bulbasaur",
      color = PokedexTheme.colors.black,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
    )
  }
}

@Preview
@Composable
internal fun PokedexCircularProgressPreview() {
  PokedexPreviewTheme(padded = false) {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
      PokedexCircularProgress()
    }
  }
}

@Preview
@Composable
internal fun PokedexProgressBarPreview() {
  PokedexPreviewTheme {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      PokedexProgressBar(progress = 0.15f, color = PokedexTheme.colors.primary, label = " 45/300")
      PokedexProgressBar(progress = 0.55f, color = PokedexTheme.colors.orange, label = " 165/300")
      PokedexProgressBar(progress = 0.96f, color = PokedexTheme.colors.green, label = " 965/1000")
    }
  }
}

@Preview
@Composable
internal fun PokedexErrorMessagePreview() {
  PokedexPreviewTheme {
    PokedexErrorMessage(
      message = "Unable to resolve host \"pokeapi.co\"",
      onRetry = {},
    )
  }
}

@Preview
@Composable
internal fun PokedexColorsDarkPreview() {
  PokedexPreviewTheme(darkTheme = true) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      PokedexText(
        text = "Bulbasaur",
        color = PokedexTheme.colors.black,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
      )
      PokedexProgressBar(progress = 0.55f, color = PokedexTheme.colors.blue, label = " 165/300")
    }
  }
}
