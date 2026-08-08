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

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Formats this float with exactly [digits] fraction digits.
 *
 * `String.format` is a JVM only API, so the shared module rounds to a scaled long and pads the
 * fraction back out by hand. That keeps the output identical on Android, iOS, desktop, and the
 * browser, and it also sidesteps the locale dependent decimal separator that `String.format` would
 * otherwise pick up.
 */
public fun Float.toFixed(digits: Int): String {
  require(digits >= 0) { "digits must not be negative, but was $digits" }

  val factor = 10.0.pow(digits)
  val scaled = (abs(this).toDouble() * factor).roundToLong()
  val sign = if (this < 0f && scaled != 0L) "-" else ""

  if (digits == 0) return "$sign$scaled"

  val text = scaled.toString().padStart(digits + 1, '0')
  return "$sign${text.dropLast(digits)}.${text.takeLast(digits)}"
}
