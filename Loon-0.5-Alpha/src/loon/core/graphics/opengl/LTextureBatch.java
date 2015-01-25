package loon.core.graphics.opengl;

import java.util.HashMap;

import loon.action.sprite.SpriteRegion;
import loon.action.sprite.SpriteBatch.BlendState;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.Mesh.VertexDataType;
import loon.core.graphics.opengl.VertexAttributes.Usage;
import loon.core.graphics.opengl.math.Transform4;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class LTextureBatch {

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

	private Mesh mesh;

	int count = 0;

	float[] vertices;

	float invTexWidth = 0, invTexHeight = 0;

	boolean drawing = false;

	private LTexture lastTexture = null;

	private final Transform4 combinedMatrix = new Transform4();

	private ShaderProgram shader;
	private ShaderProgram customShader = null;
	private boolean ownsShader;

	float color = LColor.white.toFloatBits();
	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int maxSpritesInBatch = 0;

	private boolean isLoaded;

	LTexture texture;

	private int vertexIdx;

	private int texWidth, texHeight;

	private int size = 0;
	
	public static enum BlendState {
		Additive, AlphaBlend, NonPremultiplied, Opaque;
	}

	private BlendState lastBlendState = BlendState.NonPremultiplied;
	
	public LTextureBatch(LTexture tex) {
		this(tex, 1000, null);
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

	private final static Transform4 cacheProjectionMatrix = new Transform4();

	public LTextureBatch(LTexture tex, final int size,
			final ShaderProgram defaultShader) {
		if (size > 5460) {
			throw new IllegalArgumentException(
					"Can't have more than 5460 sprites per batch: " + size);
		}
		this.setTexture(tex);
		cacheProjectionMatrix.setToOrtho2D(0, 0, GLEx.width(), GLEx.height());
		this.shader = defaultShader;
		this.size = size;
	}

	static public ShaderProgram createDefaultShader() {
		String vertexShader = "attribute vec4 "
				+ ShaderProgram.POSITION_ATTRIBUTE
				+ ";\n" //
				+ "attribute vec4 "
				+ ShaderProgram.COLOR_ATTRIBUTE
				+ ";\n" //
				+ "attribute vec2 "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = "
				+ ShaderProgram.COLOR_ATTRIBUTE
				+ ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "   gl_Position =  u_projTrans * "
				+ ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false)
			throw new IllegalArgumentException("Error compiling shader: "
					+ shader.getLog());
		return shader;
	}

	public BlendState getBlendState() {
		return lastBlendState;
	}

	public void setBlendState(BlendState state) {
		if (state != lastBlendState) {
			this.lastBlendState = state;
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
		} 
	}
	
	public void begin() {
		if (!isLoaded) {
			mesh = new Mesh(VertexDataType.VertexArray, false, size * 4,
					size * 6, new VertexAttribute(Usage.Position, 2,
							ShaderProgram.POSITION_ATTRIBUTE),
					new VertexAttribute(Usage.ColorPacked, 4,
							ShaderProgram.COLOR_ATTRIBUTE),
					new VertexAttribute(Usage.TextureCoordinates, 2,
							ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

			projectionMatrix.setToOrtho2D(0, 0, GLEx.width(), GLEx.height());

			vertices = new float[size * SpriteRegion.SPRITE_SIZE];
			int len = size * 6;
			short[] indices = new short[len];
			short j = 0;
			for (int i = 0; i < len; i += 6, j += 4) {
				indices[i] = j;
				indices[i + 1] = (short) (j + 1);
				indices[i + 2] = (short) (j + 2);
				indices[i + 3] = (short) (j + 2);
				indices[i + 4] = (short) (j + 3);
				indices[i + 5] = j;
			}
			mesh.setIndices(indices);
			if (shader == null) {
				shader = createDefaultShader();
				ownsShader = true;
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
			submit();
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

	public float getPackedColor() {
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
		if (texture == null) {
			return false;
		}
		checkDrawing();
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
		if (!lastTexture.isLoaded()) {
			lastTexture.loadTexture();
		}
		Mesh mesh = this.mesh;
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);
		setBlendState(state);
		mesh.render(customShader != null ? customShader : shader,
				GL20.GL_TRIANGLES, 0, count);

	}

	private final Transform4 transformMatrix = new Transform4();
	private final Transform4 projectionMatrix = new Transform4();

	Transform4 getProjectionMatrix() {
		return projectionMatrix;
	}

	Transform4 getTransformMatrix() {
		return transformMatrix;
	}

	void setProjectionMatrix(Transform4 projection) {
		if (drawing) {
			submit();
		}
		projectionMatrix.set(projection);
		if (drawing) {
			setupMatrices();
		}
	}

	void setTransformMatrix(Transform4 transform) {
		if (drawing) {
			submit();
		}
		transformMatrix.set(transform);
		if (drawing) {
			setupMatrices();
		}
	}

	private void setupMatrices() {
		combinedMatrix.set(projectionMatrix).mul(transformMatrix);
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

	private void commit(Cache cache,BlendState state) {
		if (!isLoaded) {
			return;
		}
		if (customShader != null) {
			customShader.begin();
		} else {
			shader.begin();
		}
		setupMatrices();
		if (cache.vertexIdx > 0) {
			if (cache.vertexIdx == 0) {
				return;
			}
			if (!lastTexture.isLoaded()) {
				lastTexture.loadTexture();
			}
			Mesh mesh = this.mesh;
			mesh.setVertices(cache.vertices, 0, cache.vertexIdx);
			mesh.getIndicesBuffer().position(0);
			mesh.getIndicesBuffer().limit(cache.count);
			setBlendState(state);
			mesh.render(customShader != null ? customShader : shader,
					GL20.GL_TRIANGLES, 0, cache.count);
		}
		if (customShader != null) {
			customShader.end();
		} else {
			shader.end();
		}
	}

	public void postLastCache() {
		if (lastCache != null) {
			commit(lastCache,lastBlendState);
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

	public void draw(float x, float y, float rotation) {
		draw(x, y, texWidth / 2, texHeight / 2, texWidth, texHeight, 1f, 1f,
				rotation, 0, 0, texWidth, texHeight, false, false);
	}

	public void draw(float x, float y, float width, float height, float rotation) {
		if (rotation == 0 && texWidth == width && texHeight == height) {
			draw(x, y, width, height);
		} else {
			draw(x, y, width / 2, height / 2, width, height, 1f, 1f, rotation,
					0, 0, texWidth, texHeight, false, false);
		}
	}

	public void draw(float x, float y, float rotation, float srcX, float srcY,
			float srcWidth, float srcHeight) {
		draw(x, y, srcWidth / 2, srcHeight / 2, texWidth, texHeight, 1f, 1f,
				rotation, srcX, srcY, srcWidth, srcHeight, false, false);
	}

	public void draw(Vector2f pos, Vector2f origin, float width, float height,
			float scale, float rotation, RectBox src, boolean flipX,
			boolean flipY) {
		draw(pos.x, pos.y, origin.x, origin.y, width, height, scale, scale,
				rotation, src.x, src.y, src.width, src.height, flipX, flipY,
				false);
	}

	public void draw(Vector2f pos, Vector2f origin, float scale,
			float rotation, RectBox src, boolean flipX, boolean flipY) {
		draw(pos.x, pos.y, origin.x, origin.y, src.width, src.height, scale,
				scale, rotation, src.x, src.y, src.width, src.height, flipX,
				flipY, false);
	}

	public void draw(Vector2f pos, Vector2f origin, float scale, RectBox src,
			boolean flipX, boolean flipY) {
		draw(pos.x, pos.y, origin.x, origin.y, src.width, src.height, scale,
				scale, 0, src.x, src.y, src.width, src.height, flipX, flipY,
				false);
	}

	public void draw(Vector2f pos, Vector2f origin, RectBox src, boolean flipX,
			boolean flipY) {
		draw(pos.x, pos.y, origin.x, origin.y, src.width, src.height, 1f, 1f,
				0, src.x, src.y, src.width, src.height, flipX, flipY, false);
	}

	public void draw(Vector2f pos, RectBox src, boolean flipX, boolean flipY) {
		draw(pos.x, pos.y, src.width / 2, src.height / 2, src.width,
				src.height, 1f, 1f, 0, src.x, src.y, src.width, src.height,
				flipX, flipY, false);
	}

	public void draw(float x, float y, float originX, float originY,
			float width, float height, float scaleX, float scaleY,
			float rotation, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {
		draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation,
				srcX, srcY, srcWidth, srcHeight, flipX, flipY, false);
	}

	public void draw(float x, float y, float originX, float originY,
			float scaleX, float scaleY, float rotation, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY) {
		draw(x, y, originX, originY, srcWidth, srcHeight, scaleX, scaleY,
				rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, false);
	}

	public void draw(Vector2f position, RectBox src, LColor c, float rotation,
			Vector2f origin, Vector2f scale, SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		if (src != null) {
			draw(position.x, position.y, origin.x, origin.y, src.width,
					src.height, scale.x, scale.y, rotation, src.x, src.y,
					src.width, src.height, flipX, flipY, true);
		} else {
			draw(position.x, position.y, origin.x, origin.y, texWidth,
					texHeight, scale.x, scale.y, rotation, 0, 0, texWidth,
					texHeight, flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(Vector2f position, RectBox src, LColor c, float rotation,
			float sx, float sy, float scale, SpriteEffects effects) {

		if (src == null && rotation == 0 && scale == 1f && sx == 0 && sy == 0) {
			draw(position, c);
			return;
		}

		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		if (src != null) {
			draw(position.x, position.y, sx, sy, src.width, src.height, scale,
					scale, rotation, src.x, src.y, src.width, src.height,
					flipX, flipY, true);
		} else {
			draw(position.x, position.y, sx, sy, texWidth, texHeight, scale,
					scale, rotation, 0, 0, texWidth, texHeight, flipX, flipY,
					true);
		}
		setColor(old);
	}

	public void draw(Vector2f position, RectBox src, LColor c, float rotation,
			Vector2f origin, float scale, SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		if (src != null) {
			draw(position.x, position.y, origin.x, origin.y, src.width,
					src.height, scale, scale, rotation, src.x, src.y,
					src.width, src.height, flipX, flipY, true);
		} else {
			draw(position.x, position.y, origin.x, origin.y, texWidth,
					texHeight, scale, scale, rotation, 0, 0, texWidth,
					texHeight, flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(float px, float py, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor c, float rotation,
			float originX, float originY, float scale, SpriteEffects effects) {

		if (effects == SpriteEffects.None && rotation == 0f && originX == 0f
				&& originY == 0f && scale == 1f) {
			draw(px, py, srcX, srcY, srcWidth, srcHeight, c);
			return;
		}

		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		draw(px, py, originX, originY, srcWidth, srcHeight, scale, scale,
				rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(float px, float py, RectBox src, LColor c, float rotation,
			Vector2f origin, float scale, SpriteEffects effects) {
		draw(px, py, src, c, rotation, origin.x, origin.y, scale, effects);
	}

	public void draw(float px, float py, RectBox src, LColor c, float rotation,
			float ox, float oy, float scale, SpriteEffects effects) {
		draw(px, py, src, c, rotation, ox, oy, scale, scale, effects);
	}

	public void draw(float px, float py, RectBox src, LColor c, float rotation,
			float ox, float oy, float scaleX, float scaleY,
			SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		if (src != null) {
			draw(px, py, ox, oy, src.width, src.height, scaleX, scaleY,
					rotation, src.x, src.y, src.width, src.height, flipX,
					flipY, true);
		} else {
			draw(px, py, ox, oy, texWidth, texHeight, scaleX, scaleY, rotation,
					0, 0, texWidth, texHeight, flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(Vector2f position, LColor c, float rotation,
			Vector2f origin, Vector2f scale, SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}

		draw(position.x, position.y, origin.x, origin.y, texWidth, texHeight,
				scale.x, scale.y, rotation, 0, 0, texWidth, texHeight, flipX,
				flipY, true);

		setColor(old);
	}

	public void draw(Vector2f position, LColor c, float rotation,
			float originX, float originY, float scale, SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}

		draw(position.x, position.y, originX, originY, texWidth, texHeight,
				scale, scale, rotation, 0, 0, texWidth, texHeight, flipX,
				flipY, true);

		setColor(old);
	}

	public void draw(float posX, float posY, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor c, float rotation,
			float originX, float originY, float scaleX, float scaleY,
			SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		draw(posX, posY, originX, originY, srcWidth, srcHeight, scaleX, scaleY,
				rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(Vector2f position, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c, float rotation, Vector2f origin,
			Vector2f scale, SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:

			flipY = true;
			break;
		default:
			break;
		}
		draw(position.x, position.y, origin.x, origin.y, srcWidth, srcHeight,
				scale.x, scale.y, rotation, srcX, srcY, srcWidth, srcHeight,
				flipX, flipY, true);
		setColor(old);
	}

	public void draw(RectBox dst, RectBox src, LColor c, float rotation,
			Vector2f origin, SpriteEffects effects) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		if (src != null) {
			draw(dst.x, dst.y, origin.x, origin.y, dst.width, dst.height, 1f,
					1f, rotation, src.x, src.y, src.width, src.height, flipX,
					flipY, true);
		} else {
			draw(dst.x, dst.y, origin.x, origin.y, dst.width, dst.height, 1f,
					1f, rotation, 0, 0, texWidth, texHeight, flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(float dstX, float dstY, float dstWidth, float dstHeight,
			float srcX, float srcY, float srcWidth, float srcHeight, LColor c,
			float rotation, float originX, float originY, SpriteEffects effects) {
		if (effects == SpriteEffects.None && rotation == 0 && originX == 0
				&& originY == 0) {
			draw(dstX, dstY, dstWidth, dstHeight, srcX, srcY, srcWidth,
					srcHeight, c);
			return;
		}
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		boolean flipX = false;
		boolean flipY = false;
		switch (effects) {
		case FlipHorizontally:
			flipX = true;
			break;
		case FlipVertically:
			flipY = true;
			break;
		default:
			break;
		}
		draw(dstX, dstY, originX, originY, dstWidth, dstHeight, 1f, 1f,
				rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(float x, float y, float originX, float originY,
			float width, float height, float scaleX, float scaleY,
			float rotation, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY, boolean off) {

		if (!checkTexture(texture)) {
			return;
		}

		float worldOriginX = x + originX;
		float worldOriginY = y + originY;
		if (off) {
			worldOriginX = x;
			worldOriginY = y;
		}
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

		float u = srcX * invTexWidth + texture.xOff;
		float v = srcY * invTexHeight + texture.yOff;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = (srcY + srcHeight) * invTexHeight;

		if (flipX) {
			float tmp = u;
			u = u2;
			u2 = tmp;
		}

		if (flipY) {
			float tmp = v;
			v = v2;
			v2 = tmp;
		}

		int idx = this.vertexIdx;

		vertices[idx++] = x1;
		vertices[idx++] = y1;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v;

		vertices[idx++] = x2;
		vertices[idx++] = y2;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v2;

		vertices[idx++] = x3;
		vertices[idx++] = y3;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v2;

		vertices[idx++] = x4;
		vertices[idx++] = y4;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v;

		this.vertexIdx = idx;
	}

	public void draw(float x, float y, float width, float height,
			float rotation, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(x, y, width, height, rotation);
		setColor(old);
	}

	public void drawFlipX(float x, float y) {
		draw(x, y, texWidth, texHeight, 0, 0, texWidth, texHeight, true, false);
	}

	public void drawFlipY(float x, float y) {
		draw(x, y, texWidth, texHeight, 0, 0, texWidth, texHeight, false, true);
	}

	public void drawFlipX(float x, float y, float width, float height) {
		draw(x, y, width, height, 0, 0, texWidth, texHeight, true, false);
	}

	public void drawFlipY(float x, float y, float width, float height) {
		draw(x, y, width, height, 0, 0, texWidth, texHeight, false, true);
	}

	public void drawFlipX(float x, float y, float rotation) {
		draw(x, y, texWidth / 2, texHeight / 2, texWidth, texHeight, 1f, 1f,
				rotation, 0, 0, texWidth, texHeight, true, false);
	}

	public void drawFlipY(float x, float y, float rotation) {
		draw(x, y, texWidth / 2, texHeight / 2, texWidth, texHeight, 1f, 1f,
				rotation, 0, 0, texWidth, texHeight, false, true);
	}

	public void drawFlipX(float x, float y, float width, float height,
			float rotation) {
		draw(x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0,
				0, texWidth, texHeight, true, false);
	}

	public void drawFlipY(float x, float y, float width, float height,
			float rotation) {
		draw(x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0,
				0, texWidth, texHeight, false, true);
	}

	public void draw(RectBox dstBox, RectBox srcBox, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(dstBox.x, dstBox.y, dstBox.width, dstBox.height, srcBox.x,
				srcBox.y, srcBox.width, srcBox.height, false, false);
		setColor(old);
	}

	public void draw(float x, float y, float width, float height, float srcX,
			float srcY, float srcWidth, float srcHeight) {
		draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
	}

	public void draw(float x, float y, float width, float height, float srcX,
			float srcY, float srcWidth, float srcHeight, LColor c) {
		float old = color;
		if (c != null) {
			if (!c.equals(LColor.white)) {
				setColor(c);
			}
		}
		draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
		if (c != null) {
			setColor(old);
		}
	}

	public void drawEmbedded(float x, float y, float width, float height,
			float srcX, float srcY, float srcWidth, float srcHeight, LColor c) {
		draw(x, y, width - x, height - y, srcX, srcY, srcWidth - srcX,
				srcHeight - srcY, c);
	}

	public void draw(float x, float y, float width, float height, float srcX,
			float srcY, float srcWidth, float srcHeight, boolean flipX,
			boolean flipY) {

		if (!checkTexture(texture)) {
			return;
		}

		float u = srcX * invTexWidth + texture.xOff;
		float v = srcY * invTexHeight + texture.yOff;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = (srcY + srcHeight) * invTexHeight;
		final float fx2 = x + width;
		final float fy2 = y + height;

		if (flipX) {
			float tmp = u;
			u = u2;
			u2 = tmp;
		}

		if (flipY) {
			float tmp = v;
			v = v2;
			v2 = tmp;
		}

		int idx = this.vertexIdx;

		vertices[idx++] = x;
		vertices[idx++] = y;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v;

		vertices[idx++] = x;
		vertices[idx++] = fy2;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v2;

		vertices[idx++] = fx2;
		vertices[idx++] = fy2;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v2;

		vertices[idx++] = fx2;
		vertices[idx++] = y;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v;

		this.vertexIdx = idx;
	}

	public void draw(Vector2f pos, RectBox srcBox, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		if (srcBox == null) {
			draw(pos.x, pos.y, 0, 0, texWidth, texHeight);
		} else {
			draw(pos.x, pos.y, srcBox.x, srcBox.y, srcBox.width, srcBox.height);
		}
		setColor(old);
	}

	public void draw(float x, float y, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(x, y, srcX, srcY, srcWidth, srcHeight);
		setColor(old);
	}

	public void draw(float x, float y, float srcX, float srcY, float srcWidth,
			float srcHeight) {

		if (!checkTexture(texture)) {
			return;
		}

		float u = srcX * invTexWidth + texture.xOff;
		float v = srcY * invTexHeight + texture.yOff;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = (srcY + srcHeight) * invTexHeight;
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

		int idx = this.vertexIdx;

		vertices[idx++] = x;
		vertices[idx++] = y;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v;

		vertices[idx++] = x;
		vertices[idx++] = fy2;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v2;

		vertices[idx++] = fx2;
		vertices[idx++] = fy2;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v2;

		vertices[idx++] = fx2;
		vertices[idx++] = y;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v;

		this.vertexIdx = idx;
	}

	public void draw(float x, float y) {
		draw(x, y, texWidth, texHeight);
	}

	public void draw(float x, float y, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(x, y, texWidth, texHeight);
		setColor(old);
	}

	public void draw(RectBox rect, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(rect.x, rect.y, rect.width, rect.height);
		setColor(old);
	}

	public void draw(Vector2f pos, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(pos.x, pos.y, texWidth, texHeight);
		setColor(old);
	}

	public void draw(float x, float y, float width, float height) {

		if (!checkTexture(texture)) {
			return;
		}

		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = texture.xOff;
		final float v = texture.yOff;
		final float u2 = texture.widthRatio;
		final float v2 = texture.heightRatio;

		int idx = this.vertexIdx;

		vertices[idx++] = x;
		vertices[idx++] = y;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v;

		vertices[idx++] = x;
		vertices[idx++] = fy2;
		vertices[idx++] = color;
		vertices[idx++] = u;
		vertices[idx++] = v2;

		vertices[idx++] = fx2;
		vertices[idx++] = fy2;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v2;

		vertices[idx++] = fx2;
		vertices[idx++] = y;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v;

		this.vertexIdx = idx;
	}

	public void save() {
		cacheProjectionMatrix.set(projectionMatrix);
	}

	public void restore() {
		projectionMatrix.set(cacheProjectionMatrix);
	}

	public void commit(float x, float y, float sx, float sy, float ax, float ay,
			float rotaion) {
		save();
		if (x != 0 || y != 0) {
			projectionMatrix.translate(x, y, 0);
		}
		projectionMatrix.scale(sx, sy, 0);
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				projectionMatrix.translate(ax, ay, 0.0f);
				projectionMatrix.rotate(0f, 0f, 1f, rotaion);
				projectionMatrix.translate(-ax, -ay, 0.0f);
			} else {
				projectionMatrix.translate(texture.width / 2,
						texture.height / 2, 0.0f);
				projectionMatrix.rotate(0f, 0f, 0f, rotaion);
				projectionMatrix.translate(-texture.width / 2,
						-texture.height / 2, 0.0f);
			}
		}
		if (drawing) {
			setupMatrices();
		}
		end();
		restore();
	}

	public void postLastCache(Cache cache, float x, float y, float sx,
			float sy, float ax, float ay, float rotaion) {
		save();
		if (x != 0 || y != 0) {
			projectionMatrix.translate(x, y, 0);
		}
		projectionMatrix.scale(sx, sy, 0);
		if (rotaion != 0) {
			if (ax != 0 || ay != 0) {
				projectionMatrix.translate(ax, ay, 0.0f);
				projectionMatrix.rotate(0f, 0f, 1f, rotaion);
				projectionMatrix.translate(-ax, -ay, 0.0f);
			} else {
				projectionMatrix.translate(texture.width / 2,
						texture.height / 2, 0.0f);
				projectionMatrix.rotate(0f, 0f, 0f, rotaion);
				projectionMatrix.translate(-texture.width / 2,
						-texture.height / 2, 0.0f);
			}
		}
		commit(cache,lastBlendState);
		restore();
	}

	public void postLastCache(Cache cache, float rotaion) {
		save();
		if (rotaion != 0) {
			projectionMatrix.translate(texture.width / 2, texture.height / 2,
					0.0f);
			projectionMatrix.rotate(0f, 0f, 1f, rotaion);
			projectionMatrix.translate(-texture.width / 2, -texture.height / 2,
					0.0f);
		}
		commit(cache,lastBlendState);
		restore();
	}

	public void postLastCache(float rotaion) {
		if (lastCache != null) {
			postLastCache(lastCache, rotaion);
		}
	}

	public void dispose() {
		mesh.dispose();
		if (ownsShader && shader != null) {
			shader.dispose();
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
