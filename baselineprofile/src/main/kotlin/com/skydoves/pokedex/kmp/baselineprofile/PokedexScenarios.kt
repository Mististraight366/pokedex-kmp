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

package com.skydoves.pokedex.kmp.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * The journey the profile is generated from: land on the grid, scroll it, open a Pokémon, and let
 * the details screen settle. Those are the code paths worth having compiled ahead of time.
 *
 * The selectors are the `testTag`s the screens set, which reach UI Automator because
 * [com.skydoves.pokedex.kmp.app.uicomponents.theme.PokedexTheme] turns on `testTagsAsResourceId`.
 */
public fun MacrobenchmarkScope.pokedexScenarios() {
  explorePokedexHome()
  navigateFromHomeToDetails()
  detailsWaitForContent()
}

public fun MacrobenchmarkScope.explorePokedexHome(): UiDevice = device.apply {
  homeWaitForContent()
  pokedexListScrollDownUp()
}

public fun MacrobenchmarkScope.homeWaitForContent(): UiDevice = device.apply {
  wait(Until.hasObject(By.res("PokedexList")), TIMEOUT)
}

public fun MacrobenchmarkScope.pokedexListScrollDownUp(): UiDevice = device.apply {
  val pokedexList = waitAndFindObject(By.res("PokedexList"), TIMEOUT)
  flingElementDownUp(pokedexList)
}

public fun MacrobenchmarkScope.navigateFromHomeToDetails(): UiDevice = device.apply {
  waitAndFindObject(By.res("Pokemon"), TIMEOUT).click()
  waitForIdle()
}

public fun MacrobenchmarkScope.detailsWaitForContent(): UiDevice = device.apply {
  wait(Until.hasObject(By.res("PokedexDetails")), TIMEOUT)
}

internal fun UiDevice.flingElementDownUp(element: UiObject2) {
  // Keep clear of the sides so the fling is not swallowed by system back gestures.
  element.setGestureMargin(displayWidth / 5)

  element.fling(Direction.DOWN)
  waitForIdle()
  element.fling(Direction.UP)
}

internal fun UiDevice.waitAndFindObject(selector: BySelector, timeout: Long = TIMEOUT): UiObject2 {
  if (!wait(Until.hasObject(selector), timeout)) {
    throw AssertionError("Element not found on screen in ${timeout}ms (selector=$selector)")
  }
  return findObject(selector)
}

private const val TIMEOUT = 15_000L
