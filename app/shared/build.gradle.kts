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
  id("skydoves.pokedex.kmp.multiplatform.compose")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.metro)
  // The navgraph processor is a KSP processor, and the plugin cannot apply KSP itself because its
  // version is tied to the Kotlin version.
  alias(libs.plugins.ksp)
  alias(libs.plugins.compose.nav.graph)
}

kotlin {
  // The Xcode project links against this framework; `embedAndSignAppleFrameworkForXcode` is what
  // the "Compile Kotlin" build phase runs.
  listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
    target.binaries.framework {
      baseName = "Shared"
      isStatic = true
      // Without this the linker warns on every build that it fell back to the bundle name.
      binaryOption("bundleId", "com.skydoves.pokedex.kmp.shared")
    }
  }

  sourceSets {
    commonMain.dependencies {
      api(projects.app.uiComponents)
      api(projects.core.data)
      api(libs.androidx.lifecycle.viewmodel)
      api(libs.androidx.lifecycle.viewmodel.compose)
      api(libs.androidx.lifecycle.runtime.compose)
      api(libs.androidx.navigation.compose)
      api(libs.metro.runtime)
      api(libs.metro.viewmodel)
      api(libs.metro.viewmodel.compose)
      implementation(libs.kotlinx.coroutines.core)
      implementation(compose.preview)
    }

    androidMain.dependencies {
      implementation(libs.kotlinx.coroutines.android)
    }

    getByName("desktopMain").dependencies {
      implementation(libs.kotlinx.coroutines.swing)
    }

    commonTest.dependencies {
      implementation(projects.core.test)
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.turbine)
    }
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.skydoves.pokedex.kmp.app.shared.generated.resources"
}
