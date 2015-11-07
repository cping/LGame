package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

public class FadeOvalEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final LColor[] OVAL_COLORS = new LColor[5];

	private LColor _ovalColor;
	private float _ovalWidth;
	private float _ovalHeight;
	private float _max_time;
	private LTimer _timer;
	private float _elapsed;
	private boolean _visible = true;
	private boolean _finish = false;
	private int _type = TYPE_FADE_IN;

	public FadeOvalEffect(int type, LColor color) {
		this(type, color, LSystem.viewSize.width, LSystem.viewSize.height);
	}

	public FadeOvalEffect(int type, float w, float h) {
		this(type, LColor.black, 1500, w, h);
	}

	public FadeOvalEffect(int type, LColor oc, float w, float h) {
		this(type, oc, 1500, w, h);
	}

	public FadeOvalEffect(int type, LColor oc, int time, float w, float h) {
		this._type = type;
		this._elapsed = 0;
		this._ovalWidth = w;
		this._ovalHeight = h;
		this._ovalColor = oc;
		this._elapsed = 0;
		for (int i = 0; i < OVAL_COLORS.length; i++) {
			OVAL_COLORS[i] = new LColor(_ovalColor.r, _ovalColor.g,
					_ovalColor.b, 1F - 0.15f * i);
		}
		this._max_time = time;
		this._timer = new LTimer(0);
		this._visible = true;
	}

	public void setOvalColor(LColor _ovalColor) {
		this._ovalColor = _ovalColor;
	}

	public LColor getOvalColor() {
		return this._ovalColor;
	}

	public void setDelay(long delay) {
		_timer.setDelay(delay);
	}

	public long getDelay() {
		return _timer.getDelay();
	}

	public boolean isCompleted() {
		return _finish;
	}

	public int getHeight() {
		return (int) _ovalHeight;
	}

	public int getWidth() {
		return (int) _ovalWidth;
	}

	public void update(long elapsedTime) {
		if (_finish) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			if (_type == TYPE_FADE_IN) {
				this._elapsed += elapsedTime / 20f;
				float progress = this._elapsed / this._max_time;
				this._ovalWidth = (_ovalWidth * MathUtils
						.pow(1f - progress, 2f));
				this._ovalHeight = (_ovalHeight * MathUtils.pow(1f - progress,
						2f));
				if (this._elapsed >= this._max_time / 15f) {
					this._elapsed = -1;
					this._ovalWidth = (this._ovalHeight = 0f);
					this._finish = true;
				}
			} else {
				this._elapsed += elapsedTime;
				float progress = this._elapsed / this._max_time;
				this._ovalWidth = (LSystem.viewSize.width * MathUtils.pow(
						progress, 2f));
				this._ovalHeight = (LSystem.viewSize.height * MathUtils.pow(
						progress, 2f));
				if (this._elapsed >= this._max_time) {
					this._elapsed = -1;
					this._ovalWidth = (this._ovalHeight = MathUtils.max(
							LSystem.viewSize.width, LSystem.viewSize.height));
					this._finish = true;
				}
			}
		}
	}

	public void createUI(GLEx g) {
		if (_finish) {
			return;
		}
		if (!_visible) {
			return;
		}
		if (this._elapsed > -1) {
			int tmp = g.getPixSkip();
			boolean usetex = g.alltextures() && LSystem.isHTML5();
			if (usetex) {
				g.setPixSkip(12);
			}
			int old = g.color();
			if (usetex) {
				g.setColor(OVAL_COLORS[0]);
				float w = this._ovalWidth + 4 * this._ovalWidth * 0.1f;
				float h = this._ovalHeight + 4 * this._ovalWidth * 0.1f;
				g.fillOval(g.getWidth() / 2 - w / 2f, g.getHeight() / 2 - h
						/ 2f, w, h);
			} else {
				int size = OVAL_COLORS.length;
				for (int i = size - 1; i >= 0; i--) {
					g.setColor(OVAL_COLORS[i]);
					float w = this._ovalWidth + i * this._ovalWidth * 0.1f;
					float h = this._ovalHeight + i * this._ovalWidth * 0.1f;
					g.fillOval(g.getWidth() / 2 - w / 2f, g.getHeight() / 2 - h
							/ 2f, w, h);
				}
			}
			g.setColor(old);
			if (usetex) {
				g.setPixSkip(tmp);
			}
		}
	}

	public LTexture getBitmap() {
		return null;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	public boolean isVisible() {
		return _visible;
	}

	public void setVisible(boolean _visible) {
		this._visible = _visible;
	}

	public void close() {
		this._visible = false;
		this._finish = true;
	}

}
