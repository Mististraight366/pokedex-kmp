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

import androidx.room3.ColumnTypeConverter
import androidx.room3.ProvidedColumnTypeConverter
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import kotlinx.serialization.json.Json

/**
 * The type and stat lists are stored as JSON text in a single column, which keeps the schema flat
 * and avoids two join tables for data that is always read as a whole.
 */
@ProvidedColumnTypeConverter
public class TypeResponseConverter(private val json: Json) {

  @ColumnTypeConverter
  public fun fromString(value: String): List<PokemonInfo.TypeResponse> =
    json.decodeFromString(value)

  @ColumnTypeConverter
  public fun fromTypeResponses(types: List<PokemonInfo.TypeResponse>): String =
    json.encodeToString(types)
}

@ProvidedColumnTypeConverter
public class StatsResponseConverter(private val json: Json) {

  @ColumnTypeConverter
  public fun fromString(value: String): List<PokemonInfo.StatsResponse> =
    json.decodeFromString(value)

  @ColumnTypeConverter
  public fun fromStatsResponses(stats: List<PokemonInfo.StatsResponse>): String =
    json.encodeToString(stats)
}
