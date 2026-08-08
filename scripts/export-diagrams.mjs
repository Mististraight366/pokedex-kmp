import puppeteer from "puppeteer-core";
import http from "http";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const DIAGRAMS_DIR = path.resolve(__dirname, "../figure");
const CHROME_PATH =
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
const UTILS_BUNDLE = path.resolve(
  __dirname,
  "node_modules/@excalidraw/utils/dist/prod/index.js"
);

// Spin up a local server to serve the bundle and HTML page
function startServer(port) {
  return new Promise((resolve) => {
    const server = http.createServer((req, res) => {
      if (req.url === "/") {
        res.writeHead(200, { "Content-Type": "text/html" });
        res.end(`
          <!DOCTYPE html>
          <html>
          <head><meta charset="utf-8" /></head>
          <body>
            <script type="module">
              import { exportToSvg } from "/bundle.js";
              window.__exportToSvg = exportToSvg;
              window.__ready = true;
            </script>
          </body>
          </html>
        `);
      } else if (req.url === "/bundle.js") {
        res.writeHead(200, { "Content-Type": "application/javascript" });
        fs.createReadStream(UTILS_BUNDLE).pipe(res);
      } else {
        res.writeHead(404);
        res.end();
      }
    });
    server.listen(port, () => resolve(server));
  });
}

async function exportDiagrams() {
  const files = fs
    .readdirSync(DIAGRAMS_DIR)
    .filter((f) => f.endsWith(".excalidraw"));

  if (files.length === 0) {
    console.log("No .excalidraw files found.");
    return;
  }

  console.log(`Found ${files.length} diagrams to export.\n`);

  const PORT = 18293;
  const server = await startServer(PORT);
  console.log(`Local server running on port ${PORT}`);

  const browser = await puppeteer.launch({
    executablePath: CHROME_PATH,
    headless: true,
    args: ["--no-sandbox", "--disable-setuid-sandbox"],
  });

  const page = await browser.newPage();

  // Navigate to local server and wait for module to load
  await page.goto(`http://localhost:${PORT}`, { waitUntil: "networkidle0" });
  await page.waitForFunction(() => window.__ready === true, { timeout: 30000 });
  console.log("Excalidraw utils loaded.\n");

  for (const file of files) {
    const filePath = path.join(DIAGRAMS_DIR, file);
    const baseName = file.replace(".excalidraw", "");
    const data = JSON.parse(fs.readFileSync(filePath, "utf-8"));

    console.log(`Exporting: ${file}`);

    // Export to SVG
    const svgString = await page.evaluate(async (diagramData) => {
      const svg = await window.__exportToSvg({
        elements: diagramData.elements,
        appState: {
          ...diagramData.appState,
          exportBackground: true,
          viewBackgroundColor:
            diagramData.appState?.viewBackgroundColor || "#ffffff",
        },
        files: diagramData.files || {},
      });
      return svg.outerHTML;
    }, data);

    const svgPath = path.join(DIAGRAMS_DIR, `${baseName}.svg`);
    fs.writeFileSync(svgPath, svgString);
    console.log(`  -> ${baseName}.svg`);

    // Export to PNG via SVG -> canvas -> PNG (2x for crisp output)
    const pngBase64 = await page.evaluate(async (diagramData) => {
      const svg = await window.__exportToSvg({
        elements: diagramData.elements,
        appState: {
          ...diagramData.appState,
          exportBackground: true,
          viewBackgroundColor:
            diagramData.appState?.viewBackgroundColor || "#ffffff",
        },
        files: diagramData.files || {},
      });

      const svgStr = new XMLSerializer().serializeToString(svg);
      const svgBlob = new Blob([svgStr], { type: "image/svg+xml" });
      const url = URL.createObjectURL(svgBlob);

      return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => {
          const scale = 2;
          const canvas = document.createElement("canvas");
          canvas.width = img.naturalWidth * scale;
          canvas.height = img.naturalHeight * scale;
          const ctx = canvas.getContext("2d");
          ctx.scale(scale, scale);
          ctx.drawImage(img, 0, 0);
          URL.revokeObjectURL(url);
          resolve(canvas.toDataURL("image/png").split(",")[1]);
        };
        img.onerror = () => {
          URL.revokeObjectURL(url);
          reject(new Error("Failed to render SVG to canvas"));
        };
        img.src = url;
      });
    }, data);

    const pngPath = path.join(DIAGRAMS_DIR, `${baseName}.png`);
    fs.writeFileSync(pngPath, Buffer.from(pngBase64, "base64"));
    console.log(`  -> ${baseName}.png`);
  }

  await browser.close();
  server.close();
  console.log(`\nDone! Exported ${files.length} diagrams to SVG and PNG.`);
}

exportDiagrams().catch((err) => {
  console.error("Export failed:", err);
  process.exit(1);
});
