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

package com.skydoves.pokedex.kmp.app.shared.di

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlin.reflect.KClass

/**
 * Turns the map multibinding that `@ContributesIntoMap` fills into a `ViewModelProvider.Factory`.
 *
 * Every ViewModel annotated with `@ViewModelKey` lands in [viewModelProviders] at compile time, so
 * `metroViewModel<HomeViewModel>()` resolves without a single reflective lookup.
 */
@Inject
@SingleIn(AppScope::class)
public class PokedexViewModelFactory(
  override val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel>,
) : MetroViewModelFactory()

@BindingContainer
@ContributesTo(AppScope::class)
public object ViewModelBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideMetroViewModelFactory(
    factory: PokedexViewModelFactory,
  ): MetroViewModelFactory = factory
}
