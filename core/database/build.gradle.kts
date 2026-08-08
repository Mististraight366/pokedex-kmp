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

plugins {
  id("skydoves.pokedex.kmp.multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.androidx.room)
  alias(libs.plugins.ksp)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.core.model)
      api(projects.core.common)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.metro.runtime)
    }

    // Room reaches SQLite through a native driver, which rules out the browser. Everything Room
    // touches lives here; wasm gets the in-memory data source from commonMain instead.
    getByName("nonWebMain").dependencies {
      api(libs.androidx.room.runtime)
      implementation(libs.androidx.sqlite.bundled)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
    }
  }
}

room3 {
  schemaDirectory("$projectDir/schemas")
}

dependencies {
  listOf(
    "kspAndroid",
    "kspIosArm64",
    "kspIosSimulatorArm64",
    "kspDesktop",
  ).forEach { add(it, libs.androidx.room.compiler) }
}
