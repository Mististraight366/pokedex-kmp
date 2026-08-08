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

package com.skydoves.pokedex.kmp.core.database.entity

import com.skydoves.pokedex.kmp.core.model.Pokemon
import com.skydoves.pokedex.kmp.core.model.PokemonInfo

/**
 * The entity stores the display name, so a row read back out already carries the capitalized form
 * and [Pokemon.name] is a no op on it.
 */
internal fun List<Pokemon>.asEntity(): List<PokemonEntity> = map { pokemon ->
  PokemonEntity(
    page = pokemon.page,
    name = pokemon.name,
    url = pokemon.url,
    index = pokemon.index,
  )
}

internal fun List<PokemonEntity>.asDomain(): List<Pokemon> = map { entity ->
  Pokemon(
    page = entity.page,
    nameField = entity.name,
    url = entity.url,
  )
}

internal fun PokemonInfo.asEntity(): PokemonInfoEntity = PokemonInfoEntity(
  id = id,
  name = name,
  height = height,
  weight = weight,
  experience = experience,
  types = types,
  stats = stats,
)

internal fun PokemonInfoEntity.asDomain(): PokemonInfo = PokemonInfo(
  id = id,
  name = name,
  height = height,
  weight = weight,
  experience = experience,
  types = types,
  stats = stats,
)
