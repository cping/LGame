package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;
import loon.font.AutoWrap;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.TextOptions;
import loon.font.Font.Style;
import loon.font.Text;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 显示滚动文字的精灵(主要就是用来做前情提要滚动，比如:从前有个魔王，魔王认识个勇者，勇者不懂经济，魔王是个学霸，于是XXX这类的……)
 * 
 * ScrollText s = new ScrollText("ABCDEFG\nMNBVCXZ");
 * s.setDirection(Direction.LEFT); add(s); centerOn(s);
 * 
 * or:
 * 
 * String[] texts=
 * {"九阳神功惊俗世","君临天下易筋经","葵花宝典兴国邦","欢喜禅功祸苍生","紫雷刀出乾坤破","如来掌起山河动","浑天玄宇称宝鉴"
 * ,"天晶不出谁争锋","啦啦啦啦啦"}; ScrollText s = new
 * ScrollText(texts,TextOptions.VERTICAL_LEFT());
 * s.setDirection(Direction.LEFT); s.setLocation(115,20); add(s);
 */
public class ScrollText extends Entity {

	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Direction direction = Direction.UP;
	private boolean stop = false;
	private int speed = 1;
	private final Text _text;
	private Vector2f textMove = new Vector2f();
	private float _offsetX = 0, _offsetY = 0;
	private float textX = 0, textY = 0;
	private float space = 5f;
	private LTimer timer = new LTimer(50);

	public ScrollText(String text) {
		this(text, 0, 0, 0, 0);
	}

	public ScrollText(String text, TextOptions opt) {
		this(LFont.getDefaultFont(), opt, text, 0, 0, 0, 0);
	}

	public ScrollText(String[] texts) {
		this(texts, TextOptions.LEFT());
	}

	public ScrollText(String[] texts, TextOptions opt) {
		this(LFont.getDefaultFont(), opt, texts, 0, 0, 0, 0);
	}

	public ScrollText(IFont font, String text, TextOptions opt) {
		this(font, opt, text, 0, 0, 0, 0);
	}

	public ScrollText(IFont font, String[] texts, TextOptions opt) {
		this(font, opt, texts, 0, 0, 0, 0);
	}

	public ScrollText(String text, int width, int height) {
		this(text, 0, 0, width, height);
	}

	public ScrollText(String text, int x, int y, int width, int height) {
		this(LFont.getDefaultFont(), new TextOptions(), text, x, y, width,
				height);
	}

	public ScrollText(TextOptions opt, String text, int x, int y, int width,
			int height) {
		this(LFont.getDefaultFont(), opt, text, x, y, width, height);
	}

	public ScrollText(String text, String font, Style type, int size, int x,
			int y, int width, int height) {
		this(LFont.getFont(font, type, size), new TextOptions(), text, x, y,
				width, height);
	}

	public ScrollText(IFont font, TextOptions opt, String text, int x, int y,
			int width, int height) {
		this(font, opt, new String[] { text }, x, y, width, height);
	}

	public ScrollText(IFont font, TextOptions opt, String[] text, int x, int y,
			int width, int height) {
		if (text.length == 1) {
			this._text = new Text(font, text[0], opt);
		} else {
			StringBuffer sbr = new StringBuffer();
			for (int i = 0, size = text.length; i < size; i++) {
				sbr.append(text[i]);
				sbr.append(LSystem.LS);
			}
			this._text = new Text(font, sbr.toString(), opt);
		}
		this.setRepaint(true);
		this.setColor(LColor.white);
		this.setLocation(x, y);
		if (width > 0) {
			this.setWidth(width);
		} else {
			this.setWidth(_text.getWidth());
		}
		if (height > 0) {
			this.setHeight(height);
		} else {
			this.setHeight(_text.getHeight());
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!stop) {
			if (timer.action(elapsedTime)) {
				switch (direction) {
				default:
					break;
				case UP:
					textMove.move_up(speed);
					break;
				case DOWN:
					textMove.move_down(speed);
					break;
				case LEFT:
					textMove.move_left(speed);
					break;
				case RIGHT:
					textMove.move_right(speed);
					break;
				}
			}

			boolean intersects = LSystem.getProcess() != null
					&& LSystem.getProcess().getScreen() != null
					&& LSystem.getProcess().getScreen()
							.intersects(textX, textY, getWidth(), getHeight());
			if (_text.getAutoWrap() == AutoWrap.VERTICAL) {
				intersects = LSystem.getProcess() != null
						&& LSystem.getProcess().getScreen() != null
						&& LSystem
								.getProcess()
								.getScreen()
								.intersects(textX, textY, getHeight(),
										getWidth());
			}
			if (!intersects) {
				stop = true;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		textX = textMove.x + getX() + _offsetX;
		textY = textMove.y + getY() + _offsetY;
		_text.paintString(g, textX, textY, _baseColor);
	}

	public Text getOptions() {
		return this._text;
	}

	public CharSequence getText() {
		return _text.getText();
	}

	public ScrollText setText(String text) {
		_text.setText(text);
		return this;
	}

	public float getOffsetX() {
		return _offsetX;
	}

	public void setOffsetX(float offsetX) {
		this._offsetX = offsetX;
	}

	public float getOffsetY() {
		return _offsetY;
	}

	public void setOffsetY(float offsetY) {
		this._offsetY = offsetY;
	}

	public void setOffset(float offsetX, float offsetY) {
		this.setOffsetX(offsetX);
		this.setOffsetY(offsetY);
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public LColor getTextColor() {
		return getColor();
	}

	public void setTextColor(LColor textColor) {
		setColor(textColor);
	}

	public LTimer getTimer() {
		return timer;
	}

	public void setDelay(long d) {
		this.timer.setDelay(d);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public float getTextX() {
		return textX;
	}

	public float getTextY() {
		return textY;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public float getSpace() {
		return space;
	}

	public void setSpace(float space) {
		this.space = space;
	}

	@Override
	public void close() {
		super.close();
		_text.close();
	}
}
