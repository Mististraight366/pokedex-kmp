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

/**
 * Offline first: the cache is the single source of truth, and the network is only consulted when
 * the requested page is missing from it. Either way the result carries the whole accumulated list,
 * so the grid never has to stitch pages together itself.
 */
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class HomeRepositoryImpl(
  private val pokedexClient: PokedexClient,
  private val localDataSource: PokemonLocalDataSource,
) : HomeRepository {

  override fun fetchPokemonList(page: Int): Flow<PokemonPageResult> = flow {
    val cached = localDataSource.getPokemonListOfPage(page)
    if (cached.isNotEmpty()) {
      emit(
        PokemonPageResult.Success(
          pokemons = localDataSource.getPokemonList(page),
          // A short page can only be the tail of the Pokédex, and unlike the `next` link it is
          // still true when the page is replayed from cache after a restart.
          isLastPage = cached.size < PokedexClient.PAGING_SIZE,
        ),
      )
      return@flow
    }

    var emitted = false
    pokedexClient.fetchPokemonList(page = page)
      .suspendOnSuccess {
        localDataSource.insertPokemonList(data.results.map { it.copy(page = page) })
        emit(
          PokemonPageResult.Success(
            pokemons = localDataSource.getPokemonList(page),
            isLastPage = data.next == null || data.results.size < PokedexClient.PAGING_SIZE,
          ),
        )
        emitted = true
      }
      .suspendOnFailure {
        emit(PokemonPageResult.Failure(message()))
        emitted = true
      }

    // Sandwich only routes through onSuccess/onFailure, but a future response type must never
    // leave the caller waiting on a result that never arrives.
    if (!emitted) {
      emit(PokemonPageResult.Failure(null))
    }
  }.flowOn(ioDispatcher)
}
