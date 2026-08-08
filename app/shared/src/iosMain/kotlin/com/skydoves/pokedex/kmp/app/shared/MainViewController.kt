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

package com.skydoves.pokedex.kmp.app.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.skydoves.pokedex.kmp.app.shared.di.IosAppGraph
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

private val appGraph: IosAppGraph by lazy { createGraph<IosAppGraph>() }

// Swift calls this as `MainViewControllerKt.MainViewController()`, so the name follows the Cocoa
// convention for a view controller factory rather than Kotlin's.
@Suppress("ktlint:standard:function-naming")
public fun MainViewController(): UIViewController = ComposeUIViewController {
  App(appGraph)
}
