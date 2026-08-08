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

package com.skydoves.pokedex.kmp.core.datastore

import com.russhwolf.settings.MapSettings
import com.skydoves.pokedex.kmp.core.model.UiTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferencesDataSourceTest {

  @Test
  fun `an empty store follows the system theme`() = runTest {
    val dataSource = PreferencesDataSource(MapSettings())

    assertEquals(UiTheme.FOLLOW_SYSTEM, dataSource.userData.first().uiTheme)
  }

  @Test
  fun `setting a theme is published to observers`() = runTest {
    val dataSource = PreferencesDataSource(MapSettings())

    dataSource.setUiTheme(UiTheme.DARK)

    assertEquals(UiTheme.DARK, dataSource.userData.first().uiTheme)
  }

  @Test
  fun `a stored theme survives a new instance over the same store`() = runTest {
    val settings = MapSettings()
    PreferencesDataSource(settings).setUiTheme(UiTheme.LIGHT)

    assertEquals(UiTheme.LIGHT, PreferencesDataSource(settings).userData.first().uiTheme)
  }

  /**
   * The value is persisted by enum name rather than ordinal, so reordering [UiTheme] cannot
   * silently repoint an existing install at a different theme.
   */
  @Test
  fun `the theme is stored under its enum name`() = runTest {
    val settings = MapSettings()

    PreferencesDataSource(settings).setUiTheme(UiTheme.DARK)

    assertEquals("DARK", settings.getStringOrNull("ui_theme"))
  }

  @Test
  fun `an unrecognized stored value falls back to the system theme`() = runTest {
    val settings = MapSettings("ui_theme" to "SEPIA")

    assertEquals(UiTheme.FOLLOW_SYSTEM, PreferencesDataSource(settings).userData.first().uiTheme)
  }
}
