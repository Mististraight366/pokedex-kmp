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
  alias(libs.plugins.android.test)
  alias(libs.plugins.baselineprofile)
  id("skydoves.pokedex.kmp.spotless")
}

android {
  namespace = "com.skydoves.pokedex.kmp.baselineprofile"
  compileSdk = libs.versions.androidCompileSdk.get().toInt()

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  defaultConfig {
    minSdk = libs.versions.androidMinSdk.get().toInt()
    targetSdk = libs.versions.androidTargetSdk.get().toInt()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app:androidApp"

  testOptions.managedDevices.localDevices {
    maybeCreate("pixel6api31").apply {
      device = "Pixel 6"
      apiLevel = 31
      systemImageSource = "aosp"
    }
  }
}

baselineProfile {
  // A Gradle managed device keeps profile generation reproducible in CI, where no physical
  // device is attached.
  managedDevices += "pixel6api31"
  useConnectedDevices = false
}

dependencies {
  implementation(libs.androidx.test.junit)
  implementation(libs.androidx.test.espresso)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
  onVariants { variant ->
    val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
    variant.instrumentationRunnerArguments.put(
      "targetAppId",
      variant.testedApks.map { artifactsLoader.load(it)?.applicationId },
    )
  }
}
