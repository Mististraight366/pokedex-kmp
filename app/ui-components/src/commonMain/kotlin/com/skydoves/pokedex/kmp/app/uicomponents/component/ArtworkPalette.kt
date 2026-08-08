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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * The two swatches the screens paint with, lifted out of the artwork once it has loaded.
 */
@Immutable
public data class ArtworkColors(val dominant: Color? = null, val lightVibrant: Color? = null) {
  public companion object {
    public val None: ArtworkColors = ArtworkColors()
  }
}

/**
 * Extracts the dominant and light vibrant colours from a decoded sprite.
 *
 * This is deliberately hand rolled rather than delegated to a palette library. Landscapist's
 * palette plugin resolves to AndroidX Palette on Android and to a different quantizer everywhere
 * else, which made the same Pokémon render a green card on Android and a pink one on iOS. One
 * implementation in common code means all four platforms agree by construction.
 *
 * The algorithm is the standard three step quantize: sample the bitmap on a stride, drop the
 * transparent pixels that make up most of an official artwork PNG, bucket what is left by colour,
 * then score the buckets. The dominant swatch is simply the most populous bucket. The light vibrant
 * swatch is scored against a target of high saturation and a lightness around 0.74, which is what
 * gives the details header its gradient.
 */
public fun ImageBitmap.extractArtworkColors(): ArtworkColors {
  val pixels = runCatching { toPixelMap() }.getOrNull() ?: return ArtworkColors.None

  // Sample at most MAX_SAMPLES pixels. A 475x475 sprite is 225k pixels, and reading all of them
  // for every card in the grid is the difference between a smooth scroll and a stutter.
  val total = pixels.width * pixels.height
  if (total == 0) return ArtworkColors.None
  val stride = max(1, total / MAX_SAMPLES)

  val populations = HashMap<Int, Int>()
  val redSums = HashMap<Int, Long>()
  val greenSums = HashMap<Int, Long>()
  val blueSums = HashMap<Int, Long>()

  var index = 0
  while (index < total) {
    val color = pixels[index % pixels.width, index / pixels.width]
    index += stride

    if (color.alpha < MIN_ALPHA) continue

    val r = (color.red * 255f).toInt().coerceIn(0, 255)
    val g = (color.green * 255f).toInt().coerceIn(0, 255)
    val b = (color.blue * 255f).toInt().coerceIn(0, 255)

    // Five bits per channel, the same quantization depth AndroidX Palette uses.
    val bucket = (r shr 3 shl 10) or (g shr 3 shl 5) or (b shr 3)
    populations[bucket] = (populations[bucket] ?: 0) + 1
    redSums[bucket] = (redSums[bucket] ?: 0L) + r
    greenSums[bucket] = (greenSums[bucket] ?: 0L) + g
    blueSums[bucket] = (blueSums[bucket] ?: 0L) + b
  }

  if (populations.isEmpty()) return ArtworkColors.None

  val swatches = populations.map { (bucket, population) ->
    Swatch(
      color = Color(
        red = (redSums.getValue(bucket) / population).toInt(),
        green = (greenSums.getValue(bucket) / population).toInt(),
        blue = (blueSums.getValue(bucket) / population).toInt(),
      ),
      population = population,
    )
  }

  val maxPopulation = swatches.maxOf { it.population }
  val dominant = swatches.maxByOrNull { it.population }?.color

  val lightVibrant = swatches
    .map { swatch -> swatch to swatch.scoreAgainstLightVibrant(maxPopulation) }
    .filter { (_, score) -> score > 0f }
    .maxByOrNull { (_, score) -> score }
    ?.first
    ?.color

  return ArtworkColors(dominant = dominant, lightVibrant = lightVibrant)
}

private data class Swatch(val color: Color, val population: Int)

/**
 * Scores how well a swatch matches the light vibrant target, weighing the match against how much
 * of the image the swatch covers so a single stray pixel cannot win.
 */
private fun Swatch.scoreAgainstLightVibrant(maxPopulation: Int): Float {
  val (_, saturation, lightness) = color.toHsl()
  if (saturation < MIN_VIBRANT_SATURATION) return 0f
  if (lightness < MIN_LIGHT_LIGHTNESS || lightness > MAX_LIGHT_LIGHTNESS) return 0f

  val saturationScore = 1f - abs(saturation - TARGET_VIBRANT_SATURATION)
  val lightnessScore = 1f - abs(lightness - TARGET_LIGHT_LIGHTNESS)
  val populationScore = population.toFloat() / maxPopulation

  return saturationScore * SATURATION_WEIGHT +
    lightnessScore * LIGHTNESS_WEIGHT +
    populationScore * POPULATION_WEIGHT
}

/** Returns hue in degrees, plus saturation and lightness in 0..1. */
private fun Color.toHsl(): Triple<Float, Float, Float> {
  val r = red
  val g = green
  val b = blue
  val maxValue = max(r, max(g, b))
  val minValue = min(r, min(g, b))
  val delta = maxValue - minValue
  val lightness = (maxValue + minValue) / 2f

  if (delta == 0f) return Triple(0f, 0f, lightness)

  val saturation = if (lightness > 0.5f) {
    delta / (2f - maxValue - minValue)
  } else {
    delta / (maxValue + minValue)
  }

  val hue = when (maxValue) {
    r -> 60f * (((g - b) / delta) % 6f)
    g -> 60f * (((b - r) / delta) + 2f)
    else -> 60f * (((r - g) / delta) + 4f)
  }

  return Triple(if (hue < 0f) hue + 360f else hue, saturation, lightness)
}

private const val MAX_SAMPLES = 12_000
private const val MIN_ALPHA = 0.5f
private const val MIN_VIBRANT_SATURATION = 0.2f
private const val TARGET_VIBRANT_SATURATION = 1f
private const val MIN_LIGHT_LIGHTNESS = 0.55f
private const val TARGET_LIGHT_LIGHTNESS = 0.74f
private const val MAX_LIGHT_LIGHTNESS = 1f
private const val SATURATION_WEIGHT = 0.24f
private const val LIGHTNESS_WEIGHT = 0.52f
private const val POPULATION_WEIGHT = 0.24f
