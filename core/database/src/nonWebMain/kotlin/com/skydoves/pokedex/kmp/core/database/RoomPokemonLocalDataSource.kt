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

import com.skydoves.pokedex.kmp.core.database.dao.PokemonDao
import com.skydoves.pokedex.kmp.core.database.dao.PokemonInfoDao
import com.skydoves.pokedex.kmp.core.database.entity.asDomain
import com.skydoves.pokedex.kmp.core.database.entity.asEntity
import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.model.PokemonInfo
import dev.zacsweers.metro.Inject

@Inject
public class RoomPokemonLocalDataSource(
  private val pokemonDao: PokemonDao,
  private val pokemonInfoDao: PokemonInfoDao,
) : PokemonLocalDataSource {

  override suspend fun getPokemonList(page: Int): List<Pokemon> =
    pokemonDao.getAllPokemonList(page).asDomain()

  override suspend fun getPokemonListOfPage(page: Int): List<Pokemon> =
    pokemonDao.getPokemonListOfPage(page).asDomain()

  override suspend fun insertPokemonList(pokemonList: List<Pokemon>): Unit =
    pokemonDao.insertPokemonList(pokemonList.asEntity())

  override suspend fun getPokemonInfo(name: String): PokemonInfo? =
    pokemonInfoDao.getPokemonInfo(name)?.asDomain()

  override suspend fun insertPokemonInfo(pokemonInfo: PokemonInfo): Unit =
    pokemonInfoDao.insertPokemonInfo(pokemonInfo.asEntity())
}
