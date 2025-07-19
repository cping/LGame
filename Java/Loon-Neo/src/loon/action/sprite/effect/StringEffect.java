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
import loon.canvas.LColor;
import loon.font.IFont;
import loon.font.Text;
import loon.font.TextOptions;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 一个字符淡出效果类(主要就是减血加血之类效果用的……)
 */
public class StringEffect extends BaseAbstractEffect {

	public static enum StringEffectModel {
		BASE, AWAY, ZOOM, SHAKE, ROTATED, ZOOM_ROTATED, AWAY_ZOOM_ROTATED, AWAY_SHAKE_ZOOM_ROTATED
	}

	private final static float MOVE_VALUE = 1.5f;

	private final static LColor TEMP_COLOR = new LColor();

	private StringEffectModel _model;

	private float _alphaUpdate;

	private float _scaleUpdate;

	private float _rotatedUpdate;

	private float _shakeUpdate;

	private float _awayOffsetUpdate;

	private Vector2f _updatePos;

	private Text _font;

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

	public StringEffect(IFont font, StringEffectModel model, String mes, Vector2f pos, Vector2f update, LColor c) {
		this(TextOptions.LEFT(), font, model, mes, pos, update, c);
	}

	public StringEffect(TextOptions opt, IFont font, String mes, Vector2f pos, Vector2f update, LColor color) {
		this(opt, font, StringEffectModel.BASE, mes, pos, update, color);
	}

	public StringEffect(TextOptions opt, IFont font, StringEffectModel model, String mes, Vector2f pos, Vector2f update,
			LColor color) {
		this(opt, font, model, 0.0125f, mes, pos, update, color);
	}

	public StringEffect(TextOptions opt, IFont font, StringEffectModel model, float updateValue, String mes,
			Vector2f pos, Vector2f update, LColor color) {
		this._font = new Text(font, mes, opt);
		this._alphaUpdate = _scaleUpdate = _rotatedUpdate = updateValue;
		this._shakeUpdate = _awayOffsetUpdate = updateValue * LSystem.getFPS();
		this._updatePos = update;
		this._objectAlpha = this._scaleX = this._scaleY = 1f;
		this._objectRotation = 0f;
		this._model = StringEffectModel.BASE;
		this._completed = false;
		this.setLocation(pos);
		this.setColor(color);
		this.setSize(_font.getWidth(), _font.getHeight());
		this.setLocation(pos.x, pos.y);
		this.setRepaint(true);
	}

	public StringEffectModel getEffectModel() {
		return this._model;
	}

	public StringEffect setStringEffect(StringEffectModel m) {
		this._model = m;
		return this;
	}

	public StringEffect setScaleUpdateValue(float s) {
		this._scaleUpdate = s;
		return this;
	}

	public float getScaleUpdateValue() {
		return _scaleUpdate;
	}

	public float getAlphaUpdateValue() {
		return _alphaUpdate;
	}

	public StringEffect setAlphaUpdateValue(float a) {
		this._alphaUpdate = a;
		return this;
	}

	public float getRotatedUpdateValue() {
		return _rotatedUpdate;
	}

	public StringEffect setRotatedUpdateValue(float r) {
		this._rotatedUpdate = r;
		return this;
	}

	public float getAwayOffsetUpdateValue() {
		return _awayOffsetUpdate;
	}

	public StringEffect setAwayOffsetUpdateValue(float a) {
		this._awayOffsetUpdate = a;
		return this;
	}

	public float getShakeUpdateValue() {
		return _shakeUpdate;
	}

	public StringEffect setShakeUpdateValue(float s) {
		this._shakeUpdate = s;
		return this;
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

	protected void onAwayEffect() {
		getLocation().addSelf(_awayOffsetUpdate, 0f);
	}

	protected void onShakeEffect() {
		getLocation().addSelf(MathUtils.nextBoolean() ? _shakeUpdate : -_shakeUpdate, 0f);
	}

	protected void onZoomEffect() {
		setScale(getScaleX() + _scaleUpdate, getScaleY() + _scaleUpdate);
	}

	protected void onRotatedEffect() {
		final float v = MathUtils.toDegrees(_rotatedUpdate);
		setRotation(MathUtils.nextBoolean() ? (getRotation() + v) : (getRotation() - v));
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			getLocation().addSelf(this._updatePos);
			this._objectAlpha -= _alphaUpdate;
			switch (_model) {
			case BASE:
			default:
				break;
			case AWAY:
				onAwayEffect();
				break;
			case ZOOM:
				onZoomEffect();
				break;
			case SHAKE:
				onShakeEffect();
				break;
			case ROTATED:
				onRotatedEffect();
				break;
			case ZOOM_ROTATED:
				onZoomEffect();
				onRotatedEffect();
				break;
			case AWAY_ZOOM_ROTATED:
				onAwayEffect();
				onZoomEffect();
				onRotatedEffect();
				break;
			case AWAY_SHAKE_ZOOM_ROTATED:
				onAwayEffect();
				onShakeEffect();
				onZoomEffect();
				onRotatedEffect();
				break;
			}
			if (_objectAlpha <= 0) {
				_completed = true;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		_font.paintString(g, drawX(offsetX), drawY(offsetY), _baseColor.multiply(this._objectAlpha, TEMP_COLOR));
	}

	@Override
	public StringEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		_font.close();
	}

}
