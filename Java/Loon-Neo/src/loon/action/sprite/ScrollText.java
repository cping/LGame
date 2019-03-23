/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
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
 * <pre>
 * ScrollText s = new ScrollText("ABCDEFG\nMNBVCXZ");
 * s.setDirection(Direction.LEFT);
 * add(s);
 * centerOn(s);
 * </pre>
 * 
 * or:
 * 
 * <pre>
 * String[] texts = { "九阳神功惊俗世", "君临天下易筋经", "葵花宝典兴国邦", "欢喜禅功祸苍生", "紫雷刀出乾坤破", "如来掌起山河动", "浑天玄宇称宝鉴", "天晶不出谁争锋", "我没疯" };
 * ScrollText s = new ScrollText(texts, TextOptions.VERTICAL_LEFT());
 * s.setDirection(Direction.LEFT);
 * s.setLocation(115, 20);
 * add(s);
 * </pre>
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
	private float textX = 0, textY = 0;
	private float space = 5f;
	private LTimer timer = new LTimer(50);

	public ScrollText(String text) {
		this(text, 0, 0, 0, 0);
	}

	public ScrollText(String text, TextOptions opt) {
		this(LSystem.getSystemGameFont(), opt, text, 0, 0, 0, 0);
	}

	public ScrollText(String[] texts) {
		this(texts, TextOptions.LEFT());
	}

	public ScrollText(String[] texts, TextOptions opt) {
		this(LSystem.getSystemGameFont(), opt, texts, 0, 0, 0, 0);
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
		this(LSystem.getSystemGameFont(), new TextOptions(), text, x, y, width, height);
	}

	public ScrollText(TextOptions opt, String text, int x, int y, int width, int height) {
		this(LSystem.getSystemGameFont(), opt, text, x, y, width, height);
	}

	public ScrollText(String text, String font, Style type, int size, int x, int y, int width, int height) {
		this(LFont.getFont(font, type, size), new TextOptions(), text, x, y, width, height);
	}

	public ScrollText(IFont font, TextOptions opt, String text, int x, int y, int width, int height) {
		this(font, opt, new String[] { text }, x, y, width, height);
	}

	public ScrollText(IFont font, TextOptions opt, String[] text, int x, int y, int width, int height) {
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

			boolean intersects = LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null
					&& LSystem.getProcess().getScreen().intersects(textX, textY, getWidth(), getHeight());
			if (_text.getAutoWrap() == AutoWrap.VERTICAL) {
				intersects = LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null
						&& LSystem.getProcess().getScreen().intersects(textX, textY, getHeight(), getWidth());
			}
			if (!intersects) {
				stop = true;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		textX = textMove.x + getX() + _offset.x + offsetX;
		textY = textMove.y + getY() + _offset.y + offsetY;
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

	public boolean isStop() {
		return stop;
	}

	public ScrollText setStop(boolean stop) {
		this.stop = stop;
		return this;
	}

	public int getSpeed() {
		return speed;
	}

	public ScrollText setSpeed(int speed) {
		this.speed = speed;
		return this;
	}

	public LColor getTextColor() {
		return getColor();
	}

	public ScrollText setTextColor(LColor textColor) {
		setColor(textColor);
		return this;
	}

	public LTimer getTimer() {
		return timer;
	}

	public ScrollText setDelay(long d) {
		this.timer.setDelay(d);
		return this;
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

	public ScrollText setDirection(Direction direction) {
		this.direction = direction;
		return this;
	}

	public float getSpace() {
		return space;
	}

	public ScrollText setSpace(float space) {
		this.space = space;
		return this;
	}

	@Override
	public void close() {
		super.close();
		_text.close();
	}
}
