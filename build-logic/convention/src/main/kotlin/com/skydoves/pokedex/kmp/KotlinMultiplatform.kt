package com.skydoves.pokedex.kmp

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

/**
 * The Android namespace every module derives from its Gradle path, so no module has to repeat it.
 * `:core:model` becomes `com.skydoves.pokedex.kmp.core.model`, `:app:ui-components` becomes
 * `com.skydoves.pokedex.kmp.app.uicomponents`.
 */
internal val Project.derivedNamespace: String
  get() = "com.skydoves.pokedex.kmp" + path.replace(":", ".").replace("-", "")

/**
 * Declares the four target platforms and the two intermediate source sets that carry the
 * dependencies shared by some, but not all, of them.
 *
 * Since AGP 9 the Android side of a multiplatform module comes from
 * `com.android.kotlin.multiplatform.library`, which contributes an `android` target to the `kotlin`
 * extension instead of a separate `android { }` block. Its test compilations are opt in, so
 * [withHostTest] and [withDeviceTest] are what create `androidHostTest` and `androidDeviceTest`.
 */
internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension) =
  extension.apply {
    val jvmTargetVersion = libs.version("jvmTarget")

    androidTarget(this@configureKotlinMultiplatform, jvmTargetVersion)

    jvm("desktop") {
      compilerOptions { jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion)) }
    }

    // No iosX64: Room 3 and androidx.sqlite 2.7.0 stopped publishing that target, and an Intel
    // simulator is not something this app needs to support.
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    compilerOptions {
      freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }

    with(sourceSets) {
      all {
        languageSettings.optIn("kotlin.RequiresOptIn")
      }

      // Everything except the browser: Room, the file system, and the OkHttp/Darwin engines.
      val nonWebMain = create("nonWebMain") { dependsOn(getByName("commonMain")) }
      val nonWebTest = create("nonWebTest") { dependsOn(getByName("commonTest")) }
      dependOn(nonWebMain, "androidMain", "iosMain", "desktopMain")
      dependOn(nonWebTest, "androidHostTest", "iosTest", "desktopTest")

      // Everything Skiko renders: no Android framework, no `android.content.Context`.
      val nonAndroidMain = create("nonAndroidMain") { dependsOn(getByName("commonMain")) }
      dependOn(nonAndroidMain, "iosMain", "desktopMain", "wasmJsMain")
    }
  }

/**
 * Wires [parent] into each named source set that exists. Test source sets only materialise once
 * `withHostTest`/`withDeviceTest` have run, so a missing name is skipped rather than failing.
 */
private fun org.gradle.api.NamedDomainObjectContainer<KotlinSourceSet>.dependOn(
  parent: KotlinSourceSet,
  vararg names: String,
) = names.forEach { name -> findByName(name)?.dependsOn(parent) }

private fun KotlinMultiplatformExtension.androidTarget(project: Project, jvmTargetVersion: String) {
  val android = (this as ExtensionAware).extensions
    .getByName("android") as KotlinMultiplatformAndroidLibraryTarget

  android.apply {
    namespace = project.derivedNamespace
    compileSdk = project.libs.version("androidCompileSdk").toInt()
    minSdk = project.libs.version("androidMinSdk").toInt()
    compilerOptions { jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion)) }
    androidResources.enable = true
    lint { abortOnError = false }

    withHostTest {
      isIncludeAndroidResources = true
      isReturnDefaultValues = true
    }
    withDeviceTest {
      instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
  }
}
