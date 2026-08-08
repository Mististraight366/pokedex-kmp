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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.pokedex.kmp.core.data.repository.UserDataRepository
import com.skydoves.pokedex.kmp.core.model.UiTheme
import com.skydoves.pokedex.kmp.core.model.UserData
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
@ContributesIntoMap(AppScope::class)
@ViewModelKey(SettingsViewModel::class)
public class SettingsViewModel(private val userDataRepository: UserDataRepository) : ViewModel() {

  public val uiState: StateFlow<SettingsUiState> = userDataRepository.userData
    .map<UserData, SettingsUiState> { SettingsUiState.Success(it) }
    .catch { emit(SettingsUiState.Error(it.message)) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = SettingsUiState.Loading,
    )

  public fun setUiTheme(uiTheme: UiTheme) {
    viewModelScope.launch { userDataRepository.setUiTheme(uiTheme) }
  }
}

public sealed interface SettingsUiState {
  public data object Loading : SettingsUiState
  public data class Success(val userData: UserData) : SettingsUiState
  public data class Error(val message: String?) : SettingsUiState
}
