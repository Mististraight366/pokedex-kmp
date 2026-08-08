plugins {
  `kotlin-dsl`
}

group = "com.skydoves.pokedex.kmp.buildlogic"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.compose.gradlePlugin)
  compileOnly(libs.compose.compiler.gradlePlugin)
  compileOnly(libs.spotless.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("kotlinMultiplatform") {
      id = "skydoves.pokedex.kmp.multiplatform"
      implementationClass = "KotlinMultiplatformConventionPlugin"
    }
    register("kotlinMultiplatformCompose") {
      id = "skydoves.pokedex.kmp.multiplatform.compose"
      implementationClass = "KotlinMultiplatformComposeConventionPlugin"
    }
    register("androidApplication") {
      id = "skydoves.pokedex.kmp.android.application"
      implementationClass = "AndroidApplicationConventionPlugin"
    }
    register("spotless") {
      id = "skydoves.pokedex.kmp.spotless"
      implementationClass = "SpotlessConventionPlugin"
    }
  }
}
