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

package com.skydoves.pokedex.kmp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.skydoves.pokedex.kmp.app.shared.App
import com.skydoves.pokedex.kmp.app.shared.di.WebAppGraph
import dev.zacsweers.metro.createGraph
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
public fun main() {
  val appGraph = createGraph<WebAppGraph>()

  ComposeViewport(document.body!!) {
    App(appGraph)
  }
}
