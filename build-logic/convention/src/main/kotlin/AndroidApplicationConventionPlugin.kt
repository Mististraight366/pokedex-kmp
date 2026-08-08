import com.android.build.api.dsl.ApplicationExtension
import com.skydoves.pokedex.kmp.libs
import com.skydoves.pokedex.kmp.version
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.android.application")
      pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
      pluginManager.apply("skydoves.pokedex.kmp.spotless")

      extensions.configure<ApplicationExtension> {
        compileSdk = libs.version("androidCompileSdk").toInt()

        defaultConfig {
          minSdk = libs.version("androidMinSdk").toInt()
          targetSdk = libs.version("androidTargetSdk").toInt()
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

      // AGP 9 ships built in Kotlin support, so there is no separate kotlin-android plugin to
      // apply and the Kotlin extension it registers is what carries the JVM target.
      extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
          jvmTarget.set(JvmTarget.fromTarget(libs.version("jvmTarget")))
        }
      }
    }
  }
}
