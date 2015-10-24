package loon.canvas;

public class ColorGridLine {

	public float x, y, x2, y2;
	private int _color;

	public ColorGridLine(float x, float y, float x2, float y2, LColor color) {
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this._color = color.getARGB();
	}

	public int color() {
		return _color;
	}

	public LColor getColor() {
		return new LColor(_color);
	}

	public void setColor(int color) {
		this._color = color;
	}

	public void setColor(LColor color) {
		this._color = color.getARGB();
	}
}