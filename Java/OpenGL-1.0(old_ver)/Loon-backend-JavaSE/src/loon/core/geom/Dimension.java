package loon.core.geom;

public class Dimension {

	int width = -1, height = -1;

	public Dimension(int w, int h) {
		width = w;
		height = h;
	}

	public Dimension(Dimension d) {
		width = d.getWidth();
		height = d.getHeight();
	}

	public boolean contains(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public String toString() {
		return "(" + width + ", " + height + ")";
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setSize(Dimension d) {
		this.width = d.getWidth();
		this.height = d.getHeight();
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
