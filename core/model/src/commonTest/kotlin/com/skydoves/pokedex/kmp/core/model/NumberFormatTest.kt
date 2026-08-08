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

package com.skydoves.pokedex.kmp.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NumberFormatTest {

  @Test
  fun `pads the fraction out to the requested width`() {
    assertEquals("6.9", 6.9f.toFixed(1))
    assertEquals("2.0", 2f.toFixed(1))
    assertEquals("0.1", 0.1f.toFixed(1))
    assertEquals("0.10", 0.1f.toFixed(2))
  }

  @Test
  fun `rounds a half up in magnitude`() {
    assertEquals("0.3", 0.25f.toFixed(1))
    assertEquals("0.8", 0.75f.toFixed(1))
    assertEquals("-0.3", (-0.25f).toFixed(1))
  }

  @Test
  fun `carries into the integer part`() {
    assertEquals("1.0", 0.96f.toFixed(1))
    assertEquals("10.0", 9.99f.toFixed(1))
  }

  @Test
  fun `keeps a leading zero for values below one`() {
    assertEquals("0.7", 0.7f.toFixed(1))
    assertEquals("0.0", 0f.toFixed(1))
  }

  @Test
  fun `renders negatives with a single sign`() {
    assertEquals("-1.5", (-1.5f).toFixed(1))
    assertEquals("0.0", (-0.01f).toFixed(1))
  }

  @Test
  fun `zero digits drops the separator`() {
    assertEquals("7", 6.9f.toFixed(0))
    assertEquals("0", 0.4f.toFixed(0))
  }

  @Test
  fun `rejects a negative digit count`() {
    assertFailsWith<IllegalArgumentException> { 1f.toFixed(-1) }
  }
}
