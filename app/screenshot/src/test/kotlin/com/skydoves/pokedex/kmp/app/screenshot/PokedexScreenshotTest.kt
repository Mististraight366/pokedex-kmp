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

package com.skydoves.pokedex.kmp.app.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexProgressBar
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexText
import com.skydoves.pokedex.kmp.app.uicomponents.preview.PokedexPreviewTheme
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import org.junit.Rule
import org.junit.Test

/**
 * Device-spec goldens for the design system.
 *
 * Scope is deliberately narrow: components that render purely from their inputs.
 *
 * Two things are excluded and covered elsewhere. Anything that loads an image over the network is
 * nondeterministic under an offline renderer. And anything calling `stringResource` throws
 * `MissingResourceException` here, because Compose Multiplatform reads resources from the
 * classpath root as `composeResources/...` while Paparazzi renders inside its own layoutlib
 * classloader that does not carry them. Whole screens, including every string, are captured
 * instead by the HotSwan preview run in CI, which executes in a real app process on a device.
 *
 * Record with `./gradlew :app:screenshot:recordPaparazziDebug`, verify with
 * `./gradlew :app:screenshot:verifyPaparazziDebug`.
 */
class PokedexScreenshotTest {

  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL_6,
    renderingMode = SessionParams.RenderingMode.SHRINK,
    showSystemUi = false,
  )

  @Test
  fun pokedexProgressBars() {
    paparazzi.snapshot {
      PokedexPreviewTheme {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          PokedexProgressBar(
            progress = 0.15f,
            color = PokedexTheme.colors.primary,
            label = " 45/300",
          )
          PokedexProgressBar(
            progress = 0.55f,
            color = PokedexTheme.colors.orange,
            label = " 165/300",
          )
          PokedexProgressBar(
            progress = 0.96f,
            color = PokedexTheme.colors.green,
            label = " 965/1000",
          )
        }
      }
    }
  }

  @Test
  fun pokedexTextLight() {
    paparazzi.snapshot {
      PokedexPreviewTheme(darkTheme = false) {
        PokedexText(
          text = "Bulbasaur",
          color = PokedexTheme.colors.black,
          fontWeight = FontWeight.Bold,
          fontSize = 24.sp,
        )
      }
    }
  }

  @Test
  fun pokedexProgressBarLabelOutsideWhenBarIsShort() {
    // The label flips from inside the filled bar to outside it below a width threshold; that
    // switch is exactly the kind of layout regression a golden is for.
    paparazzi.snapshot {
      PokedexPreviewTheme {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          PokedexProgressBar(progress = 0f, color = PokedexTheme.colors.primary, label = " 0/300")
          PokedexProgressBar(progress = 0.04f, color = PokedexTheme.colors.blue, label = " 12/300")
        }
      }
    }
  }

  @Test
  fun pokedexTextDark() {
    paparazzi.snapshot {
      PokedexPreviewTheme(darkTheme = true) {
        PokedexText(
          text = "Bulbasaur",
          color = PokedexTheme.colors.black,
          fontWeight = FontWeight.Bold,
          fontSize = 24.sp,
        )
      }
    }
  }
}
