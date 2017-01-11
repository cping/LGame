package loon.action.map.view;

public class ChoiceModel {
	final int _x;
	final int _y;
	final int _width;
	final int _height;

	public ChoiceModel(int x, int y, int w, int h) {
		this._x = x;
		this._y = y;
		this._width = w;
		this._height = h;
	}

	public boolean isChoiced(int x, int y) {
		return _x <= x && x < _x + _width && _y <= y && y < _y + _height;
	}
}
