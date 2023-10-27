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
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.font.IFont;
import loon.font.Text;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * 针对其它组件UI的信息提示用UI
 */
public class LToolTip extends LComponent {

	// 默认悬浮时间
	private int _initialDelay;

	// 默认关闭时间
	private int _dismissDelay;

	// 中间延迟
	private int _reshowDelay;

	protected int _initialFlag, _dismiss, _reshow, _dismissTime;

	private LComponent _tooltip;

	private String _tipText = LSystem.EMPTY;

	protected boolean _tooltipChanged, _dismissing;

	private boolean _drawToScreenDesktop;

	private boolean _fadeCompleted;

	private boolean _running;

	private boolean _lockedFadeIn;

	private float _currentFrame, _fadeTime;

	private Text _text;

	private LColor _fontColor;

	private IFont _tipFont;

	public LToolTip() {
		this(LSystem.EMPTY);
	}

	public LToolTip(String text) {
		this(SkinManager.get().getMessageSkin().getFont(), text,
				SkinManager.get().getMessageSkin().getBackgroundTexture(),
				SkinManager.get().getMessageSkin().getFontColor());
	}

	public LToolTip(IFont font, String text, LColor fontColor) {
		this(font, text, SkinManager.get().getMessageSkin().getBackgroundTexture(), fontColor);
	}

	public LToolTip(IFont font, String text) {
		this(font, text, SkinManager.get().getMessageSkin().getBackgroundTexture(),
				SkinManager.get().getMessageSkin().getFontColor());
	}

	public LToolTip(IFont font, String text, LTexture bg, LColor color) {
		this(font, text, bg, color, 0f, 0f);
	}

	public LToolTip(IFont font, String text, LTexture bg, LColor color, float x, float y) {
		super(MathUtils.ifloor(x), MathUtils.ifloor(y), 0, 0);
		this._tipFont = font;
		this._fontColor = color;
		this._drawToScreenDesktop = true;
		// 默认悬浮时间
		this._initialDelay = 60;
		// 默认关闭时间
		this._dismissDelay = 180;
		// 中间延迟
		this._reshowDelay = 30;
		// 淡入时间
		this._fadeTime = 60;
		this.setText(font, text);
		this.setLocked(true);
		this.setLayer(10000);
		this.setAlpha(0f);
		this.onlyBackground(bg);
	}

	public LToolTip setText(String mes) {
		return setText(_tipFont, mes);
	}

	public LToolTip setText(IFont font, String mes) {
		if (mes != null) {
			this._tipFont = (font == null) ? SkinManager.get().getMessageSkin().getFont() : font;
			if (this._text == null) {
				this._text = new Text(font, mes);
			} else {
				this._text.setText(font, mes);
			}
			final float w = _text.getWidth() + font.getSize();
			final float h = _text.getHeight() + font.getHeight() / 2f;
			this.setSize(w, h);
		}
		return this;
	}

	public boolean isRunning() {
		return _running;
	}

	public LToolTip setLockedFadeIn(boolean l) {
		this._lockedFadeIn = l;
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (this.isVisible()) {
			if (this._tooltip != null && !this._tooltipChanged) {
				if (_dismissing && _running) {
					if (!_fadeCompleted) {
						_currentFrame++;
						if (_currentFrame >= _fadeTime) {
							setAlpha(1f);
							if (!_lockedFadeIn) {
								_running = false;
							}
							_fadeCompleted = true;
							return;
						}
					}
					_objectAlpha = (_currentFrame / _fadeTime);
					if (_lockedFadeIn && _objectAlpha >= 1f && _currentFrame >= _fadeTime) {
						setAlpha(1f);
						_fadeCompleted = true;
						return;
					}
				} else if (this._dismiss++ >= this._dismissTime) {

					this.setToolTipComponent(null);
					this.setVisible(false);
					this._dismissing = false;
					this._dismiss = 0;
					this._reshow = 0;
				} else {
					this.setAlpha(1f);
					this._running = false;
					this._fadeCompleted = true;
				}

			} else {
				this.setVisible(false);
				this._running = false;
			}
		} else {
			if (this._reshow > 0) {
				this._reshow--;
			}
			if (this._tooltip != null && (this._reshow > 0 || ++this._initialFlag >= this._initialDelay)) {
				this.showTip();
			}
		}
	}

	public LToolTip setFadeTime(float delay) {
		this._fadeTime = delay;
		return this;
	}

	public float getFadeTime() {
		return this._fadeTime;
	}

	public LToolTip showTip() {
		if (this._tooltip == null) {
			return this;
		}
		this.setVisible(true);
		this._reshow = 0;
		this._initialFlag = 0;
		this._dismiss = 0;
		this._dismissing = true;
		this._currentFrame = 0;
		this._reshow = this._reshowDelay;
		this._tooltipChanged = false;
		if (this._tooltip != null) {
			if (!this._tooltip.getToolTipText().equals(this._tipText)) {
				this._tipText = this._tooltip.getToolTipText();
				this._dismissTime = (this._dismissDelay * StringUtils.split(this._tipText, LSystem.LF).length);
			}
		}
		this._objectAlpha = 0f;
		this._running = true;
		this._fadeCompleted = false;
		return this;
	}

	public LComponent getToolTipComponent() {
		return this._tooltip;
	}

	public LToolTip setToolTipComponent(LComponent tooltip) {
		if (tooltip != null) {
			if (tooltip.getToolTipParent() != null) {
				tooltip = tooltip.getToolTipParent();
			}
			if (tooltip.getToolTipText() == null) {
				tooltip = null;
			}
		}
		if (this._tooltip == tooltip) {
			return this;
		}
		this._tooltip = tooltip;
		this._tooltipChanged = true;
		if (!this.isVisible()) {
			this._initialFlag = 0;
		}
		return this;
	}

	public int getInitialDelay() {
		return this._initialDelay;
	}

	public LToolTip setInitialDelay(int i) {
		this._initialDelay = i;
		return this;
	}

	public int getDismissDelay() {
		return this._dismissDelay;
	}

	public LToolTip setDismissDelay(int i) {
		this._dismissDelay = i;
		return this;
	}

	public int getReshowDelay() {
		return this._reshowDelay;
	}

	public LToolTip setReshowDelay(int i) {
		this._reshowDelay = i;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (_objectAlpha <= 0f) {
			return;
		}
		if (_tooltip == null) {
			return;
		}
		if (_drawToScreenDesktop) {
			drawToScreen(g);
		} else {
			drawToUI(g, x, y);
		}
	}

	protected void drawToScreen(GLEx g) {
		final String tipText = _tooltip.getToolTipText();
		if (!_text.getText().equals(tipText)) {
			_text.setText(tipText);
		}
		final IFont font = _text.getFont();
		final float posX = _tooltip.getScreenX() + _tooltip.getWidth() / 2f;
		final float posY = _tooltip.getScreenY() + _tooltip.getHeight() / 2f;
		final float width = _text.getWidth() + font.getSize();
		final float height = _text.getHeight() + font.getHeight() / 2f;
		float currentX = posX;
		if (!getScreen().contains(posX, posY, width, height)) {
			currentX = (getScreen().getX() + getScreen().getScreenWidth()) - width - font.getSize() / 2f;
		}
		if (_background == null) {
			g.fillRect(currentX, posY, width, height, LColor.darkGray);
		} else {
			g.draw(_background, currentX, posY, width, height);
		}
		_text.paintString(g, currentX + (width - _text.getWidth()) / 2f, posY + (height - _text.getHeight()) / 2f,
				_fontColor);
	}

	protected void drawToUI(GLEx g, int x, int y) {
		if (_tooltip == null) {
			return;
		}
		final String tipText = _tooltip.getToolTipText();
		if (!_text.getText().equals(tipText)) {
			_text.setText(tipText);
		}
		final IFont font = _text.getFont();
		final float width = _text.getWidth() + font.getSize();
		final float height = _text.getHeight() + font.getHeight() / 2f;
		if (_background == null) {
			g.fillRect(x, y, width, height, LColor.darkGray);
		} else {
			g.draw(_background, x, y, width, height);
		}
		_text.paintString(g, x + (width - _text.getWidth()) / 2f, y + (height - _text.getHeight()) / 2f, _fontColor);
	}

	public boolean isCompleted() {
		return _fadeCompleted;
	}

	public int getDismiss() {
		return _dismiss;
	}

	public LToolTip setDismiss(int d) {
		this._dismiss = d;
		return this;
	}

	public int getDismissTime() {
		return _dismissTime;
	}

	public LToolTip setDismissTime(int d) {
		this._dismissTime = d;
		return this;
	}

	public boolean isDismissing() {
		return _dismissing;
	}

	public LToolTip setDismissing(boolean d) {
		this._dismissing = d;
		return this;
	}

	public boolean isDrawToScreenDesktop() {
		return _drawToScreenDesktop;
	}

	public LToolTip setDrawToScreenDesktop(boolean d) {
		this._drawToScreenDesktop = d;
		return this;
	}

	@Override
	public String getUIName() {
		return "ToolTip";
	}

	@Override
	public void destory() {
		_text.close();
		_running = false;
	}

}
