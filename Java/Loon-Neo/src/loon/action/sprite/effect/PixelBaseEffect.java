package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

//此像素非真像素，而是指'像素风'……实际是三角形纹理贴图效果……
public abstract class PixelBaseEffect extends LObject implements BaseEffect, ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected boolean visible;

	protected boolean completed;

	protected TArray<TriangleEffect[]> triangleEffects = new TArray<TriangleEffect[]>();

	protected LColor color;

	protected float[] start;

	protected float[] target;

	protected int frame;

	protected LTimer timer;

	protected float width, height;

	protected int limit = 90;

	public abstract void draw(GLEx g, float tx, float ty);

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public PixelBaseEffect(LColor c, float x1, float y1, float x2, float y2) {
		this.reset();
		this.setEffectPosition(x1, y1, x2, y2);
		this.color = c;
		this.timer = new LTimer(10);
		this.frame = 0;
		this.visible = true;
		this.completed = false;
		this.width = x2;
		this.height = y2;
	}

	public void setEffectDelay(long timer) {
		for (TriangleEffect[] ts : triangleEffects) {
			if (ts != null) {
				int size = ts.length;
				for (int i = 0; i < size; i++) {
					if (ts[i] != null) {
						ts[i].setDelay(timer);
					}
				}
			}
		}
	}

	public void reset() {
		this.start = new float[2];
		this.target = new float[2];
		this.frame = 0;
	}

	public void update() {
		this.start = new float[2];
		this.target = new float[2];
		this.frame = 0;
	}

	public void setEffectPosition(float x1, float y1, float x2, float y2) {
		this.start[0] = x1;
		this.start[1] = y1;
		this.target[0] = x2;
		this.target[1] = y2;
	}

	public float next() {
		this.frame++;
		for (TriangleEffect[] ts : triangleEffects) {
			if (ts != null) {
				int size = ts.length;
				for (int i = 0; i < size; i++) {
					if (ts[i] != null) {
						_rotation = ts[i].next();
					}
				}
			}
		}
		return _rotation;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		int tmp = g.getPixSkip();
		boolean useTex = g.alltextures() && LSystem.isHTML5();
		if (useTex) {
			g.setPixSkip(4);
		}
		draw(g, this.x(), this.y());
		if (useTex) {
			g.setPixSkip(tmp);
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void update(long elapsedTime) {
		if (!completed) {
			if (timer.action(elapsedTime)) {
				next();
			}
		}
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {

		visible = false;
		completed = true;
	}

}
