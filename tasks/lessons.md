# Lessons

## Never assume a multiplatform library behaves the same on every target

**What happened.** Card background colours differed between Android and iOS, and the web had none
at all. I had reached for `landscapist-palette`'s `PalettePlugin` and assumed "it is a KMP artifact"
meant "it does the same thing everywhere".

It does not. `landscapist-palette` ships `androidMain` and `commonMain` source sets: AndroidX
Palette on Android, a different quantizer elsewhere. Its `wasm-js` klib is 597 bytes, an empty
shell. Three platforms, three behaviours, from one dependency.

**Rule for next time.** Before depending on a KMP library for anything whose *output* is
user-visible, check two things:

1. `ls <lib>/src/` in the source repo. More than `commonMain` means per platform behaviour, not
   just per platform plumbing.
2. The published klib size per target. `curl -sIL <maven>/<lib>-wasm-js/<v>/<lib>-wasm-js-<v>.klib`
   and read `content-length`. A few hundred bytes means the target is published but empty.

An artifact existing for a target is not evidence that the feature works on that target.

**How to apply.** When platform results must agree exactly, implement the logic in `commonMain`
rather than delegating to a library that resolves differently per target. Here that meant a hand
rolled quantizer in `ArtworkPalette.kt`: ~120 lines, identical on all four platforms, and it also
gave the web a feature it previously did not have.

## Shared elements break `matchParentSize()`

**What happened.** Adding `Modifier.sharedElement` to the artwork made it vanish mid transition
instead of animating. While a shared element is in flight Compose draws it in the
`SharedTransitionLayout` overlay, where there is no parent to match, so `matchParentSize()`
collapsed the image to zero. `fillMaxSize()` measures against the constraints it is handed and
works in both places.

**Also.** Do not make an async-loading image its own shared element. Re-composing `CoilImage` in
the overlay restarts the request and blanks the frame. Put `sharedBounds` on the container that
owns the image and let the artwork ride along with the bounds.

**Rule for next time.** Any modifier that resolves against the parent's placement
(`matchParentSize`, `align` in some cases) is suspect inside a shared element. Verify a transition
by recording it and stepping frames, never by looking at the settled state:

    adb shell screenrecord --time-limit 4 --size 720x1600 /sdcard/t.mp4
    ffmpeg -i t.mp4 -vf "fps=15,scale=170:-1,tile=8x3" -frames:v 1 sheet.png

The settled state was correct in every broken version of this transition.

## Verify on every target, not on the one that is quickest to run

Both bugs were invisible on desktop, which is the fastest platform to iterate on. The colour
mismatch only shows when Android and iOS screenshots are compared side by side. Budget a real
run on all four targets before calling a UI change done.

## Callbacks that report progress out of band cannot express failure

**What happened.** `HomeRepository`/`DetailsRepository` returned `Flow<List<T>>` plus
`onStart`/`onComplete`/`onError` callbacks. `onError` fired from inside the flow and `onComplete`
fired from `onCompletion` the instant the flow terminated. Both wrote the same
`MutableStateFlow<UiState>`, which conflates, so `Error` was overwritten by `Idle` before any
collector could observe it.

The user-visible result: offline, the details screen showed a spinner that never resolved and
offered no retry, and the home screen showed an empty grid with no explanation. `HomeUiState.Error`
and `DetailsUiState.Error` were unreachable code in a shipping app.

**Rule for next time.** If success and failure are mutually exclusive, model them in one return
value, not in two callbacks that race. `Flow<PokemonPageResult>` where the result is
`Success | Failure` made the bug impossible to reintroduce, and the error branch became reachable
enough that a test could assert on it.

**Smell to watch for.** Any `onCompletion { setState(Idle) }` sitting downstream of something that
can set an error state. `onCompletion` runs on success *and* failure, so it always wins.

## Side effects in a composable body run once per item, not once

**What happened.** Pagination was triggered from inside a `LazyVerticalGrid` item lambda. Every
trailing item composed in the same frame called it, and the `!= Loading` guard could not close in
time because the state only flips after a dispatch to the IO dispatcher. The page counter jumped by
6-8 at once and every page in between was silently never fetched: roughly half the Pokédex became
unreachable.

**Rule for next time.** A composable body is not a callback. Drive scroll-triggered work from
`snapshotFlow`/`derivedStateOf` on the list state inside a `LaunchedEffect`, and make the guard
close *synchronously* in the caller (`compareAndSet`), never via state that arrives asynchronously.

## Never advance a cursor before the outcome is known

`page.value += 1` ran before the fetch resolved, so a failed page was skipped permanently, leaving a
20-item hole no amount of retrying could heal. Advance on success only, and keep the failed page as
the next request so a retry is just calling the same function again.

## A `@Composable` getter that allocates is a recomposition trap

`CoilImageState.Success.imageBitmap` is a `@Composable` getter that decodes a fresh `ImageBitmap`
every call and does not `remember`. Keying a `LaunchedEffect` on it restarted the effect every
composition; the effect wrote state; the write invalidated the composable; repeat. Roughly a
megabyte re-decoded per card per frame, and the composition never went idle, which would also hang
any `waitForIdle()` UI test.

**Rule for next time.** Before keying an effect on a value, check whether the expression that
produces it is stable. If it comes from a `@Composable` getter, assume it is not, and either
`remember` it against something that *is* stable or gate the read behind a latch so it stops being
read once the work is done.

## Randomness in a data class constructor leaks into equality and serialization

`val exp: Int = Random.nextInt(MAX_EXP)` as a constructor default put a nondeterministic value into
`equals`/`hashCode` (so two instances decoded from identical JSON were unequal, defeating
`StateFlow` conflation and Compose skipping) and into the serial descriptor as an optional field
(so `encodeToString` silently dropped it). Deriving it from the id via `Random(id)` as a computed
property removed it from all three at once.

## SQLite guarantees nothing about row order

`SELECT * FROM PokemonEntity WHERE page <= :page` returned rows in insertion order *by luck*.
`INSERT OR REPLACE` deletes and re-inserts, moving a replaced row to the end, and the grid uses
`animateItem`, so a refetch would visibly reshuffle. Always `ORDER BY` an explicit column, and make
any in-memory stand-in sort by the same key, or the two diverge exactly where the fake is supposed
to prove the real one correct.

## Verify by opening the screen, not by assuming it works

The Settings screen was registered with `composable<>` instead of `dialog<>`. It rendered pinned to
the top-left with no scrim, over a Home screen that had been fully replaced. It had never been
screenshotted, so it survived every previous "verified on four platforms" claim.

**Rule for next time.** "Verified" means every screen was opened and looked at. List the screens
first, then check them off.

## Kotlin/Native rejects commas in backtick test names

`fun \`a failed page is retried, not skipped\`()` compiles on JVM and fails on iOS with
`Name contains illegal characters: ","` because the name becomes an Objective-C selector. Caught it
twice in one session. Guard before building:

    grep -rn --include='*.kt' 'fun `[^`]*,[^`]*`' . | grep -v /build/
