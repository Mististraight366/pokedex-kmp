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

package com.skydoves.pokedex.kmp.core.database.di

import com.skydoves.pokedex.kmp.core.database.InMemoryPokemonLocalDataSource
import com.skydoves.pokedex.kmp.core.database.PokemonLocalDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * SQLite in the browser means either a WebAssembly build behind cross-origin isolation headers or
 * an OPFS backed driver, and neither is something a demo should force on whoever hosts it. The web
 * build keeps its cache in memory for the length of the session instead.
 */
@BindingContainer
@ContributesTo(AppScope::class)
public object WebDatabaseBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun providePokemonLocalDataSource(): PokemonLocalDataSource =
    InMemoryPokemonLocalDataSource()
}
