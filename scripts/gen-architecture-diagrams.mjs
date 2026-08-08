// Generates the .excalidraw sources for the architecture figures in the README.
// Run `npm run diagrams` to regenerate and export them to figure/*.png.
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const OUT_DIR = path.resolve(__dirname, "../figure");

let nextId = 1;
const id = () => `el-${nextId++}`;
// Excalidraw seeds only affect the hand-drawn jitter. A counter keeps every regeneration
// byte-identical, so a diagram only changes in git when its content actually changed.
let seedCounter = 1;
const seed = () => seedCounter++ * 7919;

const FONT = 16;
const LINE = 20;

function box(b) {
  const rectId = id();
  const textId = id();
  const lines = b.label.split("\n");
  const textH = lines.length * LINE;
  const rect = {
    id: rectId,
    type: "rectangle",
    x: b.x,
    y: b.y,
    width: b.w,
    height: b.h,
    angle: 0,
    strokeColor: b.stroke,
    backgroundColor: b.bg,
    fillStyle: "solid",
    strokeWidth: 2,
    roughness: 1,
    opacity: 100,
    groupIds: [],
    roundness: { type: 3 },
    seed: seed(),
    version: 1,
    versionNonce: seed(),
    isDeleted: false,
    boundElements: [{ id: textId, type: "text" }],
    updated: 1700000000000,
    link: null,
    locked: false,
  };
  const text = {
    id: textId,
    type: "text",
    x: b.x + 8,
    y: b.y + (b.h - textH) / 2,
    width: b.w - 16,
    height: textH,
    angle: 0,
    strokeColor: "#1e1e1e",
    backgroundColor: "transparent",
    fillStyle: "solid",
    strokeWidth: 2,
    roughness: 1,
    opacity: 100,
    groupIds: [],
    roundness: null,
    seed: seed(),
    version: 1,
    versionNonce: seed(),
    isDeleted: false,
    boundElements: null,
    updated: 1700000000000,
    link: null,
    locked: false,
    text: b.label,
    fontSize: FONT,
    fontFamily: 1,
    textAlign: "center",
    verticalAlign: "middle",
    containerId: rectId,
    originalText: b.label,
    lineHeight: 1.25,
  };
  return [rect, text];
}

function arrow(x1, y1, x2, y2, opts = {}) {
  return {
    id: id(),
    type: "arrow",
    x: x1,
    y: y1,
    width: Math.abs(x2 - x1),
    height: Math.abs(y2 - y1),
    angle: 0,
    strokeColor: opts.color ?? "#343a40",
    backgroundColor: "transparent",
    fillStyle: "solid",
    strokeWidth: 2,
    strokeStyle: opts.dashed ? "dashed" : "solid",
    roughness: 1,
    opacity: 100,
    groupIds: [],
    roundness: { type: 2 },
    seed: seed(),
    version: 1,
    versionNonce: seed(),
    isDeleted: false,
    boundElements: null,
    updated: 1700000000000,
    link: null,
    locked: false,
    points: [
      [0, 0],
      [x2 - x1, y2 - y1],
    ],
    lastCommittedPoint: null,
    startBinding: null,
    endBinding: null,
    startArrowhead: null,
    endArrowhead: "arrow",
  };
}

function label(x, y, text, opts = {}) {
  return {
    id: id(),
    type: "text",
    x,
    y,
    width: opts.width ?? 240,
    height: LINE * text.split("\n").length,
    angle: 0,
    strokeColor: opts.color ?? "#495057",
    backgroundColor: "transparent",
    fillStyle: "solid",
    strokeWidth: 2,
    roughness: 1,
    opacity: 100,
    groupIds: [],
    roundness: null,
    seed: seed(),
    version: 1,
    versionNonce: seed(),
    isDeleted: false,
    boundElements: null,
    updated: 1700000000000,
    link: null,
    locked: false,
    text,
    fontSize: opts.size ?? 14,
    fontFamily: 1,
    textAlign: opts.align ?? "left",
    verticalAlign: "top",
    containerId: null,
    originalText: text,
    lineHeight: 1.25,
  };
}

const PLATFORM = { bg: "#a5d8ff", stroke: "#1971c2" };
const UI = { bg: "#b2f2bb", stroke: "#2f9e44" };
const DATA = { bg: "#ffec99", stroke: "#f08c00" };
const FOUNDATION = { bg: "#e9ecef", stroke: "#495057" };
const EXTERNAL = { bg: "#ffc9c9", stroke: "#e03131" };

function write(name, elements) {
  const doc = {
    type: "excalidraw",
    version: 2,
    source: "pokedex-kmp",
    elements,
    appState: { gridSize: null, viewBackgroundColor: "#ffffff" },
    files: {},
  };
  fs.mkdirSync(OUT_DIR, { recursive: true });
  const out = path.join(OUT_DIR, `${name}.excalidraw`);
  fs.writeFileSync(out, JSON.stringify(doc, null, 2));
  console.log(`Written: ${out} (${elements.length} elements)`);
}

// figure0 — the two layers, and which one each platform sees.
function figure0() {
  nextId = 1;
  seedCounter = 1;
  const e = [];
  e.push(label(40, 24, "Pokedex KMP — architecture overview", { size: 20, color: "#1e1e1e" }));

  const apps = [
    { x: 40, y: 80, w: 170, h: 56, ...PLATFORM, label: "androidApp" },
    { x: 230, y: 80, w: 170, h: 56, ...PLATFORM, label: "iosApp" },
    { x: 420, y: 80, w: 170, h: 56, ...PLATFORM, label: "desktopApp" },
    { x: 610, y: 80, w: 170, h: 56, ...PLATFORM, label: "webApp" },
  ];
  apps.forEach((b) => e.push(...box(b)));

  const ui = { x: 40, y: 200, w: 740, h: 84, ...UI, label: "UI layer\napp/shared · app/ui-components\nscreens · ViewModels · navigation · design system" };
  const data = { x: 40, y: 344, w: 740, h: 84, ...DATA, label: "Data layer\ncore/data · core/network · core/database · core/datastore\nrepositories · Ktor + Sandwich · Room · preferences" };
  const model = { x: 40, y: 488, w: 740, h: 56, ...FOUNDATION, label: "core/model · core/common" };
  [ui, data, model].forEach((b) => e.push(...box(b)));

  apps.forEach((b) => e.push(arrow(b.x + b.w / 2, b.y + b.h, b.x + b.w / 2, ui.y)));
  e.push(arrow(410, ui.y + ui.h, 410, data.y));
  e.push(arrow(410, data.y + data.h, 410, model.y));

  e.push(label(40, 566, "Every arrow points at a dependency. The data layer never points back up:", { width: 740 }));
  e.push(label(40, 588, "it has no knowledge of the UI, which is what lets all four apps share it unchanged.", { width: 740 }));
  write("figure0", e);
}

// figure1 — unidirectional data flow.
function figure1() {
  nextId = 1;
  seedCounter = 1;
  const e = [];
  e.push(label(40, 24, "Unidirectional data flow", { size: 20, color: "#1e1e1e" }));

  const screen = { x: 40, y: 90, w: 220, h: 64, ...UI, label: "Composable screen\nPokedexHome" };
  const vm = { x: 320, y: 90, w: 220, h: 64, ...UI, label: "ViewModel\nHomeViewModel" };
  const repo = { x: 600, y: 90, w: 220, h: 64, ...DATA, label: "Repository\nHomeRepository" };
  const cache = { x: 600, y: 240, w: 220, h: 64, ...DATA, label: "Room / in-memory\nPokemonLocalDataSource" };
  const api = { x: 600, y: 360, w: 220, h: 64, ...EXTERNAL, label: "PokeAPI\nKtor + Sandwich" };
  [screen, vm, repo, cache, api].forEach((b) => e.push(...box(b)));

  e.push(arrow(screen.x + screen.w, 112, vm.x, 112));
  e.push(label(268, 86, "events", { width: 60, size: 12 }));
  e.push(arrow(vm.x, 138, screen.x + screen.w, 138));
  e.push(label(268, 140, "state", { width: 60, size: 12 }));

  e.push(arrow(vm.x + vm.w, 112, repo.x, 112));
  e.push(arrow(repo.x + repo.w / 2, repo.y + repo.h, cache.x + cache.w / 2, cache.y));
  e.push(arrow(cache.x + cache.w / 2, cache.y + cache.h, api.x + api.w / 2, api.y, { dashed: true }));
  e.push(label(830, 268, "cache first", { width: 140, size: 12 }));
  e.push(label(830, 388, "only on a miss", { width: 140, size: 12 }));

  e.push(label(40, 460, "The repository answers from the cache and reaches the network only when the requested", { width: 900 }));
  e.push(label(40, 482, "page is missing, so a warm launch renders without a request. Each call returns one", { width: 900 }));
  e.push(label(40, 504, "PokemonPageResult, which makes success and failure mutually exclusive by construction.", { width: 900 }));
  write("figure1", e);
}

// figure2 — how the shared UI reaches each platform.
function figure2() {
  nextId = 1;
  seedCounter = 1;
  const e = [];
  e.push(label(40, 24, "One UI, four entry points", { size: 20, color: "#1e1e1e" }));

  const shared = { x: 300, y: 90, w: 300, h: 72, ...UI, label: "App(appGraph)\ncommonMain" };
  e.push(...box(shared));

  const entries = [
    { x: 40, y: 250, w: 190, h: 84, ...PLATFORM, label: "MainActivity\nsetContent { App() }" },
    { x: 250, y: 250, w: 190, h: 84, ...PLATFORM, label: "MainViewController\nComposeUIViewController" },
    { x: 460, y: 250, w: 190, h: 84, ...PLATFORM, label: "main()\nWindow { App() }" },
    { x: 670, y: 250, w: 190, h: 84, ...PLATFORM, label: "main()\nComposeViewport" },
  ];
  entries.forEach((b) => e.push(...box(b)));
  entries.forEach((b) => e.push(arrow(450, shared.y + shared.h, b.x + b.w / 2, b.y)));

  const graphs = [
    { x: 40, y: 390, w: 190, h: 56, ...DATA, label: "AndroidAppGraph" },
    { x: 250, y: 390, w: 190, h: 56, ...DATA, label: "IosAppGraph" },
    { x: 460, y: 390, w: 190, h: 56, ...DATA, label: "DesktopAppGraph" },
    { x: 670, y: 390, w: 190, h: 56, ...DATA, label: "WebAppGraph" },
  ];
  graphs.forEach((b) => e.push(...box(b)));
  entries.forEach((b, i) => e.push(arrow(b.x + b.w / 2, b.y + b.h, graphs[i].x + graphs[i].w / 2, graphs[i].y)));

  e.push(label(40, 480, "Each platform builds its own Metro dependency graph, because only it knows how to reach", { width: 900 }));
  e.push(label(40, 502, "a database file or an Android Context. Everything above that interface is written once.", { width: 900 }));
  write("figure2", e);
}

// figure3 — where the platforms genuinely diverge.
function figure3() {
  nextId = 1;
  seedCounter = 1;
  const e = [];
  e.push(label(40, 24, "Where the platforms diverge", { size: 20, color: "#1e1e1e" }));

  const rows = [
    ["HTTP engine", "OkHttp", "Darwin", "OkHttp", "Js (fetch)"],
    ["Cache", "Room", "Room", "Room", "in-memory"],
    ["Preferences", "SharedPreferences", "NSUserDefaults", "java.util.prefs", "localStorage"],
    ["IO dispatcher", "Dispatchers.IO", "Dispatchers.Default", "Dispatchers.IO", "Dispatchers.Default"],
  ];

  const headers = ["", "Android", "iOS", "Desktop", "Web"];
  const colX = [40, 240, 410, 580, 750];
  const colW = [190, 160, 160, 160, 160];

  headers.forEach((h, i) => {
    if (i === 0) return;
    e.push(...box({ x: colX[i], y: 80, w: colW[i], h: 44, ...PLATFORM, label: h }));
  });

  rows.forEach((row, r) => {
    const y = 140 + r * 64;
    e.push(...box({ x: colX[0], y, w: colW[0], h: 52, ...FOUNDATION, label: row[0] }));
    for (let c = 1; c < row.length; c++) {
      const isFallback = row[c] === "in-memory" || row[c] === "Dispatchers.Default";
      e.push(...box({ x: colX[c], y, w: colW[c], h: 52, ...(isFallback ? EXTERNAL : DATA), label: row[c] }));
    }
  });

  e.push(label(40, 420, "Four expect/actual declarations, and nothing else. Red marks a deliberate fallback:", { width: 900 }));
  e.push(label(40, 442, "Room has no browser driver, and Dispatchers.IO does not exist on Kotlin/Native.", { width: 900 }));
  write("figure3", e);
}

// figure4 — the module graph.
function figure4() {
  nextId = 1;
  seedCounter = 1;
  const e = [];
  e.push(label(40, 24, "Modularization", { size: 20, color: "#1e1e1e" }));

  const androidApp = { x: 40, y: 80, w: 160, h: 48, ...PLATFORM, label: "androidApp" };
  const iosApp = { x: 220, y: 80, w: 160, h: 48, ...PLATFORM, label: "iosApp" };
  const desktopApp = { x: 400, y: 80, w: 160, h: 48, ...PLATFORM, label: "desktopApp" };
  const webApp = { x: 580, y: 80, w: 160, h: 48, ...PLATFORM, label: "webApp" };
  const screenshot = { x: 760, y: 80, w: 160, h: 48, ...FOUNDATION, label: "screenshot" };
  const baseline = { x: 940, y: 80, w: 160, h: 48, ...FOUNDATION, label: "baselineprofile" };

  const shared = { x: 220, y: 180, w: 340, h: 56, ...UI, label: "app/shared" };
  const uiComponents = { x: 620, y: 180, w: 300, h: 56, ...UI, label: "app/ui-components" };

  const data = { x: 220, y: 290, w: 340, h: 56, ...DATA, label: "core/data" };
  const network = { x: 40, y: 390, w: 200, h: 56, ...DATA, label: "core/network" };
  const database = { x: 260, y: 390, w: 200, h: 56, ...DATA, label: "core/database" };
  const datastore = { x: 480, y: 390, w: 200, h: 56, ...DATA, label: "core/datastore" };
  const test = { x: 700, y: 390, w: 200, h: 56, ...FOUNDATION, label: "core/test" };

  const model = { x: 40, y: 500, w: 420, h: 52, ...FOUNDATION, label: "core/model" };
  const common = { x: 480, y: 500, w: 420, h: 52, ...FOUNDATION, label: "core/common" };

  [androidApp, iosApp, desktopApp, webApp, screenshot, baseline, shared, uiComponents,
    data, network, database, datastore, test, model, common].forEach((b) => e.push(...box(b)));

  [androidApp, iosApp, desktopApp, webApp].forEach((b) =>
    e.push(arrow(b.x + b.w / 2, b.y + b.h, shared.x + shared.w / 2, shared.y)),
  );
  e.push(arrow(screenshot.x + screenshot.w / 2, screenshot.y + screenshot.h, uiComponents.x + uiComponents.w / 2, uiComponents.y));
  e.push(arrow(shared.x + shared.w, 208, uiComponents.x, 208));
  e.push(arrow(shared.x + shared.w / 2, shared.y + shared.h, data.x + data.w / 2, data.y));
  [network, database, datastore].forEach((b) =>
    e.push(arrow(data.x + data.w / 2, data.y + data.h, b.x + b.w / 2, b.y)),
  );
  e.push(arrow(network.x + network.w / 2, network.y + network.h, model.x + model.w / 2, model.y));
  e.push(arrow(datastore.x + datastore.w / 2, datastore.y + datastore.h, common.x + common.w / 2, common.y));
  e.push(arrow(test.x + test.w / 2, test.y + test.h, common.x + common.w / 2, common.y, { dashed: true }));

  e.push(label(40, 580, "core/test is a test-only dependency, so the fake repositories it holds can never be bound", { width: 1100 }));
  e.push(label(40, 602, "into a real graph. Sample data that previews need lives in core/model instead.", { width: 1100 }));
  write("figure4", e);
}

figure0();
figure1();
figure2();
figure3();
figure4();
