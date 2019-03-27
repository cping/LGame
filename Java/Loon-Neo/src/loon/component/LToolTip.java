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
	private int initialDelay = 60;

	// 默认关闭时间
	private int dismissDelay = 180;

	// 中间延迟
	private int reshowDelay = 30;

	public int initial, dismiss, reshow, dismissTime;

	private LComponent tooltip;

	private String tipText = "";

	public boolean tooltipChanged, dismissing;

	private boolean fadeCompleted;

	private boolean running;

	private float currentFrame = 0, fadeTime = 60;

	private Text _text;

	private LColor _fontColor;

	public LToolTip() {
		this("");
	}

	public LToolTip(String text) {
		this(SkinManager.get().getMessageSkin().getFont(), text, SkinManager
				.get().getMessageSkin().getBackgroundTexture(), SkinManager
				.get().getMessageSkin().getFontColor());
	}

	public LToolTip(IFont font, String text, LColor fontColor) {
		this(font, text, SkinManager.get().getMessageSkin()
				.getBackgroundTexture(), fontColor);
	}

	public LToolTip(IFont font, String text) {
		this(font, text, SkinManager.get().getMessageSkin()
				.getBackgroundTexture(), SkinManager.get().getMessageSkin()
				.getFontColor());
	}

	public LToolTip(IFont font, String text, LTexture background, LColor color) {
		super(0, 0, 0, 0);
		this._drawBackground = false;
		this._background = background;
		this._text = new Text(font, text);
		this._fontColor = color;
		this.setLayer(10000);
		this.setAlpha(0);
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (this.isVisible()) {
			if (this.tooltip != null && !this.tooltipChanged) {
				if (dismissing && running) {
			
					if (!fadeCompleted) {
						currentFrame++;
						if (currentFrame == fadeTime) {
							setAlpha(1f);
							fadeCompleted = true;
							return;
						}
					}
					_alpha = (currentFrame / fadeTime);
				}
				if (this.dismiss++ >= this.dismissTime) {
					this.setToolTipComponent(null);
					this.setVisible(false);
					this.dismissing = false;
					this.dismiss = 0;
					this.reshow = 0;
				}
			} else {
				this.setVisible(false);
			}

		} else {
			if (this.reshow > 0) {
				this.reshow--;
			}
			if (this.tooltip != null
					&& (this.reshow > 0 || ++this.initial >= this.initialDelay)) {
				this.showTip();

			}

		}
	}

	public LToolTip setFadeTime(float delay) {
		this.fadeTime = delay;
		return this;
	}

	public float getFadeTime() {
		return this.fadeTime;
	}

	public LToolTip showTip() {
		if (this.tooltip == null) {
			return this;
		}
		this.setVisible(true);
		this.initial = 0;
		this.dismiss = 0;
		this.currentFrame = 0;
		this.reshow = this.reshowDelay;
		this.tooltipChanged = false;
		if (this.tooltip != null) {
			if (!this.tooltip.getToolTipText().equals(this.tipText)) {
				this.tipText = this.tooltip.getToolTipText();
				String[] componentTipText = StringUtils.split(this.tipText,
						'\n');
				this.dismissTime = (this.dismissDelay * componentTipText.length);
			}
		}
		this._alpha = 0f;
		this.running = true;
		this.fadeCompleted = false;
		return this;
	}

	public LComponent getToolTipComponent() {
		return this.tooltip;
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
		if (this.tooltip == tooltip) {
			return this;
		}
		this.tooltip = tooltip;
		this.tooltipChanged = true;
		if (!this.isVisible()) {
			this.initial = 0;
		}
		return this;
	}

	public int getInitialDelay() {
		return this.initialDelay;
	}

	public LToolTip setInitialDelay(int i) {
		this.initialDelay = i;
		return this;
	}

	public int getDismissDelay() {
		return this.dismissDelay;
	}

	public LToolTip setDismissDelay(int i) {
		this.dismissDelay = i;
		return this;
	}

	public int getReshowDelay() {
		return this.reshowDelay;
	}

	public LToolTip setReshowDelay(int i) {
		this.reshowDelay = i;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (component == null || tooltip == null) {
			return;
		}
		LComponent tooltip = ((LToolTip) component).getToolTipComponent();
		String tipText = tooltip.getToolTipText();
		if (!_text.getText().equals(tipText)) {
			_text.setText(tipText);
		}
		float posX = tooltip.getScreenX() + tooltip.getWidth() / 2;
		float posY = tooltip.getScreenY() + tooltip.getHeight() / 2;
		float width = _text.getWidth() + 5;
		float height = _text.getHeight() + 8;
		if (getScreen().contains(posX, posY, width, height)) {
			if (_background == null) {
				g.fillRect(posX, posY, width, height, LColor.darkGray);
			} else {
				g.draw(_background, posX, posY, width, height);
			}
			_text.paintString(g, posX + 2, posY, _fontColor);
		} else {
			if (_background == null) {
				g.fillRect(posX - width, posY, width, height, LColor.darkGray);
			} else {
				g.draw(_background, posX - width, posY, width, height);
			}
			_text.paintString(g, posX + 2 - width, posY, _fontColor);
		}

	}

	@Override
	public void close() {
		super.close();
		_text.close();
	}

	@Override
	public String getUIName() {
		return "ToolTip";
	}

}
