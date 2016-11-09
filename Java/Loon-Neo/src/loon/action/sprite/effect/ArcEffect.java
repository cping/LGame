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
public class ArcEffect extends LObject<ISprite> implements BaseEffect, ISprite {

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

	private boolean visible, completed;

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

	@Override
	public boolean isCompleted() {
		return completed;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public void update(long elapsedTime) {
		if (completed) {
			return;
		}
		if (this.count >= this.div) {
			this.completed = true;
		}
		if (timer.action(elapsedTime)) {
			count++;
		}
	}

	private int tmpColor;

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}
	
	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		if (completed) {
			return;
		}
		tmpColor = g.color();
		if (_alpha > 0 && _alpha < 1f) {
			g.setAlpha(_alpha);
		}
		g.setColor(color);
		int tmp = g.getPixSkip();
		boolean useTex = LSystem.isHTML5();
		if (useTex) {
			g.setPixSkip(8);
		}
		if (count <= 1) {
			g.fillRect(x() + offsetX, y() + offsetY, width, height);
		} else {
			float deg = 360f / this.div * this.count;
			if (deg < 360) {
				float length = MathUtils.sqrt(MathUtils.pow(width / 2, 2.0f)
						+ MathUtils.pow(height / 2, 2.0f));
				float x = getX() + (width / 2 - length);
				float y = getY() + (height / 2 - length);
				float w = width / 2 + length - x;
				float h = height / 2 + length - y;
				g.fillArc(x + offsetX, y + offsetY, w, h, 20, 0,
						this.sign[this.turn] * deg);
			}
		}
		if (useTex) {
			g.setPixSkip(tmp);
		}
		if (_alpha != 1f) {
			g.setAlpha(1f);
		}
		g.setColor(tmpColor);

	}

	public void reset() {
		this.completed = false;
		this.count = 0;
		this.turn = 1;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
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
	}

}
