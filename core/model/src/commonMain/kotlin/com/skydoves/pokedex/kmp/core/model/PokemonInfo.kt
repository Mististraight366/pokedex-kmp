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

package com.skydoves.pokedex.kmp.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Immutable
@Serializable
public data class PokemonInfo(
  @SerialName(value = "id")
  val id: Int,
  @SerialName(value = "name")
  val name: String,
  @SerialName(value = "height")
  val height: Int,
  @SerialName(value = "weight")
  val weight: Int,
  @SerialName(value = "base_experience")
  val experience: Int,
  @SerialName(value = "types")
  val types: List<TypeResponse>,
  @SerialName(value = "stats")
  val stats: List<StatsResponse>,
) {
  /**
   * PokeAPI has no experience-to-next-level field, so the bar is filled from a value derived from
   * the Pokédex index. Seeding on [id] rather than calling [Random.nextInt] keeps it out of
   * `equals`/`hashCode` and out of the serial descriptor, and makes it identical on every platform
   * and every launch, which a random default was not.
   */
  val exp: Int
    get() = Random(id).nextInt(MAX_EXP)

  val hp: Int
    get() = stats.firstOrNull { it.stat.name == "hp" }?.baseStat ?: 0

  val attack: Int
    get() = stats.firstOrNull { it.stat.name == "attack" }?.baseStat ?: 0

  val defense: Int
    get() = stats.firstOrNull { it.stat.name == "defense" }?.baseStat ?: 0

  val speed: Int
    get() = stats.firstOrNull { it.stat.name == "speed" }?.baseStat ?: 0

  public fun getIdString(): String = "#" + id.toString().padStart(3, '0')

  public fun getWeightString(): String = "${(weight / 10f).toFixed(1)} KG"

  public fun getHeightString(): String = "${(height / 10f).toFixed(1)} M"

  public fun getHpString(): String = " $hp/$MAX_HP"

  public fun getAttackString(): String = " $attack/$MAX_ATTACK"

  public fun getDefenseString(): String = " $defense/$MAX_DEFENSE"

  public fun getSpeedString(): String = " $speed/$MAX_SPEED"

  public fun getExpString(): String = " $exp/$MAX_EXP"

  @Immutable
  @Serializable
  public data class TypeResponse(
    @SerialName(value = "slot") val slot: Int,
    @SerialName(value = "type") val type: Type,
  )

  @Immutable
  @Serializable
  public data class StatsResponse(
    @SerialName(value = "base_stat") val baseStat: Int,
    @SerialName(value = "effort") val effort: Int,
    @SerialName(value = "stat") val stat: Stat,
  )

  @Immutable
  @Serializable
  public data class Stat(@SerialName(value = "name") val name: String)

  @Immutable
  @Serializable
  public data class Type(@SerialName(value = "name") val name: String)

  public companion object {
    public const val MAX_HP: Int = 300
    public const val MAX_ATTACK: Int = 300
    public const val MAX_DEFENSE: Int = 300
    public const val MAX_SPEED: Int = 300
    public const val MAX_EXP: Int = 1000
  }
}
