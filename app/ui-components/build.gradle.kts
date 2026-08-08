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
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(projects.core.model)
      api(compose.runtime)
      api(compose.foundation)
      api(compose.material3)
      api(libs.compose.material.icons.core)
      api(compose.animation)
      api(compose.ui)
      api(compose.components.resources)
      api(compose.preview)
      api(libs.landscapist.coil3)
      api(libs.cloudy)
      api(libs.kotlinx.immutable.collections)
    }

    androidMain.dependencies {
      implementation(compose.uiTooling)
    }

    getByName("desktopMain").dependencies {
      implementation(compose.desktop.currentOs)
    }
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.skydoves.pokedex.kmp.app.uicomponents.generated.resources"
  generateResClass = always
}
