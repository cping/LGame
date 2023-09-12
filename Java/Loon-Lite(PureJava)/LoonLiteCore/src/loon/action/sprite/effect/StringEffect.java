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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.action.map.Field2D;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.font.Text;
import loon.font.TextOptions;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 一个字符淡出效果类(主要就是减血加血之类效果用的……)
 */
public class StringEffect extends Entity implements BaseEffect {

	public final static float MOVE_VALUE = 1.5f;

	private LTimer delayTimer = new LTimer(0);

	private Vector2f _updatePos;

	private Text _font;

	private boolean _completed;

	private boolean _autoRemoved;

	/**
	 * not Move
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect notMove(String mes, Vector2f pos, LColor color) {
		return notMove(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * not Move
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect notMove(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(0, 0), color).setAutoRemoved(true);
	}

	/**
	 * ↙
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Ddown(String mes, Vector2f pos, LColor color) {
		return m45Ddown(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ↙
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Ddown(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(-MOVE_VALUE, MOVE_VALUE), color).setAutoRemoved(true);
	}

	/**
	 * ↗
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Dup(String mes, Vector2f pos, LColor color) {
		return m45Dup(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ↗
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Dup(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(MOVE_VALUE, -MOVE_VALUE), color).setAutoRemoved(true);
	}

	/**
	 * ↘
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Dright(String mes, Vector2f pos, LColor color) {
		return m45Dright(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ↘
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Dright(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(MOVE_VALUE, MOVE_VALUE), color).setAutoRemoved(true);
	}

	/**
	 * ↖
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Dleft(String mes, Vector2f pos, LColor color) {
		return m45Dleft(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ↖
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect m45Dleft(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(-MOVE_VALUE, -MOVE_VALUE), color).setAutoRemoved(true);
	}

	/**
	 * →
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect right(String mes, Vector2f pos, LColor color) {
		return right(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * →
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect right(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(MOVE_VALUE, 0), color).setAutoRemoved(true);
	}

	/**
	 * ←
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect left(String mes, Vector2f pos, LColor color) {
		return left(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ←
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect left(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(-MOVE_VALUE, 0), color).setAutoRemoved(true);
	}

	/**
	 * ↑
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect up(String mes, Vector2f pos, LColor color) {
		return up(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ↑
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect up(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(0, -MOVE_VALUE), color).setAutoRemoved(true);
	}

	/**
	 * ↓
	 * 
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect down(String mes, Vector2f pos, LColor color) {
		return down(LSystem.getSystemGameFont(), mes, pos, color).setAutoRemoved(true);
	}

	/**
	 * ↓
	 * 
	 * @param font
	 * @param mes
	 * @param pos
	 * @param color
	 * @return
	 */
	public final static StringEffect down(IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Vector2f.at(0, MOVE_VALUE), color).setAutoRemoved(true);
	}

	public final static StringEffect move(int dir, IFont font, String mes, Vector2f pos, LColor color) {
		return new StringEffect(font, mes, pos, Field2D.getDirection(dir).cpy(), color).setAutoRemoved(true);
	}

	public StringEffect(String mes, Vector2f pos, Vector2f update, LColor c) {
		this(TextOptions.LEFT(), LSystem.getSystemGameFont(), mes, pos, update, c);
	}

	public StringEffect(IFont font, String mes, Vector2f pos, Vector2f update, LColor c) {
		this(TextOptions.LEFT(), font, mes, pos, update, c);
	}

	public StringEffect(TextOptions opt, IFont font, String mes, Vector2f pos, Vector2f update, LColor color) {
		this._font = new Text(font, mes, opt);
		this._updatePos = update;
		this._objectAlpha = 1f;
		this._completed = false;
		this.setLocation(pos);
		this.setColor(color);
		this.setSize(_font.getWidth(), _font.getHeight());
		this.setLocation(pos.x, pos.y);
		this.setRepaint(true);
	}

	public float getFontWidth() {
		return _font == null ? 0 : _font.getWidth();
	}

	public IFont getFont() {
		return _font == null ? null : _font.getFont();
	}

	public Text getText() {
		return _font;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (_completed) {
			return;
		}
		if (delayTimer.action(elapsedTime)) {
			getLocation().addSelf(this._updatePos);
			this._objectAlpha -= 0.0125f;
			if (_objectAlpha <= 0) {
				_completed = true;
			}
			if (_completed) {
				if (_autoRemoved) {
					if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
						LSystem.getProcess().getScreen().remove(this);
					}
					if (getSprites() != null) {
						getSprites().remove(this);
					}
				}
			}
		}

	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (_completed) {
			return;
		}
		_font.paintString(g, drawX(offsetX), drawY(offsetY), _baseColor.multiply(this._objectAlpha));
	}

	public StringEffect setDelay(long d) {
		delayTimer.setDelay(d);
		return this;
	}

	public long getDelay() {
		return delayTimer.getDelay();
	}

	@Override
	public boolean isCompleted() {
		return _completed;
	}

	@Override
	public StringEffect setStop(boolean c) {
		this._completed = c;
		return this;
	}
	
	public boolean isAutoRemoved() {
		return _autoRemoved;
	}

	public StringEffect setAutoRemoved(boolean autoRemoved) {
		this._autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public void close() {
		super.close();
		_font.close();
	}

}
