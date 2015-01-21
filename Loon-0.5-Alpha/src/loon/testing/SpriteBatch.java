package loon.testing;

import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GL20;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;
import loon.core.graphics.opengl.Mesh;
import loon.core.graphics.opengl.ShaderProgram;
import loon.core.graphics.opengl.VertexAttribute;
import loon.core.graphics.opengl.Mesh.VertexDataType;
import loon.core.graphics.opengl.VertexAttributes.Usage;
import loon.core.graphics.opengl.math.Transform4;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class SpriteBatch {
	private Mesh mesh;

	float[] vertices;
	int idx = 0;
	LTexture lastTexture = null;
	float invTexWidth = 0, invTexHeight = 0;

	boolean drawing = false;

	private final Transform4 transformMatrix = new Transform4();
	private final Transform4 projectionMatrix = new Transform4();
	private final Transform4 combinedMatrix = new Transform4();

	private boolean blendingDisabled = false;
	private int blendSrcFunc = GL20.GL_SRC_ALPHA;
	private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;

	private ShaderProgram shader;
	private ShaderProgram customShader = null;
	private boolean ownsShader;

	float color = LColor.white.toFloatBits();
	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int renderCalls = 0;

	public int totalRenderCalls = 0;

	public int maxSpritesInBatch = 0;

	private boolean isLoaded;

	public SpriteBatch() {
		this(1000, null);
	}

	public SpriteBatch(int size) {
		this(size, null);
	}

	public SpriteBatch(final int size, final ShaderProgram defaultShader) {
		if (size > 5460) {
			throw new IllegalArgumentException(
					"Can't have more than 5460 sprites per batch: " + size);
		}
		Updateable update = new Updateable() {

			
			public void action(Object a) {

				mesh = new Mesh(VertexDataType.VertexArray, false, size * 4,
						size * 6, new VertexAttribute(Usage.Position, 2,
								ShaderProgram.POSITION_ATTRIBUTE),
						new VertexAttribute(Usage.ColorPacked, 4,
								ShaderProgram.COLOR_ATTRIBUTE),
						new VertexAttribute(Usage.TextureCoordinates, 2,
								ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

				projectionMatrix.setToOrtho2D(0, 0,
						LSystem.screenRect.getWidth(),
						LSystem.screenRect.getHeight());

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

				if (defaultShader == null) {
					shader = createDefaultShader();
					ownsShader = true;
				} else {
					shader = defaultShader;
				}
				isLoaded = true;
			}
		};
		LSystem.load(update);

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

	
	public void begin() {
		if (!isLoaded) {
			return;
		}
		if (drawing) {
			throw new IllegalStateException(
					"SpriteBatch.end must be called before begin.");
		}
		renderCalls = 0;
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
		if (idx > 0) {
			flush();
		}
		lastTexture = null;
		drawing = false;
		GL20 gl = GLEx.gl;
		gl.glDepthMask(true);
		if (isBlendingEnabled()) {
			gl.glDisable(GL20.GL_BLEND);
		}
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

	
	public void draw(LTexture texture, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, int srcX, int srcY, int srcWidth,
			int srcHeight, boolean flipX, boolean flipY) {
		if (!isLoaded) {
			return;
		}
		if (!drawing)
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		// bottom left and top right corner points relative to origin
		final float worldOriginX = x + originX;
		final float worldOriginY = y + originY;
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		// scale
		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		// construct corner points, start from top left and go counter clockwise
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

		// rotate
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

		float color = this.color;
		int idx = this.idx;
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
		this.idx = idx;
	}

	
	public void draw(LTexture texture, float x, float y, float width,
			float height, int srcX, int srcY, int srcWidth, int srcHeight,
			boolean flipX, boolean flipY) {
		if (!drawing)
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

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

		float color = this.color;
		int idx = this.idx;
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
		this.idx = idx;
	}

	
	public void draw(LTexture texture, float x, float y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		if (!drawing)
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		final float u = srcX * invTexWidth;
		final float v = (srcY + srcHeight) * invTexHeight;
		final float u2 = (srcX + srcWidth) * invTexWidth;
		final float v2 = srcY * invTexHeight;
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

		float color = this.color;
		int idx = this.idx;
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
		this.idx = idx;
	}

	
	public void draw(LTexture texture, float x, float y, float width,
			float height, float u, float v, float u2, float v2) {
		if (!drawing)
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before draw.");

		float[] vertices = this.vertices;

		if (texture != lastTexture)
			switchTexture(texture);
		else if (idx == vertices.length) //
			flush();

		final float fx2 = x + width;
		final float fy2 = y + height;

		float color = this.color;
		int idx = this.idx;
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
		this.idx = idx;
	}

	
	public void draw(LTexture texture, float x, float y) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight());
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
		checkDrawing();
		LTexture tex2d = texture.getParent();
		if (tex2d != null) {
			if (tex2d != lastTexture) {
				flush();
				lastTexture = tex2d;
			} else if (idx == vertices.length) {
				flush();
			}
			invTexWidth = (1f / texture.getWidth()) * texture.widthRatio;
			invTexHeight = (1f / texture.getHeight()) * texture.heightRatio;
		} else if (texture != lastTexture) {
			flush();
			lastTexture = texture;
			invTexWidth = (1f / texture.getWidth()) * texture.widthRatio;
			invTexHeight = (1f / texture.getHeight()) * texture.heightRatio;
		} else if (idx == vertices.length) {
			flush();
		}
		return true;
	}

	
	public void draw(LTexture texture, float x, float y, float width,
			float height) {
		if (checkTexture(texture)) {
			float[] vertices = this.vertices;
			float color = this.color;
			int idx = this.idx;

			final float fx2 = x + width;
			final float fy2 = y + height;
			final float u = texture.xOff;
			final float v = texture.yOff;
			final float u2 = texture.widthRatio;
			final float v2 = texture.heightRatio;

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

			this.idx = idx;
		}
	}

	
	public void draw(LTexture texture, float[] spriteVertices, int offset,
			int count) {
		if (!drawing)
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before draw.");

		int verticesLength = vertices.length;
		int remainingVertices = verticesLength;
		if (texture != lastTexture)
			switchTexture(texture);
		else {
			remainingVertices -= idx;
			if (remainingVertices == 0) {
				flush();
				remainingVertices = verticesLength;
			}
		}
		int copyCount = Math.min(remainingVertices, count);

		System.arraycopy(spriteVertices, offset, vertices, idx, copyCount);
		idx += copyCount;
		count -= copyCount;
		while (count > 0) {
			offset += copyCount;
			flush();
			copyCount = Math.min(verticesLength, count);
			System.arraycopy(spriteVertices, offset, vertices, 0, copyCount);
			idx += copyCount;
			count -= copyCount;
		}
	}

	
	public void draw(LTextureRegion region, float x, float y) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}

	
	public void draw(LTextureRegion region, float x, float y, float width,
			float height) {
	}

	
	public void draw(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation) {

	}

	
	public void draw(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, boolean clockwise) {

	}

	
	public void flush() {
		if (idx == 0) {
			return;
		}
		renderCalls++;
		totalRenderCalls++;
		int spritesInBatch = idx / 20;
		if (spritesInBatch > maxSpritesInBatch) {
			maxSpritesInBatch = spritesInBatch;
		}
		int count = spritesInBatch * 6;

		if (!lastTexture.isLoaded()) {
			lastTexture.loadTexture();
		}
		Mesh mesh = this.mesh;
		mesh.setVertices(vertices, 0, idx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);

		if (blendingDisabled) {
			GLEx.gl.glDisable(GL20.GL_BLEND);
		} else {
			GLEx.gl.glEnable(GL20.GL_BLEND);
			if (blendSrcFunc != -1)
				GLEx.gl.glBlendFunc(blendSrcFunc, blendDstFunc);
		}

		mesh.render(customShader != null ? customShader : shader,
				GL20.GL_TRIANGLES, 0, count);

		idx = 0;
	}

	
	public void disableBlending() {
		if (blendingDisabled)
			return;
		flush();
		blendingDisabled = true;
	}

	
	public void enableBlending() {
		if (!blendingDisabled)
			return;
		flush();
		blendingDisabled = false;
	}

	
	public void setBlendFunction(int srcFunc, int dstFunc) {
		if (blendSrcFunc == srcFunc && blendDstFunc == dstFunc)
			return;
		flush();
		blendSrcFunc = srcFunc;
		blendDstFunc = dstFunc;
	}

	
	public int getBlendSrcFunc() {
		return blendSrcFunc;
	}

	
	public int getBlendDstFunc() {
		return blendDstFunc;
	}

	
	public void dispose() {
		mesh.dispose();
		if (ownsShader && shader != null)
			shader.dispose();
	}

	
	public Transform4 getProjectionMatrix() {
		return projectionMatrix;
	}

	
	public Transform4 getTransformMatrix() {
		return transformMatrix;
	}

	
	public void setProjectionMatrix(Transform4 projection) {
		if (drawing)
			flush();
		projectionMatrix.set(projection);
		if (drawing)
			setupMatrices();
	}

	
	public void setTransformMatrix(Transform4 transform) {
		if (drawing)
			flush();
		transformMatrix.set(transform);
		if (drawing)
			setupMatrices();
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
		flush();
		lastTexture = texture;
		invTexWidth = 1.0f / texture.getWidth();
		invTexHeight = 1.0f / texture.getHeight();
	}

	
	public void setShader(ShaderProgram shader) {

		if (drawing) {
			flush();
			if (customShader != null)
				customShader.end();
			else
				this.shader.end();
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

	public boolean isBlendingEnabled() {
		return !blendingDisabled;
	}

	public boolean isDrawing() {
		return drawing;
	}
}
