package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.sprite.Entity;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

public class SplitEffect extends Entity implements BaseEffect {

	private Vector2f v1, v2;

	private int halfWidth, halfHeight, multiples, direction;

	private boolean completed, special;

	private RectBox limit;

	private LTimer timer;

	public SplitEffect(String fileName, int d) {
		this(LTextures.loadTexture(fileName), d);
	}

	public SplitEffect(LTexture t, int d) {
		this(t, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LTexture t, RectBox limit, int d) {
		this.setRepaint(true);
		this._image = t;
		this.setSize(t.width(), t.height());
		this.halfWidth = (int) (_width / 2f);
		this.halfHeight = (int) (_height / 2f);
		this.multiples = 2;
		this.direction = d;
		this.limit = limit;
		this.timer = new LTimer(10);
		this.v1 = new Vector2f();
		this.v2 = new Vector2f();
		switch (direction) {
		case Config.UP:
		case Config.DOWN:
			special = true;
		case Config.TLEFT:
		case Config.TRIGHT:
			v1.set(0, 0);
			v2.set(halfWidth, 0);
			break;
		case Config.LEFT:
		case Config.RIGHT:
			special = true;
		case Config.TUP:
		case Config.TDOWN:
			v1.set(0, 0);
			v2.set(0, halfHeight);
			break;
		}
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			if (timer.action(elapsedTime)) {
				switch (direction) {
				case Config.LEFT:
				case Config.RIGHT:
				case Config.TLEFT:
				case Config.TRIGHT:
					v1.move_multiples(Field2D.TLEFT, multiples);
					v2.move_multiples(Field2D.TRIGHT, multiples);
					break;
				case Config.UP:
				case Config.DOWN:
				case Config.TUP:
				case Config.TDOWN:
					v1.move_multiples(Field2D.TUP, multiples);
					v2.move_multiples(Field2D.TDOWN, multiples);
					break;
				}

				if (special) {
					if (!limit.intersects(v1.x, v1.y, halfHeight, halfWidth)
							&& !limit.intersects(v2.x, v2.y, halfHeight,
									halfWidth)) {
						this.completed = true;
					}
				} else if (!limit.intersects(v1.x, v1.y, halfWidth, halfHeight)
						&& !limit.intersects(v2.x, v2.y, halfWidth, halfHeight)) {
					this.completed = true;
				}
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (!completed) {
			final float x1 = v1.x + getX() + offsetX;
			final float y1 = v1.y + getY() + offsetY;

			final float x2 = v2.x + getX() + offsetX;
			final float y2 = v2.y + getY() + offsetY;

			switch (direction) {
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TUP:
			case Config.TDOWN:
				g.draw(_image, x1, y1, _width, halfHeight, 0, 0, _width,
						halfHeight);
				g.draw(_image, x2, y2, _width, halfHeight, 0, halfHeight,
						_width, _height - halfHeight);
				break;
			case Config.UP:
			case Config.DOWN:
			case Config.TLEFT:
			case Config.TRIGHT:
				g.draw(_image, x1, y1, halfWidth, _height, 0, 0, halfWidth,
						_height);
				g.draw(_image, x2, y2, halfWidth, _height, halfWidth, 0, _width
						- halfWidth, _height);
				break;

			}
		}
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}
