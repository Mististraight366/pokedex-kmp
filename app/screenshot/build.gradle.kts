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
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.paparazzi)
  id("skydoves.pokedex.kmp.spotless")
}

/**
 * Paparazzi renders Compose without a device, but it only attaches to `com.android.library` and
 * `com.android.application` modules: it cannot be applied to a Kotlin Multiplatform module. So the
 * goldens live in this Android only module, which depends on the Android variants of the shared UI
 * and renders exactly the same composables the four apps do.
 */
android {
  namespace = "com.skydoves.pokedex.kmp.app.screenshot"
  compileSdk = libs.versions.androidCompileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.androidMinSdk.get().toInt()
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
  }

  lint {
    abortOnError = false
  }
}

dependencies {
  implementation(projects.app.uiComponents)
  implementation(projects.app.shared)
  implementation(projects.core.model)

  testImplementation(libs.junit)
}
