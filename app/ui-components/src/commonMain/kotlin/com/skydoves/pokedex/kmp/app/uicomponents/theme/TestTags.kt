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

package com.skydoves.pokedex.kmp.app.uicomponents.theme

import androidx.compose.ui.Modifier

/**
 * Exposes Compose `testTag`s to UI Automator as Android resource IDs.
 *
 * Macrobenchmark and baseline profile generation drive the app from outside the process, so they
 * can only see the semantics tree through resource IDs. Without this the profile generator cannot
 * find `PokedexList` and the run fails. It is an Android only concern, so every other platform
 * returns the modifier untouched.
 */
public expect fun Modifier.enableTestTagsAsResourceId(): Modifier
