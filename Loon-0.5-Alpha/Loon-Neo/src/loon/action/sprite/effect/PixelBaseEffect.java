package loon.action.sprite.effect;

import java.util.ArrayList;

import loon.LObject;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

public abstract class PixelBaseEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float rotation = 0;

	protected boolean visible;

	protected boolean complete;

	protected ArrayList<TriangleEffect[]> triangleEffects = new ArrayList<TriangleEffect[]>();

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
		this.complete = false;
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
						rotation = ts[i].next();
					}
				}
			}
		}
		return rotation;
	}

	public float getRotation() {
		return rotation;
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
		boolean usetex = g.alltextures();
		if (usetex) {
			g.setPixSkip(6);
		}
		draw(g, this.x(), this.y());
		if (usetex) {
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
		if (!complete) {
			if (timer.action(elapsedTime)) {
				next();
			}
		}
	}

	@Override
	public int getWidth() {
		return (int) width;
	}

	@Override
	public int getHeight() {
		return (int) height;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isCompleted() {
		return complete;
	}

	@Override
	public void close() {

	}

}
