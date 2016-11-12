package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.Config;
import loon.action.sprite.Entity;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class OutEffect extends Entity implements BaseEffect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean completed;

	private int type, multiples;

	private RectBox limit;

	public OutEffect(String fileName, int code) {
		this(LTextures.loadTexture(fileName), code);
	}

	public OutEffect(LTexture t, int code) {
		this(t, LSystem.viewSize.getRect(), code);
	}

	public OutEffect(LTexture t, RectBox limit, int code) {
		this.setTexture(t);
		this.setSize(t.width(), t.height());
		this.setRepaint(true);
		this.type = code;
		this.multiples = 1;
		this.limit = limit;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			switch (type) {
			case Config.DOWN:
				move_45D_down(multiples);
				break;
			case Config.UP:
				move_45D_up(multiples);
				break;
			case Config.LEFT:
				move_45D_left(multiples);
				break;
			case Config.RIGHT:
				move_45D_right(multiples);
				break;
			case Config.TDOWN:
				move_down(multiples);
				break;
			case Config.TUP:
				move_up(multiples);
				break;
			case Config.TLEFT:
				move_left(multiples);
				break;
			case Config.TRIGHT:
				move_right(multiples);
				break;
			}
			if (!limit.intersects(x(), y(), _width, _height)) {
				completed = true;
			}
		}
	}

	public boolean isComplete() {
		return completed;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (!completed) {
			g.draw(_image, x() + offsetX, y() + offsetY);
		}
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}
