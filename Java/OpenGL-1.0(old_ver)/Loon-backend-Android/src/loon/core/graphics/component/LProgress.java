package loon.core.graphics.component;

import loon.LSystem;
import loon.action.sprite.SpriteBatch;
import loon.core.graphics.LComponent;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;
import loon.core.graphics.opengl.TextureUtils;

/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 * 
 *          Examples1:
 * 
 *          LProgress progress = new LProgress(LColor.red, 110, 110, 200,20);
 *          progress.setPercentage(0.6f);
 * 
 *          Examples2:
 * 
 *          LProgress progress = new LProgress(ProgressType.UI , LColor.red,
 *          110, 110, 200,20); progress.setPercentage(0.6f);
 */
public class LProgress extends LComponent {

	private boolean vertical = false;

	// 默认提供了三种进度条模式，分别是游戏类血槽，普通的UI形式，以及用户自制图像.(默认为游戏模式)
	public enum ProgressType {
		GAME, UI, Custom
	}

	private static LTexture defaultColorTexture;
	private LTextureRegion bgTexture;
	private LTextureRegion bgTextureEnd;
	private LTextureRegion bgProgressTexture;
	private LTextureRegion bgProgressStart;
	private LColor color;
	private float percentage = 1f;

	private LTextureRegion texture;

	private SpriteBatch batch;

	private ProgressType progressType;

	public LProgress(int x, int y, int width, int height) {
		this(ProgressType.GAME, LColor.red, x, y, width, height, null, null);
	}

	public LProgress(LColor color, int x, int y, int width, int height) {
		this(ProgressType.GAME, color, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, LColor color, int x, int y, int width,
			int height) {
		this(type, color, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, LColor color, int x, int y, int width,
			int height, LTexture bg, LTexture bgProgress) {
		super(x, y, width, height);
		this.progressType = type;
		this.batch = new SpriteBatch();
		this.color = color;
		switch (progressType) {
		case GAME:
			this.texture = new LTextureRegion(LSystem.FRAMEWORK_IMG_NAME
					+ "bar.png");
			this.bgTexture = new LTextureRegion(texture.getTexture(),
					texture.getRegionX() + 3, texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			this.bgProgressTexture = new LTextureRegion(texture.getTexture(),
					texture.getRegionX() + 1, texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			this.bgProgressStart = new LTextureRegion(texture.getTexture(),
					texture.getRegionX(), texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			this.bgTextureEnd = new LTextureRegion(texture.getTexture(),
					texture.getRegionX() + 4, texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			break;
		case UI:
			if (defaultColorTexture == null || defaultColorTexture.isClose()) {
				defaultColorTexture = TextureUtils.createTexture(1, 1,
						LColor.white);
			}
			this.bgTexture = new LTextureRegion(DefUI.getDefaultTextures(3));
			this.bgProgressTexture = new LTextureRegion(defaultColorTexture);
			break;
		default:
			this.bgTexture = new LTextureRegion(bg);
			this.bgProgressTexture = new LTextureRegion(bgProgress);
			break;
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (batch != null) {
			batch.begin();
			draw(batch, x, y);
			batch.end();
		}
	}

	public void draw(SpriteBatch batch, int x, int y) {
		if (vertical) {
			float size = 0;
			switch (progressType) {
			case GAME:
				size = getWidth() * (1 - percentage);
				float posY = getHeight() / 2;
				batch.draw(bgTexture, x + getHeight() / 2 + 1, y - posY, size,
						getHeight(), 90);
				batch.setColor(color);
				size = getWidth() * percentage;
				batch.draw(bgProgressTexture, x + 1 + getHeight() / 2, y
						+ getWidth() - size - posY, getWidth() * percentage,
						getHeight(), 90);
				batch.resetColor();
				break;
			case UI:
				batch.draw(bgTexture.getTexture(), x, y, getHeight(),
						getWidth());
				batch.setColor(color);
				size = (getWidth() * percentage - 2);
				batch.draw(bgProgressTexture.getTexture(), x + 1, y
						+ getWidth() - size - 1, getHeight() - 2, size);
				batch.resetColor();
				break;
			default:
				batch.draw(bgTexture.getTexture(), x, y, getHeight(),
						getWidth());
				batch.setColor(color);
				size = (getWidth() * percentage);
				batch.draw(bgProgressTexture.getTexture(), x, y + getWidth()
						- size, getHeight(), size);
				batch.resetColor();
				break;
			}
		} else {
			switch (progressType) {
			case GAME:
				batch.draw(bgTexture, x + getWidth() * percentage + 1, y,
						getWidth() * (1 - percentage), getHeight());
				batch.draw(bgTextureEnd, x + getWidth() + 1, y,
						bgTextureEnd.getRegionWidth(), getHeight());
				batch.setColor(color);
				batch.draw(bgProgressTexture, x + 1, y,
						getWidth() * percentage, getHeight());
				batch.draw(bgProgressStart, x, y,
						bgProgressStart.getRegionWidth(), getHeight());
				batch.resetColor();
				break;
			case UI:
				batch.draw(bgTexture.getTexture(), x, y, getWidth(),
						getHeight());
				batch.setColor(color);
				batch.draw(bgProgressTexture.getTexture(), x + 1, y + 1,
						getWidth() * percentage - 2, getHeight() - 2);
				batch.resetColor();
				break;
			default:
				batch.draw(bgTexture.getTexture(), x, y, getWidth(),
						getHeight());
				batch.setColor(color);
				batch.draw(bgProgressTexture.getTexture(), x, y, getWidth()
						* percentage, getHeight());
				batch.resetColor();
				break;
			}
		}
	}

	public void setPercentage(float p) {
		if (p >= 0f && p <= 1f) {
			this.percentage = p;
		} else {
			if (p > 1f) {
				this.percentage = 1f;
			} else if (p < 0f) {
				this.percentage = 0f;
			}
		}
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

	public void dispose() {
		super.dispose();
		if (texture != null) {
			if (bgTexture != null) {
				bgTexture.dispose();
			}
			if (bgTextureEnd != null) {
				bgTextureEnd.dispose();
			}
			if (bgProgressTexture != null) {
				bgProgressTexture.dispose();
			}
			if (bgProgressStart != null) {
				bgProgressStart.dispose();
			}
			texture.dispose();
			batch.dispose();
		}
	}

	@Override
	public String getUIName() {
		return "Progress";
	}

}
