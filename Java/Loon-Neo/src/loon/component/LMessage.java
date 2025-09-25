/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 信息显示用UI,支持一些简单的字符命令用于构建显示文字
 */
public class LMessage extends LContainer implements FontSet<LMessage> {

	private TArray<String> _tempMessageList;

	private int _baseMessageLength;

	private int _maxMessageLength;

	private Animation _animation;

	private IFont _messageFont;

	private LColor _fontColor = LColor.white;

	private long _printTime, _totalDuration;

	private int _tempColor;

	private float _dx, _dy, _dw, _dh;

	private Print _print;

	public LMessage(int width, int height) {
		this(SkinManager.get().getMessageSkin().getFont(), 0, 0, width, height);
	}

	public LMessage(IFont font, int width, int height) {
		this(font, 0, 0, width, height);
	}

	public LMessage(int x, int y, int width, int height) {
		this(SkinManager.get().getMessageSkin().getFont(), (LTexture) null, x, y, width, height);
	}

	public LMessage(IFont font, int x, int y, int width, int height) {
		this(font, (LTexture) null, x, y, width, height);
	}

	public LMessage(IFont font, String fileName, int x, int y) {
		this(font, LSystem.loadTexture(fileName), x, y);
	}

	public LMessage(LTexture formImage, int x, int y) {
		this(SkinManager.get().getMessageSkin().getFont(), formImage, x, y, formImage.getWidth(),
				formImage.getHeight());
	}

	public LMessage(IFont font, LTexture formImage, int x, int y) {
		this(font, formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LMessage(IFont font, LTexture formImage, int x, int y, int width, int height) {
		this(font, formImage, x, y, width, height, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LMessage(MessageSkin skin, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getBackgroundTexture(), x, y, width, height, skin.getFontColor());
	}

	public LMessage(IFont font, LTexture formImage, int x, int y, int width, int height, LColor color) {
		super(x, y, width, height);
		this._fontColor = color;
		this._messageFont = (font == null ? SkinManager.get().getMessageSkin().getFont() : font);
		this._animation = new Animation();
		if (formImage == null) {
			this.setBackground(LSystem.base().graphics().finalColorTex());
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
			if (width == -1) {
				width = formImage.getWidth();
			}
			if (height == -1) {
				height = formImage.getHeight();
			}
		}
		this._print = new Print(getLocation(), _messageFont, width, height);
		this.setTipIcon(LSystem.getSystemImagePath() + "creese.png");
		this._totalDuration = 80;
		this.customRendering = true;
		this.setWait(false);
		this.setElastic(false);
		this.setLocked(true);
	}

	public boolean isGradientFontColor() {
		return _print.isGradientFontColor();
	}

	public LMessage setGradientFontColor(boolean g) {
		_print.setGradientFontColor(g);
		return this;
	}

	public float getSpaceTextX() {
		return _print.getSpaceTextX();
	}

	public float getSpaceTextY() {
		return _print.getSpaceTextY();
	}

	public LMessage setSpaceTextX(float x) {
		_print.setSpaceTextX(x);
		return this;
	}

	public LMessage setSpaceTextY(float y) {
		_print.setSpaceTextY(y);
		return this;
	}

	public LMessage setSpaceText(float off) {
		return setSpaceText(off, off);
	}

	public LMessage setSpaceText(float x, float y) {
		setSpaceTextX(x);
		setSpaceTextY(y);
		return this;
	}

	public LMessage setWait(boolean flag) {
		_print.setWait(flag);
		return this;
	}

	public boolean isWait() {
		return _print.isWait();
	}

	public LMessage complete() {
		_print.complete();
		return this;
	}

	public LMessage setLeftOffset(int left) {
		_print.setLeftOffset(left);
		return this;
	}

	public LMessage setTopOffset(int top) {
		_print.setTopOffset(top);
		return this;
	}

	public int getLeftOffset() {
		return _print.getLeftOffset();
	}

	public int getTopOffset() {
		return _print.getTopOffset();
	}

	public int getMessageLength() {
		return _print.getMessageLength();
	}

	public LMessage setMessageLength(int messageLength) {
		_print.setMessageLength(messageLength);
		setBaseMessageLength(messageLength);
		return this;
	}

	public LMessage setBaseMessageLength(int messageLength) {
		this._baseMessageLength = messageLength;
		return this;
	}

	public int getBaseMessageLength() {
		return this._baseMessageLength;
	}

	public LMessage setMaxMessageLength(int messageLength) {
		this._maxMessageLength = messageLength;
		return this;
	}

	public int getMaxMessageLength() {
		return this._maxMessageLength;
	}

	public LMessage setTipIcon(String fileName) {
		setTipIcon(LSystem.loadTexture(fileName));
		return this;
	}

	public LMessage setTipIcon(LTexture icon) {
		if (icon != null) {
			_print.setCreeseIcon(icon);
			setDisplayIconFlag(true);
		}
		return this;
	}

	public LMessage setNotTipIcon() {
		_print.setCreeseIcon(null);
		setDisplayIconFlag(false);
		return this;
	}

	public LMessage setDisplayIconFlag(boolean flag) {
		_print.setIconFlag(flag);
		return this;
	}

	public boolean isDisplayIconFlag() {
		return _print.isIconFlag();
	}

	public float getOffsetIconX() {
		return _print.getOffsetIconX();
	}

	public LMessage setOffsetIconX(float offsetIconX) {
		_print.setOffsetIconX(offsetIconX);
		return this;
	}

	public float getOffsetIconY() {
		return _print.getOffsetIconY();
	}

	public LMessage setOffsetIconY(float offsetIconY) {
		_print.setOffsetIconX(offsetIconY);
		return this;
	}

	public LMessage setEnglish(boolean e) {
		_print.setEnglish(true);
		return this;
	}

	public boolean isEnglish() {
		return _print.isEnglish();
	}

	public LMessage setDelay(long delay) {
		this._totalDuration = (delay < 1 ? 1 : delay);
		return this;
	}

	public long getDelay() {
		return _totalDuration;
	}

	public boolean isComplete() {
		return _print.isComplete();
	}

	public LMessage setPauseIconAnimationLocation(float dx, float dy) {
		this._dx = dx;
		this._dy = dy;
		return this;
	}

	public LMessage setMessage(String context) {
		return this.setMessage(context, false);
	}

	public LMessage setMessage(String context, boolean isComplete) {
		return setMessage(context, isComplete, false);
	}

	public LMessage setMessage(String context, boolean isComplete, boolean autoLength) {
		if (StringUtils.isEmpty(context)) {
			return this;
		}
		if (_maxMessageLength > 0) {
			_print.setMessageLength(_maxMessageLength);
		} else {
			final PointF size = FontUtils.getTextWidthAndHeight(_messageFont, context);
			if ((autoLength || LSTRFont.isAllInBaseCharsPool(context))) {
				int maxLen;
				if (getWidth() == 0) {
					maxLen = ((int) ((MathUtils.min(getWidth(), size.x) / _messageFont.getSize()) + 1));
				} else {
					maxLen = ((int) (size.x / _messageFont.getSize()) + 1);
				}
				final float maxSize = (getWidth() == 0) ? LSystem.viewSize.getWidth() : getWidth();
				_tempMessageList = FontUtils.splitLines(context, _messageFont,
						maxSize - MathUtils.ifloor(_messageFont.getSize() * 1.25f), _tempMessageList);
				int curMaxLen = 0;
				for (int i = _tempMessageList.size - 1; i > -1; i--) {
					final String mes = _tempMessageList.get(i);
					if (mes != null) {
						curMaxLen = MathUtils.max(mes.length(), curMaxLen);
					}
				}
				_print.setMessageLength(MathUtils.clamp(_baseMessageLength, curMaxLen, maxLen));
			} else if (_baseMessageLength != 0) {
				_print.setMessageLength(_baseMessageLength);
			}
		}
		_print.setMessage(context, _messageFont, isComplete);
		return this;
	}

	public String getMessage() {
		return _print.getMessage();
	}

	public Print getPrint() {
		return _print;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.doClick();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (!_input.isMoving()) {
			super.processTouchPressed();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!_input.isMoving()) {
			super.processTouchReleased();
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (_print.isComplete()) {
			_animation.update(elapsedTime);
		}
		_printTime += elapsedTime;
		if (_printTime >= _totalDuration) {
			_printTime = _printTime % _totalDuration;
			_print.next();
		}
	}

	@Override
	protected synchronized void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!isVisible()) {
			return;
		}
		_tempColor = g.color();
		_print.draw(g, _fontColor);
		if (_print.isComplete() && _animation != null) {
			if (_animation.getSpriteImage() != null) {
				float alpha = g.getAlpha();
				g.setAlpha(1f);
				updateIcon();
				g.draw(_animation.getSpriteImage(), _dx, _dy);
				g.setAlpha(alpha);
			}
		}
		g.setColor(_tempColor);
	}

	@Override
	protected void processTouchDragged() {
		if (!_dragLocked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this._input.getTouchDX(), this._input.getTouchDY());
			if (_clickListener != null) {
				_clickListener.DragClick(this, getUITouchX(), getUITouchY());
			}
			this.updateIcon();
		}
		super.dragClick();
	}

	public void setPauseIconAnimation(Animation animation) {
		this._animation = animation;
		if (animation != null) {
			LTexture image = animation.getSpriteImage(0);
			if (image != null) {
				this._dw = image.getWidth();
				this._dh = image.getHeight();
				this.updateIcon();
			}
		}
	}

	private void updateIcon() {
		this.setPauseIconAnimationLocation((int) (getScreenX() + getWidth() - _dw / 2 - 20),
				(int) (getScreenY() + getHeight() - _dh - 10));
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	@Override
	public LMessage setFontColor(LColor fontColor) {
		this._fontColor = fontColor;
		return this;
	}

	@Override
	public IFont getFont() {
		return getMessageFont();
	}

	public IFont getMessageFont() {
		return _messageFont;
	}

	@Override
	public LMessage setFont(IFont font) {
		return this.setMessageFont(font);
	}

	/**
	 * 注入一个实现了IFont接口的字体
	 * 
	 * @param messageFont
	 */
	public LMessage setMessageFont(IFont messageFont) {
		this._messageFont = messageFont;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "Message";
	}

	@Override
	public void destory() {
		if (_print != null) {
			_print.close();
			_print = null;
		}
		if (_animation != null) {
			_animation.close();
			_animation = null;
		}
	}

}
