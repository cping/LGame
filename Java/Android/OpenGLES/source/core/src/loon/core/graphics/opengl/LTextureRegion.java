package loon.core.graphics.opengl;

import loon.core.LRelease;
import loon.core.graphics.opengl.LTexture.Format;

import loon.utils.MathUtils;


/**
 * Copyright 2008 - 2011
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
public class LTextureRegion implements LRelease {

	public LTexture texture;

	public float xOff, yOff;

	public float widthRatio = 1f, heightRatio = 1f;

	public LTextureRegion() {
	}

	public LTextureRegion(String file) {
		this(file, Format.LINEAR);
	}

	public LTextureRegion(String file, Format f) {
		this(LTextures.loadTexture(file, f));
	}

	public LTextureRegion(String file, int x, int y, int width, int height) {
		this(LTextures.loadTexture(file), x, y, width, height);
	}

	public LTextureRegion(LTexture texture) {
		if (texture == null) {
			throw new IllegalArgumentException("texture cannot be null.");
		}
		this.texture = texture;
		setRegion(0, 0, texture.texWidth, texture.texHeight);
	}

	public LTextureRegion(LTexture texture, int width, int height) {
		if (texture == null) {
			throw new IllegalArgumentException("texture cannot be null.");
		}
		this.texture = texture;
		setRegion(0, 0, width, height);
	}

	public LTextureRegion(LTexture texture, int x, int y, int width, int height) {
		if (texture == null) {
			throw new IllegalArgumentException("texture cannot be null.");
		}
		this.texture = texture;
		setRegion(x, y, width, height);
	}

	public LTextureRegion(LTextureRegion region) {
		setRegion(region);
	}

	public LTextureRegion(LTextureRegion region, int x, int y, int width,
			int height) {
		setRegion(region, x, y, width, height);
	}

	public void setRegion(LTexture texture) {
		this.texture = texture;
		setRegion(0, 0, texture.getWidth(), texture.getHeight());
	}

	public void setRegion(int x, int y, int width, int height) {
		float invTexWidth = (1f / texture.getWidth()) * texture.widthRatio;
		float invTexHeight = (1f / texture.getHeight()) * texture.heightRatio;
		setRegion(x * invTexWidth + texture.xOff, y * invTexHeight
				+ texture.yOff, (x + width) * invTexWidth, (y + height)
				* invTexHeight);
	}

	public void setRegion(float xOff, float yOff, float widthRatio,
			float heightRatio) {
		this.xOff = xOff;
		this.yOff = yOff;
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
	}

	public void setRegion(LTextureRegion region) {
		texture = region.texture;
		setRegion(region.xOff, region.yOff, region.widthRatio,
				region.heightRatio);
	}

	public void setRegion(LTextureRegion region, int x, int y, int width,
			int height) {
		texture = region.texture;
		setRegion(region.getRegionX() + x, region.getRegionY() + y, width,
				height);
	}

	public LTexture getTexture() {
		return texture;
	}

	public void setTexture(LTexture texture) {
		this.texture = texture;
	}

	public int getRegionX() {
		return (int) (xOff * texture.texWidth);
	}

	public int getRegionY() {
		return (int) (yOff * texture.texHeight);
	}

	public int getRegionWidth() {
		int result = MathUtils.round((widthRatio - xOff)
				* texture.getTextureWidth());
		return result > 0 ? result : -result;
	}

	public int getRegionHeight() {
		int result = MathUtils.round((heightRatio - yOff)
				* texture.getTextureHeight());
		return result > 0 ? result : -result;
	}

	public void flip(boolean x, boolean y) {
		if (x) {
			float temp = xOff;
			xOff = widthRatio;
			widthRatio = temp;
		}
		if (y) {
			float temp = yOff;
			yOff = heightRatio;
			heightRatio = temp;
		}
	}

	public void scroll(float xAmount, float yAmount) {
		if (xAmount != 0) {
			float width = (widthRatio - xOff) * texture.getTextureWidth();
			xOff = (xOff + xAmount) % 1;
			widthRatio = xOff + width / texture.getWidth();
		}
		if (yAmount != 0) {
			float height = (heightRatio - yOff) * texture.getTextureHeight();
			yOff = (yOff + yAmount) % 1;
			heightRatio = yOff + height / texture.getTextureHeight();
		}
	}

	public LTextureRegion[][] split(int tileWidth, int tileHeight) {
		int x = getRegionX();
		int y = getRegionY();
		int width = getRegionWidth();
		int height = getRegionHeight();

		if (width < 0) {
			x = x - width;
			width = -width;
		}

		if (height < 0) {
			y = y - height;
			height = -height;
		}

		int rows = height / tileHeight;
		int cols = width / tileWidth;

		int startX = x;
		LTextureRegion[][] tiles = new LTextureRegion[rows][cols];
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x = startX;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				tiles[row][col] = new LTextureRegion(texture, x, y, tileWidth,
						tileHeight);
			}
		}

		return tiles;
	}

	public static LTextureRegion[][] split(LTexture texture, int tileWidth,
			int tileHeight) {
		LTextureRegion region = new LTextureRegion(texture);
		return region.split(tileWidth, tileHeight);
	}

	@Override
	public void dispose() {
		if (texture != null) {
			texture.destroy();
		}
	}
}
