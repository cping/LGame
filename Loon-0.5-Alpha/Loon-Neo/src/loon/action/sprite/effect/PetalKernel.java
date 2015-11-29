package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

public class PetalKernel implements IKernel {

	private boolean exist;

	private LTexture sakura;

	private float offsetX, offsetY, speed, x, y, width, height, sakuraWidth,
			sakuraHeight;

	private int id;

	public PetalKernel(LTexturePack pack, int n, int w, int h) {
		id = n;
		sakura = pack.getTexture(LSystem.FRAMEWORK_IMG_NAME + "sakura_" + n);
		sakuraWidth = sakura.width();
		sakuraHeight = sakura.height();
		width = w;
		height = h;
		offsetX = 0;
		offsetY = n * 0.6f + 1.9f + MathUtils.random() * 0.2f;
		speed = MathUtils.random();
	}

	public int id() {
		return id;
	}

	public void make() {
		exist = true;
		x = MathUtils.random() * width;
		y = -sakuraHeight;
	}

	public void update() {
		if (!exist) {
			if (MathUtils.random() < 0.002) {
				make();
			}
		} else {
			x += offsetX;
			y += offsetY;
			offsetX += speed;
			speed += (MathUtils.random() - 0.5) * 0.3;
			if (offsetX >= 1.5) {
				offsetX = 1.5f;
			}
			if (offsetX <= -1.5) {
				offsetX = -1.5f;
			}
			if (speed >= 0.2) {
				speed = 0.2f;
			}
			if (speed <= -0.2) {
				speed = -0.2f;
			}
			if (y >= height) {
				y = -(MathUtils.random() * 1) - sakuraHeight;
				x = (MathUtils.random() * (width - 1));
			}
		}
	}

	public void draw(GLEx g, float mx, float my) {
		if (exist) {
			sakura.draw(mx + x, my + y);
		}
	}

	public LTexture get() {
		return sakura;
	}

	public float getHeight() {
		return sakuraHeight;
	}

	public float getWidth() {
		return sakuraWidth;
	}

	public void close() {
		if (sakura != null) {
			sakura.close();
			sakura = null;
		}
	}
}
