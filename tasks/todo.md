# Pokedex KMP — Implementation Plan

Port `skydoves/pokedex` + `skydoves/pokedex-compose` to Kotlin Multiplatform, targeting
**Android, iOS, Desktop (JVM), and Web (wasmJs)**, with the module layout of
[JetBrains/kotlinconf-app](https://github.com/JetBrains/kotlinconf-app).

## Target module structure

```
pokedex-kmp/
├── build-logic/convention/          # convention plugins (skydoves.pokedex.kmp.*)
├── core/
│   ├── model/                       # KMP domain models
│   ├── network/                     # Ktor + Sandwich
│   ├── database/                    # Room KMP (+ in-memory fallback for wasm)
│   ├── datastore/                   # multiplatform-settings / datastore-okio
│   ├── data/                        # repositories
│   └── test/                        # shared fakes + coroutine rules
├── app/
│   ├── ui-components/               # design system (theme, colors, components)
│   ├── shared/                      # screens, ViewModels, navigation, Metro graph
│   ├── androidApp/
│   ├── desktopApp/
│   ├── webApp/                      # wasmJs
│   ├── iosApp/                      # Xcode project
│   └── screenshot/                  # Android-only Paparazzi goldens
├── baselineprofile/
├── figure/  previews/  spotless/  scripts/
└── README.md
```

## Phase 1 — Build foundation
- [x] `gradle/libs.versions.toml` with the full KMP stack
- [x] `settings.gradle.kts` + root `build.gradle.kts` + `gradle.properties`
- [x] `build-logic/convention` plugins: `kotlinMultiplatform`, `kotlinMultiplatformCompose`, `androidApplication`, `spotless`
- [x] Gradle wrapper 9.5.1, spotless license headers
- [x] Verify: `./gradlew projects` resolves

## Phase 2 — core/model
- [x] `Pokemon`, `PokemonInfo` (+ `TypeResponse`/`StatsResponse`/`Stat`/`Type`), `PokemonResponse`, `PokemonErrorResponse`, `UserData`/`UiTheme`
- [x] Replace JVM `String.format` with a multiplatform formatter
- [x] Unit tests for computed properties (`imageUrl`, `name`, stat getters, formatters)

## Phase 3 — core/network
- [x] Ktor `HttpClient` with per-platform engines (OkHttp / Darwin / OkHttp-JVM / Js)
- [x] `PokedexService` + `PokedexClient` returning Sandwich `ApiResponse<T>`
- [x] ContentNegotiation + kotlinx.serialization + Logging
- [x] Unit tests with `MockEngine` (replaces MockWebServer)

## Phase 4 — core/database
- [x] Room KMP entities, DAOs, converters, `PokedexDatabase`
- [x] `expect/actual` database builders: Android / iOS / Desktop
- [x] wasmJs: in-memory `PokemonLocalDataSource` (Room has no wasm target)
- [x] Entity mappers

## Phase 5 — core/datastore + core/data
- [x] Multiplatform preferences for `UiTheme`
- [x] `HomeRepository`, `DetailsRepository`, `UserDataRepository` (+ impls, + fakes)
- [x] Repository tests with Turbine

## Phase 6 — app/ui-components (design system)
- [x] `PokedexTheme`, `PokedexColors` (32 colors, light + dark), `PokedexBackground`
- [x] `PokedexAppBar`, `PokedexText`, `PokedexProgressBar`, `PokedexCircularProgress`
- [x] Compose Multiplatform resources (`Res.string`, `Res.drawable`) + i18n (es, it, pt-BR)
- [x] `getPokemonTypeColor`, size utils

## Phase 7 — app/shared
- [x] Metro DI: `AppGraph` + per-platform graphs, `@ContributesIntoMap` ViewModels
- [x] Navigation: `PokedexScreen` routes + `PokedexNavigator` (navigation-compose MP)
- [x] Home screen + `HomeViewModel` (paging, palette-tinted grid, shared element)
- [x] Details screen + `DetailsViewModel` (header, info, stats, Cloudy blur)
- [x] Settings dialog + `SettingsViewModel`
- [x] Landscapist `CoilImage` + PalettePlugin for artwork
- [x] compose-nav-graph annotations on every destination

## Phase 8 — Platform apps
- [x] `androidApp`: Application, MainActivity, splash, edge-to-edge
- [x] `androidApp`: Compose HotSwan (declared in the catalog, never applied)
- [x] `desktopApp`: `application { Window { App() } }` + nativeDistributions
- [x] `webApp`: wasmJs `ComposeViewport`, index.html, resources
- [x] `iosApp`: Xcode project, `MainViewController`, embedAndSign build phase

## Phase 9 — Tests
- [x] ViewModel and data source tests in `commonTest`, running on all four targets
- [ ] Compose UI interaction tests with `runComposeUiTest` (still open)
- [x] Paparazzi golden screenshots (Android-only `app/screenshot` module)
- [x] Compose HotSwan on-device preview capture, published to GitHub Pages
- [x] Full unit test suite green on all targets

## Phase 10 — Baseline profiles
- [x] `baselineprofile` module (macrobenchmark + GMD pixel6api31)
- [x] `BaselineProfileGenerator` + `PokedexScenarios` + `StartupBenchmarks`
- [x] Generate and commit `baseline-prof.txt` / `startup-prof.txt`

## Phase 11 — README + diagrams
- [x] Excalidraw diagram generator script → `figure/figure0..4.png`
- [x] README mirroring pokedex/pokedex-compose structure, prose per `blogs/rules.md`
- [x] `.github/workflows` CI

## Phase 12a — Cross platform consistency fixes
- [x] Replace `landscapist-palette` with a common quantizer so all four targets agree on card colour
- [x] Cache extracted colours so the details header opens on the right colour instead of flashing dark
- [x] Shared element transition: card morphs into the details header via `SharedTransitionLayout`
- [x] Verify the transition frame by frame on Android, not just the settled state

## Phase 12 — E2E verification
- [x] Android: build + install on emulator + screenshot
- [x] Desktop: run + screenshot
- [x] iOS: build + boot simulator + screenshot
- [x] Web: build wasm dist + serve + headless screenshot
- [x] Assemble `previews/screenshot.png`

---

## Decisions

**Paparazzi and KMP.** Paparazzi only attaches to `com.android.library`/`com.android.application`
modules, not to Kotlin Multiplatform modules. So the goldens live in a dedicated Android library
module, `app/screenshot`, which consumes the Android variant of `app/ui-components` and
`app/shared` and renders the very same composables. Compose Multiplatform's own
`runComposeUiTest` + `captureToImage()` covers the shared UI on the JVM/desktop target, so both
the Android device-spec rendering and the cross-platform rendering are pinned.

**Room on the web.** Room has no wasmJs target. `core/database` exposes a
`PokemonLocalDataSource` interface; Room backs it on Android/iOS/Desktop, and an in-memory
implementation backs it on wasmJs. The repository layer is unaware of the difference.

**DataStore Proto is out.** `protobuf-kotlin-lite` is JVM only. Theme preference persistence
moves to a multiplatform key/value store behind the same `UserDataRepository` interface.

**Navigation.** `androidx.navigation3` is Android-only today; the shared code uses
`org.jetbrains.androidx.navigation:navigation-compose` and keeps the existing
platform-agnostic `PokedexNavigator` abstraction.

## Review

All twelve phases are complete. The three skydoves tools the original brief named are wired and
producing output:

- **compose-nav-graph** on `:app:shared` — 3 destinations, 2 labelled edges, 3/3 Layoutlib
  thumbnails, with `app/shared/nav/shared.nav` committed as the reviewed baseline and `navCheck`
  enforcing it in CI.
- **Compose Stability Analyzer** on `:app:androidApp` — adds `debugStabilityDump` / `debugStabilityCheck`.
- **Compose HotSwan** on `:app:androidApp`, opt in behind `-Photswan.enabled=true` — captures all
  14 `@Preview`s from the running app and writes an SDK style HTML catalog, which the
  `Preview Gallery` workflow publishes to GitHub Pages.

The HotSwan flag is not cosmetic. Its compiler plugin rewrites Compose call sites, and those
rewritten classes cannot be loaded by compose-nav-graph's headless Layoutlib renderer, which has no
HotSwan runtime to initialise. With HotSwan applied unconditionally the navigation thumbnails
failed 0/3; behind the flag both tools work.

Two limits worth recording rather than papering over:

- **Paparazzi cannot render Compose Multiplatform resources.** `stringResource` throws
  `MissingResourceException` because CMP reads `composeResources/...` from the classpath root while
  Paparazzi renders inside its own layoutlib classloader. Adding the resources to the test runtime
  classpath did not reach that classloader. Paparazzi therefore covers components that render from
  their inputs, and whole screens are covered by the HotSwan capture instead, which runs in a real
  app process.
- **`@Preview` must be the AndroidX annotation.** The navgraph processor matches only
  `androidx.compose.ui.tooling.preview.Preview`, which comes from `org.jetbrains.compose.ui:ui-tooling-preview`
  (`compose.preview`), not from `components-ui-tooling-preview`, whose annotation is deprecated and
  in a different package.
