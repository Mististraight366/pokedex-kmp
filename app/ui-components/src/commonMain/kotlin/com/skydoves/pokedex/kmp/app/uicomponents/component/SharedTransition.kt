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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect

/**
 * The two scopes a shared element needs, published so a screen deep in the tree can join a
 * transition without every composable between here and there taking them as parameters.
 *
 * Both are null outside a `SharedTransitionLayout`, which is what previews and screenshot tests
 * render in. A composable reads them once and keeps that answer for its lifetime, so the branch
 * below never flips underneath an existing composition.
 */
public val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope?> =
  compositionLocalOf { null }

public val LocalNavAnimatedVisibilityScope: ProvidableCompositionLocal<AnimatedVisibilityScope?> =
  compositionLocalOf { null }

/** The shared element key both the grid card and the details header register under. */
public fun pokemonSharedElementKey(name: String): String = "pokemon-$name"

/**
 * Joins this element to the shared transition registered under [key], if there is one to join.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
public fun Modifier.sharedPokemonBounds(key: String): Modifier {
  val sharedTransitionScope = LocalSharedTransitionScope.current
  val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current

  if (sharedTransitionScope == null || animatedVisibilityScope == null) return this

  return with(sharedTransitionScope) {
    this@sharedPokemonBounds.sharedBounds(
      sharedContentState = rememberSharedContentState(key = key),
      animatedVisibilityScope = animatedVisibilityScope,
      boundsTransform = { _, _ -> PokemonBoundsSpring },
    )
  }
}

/**
 * Slow enough to read as one card growing into a screen, damped so it settles without a bounce
 * that would fight the cross fade underneath it.
 */
private val PokemonBoundsSpring: FiniteAnimationSpec<Rect> = spring(
  dampingRatio = Spring.DampingRatioNoBouncy,
  stiffness = Spring.StiffnessMediumLow,
  visibilityThreshold = Rect.VisibilityThreshold,
)
