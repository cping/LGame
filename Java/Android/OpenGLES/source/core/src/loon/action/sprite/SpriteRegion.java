/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import java.util.List;

import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.utils.MathUtils;


public class SpriteRegion extends LTextureRegion {

	public static class Animation {

		final LTextureRegion[] keyFrames;

		public final float frameDuration;

		public final float animationDuration;

		public Animation(float frameDuration, List<LTextureRegion> keyFrames) {
			this.frameDuration = frameDuration;
			this.animationDuration = frameDuration * keyFrames.size();
			this.keyFrames = new LTextureRegion[keyFrames.size()];
			for (int i = 0, n = keyFrames.size(); i < n; i++) {
				this.keyFrames[i] = keyFrames.get(i);
			}
		}

		public Animation(float frameDuration, LTextureRegion... keyFrames) {
			this.frameDuration = frameDuration;
			this.keyFrames = keyFrames;
			this.animationDuration = frameDuration * keyFrames.length;
		}

		public LTextureRegion getKeyFrame(float stateTime, boolean looping) {
			int frameNumber = (int) (stateTime / frameDuration);
			if (!looping) {
				frameNumber = MathUtils.min(keyFrames.length - 1, frameNumber);
			} else {
				frameNumber = frameNumber % keyFrames.length;
			}
			return keyFrames[frameNumber];
		}

		public boolean isAnimationFinished(float stateTime) {
			int frameNumber = (int) (stateTime / frameDuration);
			return keyFrames.length - 1 < frameNumber;
		}
	}

	final float[] vertices = new float[SpriteBatch.SPRITE_SIZE];
	private final LColor color = new LColor(1f, 1f, 1f, 1f);
	private float x, y;
	float width, height;
	private float originX, originY;
	private float rotation;
	private float scaleX = 1, scaleY = 1;
	private boolean dirty = true;
	private RectBox bounds = new RectBox();

	public SpriteRegion() {
		setColor(1f, 1f, 1f, 1f);
	}

	public SpriteRegion(String file) {
		this(LTextures.loadTexture(file));
	}

	public SpriteRegion(String file, Format format) {
		this(LTextures.loadTexture(file, format));
	}

	public SpriteRegion(LTexture texture) {
		this(texture, 0, 0, texture.getWidth(), texture.getHeight());
	}

	public SpriteRegion(LTexture texture, int srcWidth, int srcHeight) {
		this(texture, 0, 0, srcWidth, srcHeight);
	}

	public SpriteRegion(String file, int srcX, int srcY, int srcWidth,
			int srcHeight) {
		this(LTextures.loadTexture(file), srcX, srcY, srcWidth, srcHeight);
	}

	public SpriteRegion(LTexture texture, int srcX, int srcY, int srcWidth,
			int srcHeight) {
		if (texture == null) {
			throw new IllegalArgumentException("texture cannot be null.");
		}
		this.texture = texture;
		setRegion(srcX, srcY, srcWidth, srcHeight);
		setColor(1f, 1f, 1f, 1f);
		setSize(Math.abs(srcWidth), Math.abs(srcHeight));
		setOrigin(width / 2, height / 2);
	}

	public SpriteRegion(LTextureRegion region) {
		setRegion(region);
		setColor(1f, 1f, 1f, 1f);
		setSize(Math.abs(region.getRegionWidth()),
				Math.abs(region.getRegionHeight()));
		setOrigin(width / 2, height / 2);
	}

	public SpriteRegion(LTextureRegion region, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		setRegion(region, srcX, srcY, srcWidth, srcHeight);
		setColor(1f, 1f, 1f, 1f);
		setSize(Math.abs(srcWidth), Math.abs(srcHeight));
		setOrigin(width / 2, height / 2);
	}

	public SpriteRegion(SpriteRegion sprite) {
		set(sprite);
	}

	public void set(SpriteRegion sprite) {
		if (sprite == null) {
			throw new IllegalArgumentException("sprite cannot be null.");
		}
		System.arraycopy(sprite.vertices, 0, vertices, 0, SpriteBatch.SPRITE_SIZE);
		texture = sprite.texture;
		xOff = sprite.xOff;
		yOff = sprite.yOff;
		widthRatio = sprite.widthRatio;
		heightRatio = sprite.heightRatio;
		x = sprite.x;
		y = sprite.y;
		width = sprite.width;
		height = sprite.height;
		originX = sprite.originX;
		originY = sprite.originY;
		rotation = sprite.rotation;
		scaleX = sprite.scaleX;
		scaleY = sprite.scaleY;
		color.setColor(sprite.color);
		dirty = sprite.dirty;
	}

	public void setBounds(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if (dirty) {
			return;
		}
		float x2 = x + width;
		float y2 = y + height;
		float[] vertices = this.vertices;
		vertices[0] = x;
		vertices[1] = y;

		vertices[5] = x;
		vertices[6] = y2;

		vertices[10] = x2;
		vertices[11] = y2;

		vertices[15] = x2;
		vertices[16] = y;

		if (rotation != 0 || scaleX != 1 || scaleY != 1) {
			dirty = true;
		}
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;

		if (dirty) {
			return;
		}

		float x2 = x + width;
		float y2 = y + height;
		float[] vertices = this.vertices;
		vertices[0] = x;
		vertices[1] = y;

		vertices[5] = x;
		vertices[6] = y2;

		vertices[10] = x2;
		vertices[11] = y2;

		vertices[15] = x2;
		vertices[16] = y;

		if (rotation != 0 || scaleX != 1 || scaleY != 1) {
			dirty = true;
		}
	}

	public void setPosition(float x, float y) {
		translate(x - this.x, y - this.y);
	}

	public void setX(float x) {
		translateX(x - this.x);
	}

	public void setY(float y) {
		translateY(y - this.y);
	}

	public void translateX(float xAmount) {
		this.x += xAmount;

		if (dirty) {
			return;
		}

		float[] vertices = this.vertices;
		vertices[0] += xAmount;
		vertices[5] += xAmount;
		vertices[10] += xAmount;
		vertices[15] += xAmount;
	}

	public void translateY(float yAmount) {
		y += yAmount;

		if (dirty) {
			return;
		}

		float[] vertices = this.vertices;
		vertices[1] += yAmount;
		vertices[6] += yAmount;
		vertices[11] += yAmount;
		vertices[16] += yAmount;
	}

	public void translate(float xAmount, float yAmount) {
		x += xAmount;
		y += yAmount;

		if (dirty) {
			return;
		}

		float[] vertices = this.vertices;
		vertices[0] += xAmount;
		vertices[1] += yAmount;

		vertices[5] += xAmount;
		vertices[6] += yAmount;

		vertices[10] += xAmount;
		vertices[11] += yAmount;

		vertices[15] += xAmount;
		vertices[16] += yAmount;
	}

	public void setColor(LColor tint) {
		float color = tint.toFloatBits();
		float[] vertices = this.vertices;
		vertices[2] = color;
		vertices[7] = color;
		vertices[12] = color;
		vertices[17] = color;
	}

	public void setColor(float r, float g, float b, float a) {
		int intBits = ((int) (255 * a) << 24) | ((int) (255 * b) << 16)
				| ((int) (255 * g) << 8) | ((int) (255 * r));
		float color = Float.intBitsToFloat(intBits & 0xfeffffff);
		float[] vertices = this.vertices;
		vertices[2] = color;
		vertices[7] = color;
		vertices[12] = color;
		vertices[17] = color;
	}

	public void setOrigin(float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
		dirty = true;
	}

	public void setRotation(float degrees) {
		this.rotation = degrees;
		dirty = true;
	}

	public void rotate(float degrees) {
		rotation += degrees;
		dirty = true;
	}

	public void rotate90(boolean clockwise) {
		float[] vertices = this.vertices;

		if (clockwise) {
			float temp = vertices[4];
			vertices[4] = vertices[19];
			vertices[19] = vertices[14];
			vertices[14] = vertices[9];
			vertices[9] = temp;

			temp = vertices[3];
			vertices[3] = vertices[18];
			vertices[18] = vertices[13];
			vertices[13] = vertices[8];
			vertices[8] = temp;
		} else {
			float temp = vertices[4];
			vertices[4] = vertices[9];
			vertices[9] = vertices[14];
			vertices[14] = vertices[19];
			vertices[19] = temp;

			temp = vertices[3];
			vertices[3] = vertices[8];
			vertices[8] = vertices[13];
			vertices[13] = vertices[18];
			vertices[18] = temp;
		}
	}

	public void setScale(float scaleXY) {
		this.scaleX = scaleXY;
		this.scaleY = scaleXY;
		dirty = true;
	}

	public void setScale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		dirty = true;
	}

	public void scale(float amount) {
		this.scaleX += amount;
		this.scaleY += amount;
		dirty = true;
	}

	public float[] getVertices() {
		if (dirty) {
			dirty = false;

			float[] vertices = this.vertices;
			float localX = -originX;
			float localY = -originY;
			float localX2 = localX + width;
			float localY2 = localY + height;
			float worldOriginX = this.x - localX;
			float worldOriginY = this.y - localY;
			if (scaleX != 1 || scaleY != 1) {
				localX *= scaleX;
				localY *= scaleY;
				localX2 *= scaleX;
				localY2 *= scaleY;
			}
			if (rotation != 0) {
				final float cos = MathUtils.cosDeg(rotation);
				final float sin = MathUtils.sinDeg(rotation);
				final float localXCos = localX * cos;
				final float localXSin = localX * sin;
				final float localYCos = localY * cos;
				final float localYSin = localY * sin;
				final float localX2Cos = localX2 * cos;
				final float localX2Sin = localX2 * sin;
				final float localY2Cos = localY2 * cos;
				final float localY2Sin = localY2 * sin;

				final float x1 = localXCos - localYSin + worldOriginX;
				final float y1 = localYCos + localXSin + worldOriginY;
				vertices[0] = x1;
				vertices[1] = y1;

				final float x2 = localXCos - localY2Sin + worldOriginX;
				final float y2 = localY2Cos + localXSin + worldOriginY;
				vertices[5] = x2;
				vertices[6] = y2;

				final float x3 = localX2Cos - localY2Sin + worldOriginX;
				final float y3 = localY2Cos + localX2Sin + worldOriginY;
				vertices[10] = x3;
				vertices[11] = y3;

				vertices[15] = x1 + (x3 - x2);
				vertices[16] = y3 - (y2 - y1);
			} else {
				final float x1 = localX + worldOriginX;
				final float y1 = localY + worldOriginY;
				final float x2 = localX2 + worldOriginX;
				final float y2 = localY2 + worldOriginY;

				vertices[0] = x1;
				vertices[1] = y1;

				vertices[5] = x1;
				vertices[6] = y2;

				vertices[10] = x2;
				vertices[11] = y2;

				vertices[15] = x2;
				vertices[16] = y1;
			}
		}
		return vertices;
	}

	public RectBox getBoundingRectangle() {
		final float[] vertices = getVertices();

		float minx = vertices[0];
		float miny = vertices[1];
		float maxx = vertices[0];
		float maxy = vertices[1];

		minx = minx > vertices[5] ? vertices[5] : minx;
		minx = minx > vertices[10] ? vertices[10] : minx;
		minx = minx > vertices[15] ? vertices[15] : minx;

		maxx = maxx < vertices[5] ? vertices[5] : maxx;
		maxx = maxx < vertices[10] ? vertices[10] : maxx;
		maxx = maxx < vertices[15] ? vertices[15] : maxx;

		miny = miny > vertices[6] ? vertices[6] : miny;
		miny = miny > vertices[11] ? vertices[11] : miny;
		miny = miny > vertices[16] ? vertices[16] : miny;

		maxy = maxy < vertices[6] ? vertices[6] : maxy;
		maxy = maxy < vertices[11] ? vertices[11] : maxy;
		maxy = maxy < vertices[16] ? vertices[16] : maxy;

		bounds.x = minx;
		bounds.y = miny;
		bounds.width = (int) (maxx - minx);
		bounds.height = (int) (maxy - miny);

		return bounds;
	}

	public void draw(SpriteBatch batch) {
		batch.draw(texture, getVertices(), 0, SpriteBatch.SPRITE_SIZE);
	}

	public void draw(SpriteBatch batch, float alpha) {
		LColor color = getColor();
		float oldAlpha = color.a;
		color.a *= alpha;
		setColor(color);
		draw(batch);
		color.a = oldAlpha;
		setColor(color);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getOriginX() {
		return originX;
	}

	public float getOriginY() {
		return originY;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public LColor getColor() {
		float floatBits = vertices[2];
		int intBits = Float.floatToRawIntBits(floatBits);
		LColor color = this.color;
		color.r = (intBits & 0xff) / 255f;
		color.g = ((intBits >>> 8) & 0xff) / 255f;
		color.b = ((intBits >>> 16) & 0xff) / 255f;
		color.a = ((intBits >>> 24) & 0xff) / 255f;
		return color;
	}

	@Override
	public void setRegion(float u, float v, float u2, float v2) {
		this.xOff = u;
		this.yOff = v;
		this.widthRatio = u2;
		this.heightRatio = v2;

		float[] vertices = SpriteRegion.this.vertices;
		vertices[3] = u;
		vertices[4] = v;

		vertices[8] = u;
		vertices[9] = v2;

		vertices[13] = u2;
		vertices[14] = v2;

		vertices[18] = u2;
		vertices[19] = v;
	}

	@Override
	public void flip(boolean x, boolean y) {
		super.flip(x, y);
		float[] vertices = SpriteRegion.this.vertices;
		if (x) {
			float temp = vertices[3];
			vertices[3] = vertices[13];
			vertices[13] = temp;
			temp = vertices[8];
			vertices[8] = vertices[18];
			vertices[18] = temp;
		}
		if (y) {
			float temp = vertices[4];
			vertices[4] = vertices[14];
			vertices[14] = temp;
			temp = vertices[9];
			vertices[9] = vertices[19];
			vertices[19] = temp;
		}
	}

	@Override
	public void scroll(float xAmount, float yAmount) {
		float[] vertices = SpriteRegion.this.vertices;
		if (xAmount != 0) {
			float u = (vertices[3] + xAmount) % 1;
			float u2 = u + width / texture.getWidth();
			this.xOff = u;
			this.widthRatio = u2;
			vertices[3] = u;
			vertices[8] = u;
			vertices[13] = u2;
			vertices[18] = u2;
		}
		if (yAmount != 0) {
			float v = (vertices[9] + yAmount) % 1;
			float v2 = v + height / texture.getHeight();
			this.yOff = v;
			this.heightRatio = v2;
			vertices[4] = v2;
			vertices[9] = v;
			vertices[14] = v;
			vertices[19] = v2;
		}
	}
}
