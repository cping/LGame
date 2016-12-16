package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;
import loon.component.Print;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.Font.Style;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 显示滚动文字的精灵(主要就是用来做前情提要滚动，比如:从前有个魔王，魔王认识个勇者，勇者不懂经济，魔王是个学霸，于是XXX这类的……)
 * 
 * 	ScrollText s = new ScrollText("ABCDEFG\nMNBVCXZ");
		s.setDirection(Direction.LEFT);
		add(s);
		centerOn(s);
 */
public class ScrollText extends Entity {

	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Direction direction = Direction.UP;
	private boolean stop = false;
	private final TArray<String> strings;
	private int speed = 1;
	private String text = "";
	private Vector2f textMove = new Vector2f();
	private float _offsetX = 0, _offsetY = 0;
	private float textX = 0, textY = 0;
	private float space = 5f;
	private IFont font;
	private LTimer timer = new LTimer(50);

	public ScrollText(String text) {
		this(LFont.getDefaultFont(), text, 0, 0, 0, 0);
	}

	public ScrollText(String text, int width, int height) {
		this(LFont.getDefaultFont(), text, 0, 0, width, height);
	}

	public ScrollText(String text, int x, int y, int width, int height) {
		this(LFont.getDefaultFont(), text, x, y, width, height);
	}

	public ScrollText(String text, String font, Style type, int size, int x,
			int y, int width, int height) {
		this(LFont.getFont(font, type, size), text, x, y, width, height);
	}

	public ScrollText(IFont font, String text, int x, int y, int width,
			int height) {
		this.font = font;
		this.setRepaint(true);
		this.setColor(LColor.white);
		this.setLocation(x, y);
		if (width > 0) {
			this.setWidth(width);
		} else {
			this.setWidth(font.stringWidth(text) + font.getSize());
		}
		if (font != null && font instanceof LFont) {
			LSTRDictionary.bind((LFont) font, text);
		}
		strings = Print.formatMessage(text, font, (int) getWidth());
		if (height > 0) {
			this.setHeight(height);
		} else {
			this.setHeight((strings.size * (font.getHeight() + space))
					+ font.getHeight());
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
			if (!intersects) {
				stop = true;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		textX = textMove.x + getX() + _offsetX;
		textY = textMove.y + getY() + _offsetY;
		for (int i = 0, size = strings.size; i < size; i++) {
			g.drawString(strings.get(i), textX + offsetX,
					textY + (i * (font.stringHeight(strings.get(i)) + space))
							+ offsetY, _baseColor);
		}
	}

	public String getText() {
		return text;
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
}
