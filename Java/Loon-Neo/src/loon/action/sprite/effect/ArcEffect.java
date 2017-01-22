package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

/**
 * 0.3.2版新增类，单一色彩的圆弧渐变特效
 */
public class ArcEffect extends Entity implements BaseEffect {

	private int count;

	private int div = 10;

	private int turn = 1;

	private int[] sign = { 1, -1 };

	private boolean completed;

	private LTimer timer;

	public ArcEffect(LColor c) {
		this(c, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public ArcEffect(LColor c, int x, int y, int width, int height) {
		this.setLocation(x, y);
		this.setSize(width, height);
		this.timer = new LTimer(200);
		this.setColor(c == null ? LColor.black : c);
		this.setRepaint(true);
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

	@Override
	public void onUpdate(long elapsedTime) {
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
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completed) {
			return;
		}
		tmpColor = g.color();
		g.setColor(_baseColor);
		int tmp = g.getPixSkip();
		boolean useTex = LSystem.isHTML5();
		if (useTex) {
			g.setPixSkip(8);
		}
		if (count <= 1) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height);
		} else {
			float deg = 360f / this.div * this.count;
			if (deg < 360) {
				float length = MathUtils.sqrt(MathUtils.pow(_width / 2, 2.0f) + MathUtils.pow(_height / 2, 2.0f));
				float x = getX() + (_width / 2 - length);
				float y = getY() + (_height / 2 - length);
				float w = _width / 2 + length - x;
				float h = _height / 2 + length - y;
				g.fillArc(x + offsetX + _offset.x, y + offsetY + _offset.y, w, h, 20, 0, this.sign[this.turn] * deg);
			}
		}
		if (useTex) {
			g.setPixSkip(tmp);
		}
		g.setColor(tmpColor);
	}

	@Override
	public void reset() {
		super.reset();
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
	public void close() {
		super.close();
		completed = true;
	}

}
