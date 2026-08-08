# kotlinx.serialization resolves these models reflectively through their Companion, because Ktor's
# ContentNegotiation looks the serializer up by KType. Everything else serialization needs comes
# from kotlinx-serialization-core's own META-INF/com.android.tools/r8 rules.
-keepclassmembers class com.skydoves.pokedex.kmp.** { *** Companion; }
-keepclasseswithmembers class com.skydoves.pokedex.kmp.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# UiTheme is persisted by enum name, so obfuscating the constants would reset a saved theme on the
# first update after release.
-keepclassmembers enum com.skydoves.pokedex.kmp.core.model.UiTheme { *; }

# Ktor's JVM utils reference java.lang.management, which does not exist on Android. R8 shrinks that
# path away today; this keeps a future Ktor version from turning it into a hard failure.
-dontwarn java.lang.management.**

# Room 3 ships its own consumer rules (it keeps the generated RoomDatabase constructor). A blanket
# `-keep class androidx.room3.** { *; }` here previously accounted for 3,244 of 8,548 keep seeds
# and stopped anything in the persistence layer from being shrunk at all.
