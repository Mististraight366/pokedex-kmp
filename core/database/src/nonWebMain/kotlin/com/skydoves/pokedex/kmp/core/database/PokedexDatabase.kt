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

package com.skydoves.pokedex.kmp.core.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.skydoves.pokedex.kmp.core.database.dao.PokemonDao
import com.skydoves.pokedex.kmp.core.database.dao.PokemonInfoDao
import com.skydoves.pokedex.kmp.core.database.entity.PokemonEntity
import com.skydoves.pokedex.kmp.core.database.entity.PokemonInfoEntity

@Database(
  entities = [PokemonEntity::class, PokemonInfoEntity::class],
  // v2 adds PokemonEntity.index (the grid's sort key) and drops PokemonInfoEntity.exp, which is
  // now derived. Room validates a schema identity hash on open, and
  // `fallbackToDestructiveMigration` only fires on a version change, so changing the entities
  // without bumping this crashes any install carrying the old file.
  version = 2,
  exportSchema = true,
)
@ColumnTypeConverters(value = [TypeResponseConverter::class, StatsResponseConverter::class])
@ConstructedBy(PokedexDatabaseConstructor::class)
public abstract class PokedexDatabase : RoomDatabase() {
  public abstract fun pokemonDao(): PokemonDao
  public abstract fun pokemonInfoDao(): PokemonInfoDao
}

/**
 * Room generates the `actual` for every target it processes, so the expect declaration only has to
 * name the contract. Android, iOS, and desktop each resolve their own generated implementation.
 */
@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_IR_INCOMPATIBILITY")
public expect object PokedexDatabaseConstructor : RoomDatabaseConstructor<PokedexDatabase> {
  override fun initialize(): PokedexDatabase
}

public const val POKEDEX_DATABASE_NAME: String = "pokedex.db"
