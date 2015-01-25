package loon.testing;

import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GL;
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


	private ShaderProgram shader;
	private ShaderProgram customShader = null;
	private boolean ownsShader;

	float color = LColor.white.toFloatBits();
	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int renderCalls = 0;

	public int totalRenderCalls = 0;

	public int maxSpritesInBatch = 0;

	private boolean isLoaded;
	
	public static enum BlendState {
		Additive, AlphaBlend, NonPremultiplied, Opaque;
	}

	private BlendState lastBlendState = BlendState.NonPremultiplied;

	
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
					GLEx.width(),
						GLEx.height());

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
	
	public void end() {
		if (!isLoaded) {
			return;
		}
		if (!drawing) {
			throw new IllegalStateException(
					"SpriteBatch.begin must be called before end.");
		}
		if (idx > 0) {
			submit();
		}
		lastTexture = null;
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
		if (!isLoaded) {
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
			} else if (idx == vertices.length) {
				submit();
			}
			invTexWidth = (1f / texture.getWidth()) * texture.widthRatio;
			invTexHeight = (1f / texture.getHeight()) * texture.heightRatio;
		} else if (texture != lastTexture) {
			submit();
			lastTexture = texture;
			invTexWidth = (1f / texture.getWidth()) * texture.widthRatio;
			invTexHeight = (1f / texture.getHeight()) * texture.heightRatio;
		} else if (idx == vertices.length) {
			submit();
		}
		return true;
	}

	public void submit() {
		submit(lastBlendState);
	}
	
	public void submit(BlendState state) {
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
		setBlendState(state);
		mesh.render(customShader != null ? customShader : shader,
				GL20.GL_TRIANGLES, 0, count);

		idx = 0;
	}

	public void dispose() {
		mesh.dispose();
		if (ownsShader && shader != null) {
			shader.dispose();
		}
	}

	public Transform4 getProjectionMatrix() {
		return projectionMatrix;
	}

	public Transform4 getTransformMatrix() {
		return transformMatrix;
	}

	public void setProjectionMatrix(Transform4 projection) {
		if (drawing) {
			submit();
		}
		projectionMatrix.set(projection);
		if (drawing) {
			setupMatrices();
		}
	}

	public void setTransformMatrix(Transform4 transform) {
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
		invTexWidth = 1.0f / texture.getWidth();
		invTexHeight = 1.0f / texture.getHeight();
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

	public void draw(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.getWidth() / 2, texture.getHeight() / 2,
				texture.getWidth(), texture.getHeight(), 1f, 1f, rotation, 0,
				0, texture.getWidth(), texture.getHeight(), false, false);
	}

	public void draw(LTexture texture, float x, float y, float width,
			float height, float rotation) {
		if (rotation == 0 && texture.getWidth() == width
				&& texture.getHeight() == height) {
			draw(texture, x, y, width, height);
		} else {
			draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
					rotation, 0, 0, texture.getWidth(), texture.getHeight(),
					false, false);
		}
	}

	public void draw(LTexture texture, float x, float y, float rotation,
			float srcX, float srcY, float srcWidth, float srcHeight) {
		draw(texture, x, y, srcWidth / 2, srcHeight / 2, texture.getWidth(),
				texture.getHeight(), 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, false, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin,
			float width, float height, float scale, float rotation,
			RectBox src, boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, width, height, scale,
				scale, rotation, src.x, src.y, src.width, src.height, flipX,
				flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin,
			float scale, float rotation, RectBox src, boolean flipX,
			boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height,
				scale, scale, rotation, src.x, src.y, src.width, src.height,
				flipX, flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin,
			float scale, RectBox src, boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height,
				scale, scale, 0, src.x, src.y, src.width, src.height, flipX,
				flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin,
			RectBox src, boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height,
				1f, 1f, 0, src.x, src.y, src.width, src.height, flipX, flipY,
				false);
	}

	public void draw(LTexture texture, Vector2f pos, RectBox src,
			boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, src.width / 2, src.height / 2, src.width,
				src.height, 1f, 1f, 0, src.x, src.y, src.width, src.height,
				flipX, flipY, false);
	}

	public void draw(LTexture texture, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY) {
		draw(texture, x, y, originX, originY, width, height, scaleX, scaleY,
				rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, false);
	}

	public void draw(LTexture texture, float x, float y, float originX,
			float originY, float scaleX, float scaleY, float rotation,
			float srcX, float srcY, float srcWidth, float srcHeight,
			boolean flipX, boolean flipY) {
		draw(texture, x, y, originX, originY, srcWidth, srcHeight, scaleX,
				scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX,
				flipY, false);
	}

	public void draw(LTexture texture, Vector2f position, RectBox src,
			LColor c, float rotation, Vector2f origin, Vector2f scale,
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
			draw(texture, position.x, position.y, origin.x, origin.y,
					src.width, src.height, scale.x, scale.y, rotation, src.x,
					src.y, src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, position.x, position.y, origin.x, origin.y,
					texture.getWidth(), texture.getHeight(), scale.x, scale.y,
					rotation, 0, 0, texture.getWidth(), texture.getHeight(),
					flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, RectBox src,
			LColor c, float rotation, float sx, float sy, float scale,
			SpriteEffects effects) {

		if (src == null && rotation == 0 && scale == 1f && sx == 0 && sy == 0) {
			draw(texture, position, c);
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
			draw(texture, position.x, position.y, sx, sy, src.width,
					src.height, scale, scale, rotation, src.x, src.y,
					src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, position.x, position.y, sx, sy, texture.getWidth(),
					texture.getHeight(), scale, scale, rotation, 0, 0,
					texture.getWidth(), texture.getHeight(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, RectBox src,
			LColor c, float rotation, Vector2f origin, float scale,
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
			draw(texture, position.x, position.y, origin.x, origin.y,
					src.width, src.height, scale, scale, rotation, src.x,
					src.y, src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, position.x, position.y, origin.x, origin.y,
					texture.getWidth(), texture.getHeight(), scale, scale,
					rotation, 0, 0, texture.getWidth(), texture.getHeight(),
					flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, float px, float py, float srcX,
			float srcY, float srcWidth, float srcHeight, LColor c,
			float rotation, float originX, float originY, float scale,
			SpriteEffects effects) {

		if (effects == SpriteEffects.None && rotation == 0f && originX == 0f
				&& originY == 0f && scale == 1f) {
			draw(texture, px, py, srcX, srcY, srcWidth, srcHeight, c);
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
		draw(texture, px, py, originX, originY, srcWidth, srcHeight, scale,
				scale, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY,
				true);
		setColor(old);
	}

	public void draw(LTexture texture, float px, float py, RectBox src,
			LColor c, float rotation, Vector2f origin, float scale,
			SpriteEffects effects) {
		draw(texture, px, py, src, c, rotation, origin.x, origin.y, scale,
				effects);
	}

	public void draw(LTexture texture, float px, float py, RectBox src,
			LColor c, float rotation, float ox, float oy, float scale,
			SpriteEffects effects) {
		draw(texture, px, py, src, c, rotation, ox, oy, scale, scale, effects);
	}

	public void draw(LTexture texture, float px, float py, RectBox src,
			LColor c, float rotation, float ox, float oy, float scaleX,
			float scaleY, SpriteEffects effects) {
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
			draw(texture, px, py, ox, oy, src.width, src.height, scaleX,
					scaleY, rotation, src.x, src.y, src.width, src.height,
					flipX, flipY, true);
		} else {
			draw(texture, px, py, ox, oy, texture.getWidth(),
					texture.getHeight(), scaleX, scaleY, rotation, 0, 0,
					texture.getWidth(), texture.getHeight(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, LColor c,
			float rotation, Vector2f origin, Vector2f scale,
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

		draw(texture, position.x, position.y, origin.x, origin.y,
				texture.getWidth(), texture.getHeight(), scale.x, scale.y,
				rotation, 0, 0, texture.getWidth(), texture.getHeight(), flipX,
				flipY, true);

		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, LColor c,
			float rotation, float originX, float originY, float scale,
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

		draw(texture, position.x, position.y, originX, originY,
				texture.getWidth(), texture.getHeight(), scale, scale,
				rotation, 0, 0, texture.getWidth(), texture.getHeight(), flipX,
				flipY, true);

		setColor(old);
	}

	public void draw(LTexture texture, float posX, float posY, float srcX,
			float srcY, float srcWidth, float srcHeight, LColor c,
			float rotation, float originX, float originY, float scaleX,
			float scaleY, SpriteEffects effects) {
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
		draw(texture, posX, posY, originX, originY, srcWidth, srcHeight,
				scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight,
				flipX, flipY, true);
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, float srcX,
			float srcY, float srcWidth, float srcHeight, LColor c,
			float rotation, Vector2f origin, Vector2f scale,
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
		draw(texture, position.x, position.y, origin.x, origin.y, srcWidth,
				srcHeight, scale.x, scale.y, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(LTexture texture, RectBox dst, RectBox src, LColor c,
			float rotation, Vector2f origin, SpriteEffects effects) {
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
			draw(texture, dst.x, dst.y, origin.x, origin.y, dst.width,
					dst.height, 1f, 1f, rotation, src.x, src.y, src.width,
					src.height, flipX, flipY, true);
		} else {
			draw(texture, dst.x, dst.y, origin.x, origin.y, dst.width,
					dst.height, 1f, 1f, rotation, 0, 0, texture.getWidth(),
					texture.getHeight(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, float dstX, float dstY, float dstWidth,
			float dstHeight, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c, float rotation, float originX,
			float originY, SpriteEffects effects) {
		if (effects == SpriteEffects.None && rotation == 0 && originX == 0
				&& originY == 0) {
			draw(texture, dstX, dstY, dstWidth, dstHeight, srcX, srcY,
					srcWidth, srcHeight, c);
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
		draw(texture, dstX, dstY, originX, originY, dstWidth, dstHeight, 1f,
				1f, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY,
				true);
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY,
			boolean off) {

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
			float height, float rotation, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, width, height, rotation);
		setColor(old);
	}

	public void drawFlipX(LTexture texture, float x, float y) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight(), 0, 0,
				texture.getWidth(), texture.getHeight(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight(), 0, 0,
				texture.getWidth(), texture.getHeight(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width,
			float height) {
		draw(texture, x, y, width, height, 0, 0, texture.getWidth(),
				texture.getHeight(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float width,
			float height) {
		draw(texture, x, y, width, height, 0, 0, texture.getWidth(),
				texture.getHeight(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.getWidth() / 2, texture.getHeight() / 2,
				texture.getWidth(), texture.getHeight(), 1f, 1f, rotation, 0,
				0, texture.getWidth(), texture.getHeight(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.getWidth() / 2, texture.getHeight() / 2,
				texture.getWidth(), texture.getHeight(), 1f, 1f, rotation, 0,
				0, texture.getWidth(), texture.getHeight(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width,
			float height, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
				rotation, 0, 0, texture.getWidth(), texture.getHeight(), true,
				false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float width,
			float height, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
				rotation, 0, 0, texture.getWidth(), texture.getHeight(), false,
				true);
	}

	public void draw(LTexture texture, RectBox dstBox, RectBox srcBox, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, dstBox.x, dstBox.y, dstBox.width, dstBox.height,
				srcBox.x, srcBox.y, srcBox.width, srcBox.height, false, false);
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight) {
		draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight,
				false, false);
	}

	public void draw(LTexture texture, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight,
				false, false);
		setColor(old);
	}

	public void drawEmbedded(LTexture texture, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight, LColor c) {
		draw(texture, x, y, width - x, height - y, srcX, srcY, srcWidth - srcX,
				srcHeight - srcY, c);
	}

	public void draw(LTexture texture, float x, float y, float width,
			float height, float srcX, float srcY, float srcWidth,
			float srcHeight, boolean flipX, boolean flipY) {

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

	public void draw(LTexture texture, Vector2f pos, RectBox srcBox, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		if (srcBox == null) {
			draw(texture, pos.x, pos.y, 0, 0, texture.getWidth(),
					texture.getHeight());
		} else {
			draw(texture, pos.x, pos.y, srcBox.x, srcBox.y, srcBox.width,
					srcBox.height);
		}
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float srcX,
			float srcY, float srcWidth, float srcHeight, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float srcX,
			float srcY, float srcWidth, float srcHeight) {

		if (!checkTexture(texture)) {
			return;
		}

		float u = srcX * invTexWidth + texture.xOff;
		float v = srcY * invTexHeight + texture.yOff;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = (srcY + srcHeight) * invTexHeight;
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

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

	public void draw(LTexture texture, float x, float y, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, texture.getWidth(), texture.getHeight());
		setColor(old);
	}

	public void draw(LTexture texture, RectBox rect, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, rect.x, rect.y, rect.width, rect.height);
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f pos, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, pos.x, pos.y, texture.getWidth(), texture.getHeight());
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float width,
			float height) {

		if (!checkTexture(texture)) {
			return;
		}

		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = texture.xOff;
		final float v = texture.yOff;
		final float u2 = texture.widthRatio;
		final float v2 = texture.heightRatio;

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

	public void draw(LTexture texture, float[] spriteVertices, int offset,
			int length) {

		if (checkTexture(texture)) {
			return;
		}

		int remainingVertices = vertices.length - idx;
		if (remainingVertices == 0) {
			submit();
			remainingVertices = vertices.length;
		}
		int vertexCount = MathUtils.min(remainingVertices, length - offset);
		System.arraycopy(spriteVertices, offset, vertices, idx, vertexCount);
		offset += vertexCount;
		idx += vertexCount;

		while (offset < length) {
			submit();
			vertexCount = MathUtils.min(vertices.length, length - offset);
			System.arraycopy(spriteVertices, offset, vertices, 0, vertexCount);
			offset += vertexCount;
			idx += vertexCount;
		}
	}

	public void draw(LTextureRegion region, float x, float y, float rotation) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight(),
				rotation);
	}

	public void draw(LTextureRegion region, float x, float y, float width,
			float height, float rotation) {
		draw(region, x, y, region.getRegionWidth() / 2,
				region.getRegionHeight() / 2, width, height, 1f, 1f, rotation);
	}

	public void draw(LTextureRegion region, float x, float y) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}

	public void draw(LTextureRegion region, float x, float y, float width,
			float height) {

		if (!checkTexture(region.getTexture())) {
			return;
		}

		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = region.xOff;
		final float v = region.yOff;
		final float u2 = region.widthRatio;
		final float v2 = region.heightRatio;

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

	public void draw(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation) {

		if (!checkTexture(region.getTexture())) {
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

		final float u = region.xOff;
		final float v = region.yOff;
		final float u2 = region.widthRatio;
		final float v2 = region.heightRatio;

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

	public void draw(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, boolean clockwise) {

		if (!checkTexture(region.getTexture())) {
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

		float u1, v1, u2, v2, u3, v3, u4, v4;
		if (clockwise) {
			u1 = region.widthRatio;
			v1 = region.heightRatio;
			u2 = region.xOff;
			v2 = region.heightRatio;
			u3 = region.xOff;
			v3 = region.yOff;
			u4 = region.widthRatio;
			v4 = region.yOff;
		} else {
			u1 = region.xOff;
			v1 = region.yOff;
			u2 = region.widthRatio;
			v2 = region.yOff;
			u3 = region.widthRatio;
			v3 = region.heightRatio;
			u4 = region.xOff;
			v4 = region.heightRatio;
		}

		int idx = this.idx;

		vertices[idx++] = x1;
		vertices[idx++] = y1;
		vertices[idx++] = color;
		vertices[idx++] = u1;
		vertices[idx++] = v1;

		vertices[idx++] = x2;
		vertices[idx++] = y2;
		vertices[idx++] = color;
		vertices[idx++] = u2;
		vertices[idx++] = v2;

		vertices[idx++] = x3;
		vertices[idx++] = y3;
		vertices[idx++] = color;
		vertices[idx++] = u3;
		vertices[idx++] = v3;

		vertices[idx++] = x4;
		vertices[idx++] = y4;
		vertices[idx++] = color;
		vertices[idx++] = u4;
		vertices[idx++] = v4;

		this.idx = idx;
	}

}
