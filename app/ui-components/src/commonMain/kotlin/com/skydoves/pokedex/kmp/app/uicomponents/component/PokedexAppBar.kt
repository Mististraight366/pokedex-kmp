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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.Res
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.app_name
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.settings
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PokedexAppBar(modifier: Modifier = Modifier, onActionClick: () -> Unit = {}) {
  TopAppBar(
    modifier = modifier,
    title = {
      Text(
        text = stringResource(Res.string.app_name),
        color = PokedexTheme.colors.absoluteWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
      )
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = PokedexTheme.colors.primary,
    ),
    actions = {
      IconButton(onClick = onActionClick) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = stringResource(Res.string.settings),
          tint = PokedexTheme.colors.absoluteWhite,
        )
      }
    },
  )
}
