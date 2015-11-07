package loon.action.sprite.effect;

import java.util.ArrayList;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

public class FadeDotEffect extends LObject implements ISprite {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private class Dot {

		private float x;

		private float y;

		private float growSpeed;

		private float rad;

		private int type;

		private boolean finish, fade_allowed;

		private float currentFrame;

		private float time;

		public Dot(int type, int time, int rad, int w, int h) {
			this.type = type;
			if (time <= -1) {
				fade_allowed = true;
			}
			this.time = time;
			if (type == ISprite.TYPE_FADE_IN) {
				if (rad < 0 || rad > 360) {
					// 360速度较慢，越界的话索性改成260……
					rad = 260;
				}
				this.rad = rad;
				this.currentFrame = time;
			} else {
				if (rad < 0) {
					rad = 0;
				}
				this.rad = 0;
				this.currentFrame = 0;
			}
			x = (MathUtils.random(0, 1f) * w);
			y = (MathUtils.random(0, 1f) * h);
			growSpeed = 1f + (MathUtils.random(0, 1f));
		}

		public void update(long elapsedTime) {
			if (type == ISprite.TYPE_FADE_IN) {
				currentFrame--;
				rad -= growSpeed * (elapsedTime / 10) * 0.6f;
				if (rad <= 0) {
					rad = 0;
					finish = true;
				}
			} else {
				currentFrame++;
				rad += growSpeed * (elapsedTime / 10) * 0.4f;
				if (rad >= 360) {
					rad = 360;
					finish = true;
				}
			}
		}

		public void paint(GLEx g) {
			if (rad > 0 && rad < 360) {
				float a = g.alpha();
				if (!fade_allowed) {
					float alpha = currentFrame / time;
					g.setAlpha(alpha);
				}
				g.fillOval(x - rad, y - rad, rad * 2, rad * 2);
				g.setAlpha(a);
			}
		}

	}

	private boolean _finish;

	private LColor _color;

	private ArrayList<Dot> _dots = new ArrayList<Dot>();

	private int _count = 5;

	private int _width;

	private int _height;

	private boolean _visible;

	private int _type = ISprite.TYPE_FADE_IN;

	private LTimer timer = new LTimer(0);

	public FadeDotEffect(LColor c, int type, int time, int count) {
		this(type, time, time, count, c, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public FadeDotEffect(LColor c) {
		this(ISprite.TYPE_FADE_IN, 280, c);
	}

	public FadeDotEffect(int type, int time, LColor c) {
		this(type, time, time, 5, c, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public FadeDotEffect(int type, int time, int rad, int count, LColor c,
			int w, int h) {
		this._type = type;
		this._count = count;
		this._visible = true;
		this.setColor(c);
		this._width = w;
		this._height = h;
		if (_dots.size() == 0) {
			for (int i = 0; i < _count; i++) {
				_dots.add(new Dot(_type, time, rad, _width, _height));
			}
		}
	}

	public float getDelay() {
		return timer.getDelay();
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public LColor getColor() {
		return _color;
	}

	public void setColor(LColor color) {
		this._color = color;
	}

	public boolean isCompleted() {
		if (_finish) {
			return _finish;
		}
		for (int i = 0; i < _dots.size(); i++) {
			if (!((Dot) _dots.get(i)).finish) {
				return false;
			}
		}
		return (_finish = true);
	}

	public void setVisible(boolean visible) {
		this._visible = visible;
	}

	public boolean isVisible() {
		return _visible;
	}

	public void update(long elapsedTime) {
		if (!_visible) {
			return;
		}
		if (_finish) {
			return;
		}
		if (timer.action(elapsedTime)) {
			for (int i = 0; i < _dots.size(); i++) {
				_dots.get(i).update(elapsedTime);
			}
		}
	}

	public void createUI(GLEx g) {
		if (!_visible) {
			return;
		}
		if (_finish) {
			return;
		}
		boolean useText = g.alltextures() && LSystem.isHTML5();
		int skip = g.getPixSkip();
		if (useText) {
			g.setPixSkip(10);
		}
		int tmp = g.color();
		g.setColor(_color);
		for (int i = 0; i < _dots.size(); i++) {
			((Dot) _dots.get(i)).paint(g);
		}
		if (useText) {
			g.setPixSkip(skip);
		}
		g.setColor(tmp);
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	public int getHeight() {
		return _height;
	}

	public int getWidth() {
		return _width;
	}

	public LTexture getBitmap() {
		return null;
	}

	public void close() {

	}

}
