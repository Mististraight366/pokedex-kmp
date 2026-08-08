import com.skydoves.pokedex.kmp.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.android.kotlin.multiplatform.library")
      pluginManager.apply("org.jetbrains.kotlin.multiplatform")
      pluginManager.apply("skydoves.pokedex.kmp.spotless")

      extensions.configure<KotlinMultiplatformExtension> { configureKotlinMultiplatform(this) }

      // AGP's lint tasks read KSP's generated source directories but do not declare the
      // dependency. Gradle 9 fails the build on that rather than warning, so the edge is added
      // here for whichever KSP tasks a module happens to register.
      val lintTasks = tasks.matching {
        (it.name.startsWith("generate") && it.name.endsWith("LintModel")) ||
          it.name.startsWith("lintAnalyze") ||
          it.name.startsWith("lintVitalAnalyze")
      }
      lintTasks.configureEach {
        dependsOn(tasks.matching { task -> task.name.startsWith("ksp") })
      }
    }
  }
}
