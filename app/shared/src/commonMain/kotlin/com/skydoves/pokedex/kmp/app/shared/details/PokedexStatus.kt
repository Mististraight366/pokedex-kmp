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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.Res
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.atk
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.def
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.exp
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.hp
import com.skydoves.pokedex.kmp.app.uicomponents.generated.resources.spd
import com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Immutable
internal data class PokedexStatus(
  val type: String,
  val progress: Float,
  val color: Color,
  val label: String,
)

@Composable
internal fun PokemonInfo.toPokedexStatusList(): ImmutableList<PokedexStatus> = persistentListOf(
  PokedexStatus(
    type = stringResource(Res.string.hp),
    progress = hp / PokemonInfo.MAX_HP.toFloat(),
    color = PokedexTheme.colors.primary,
    label = getHpString(),
  ),
  PokedexStatus(
    type = stringResource(Res.string.atk),
    progress = attack / PokemonInfo.MAX_ATTACK.toFloat(),
    color = PokedexTheme.colors.orange,
    label = getAttackString(),
  ),
  PokedexStatus(
    type = stringResource(Res.string.def),
    progress = defense / PokemonInfo.MAX_DEFENSE.toFloat(),
    color = PokedexTheme.colors.blue,
    label = getDefenseString(),
  ),
  PokedexStatus(
    type = stringResource(Res.string.spd),
    progress = speed / PokemonInfo.MAX_SPEED.toFloat(),
    color = PokedexTheme.colors.flying,
    label = getSpeedString(),
  ),
  PokedexStatus(
    type = stringResource(Res.string.exp),
    progress = exp / PokemonInfo.MAX_EXP.toFloat(),
    color = PokedexTheme.colors.green,
    label = getExpString(),
  ),
)
