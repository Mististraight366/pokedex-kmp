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

package com.skydoves.pokedex.kmp.app.shared.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavPreview
import com.skydoves.pokedex.kmp.app.shared.details.DetailsContent
import com.skydoves.pokedex.kmp.app.shared.details.DetailsUiState
import com.skydoves.pokedex.kmp.app.shared.home.HomeContent
import com.skydoves.pokedex.kmp.app.shared.home.HomeUiState
import com.skydoves.pokedex.kmp.app.shared.navigation.PokedexScreen
import com.skydoves.pokedex.kmp.app.shared.settings.SettingsDialog
import com.skydoves.pokedex.kmp.app.shared.settings.SettingsUiState
import com.skydoves.pokedex.kmp.app.uicomponents.preview.PokedexPreviewTheme
import com.skydoves.pokedex.kmp.core.model.PokemonSamples
import com.skydoves.pokedex.kmp.core.model.UiTheme
import com.skydoves.pokedex.kmp.core.model.UserData

/**
 * Screen previews built from [PokemonSamples] rather than from a graph.
 *
 * They render the stateless `*Content` composables, not the ViewModel backed entry points, so the
 * preview renderer, the HotSwan capture, and the navigation graph thumbnails never need Metro, a
 * network, or a database.
 */
@NavPreview(route = PokedexScreen.Home::class, primary = true)
@Preview
@Composable
internal fun HomeContentPreview() {
  PokedexPreviewTheme(padded = false) {
    HomeContent(
      uiState = HomeUiState.Idle,
      pokemonList = PokemonSamples.pokemons,
      onLoadMore = {},
      onRetry = {},
      onCardClick = {},
    )
  }
}

@Preview
@Composable
internal fun HomeContentLoadingPreview() {
  PokedexPreviewTheme(padded = false) {
    HomeContent(
      uiState = HomeUiState.Loading,
      pokemonList = emptyList(),
      onLoadMore = {},
      onRetry = {},
      onCardClick = {},
    )
  }
}

@Preview
@Composable
internal fun HomeContentErrorPreview() {
  PokedexPreviewTheme(padded = false) {
    HomeContent(
      uiState = HomeUiState.Error("Unable to resolve host \"pokeapi.co\""),
      pokemonList = emptyList(),
      onLoadMore = {},
      onRetry = {},
      onCardClick = {},
    )
  }
}

@NavPreview(route = PokedexScreen.Details::class, primary = true)
@Preview
@Composable
internal fun DetailsContentPreview() {
  PokedexPreviewTheme(padded = false) {
    DetailsContent(
      pokemon = PokemonSamples.bulbasaur,
      uiState = DetailsUiState.Idle,
      pokemonInfo = PokemonSamples.bulbasaurInfo,
    )
  }
}

@Preview
@Composable
internal fun DetailsContentLoadingPreview() {
  PokedexPreviewTheme(padded = false) {
    DetailsContent(
      pokemon = PokemonSamples.bulbasaur,
      uiState = DetailsUiState.Loading,
      pokemonInfo = null,
    )
  }
}

@Preview
@Composable
internal fun DetailsContentErrorPreview() {
  PokedexPreviewTheme(padded = false) {
    DetailsContent(
      pokemon = PokemonSamples.bulbasaur,
      uiState = DetailsUiState.Error("Unable to resolve host \"pokeapi.co\""),
      pokemonInfo = null,
    )
  }
}

@NavPreview(route = PokedexScreen.Settings::class, primary = true)
@Preview
@Composable
internal fun SettingsDialogPreview() {
  PokedexPreviewTheme {
    SettingsDialog(
      uiState = SettingsUiState.Success(UserData(UiTheme.FOLLOW_SYSTEM)),
      onChangeUiTheme = {},
      onDismiss = {},
    )
  }
}

@Preview
@Composable
internal fun SettingsDialogDarkPreview() {
  PokedexPreviewTheme(darkTheme = true) {
    SettingsDialog(
      uiState = SettingsUiState.Success(UserData(UiTheme.DARK)),
      onChangeUiTheme = {},
      onDismiss = {},
    )
  }
}
