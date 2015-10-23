package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class RainKernel implements IKernel {

	private boolean exist;

	private LTexture rain;

	private int id;

	private float offsetX, offsetY, x, y, width, height, rainWidth, rainHeight;

	public RainKernel(int n, int w, int h) {
		rain = LTextures
				.loadTexture((LSystem.FRAMEWORK_IMG_NAME + "rain_" + n + ".png")
						.intern());
		rainWidth = rain.width();
		rainHeight = rain.height();
		width = w;
		height = h;
		offsetX = 0;
		offsetY = (5 - n) * 30 + 75 + MathUtils.random() * 15;
	}

	public int id() {
		return id;
	}

	public void make() {
		exist = true;
		x = MathUtils.random() * width;
		y = -rainHeight;
	}

	public void update() {
		if (!exist) {
			if (MathUtils.random() < 0.002) {
				make();
			}
		} else {
			x += offsetX;
			y += offsetY;
			if (y >= height) {
				x = MathUtils.random() * width;
				y = -rainHeight * MathUtils.random();
			}
		}
	}

	public void draw(GLEx g) {
		if (exist) {
			rain.draw(x, y);
		}
	}

	public LTexture get() {
		return rain;
	}

	public float getHeight() {
		return rainHeight;
	}

	public float getWidth() {
		return rainWidth;
	}

	public void close() {
		if (rain != null) {
			rain.close();
			rain = null;
		}
	}

}
