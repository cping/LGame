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
package loon;

import loon.canvas.LColor;
import loon.geom.Matrix4;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.BlendMethod;
import loon.opengl.ExpandVertices;
import loon.opengl.GL20;
import loon.opengl.Submit;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.TimeUtils;

/**
 * 这是一个针对单独纹理的批量渲染类,默认绑定在特定Texture上运行（普通纹理texture.geTexturetBatch即可获得）,<br>
 * 也就是当用户操作的纹理有仅有一个时,方便针对此纹理的特定缓存以及渲染操作.
 */
public class LTextureBatch implements LRelease {

	private final static String _batch_name = "texbatch";

	private final ShaderSource source;

	private String name = _batch_name;

	private IntMap<LTextureBatch.Cache> _caches;

	private float baseColor = LColor.white.toFloatBits();

	private boolean isClosed;

	protected boolean isCacheLocked;

	private Cache lastCache;

	private LColor[] colors;

	public static class Cache implements LRelease {

		public float x = 0;

		public float y = 0;

		float[] vertices;

		int vertexIdx;

		int count;

		public Cache(LTextureBatch batch) {
			count = batch.count;
			vertexIdx = batch.vertexIdx;
			float[] verts = batch.expandVertices.getVertices();
			vertices = new float[verts.length];
			System.arraycopy(verts, 0, vertices, 0, verts.length);
		}

		public boolean isClosed() {
			return vertices == null;
		}

		@Override
		public void close() {
			if (vertices != null) {
				vertices = null;
			}
		}

	}

	private float xOff, yOff, widthRatio, heightRatio;

	private float drawWidth, drawHeight;

	private float textureSrcX, textureSrcY;

	private float srcWidth, srcHeight;

	private float renderWidth, renderHeight;

	private Submit mesh;

	private BlendState lastBlendState = BlendState.NonPremultiplied;

	protected int count = 0;

	private final ExpandVertices expandVertices;

	protected float invTexWidth = 0, invTexHeight = 0;

	protected boolean drawing = false;

	private LTexture lastTexture = null;

	private final Matrix4 combinedMatrix = new Matrix4();

	private ShaderProgram shader = null;
	private ShaderProgram customShader = null;
	private ShaderProgram globalShader = null;

	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int maxSpritesInBatch = 0;

	protected boolean isLoaded;

	protected LTexture texture;

	private Matrix4 batchMatrix;

	private int vertexIdx;

	private int texWidth, texHeight;

	private float tx, ty;

	public LTextureBatch(LTexture tex) {
		this(tex, 256, LSystem.getShaderSource(), null);
	}

	public LTextureBatch(LTexture tex, final ShaderSource src) {
		this(tex, src, 256);
	}

	public LTextureBatch(LTexture tex, final ShaderSource src, int size) {
		this(tex, size, src, null);
	}

	public LTextureBatch(LTexture tex, final int size, final ShaderSource src, final ShaderProgram defaultShader) {
		if (size > 5460) {
			throw new LSysException("Can't have more than 5460 sprites per batch: " + size);
		}
		this.setTexture(tex);
		this.source = src;
		this.shader = defaultShader;
		this.expandVertices = new ExpandVertices(size);
		this.mesh = new Submit();
	}

	/**
	 * 使用独立的矩阵渲染纹理(这个函数是专门为Live2d增加的，因为官方API本身的矩阵限制，没法和loon已有的view做混合运算（
	 * 否则会产生奇怪的效果(因为是2D框架，不需要处理长宽高，所以我默认只用了一个2d矩阵，和live2d的矩阵相乘后会混乱的……)）)
	 * 
	 * @param val
	 */
	public LTextureBatch setBatchMatrix(float[] val) {
		if (batchMatrix == null) {
			batchMatrix = new Matrix4(val);
		} else {
			batchMatrix.set(val);
		}
		return this;
	}

	public LTextureBatch setBatchMatrix(Matrix4 m) {
		if (batchMatrix == null) {
			batchMatrix = new Matrix4(m);
		} else {
			batchMatrix.set(m);
		}
		return this;
	}

	public LTextureBatch setTexture(LTexture tex2d) {
		this.texture = tex2d;
		this.texWidth = (int) texture.width();
		this.texHeight = (int) texture.height();
		if (texture.isCopy()) {
			invTexWidth = (1f / texture.width());
			invTexHeight = (1f / texture.height());
		} else {
			invTexWidth = (1f / texture.width()) * texture.widthRatio();
			invTexHeight = (1f / texture.height()) * texture.heightRatio();
		}
		return this;
	}

	public LTextureBatch setLocation(float tx, float ty) {
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	public float getInvTexWidth() {
		return this.invTexWidth;
	}

	public float getInvTexHeight() {
		return this.invTexHeight;
	}

	public LTexture toTexture() {
		return texture;
	}

	public LTextureBatch glColor4f() {
		expandVertices.setVertice(vertexIdx++, baseColor);
		return this;
	}

	public LTextureBatch glColor4f(LColor baseColor) {
		expandVertices.setVertice(vertexIdx++, baseColor.toFloatBits());
		return this;
	}

	public LTextureBatch glColor4f(float r, float g, float b, float a) {
		expandVertices.setVertice(vertexIdx++, LColor.toFloatBits(r, g, b, a));
		return this;
	}

	public LTextureBatch glColor4f(float baseColor) {
		expandVertices.setVertice(vertexIdx++, baseColor);
		return this;
	}

	public LTextureBatch glTexCoord2f(float u, float v) {
		expandVertices.setVertice(vertexIdx++, u);
		expandVertices.setVertice(vertexIdx++, v);
		return this;
	}

	public LTextureBatch glVertex2f(Vector2f v) {
		expandVertices.setVertice(vertexIdx++, v.x);
		expandVertices.setVertice(vertexIdx++, v.y);
		return this;
	}

	public LTextureBatch glVertex2f(float x, float y) {
		expandVertices.setVertice(vertexIdx++, x);
		expandVertices.setVertice(vertexIdx++, y);
		return this;
	}

	public BlendState getBlendState() {
		return lastBlendState;
	}

	public LTextureBatch setBlendState(BlendState state) {
		this.lastBlendState = state;
		return this;
	}

	private static boolean runningCache = false;

	public final static boolean isRunningCache() {
		return runningCache;
	}

	public LTextureBatch begin() {
		if (!isLoaded) {
			if (shader == null) {
				shader = LSystem.createShader(source.vertexShader(), source.fragmentShader());
			}
			isLoaded = true;
		}
		if (drawing) {
			throw new LSysException("TextureBatch.end must be called before begin.");
		}
		LSystem.mainEndDraw();
		if (!isCacheLocked) {
			vertexIdx = 0;
			lastTexture = null;
		}
		LSystem.base().graphics().gl.glDepthMask(false);
		if (customShader != null) {
			customShader.begin();
		} else {
			shader.begin();
		}
		setupMatrices(LSystem.base().graphics().getViewMatrix());
		drawing = true;
		runningCache = true;
		return this;
	}

	public LTextureBatch end() {
		if (!isLoaded) {
			return this;
		}
		if (!drawing) {
			throw new LSysException("TextureBatch.begin must be called before end.");
		}
		if (vertexIdx > 0) {
			if (tx != 0 || ty != 0) {
				Matrix4 project = LSystem.base().graphics().getViewMatrix().cpy();
				project.translate(tx, ty, 0);
				if (drawing) {
					setupMatrices(project);
				}
			}
			submit();
		}
		drawing = false;
		LSystem.base().graphics().gl.glDepthMask(true);
		if (customShader != null) {
			customShader.end();
		} else {
			shader.end();
		}
		LSystem.mainBeginDraw();
		return this;
	}

	public LTextureBatch draw(float[] vertices, int offset, int length) {
		if (checkTexture(texture)) {
			return this;
		}
		int remainingVertices = expandVertices.length() - vertexIdx;
		if (remainingVertices == 0) {
			submit();
			remainingVertices = expandVertices.length();
		}
		int vertexCount = MathUtils.min(remainingVertices, length - offset);
		System.arraycopy(vertices, offset, expandVertices.getVertices(), vertexIdx, vertexCount);
		offset += vertexCount;
		vertexIdx += vertexCount;
		while (offset < length) {
			submit();
			vertexCount = MathUtils.min(expandVertices.length(), length - offset);
			System.arraycopy(vertices, offset, expandVertices.getVertices(), 0, vertexCount);
			offset += vertexCount;
			vertexIdx += vertexCount;
		}
		return this;
	}

	public LTextureBatch setColor(LColor tint) {
		baseColor = tint.toFloatBits();
		return this;
	}

	public LTextureBatch setColor(float r, float g, float b, float a) {
		int intBits = (int) (255 * a) << 24 | (int) (255 * b) << 16 | (int) (255 * g) << 8 | (int) (255 * r);
		baseColor = NumberUtils.intToFloatColor(intBits);
		return this;
	}

	public LTextureBatch setColor(float baseColor) {
		this.baseColor = baseColor;
		return this;
	}

	public LColor getColor() {
		int intBits = NumberUtils.floatToIntColor(baseColor);
		LColor baseColor = tempColor;
		baseColor.r = (intBits & 0xff) / 255f;
		baseColor.g = ((intBits >>> 8) & 0xff) / 255f;
		baseColor.b = ((intBits >>> 16) & 0xff) / 255f;
		baseColor.a = ((intBits >>> 24) & 0xff) / 255f;
		return baseColor;
	}

	public float getFloatColor() {
		return baseColor;
	}

	private void checkDrawing() {
		if (!drawing) {
			throw new LSysException("Not implemented begin !");
		}
	}

	public boolean checkTexture(final LTexture texture) {
		if (!isLoaded || isCacheLocked) {
			return false;
		}
		if (isClosed) {
			return false;
		}
		if (texture == null) {
			return false;
		}
		checkDrawing();
		if (!texture.isLoaded()) {
			texture.loadTexture();
		}
		LTexture tex2d = LTexture.firstFather(texture);
		if (tex2d != null) {
			if (tex2d != lastTexture) {
				submit();
				lastTexture = tex2d;
			} else if (vertexIdx == expandVertices.length()) {
				submit();
			}
			invTexWidth = (1f / texWidth) * texture.widthRatio();
			invTexHeight = (1f / texHeight) * texture.heightRatio();
		} else if (texture != lastTexture) {
			submit();
			lastTexture = texture;
			invTexWidth = (1f / texWidth) * texture.widthRatio();
			invTexHeight = (1f / texHeight) * texture.heightRatio();
		} else if (vertexIdx == expandVertices.length()) {
			submit();
		}

		return true;
	}

	public LTextureBatch submit() {
		return submit(lastBlendState);
	}

	public LTextureBatch submit(BlendState state) {
		if (vertexIdx == 0) {
			return this;
		}
		if (!isCacheLocked) {
			int spritesInBatch = vertexIdx / 20;
			if (spritesInBatch > maxSpritesInBatch) {
				maxSpritesInBatch = spritesInBatch;
			}
			this.count = spritesInBatch * 6;
		}
		GL20 gl = LSystem.base().graphics().gl;
		GLUtils.bindTexture(gl, texture.getID());
		int old = GLUtils.getBlendMode();
		try {
			switch (lastBlendState) {
			case Additive:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_ALPHA_ONE);
				break;
			case AlphaBlend:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NORMAL);
				break;
			case Opaque:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NONE);
				break;
			case NonPremultiplied:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_SPEED);
				break;
			case Null:
				break;
			}
			mesh.post(name, expandVertices.getSize(), customShader != null ? customShader : shader,
					expandVertices.getVertices(), vertexIdx, count);
		} catch (Throwable e) {
			LSystem.error("TextureBatch submit() exception", e);
		} finally {
			if (expandVertices.expand(this.vertexIdx)) {
				mesh.reset(name, expandVertices.length());
			}
			GLUtils.setBlendMode(gl, old);
		}
		return this;
	}

	public LTextureBatch setTextureBatchName(String n) {
		this.name = n;
		return this;
	}

	public String getTextureBatchName() {
		return this.name;
	}

	private LTextureBatch setupMatrices(Matrix4 view) {
		if (batchMatrix != null) {
			combinedMatrix.set(batchMatrix);
		} else {
			combinedMatrix.set(view);
		}
		if (customShader != null) {
			customShader.setUniformMatrix("u_projTrans", combinedMatrix);
			customShader.setUniformi("u_texture", 0);
			source.setupShader(customShader);
		} else {
			shader.setUniformMatrix("u_projTrans", combinedMatrix);
			shader.setUniformi("u_texture", 0);
			source.setupShader(shader);
		}
		return this;
	}

	protected LTextureBatch setShader(Matrix4 view, ShaderProgram shader) {
		if (drawing) {
			submit();
			if (customShader != null) {
				customShader.end();
			} else {
				this.shader.end();
			}
		}
		customShader = shader;
		if (drawing) {
			if (customShader != null) {
				customShader.begin();
			} else {
				this.shader.begin();
			}
			setupMatrices(view);
		}
		return this;
	}

	public boolean isDrawing() {
		return drawing;
	}

	public LTextureBatch lock() {
		this.isCacheLocked = true;
		return this;
	}

	public LTextureBatch unLock() {
		this.isCacheLocked = false;
		return this;
	}

	private LTextureBatch commit(Matrix4 view, Cache cache, LColor baseColor, BlendState state) {
		if (!isLoaded) {
			return this;
		}
		if (drawing) {
			end();
		}
		LSystem.mainEndDraw();
		if (baseColor == null) {
			if (shader == null) {
				shader = LSystem.createShader(source.vertexShader(), source.fragmentShader());
			}
			globalShader = shader;
		} else if (globalShader == null) {
			globalShader = LSystem.createShader(LSystem.getGLExVertexShader(), LSystem.getColorFragmentShader());
		}
		globalShader.begin();
		float oldColor = getFloatColor();
		if (baseColor != null) {
			globalShader.setUniformf("v_color", baseColor.r, baseColor.g, baseColor.b, baseColor.a);
		}
		if (batchMatrix != null) {
			combinedMatrix.set(batchMatrix);
		} else {
			combinedMatrix.set(view);
		}
		if (globalShader != null) {
			globalShader.setUniformMatrix("u_projTrans", combinedMatrix);
			globalShader.setUniformi("u_texture", 0);
		}
		if (cache.vertexIdx > 0) {
			GL20 gl = LSystem.base().graphics().gl;
			GLUtils.bindTexture(gl, texture.getID());
			int old = GLUtils.getBlendMode();
			switch (lastBlendState) {
			case Additive:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_ALPHA_ONE);
				break;
			case AlphaBlend:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NORMAL);
				break;
			case Opaque:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_NONE);
				break;
			case NonPremultiplied:
				GLUtils.setBlendMode(gl, BlendMethod.MODE_SPEED);
				break;
			case Null:
				break;
			}
			mesh.post(name, expandVertices.getSize(), globalShader, cache.vertices, cache.vertexIdx, cache.count);
			GLUtils.setBlendMode(gl, old);
		} else if (baseColor != null) {
			globalShader.setUniformf("v_color", oldColor);
		}
		globalShader.end();
		LSystem.mainBeginDraw();
		runningCache = true;
		return this;
	}

	public LTextureBatch setIndices(short[] indices) {
		mesh.getMesh(name, expandVertices.getSize()).setIndices(indices);
		return this;
	}

	public LTextureBatch resetIndices() {
		mesh.resetIndices(name, expandVertices.getSize());
		return this;
	}

	public LTextureBatch setGLType(int type) {
		mesh.setGLType(type);
		return this;
	}

	public boolean postLastCache() {
		if (lastCache != null) {
			commit(LSystem.base().graphics().getViewMatrix(), lastCache, null, lastBlendState);
			return true;
		}
		return false;
	}

	public Cache getLastCache() {
		return lastCache;
	}

	public boolean existCache() {
		return lastCache != null && lastCache.count > 0;
	}

	public Cache newCache() {
		if (isLoaded) {
			return (lastCache = new Cache(this));
		} else {
			return null;
		}
	}

	public boolean disposeLastCache() {
		if (lastCache != null) {
			lastCache.close();
			lastCache = null;
			return true;
		}
		return false;
	}

	public LTextureBatch draw(float x, float y) {
		return draw(colors, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height());
	}

	public LTextureBatch draw(float x, float y, float width, float height) {
		return draw(colors, x, y, width, height, 0, 0, texture.width(), texture.height());
	}

	public LTextureBatch draw(float x, float y, float width, float height, float srcX, float srcY, float srcWidth,
			float srcHeight) {
		return draw(colors, x, y, width, height, srcX, srcY, srcWidth, srcHeight);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height) {
		return draw(colors, x, y, width, height, 0, 0, texture.width(), texture.height());
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
	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight) {

		if (!checkTexture(texture)) {
			return this;
		}

		xOff = srcX * invTexWidth + texture.xOff();
		yOff = srcY * invTexHeight + texture.yOff();
		widthRatio = srcWidth * invTexWidth;
		heightRatio = srcHeight * invTexHeight;

		final float fx2 = x + width;
		final float fy2 = y + height;

		if (colors == null) {
			glVertex2f(x, y);
			glColor4f();
			glTexCoord2f(xOff, yOff);

			glVertex2f(x, fy2);
			glColor4f();
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f();
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(fx2, y);
			glColor4f();
			glTexCoord2f(widthRatio, yOff);

		} else {
			glVertex2f(x, y);
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(xOff, yOff);

			glVertex2f(x, fy2);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(fx2, y);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);

		}
		return this;
	}

	public LTextureBatch drawQuad(float drawX, float drawY, float c1, float drawX2, float drawY2, float c2, float srcX,
			float srcY, float c3, float srcX2, float srcY2, float c4) {

		if (!checkTexture(texture)) {
			return this;
		}

		drawWidth = drawX2 - drawX;
		drawHeight = drawY2 - drawY;
		textureSrcX = ((srcX / texWidth) * texture.widthRatio()) + texture.xOff();
		textureSrcY = ((srcY / texHeight) * texture.heightRatio()) + texture.yOff();
		srcWidth = srcX2 - srcX;
		srcHeight = srcY2 - srcY;
		renderWidth = ((srcWidth / texWidth) * texture.widthRatio());
		renderHeight = ((srcHeight / texHeight) * texture.heightRatio());

		glVertex2f(drawX, drawY);
		glColor4f(c1);
		glTexCoord2f(textureSrcX, textureSrcY);

		glVertex2f(drawX, drawY + drawHeight);
		glColor4f(c2);
		glTexCoord2f(textureSrcX, textureSrcY + renderHeight);

		glVertex2f(drawX + drawWidth, drawY + drawHeight);
		glColor4f(c3);
		glTexCoord2f(textureSrcX + renderWidth, textureSrcY + renderHeight);

		glVertex2f(drawX + drawWidth, drawY);
		glColor4f(c4);
		glTexCoord2f(textureSrcX + renderWidth, textureSrcY);

		return this;
	}

	public LTextureBatch drawQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		return drawQuad(x1, y1, baseColor, x2, y2, baseColor, x3, y3, baseColor, x4, y4, baseColor);
	}

	public LTextureBatch draw(float x, float y, LColor[] c) {
		draw(c, x, y, texture.width(), texture.height());
		return this;
	}

	public LTextureBatch draw(float x, float y, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, texture.width(), texture.height());
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(float x, float y, float width, float height, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, width, height);
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor[] c) {
		draw(c, x, y, width, height, x1, y1, x2, y2);
		return this;
	}

	public LTextureBatch draw(float x, float y, float width, float height, float x1, float y1, float x2, float y2,
			LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, width, height, x1, y1, x2, y2);
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(float x, float y, float w, float h, float rotation, LColor c) {
		final boolean update = checkUpdateColor(c);
		if (update) {
			setImageColor(c);
		}
		draw(colors, x, y, w, h, rotation);
		if (update) {
			setImageColor(LColor.white);
		}
		return this;
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float rotation) {
		return draw(colors, x, y, texture.width() / 2, texture.height() / 2, texture.width(), texture.height(), 1f, 1f,
				rotation, 0, 0, texture.width(), texture.height(), false, false);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float rotation) {
		return draw(colors, x, y, texture.width() / 2, texture.height() / 2, width, height, 1f, 1f, rotation, 0, 0,
				texture.width(), texture.height(), false, false);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float srcX, float srcY, float srcWidth,
			float srcHeight, float rotation) {
		return draw(colors, x, y, texture.width() / 2, texture.height() / 2, texture.width(), texture.height(), 1f, 1f,
				rotation, srcX, srcY, srcWidth, srcHeight, false, false);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, float rotation) {
		return draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, false, false);
	}

	public LTextureBatch draw(float x, float y, float width, float height, float srcX, float srcY, float srcWidth,
			float srcHeight, float rotation, boolean flipX, boolean flipY) {
		return draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, float rotation, boolean flipX, boolean flipY) {
		return draw(colors, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(float x, float y, float width, float height, float scaleX, float scaleY, float rotation,
			float srcX, float srcY, float srcWidth, float srcHeight, boolean flipX, boolean flipY) {
		return draw(colors, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(float x, float y, float originX, float originY, float width, float height, float scaleX,
			float scaleY, float rotation, float srcX, float srcY, float srcWidth, float srcHeight, boolean flipX,
			boolean flipY) {
		return draw(colors, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY);
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float originX, float originY, float width,
			float height, float scaleX, float scaleY, float rotation, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {

		if (!checkTexture(texture)) {
			return this;
		}
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

		xOff = srcX * invTexWidth + texture.xOff();
		yOff = srcY * invTexHeight + texture.yOff();
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
			glVertex2f(x1, y1);
			glColor4f();
			glTexCoord2f(xOff, yOff);

			glVertex2f(x2, y2);
			glColor4f();
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(x3, y3);
			glColor4f();
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(x4, y4);
			glColor4f();
			glTexCoord2f(widthRatio, yOff);

		} else {
			glVertex2f(x1, y1);
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(xOff, yOff);

			glVertex2f(x2, y2);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(x3, y3);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(x4, y4);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);
		}
		return this;
	}

	public LTextureBatch draw(LColor[] colors, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY) {

		if (!checkTexture(texture)) {
			return this;
		}
		xOff = srcX * invTexWidth + texture.xOff();
		yOff = srcY * invTexHeight + texture.yOff();
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
			glVertex2f(x, y);
			glColor4f();
			glTexCoord2f(xOff, yOff);

			glVertex2f(x, fy2);
			glColor4f();
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f();
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(fx2, y);
			glColor4f();
			glTexCoord2f(widthRatio, yOff);
		} else {
			glVertex2f(x, y);
			glColor4f(colors[LTexture.TOP_LEFT]);
			glTexCoord2f(xOff, yOff);

			glVertex2f(x, fy2);
			glColor4f(colors[LTexture.BOTTOM_LEFT]);
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(fx2, fy2);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(fx2, y);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);
		}
		return this;
	}

	public LTextureBatch setImageColor(float r, float g, float b, float a) {
		setColor(LTexture.TOP_LEFT, r, g, b, a);
		setColor(LTexture.TOP_RIGHT, r, g, b, a);
		setColor(LTexture.BOTTOM_LEFT, r, g, b, a);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b, a);
		return this;
	}

	public LTextureBatch setImageColor(float r, float g, float b) {
		setColor(LTexture.TOP_LEFT, r, g, b);
		setColor(LTexture.TOP_RIGHT, r, g, b);
		setColor(LTexture.BOTTOM_LEFT, r, g, b);
		setColor(LTexture.BOTTOM_RIGHT, r, g, b);
		return this;
	}

	public LTextureBatch setImageColor(LColor c) {
		if (c == null) {
			return this;
		}
		setImageColor(c.r, c.g, c.b, c.a);
		return this;
	}

	public LTextureBatch draw(short[] indexArray, float[] vertexArray, float[] uvArray, float x, float y, float sx,
			float sy, LColor baseColor) {
		int length = vertexArray.length;
		if (indexArray.length < 1024) {
			short[] indices = new short[1024];
			for (int i = 0; i < indexArray.length; i++) {
				indices[i] = indexArray[i];
			}
			for (int i = 0; i < indexArray.length; i++) {
				indices[i + indexArray.length] = indexArray[i];
			}
			setIndices(indices);
		} else if (indexArray.length < 2048) {
			short[] indices = new short[2048];
			for (int i = 0; i < indexArray.length; i++) {
				indices[i] = indexArray[i];
			}
			for (int i = 0; i < indexArray.length; i++) {
				indices[i + indexArray.length] = indexArray[i];
			}
			setIndices(indices);
		} else if (indexArray.length < 4096) {
			short[] indices = new short[4096];
			for (int i = 0; i < indexArray.length; i++) {
				indices[i] = indexArray[i];
			}
			for (int i = 0; i < indexArray.length; i++) {
				indices[i + indexArray.length] = indexArray[i];
			}
			setIndices(indices);
		}
		for (int q = 0; q < 4; q++) {
			for (int idx = 0; idx < length; idx += 2) {
				glVertex2f(vertexArray[idx] * sx + x, vertexArray[idx + 1] * sy + y);
				glColor4f(baseColor.r, baseColor.g, baseColor.b, baseColor.a);
				glTexCoord2f(uvArray[idx], uvArray[idx + 1]);
			}
		}
		return this;
	}

	public LTextureBatch setColor(int corner, float r, float g, float b, float a) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		colors[corner].a = a;
		return this;
	}

	public LTextureBatch setColor(int corner, float r, float g, float b) {
		if (colors == null) {
			colors = new LColor[] { new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f), new LColor(1f, 1f, 1f, 1f),
					new LColor(1f, 1f, 1f, 1f) };
		}
		colors[corner].r = r;
		colors[corner].g = g;
		colors[corner].b = b;
		return this;
	}

	private boolean checkUpdateColor(LColor c) {
		return c != null && !LColor.white.equals(c);
	}

	public LTextureBatch commit(float x, float y, float sx, float sy, float ax, float ay, float rotaion) {
		if (isClosed) {
			return this;
		}
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		boolean update = (x != 0 || y != 0 || rotaion != 0 || sx != 1f || sy != 1f);
		if (update) {
			project = project.cpy();
		}
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		if (sx != 1f || sy != 1f) {
			project.scale(sx, sy, 0);
		}
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				project.translate(ax, ay, 0.0f);
				project.rotate(0f, 0f, 1f, rotaion);
				project.translate(-ax, -ay, 0.0f);
			} else {
				project.translate(texture.width() / 2, texture.height() / 2, 0.0f);
				project.rotate(0f, 0f, 0f, rotaion);
				project.translate(-texture.width() / 2, -texture.height() / 2, 0.0f);
			}
		}
		if (drawing) {
			setupMatrices(project);
		}
		end();
		runningCache = true;
		return this;
	}

	public LTextureBatch postCache(Cache cache, LColor baseColor, float x, float y) {
		if (isClosed) {
			return this;
		}
		x += cache.x;
		y += cache.y;
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		if (x != 0 || y != 0) {
			project = project.cpy();
			project.translate(x, y, 0);
		}
		commit(project, cache, baseColor, lastBlendState);
		return this;
	}

	public LTextureBatch postCache(Cache cache, LColor baseColor, float x, float y, float sx, float sy, float ax,
			float ay, float rotaion) {
		if (isClosed) {
			return this;
		}
		x += cache.x;
		y += cache.y;
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		boolean update = (x != 0 || y != 0 || rotaion != 0 || sx != 1f || sy != 1f);
		if (update) {
			project = project.cpy();
		}
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		if (sx != 1f || sy != 1f) {
			project.scale(sx, sy, 0);
		}
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				project.translate(ax, ay, 0.0f);
				project.rotate(0f, 0f, 1f, rotaion);
				project.translate(-ax, -ay, 0.0f);
			} else {
				project.translate(texture.width() / 2, texture.height() / 2, 0.0f);
				project.rotate(0f, 0f, 0f, rotaion);
				project.translate(-texture.width() / 2, -texture.height() / 2, 0.0f);
			}
		}
		commit(project, cache, baseColor, lastBlendState);
		return this;
	}

	public LTextureBatch postCache(Cache cache, LColor baseColor, float rotaion) {
		if (isClosed) {
			return this;
		}
		float x = cache.x;
		float y = cache.y;
		Matrix4 project = LSystem.base().graphics().getViewMatrix();
		if (rotaion != 0) {
			project = project.cpy();
			project.translate((texture.width() / 2) + x, (y + texture.height() / 2) + y, 0.0f);
			project.rotate(0f, 0f, 1f, rotaion);
			project.translate((-texture.width() / 2) + y, (-texture.height() / 2) + y, 0.0f);
		}
		commit(project, cache, baseColor, lastBlendState);
		return this;
	}

	public LTextureBatch postCache(LColor baseColor, float rotaion) {
		if (lastCache != null) {
			postCache(lastCache, baseColor, rotaion);
		}
		return this;
	}

	public int saveCache() {
		return saveCache((int) (TimeUtils.millis() + (_caches == null ? 1 : _caches.size)));
	}

	public int saveCache(int hashCodeValue) {
		if (_caches == null) {
			_caches = new IntMap<LTextureBatch.Cache>();
		}
		LTextureBatch.Cache cache = newCache();
		if (cache != null) {
			_caches.put(hashCodeValue, cache);
		}
		return hashCodeValue;
	}

	public LTextureBatch.Cache restoreCachePost(int hashCodeValue) {
		return restoreCachePost(hashCodeValue, colors == null ? LColor.white : colors[0], 0f, 0f);
	}

	public LTextureBatch.Cache restoreCachePost(int hashCodeValue, LColor baseColor, float x, float y) {
		LTextureBatch.Cache cache = restoreCache(hashCodeValue);
		if (cache != null) {
			postCache(cache, baseColor, x, y);
		}
		return cache;
	}

	public LTextureBatch.Cache restoreCache(int hashCodeValue) {
		if (_caches != null) {
			return _caches.get(hashCodeValue);
		}
		return null;
	}

	public int getTextureID() {
		if (texture != null) {
			return texture.getID();
		}
		return -1;
	}

	public int getTextureHashCode() {
		if (texture != null) {
			return texture.hashCode();
		}
		return -1;
	}

	public boolean closed() {
		return isClosed;
	}

	public boolean isClosed() {
		return closed();
	}

	@Override
	public void close() {
		isClosed = true;
		isLoaded = false;
		isCacheLocked = false;
		if (shader != null) {
			shader.close();
		}
		if (globalShader != null) {
			globalShader.close();
		}
		if (customShader != null) {
			customShader.close();
		}
		if (_caches != null) {
			for (LTextureBatch.Cache cache : _caches) {
				if (cache != null) {
					cache.close();
				}
			}
			_caches.clear();
		}
		if (lastCache != null) {
			lastCache.close();
		}
		if (!_batch_name.equals(name)) {
			mesh.dispose(name, expandVertices.getSize());
		}
		LSystem.disposeBatchCache(this, false);
		runningCache = false;
	}

	public LTextureBatch destroy() {
		if (texture != null) {
			texture.close(true);
		}
		return this;
	}
}
