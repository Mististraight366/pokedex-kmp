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

package com.skydoves.pokedex.kmp.app.shared.details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.skydoves.navgraph.annotations.NavDestination
import com.skydoves.cloudy.cloudy
import com.skydoves.pokedex.kmp.app.shared.navigation.PokedexScreen
import com.skydoves.pokedex.kmp.app.shared.navigation.currentComposeNavigator
import com.skydoves.pokedex.kmp.app.uicomponents.component.ArtworkColors
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexCircularProgress
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexErrorMessage
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexProgressBar
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokedexText
import com.skydoves.pokedex.kmp.app.uicomponents.component.PokemonArtwork
import com.skydoves.pokedex.kmp.app.uicomponents.component.pokemonSharedElementKey
import com.skydoves.pokedex.kmp.app.uicomponents.component.sharedPokemonBounds
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.Res
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.back
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.base_stats
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.height
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.ic_arrow
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.weight
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import com.skydoves.pokedex.kmp.app.uicomponents.util.getPokemonTypeColor
import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@NavDestination(route = PokedexScreen.Details::class)
@Composable
public fun PokedexDetails(
  pokemon: Pokemon,
  detailsViewModel: DetailsViewModel = metroViewModel(key = pokemon.name),
) {
  LaunchedEffect(pokemon.nameField) { detailsViewModel.load(pokemon.nameField) }

  val uiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
  val pokemonInfo by detailsViewModel.pokemonInfo.collectAsStateWithLifecycle()

  DetailsContent(
    pokemon = pokemon,
    uiState = uiState,
    pokemonInfo = pokemonInfo,
    onRetry = detailsViewModel::retry,
  )
}

@Composable
internal fun DetailsContent(
  pokemon: Pokemon,
  uiState: DetailsUiState,
  pokemonInfo: PokemonInfo?,
  onRetry: () -> Unit = {},
) {
  var artworkColors by remember { mutableStateOf(ArtworkColors.None) }
  val backgroundBrush by artworkColors.headerBrush()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(PokedexTheme.colors.background)
      .verticalScroll(rememberScrollState())
      .testTag("PokedexDetails"),
  ) {
    DetailsHeader(
      pokemon = pokemon,
      pokemonInfo = pokemonInfo,
      backgroundBrush = backgroundBrush,
      onColorsExtracted = { artworkColors = it },
    )

    when {
      pokemonInfo != null -> {
        DetailsInfo(pokemonInfo = pokemonInfo)
        DetailsStatus(pokemonInfo = pokemonInfo)
      }

      uiState is DetailsUiState.Error -> Box(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center,
      ) {
        PokedexErrorMessage(message = uiState.message, onRetry = onRetry)
      }

      else -> Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        PokedexCircularProgress()
      }
    }
  }
}

@Composable
private fun DetailsHeader(
  pokemon: Pokemon,
  pokemonInfo: PokemonInfo?,
  backgroundBrush: Brush,
  onColorsExtracted: (ArtworkColors) -> Unit,
) {
  val composeNavigator = currentComposeNavigator
  val shape = RoundedCornerShape(bottomStart = 64.dp, bottomEnd = 64.dp)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(290.dp)
      .sharedPokemonBounds(pokemonSharedElementKey(pokemon.name))
      .shadow(elevation = 9.dp, shape = shape)
      .background(brush = backgroundBrush, shape = shape),
  ) {
    // A soft blur behind the artwork, so the gradient reads as depth rather than a flat panel.
    Box(
      modifier = Modifier
        .fillMaxSize()
        .cloudy(radius = 18)
        .background(brush = backgroundBrush, shape = shape),
    )

    Row(
      modifier = Modifier.statusBarsPadding().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier
          .padding(end = 6.dp)
          .clickable { composeNavigator.navigateUp() },
        painter = painterResource(Res.drawable.ic_arrow),
        contentDescription = stringResource(Res.string.back),
        tint = PokedexTheme.colors.absoluteWhite,
      )
      Text(
        modifier = Modifier.padding(horizontal = 10.dp),
        text = pokemon.name,
        color = PokedexTheme.colors.absoluteWhite,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
      )
    }

    PokedexText(
      modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(12.dp),
      text = pokemonInfo?.getIdString().orEmpty(),
      previewText = "#001",
      color = PokedexTheme.colors.absoluteWhite,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp,
    )

    PokemonArtwork(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 20.dp)
        .size(190.dp),
      imageUrl = pokemon.imageUrl,
      onColorsExtracted = onColorsExtracted,
    )
  }

  PokedexText(
    modifier = Modifier.padding(top = 24.dp).animateContentSize().fillMaxWidth(),
    text = pokemon.name,
    previewText = "Bulbasaur",
    color = PokedexTheme.colors.blue,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    fontSize = 36.sp,
  )
}

@Composable
private fun DetailsInfo(pokemonInfo: PokemonInfo) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
    horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally),
  ) {
    pokemonInfo.types.forEach { typeResponse ->
      Text(
        modifier = Modifier
          .background(
            color = getPokemonTypeColor(typeResponse.type.name),
            shape = RoundedCornerShape(64.dp),
          )
          .padding(horizontal = 40.dp, vertical = 4.dp),
        text = typeResponse.type.name,
        color = PokedexTheme.colors.absoluteWhite,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1,
        fontSize = 16.sp,
      )
    }
  }

  Row(
    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
  ) {
    PokemonInfoItem(
      title = pokemonInfo.getWeightString(),
      previewTitle = "6.9 KG",
      content = stringResource(Res.string.weight),
    )
    PokemonInfoItem(
      title = pokemonInfo.getHeightString(),
      previewTitle = "0.7 M",
      content = stringResource(Res.string.height),
    )
  }
}

@Composable
private fun PokemonInfoItem(title: String, previewTitle: String, content: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    PokedexText(
      modifier = Modifier.padding(10.dp),
      text = title,
      previewText = previewTitle,
      color = PokedexTheme.colors.black,
      fontWeight = FontWeight.Bold,
      fontSize = 21.sp,
    )
    PokedexText(
      text = content,
      color = PokedexTheme.colors.white56,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
    )
  }
}

@Composable
private fun DetailsStatus(pokemonInfo: PokemonInfo) {
  Text(
    modifier = Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 16.dp),
    text = stringResource(Res.string.base_stats),
    color = PokedexTheme.colors.black,
    textAlign = TextAlign.Center,
    fontWeight = FontWeight.Bold,
    fontSize = 21.sp,
  )

  Column(modifier = Modifier.padding(bottom = 24.dp)) {
    pokemonInfo.toPokedexStatusList().forEach { status ->
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          modifier = Modifier.padding(start = 32.dp).size(width = 40.dp, height = 18.dp),
          text = status.type,
          color = PokedexTheme.colors.white70,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
        )
        PokedexProgressBar(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
          progress = status.progress,
          color = status.color,
          label = status.label,
        )
      }
    }
  }
}
