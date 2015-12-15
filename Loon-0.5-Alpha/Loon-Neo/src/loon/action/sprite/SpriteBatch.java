package loon.action.sprite;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.canvas.PixmapFImpl;
import loon.font.LFont;
import loon.geom.Matrix4;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.GL20;
import loon.opengl.LSTRDictionary;
import loon.opengl.LTextureRegion;
import loon.opengl.MeshDefault;
import loon.opengl.ShaderProgram;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.ObjectMap;

public class SpriteBatch extends PixmapFImpl {

	public static enum SpriteEffects {
		None, FlipHorizontally, FlipVertically;
	}

	private float alpha = 1f;

	float[] vertices;
	int idx = 0;
	LTexture lastTexture = null;
	float invTexWidth = 0, invTexHeight = 0;

	boolean drawing = false;

	private ShaderProgram shader;
	private ShaderProgram customShader = null;
	private boolean ownsShader;

	float color = LColor.white.toFloatBits();
	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int renderCalls = 0;

	public int totalRenderCalls = 0;

	public int maxSpritesInBatch = 0;

	int size;

	private boolean isLoaded;

	private boolean lockSubmit = false;

	private MeshDefault mesh;

	private BlendState lastBlendState = BlendState.NonPremultiplied;

	private LFont font = LFont.getDefaultFont();

	private LTexture colorTexture;

	public static class TextureLine {

		private Vector2f pstart = new Vector2f();

		private Vector2f pend = new Vector2f();

		private float pstrokeWidth;

		private float pangle;

		private Vector2f pdirection;

		private Vector2f pcentre;

		private float plength;

		private boolean pchanged;

		private LTexture whitePixel;

		public TextureLine(LTexture texture) {
			pchanged = true;
			whitePixel = texture;
		}

		public void setStart(float x, float y) {
			pstart.set(x, y);
			pchanged = true;
		}

		public void setEnd(float x, float y) {
			pend.set(x, y);
			pchanged = true;
		}

		public float getStrokeWidth() {
			return pstrokeWidth;
		}

		public void setStrokeWidth(float value) {
			pstrokeWidth = value;
			pchanged = true;
		}

		public void update() {
			pdirection = new Vector2f(pend.x - pstart.x, pend.y - pstart.y);
			pdirection.nor();
			pangle = MathUtils.toDegrees(MathUtils.atan2(pend.y - pstart.y,
					pend.x - pstart.x));
			plength = MathUtils.ceil(Vector2f.dst(pstart, pend));
			pcentre = new Vector2f((pend.x + pstart.x) / 2,
					(pend.y + pstart.y) / 2);
			pchanged = false;
		}

		public void draw(SpriteBatch batch) {
			if (pchanged) {
				update();
			}
			if (pstrokeWidth > 0) {
				batch.draw(whitePixel, pcentre.x, pcentre.y, plength / 2f,
						pstrokeWidth / 2, plength, pstrokeWidth, 1f, 1f,
						pangle, 0, 0, 1f, 1f, false, false, true);
			}
		}
	}

	private ObjectMap<Integer, SpriteBatch.TextureLine> lineLazy = new ObjectMap<Integer, SpriteBatch.TextureLine>(
			1000);

	@Override
	protected void drawLineImpl(float x1, float y1, float x2, float y2) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x1);
		hashCode = LSystem.unite(hashCode, y1);
		hashCode = LSystem.unite(hashCode, x2);
		hashCode = LSystem.unite(hashCode, y2);
		SpriteBatch.TextureLine line = lineLazy.get(hashCode);
		if (line == null) {
			line = new SpriteBatch.TextureLine(colorTexture);
			line.setStart(x1, y1);
			line.setEnd(x2, y2);
			line.setStrokeWidth(LSystem.base().display().GL().getPixSkip());
			lineLazy.put(hashCode, line);
		}
		line.draw(this);
	}

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public SpriteBatch() {
		this(1000, null);
	}

	public SpriteBatch(int size) {
		this(size, null);
	}

	public SpriteBatch(final int size, final ShaderProgram defaultShader) {
		super(0, 0, LSystem.viewSize.getRect(), LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight(), 4);
		if (size > 5460) {
			throw new IllegalArgumentException(
					"Can't have more than 5460 sprites per batch: " + size);
		}
		this.colorTexture = LSystem.base().graphics().finalColorTex();
		this.mesh = new MeshDefault();
		this.shader = defaultShader;
		this.size = size;
	}

	public void setShaderUniformf(String name, LColor color) {
		if (shader != null) {
			shader.setUniformf(name, color);
		}
	}

	public void setShaderUniformf(int name, LColor color) {
		if (shader != null) {
			shader.setUniformf(name, color);
		}
	}

	public void setColor(LColor c) {
		color = c.toFloatBits();
	}

	public void setColor(int r, int g, int b, int a) {
		color = LColor.toFloatBits(r, g, b, alpha == 1f ? a
				: (int) (alpha * 255));
	}

	public void setColor(float r, float g, float b, float a) {
		color = LColor.toFloatBits(r, g, b, alpha == 1f ? a : alpha);
	}

	public void setColor(int r, int g, int b) {
		color = LColor.toFloatBits(r, g, b, (int) (alpha * 255));
	}

	public void setColor(float r, float g, float b) {
		color = LColor.toFloatBits(r, g, b, alpha);
	}

	public void setColor(int v) {
		color = NumberUtils.intBitsToFloat(v & 0xfeffffff);
	}

	public void setColor(float color) {
		this.color = color;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		int intBits = NumberUtils.floatToRawIntBits(color);
		float r = (intBits & 0xff) / 255f;
		float g = ((intBits >>> 8) & 0xff) / 255f;
		float b = ((intBits >>> 16) & 0xff) / 255f;
		float a = (int) (alpha * 255) / 255f;
		color = LColor.toFloatBits(r, g, b, a);
	}

	public float alpha() {
		return alpha;
	}

	public float getAlpha() {
		return alpha;
	}

	public float color() {
		return color;
	}

	public LColor getColor() {
		int intBits = NumberUtils.floatToRawIntBits(color);
		LColor color = this.tempColor;
		color.r = (intBits & 0xff) / 255f;
		color.g = ((intBits >>> 8) & 0xff) / 255f;
		color.b = ((intBits >>> 16) & 0xff) / 255f;
		color.a = ((intBits >>> 24) & 0xff) / 255f;
		return color;
	}

	public float getFloatColor() {
		return color;
	}

	public void halfAlpha() {
		color = 1.7014117E38f;
		alpha = 0.5f;
	}

	public void resetColor() {
		color = -1.7014117E38f;
		alpha = 1f;
	}

	public boolean isLockSubmit() {
		return lockSubmit;
	}

	public void setLockSubmit(boolean lockSubmit) {
		this.lockSubmit = lockSubmit;
	}

	public final void drawString(String mes, Vector2f position) {
		drawString(mes, position.x, position.y, getColor());
	}

	public final void drawString(String mes, Vector2f position, LColor color) {
		drawString(mes, position.x, position.y, color);
	}

	public final void drawString(String mes, float x, float y) {
		drawString(mes, x, y, getColor());
	}

	public final void drawString(String mes, float x, float y, LColor color) {
		drawString(mes, x, y, 0, color);
	}

	public final void drawString(String mes, float x, float y, float rotation) {
		drawString(mes, x, y, rotation, getColor());
	}

	public void drawString(String mes, float x, float y, float rotation,
			LColor c) {
		drawString(mes, x, y, 1f, 1f, 0, 0, rotation, c);
	}

	public void drawString(String mes, float x, float y, float sx, float sy,
			Vector2f origin, float rotation, LColor c) {
		drawString(mes, x, y, sx, sy, origin.x, origin.y, rotation, c);
	}

	public void drawString(String mes, float x, float y, Vector2f origin,
			float rotation, LColor c) {
		drawString(mes, x, y, 1f, 1f, origin.x, origin.y, rotation, c);
	}

	public void drawString(String mes, float x, float y, Vector2f origin,
			LColor c) {
		drawString(mes, x, y, 1f, 1f, origin.x, origin.y, 0, c);
	}

	public void drawString(String mes, float x, float y, float scaleX,
			float scaleY, float ax, float ay, float rotation, LColor c) {
		checkDrawing();
		if (c == null) {
			return;
		}
		if (mes == null || mes.length() == 0) {
			return;
		}
		if (!lockSubmit) {
			submit();
		}
		LSTRDictionary.drawString(font, mes, x, y, scaleX, scaleX, ax, ay,
				rotation, c);
	}

	public void begin() {
		if (!isLoaded) {
			vertices = new float[size * SpriteRegion.SPRITE_SIZE];
			if (shader == null) {
				shader = LSystem.createDefaultShader();
				ownsShader = true;
			}
			isLoaded = true;
		}
		LSystem.mainEndDraw();
		if (drawing) {
			throw new IllegalStateException(
					"SpriteBatch.end must be called before begin.");
		}
		renderCalls = 0;
		LSystem.base().graphics().gl.glDepthMask(false);
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
		this.lastBlendState = state;
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
		LSystem.base().graphics().gl.glDepthMask(true);
		if (customShader != null) {
			customShader.end();
		} else {
			shader.end();
		}
		LSystem.mainBeginDraw();
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
		LTexture tex2d = LTexture.firstFather(texture);
		if (tex2d != null) {
			if (tex2d != lastTexture) {
				submit();
				lastTexture = tex2d;
			} else if (idx == vertices.length) {
				submit();
			}
			invTexWidth = (1f / texture.width()) * texture.widthRatio;
			invTexHeight = (1f / texture.height()) * texture.heightRatio;
		} else if (texture != lastTexture) {
			submit();
			lastTexture = texture;
			invTexWidth = (1f / texture.width()) * texture.widthRatio;
			invTexHeight = (1f / texture.height()) * texture.heightRatio;
		} else if (idx == vertices.length) {
			submit();
		}
		return true;
	}

	public void submit() {
		submit(lastBlendState);
	}

	public synchronized void submit(BlendState state) {
		if (idx == 0) {
			return;
		}
		GL20 gl = LSystem.base().graphics().gl;
		int old = GLUtils.getBlendMode();
		try {
			LSystem.mainEndDraw();
			renderCalls++;
			totalRenderCalls++;
			int spritesInBatch = idx / 20;
			if (spritesInBatch > maxSpritesInBatch) {
				maxSpritesInBatch = spritesInBatch;
			}
			int count = spritesInBatch * 6;
			GLUtils.bindTexture(gl, lastTexture);
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
			case Null:
				break;
			}
			mesh.post(name, size, customShader != null ? customShader : shader,
					vertices, idx, count);
		} finally {
			GLUtils.setBlendMode(gl, old);
			LSystem.mainBeginDraw();
			idx = 0;
		}
	}

	private final static String name = "batch";

	public void close() {
		if (ownsShader && shader != null) {
			shader.close();
		}
		if (customShader != null) {
			customShader.close();
		}
	}

	private void setupMatrices() {
		final Matrix4 view = LSystem.base().graphics().getViewMatrix();
		if (customShader != null) {
			customShader.setUniformMatrix("u_projTrans", view);
			customShader.setUniformi("u_texture", 0);
		} else {
			shader.setUniformMatrix("u_projTrans", view);
			shader.setUniformi("u_texture", 0);
		}
	}

	protected void switchTexture(LTexture texture) {
		submit();
		lastTexture = texture;
		if (texture.isScale()) {
			invTexWidth = (1f / texture.width());
			invTexHeight = (1f / texture.height());
		} else {
			invTexWidth = (1f / texture.width()) * texture.widthRatio;
			invTexHeight = (1f / texture.height()) * texture.heightRatio;
		}
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

	public void drawScale(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, rotation, 0, 0, texture.width(), texture.height(),
				false, false);
	}

	public void drawScale(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, 0, 0, 0, texture.width(), texture.height(), false,
				false);
	}

	public void drawScaleFlipX(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, 0, 0, 0, texture.width(), texture.height(), true, false);
	}

	public void drawScaleFlipX(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, rotation, 0, 0, texture.width(), texture.height(),
				true, false);
	}
	
	public void drawScaleFlipY(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, 0, 0, 0, texture.width(), texture.height(), false, true);
	}

	public void drawScaleFlipY(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, rotation, 0, 0, texture.width(), texture.height(),
				false, true);
	}
	
	public void draw(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.width() / 2, texture.height() / 2,
				texture.width(), texture.height(), 1f, 1f, rotation, 0, 0,
				texture.width(), texture.height(), false, false);
	}

	public void draw(LTexture texture, float x, float y, float width,
			float height, float rotation) {
		if (rotation == 0 && texture.width() == width
				&& texture.height() == height) {
			draw(texture, x, y, width, height);
		} else {
			draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
					rotation, 0, 0, texture.width(), texture.height(), false,
					false);
		}
	}

	public void draw(LTexture texture, float x, float y, float rotation,
			float srcX, float srcY, float srcWidth, float srcHeight) {
		draw(texture, x, y, srcWidth / 2, srcHeight / 2, texture.width(),
				texture.height(), 1f, 1f, rotation, srcX, srcY, srcWidth,
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
					texture.width(), texture.height(), scale.x, scale.y,
					rotation, 0, 0, texture.width(), texture.height(), flipX,
					flipY, true);
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
			draw(texture, position.x, position.y, sx, sy, texture.width(),
					texture.height(), scale, scale, rotation, 0, 0,
					texture.width(), texture.height(), flipX, flipY, true);
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
					texture.width(), texture.height(), scale, scale, rotation,
					0, 0, texture.width(), texture.height(), flipX, flipY, true);
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
			draw(texture, px, py, ox, oy, texture.width(), texture.height(),
					scaleX, scaleY, rotation, 0, 0, texture.width(),
					texture.height(), flipX, flipY, true);
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
				texture.width(), texture.height(), scale.x, scale.y, rotation,
				0, 0, texture.width(), texture.height(), flipX, flipY, true);

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
				texture.width(), texture.height(), scale, scale, rotation, 0,
				0, texture.width(), texture.height(), flipX, flipY, true);

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
					dst.height, 1f, 1f, rotation, 0, 0, texture.width(),
					texture.height(), flipX, flipY, true);
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
		draw(texture, x, y, texture.width(), texture.height(), 0, 0,
				texture.width(), texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y) {
		draw(texture, x, y, texture.width(), texture.height(), 0, 0,
				texture.width(), texture.height(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width,
			float height) {
		draw(texture, x, y, width, height, 0, 0, texture.width(),
				texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float width,
			float height) {
		draw(texture, x, y, width, height, 0, 0, texture.width(),
				texture.height(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.width() / 2, texture.height() / 2,
				texture.width(), texture.height(), 1f, 1f, rotation, 0, 0,
				texture.width(), texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.width() / 2, texture.height() / 2,
				texture.width(), texture.height(), 1f, 1f, rotation, 0, 0,
				texture.width(), texture.height(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width,
			float height, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
				rotation, 0, 0, texture.width(), texture.height(), true, false);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width,
			float height, float scaleX, float scaleY, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX,
				scaleY, rotation, 0, 0, texture.width(), texture.height(),
				true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float width,
			float height, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f,
				rotation, 0, 0, texture.width(), texture.height(), false, true);
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
			draw(texture, pos.x, pos.y, 0, 0, texture.width(), texture.height());
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
		draw(texture, x, y, texture.width(), texture.height());
	}

	public void draw(LTexture texture, float x, float y, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, texture.width(), texture.height());
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
		draw(texture, pos.x, pos.y, texture.width(), texture.height());
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

	public void drawPoint(int x, int y) {

		drawPointImpl(x, y);

	}

	public void fillPolygon(float xPoints[], float yPoints[], int nPoints) {
		fillPolygonImpl(xPoints, yPoints, nPoints);
	}

	public void drawPolygon(float[] xPoints, float[] yPoints, int nPoints) {
		drawPolygonImpl(xPoints, yPoints, nPoints);
	}

	public void drawOval(float x1, float y1, float width, float height) {
		drawOvalImpl(x1, y1, width, height);
	}

	public void fillOval(float x1, float y1, float width, float height) {
		fillOvalImpl(x1, y1, width, height);
	}

	public void drawArc(RectBox rect, float start, float end) {
		drawArcImpl(rect.x, rect.y, rect.width, rect.height, start, end);
	}

	public void drawArc(float x1, float y1, float width, float height,
			float start, float end) {
		drawArcImpl(x1, y1, width, height, start, end);
	}

	public void fillArc(float x1, float y1, float width, float height,
			float start, float end) {
		fillArcImpl(x1, y1, width, height, start, end);
	}

	public void drawRect(float x, float y, float width, float height) {
		drawRectImpl(x, y, width, height);

	}

	public final void drawRoundRect(float x, float y, float width,
			float height, int radius) {
		drawRoundRectImpl(x, y, width, height, radius, radius);

	}

	public final void fillRoundRect(float x, float y, float width,
			float height, int radius) {
		fillRoundRectImpl(x, y, width, height, radius);
	}

	public void fillRect(float x, float y, float width, float height) {
		fillRectNative(x, y, width, height);
	}

	public void draw(Shape shape) {
		draw(shape, 0, 0);
	}

	public void draw(Shape shape, float x, float y) {
		drawShapeImpl(shape, x, y);
	}

	public void fill(Shape shape) {
		fill(shape, 0, 0);
	}

	public void fill(Shape shape, float x, float y) {
		fillShapeImpl(shape, x, y);
	}

	@Override
	protected void drawPointNative(float x, float y, int skip) {
		if (!inside(x, y)) {
			draw(colorTexture, x, y, skip, skip);
		}
	}

	@Override
	protected void fillRectNative(float x, float y, float width, float height) {
		draw(colorTexture, x, y, width, height);
	}

}
