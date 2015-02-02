package loon.core.graphics.opengl;

import java.util.HashMap;

import loon.LSystem;
import loon.action.sprite.SpriteBatch.BlendState;
import loon.action.sprite.SpriteRegion;
import loon.core.LRelease;
import loon.core.geom.Matrix4;
import loon.core.graphics.device.LColor;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class LTextureBatch implements LRelease {

	private boolean isClosed;

	public boolean isCacheLocked;

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

	private Cache lastCache;

	private LColor[] colors;

	public static class Cache implements LRelease {

		float[] vertices;

		int vertexIdx;

		int count;

		public Cache(LTextureBatch batch) {
			count = batch.count;
			vertexIdx = batch.vertexIdx;
			vertices = new float[batch.vertices.length];
			System.arraycopy(batch.vertices, 0, vertices, 0,
					batch.vertices.length);
		}

		public void dispose() {
			if (vertices != null) {
				vertices = null;
			}
		}

	}

	int count = 0;

	float[] vertices;

	float invTexWidth = 0, invTexHeight = 0;

	boolean drawing = false;

	private LTexture lastTexture = null;

	private final Matrix4 combinedMatrix = new Matrix4();

	private ShaderProgram shader = null;
	private ShaderProgram customShader = null;
	private ShaderProgram globalShader = null;

	private final float whiteColor = LColor.white.toFloatBits();
	float color = whiteColor;
	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int maxSpritesInBatch = 0;

	boolean isLoaded;

	LTexture texture;

	private int vertexIdx;

	private int texWidth, texHeight;

	private int size = 0;

	private float tx, ty;

	public void setLocation(float tx, float ty) {
		this.tx = tx;
		this.ty = ty;
	}

	private BlendState lastBlendState = BlendState.NonPremultiplied;

	public LTextureBatch(LTexture tex) {
		this(tex, 4096, null);
	}

	public LTextureBatch(LTexture tex, int size) {
		this(tex, size, null);
	}

	public void setTexture(LTexture tex2d) {
		this.texture = tex2d;
		this.texWidth = texture.width;
		this.texHeight = texture.height;
		this.invTexWidth = (1f / texWidth) * texture.widthRatio;
		this.invTexHeight = (1f / texHeight) * texture.heightRatio;
	}

	private final static Matrix4 cacheProjectionMatrix = new Matrix4();

	public LTextureBatch(LTexture tex, final int size,
			final ShaderProgram defaultShader) {
		if (size > 5460) {
			throw new IllegalArgumentException(
					"Can't have more than 5460 sprites per batch: " + size);
		}
		cacheProjectionMatrix.setToOrtho2D(0, 0, GLEx.width(), GLEx.height());
		this.setTexture(tex);
		this.shader = defaultShader;
		this.size = size;
	}

	public void glColor4f() {
		vertices[vertexIdx++] = color;
	}

	public void glColor4f(LColor color) {
		vertices[vertexIdx++] = color.toFloatBits();
	}

	public void glColor4f(float r, float g, float b, float a) {
		vertices[vertexIdx++] = LColor.toFloatBits(r, g, b, a);
	}

	public void glTexCoord2f(float u, float v) {
		vertices[vertexIdx++] = u;
		vertices[vertexIdx++] = v;
	}

	public void glVertex2f(float x, float y) {
		vertices[vertexIdx++] = x;
		vertices[vertexIdx++] = y;
	}

	public BlendState getBlendState() {
		return lastBlendState;
	}

	public void setBlendState(BlendState state) {
		if (state != lastBlendState) {
			this.lastBlendState = state;
		}
	}

	public void begin() {
		if (!isLoaded) {

			vertices = new float[size * SpriteRegion.SPRITE_SIZE];

			if (shader == null) {
				shader = GLEx.createDefaultShader();
			}
			isLoaded = true;
		}
		if (drawing) {
			throw new IllegalStateException(
					"SpriteBatch.end must be called before begin.");
		}
		if (!isCacheLocked) {
			vertexIdx = 0;
			lastTexture = null;
		}
		GLEx.gl.glDepthMask(false);
		if (customShader != null) {
			customShader.begin();
		} else {
			shader.begin();
		}
		setupMatrices();
		drawing = true;
	}

	public void end() {
		if (!isLoaded) {
			return;
		}
		if (!drawing) {
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before end.");
		}
		if (vertexIdx > 0) {
			if (tx != 0 || ty != 0) {
				GLEx.self.save();
				Matrix4 project = GLEx.getProjectionMatrix();
				project.translate(tx, ty, 0);
				if (drawing) {
					setupMatrices();
				}
			}
			submit();
			if (tx != 0 || ty != 0) {
				GLEx.self.restore();
			}
		}
		drawing = false;
		GLEx.gl.glDepthMask(true);
		if (customShader != null) {
			customShader.end();
		} else {
			shader.end();
		}
	}

	public void setColor(LColor tint) {
		color = tint.toFloatBits();
	}

	public void setColor(float r, float g, float b, float a) {
		int intBits = (int) (255 * a) << 24 | (int) (255 * b) << 16
				| (int) (255 * g) << 8 | (int) (255 * r);
		color = NumberUtils.intToFloatColor(intBits);
	}

	public void setColor(float color) {
		this.color = color;
	}

	public LColor getColor() {
		int intBits = NumberUtils.floatToIntColor(color);
		LColor color = tempColor;
		color.r = (intBits & 0xff) / 255f;
		color.g = ((intBits >>> 8) & 0xff) / 255f;
		color.b = ((intBits >>> 16) & 0xff) / 255f;
		color.a = ((intBits >>> 24) & 0xff) / 255f;
		return color;
	}

	public float getFloatColor() {
		return color;
	}

	private void checkDrawing() {
		if (!drawing) {
			throw new IllegalStateException("Not implemented begin !");
		}
	}

	private boolean checkTexture(final LTexture texture) {
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
		LTexture tex2d = texture.getParent();
		if (tex2d != null) {
			if (tex2d != lastTexture) {
				submit();
				lastTexture = tex2d;
			} else if (vertexIdx == vertices.length) {
				submit();
			}
			invTexWidth = (1f / texWidth) * texture.widthRatio;
			invTexHeight = (1f / texHeight) * texture.heightRatio;
		} else if (texture != lastTexture) {
			submit();
			lastTexture = texture;
			invTexWidth = (1f / texWidth) * texture.widthRatio;
			invTexHeight = (1f / texHeight) * texture.heightRatio;
		} else if (vertexIdx == vertices.length) {
			submit();
		}

		return true;
	}

	public void submit() {
		submit(lastBlendState);
	}

	public void submit(BlendState state) {
		if (vertexIdx == 0) {
			return;
		}
		if (!isCacheLocked) {
			int spritesInBatch = vertexIdx / 20;
			if (spritesInBatch > maxSpritesInBatch) {
				maxSpritesInBatch = spritesInBatch;
			}
			this.count = spritesInBatch * 6;
		}
		GLEx.self.bind(texture);
		switch (lastBlendState) {
		case Additive:
			GLEx.self.setBlendMode(GL.MODE_ALPHA_ONE);
			break;
		case AlphaBlend:
			GLEx.self.setBlendMode(GL.MODE_SPEED);
			break;
		case Opaque:
			GLEx.self.setBlendMode(GL.MODE_NONE);
			break;
		case NonPremultiplied:
			GLEx.self.setBlendMode(GL.MODE_NORMAL);
			break;
		}
		MeshDefault.post(customShader != null ? customShader : shader, vertices,
				vertexIdx, count);

	}

	private void setupMatrices() {
		combinedMatrix.set(GLEx.getProjectionMatrix()).mul(
				GLEx.getTransformMatrix());
		if (customShader != null) {
			customShader.setUniformMatrix("u_projTrans", combinedMatrix);
			customShader.setUniformi("u_texture", 0);
		} else {
			shader.setUniformMatrix("u_projTrans", combinedMatrix);
			shader.setUniformi("u_texture", 0);
		}
	}

	protected void switchTexture(LTexture texture) {
		submit();
		lastTexture = texture;
		invTexWidth = 1.0f / texWidth;
		invTexHeight = 1.0f / texHeight;
	}

	public void setShader(ShaderProgram shader) {
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
			setupMatrices();
		}

	}

	public boolean isDrawing() {
		return drawing;
	}

	public void lock() {
		this.isCacheLocked = true;
	}

	public void unLock() {
		this.isCacheLocked = false;
	}

	private void commit(Cache cache, LColor color, BlendState state) {
		if (!isLoaded) {
			return;
		}
		if (drawing) {
			end();
		}
		if (color == null) {
			if (shader == null) {
				shader = GLEx.createDefaultShader();
			}
			globalShader = shader;
		} else if (globalShader == null) {
			globalShader = GLEx.createGlobalShader();
		}
		globalShader.begin();
		float oldColor = getFloatColor();
		if (color != null) {
			globalShader.setUniformf("v_color", color.r, color.g, color.b,
					GLEx.self.lastAlpha != 1f ? GLEx.self.lastAlpha : color.a);
		}
		combinedMatrix.set(GLEx.getProjectionMatrix()).mul(
				GLEx.getTransformMatrix());
		if (globalShader != null) {
			globalShader.setUniformMatrix("u_projTrans", combinedMatrix);
			globalShader.setUniformi("u_texture", 0);
		}
		if (cache.vertexIdx > 0) {
			GLEx.self.bind(texture);
			switch (lastBlendState) {
			case Additive:
				GLEx.self.setBlendMode(GL.MODE_ALPHA_ONE);
				break;
			case AlphaBlend:
				GLEx.self.setBlendMode(GL.MODE_SPEED);
				break;
			case Opaque:
				GLEx.self.setBlendMode(GL.MODE_NONE);
				break;
			case NonPremultiplied:
				GLEx.self.setBlendMode(GL.MODE_NORMAL);
				break;
			}
			MeshDefault.post(globalShader, cache.vertices, cache.vertexIdx, cache.count);
		}
		if (color != null) {
			globalShader.setUniformf("v_color", oldColor);
		}
		globalShader.end();
	}

	public void postLastCache() {
		if (lastCache != null) {
			commit(lastCache, null, lastBlendState);
		}
	}

	public Cache getLastCache() {
		return lastCache;
	}

	public boolean existCache() {
		return lastCache != null;
	}

	public Cache newCache() {
		if (isLoaded) {
			return (lastCache = new Cache(this));
		} else {
			return null;
		}
	}

	public void disposeLastCache() {
		if (lastCache != null) {
			lastCache.dispose();
			lastCache = null;
		}
	}

	private float xOff, yOff, widthRatio, heightRatio;

	private float drawWidth, drawHeight;

	private float textureSrcX, textureSrcY;

	private float srcWidth, srcHeight;

	private float renderWidth, renderHeight;

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

		if (!checkTexture(texture)) {
			return;
		}

		xOff = srcX * invTexWidth + texture.xOff;
		yOff = srcY * invTexHeight + texture.yOff;
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
	}

	public void drawQuad(float drawX, float drawY, float drawX2, float drawY2,
			float srcX, float srcY, float srcX2, float srcY2) {

		if (!checkTexture(texture)) {
			return;
		}

		drawWidth = drawX2 - drawX;
		drawHeight = drawY2 - drawY;
		textureSrcX = ((srcX / texWidth) * texture.widthRatio) + texture.xOff;
		textureSrcY = ((srcY / texHeight) * texture.heightRatio) + texture.yOff;
		srcWidth = srcX2 - srcX;
		srcHeight = srcY2 - srcY;
		renderWidth = ((srcWidth / texWidth) * texture.widthRatio);
		renderHeight = ((srcHeight / texHeight) * texture.heightRatio);

		glVertex2f(drawX, drawY);
		glColor4f();
		glTexCoord2f(textureSrcX, textureSrcY);

		glVertex2f(drawX, drawY + drawHeight);
		glColor4f();
		glTexCoord2f(textureSrcX, textureSrcY + renderHeight);

		glVertex2f(drawX + drawWidth, drawY + drawHeight);
		glColor4f();
		glTexCoord2f(textureSrcX + renderWidth, textureSrcY + renderHeight);

		glVertex2f(drawX + drawWidth, drawY);
		glColor4f();
		glTexCoord2f(textureSrcX + renderWidth, textureSrcY);

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

		if (!checkTexture(texture)) {
			return;
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
	}

	public void draw(LColor[] colors, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {

		if (!checkTexture(texture)) {
			return;
		}
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
			glVertex2f(x, y);
			glColor4f();
			glTexCoord2f(xOff, yOff);

			glVertex2f(x, fy2);
			glColor4f();
			glTexCoord2f(xOff, heightRatio);

			glVertex2f(fx2, fx2);
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

			glVertex2f(fx2, fx2);
			glColor4f(colors[LTexture.BOTTOM_RIGHT]);
			glTexCoord2f(widthRatio, heightRatio);

			glVertex2f(fx2, y);
			glColor4f(colors[LTexture.TOP_RIGHT]);
			glTexCoord2f(widthRatio, yOff);

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

	public void commit(float x, float y, float sx, float sy, float ax,
			float ay, float rotaion) {
		if (isClosed) {
			return;
		}
		GLEx.self.save();
		Matrix4 project = GLEx.getProjectionMatrix();
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		project.scale(sx, sy, 0);
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				project.translate(ax, ay, 0.0f);
				project.rotate(0f, 0f, 1f, rotaion);
				project.translate(-ax, -ay, 0.0f);
			} else {
				project.translate(texture.width / 2, texture.height / 2, 0.0f);
				project.rotate(0f, 0f, 0f, rotaion);
				project.translate(-texture.width / 2, -texture.height / 2, 0.0f);
			}
		}
		if (drawing) {
			setupMatrices();
		}
		end();
		GLEx.self.restore();
	}

	public void postCache(Cache cache, LColor color, float x, float y) {
		if (isClosed) {
			return;
		}
		GLEx.self.save();
		Matrix4 project = GLEx.getProjectionMatrix();
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		commit(cache, color, lastBlendState);
		GLEx.self.restore();
	}

	public void postCache(Cache cache, LColor color, float x, float y,
			float sx, float sy, float ax, float ay, float rotaion) {
		if (isClosed) {
			return;
		}
		GLEx.self.save();
		Matrix4 project = GLEx.getProjectionMatrix();
		if (x != 0 || y != 0) {
			project.translate(x, y, 0);
		}
		project.scale(sx, sy, 0);
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				project.translate(ax, ay, 0.0f);
				project.rotate(0f, 0f, 1f, rotaion);
				project.translate(-ax, -ay, 0.0f);
			} else {
				project.translate(texture.width / 2, texture.height / 2, 0.0f);
				project.rotate(0f, 0f, 0f, rotaion);
				project.translate(-texture.width / 2, -texture.height / 2, 0.0f);
			}
		}
		commit(cache, color, lastBlendState);
		GLEx.self.restore();
	}

	public void postCache(Cache cache, LColor color, float rotaion) {
		if (isClosed) {
			return;
		}
		GLEx.self.save();
		Matrix4 project = GLEx.getProjectionMatrix();
		if (rotaion != 0) {
			project.translate(texture.width / 2, texture.height / 2, 0.0f);
			project.rotate(0f, 0f, 1f, rotaion);
			project.translate(-texture.width / 2, -texture.height / 2, 0.0f);
		}
		commit(cache, color, lastBlendState);
		GLEx.self.restore();
	}

	public void postCache(LColor color, float rotaion) {
		if (lastCache != null) {
			postCache(lastCache, color, rotaion);
		}
	}

	public void dispose() {
		isClosed = true;
		isLoaded = false;
		if (shader != null) {
			shader.dispose();
		}
		if (globalShader != null) {
			globalShader.dispose();
		}
		if (customShader != null) {
			customShader.dispose();
		}
		if (lastCache != null) {
			lastCache.dispose();
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
}
