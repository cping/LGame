package loon.action.map.view;

import loon.LSystem;
import loon.opengl.GLEx;

public abstract class GameView {

	protected int _x;
	protected int _y;
	protected int _width;
	protected int _height;

	public GameView() {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public GameView(int x, int y, int w, int h) {
		this._x = x;
		this._y = y;
		this._width = w;
		this._height = h;
	}

	public void setPlace(int x, int y) {
		pos(x, y);
	}
	
	public void pos(int x, int y) {
		this._x = x;
		this._y = y;
	}

	public abstract void draw(GLEx g);
}
