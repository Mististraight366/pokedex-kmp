// Serves the built wasm distribution and captures a screenshot of the running app.
import puppeteer from "puppeteer-core";
import http from "http";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const DIST = path.resolve(__dirname, "../app/webApp/build/dist/wasmJs/productionExecutable");
const OUT = path.resolve(__dirname, "../previews/screenshot-web.png");
const CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
const PORT = 18391;

const MIME = {
  ".html": "text/html",
  ".js": "application/javascript",
  ".mjs": "application/javascript",
  ".wasm": "application/wasm",
  ".json": "application/json",
  ".png": "image/png",
  ".xml": "application/xml",
  ".map": "application/json",
  ".ttf": "font/ttf",
  ".otf": "font/otf",
};

const server = http.createServer((req, res) => {
  const urlPath = decodeURIComponent(req.url.split("?")[0]);
  const filePath = path.join(DIST, urlPath === "/" ? "index.html" : urlPath);
  if (!filePath.startsWith(DIST) || !fs.existsSync(filePath) || fs.statSync(filePath).isDirectory()) {
    res.writeHead(404);
    res.end("not found");
    return;
  }
  res.writeHead(200, { "Content-Type": MIME[path.extname(filePath)] ?? "application/octet-stream" });
  fs.createReadStream(filePath).pipe(res);
});

await new Promise((resolve) => server.listen(PORT, resolve));
console.log(`serving ${DIST} on http://localhost:${PORT}`);

const browser = await puppeteer.launch({
  executablePath: CHROME,
  headless: "new",
  args: ["--no-sandbox", "--disable-setuid-sandbox", "--enable-features=WebAssemblyJSPromiseIntegration"],
});
const page = await browser.newPage();
await page.setViewport({ width: 480, height: 900, deviceScaleFactor: 2 });

const logs = [];
page.on("console", (m) => logs.push(`[${m.type()}] ${m.text()}`));
page.on("pageerror", (e) => logs.push(`[pageerror] ${e.message}`));

await page.goto(`http://localhost:${PORT}/`, { waitUntil: "networkidle2", timeout: 120000 });

// The Compose canvas replaces the splash div once the wasm module has started.
await page
  .waitForFunction(() => document.querySelector("canvas") !== null, { timeout: 120000 })
  .catch(() => console.log("no canvas appeared"));

const waitFor = Number(process.env.SETTLE_MS ?? 12000);
await new Promise((r) => setTimeout(r, waitFor));

fs.mkdirSync(path.dirname(OUT), { recursive: true });
await page.screenshot({ path: OUT });
console.log(`wrote ${OUT}`);
console.log("--- browser logs ---");
console.log(logs.slice(0, 40).join("\n"));

await browser.close();
server.close();
