package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

public class SplitEffect extends LObject implements BaseEffect, ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2f v1, v2;

	private int width, height, halfWidth, halfHeight, multiples, direction;

	private boolean visible, completed, special;

	private RectBox limit;

	private LTexture texture;

	private LTimer timer;

	public SplitEffect(String fileName, int d) {
		this(LTextures.loadTexture(fileName), d);
	}

	public SplitEffect(LTexture t, int d) {
		this(t, LSystem.viewSize.getRect(), d);
	}

	public SplitEffect(LTexture t, RectBox limit, int d) {
		this.texture = t;
		this.width = (int) texture.width();
		this.height = (int) texture.height();
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.multiples = 2;
		this.direction = d;
		this.limit = limit;
		this.timer = new LTimer(10);
		this.visible = true;
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
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void update(long elapsedTime) {
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
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (!completed) {
			float tmp = g.alpha();
			if (_alpha > 0 && _alpha < 1f) {
				g.setAlpha(_alpha);
			}
			final float x1 = v1.x + getX();
			final float y1 = v1.y + getY();

			final float x2 = v2.x + getX();
			final float y2 = v2.y + getY();

			switch (direction) {
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TUP:
			case Config.TDOWN:
				g.draw(texture, x1, y1, width, halfHeight, 0, 0, width,
						halfHeight);
				g.draw(texture, x2, y2, width, halfHeight, 0, halfHeight,
						width, height);
				break;
			case Config.UP:
			case Config.DOWN:
			case Config.TLEFT:
			case Config.TRIGHT:
				g.draw(texture, x1, y1, halfWidth, height, 0, 0, halfWidth,
						height);
				g.draw(texture, x2, y2, halfWidth, height, halfWidth, 0, width,
						height);
				break;

			}
			g.setAlpha(tmp);
		}
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public LTexture getBitmap() {
		return texture;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void close() {
		visible = false;
		completed = true;
		if (texture != null) {
			texture.close();
			texture = null;
		}
	}

}
