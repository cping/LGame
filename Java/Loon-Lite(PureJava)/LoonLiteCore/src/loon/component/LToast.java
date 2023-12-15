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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.component.skin.ToastSkin;
import loon.font.FontSet;
import loon.font.IFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

/**
 * Android中常见的气泡提示框UI的Loon版
 * 
 * Example:
 * 
 * LToast toast = LToast.makeText(null, "ABCDEFG",Style.ERROR); add(toast);
 * 
 * or:
 * 
 * LToast.makeText("ABCDEFG",Style.ERROR).show();
 * 
 */
public class LToast extends LComponent implements FontSet<LToast> {

	public static enum Style {
		NORMAL, SUCCESS, ERROR
	};

	public static LToast makeText(String text) {
		return makeText(SkinManager.get().getMessageSkin().getFont(), null, text, LENGTH_SHORT);
	}

	public static LToast makeText(String text, Style style) {
		return makeText(SkinManager.get().getMessageSkin().getFont(), null, text, LENGTH_SHORT, style);
	}

	public static LToast makeText(String text, int d) {
		return makeText(SkinManager.get().getMessageSkin().getFont(), null, text, d, Style.NORMAL);
	}

	public static LToast makeText(LComponent owner, String text) {
		return makeText(SkinManager.get().getMessageSkin().getFont(), owner, text, LENGTH_SHORT);
	}

	public static LToast makeText(LComponent owner, String text, Style style) {
		return makeText(SkinManager.get().getMessageSkin().getFont(), owner, text, LENGTH_SHORT, style);
	}

	public static LToast makeText(LComponent owner, String text, int d) {
		return makeText(SkinManager.get().getMessageSkin().getFont(), owner, text, d, Style.NORMAL);
	}

	public static LToast makeText(IFont f, LComponent owner, String text) {
		return makeText(f, owner, text, LENGTH_SHORT);
	}

	public static LToast makeText(IFont f, LComponent owner, String text, Style style) {
		return makeText(f, owner, text, LENGTH_SHORT, style);
	}

	public static LToast makeText(IFont f, LComponent owner, String text, int d) {
		return makeText(f, owner, text, d, Style.NORMAL);
	}

	public static LToast makeText(IFont f, LComponent owner, String text, int d, Style style) {
		LToast toast = null;
		if (owner != null) {
			if (owner instanceof LToast) {
				return (LToast) owner;
			} else if (owner instanceof LContainer) {
				toast = new LToast(f, text, d, owner.x(), owner.y(), (int) owner.getWidth(), (int) owner.getHeight());
				((LContainer) owner).add(toast);
			} else {
				toast = new LToast(f, text, d, owner.x(), owner.y(), (int) owner.getWidth(), (int) owner.getHeight());
			}
		} else {
			toast = new LToast(f, text, d, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		}
		if (style == Style.SUCCESS) {
			toast._backgroundColor = SUCCESS_GRAY;
		}
		if (style == Style.ERROR) {
			toast._backgroundColor = ERROR_RED;
		}
		if (style == Style.NORMAL) {
			toast._backgroundColor = NORMAL_ORANGE;
		}
		return toast;
	}

	public static final int LENGTH_SHORT = 30;
	public static final int LENGTH_LONG = 60;
	public static final LColor ERROR_RED = LColor.maroon;
	public static final LColor SUCCESS_GRAY = LColor.gray;
	public static final LColor NORMAL_ORANGE = LColor.orange.cpy();

	private final float MAX_OPACITY = 1f;
	private final float OPACITY_INCREMENT = 0.05f;

	private boolean _toastInStop = false;

	private boolean _toastOutStop = false;

	private String _displayText;

	private LTimer _timer = new LTimer();
	private LTimer _locked = new LTimer(LSystem.SECOND * 2);
	private LColor _backgroundColor;
	private IFont _toastFont;

	private float _displayTextX = 0f;
	private float _displayTextY = 0f;

	private int _frame_radius = 15;
	private int _frame_length_multiplier = 10;
	private int _duration;
	private int _cellHeight = 30;
	private int _cellWidth = 30;
	private int _displayType;

	private boolean _autoClose = true;

	public LToast(IFont font, String text, int d, int x, int y, int width, int height) {
		this(font, SkinManager.get().getMessageSkin().getFontColor(), text, d, x, y, width, height);
	}

	public LToast(IFont font, LColor fontColor, String text, int d, int x, int y, int width, int height) {
		this(font, null, fontColor, text, d, x, y, width, height);
	}

	public LToast(ToastSkin skin, String text, int d, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getBackgroundTexture(), skin.getFontColor(), text, d, x, y, width, height);
	}

	public LToast(IFont font, LTexture bg, LColor fontColor, String text, int d, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.onlyBackground(bg);
		this._component_baseColor = fontColor;
		this._displayType = ISprite.TYPE_FADE_IN;
		this._objectAlpha = 0f;
		this._duration = d;
		this._toastFont = font;
		this._displayText = text;
		this._cellWidth = _toastFont.stringWidth(_displayText) + (_frame_length_multiplier * 10);
		this._cellHeight = _toastFont.getHeight() + 10;
		if (this._cellHeight < 30) {
			this._cellHeight = 30;
		}
		this._timer.setDelay(this._duration);
		final float displayX = x + ((width / 2) - (_cellWidth / 2));
		final float displayY = (y + ((height / 2) - (_cellHeight / 2))) - _toastFont.getHeight() / 2;
		this.setLocation(displayX, displayY);
		this.setElastic(true);
		this.setText(text);
		this.setSize(_cellWidth, _cellHeight);
		this.setLayer(10000);
	}

	public void fadeIn() {
		this._displayType = ISprite.TYPE_FADE_IN;
		this._objectAlpha = 0f;
	}

	public void fadeOut() {
		this._displayType = ISprite.TYPE_FADE_OUT;
		this._objectAlpha = MAX_OPACITY;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (!_component_visible) {
			return;
		}
		final boolean cliped = g.isClip();
		final RectBox oldClip = g.getClip();
		final int w = (int) this.getWidth();
		final int h = (int) this.getHeight();
		final int oc = g.color();
		final float alpha = g.alpha();
		try {
			if (cliped) {
				g.resetClip();
			}
			g.setColor(_backgroundColor);
			g.setAlpha(_objectAlpha);
			if (_background == null) {
				g.fillRoundRect(x, y, w, h, _frame_radius);
			} else {
				g.draw(_background, x, y, w, h);
			}
			g.setColor(_component_baseColor);
			g.setAlpha(_objectAlpha);
			_toastFont.drawString(g, _displayText, x + _displayTextX, y + _displayTextY);
		} finally {
			g.setColor(oc);
			g.setAlpha(alpha);
			if (cliped) {
				g.setClip(oldClip);
			}
		}
	}

	public float getDisplayTextX() {
		return this._displayTextX;
	}

	public float getDisplayTextY() {
		return this._displayTextY;
	}

	public LToast setDisplayTextX(float x) {
		this._displayTextX = x;
		return this;
	}

	public LToast setDisplayTextY(float y) {
		this._displayTextY = y;
		return this;
	}

	@Override
	public LComponent setBackground(LTexture texture) {
		this._background = texture;
		return this;
	}

	@Override
	public LComponent setBackground(String filePath) {
		setBackground(LSystem.loadTexture(filePath));
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (_timer.action(elapsedTime)) {
			if (_displayType == ISprite.TYPE_FADE_IN) {
				_objectAlpha += OPACITY_INCREMENT;
				_objectAlpha = (MathUtils.min(_objectAlpha, MAX_OPACITY));
				if (_objectAlpha >= MAX_OPACITY) {
					_toastInStop = true;
				}
			} else {
				_objectAlpha -= OPACITY_INCREMENT;
				_objectAlpha = (MathUtils.max(_objectAlpha, OPACITY_INCREMENT));
				if (_objectAlpha <= OPACITY_INCREMENT) {
					_toastOutStop = true;
					setVisible(false);
					close();
					if (getScreen() != null) {
						getScreen().remove(this);
					}
					if (_desktop != null) {
						_desktop.remove(this);
					}
				}
			}
		}
		if (_toastInStop && _autoClose && _locked.action(elapsedTime)) {
			fadeOut();
		}
	}

	public LComponent setText(String text) {
		_displayText = text;
		_displayTextX = MathUtils.min(_cellWidth / 2 - 1, (_cellWidth - _toastFont.stringWidth(_displayText)) / 2);
		_displayTextY = MathUtils.min(_cellHeight / 2 - 1, (_cellHeight - _toastFont.stringHeight(_displayText)) / 2)
				- 1;
		return this;
	}

	public LComponent setDuration(int d) {
		this._duration = d;
		_timer.setDelay(this._duration);
		return this;
	}

	@Override
	public LComponent setBackground(LColor color) {
		super.setBackground(color);
		_backgroundColor = color;
		return this;
	}

	public LComponent setForeground(LColor foregroundColor) {
		_component_baseColor = foregroundColor;
		return this;
	}

	public boolean isInStop() {
		return _toastInStop;
	}

	public boolean isOutStop() {
		return _toastOutStop;
	}

	public boolean isStop() {
		return _toastInStop && _toastOutStop;
	}

	public boolean isAutoClose() {
		return _autoClose;
	}

	public float getOpacity() {
		return _objectAlpha;
	}

	@Override
	public IFont getFont() {
		return _toastFont;
	}

	@Override
	public LToast setFont(IFont f) {
		this._toastFont = f;
		return this;
	}

	@Override
	public LToast setFontColor(LColor color) {
		this._component_baseColor = color;
		return null;
	}

	@Override
	public LColor getFontColor() {
		return _component_baseColor.cpy();
	}

	public LToast setAutoClose(boolean c) {
		this._autoClose = c;
		return this;
	}

	@Override
	public String getUIName() {
		return "Toast";
	}

	@Override
	public void destory() {
	}

}
