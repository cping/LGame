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
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.ProgressSkin;
import loon.component.skin.SkinManager;
import loon.events.ValueListener;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 一个进度条用UI
 */
public class LProgress extends LComponent {

	private float minValue = 0f;

	private float maxValue = 0f;

	private boolean vertical = false;

	// 默认提供了四种进度条模式，分别是游戏类血槽，普通的UI形式，圆形UI模式，以及用户自制图像.(默认为游戏模式)
	public enum ProgressType {
		GAME, UI, CircleUI, Custom
	}

	private LTexture defaultColorTexture;
	private LTexture bgTexture;
	private LTexture bgTextureEnd;
	private LTexture bgProgressTexture;
	private LTexture bgProgressStart;

	private ValueListener listener;

	private float percentage = 1f;

	private LTexture texture;

	private ProgressType progressType;

	public LProgress(int x, int y, int width, int height) {
		this(ProgressType.GAME, LColor.red, x, y, width, height, null, null);
	}

	public LProgress(LColor color, int x, int y, int width, int height) {
		this(ProgressType.GAME, color, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, int x, int y, int width, int height) {
		this(type, LColor.red, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, LColor color, int x, int y, int width, int height) {
		this(type, color, x, y, width, height, null, null);
	}

	public LProgress(ProgressSkin skin, int x, int y, int width, int height) {
		this(ProgressType.Custom, skin.getColor(), x, y, width, height, skin.getBackgroundTexture(),
				skin.getProgressTexture());
	}

	public LProgress(ProgressType type, LColor color, int x, int y, int width, int height, LTexture bg,
			LTexture bgProgress) {
		super(x, y, width, height);
		this.progressType = type;
		this._component_baseColor = color == null ? LColor.red : color;
		switch (progressType) {
		case GAME:
			this.texture = LTextures.loadTexture(LSystem.getSystemImagePath() + "bar.png");
			this.bgTexture = texture.cpy(3, 0, 1, texture.height() - 2);
			this.bgProgressTexture = texture.cpy(1, 0, 1, texture.height() - 2);
			this.bgProgressStart = texture.cpy(0, 0, 1, texture.height() - 2);
			this.bgTextureEnd = texture.cpy(4, 0, 1, texture.height() - 2);
			break;
		case UI:
		case CircleUI:
			if (defaultColorTexture == null || defaultColorTexture.isClosed()) {
				defaultColorTexture = LSystem.base().graphics().finalColorTex();
			}
			this.bgTexture = SkinManager.get().getProgressSkin().getBackgroundTexture();
			this.bgProgressTexture = defaultColorTexture;
			break;
		default:
			this.bgTexture = bg;
			this.bgProgressTexture = bgProgress;
			break;
		}
		this.reset();
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (progressType != ProgressType.CircleUI) {
			draw(g, x, y);
		} else {
			float radius = MathUtils.min(getWidth(), getHeight());
			float size = radius / 6f;
			g.drawStrokeGradientCircle(x, y, 0, 360f, getWidth(), getHeight(), size, LColor.gray, LColor.darkGray);
			g.drawStrokeGradientCircle(x, y, 0, 360f * percentage, getWidth(), getHeight(), size,
					_component_baseColor.darker(), _component_baseColor);

		}
	}

	@Override
	public void update(final long elapsedTime) {
		super.update(elapsedTime);
		if (listener != null) {
			listener.onChange(this, percentage);
		}
	}

	public void draw(GLEx batch, int x, int y) {
		if (vertical) {
			float size = 0;
			switch (progressType) {
			case GAME:
				size = getWidth() * (1f - percentage);
				float posY = getHeight() / 2;
				batch.draw(bgTexture, x + getHeight() / 2 + getWidth() / 2, y - posY, size, getHeight(), 0f, 0f, 90);
				batch.setColor(_component_baseColor);
				size = getWidth() * percentage;
				batch.draw(bgProgressTexture, x + getHeight() / 2 + getWidth() / 2, y + getWidth() - size - posY,
						getWidth() * percentage, getHeight(), 0f, 0f, 90);
				batch.resetColor();
				break;
			case UI:
				batch.draw(bgTexture, x, y, getHeight(), getWidth());
				batch.setColor(_component_baseColor);
				size = (getWidth() * percentage - 2);
				batch.draw(bgProgressTexture, x + 1, y + getWidth() - size - 1, getHeight() - 2, size);
				batch.resetColor();
				break;
			default:
				batch.draw(bgTexture, x, y, getHeight(), getWidth());
				batch.setColor(_component_baseColor);
				size = (getWidth() * percentage);
				batch.draw(bgProgressTexture, x, y + getWidth() - size, getHeight(), size);
				batch.resetColor();
				break;
			}
		} else {
			switch (progressType) {
			case GAME:
				batch.draw(bgTexture, x + getWidth() * percentage + 1, y, getWidth() * (1 - percentage), getHeight());
				batch.draw(bgTextureEnd, x + getWidth() + 1, y, bgTextureEnd.width(), getHeight());
				batch.setColor(_component_baseColor);
				batch.draw(bgProgressTexture, x + 1, y, getWidth() * percentage, getHeight());
				batch.draw(bgProgressStart, x, y, bgProgressStart.width(), getHeight());
				batch.resetColor();
				break;
			case UI:
				batch.draw(bgTexture, x, y, getWidth(), getHeight());
				batch.setColor(_component_baseColor);
				batch.draw(bgProgressTexture, x + 1, y + 1, getWidth() * percentage - 2, getHeight() - 2);
				batch.resetColor();
				break;
			default:
				batch.draw(bgTexture, x, y, getWidth(), getHeight());
				batch.setColor(_component_baseColor);
				batch.draw(bgProgressTexture, x, y, getWidth() * percentage, getHeight());
				batch.resetColor();
				break;
			}
		}
	}

	@Override
	public LProgress reset() {
		super.reset();
		this.percentage = 1f;
		this.minValue = 0f;
		this.maxValue = 100f;
		return this;
	}

	public boolean isZero() {
		return MathUtils.equal(this.getValue(), 0f);
	}

	public boolean isMin() {
		return MathUtils.equal(this.getValue(), this.minValue);
	}

	public boolean isMax() {
		return MathUtils.equal(this.getValue(), this.maxValue);
	}

	public float getValue() {
		return this.percentage * this.maxValue;
	}

	public LProgress setValue(float v) {
		float process = 0f;
		if (v < minValue) {
			process = minValue / maxValue;
		} else if (v > maxValue) {
			process = 1f;
		} else {
			process = v / maxValue;
		}
		this.percentage = process;
		return this;
	}

	public float getMinValue() {
		return minValue;
	}

	public LProgress setMinValue(float v) {
		if (v > this.maxValue) {
			this.maxValue = v;
			return this;
		}
		this.minValue = v;
		setValue(getValue());
		return this;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public LProgress setMaxValue(float v) {
		if (v < this.minValue) {
			this.minValue = v;
			return this;
		}
		this.maxValue = v;
		setValue(getValue());
		return this;
	}

	public LProgress setPercentage(float p) {
		setValue(p * maxValue);
		return this;
	}

	public LProgress setValue(float v, float min, float max) {
		setMinValue(min);
		setMaxValue(max);
		setValue(v);
		return this;
	}

	public boolean isVertical() {
		return vertical;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public float getPercentage() {
		return this.percentage;
	}

	public ValueListener getListener() {
		return listener;
	}

	public void setListener(ValueListener listener) {
		this.listener = listener;
	}

	@Override
	public String getUIName() {
		return "Progress";
	}

	@Override
	public void destory() {
		if (texture != null) {
			if (bgTexture != null) {
				bgTexture.close();
			}
			if (bgTextureEnd != null) {
				bgTextureEnd.close();
			}
			if (bgProgressTexture != null) {
				bgProgressTexture.close();
			}
			if (bgProgressStart != null) {
				bgProgressStart.close();
			}
			texture.close();
		}
	}

}
