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
import loon.utils.StringUtils;

/**
 * 针对其它组件UI的信息提示用UI
 */
public class LToolTip extends LComponent {

	// 默认悬浮时间
	private int _initialDelay = 60;

	// 默认关闭时间
	private int _dismissDelay = 180;

	// 中间延迟
	private int _reshowDelay = 30;

	protected int _initialFlag, _dismiss, _reshow, _dismissTime;

	private LComponent _tooltip;

	private String _tipText = LSystem.EMPTY;

	protected boolean _tooltipChanged, _dismissing;

	private boolean _fadeCompleted;

	private boolean _running;

	private float _currentFrame = 0, _fadeTime = 60;

	private Text _text;

	private LColor _fontColor;

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
		super(0, 0, 0, 0);
		this._text = new Text(font, text);
		this._fontColor = color;
		this.onlyBackground(bg);
		this.setLayer(10000);
		this.setAlpha(0);
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (this.isVisible()) {
			if (this._tooltip != null && !this._tooltipChanged) {
				if (_dismissing && _running) {

					if (!_fadeCompleted) {
						_currentFrame++;
						if (_currentFrame == _fadeTime) {
							setAlpha(1f);
							_fadeCompleted = true;
							return;
						}
					}
					_objectAlpha = (_currentFrame / _fadeTime);
				}
				if (this._dismiss++ >= this._dismissTime) {
					this.setToolTipComponent(null);
					this.setVisible(false);
					this._dismissing = false;
					this._dismiss = 0;
					this._reshow = 0;
				}
			} else {
				this.setVisible(false);
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
		this._initialFlag = 0;
		this._dismiss = 0;
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

	public LToolTip setToolTipComponent(LComponent _tooltip) {
		if (_tooltip != null) {
			if (_tooltip.getToolTipParent() != null) {
				_tooltip = _tooltip.getToolTipParent();
			}
			if (_tooltip.getToolTipText() == null) {
				_tooltip = null;
			}
		}
		if (this._tooltip == _tooltip) {
			return this;
		}
		this._tooltip = _tooltip;
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
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (component == null || _tooltip == null) {
			return;
		}
		LComponent _tooltip = ((LToolTip) component).getToolTipComponent();
		String tipText = _tooltip.getToolTipText();
		if (!_text.getText().equals(tipText)) {
			_text.setText(tipText);
		}
		float posX = _tooltip.getScreenX() + _tooltip.getWidth() / 2;
		float posY = _tooltip.getScreenY() + _tooltip.getHeight() / 2;
		float width = _text.getWidth() + 6;
		float height = _text.getHeight() + 8;
		float currentX = posX;
		if (!getScreen().contains(posX, posY, width, height)) {
			currentX = posX - width;
		}
		if (_background == null) {
			g.fillRect(currentX, posY, width, height, LColor.darkGray);
		} else {
			g.draw(_background, currentX, posY, width, height);
		}
		_text.paintString(g, currentX + (width - _text.getWidth()) / 2, posY + (height - _text.getHeight()) / 2,
				_fontColor);
	}

	public int getDismiss() {
		return _dismiss;
	}

	public void setDismiss(int d) {
		this._dismiss = d;
	}

	public int getDismissTime() {
		return _dismissTime;
	}

	public void setDismissTime(int d) {
		this._dismissTime = d;
	}

	public boolean isDismissing() {
		return _dismissing;
	}

	public void setDismissing(boolean d) {
		this._dismissing = d;
	}

	@Override
	public String getUIName() {
		return "ToolTip";
	}

	@Override
	public void destory() {
		_text.close();
	}

}
