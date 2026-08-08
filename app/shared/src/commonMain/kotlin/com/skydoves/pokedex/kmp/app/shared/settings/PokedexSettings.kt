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

package com.skydoves.pokedex.kmp.app.shared.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.skydoves.navgraph.annotations.NavDestination
import com.skydoves.pokedex.kmp.app.shared.navigation.PokedexScreen
import com.skydoves.pokedex.kmp.app.shared.navigation.currentComposeNavigator
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexCircularProgress
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexText
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.Res
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.ok
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.settings
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.theme
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.theme_dark
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.theme_follow_system
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.theme_light
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import com.skydoves.pokedex.kmp.core.model.UiTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@NavDestination(route = PokedexScreen.Settings::class)
@Composable
public fun PokedexSettings(settingsViewModel: SettingsViewModel = metroViewModel()) {
  val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
  val composeNavigator = currentComposeNavigator

  SettingsDialog(
    uiState = uiState,
    onChangeUiTheme = settingsViewModel::setUiTheme,
    onDismiss = { composeNavigator.navigateUp() },
  )
}

@Composable
internal fun SettingsDialog(
  uiState: SettingsUiState,
  onChangeUiTheme: (UiTheme) -> Unit,
  onDismiss: () -> Unit,
) {
  Box(
    modifier = Modifier
      .padding(40.dp)
      .background(PokedexTheme.colors.background, RoundedCornerShape(32.dp))
      .testTag("PokedexSettings"),
  ) {
    Column(
      modifier = Modifier.padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
    ) {
      PokedexText(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(Res.string.settings),
        color = PokedexTheme.colors.black,
        textAlign = TextAlign.Start,
        fontSize = 22.sp,
      )

      HorizontalDivider()

      when (uiState) {
        is SettingsUiState.Success -> ThemeSection(
          uiTheme = uiState.userData.uiTheme,
          onChangeUiTheme = onChangeUiTheme,
        )

        SettingsUiState.Loading -> Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
          PokedexCircularProgress()
        }

        is SettingsUiState.Error -> PokedexText(
          modifier = Modifier.fillMaxWidth(),
          text = uiState.message.orEmpty(),
          color = PokedexTheme.colors.primary,
          textAlign = TextAlign.Start,
          fontSize = 14.sp,
        )
      }

      HorizontalDivider()

      TextButton(modifier = Modifier.align(Alignment.End), onClick = onDismiss) {
        PokedexText(
          text = stringResource(Res.string.ok),
          color = PokedexTheme.colors.primary,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
        )
      }
    }
  }
}

@Composable
private fun ThemeSection(uiTheme: UiTheme, onChangeUiTheme: (UiTheme) -> Unit) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
  ) {
    PokedexText(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(Res.string.theme),
      color = PokedexTheme.colors.black,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Start,
      fontSize = 16.sp,
    )

    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).selectableGroup(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      UiTheme.entries.forEach { entry ->
        Row(
          modifier = Modifier.selectable(
            selected = uiTheme == entry,
            role = Role.RadioButton,
            onClick = { onChangeUiTheme(entry) },
          ),
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          RadioButton(
            selected = uiTheme == entry,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = PokedexTheme.colors.primary),
          )
          PokedexText(
            text = stringResource(entry.labelResource()),
            color = PokedexTheme.colors.black,
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
          )
        }
      }
    }
  }
}

private fun UiTheme.labelResource() = when (this) {
  UiTheme.FOLLOW_SYSTEM -> Res.string.theme_follow_system
  UiTheme.DARK -> Res.string.theme_dark
  UiTheme.LIGHT -> Res.string.theme_light
}
