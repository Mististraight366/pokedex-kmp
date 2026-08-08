#!/usr/bin/env python3
"""Compose the per-platform screenshots into previews/screenshot.png.

Usage: python3 scripts/compose-preview.py
Requires Pillow (`python3 -m pip install pillow`).
"""

import os

from PIL import Image, ImageDraw, ImageFont

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PREVIEWS = os.path.join(ROOT, "previews")
OUT = os.path.join(PREVIEWS, "screenshot.png")

PANELS = [
    ("screenshot-android.png", "Android"),
    ("screenshot-ios.png", "iOS"),
    ("screenshot-desktop.png", "Desktop"),
    ("screenshot-web.png", "Web (wasm)"),
]

MARGIN = 28
GAP = 28
PANEL_HEIGHT = 900
LABEL_GAP = 16
LABEL_SIZE = 26

BACKGROUND = (20, 18, 21)
BORDER = (48, 45, 51)
LABEL_COLOR = (226, 220, 229)

FONT_CANDIDATES = [
    "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
    "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf",
    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
]


def load_font():
    for path in FONT_CANDIDATES:
        if os.path.exists(path):
            return ImageFont.truetype(path, LABEL_SIZE)
    return ImageFont.load_default()


def main():
    font = load_font()
    shots = []
    for filename, label in PANELS:
        image = Image.open(os.path.join(PREVIEWS, filename)).convert("RGB")
        width = round(image.width * PANEL_HEIGHT / image.height)
        shots.append((image.resize((width, PANEL_HEIGHT), Image.LANCZOS), label))

    # Tallest label decides the canvas height so descenders never get clipped.
    label_bottom = max(font.getbbox(label)[3] for _, label in shots)

    canvas_width = MARGIN * 2 + sum(s.width for s, _ in shots) + GAP * (len(shots) - 1)
    canvas_height = MARGIN + PANEL_HEIGHT + LABEL_GAP + label_bottom + MARGIN
    canvas = Image.new("RGB", (canvas_width, canvas_height), BACKGROUND)
    draw = ImageDraw.Draw(canvas)

    x = MARGIN
    for shot, label in shots:
        canvas.paste(shot, (x, MARGIN))
        # Hairline keeps the dark app surfaces from bleeding into the dark canvas.
        draw.rectangle(
            (x, MARGIN, x + shot.width - 1, MARGIN + PANEL_HEIGHT - 1),
            outline=BORDER,
            width=1,
        )
        draw.text(
            (x + shot.width / 2, MARGIN + PANEL_HEIGHT + LABEL_GAP),
            label,
            font=font,
            fill=LABEL_COLOR,
            anchor="ma",
        )
        x += shot.width + GAP

    canvas.save(OUT)
    print(f"wrote {OUT} ({canvas.width}x{canvas.height})")


if __name__ == "__main__":
    main()
