package loon.action.sprite;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.Matrix4;
import loon.opengl.BlendState;
import loon.opengl.GL20;
import loon.opengl.LTextureRegion;
import loon.opengl.Mesh;
import loon.opengl.ShaderProgram;
import loon.opengl.VertexAttribute;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.GLUtils;
import loon.utils.IntArray;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class SpriteCache implements LRelease {
	
	static private class Cache {
		final int id;
		final int offset;
		int maxCount;
		int textureCount;
		LTexture[] textures;
		int[] counts;

		public Cache(int id, int offset) {
			this.id = id;
			this.offset = offset;
		}
	}
	
	static private final float[] tempVertices = new float[LSystem.VERTEX_SIZE * 6];

	private final Mesh mesh;
	private boolean drawing;
	private final Matrix4 transformMatrix = new Matrix4();
	private final Matrix4 projectionMatrix = new Matrix4();
	private ArrayList<Cache> caches = new ArrayList<Cache>();

	private final Matrix4 combinedMatrix = new Matrix4();
	private final ShaderProgram shader;

	private Cache currentCache;
	private final ArrayList<LTexture> textures = new ArrayList<LTexture>();
	private final IntArray counts = new IntArray(8);

	private float color = LColor.white.toFloatBits();
	private LColor tempColor = new LColor(1, 1, 1, 1);

	private ShaderProgram customShader = null;

	public int renderCalls = 0;

	public int totalRenderCalls = 0;

	private boolean isLoaded;
	
	private LTexture lastTexture = null;
	
	private float invTexWidth = 0, invTexHeight = 0;
	
	private BlendState lastBlendState = BlendState.NonPremultiplied;
	
	public SpriteCache() {
		this(1000, false);
	}

	public SpriteCache(int size, boolean useIndices) {
		this(size, LSystem.createDefaultShader(), useIndices);
	}

	public SpriteCache(int size, ShaderProgram shader, boolean useIndices) {
		this.shader = shader;
		if (useIndices && size > 5460) {
			throw new IllegalArgumentException(
					"Can't have more than 5460 sprites per batch: " + size);
		}
		mesh = new Mesh(true, size * (useIndices ? 4 : 6),
				useIndices ? size * 6 : 0, new VertexAttribute(Usage.Position,
						2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4,
						ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(
						Usage.TextureCoordinates, 2,
						ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
		mesh.setAutoBind(false);

		if (useIndices) {
			int length = size * 6;
			short[] indices = new short[length];
			short j = 0;
			for (int i = 0; i < length; i += 6, j += 4) {
				indices[i + 0] = (short) j;
				indices[i + 1] = (short) (j + 1);
				indices[i + 2] = (short) (j + 2);
				indices[i + 3] = (short) (j + 2);
				indices[i + 4] = (short) (j + 3);
				indices[i + 5] = (short) j;
			}
			mesh.setIndices(indices);
		}

		projectionMatrix.setToOrtho2D(0, 0, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
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
		LColor color = this.tempColor;
		color.r = (intBits & 0xff) / 255f;
		color.g = ((intBits >>> 8) & 0xff) / 255f;
		color.b = ((intBits >>> 16) & 0xff) / 255f;
		color.a = ((intBits >>> 24) & 0xff) / 255f;
		return color;
	}

	public void beginCache() {
		if (currentCache != null) {
			throw new IllegalStateException(
					"endCache must be called before begin.");
		}
		currentCache = new Cache(caches.size(), mesh.getVerticesBuffer()
				.limit());
		caches.add(currentCache);
		mesh.getVerticesBuffer().compact();
	}

	public void beginCache(int cacheID) {
		if (currentCache != null){
			throw new IllegalStateException(
					"endCache must be called before begin.");
		}
		if (cacheID == caches.size() - 1) {
			Cache oldCache = caches.remove(cacheID);
			mesh.getVerticesBuffer().limit(oldCache.offset);
			beginCache();
			return;
		}
		currentCache = caches.get(cacheID);
		mesh.getVerticesBuffer().position(currentCache.offset);
	}

	public int endCache() {
		if (currentCache == null){
			throw new IllegalStateException(
					"beginCache must be called before endCache.");
		}
		Cache cache = currentCache;
		int cacheCount = mesh.getVerticesBuffer().position() - cache.offset;
		if (cache.textures == null) {
			cache.maxCount = cacheCount;
			cache.textureCount = textures.size();
			cache.textures = textures.toArray(new LTexture[0]);
			cache.counts = new int[cache.textureCount];
			for (int i = 0, n = counts.length; i < n; i++){
				cache.counts[i] = counts.get(i);
			}
			mesh.getVerticesBuffer().flip();
		} else {
			if (cacheCount > cache.maxCount) {
				throw new RuntimeException(
						"If a cache is not the last created, it cannot be redefined with more entries than when it was first created: "
								+ cacheCount + " (" + cache.maxCount + " max)");
			}

			cache.textureCount = textures.size();

			if (cache.textures.length < cache.textureCount){
				cache.textures = new LTexture[cache.textureCount];
			}
			for (int i = 0, n = cache.textureCount; i < n; i++){
				cache.textures[i] = textures.get(i);
			}

			if (cache.counts.length < cache.textureCount){
				cache.counts = new int[cache.textureCount];
			}
			for (int i = 0, n = cache.textureCount; i < n; i++){
				cache.counts[i] = counts.get(i);
			}

			FloatBuffer vertices = mesh.getVerticesBuffer();
			vertices.position(0);
			Cache lastCache = caches.get(caches.size() - 1);
			vertices.limit(lastCache.offset + lastCache.maxCount);
		}

		currentCache = null;
		textures.clear();
		counts.clear();

		return cache.id;
	}

	public void clear() {
		caches.clear();
		mesh.getVerticesBuffer().clear().flip();
	}

	public void add(LTexture texture, float[] vertices, int offset, int length) {
		if (currentCache == null){
			throw new IllegalStateException(
					"beginCache must be called before add.");
		}
		checkTexture(texture);
		int verticesPerImage = mesh.getNumIndices() > 0 ? 4 : 6;
		int count = length / (verticesPerImage * LSystem.VERTEX_SIZE) * 6;
		int lastIndex = textures.size() - 1;
		if (lastIndex < 0 || textures.get(lastIndex) != texture) {
			textures.add(texture);
			counts.add(count);
		} else{
			counts.incr(lastIndex, count);
		}
		mesh.getVerticesBuffer().put(vertices, offset, length);
	}

	public void add(LTexture texture, float x, float y) {
		checkTexture(texture);
		final float fx2 = x + texture.getWidth();
		final float fy2 = y + texture.getHeight();

		tempVertices[0] = x;
		tempVertices[1] = y;
		tempVertices[2] = color;
		tempVertices[3] = 0;
		tempVertices[4] = 1;

		tempVertices[5] = x;
		tempVertices[6] = fy2;
		tempVertices[7] = color;
		tempVertices[8] = 0;
		tempVertices[9] = 0;

		tempVertices[10] = fx2;
		tempVertices[11] = fy2;
		tempVertices[12] = color;
		tempVertices[13] = 1;
		tempVertices[14] = 0;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = fx2;
			tempVertices[16] = y;
			tempVertices[17] = color;
			tempVertices[18] = 1;
			tempVertices[19] = 1;
			add(texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = fx2;
			tempVertices[16] = fy2;
			tempVertices[17] = color;
			tempVertices[18] = 1;
			tempVertices[19] = 0;

			tempVertices[20] = fx2;
			tempVertices[21] = y;
			tempVertices[22] = color;
			tempVertices[23] = 1;
			tempVertices[24] = 1;

			tempVertices[25] = x;
			tempVertices[26] = y;
			tempVertices[27] = color;
			tempVertices[28] = 0;
			tempVertices[29] = 1;
			add(texture, tempVertices, 0, 30);
		}
	}

	public void add(LTexture texture, float x, float y, int srcWidth,
			int srcHeight, float u, float v, float u2, float v2, float color) {
		checkTexture(texture);
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

		tempVertices[0] = x;
		tempVertices[1] = y;
		tempVertices[2] = color;
		tempVertices[3] = u;
		tempVertices[4] = v;

		tempVertices[5] = x;
		tempVertices[6] = fy2;
		tempVertices[7] = color;
		tempVertices[8] = u;
		tempVertices[9] = v2;

		tempVertices[10] = fx2;
		tempVertices[11] = fy2;
		tempVertices[12] = color;
		tempVertices[13] = u2;
		tempVertices[14] = v2;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = fx2;
			tempVertices[16] = y;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v;
			add(texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = fx2;
			tempVertices[16] = fy2;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v2;

			tempVertices[20] = fx2;
			tempVertices[21] = y;
			tempVertices[22] = color;
			tempVertices[23] = u2;
			tempVertices[24] = v;

			tempVertices[25] = x;
			tempVertices[26] = y;
			tempVertices[27] = color;
			tempVertices[28] = u;
			tempVertices[29] = v;
			add(texture, tempVertices, 0, 30);
		}
	}

	public void add(LTexture texture, float x, float y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		checkTexture(texture);
	
		final float u = srcX * invTexWidth;
		final float v = (srcY + srcHeight) * invTexHeight;
		final float u2 = (srcX + srcWidth) * invTexWidth;
		final float v2 = srcY * invTexHeight;
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

		tempVertices[0] = x;
		tempVertices[1] = y;
		tempVertices[2] = color;
		tempVertices[3] = u;
		tempVertices[4] = v;

		tempVertices[5] = x;
		tempVertices[6] = fy2;
		tempVertices[7] = color;
		tempVertices[8] = u;
		tempVertices[9] = v2;

		tempVertices[10] = fx2;
		tempVertices[11] = fy2;
		tempVertices[12] = color;
		tempVertices[13] = u2;
		tempVertices[14] = v2;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = fx2;
			tempVertices[16] = y;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v;
			add(texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = fx2;
			tempVertices[16] = fy2;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v2;

			tempVertices[20] = fx2;
			tempVertices[21] = y;
			tempVertices[22] = color;
			tempVertices[23] = u2;
			tempVertices[24] = v;

			tempVertices[25] = x;
			tempVertices[26] = y;
			tempVertices[27] = color;
			tempVertices[28] = u;
			tempVertices[29] = v;
			add(texture, tempVertices, 0, 30);
		}
	}

	public void add(LTexture texture, float x, float y, float width,
			float height, int srcX, int srcY, int srcWidth, int srcHeight,
			boolean flipX, boolean flipY) {
		checkTexture(texture);

		float u = srcX * invTexWidth;
		float v = (srcY + srcHeight) * invTexHeight;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = srcY * invTexHeight;
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

		tempVertices[0] = x;
		tempVertices[1] = y;
		tempVertices[2] = color;
		tempVertices[3] = u;
		tempVertices[4] = v;

		tempVertices[5] = x;
		tempVertices[6] = fy2;
		tempVertices[7] = color;
		tempVertices[8] = u;
		tempVertices[9] = v2;

		tempVertices[10] = fx2;
		tempVertices[11] = fy2;
		tempVertices[12] = color;
		tempVertices[13] = u2;
		tempVertices[14] = v2;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = fx2;
			tempVertices[16] = y;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v;
			add(texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = fx2;
			tempVertices[16] = fy2;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v2;

			tempVertices[20] = fx2;
			tempVertices[21] = y;
			tempVertices[22] = color;
			tempVertices[23] = u2;
			tempVertices[24] = v;

			tempVertices[25] = x;
			tempVertices[26] = y;
			tempVertices[27] = color;
			tempVertices[28] = u;
			tempVertices[29] = v;
			add(texture, tempVertices, 0, 30);
		}
	}

	public void add(LTexture texture, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, int srcX, int srcY, int srcWidth,
			int srcHeight, boolean flipX, boolean flipY) {
		checkTexture(texture);
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

		float u = srcX * invTexWidth;
		float v = (srcY + srcHeight) * invTexHeight;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = srcY * invTexHeight;

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

		tempVertices[0] = x1;
		tempVertices[1] = y1;
		tempVertices[2] = color;
		tempVertices[3] = u;
		tempVertices[4] = v;

		tempVertices[5] = x2;
		tempVertices[6] = y2;
		tempVertices[7] = color;
		tempVertices[8] = u;
		tempVertices[9] = v2;

		tempVertices[10] = x3;
		tempVertices[11] = y3;
		tempVertices[12] = color;
		tempVertices[13] = u2;
		tempVertices[14] = v2;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = x4;
			tempVertices[16] = y4;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v;
			add(texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = x3;
			tempVertices[16] = y3;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v2;

			tempVertices[20] = x4;
			tempVertices[21] = y4;
			tempVertices[22] = color;
			tempVertices[23] = u2;
			tempVertices[24] = v;

			tempVertices[25] = x1;
			tempVertices[26] = y1;
			tempVertices[27] = color;
			tempVertices[28] = u;
			tempVertices[29] = v;
			add(texture, tempVertices, 0, 30);
		}
	}

	public void add(LTextureRegion region, float x, float y) {
		add(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}

	public void add(LTextureRegion region, float x, float y, float width,
			float height) {
		checkTexture(region.texture);
		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = region.xOff;
		final float v = region.yOff;
		final float u2 = region.widthRatio;
		final float v2 = region.heightRatio;

		tempVertices[0] = x;
		tempVertices[1] = y;
		tempVertices[2] = color;
		tempVertices[3] = u;
		tempVertices[4] = v;

		tempVertices[5] = x;
		tempVertices[6] = fy2;
		tempVertices[7] = color;
		tempVertices[8] = u;
		tempVertices[9] = v2;

		tempVertices[10] = fx2;
		tempVertices[11] = fy2;
		tempVertices[12] = color;
		tempVertices[13] = u2;
		tempVertices[14] = v2;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = fx2;
			tempVertices[16] = y;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v;
			add(region.texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = fx2;
			tempVertices[16] = fy2;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v2;

			tempVertices[20] = fx2;
			tempVertices[21] = y;
			tempVertices[22] = color;
			tempVertices[23] = u2;
			tempVertices[24] = v;

			tempVertices[25] = x;
			tempVertices[26] = y;
			tempVertices[27] = color;
			tempVertices[28] = u;
			tempVertices[29] = v;
			add(region.texture, tempVertices, 0, 30);
		}
	}

	public void add(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation) {
		checkTexture(region.texture);
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

		final float u = region.xOff;
		final float v = region.yOff;
		final float u2 = region.widthRatio;
		final float v2 = region.heightRatio;

		tempVertices[0] = x1;
		tempVertices[1] = y1;
		tempVertices[2] = color;
		tempVertices[3] = u;
		tempVertices[4] = v;

		tempVertices[5] = x2;
		tempVertices[6] = y2;
		tempVertices[7] = color;
		tempVertices[8] = u;
		tempVertices[9] = v2;

		tempVertices[10] = x3;
		tempVertices[11] = y3;
		tempVertices[12] = color;
		tempVertices[13] = u2;
		tempVertices[14] = v2;

		if (mesh.getNumIndices() > 0) {
			tempVertices[15] = x4;
			tempVertices[16] = y4;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v;
			add(region.texture, tempVertices, 0, 20);
		} else {
			tempVertices[15] = x3;
			tempVertices[16] = y3;
			tempVertices[17] = color;
			tempVertices[18] = u2;
			tempVertices[19] = v2;

			tempVertices[20] = x4;
			tempVertices[21] = y4;
			tempVertices[22] = color;
			tempVertices[23] = u2;
			tempVertices[24] = v;

			tempVertices[25] = x1;
			tempVertices[26] = y1;
			tempVertices[27] = color;
			tempVertices[28] = u;
			tempVertices[29] = v;
			add(region.texture, tempVertices, 0, 30);
		}
	}

	public void add(SpriteRegion sprite) {
		if (mesh.getNumIndices() > 0) {
			add(sprite.getTexture(), sprite.getVertices(), 0,
					LSystem.SPRITE_SIZE);
			return;
		}

		float[] spriteVertices = sprite.getVertices();
		System.arraycopy(spriteVertices, 0, tempVertices, 0,
				3 * LSystem.VERTEX_SIZE);
		System.arraycopy(spriteVertices, 2 * LSystem.VERTEX_SIZE, tempVertices,
				3 * LSystem.VERTEX_SIZE, LSystem.VERTEX_SIZE);
		System.arraycopy(spriteVertices, 3 * LSystem.VERTEX_SIZE, tempVertices,
				4 * LSystem.VERTEX_SIZE, LSystem.VERTEX_SIZE); 
		System.arraycopy(spriteVertices, 0, tempVertices,
				5 * LSystem.VERTEX_SIZE, LSystem.VERTEX_SIZE); 
		add(sprite.getTexture(), tempVertices, 0, 30);
	}

	public void begin() {
		if (drawing){
			throw new IllegalStateException("end must be called before begin.");
		}
		renderCalls = 0;
		combinedMatrix.set(projectionMatrix).mul(transformMatrix);

		LSystem.base().graphics().gl.glDepthMask(false);

		if (customShader != null) {
			customShader.begin();
			customShader.setUniformMatrix("u_proj", projectionMatrix);
			customShader.setUniformMatrix("u_trans", transformMatrix);
			customShader.setUniformMatrix("u_projTrans", combinedMatrix);
			customShader.setUniformi("u_texture", 0);
			mesh.bind(customShader);
		} else {
			shader.begin();
			shader.setUniformMatrix("u_projectionViewMatrix", combinedMatrix);
			shader.setUniformi("u_texture", 0);
			mesh.bind(shader);
		}
		drawing = true;
	}

	private void checkDrawing() {
		if (!drawing) {
			throw new IllegalStateException("Not implemented begin !");
		}
	}

	private boolean checkTexture(final LTexture texture) {
		if (!isLoaded) {
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
				end();
				lastTexture = tex2d;
			} 
			if (texture.isScale()) {
				invTexWidth = (1f / texture.width());
				invTexHeight = (1f / texture.height());
			} else {
				invTexWidth = (1f / texture.width()) * texture.widthRatio;
				invTexHeight = (1f / texture.height()) * texture.heightRatio;
			}
		} else if (texture != lastTexture) {
			end();
			lastTexture = texture;
			if (texture.isScale()) {
				invTexWidth = (1f / texture.width());
				invTexHeight = (1f / texture.height());
			} else {
				invTexWidth = (1f / texture.width()) * texture.widthRatio;
				invTexHeight = (1f / texture.height()) * texture.heightRatio;
			}
		} 
		return true;
	}
	
	public void end() {
		if (!drawing) {
			throw new IllegalStateException("begin must be called before end.");
		}
		drawing = false;
		GL20 gl = LSystem.base().graphics().gl;
		GLUtils.bindTexture(gl, lastTexture);
		int old = GLUtils.getBlendMode();
		switch (lastBlendState) {
		case Additive:
			GLUtils.setBlendMode(gl, LSystem.MODE_ALPHA_ONE);
			break;
		case AlphaBlend:
			GLUtils.setBlendMode(gl, LSystem.MODE_NORMAL);
			break;
		case Opaque:
			GLUtils.setBlendMode(gl, LSystem.MODE_NONE);
			break;
		case NonPremultiplied:
			GLUtils.setBlendMode(gl, LSystem.MODE_SPEED);
			break;
		}
		shader.end();
		LSystem.base().graphics().gl.glDepthMask(true);
		if (customShader != null){
			mesh.unbind(customShader);
		}
		else{
			mesh.unbind(shader);
		}
		GLUtils.setBlendMode(gl, old);
		LSystem.mainBeginDraw();
	}

	public void draw(int cacheID) {
		if (!drawing){
			throw new IllegalStateException(
					"SpriteCache.begin must be called before draw.");
		}

		Cache cache = caches.get(cacheID);
		int verticesPerImage = mesh.getNumIndices() > 0 ? 4 : 6;
		int offset = cache.offset / (verticesPerImage * LSystem.VERTEX_SIZE)
				* 6;
		LTexture[] textures = cache.textures;
		int[] counts = cache.counts;
		int textureCount = cache.textureCount;
		for (int i = 0; i < textureCount; i++) {
			int count = counts[i];
			textures[i].loadTexture();
			if (customShader != null){
				mesh.render(customShader, GL20.GL_TRIANGLES, offset, count);
			}
			else{
				mesh.render(shader, GL20.GL_TRIANGLES, offset, count);
			}
			offset += count;
		}
		renderCalls += textureCount;
		totalRenderCalls += textureCount;
	}

	public void draw(int cacheID, int offset, int length) {
		if (!drawing){
			throw new IllegalStateException(
					"SpriteCache.begin must be called before draw.");
		}

		Cache cache = caches.get(cacheID);
		offset = offset * 6 + cache.offset;
		length *= 6;
		LTexture[] textures = cache.textures;
		int[] counts = cache.counts;
		int textureCount = cache.textureCount;
		for (int i = 0; i < textureCount; i++) {
			textures[i].loadTexture();
			int count = counts[i];
			if (count > length) {
				i = textureCount;
				count = length;
			} else
				length -= count;
			if (customShader != null){
				mesh.render(customShader, GL20.GL_TRIANGLES, offset, count);
			}
			else{
				mesh.render(shader, GL20.GL_TRIANGLES, offset, count);
			}
			offset += count;
		}
		renderCalls += cache.textureCount;
		totalRenderCalls += textureCount;
	}

	public void close() {
		mesh.close();
		if (shader != null){
			shader.close();
		}
	}

	public Matrix4 getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4 projection) {
		if (drawing){
			throw new IllegalStateException(
					"Can't set the matrix within begin/end.");
		}
		projectionMatrix.set(projection);
	}

	public Matrix4 getTransformMatrix() {
		return transformMatrix;
	}

	public void setTransformMatrix(Matrix4 transform) {
		if (drawing)
			throw new IllegalStateException(
					"Can't set the matrix within begin/end.");
		transformMatrix.set(transform);
	}



	public void setShader(ShaderProgram shader) {
		customShader = shader;
	}
}
