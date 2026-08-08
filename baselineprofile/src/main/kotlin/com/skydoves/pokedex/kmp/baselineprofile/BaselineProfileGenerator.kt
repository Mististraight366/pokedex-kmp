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

package com.skydoves.pokedex.kmp.baselineprofile

import android.annotation.SuppressLint
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the baseline profile that ships in the release APK.
 *
 * Run it with `./gradlew :app:androidApp:generateReleaseBaselineProfile`. The output lands in
 * `app/androidApp/src/release/generated/baselineProfiles/` and should be committed.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

  @SuppressLint("NewApi")
  @get:Rule
  val rule = BaselineProfileRule()

  @SuppressLint("NewApi")
  @Test
  fun generate() {
    rule.collect(
      packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: error("targetAppId was not passed as an instrumentation runner argument"),
      // Also emit a startup profile, which drives the dex layout optimization.
      includeInStartupProfile = true,
      stableIterations = 2,
      maxIterations = 8,
    ) {
      pressHome()
      startActivityAndWait()

      pokedexScenarios()
    }
  }
}
