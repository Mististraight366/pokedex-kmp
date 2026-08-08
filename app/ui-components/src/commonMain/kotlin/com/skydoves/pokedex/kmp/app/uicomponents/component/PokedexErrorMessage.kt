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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.Res
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.retry
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.something_went_wrong
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import org.jetbrains.compose.resources.stringResource

/**
 * Shown when a request fails. It always offers a retry, because the alternative the app had before
 * was an indistinguishable spinner that never resolved.
 */
@Composable
public fun PokedexErrorMessage(
  message: String?,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .background(PokedexTheme.colors.backgroundLight, RoundedCornerShape(16.dp))
      .padding(horizontal = 24.dp, vertical = 16.dp)
      .testTag("PokedexError"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    PokedexText(
      text = stringResource(Res.string.something_went_wrong),
      color = PokedexTheme.colors.black,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      fontSize = 16.sp,
    )

    if (!message.isNullOrBlank()) {
      PokedexText(
        text = message,
        previewText = "",
        color = PokedexTheme.colors.white56,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        maxLines = 3,
      )
    }

    TextButton(onClick = onRetry) {
      PokedexText(
        text = stringResource(Res.string.retry),
        color = PokedexTheme.colors.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
      )
    }
  }
}
