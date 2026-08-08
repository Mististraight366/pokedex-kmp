@file:Suppress("UnstableApiUsage")

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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    google {
      content {
        includeGroupByRegex("androidx\\..*")
        includeGroupByRegex("com\\.android(\\..*|)")
        includeGroupByRegex("com\\.google\\.android\\..*")
        includeGroupByRegex("com\\.google\\.testing\\.platform")
      }
      mavenContent { releasesOnly() }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  // No `repositoriesMode` lock: the Kotlin wasm/js plugin registers its own Node.js distribution
  // repository on the root project, which either of the strict modes would break.
  repositories {
    google {
      content {
        includeGroupByRegex("androidx\\..*")
        includeGroupByRegex("com\\.android(\\..*|)")
        includeGroupByRegex("com\\.google\\.android\\..*")
        includeGroupByRegex("com\\.google\\.testing\\.platform")
      }
      mavenContent { releasesOnly() }
    }
    mavenCentral()
  }
}

rootProject.name = "pokedex-kmp"

include(":core:common")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":core:test")

include(":app:ui-components")
include(":app:shared")
include(":app:androidApp")
include(":app:desktopApp")
include(":app:webApp")
include(":app:screenshot")

include(":baselineprofile")
