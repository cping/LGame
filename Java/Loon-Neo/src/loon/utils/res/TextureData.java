package loon.utils.res;

import loon.geom.XY;

public class TextureData implements XY {

	protected int sourceW = 0;

	protected int sourceH = 0;

	protected int x = 0;

	protected int y = 0;

	protected int offX = 0;

	protected int offY = 0;

	protected String name = "unkown";

	protected TextureData() {

	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	protected int w = 0;

	public int w() {
		return w;
	}

	protected int h = 0;

	public int h() {
		return h;
	}

	public int offX() {
		return offX;
	}

	public int offY() {
		return offY;
	}

	public int sourceW() {
		return sourceW;
	}

	public int sourceH() {
		return sourceH;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	public String name() {
		return name;
	}

}
