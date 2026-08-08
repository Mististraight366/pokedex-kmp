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
  id("skydoves.pokedex.kmp.android.application")
  alias(libs.plugins.metro)
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.compose.stability.analyzer)
  // Applied conditionally below, not here.
  alias(libs.plugins.hotswan.compiler) apply false
}

/**
 * HotSwan is opt in, via `-Photswan.enabled=true`.
 *
 * Its compiler plugin rewrites Compose call sites so a running app can swap them at runtime, and
 * `captureAllPreviews` then drives that app over adb to photograph every `@Preview`. But the same
 * rewrite makes those classes unloadable by compose-nav-graph's headless Layoutlib renderer, which
 * has no HotSwan runtime to initialise. Rather than lose one tool to the other, ordinary builds
 * and the navigation graph render clean bytecode, and the preview-capture job turns HotSwan on for
 * the one task that needs it:
 *
 * ```
 * ./gradlew -Photswan.enabled=true :app:androidApp:captureAllPreviews
 * ```
 */
val hotSwanEnabled = providers.gradleProperty("hotswan.enabled").orNull == "true"

if (hotSwanEnabled) {
  apply(plugin = "com.github.skydoves.compose.hotswan.compiler")

  extensions.configure<com.skydoves.compose.hotswan.interpreter.compiler.gradle.InterpreterExtension>(
    "hotSwanCompiler",
  ) {
    preview {
      // Renders the captured gallery as SDK style documentation, with each preview's composable
      // name and parameter table beside the screenshot.
      sdkModeEnabled.set(true)
      // The cards load artwork over the network, so the capture waits for Coil rather than
      // photographing an empty frame.
      renderDelayMs.set(4000L)
    }
  }
}

composeStabilityAnalyzer {
  enabled.set(true)
}

android {
  namespace = "com.skydoves.pokedex.kmp"

  defaultConfig {
    applicationId = "com.skydoves.pokedex.kmp"
    versionCode = 1
    versionName = "1.0.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".debug"
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  packaging {
    resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
  }
}

dependencies {
  // Supplies HotSwanPreviewActivity, which `captureAllPreviews` launches over adb. It is not
  // added automatically, and without it every capture reports "Activity does not exist".
  if (hotSwanEnabled) {
    debugImplementation(libs.hotswan.preview)
  }

  implementation(projects.app.shared)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.profileinstaller)
  baselineProfile(projects.baselineprofile)
  implementation(libs.kotlinx.coroutines.android)
}
