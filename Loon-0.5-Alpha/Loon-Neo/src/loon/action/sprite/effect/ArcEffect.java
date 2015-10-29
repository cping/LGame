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

/**
 * 0.3.2版新增类，单一色彩的圆弧渐变特效
 */
public class ArcEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int count;

	private int div = 10;

	private int turn = 1;

	private int[] sign = { 1, -1 };

	private int width, height;

	private LColor color;

	private boolean visible, complete;

	private LTimer timer;

	public ArcEffect(LColor c) {
		this(c, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public ArcEffect(LColor c, int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.width = width;
		this.height = height;
		this.timer = new LTimer(200);
		this.color = c == null ? LColor.black : c;
		this.visible = true;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public boolean isCompleted() {
		return complete;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void update(long elapsedTime) {
		if (complete) {
			return;
		}
		if (this.count >= this.div) {
			this.complete = true;
		}
		if (timer.action(elapsedTime)) {
			count++;
		}
	}

	private int tmpColor;

	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (complete) {
			return;
		}
		tmpColor = g.color();
		if (alpha > 0 && alpha < 1f) {
			g.setAlpha(alpha);
		}
		g.setColor(color);
		if (count <= 1) {
			g.fillRect(x(), y(), width, height);
		} else {
			float deg = 360f / this.div * this.count;
			if (deg < 360) {
				float length = MathUtils.sqrt(MathUtils.pow(width / 2, 2.0f)
						+ MathUtils.pow(height / 2, 2.0f));
				float x = getX() + (width / 2 - length);
				float y = getY() + (height / 2 - length);
				float w = width / 2 + length - x;
				float h = height / 2 + length - y;
				g.fillArc(x, y, w, h, 20, 0, this.sign[this.turn] * deg);
			}
		}
		if (alpha != 1f) {
			g.setAlpha(1f);
		}
		g.setColor(tmpColor);

	}

	public void reset() {
		this.complete = false;
		this.count = 0;
		this.turn = 1;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public LTexture getBitmap() {
		return null;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void close() {

	}

}
