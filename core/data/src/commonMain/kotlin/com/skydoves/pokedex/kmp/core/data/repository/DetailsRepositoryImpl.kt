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

package com.skydoves.pokedex.kmp.core.data.repository

import com.skydoves.pokedex.kmp.core.common.ioDispatcher
import com.skydoves.pokedex.kmp.core.database.PokemonLocalDataSource
import com.skydoves.pokedex.kmp.core.network.PokedexClient
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DetailsRepositoryImpl(
  private val pokedexClient: PokedexClient,
  private val localDataSource: PokemonLocalDataSource,
) : DetailsRepository {

  override fun fetchPokemonInfo(name: String): Flow<PokemonInfoResult> = flow {
    val cached = localDataSource.getPokemonInfo(name)
    if (cached != null) {
      emit(PokemonInfoResult.Success(cached))
      return@flow
    }

    var emitted = false
    pokedexClient.fetchPokemonInfo(name = name)
      .suspendOnSuccess {
        localDataSource.insertPokemonInfo(data)
        emit(PokemonInfoResult.Success(data))
        emitted = true
      }
      .suspendOnFailure {
        emit(PokemonInfoResult.Failure(message()))
        emitted = true
      }

    if (!emitted) {
      emit(PokemonInfoResult.Failure(null))
    }
  }.flowOn(ioDispatcher)
}
