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

import com.russhwolf.settings.Settings
import com.skydoves.pokedex.kmp.core.model.UiTheme
import com.skydoves.pokedex.kmp.core.model.UserData
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The single user preference this app persists.
 *
 * It is stored under the enum name rather than an ordinal, so reordering [UiTheme] later cannot
 * silently repoint an existing install at a different theme. Browser localStorage has no change
 * notification, so the current value is mirrored into a [MutableStateFlow] that every platform
 * observes the same way.
 */
@Inject
@SingleIn(AppScope::class)
public class PreferencesDataSource(private val settings: Settings) {

  private val state = MutableStateFlow(UserData(uiTheme = readUiTheme()))

  public val userData: Flow<UserData> = state.asStateFlow()

  public fun setUiTheme(uiTheme: UiTheme) {
    settings.putString(KEY_UI_THEME, uiTheme.name)
    state.value = UserData(uiTheme = uiTheme)
  }

  private fun readUiTheme(): UiTheme {
    val stored = settings.getStringOrNull(KEY_UI_THEME)
    return UiTheme.entries.firstOrNull { it.name == stored } ?: UiTheme.FOLLOW_SYSTEM
  }

  private companion object {
    const val KEY_UI_THEME = "ui_theme"
  }
}
