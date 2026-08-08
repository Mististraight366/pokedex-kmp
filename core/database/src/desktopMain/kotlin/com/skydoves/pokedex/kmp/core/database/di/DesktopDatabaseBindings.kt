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

import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.skydoves.pokedex.kmp.core.database.POKEDEX_DATABASE_NAME
import com.skydoves.pokedex.kmp.core.database.PokedexDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import java.io.File

@BindingContainer
@ContributesTo(AppScope::class)
public object DesktopDatabaseBindings {

  @Provides
  public fun provideDatabaseBuilder(): RoomDatabase.Builder<PokedexDatabase> {
    val directory = File(System.getProperty("user.home"), ".pokedex-kmp").apply { mkdirs() }
    return Room.databaseBuilder(name = File(directory, POKEDEX_DATABASE_NAME).absolutePath)
  }
}
