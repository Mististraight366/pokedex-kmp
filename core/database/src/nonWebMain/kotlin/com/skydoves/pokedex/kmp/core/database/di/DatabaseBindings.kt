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

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.skydoves.pokedex.kmp.core.common.ioDispatcher
import com.skydoves.pokedex.kmp.core.database.PokedexDatabase
import com.skydoves.pokedex.kmp.core.database.PokemonLocalDataSource
import com.skydoves.pokedex.kmp.core.database.RoomPokemonLocalDataSource
import com.skydoves.pokedex.kmp.core.database.StatsResponseConverter
import com.skydoves.pokedex.kmp.core.database.TypeResponseConverter
import com.skydoves.pokedex.kmp.core.database.dao.PokemonDao
import com.skydoves.pokedex.kmp.core.database.dao.PokemonInfoDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json

/**
 * Assembles the database from the platform supplied builder. Each platform contributes only the
 * file location, and everything downstream of that, the driver, the converters, and the query
 * dispatcher, is decided once here.
 */
@BindingContainer
@ContributesTo(AppScope::class)
public object DatabaseBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun providePokedexDatabase(
    builder: RoomDatabase.Builder<PokedexDatabase>,
    json: Json,
  ): PokedexDatabase = builder
    .addColumnTypeConverter(TypeResponseConverter(json))
    .addColumnTypeConverter(StatsResponseConverter(json))
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(ioDispatcher)
    .build()

  @Provides
  public fun providePokemonDao(database: PokedexDatabase): PokemonDao = database.pokemonDao()

  @Provides
  public fun providePokemonInfoDao(database: PokedexDatabase): PokemonInfoDao =
    database.pokemonInfoDao()

  @Provides
  @SingleIn(AppScope::class)
  public fun providePokemonLocalDataSource(
    dataSource: RoomPokemonLocalDataSource,
  ): PokemonLocalDataSource = dataSource
}
