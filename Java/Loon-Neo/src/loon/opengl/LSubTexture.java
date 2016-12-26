package loon.opengl;

import loon.LTexture;
import loon.LTextures;

public class LSubTexture {

	private LTexture parent;

	private int x, y, x2, y2;

	public LSubTexture(LTexture tex) {
		this.parent = tex;
		this.x = 0;
		this.y = 0;
		this.x2 = parent.getWidth();
		this.y2 = parent.getHeight();
	}
	
	public LSubTexture(String path) {
		this.parent = LTextures.loadTexture(path);
		this.x = 0;
		this.y = 0;
		this.x2 = parent.getWidth();
		this.y2 = parent.getHeight();
	}

	public LSubTexture(LTexture parent, int x, int y, int x2, int y2) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
	}

	public LTexture get() {
		return this.parent.copy(x, y, getWidth() - 1, getHeight() - 1);
	}

	public LTexture getParent() {
		return parent;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

	public int getSubX() {
		return x;
	}

	public int getSubY() {
		return y;
	}

	public int getSubX2() {
		return x2;
	}

	public int getSubY2() {
		return y2;
	}

	public int getWidth() {
		return x2 - x;
	}

	public int getHeight() {
		return y2 - y;
	}

	public float getParentWidth() {
		if (parent != null) {
			return parent.width();
		}
		return 0;
	}

	public float getParentHeight() {
		if (parent != null) {
			return parent.height();
		}
		return 0;
	}
}
