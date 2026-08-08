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

package com.skydoves.pokedex.kmp.app.shared.di

import com.skydoves.pokedex.kmp.core.data.repository.UserDataRepository
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

/**
 * What the shared UI needs from the object graph.
 *
 * The concrete `@DependencyGraph` lives in each platform source set, because only there is it known
 * how to reach a database file or an Android `Context`. Everything above this interface is written
 * once.
 */
public interface AppGraph : ViewModelGraph {
  public val userDataRepository: UserDataRepository
}
