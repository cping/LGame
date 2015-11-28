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

	private LColor ovalColor;
	private float ovalWidth;
	private float ovalHeight;
	private float max_time;
	private LTimer timer;
	private float elapsed;
	private boolean visible = true;
	private boolean finished = false;
	private int type = TYPE_FADE_IN;

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
		this.type = type;
		this.elapsed = 0;
		this.ovalWidth = w;
		this.ovalHeight = h;
		this.ovalColor = oc;
		this.elapsed = 0;
		for (int i = 0; i < OVAL_COLORS.length; i++) {
			OVAL_COLORS[i] = new LColor(ovalColor.r, ovalColor.g,
					ovalColor.b, 1F - 0.15f * i);
		}
		this.max_time = time;
		this.timer = new LTimer(0);
		this.visible = true;
	}

	public void setOvalColor(LColor ovalColor) {
		this.ovalColor = ovalColor;
	}

	public LColor getOvalColor() {
		return this.ovalColor;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public boolean isCompleted() {
		return finished;
	}

	public int getHeight() {
		return (int) ovalHeight;
	}

	public int getWidth() {
		return (int) ovalWidth;
	}

	public void update(long elapsedTime) {
		if (finished) {
			return;
		}
		if (timer.action(elapsedTime)) {
			if (type == TYPE_FADE_IN) {
				this.elapsed += elapsedTime / 20f;
				float progress = this.elapsed / this.max_time;
				this.ovalWidth = (ovalWidth * MathUtils
						.pow(1f - progress, 2f));
				this.ovalHeight = (ovalHeight * MathUtils.pow(1f - progress,
						2f));
				if (this.elapsed >= this.max_time / 15f) {
					this.elapsed = -1;
					this.ovalWidth = (this.ovalHeight = 0f);
					this.finished = true;
				}
			} else {
				this.elapsed += elapsedTime;
				float progress = this.elapsed / this.max_time;
				this.ovalWidth = (LSystem.viewSize.width * MathUtils.pow(
						progress, 2f));
				this.ovalHeight = (LSystem.viewSize.height * MathUtils.pow(
						progress, 2f));
				if (this.elapsed >= this.max_time) {
					this.elapsed = -1;
					this.ovalWidth = (this.ovalHeight = MathUtils.max(
							LSystem.viewSize.width, LSystem.viewSize.height));
					this.finished = true;
				}
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
		if (this.elapsed > -1) {
			int tmp = g.getPixSkip();
			boolean usetex = LSystem.isHTML5();
			if (usetex) {
				g.setPixSkip(10);
			}
			int old = g.color();
			if (usetex) {
				g.setColor(OVAL_COLORS[0]);
				float w = this.ovalWidth + 4 * this.ovalWidth * 0.1f;
				float h = this.ovalHeight + 4 * this.ovalWidth * 0.1f;
				g.fillOval(g.getWidth() / 2 - w / 2f, g.getHeight() / 2 - h
						/ 2f, w, h);
			} else {
				int size = OVAL_COLORS.length;
				for (int i = size - 1; i >= 0; i--) {
					g.setColor(OVAL_COLORS[i]);
					float w = this.ovalWidth + i * this.ovalWidth * 0.1f;
					float h = this.ovalHeight + i * this.ovalWidth * 0.1f;
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

	public int getFadeType() {
		return type;
	}

	public LTexture getBitmap() {
		return null;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void close() {
		this.visible = false;
		this.finished = true;
	}

}
