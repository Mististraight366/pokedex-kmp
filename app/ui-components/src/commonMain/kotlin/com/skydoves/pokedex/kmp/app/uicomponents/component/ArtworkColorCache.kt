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

/**
 * Remembers the colours already extracted for an artwork URL.
 *
 * The grid extracts a Pokémon's colours before the details screen ever opens. Without this the
 * details header would start on the theme background and only turn the right colour once its own
 * copy of the image finished decoding, which shows up as a dark flash in the middle of the shared
 * element transition. Reading the cache makes the header open on the colour the card already had.
 *
 * The list is 1300 entries at most and each one holds two colours, so it is bounded by the dataset
 * rather than by traffic and does not need eviction.
 */
public object ArtworkColorCache {

  private val cache = mutableMapOf<String, ArtworkColors>()

  public operator fun get(imageUrl: String): ArtworkColors? = cache[imageUrl]

  public fun put(imageUrl: String, colors: ArtworkColors) {
    cache[imageUrl] = colors
  }
}
