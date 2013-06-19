package loon.core.graphics.opengl;

import java.nio.FloatBuffer;
import java.util.HashMap;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture.Format;
import loon.jni.NativeSupport;
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
// 此类专为批量渲染单一纹理而设置（比如处理海量精灵块，地图块等），允许提交缓存中数据到渲染器
public final class LTextureBatch implements LRelease {

	public boolean quad = true;

	static boolean isBatchCacheDitry;

	private final static HashMap<Integer, LTextureBatch> batchPools = new HashMap<Integer, LTextureBatch>(
			10);

	public final static void clearBatchCaches() {
		if (LTextureBatch.isBatchCacheDitry) {
			HashMap<Integer, LTextureBatch> batchCaches;
			synchronized (batchPools) {
				batchCaches = new HashMap<Integer, LTextureBatch>(batchPools);
				batchPools.clear();
			}
			for (LTextureBatch bt : batchCaches.values()) {
				if (bt != null) {
					synchronized (bt) {
						bt.dispose();
						bt = null;
					}
				}
			}
			batchCaches = null;
			LTextureBatch.isBatchCacheDitry = false;
		}
	}

	public final static LTextureBatch bindBatchCache(final LTexture texture) {
		return bindBatchCache(0, texture);
	}

	public final static LTextureBatch bindBatchCache(final int index,
			final LTexture texture) {
		if (texture == null) {
			return null;
		}
		int texId = texture.textureID;
		return bindBatchCache(index, texId, texture);
	}

	public final static LTextureBatch bindBatchCache(final Object o,
			final int texId, final LTexture texture) {
		return bindBatchCache(o.hashCode(), texId, texture);
	}

	public final static LTextureBatch bindBatchCache(final int index,
			final int texId, final LTexture texture) {
		if (batchPools.size() > 128) {
			clearBatchCaches();
		}
		int key = LSystem.unite(index, texId);
		LTextureBatch pBatch = batchPools.get(key);
		if (pBatch == null) {
			synchronized (batchPools) {
				pBatch = new LTextureBatch(texture);
				batchPools.put(key, pBatch);
			}
		}
		return pBatch;
	}

	public final static LTextureBatch disposeBatchCache(int texId) {
		synchronized (batchPools) {
			LTextureBatch pBatch = batchPools.remove(texId);
			if (pBatch != null) {
				synchronized (pBatch) {
					pBatch.dispose();
					pBatch = null;
				}
			}
			return pBatch;
		}
	}

	private GLCache lastCache;

	private LColor[] colors;

	public static class GLCache implements LRelease {

		FloatBuffer vertexBuffer;

		FloatBuffer coordsBuffer;

		FloatBuffer colorBuffer;

		public float x, y;

		int count;

		boolean isColor;

		public GLCache(LTextureBatch batch) {
			this(batch, true);
		}

		public GLCache(LTextureBatch batch, boolean reset) {

			if (reset) {
				batch.insertVertices();
			}

			this.count = batch.count;
			this.isColor = batch.isColor;

			vertexBuffer = NativeSupport.newFloatBuffer(batch.verts, 0,
					batch.oldVertCount);

			if (isColor) {
				colorBuffer = NativeSupport.newFloatBuffer(batch.cols, 0,
						batch.oldColorCount);
			}

			coordsBuffer = NativeSupport.newFloatBuffer(batch.coords, 0,
					batch.oldCoordCount);

			this.x = batch.moveX;
			this.y = batch.moveY;
		}

		@Override
		public void dispose() {
			if (vertexBuffer != null) {
				NativeSupport.freeMemory(vertexBuffer);
				this.vertexBuffer = null;
			}
			if (coordsBuffer != null) {
				NativeSupport.freeMemory(coordsBuffer);
				this.coordsBuffer = null;
			}
			if (colorBuffer != null) {
				NativeSupport.freeMemory(colorBuffer);
				this.colorBuffer = null;
			}
		}

	}

	private static final int DEFAULT_MAX_VERTICES = 3000;

	private float[] color = new float[] { 1f, 1f, 1f, 1f };

	private float[] coord = new float[] { 0f, 0f };

	private int count, maxCount;

	private float[] verts = new float[DEFAULT_MAX_VERTICES * 3];

	private float[] cols = new float[DEFAULT_MAX_VERTICES * 4];

	private float[] coords = new float[DEFAULT_MAX_VERTICES * 2];

	float invTexWidth;

	float invTexHeight;

	public void setTexture(LTexture tex2d) {
		this.texture = tex2d;
		this.texWidth = texture.width;
		this.texHeight = texture.height;
		this.invTexWidth = (1f / texWidth) * texture.widthRatio;
		this.invTexHeight = (1f / texHeight) * texture.heightRatio;
	}

	private LTexture texture;

	private FloatBuffer vertexBuffer;

	private FloatBuffer coordBuffer;

	private FloatBuffer colorBuffer;

	private float moveX, moveY;

	private int batchType, expand;

	private int ver, col, tex;

	private int texWidth, texHeight;

	private float xOff, yOff, widthRatio, heightRatio;

	private float drawWidth, drawHeight;

	private float textureSrcX, textureSrcY;

	private float srcWidth, srcHeight;

	private float renderWidth, renderHeight;

	int oldVertCount, oldColorCount, oldCoordCount;

	boolean useBegin, lockCoord, isColor, isLocked;

	public LTextureBatch(String fileName, Format format) {
		this(fileName, format, DEFAULT_MAX_VERTICES);
	}

	public LTextureBatch(String fileName, Format format, int count) {
		this(LTextures.loadTexture(fileName, format), count);
	}

	public LTextureBatch(String fileName) {
		this(LTextures.loadTexture(fileName), DEFAULT_MAX_VERTICES);
	}

	public LTextureBatch(LTexture texture) {
		this(texture, DEFAULT_MAX_VERTICES);
	}

	public LTextureBatch(String fileName, int count) {
		this(LTextures.loadTexture(fileName), count);
	}

	public LTextureBatch(LTexture texture, int count) {
		this.setTexture(texture);
		this.isLocked = false;
		this.make(count);
	}

	private final void glVertex3f(final int count, float x, float y, float z) {
		this.ver = count * 3;
		this.verts[ver + 0] = x;
		this.verts[ver + 1] = y;
		this.verts[ver + 2] = z;
		this.tex = count * 2;
		this.coords[tex + 0] = coord[0];
		this.coords[tex + 1] = coord[1];
		if (!isColor) {
			return;
		}
		this.col = count * 4;
		this.cols[col + 0] = color[0];
		this.cols[col + 1] = color[1];
		this.cols[col + 2] = color[2];
		this.cols[col + 3] = color[3];
	}

	public void glVertex2f(float x, float y) {
		glVertex3f(x, y, 0);
	}

	public void glVertex3f(float x, float y, float z) {
		if (quad) {
			if (batchType != GL.GL_TRIANGLES) {
				glVertex3f(count, x, y, z);
				count++;
			} else {
				switch (expand) {
				case 0:
					glVertex3f(count, x, y, z);
					glVertex3f(count + 5, x, y, z);
					count++;
					break;
				case 1:
					glVertex3f(count, x, y, z);
					count++;
					break;
				case 2:
					glVertex3f(count, x, y, z);
					glVertex3f(count + 2, x, y, z);
					count++;
					break;
				case 3:
					glVertex3f(count, x, y, z);
					count += 3;
					break;
				}
				expand++;
				if (expand > 3) {
					expand = 0;
				}
				if (count >= maxCount) {
					if (isLimit(count, batchType)) {
						int type = batchType;
						glEnd();
						batchType = type;
					}
				}
			}
		} else {
			glVertex3f(count, x, y, z);
			count++;
		}
	}

	public final void glTexCoord2f(float fcol, float frow) {
		coord[0] = fcol;
		coord[1] = frow;
	}

	public void glColor4f(float r, float g, float b, float a) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
		isColor = true;
	}

	public void glColor4f(LColor c) {
		glColor4f(c.r, c.g, c.b, c.a);
	}

	private boolean isLimit(int count, int type) {
		switch (type) {
		case GL.GL_TRIANGLES:
			return count % 3 == 0;
		case GL.GL_LINES:
			return count % 2 == 0;
		case GL.GL_QUADS:
			return count % 4 == 0;
		}
		return false;
	}

	public void lock() {
		this.isLocked = true;
	}

	public void unLock() {
		this.isLocked = false;
	}

	/**
	 * 开始批处理
	 * 
	 */
	public void glBegin() {
		glBegin(GL.GL_TRIANGLES);
	}

	/**
	 * 以指定渲染形式开始批处理
	 * 
	 * @param type
	 */
	public void glBegin(int type) {
		this.batchType = type;
		this.useBegin = true;
		this.isColor = false;
		if (!isLocked) {
			this.expand = 0;
			this.count = 0;
		}
	}

	/**
	 * 构建顶点Buffer
	 * 
	 */
	private void make(int size) {
		if (vertexBuffer == null) {
			vertexBuffer = NativeSupport.newFloatBuffer(size * 3);
		}
		if (colorBuffer == null) {
			colorBuffer = NativeSupport.newFloatBuffer(size * 4);
		}
		if (coordBuffer == null) {
			coordBuffer = NativeSupport.newFloatBuffer(size * 2);
		}
		this.maxCount = size;
	}

	/**
	 * 注入顶点数据
	 * 
	 */
	private void insertVertices() {
		if (isLocked) {
			return;
		}
		oldVertCount = count * 3;
		NativeSupport.copy(verts, vertexBuffer, oldVertCount);
		if (isColor) {
			oldColorCount = count * 4;
			NativeSupport.copy(cols, colorBuffer, oldColorCount);
		}
		int size = count * 2;
		if (lockCoord && size == oldCoordCount) {
			return;
		}
		NativeSupport.copy(coords, coordBuffer, size);
		oldCoordCount = size;
	}

	/**
	 * 提交顶点数据到渲染器
	 * 
	 */
	public void glEnd() {
		if (count == 0 || !useBegin) {
			this.useBegin = false;
			return;
		}
		this.insertVertices();
		GLEx.self.glDrawArrays(texture, vertexBuffer, coordBuffer, colorBuffer,
				isColor, count, moveX, moveY);
		this.useBegin = false;
	}

	/**
	 * 提交顶点数据到渲染器，渲染结果用来处理文字
	 * 
	 * @param c
	 */
	public void commitQuad(LColor c, float x, float y, float sx, float sy,
			float ax, float ay, float rotaion) {
		if (count == 0 || !useBegin) {
			this.useBegin = false;
			return;
		}
		this.isColor = false;
		this.insertVertices();
		if (c != null) {
			GLEx.self.setColor(c);
		}
		GLEx.self.glQuad(texture, vertexBuffer, coordBuffer, count, x, y, sx,
				sy, ax, sy, rotaion);
		if (c != null) {
			GLEx.self.setColor(LColor.white);
		}
		this.useBegin = false;
	}

	/**
	 * 提交缓存数据到渲染器，渲染结果用来处理文字
	 * 
	 * @param c
	 */
	public void commitCacheQuad(LColor c, float x, float y, float sx, float sy,
			float ax, float ay, float rotaion) {
		if (count == 0) {
			return;
		}
		if (isLocked) {
			if (c != null) {
				GLEx.self.setColor(c);
			}
			GLEx.self.glQuad(texture, vertexBuffer, coordBuffer, count, x, y,
					sx, sy, ax, ay, rotaion);
			if (c != null) {
				GLEx.self.setColor(LColor.white);
			}
		}
	}

	/**
	 * 提交缓存中的数据
	 * 
	 */
	public void glCacheCommit() {
		if (count == 0) {
			return;
		}
		if (isLocked) {
			GLEx.self.glDrawArrays(texture, vertexBuffer, coordBuffer,
					colorBuffer, isColor, count, moveX, moveY);
		}
	}

	/**
	 * 提交缓存数据
	 * 
	 * @param tex2d
	 * @param cache
	 */
	public final static void commit(LTexture tex2d, GLCache cache) {
		if (cache.count == 0) {
			return;
		}
		GLEx.self.glDrawArrays(tex2d, cache);
	}

	/**
	 * 提交缓存数据进行文字渲染
	 * 
	 * @param tex2d
	 * @param cache
	 * @param c
	 * @param x
	 * @param y
	 */
	public final static void commitQuad(LTexture tex2d, GLCache cache,
			LColor c, float x, float y, float sx, float sy, float ax, float ay,
			float rotation) {
		if (cache.count == 0) {
			return;
		}
		if (c != null) {
			GLEx.self.setColor(c);
		}
		GLEx.self.glQuad(tex2d, cache, x, y, sx, sy, ax, ay, rotation);
		if (c != null) {
			GLEx.self.setColor(LColor.white);
		}
	}

	public void draw(float x, float y) {
		draw(colors, x, y, texture.width, texture.height, 0, 0, texture.width,
				texture.height);
	}

	public void draw(float x, float y, float width, float height) {
		draw(colors, x, y, width, height, 0, 0, texture.width, texture.height);
	}

	public void draw(float x, float y, float width, float height, float srcX,
			float srcY, float srcWidth, float srcHeight) {
		draw(colors, x, y, width, height, srcX, srcY, srcWidth, srcHeight);
	}

	public void draw(LColor[] colors, float x, float y, float width,
			float height) {
		draw(colors, x, y, width, height, 0, 0, texture.width, texture.height);
	}

	/**
	 * 以指定的色彩，顶点绘制出指定区域内的纹理到指定位置
	 * 
	 * @param colors
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 */
	public void draw(LColor[] colors, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight) {
		if (!useBegin) {
			return;
		}
		if (isLocked) {
			return;
		}

		xOff = srcX * invTexWidth + texture.xOff;
		yOff = srcY * invTexHeight + texture.yOff;
		widthRatio = srcWidth * invTexWidth;
		heightRatio = srcHeight * invTexHeight;

		final float fx2 = x + width;
		final float fy2 = y + height;

		if (colors == null) {
			glTexCoord2f(xOff, yOff);
			glVertex3f(x, y, 0);
			glTexCoord2f(xOff, heightRatio);
			glVertex3f(x, fy2, 0);
			glTexCoord2f(widthRatio, heightRatio);
			glVertex3f(fx2, fy2, 0);
			glTexCoord2f(widthRatio, yOff);
			glVertex3f(fx2, y, 0);
		} else {
			isColor = true;
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(xOff, yOff);
			glVertex3f(x, y, 0);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(xOff, heightRatio);
			glVertex3f(x, fy2, 0);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);
			glVertex3f(fx2, fy2, 0);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);
			glVertex3f(fx2, y, 0);
		}
	}

	public void drawQuad(float drawX, float drawY, float drawX2, float drawY2,
			float srcX, float srcY, float srcX2, float srcY2) {

		drawWidth = drawX2 - drawX;
		drawHeight = drawY2 - drawY;
		textureSrcX = ((srcX / texWidth) * texture.widthRatio) + texture.xOff;
		textureSrcY = ((srcY / texHeight) * texture.heightRatio) + texture.yOff;
		srcWidth = srcX2 - srcX;
		srcHeight = srcY2 - srcY;
		renderWidth = ((srcWidth / texWidth) * texture.widthRatio);
		renderHeight = ((srcHeight / texHeight) * texture.heightRatio);

		glTexCoord2f(textureSrcX, textureSrcY);
		glVertex3f(drawX, drawY, 0);
		glTexCoord2f(textureSrcX, textureSrcY + renderHeight);
		glVertex3f(drawX, drawY + drawHeight, 0);
		glTexCoord2f(textureSrcX + renderWidth, textureSrcY + renderHeight);
		glVertex3f(drawX + drawWidth, drawY + drawHeight, 0);
		glTexCoord2f(textureSrcX + renderWidth, textureSrcY);
		glVertex3f(drawX + drawWidth, drawY, 0);
	}

	public void draw(LColor[] colors, float x, float y, float rotation) {
		draw(colors, x, y, texture.getWidth() / 2, texture.getHeight() / 2,
				texture.getWidth(), texture.getHeight(), 1f, 1f, rotation, 0,
				0, texture.getWidth(), texture.getHeight(), false, false);
	}

	public void draw(LColor[] colors, float x, float y, float width,
			float height, float rotation) {
		draw(colors, x, y, texture.getWidth() / 2, texture.getHeight() / 2,
				width, height, 1f, 1f, rotation, 0, 0, texture.getWidth(),
				texture.getHeight(), false, false);
	}

	public void draw(LColor[] colors, float x, float y, float srcX, float srcY,
			float srcWidth, float srcHeight, float rotation) {
		draw(colors, x, y, texture.getWidth() / 2, texture.getHeight() / 2,
				texture.getWidth(), texture.getHeight(), 1f, 1f, rotation,
				srcX, srcY, srcWidth, srcHeight, false, false);
	}

	public void draw(LColor[] colors, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight, float rotation) {
		draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f,
				rotation, srcX, srcY, srcWidth, srcHeight, false, false);
	}
	
	public void draw(float x, float y, float originX, float originY,
			float width, float height, float scaleX, float scaleY,
			float rotation, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {
		draw(colors, x, y, originX, originY, width, height, scaleX, scaleY,
				rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
	}
	
	public void draw(LColor[] colors, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY) {

		final float worldOriginX = x + originX;
		final float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		final float p1x = fx;
		final float p1y = fy;
		final float p2x = fx;
		final float p2y = fy2;
		final float p3x = fx2;
		final float p3y = fy2;
		final float p4x = fx2;
		final float p4y = fy;

		float x1;
		float y1;
		float x2;
		float y2;
		float x3;
		float y3;
		float x4;
		float y4;

		if (rotation != 0) {
			final float cos = MathUtils.cosDeg(rotation);
			final float sin = MathUtils.sinDeg(rotation);

			x1 = cos * p1x - sin * p1y;
			y1 = sin * p1x + cos * p1y;

			x2 = cos * p2x - sin * p2y;
			y2 = sin * p2x + cos * p2y;

			x3 = cos * p3x - sin * p3y;
			y3 = sin * p3x + cos * p3y;

			x4 = x1 + (x3 - x2);
			y4 = y3 - (y2 - y1);
		} else {
			x1 = p1x;
			y1 = p1y;

			x2 = p2x;
			y2 = p2y;

			x3 = p3x;
			y3 = p3y;

			x4 = p4x;
			y4 = p4y;
		}

		x1 += worldOriginX;
		y1 += worldOriginY;
		x2 += worldOriginX;
		y2 += worldOriginY;
		x3 += worldOriginX;
		y3 += worldOriginY;
		x4 += worldOriginX;
		y4 += worldOriginY;

		xOff = srcX * invTexWidth + texture.xOff;
		yOff = srcY * invTexHeight + texture.yOff;
		widthRatio = srcWidth * invTexWidth;
		heightRatio = srcHeight * invTexHeight;

		if (flipX) {
			float tmp = xOff;
			xOff = widthRatio;
			widthRatio = tmp;
		}

		if (flipY) {
			float tmp = yOff;
			yOff = heightRatio;
			heightRatio = tmp;
		}

		if (colors == null) {
			glTexCoord2f(xOff, yOff);
			glVertex3f(x1, y1, 0);
			glTexCoord2f(xOff, heightRatio);
			glVertex3f(x2, y2, 0);
			glTexCoord2f(widthRatio, heightRatio);
			glVertex3f(x3, y3, 0);
			glTexCoord2f(widthRatio, yOff);
			glVertex3f(x4, y4, 0);
		} else {
			isColor = true;
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(xOff, yOff);
			glVertex3f(x1, y1, 0);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(xOff, heightRatio);
			glVertex3f(x2, y2, 0);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);
			glVertex3f(x3, y3, 0);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);
			glVertex3f(x4, y4, 0);
		}

	}

	public void draw(LColor[] colors, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {

		xOff = srcX * invTexWidth + texture.xOff;
		yOff = srcY * invTexHeight + texture.yOff;
		widthRatio = srcWidth * invTexWidth;
		heightRatio = srcHeight * invTexHeight;

		final float fx2 = x + width;
		final float fy2 = y + height;

		if (flipX) {
			float tmp = xOff;
			xOff = widthRatio;
			widthRatio = tmp;
		}

		if (flipY) {
			float tmp = yOff;
			yOff = heightRatio;
			heightRatio = tmp;
		}

		if (colors == null) {
			glTexCoord2f(xOff, yOff);
			glVertex3f(x, y, 0);
			glTexCoord2f(xOff, heightRatio);
			glVertex3f(x, fy2, 0);
			glTexCoord2f(widthRatio, heightRatio);
			glVertex3f(fx2, fx2, 0);
			glTexCoord2f(widthRatio, yOff);
			glVertex3f(fx2, y, 0);
		} else {
			isColor = true;
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(xOff, yOff);
			glVertex3f(x, y, 0);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(xOff, heightRatio);
			glVertex3f(x, fy2, 0);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);
			glVertex3f(fx2, fx2, 0);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);
			glVertex3f(fx2, y, 0);
		}
	}

	public void setImageColor(float r, float g, float b, float a) {
		setColor(LTexture.TOP_LEFT, r, g, b, a);
		setColor(LTexture.TOP_RIGHT, r, g, b, a);
		setColor(LTexture.BOTTOM_LEFT, r, g, b, a);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b, a);
	}

	public void setImageColor(float r, float g, float b) {
		setColor(LTexture.TOP_LEFT, r, g, b);
		setColor(LTexture.TOP_RIGHT, r, g, b);
		setColor(LTexture.BOTTOM_LEFT, r, g, b);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b);
	}

	public void setImageColor(LColor c) {
		if (c == null) {
			return;
		}
		setImageColor(c.r, c.g, c.b, c.a);
	}

	public void setColor(int corner, float r, float g, float b, float a) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		colors[corner].a = a;
	}

	public void setColor(int corner, float r, float g, float b) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
	}

	public void draw(float x, float y, LColor[] c) {
		draw(c, x, y, texture.width, texture.height);
	}

	public void draw(float x, float y, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, texture.width, texture.height);
		if (update) {
			setImageColor(LColor.white);
		}
	}

	public void draw(float x, float y, float width, float height, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, width, height);
		if (update) {
			setImageColor(LColor.white);
		}
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, LColor[] c) {
		draw(c, x, y, width, height, x1, y1, x2, y2);
	}

	public void draw(float x, float y, float width, float height, float x1,
			float y1, float x2, float y2, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, width, height, x1, y1, x2, y2);
		if (update) {
			setImageColor(LColor.white);
		}
	}

	public void draw(float x, float y, float w, float h, float rotation,
			LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, w, h, rotation);
		if (update) {
			setImageColor(LColor.white);
		}
	}

	private boolean checkUpdateColor(LColor c) {
		return c != null && !LColor.white.equals(c);
	}

	public LTexture getTexture() {
		return texture;
	}

	public int getHeight() {
		return texHeight;
	}

	public int getWidth() {
		return texWidth;
	}

	public float getX() {
		return moveX;
	}

	public void setX(float x) {
		this.moveX = x;
	}

	public float getY() {
		return moveY;
	}

	public void setY(float y) {
		this.moveY = y;
	}

	public void setLocation(float x, float y) {
		this.moveX = x;
		this.moveY = y;
	}

	public boolean isLockCoord() {
		return lockCoord;
	}

	public void setLockCoord(boolean lockCoord) {
		this.lockCoord = lockCoord;
	}

	public void postLastCache() {
		if (lastCache != null) {
			LTextureBatch.commit(texture, lastCache);
		}
	}

	public GLCache getLastCache() {
		return lastCache;
	}

	public GLCache newGLCache(boolean reset) {
		return (lastCache = new GLCache(this, reset));
	}

	public GLCache newGLCache() {
		return newGLCache(false);
	}

	public void disposeLastCache() {
		if (lastCache != null) {
			lastCache.dispose();
			lastCache = null;
		}
	}

	public void destoryAll() {
		dispose();
		destroy();
	}

	public void destroy() {
		if (texture != null) {
			texture.destroy();
		}
	}

	@Override
	public void dispose() {
		this.count = 0;
		this.useBegin = false;
		this.isLocked = true;
		if (vertexBuffer != null) {
			NativeSupport.freeMemory(vertexBuffer);
			this.vertexBuffer = null;
		}
		if (coordBuffer != null) {
			NativeSupport.freeMemory(coordBuffer);
			this.coordBuffer = null;
		}
		if (colorBuffer != null) {
			NativeSupport.freeMemory(colorBuffer);
			this.colorBuffer = null;
		}
		this.verts = null;
		this.cols = null;
		this.coords = null;
		if (lastCache != null) {
			lastCache.dispose();
			lastCache = null;
		}
		if (colors != null) {
			colors = null;
		}
	}

}
