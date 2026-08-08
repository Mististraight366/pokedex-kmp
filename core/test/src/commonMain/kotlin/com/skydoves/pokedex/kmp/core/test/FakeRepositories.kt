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

package com.skydoves.pokedex.kmp.core.test

import com.skydoves.pokedex.kmp.core.data.repository.DetailsRepository
import com.skydoves.pokedex.kmp.core.data.repository.HomeRepository
import com.skydoves.pokedex.kmp.core.data.repository.PokemonInfoResult
import com.skydoves.pokedex.kmp.core.data.repository.PokemonPageResult
import com.skydoves.pokedex.kmp.core.data.repository.UserDataRepository
import com.skydoves.pokedex.kmp.core.model.UiTheme
import com.skydoves.pokedex.kmp.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Repositories that answer instantly from [MockUtil]. Previews and screenshot goldens run against
 * these, which is what keeps a rendered image free of network timing.
 */
public class FakeHomeRepository(private val failWith: String? = null) : HomeRepository {

  override fun fetchPokemonList(page: Int): Flow<PokemonPageResult> = flow {
    emit(
      if (failWith != null) {
        PokemonPageResult.Failure(failWith)
      } else {
        PokemonPageResult.Success(pokemons = MockUtil.mockPokemonList(), isLastPage = true)
      },
    )
  }
}

public class FakeDetailsRepository(private val failWith: String? = null) : DetailsRepository {

  override fun fetchPokemonInfo(name: String): Flow<PokemonInfoResult> = flow {
    emit(
      if (failWith != null) {
        PokemonInfoResult.Failure(failWith)
      } else {
        PokemonInfoResult.Success(MockUtil.mockPokemonInfo())
      },
    )
  }
}

public class FakeUserDataRepository(initial: UserData = UserData(UiTheme.FOLLOW_SYSTEM)) :
  UserDataRepository {

  private val state = MutableStateFlow(initial)

  override val userData: Flow<UserData> = state

  override suspend fun setUiTheme(uiTheme: UiTheme) {
    state.value = UserData(uiTheme)
  }
}
