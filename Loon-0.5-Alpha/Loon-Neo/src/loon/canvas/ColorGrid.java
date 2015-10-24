package loon.canvas;

import loon.utils.Array;

public class ColorGrid {

	public Array<ColorGridLine> lines;
	public LColor color;

	public int horizontal, vertical;
	public float cellSize;

	public ColorGrid(int horizontal, int vertical, float cellSize,LColor color) {
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.cellSize = cellSize;
		this.color = color;
		this.lines = new Array<ColorGridLine>();
		if ((this.horizontal == 1) || (this.vertical == 1)) {
			setupFloor();
		} else {
			setupCells();
		}
	}

	private void setupCells() {
		for (int x = 0; x < horizontal; x++) {
			ColorGridLine line = new ColorGridLine(x * cellSize, 0, x
					* cellSize, cellSize * vertical, color);
			lines.add(line);
		}
		for (int y = 0; y < vertical; y++) {
			ColorGridLine line = new ColorGridLine(0, y * cellSize, horizontal
					* cellSize, y * cellSize, color);
			lines.add(line);
		}
	}

	private void setupFloor() {
		ColorGridLine line = new ColorGridLine(0, 0, horizontal * cellSize, 0,
				color);
		lines.add(line);
	}
}
