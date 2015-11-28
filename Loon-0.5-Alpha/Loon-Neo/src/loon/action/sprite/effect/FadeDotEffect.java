package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
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

		private boolean finished, fade_allowed;

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
					finished = true;
				}
			} else {
				currentFrame++;
				rad += growSpeed * (elapsedTime / 10) * 0.4f;
				if (rad >= 360) {
					rad = 360;
					finished = true;
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

	private boolean finished;

	private LColor back;

	private TArray<Dot> dots = new TArray<Dot>();

	private int count = 4;

	private int width;

	private int height;

	private boolean visible;

	private int type = ISprite.TYPE_FADE_IN;

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
		this.type = type;
		this.count = count;
		this.visible = true;
		this.setColor(c);
		this.width = w;
		this.height = h;
		if (dots.size == 0) {
			for (int i = 0; i < count; i++) {
				dots.add(new Dot(type, time, rad, width, height));
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
		return back;
	}

	public void setColor(LColor color) {
		this.back = color;
	}

	public boolean isCompleted() {
		if (finished) {
			return finished;
		}
		for (int i = 0; i < dots.size; i++) {
			if (!((Dot) dots.get(i)).finished) {
				return false;
			}
		}
		return (finished = true);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		if (finished) {
			return;
		}
		if (timer.action(elapsedTime)) {
			for (int i = 0; i < dots.size; i++) {
				dots.get(i).update(elapsedTime);
			}
		}
	}

	public void createUI(GLEx g) {
		if (finished) {
			return;
		}
		if (!visible) {
			return;
		}
		if (finished) {
			return;
		}
		boolean useText = g.alltextures() && LSystem.isHTML5();
		int skip = g.getPixSkip();
		if (useText) {
			g.setPixSkip(10);
		}
		int tmp = g.color();
		g.setColor(back);
		for (int i = 0; i < dots.size; i++) {
			((Dot) dots.get(i)).paint(g);
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
		return height;
	}

	public int getWidth() {
		return width;
	}

	public LTexture getBitmap() {
		return null;
	}

	public int getCount() {
		return count;
	}

	public int getFadeType() {
		return type;
	}

	public void close() {
		visible = false;
		finished = true;
	}

}
