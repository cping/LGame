/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 一个多文字用效果类,用来进行一组或以上的文字效果展示及管理(比如模拟视频弹幕之类)
 */
public class TextEffect extends Entity implements BaseEffect {

	protected static class MessageBlock {

		LColor color;
		IFont font;
		String message;

		float x;
		float y;
		float width;
		float height;

		float scale;

		float rotation;

		float stateTime;

		float lifeTime;

		float velocityX;

		float velocityY;

		protected MessageBlock cpy() {
			MessageBlock block = new MessageBlock();
			block.color = color;
			block.font = font;
			block.message = message;
			block.x = x;
			block.y = y;
			block.width = width;
			block.height = height;
			block.scale = scale;
			block.rotation = rotation;
			block.stateTime = stateTime;
			block.lifeTime = lifeTime;
			block.velocityX = velocityX;
			block.velocityY = velocityY;
			return block;
		}
	}

	private final TArray<MessageBlock> texts;

	private TArray<MessageBlock> tempTexts;

	private final LTimer timer;

	private boolean completed;

	private boolean autoRemoved;

	private boolean packed;

	public TextEffect() {
		this(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TextEffect(float x, float y, float width, float height) {
		this.texts = new TArray<>();
		this.tempTexts = new TArray<>();
		this.timer = new LTimer(0);
		this.setLocation(x, y);
		this.setSize(width, height);
		this.setRepaint(true);
	}

	public TextEffect addText(IFont font, String message, LColor color, float lifeTime, float x, float y, float vx,
			float vy) {
		return addText(font, message, color, x, y, lifeTime, 0f, 1f, vx, vy);
	}

	public TextEffect addText(IFont font, String message, LColor color, float x, float y) {
		return addText(font, message, color, x, y, Float.MAX_VALUE, 0f, 1f, 0f, 0f);
	}

	public TextEffect addText(IFont font, String message, LColor color, float lifeTime, float x, float y) {
		return addText(font, message, color, x, y, lifeTime, 0f, 1f, 0f, 0f);
	}

	public TextEffect addText(String message, LColor color, float x, float y) {
		return addText(null, message, color, x, y, Float.MAX_VALUE, 0f, 1f, 0f, 0f);
	}

	public TextEffect addText(String message, LColor color, float lifeTime, float x, float y) {
		return addText(null, message, color, x, y, lifeTime, 0f, 1f, 0f, 0f);
	}

	public TextEffect addText(IFont font, String message, LColor color, float lifeTime, float rotation, float x,
			float y) {
		return addText(font, message, color, x, y, lifeTime, rotation, 1f, 0f, 0f);
	}

	public TextEffect addText(String message, LColor color, float lifeTime, float rotation, float x, float y) {
		return addText(null, message, color, x, y, lifeTime, rotation, 1f, 0f, 0f);
	}

	public TextEffect addText(String message, LColor color, float x, float y, float lifeTime, float rotation,
			float scale, float vx, float vy) {
		return addText(null, message, color, x, y, lifeTime, rotation, scale, vx, vy);
	}

	/**
	 * 注入一组文本信息
	 *
	 * @param font     使用的字体(为null则使用默认的字体LFont)
	 * @param message  文字信息
	 * @param color    文字颜色
	 * @param x        初始x位置
	 * @param y        初始y位置
	 * @param lifeTime 这组文字的生命周期(秒)
	 * @param rotation 文字旋转角度
	 * @param scale    文字缩放比例
	 * @param vx       x轴加速度
	 * @param vy       y轴加速度
	 * @return
	 */
	public TextEffect addText(IFont font, String message, LColor color, float x, float y, float lifeTime,
			float rotation, float scale, float vx, float vy) {
		MessageBlock text = new MessageBlock();
		text.font = (font == null ? LSystem.getSystemGameFont() : font);
		text.message = message;
		if (!StringUtils.isEmpty(text.message)) {
			PointF rect = FontUtils.getTextWidthAndHeight(text.font, text.message);
			text.width = rect.x;
			text.height = rect.y;
		}
		text.color = color;
		text.x = x;
		text.y = y;
		text.lifeTime = lifeTime;
		text.rotation = rotation;
		text.scale = scale;
		text.velocityX = vx;
		text.velocityY = vy;
		texts.add(text);
		tempTexts.add(text.cpy());
		packed = false;
		return this;
	}

	@Override
	public void clear() {
		texts.clear();
		tempTexts.clear();
		super.clear();
	}

	@Override
	public TextEffect reset() {
		super.reset();
		completed = false;
		texts.clear();
		for (int i = 0; i < tempTexts.size; i++) {
			MessageBlock block = tempTexts.get(i);
			if (block != null) {
				texts.add(block.cpy());
			}
		}
		return this;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		final int length = texts.size;
		if (!packed && length > 0) {
			LFont font = null;
			TArray<CharSequence> messages = new TArray<>();
			for (int i = length - 1; i > -1; --i) {
				MessageBlock text = texts.get(i);
				if (text != null) {
					if (text.font != null && text.font instanceof LFont) {
						LFont tmp = (LFont) text.font;
						if (tmp == font || font == null) {
							messages.add(text.message);
						} else {
							LSTRDictionary.get().bind(tmp, messages);
							messages.clear();
						}
						font = tmp;
					}
				}
			}
			if (font != null && messages.size > 0) {
				LSTRDictionary.get().bind(font, messages);
			}
			packed = true;
		}
		if (timer.action(elapsedTime)) {
			float delta = MathUtils.max(elapsedTime / 1000f, LSystem.MIN_SECONE_SPEED_FIXED);
			for (int i = length - 1; i > -1; --i) {
				MessageBlock text = texts.get(i);
				if (text != null) {
					text.stateTime += delta;
					text.x += text.velocityX;
					text.y += text.velocityY;
					if (text.stateTime > text.lifeTime) {
						texts.removeValue(text, true);
					} else if (!getCollisionBox().intersects(text.x, text.y, text.width, text.height)) {
						texts.removeValue(text, true);
					}
				}
			}
		}
		completed = texts.isEmpty();
		if (completed) {
			if (autoRemoved && getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	public int countText() {
		return texts.size;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		for (int i = texts.size - 1; i > -1; --i) {
			MessageBlock text = texts.get(i);
			if (text != null) {
				IFont tmp = g.getFont();
				g.setFont(text.font);
				if (text.scale == 1f && text.rotation == 0f) {
					g.drawString(text.message, drawX(offsetX + text.x), drawY(offsetY + text.y), text.color);
				} else {
					g.drawString(text.message, drawX(offsetX + text.x), drawY(offsetY + text.y), text.scale, text.scale,
							0f, 0f, text.rotation, text.color);
				}
				g.setFont(tmp);
			}
		}
	}

	public boolean isAutoRemoved() {
		return autoRemoved;
	}

	public TextEffect setAutoRemoved(boolean autoRemoved) {
		this.autoRemoved = autoRemoved;
		return this;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		clear();
		completed = true;
		packed = false;
	}

}
