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

package com.skydoves.pokedex.kmp.app.shared.home

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.skydoves.pokedex.kmp.app.shared.navigation.PokedexScreen
import com.skydoves.pokedex.kmp.app.shared.navigation.currentComposeNavigator
import com.skydoves.pokedex.kmp.app.shared.navigation.toDetailsRoute
import com.skydoves.pokedex.kmp.app.uicomponents.component.ArtworkColors
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexAppBar
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexCircularProgress
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexErrorMessage
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokemonArtwork
import com.skydoves.pokedex.kmp.app.uicomponents.component.pokemonSharedElementKey
import com.skydoves.pokedex.kmp.app.uicomponents.component.sharedPokemonBounds
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import com.skydoves.pokedex.kmp.core.model.Pokemon
import dev.zacsweers.metrox.viewmodel.metroViewModel

@NavDestination(route = PokedexScreen.Home::class)
@NavEdge(to = PokedexScreen.Details::class, label = "Pokémon card")
@NavEdge(to = PokedexScreen.Settings::class, label = "App bar")
@Composable
public fun PokedexHome(homeViewModel: HomeViewModel = metroViewModel()) {
  val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
  val pokemonList by homeViewModel.pokemonList.collectAsStateWithLifecycle()
  val composeNavigator = currentComposeNavigator

  Column(modifier = Modifier.fillMaxSize()) {
    PokedexAppBar(onActionClick = { composeNavigator.navigate(PokedexScreen.Settings) })

    HomeContent(
      uiState = uiState,
      pokemonList = pokemonList,
      onLoadMore = homeViewModel::fetchNextPokemonList,
      onRetry = homeViewModel::retry,
      onCardClick = { pokemon -> composeNavigator.navigate(pokemon.toDetailsRoute()) },
    )
  }
}

@Composable
internal fun HomeContent(
  uiState: HomeUiState,
  pokemonList: List<Pokemon>,
  onLoadMore: () -> Unit,
  onRetry: () -> Unit,
  onCardClick: (Pokemon) -> Unit,
) {
  val gridState = rememberLazyGridState()

  // Prefetch is driven off the scroll position, not off item composition. Calling it from inside
  // an item body fires once per trailing item in the same frame, and the request is asynchronous,
  // so every one of those calls sees the same not-yet-loading state and advances the page again.
  val shouldLoadMore by remember {
    derivedStateOf {
      val lastVisible =
        gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
      lastVisible + PREFETCH_THRESHOLD >= gridState.layoutInfo.totalItemsCount
    }
  }

  LaunchedEffect(shouldLoadMore) {
    if (shouldLoadMore) onLoadMore()
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyVerticalGrid(
      state = gridState,
      modifier = Modifier.fillMaxSize().testTag("PokedexList"),
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(16.dp),
    ) {
      itemsIndexed(
        items = pokemonList,
        key = { _, pokemon -> pokemon.name },
      ) { _, pokemon ->
        PokemonCard(pokemon = pokemon, onCardClick = { onCardClick(pokemon) })
      }
    }

    when {
      uiState is HomeUiState.Loading && pokemonList.isEmpty() -> PokedexCircularProgress()

      uiState is HomeUiState.Error && pokemonList.isEmpty() ->
        PokedexErrorMessage(
          modifier = Modifier.align(Alignment.Center),
          message = uiState.message,
          onRetry = onRetry,
        )

      uiState is HomeUiState.Error ->
        PokedexErrorMessage(
          modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
          message = uiState.message,
          onRetry = onRetry,
        )
    }
  }
}

@Composable
private fun LazyGridItemScope.PokemonCard(pokemon: Pokemon, onCardClick: () -> Unit) {
  var artworkColors by remember { mutableStateOf(ArtworkColors.None) }
  val backgroundColor by artworkColors.cardBackgroundColor()

  Card(
    modifier = Modifier
      .padding(6.dp)
      .fillMaxWidth()
      .testTag("Pokemon")
      .sharedPokemonBounds(pokemonSharedElementKey(pokemon.name))
      .animateItem(
        fadeInSpec = tween(durationMillis = 250),
        fadeOutSpec = tween(durationMillis = 100),
        placementSpec = spring(),
      )
      .clickable { onCardClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    PokemonArtwork(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(top = 20.dp)
        .size(120.dp),
      imageUrl = pokemon.imageUrl,
      onColorsExtracted = { artworkColors = it },
    )

    Text(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .fillMaxWidth()
        .padding(12.dp),
      text = pokemon.name,
      color = PokedexTheme.colors.black,
      textAlign = TextAlign.Center,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
    )
  }
}

private const val PREFETCH_THRESHOLD = 8
