/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.canvas.PixmapFImpl;
import loon.font.IFont;
import loon.geom.Matrix4;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.ExpandVertices;
import loon.opengl.GL20;
import loon.opengl.LTextureRegion;
import loon.opengl.MeshDefault;
import loon.opengl.ShaderProgram;
import loon.opengl.TrilateralBatch;
import loon.opengl.TrilateralBatch.Source;
import loon.utils.GLUtils;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

/**
 * 这是一个纹理批量渲染的实现,其中API可以基本兼容xna(monogame)以及libgdx的同名SpriteBatch类(干什么用大家都懂的)
 * 
 */
public class SpriteBatch extends PixmapFImpl {

	public static enum SpriteEffects {
		None, FlipHorizontally, FlipVertically;
	}

	private boolean _use_ascent = false;

	private float offsetX, offsetY;

	private float alpha = 1f;

	private final ExpandVertices expandVertices;
	int idx = 0;
	LTexture lastTexture = null;
	float invTexWidth = 0, invTexHeight = 0;

	boolean drawing = false;

	private ShaderProgram shader;
	private ShaderProgram customShader = null;
	private boolean ownsShader;

	private float color = LColor.white.toFloatBits();

	private LColor tempColor = new LColor(1, 1, 1, 1);

	public int renderCalls = 0;

	public int totalRenderCalls = 0;

	public int maxSpritesInBatch = 0;

	private boolean isLoaded;

	private boolean lockSubmit = false;

	private MeshDefault mesh;

	private BlendState lastBlendState = BlendState.NonPremultiplied;

	private IFont font;

	private final Source source;

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
			pangle = MathUtils.toDegrees(MathUtils.atan2(pend.y - pstart.y, pend.x - pstart.x));
			plength = MathUtils.ceil(Vector2f.dst(pstart, pend));
			pcentre = new Vector2f((pend.x + pstart.x) / 2, (pend.y + pstart.y) / 2);
			pchanged = false;
		}

		public void draw(SpriteBatch batch) {
			if (pchanged) {
				update();
			}
			if (pstrokeWidth > 0) {
				batch.draw(whitePixel, pcentre.x, pcentre.y, plength / 2f, pstrokeWidth / 2, plength, pstrokeWidth, 1f,
						1f, pangle, 0, 0, 1f, 1f, false, false, true);
			}
		}
	}

	private IntMap<SpriteBatch.TextureLine> lineLazy = new IntMap<SpriteBatch.TextureLine>(1000);

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

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public SpriteBatch() {
		this(256);
	}

	public SpriteBatch(int size) {
		this(TrilateralBatch.DEF_SOURCE, size);
	}

	public SpriteBatch(Source src, int size) {
		this(src, size, null);
	}

	public SpriteBatch(final Source src, final int size, final ShaderProgram defaultShader) {
		super(0, 0, LSystem.viewSize.getRect(), LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), 4);
		if (size > 5460) {
			throw new IllegalArgumentException("Can't have more than 5460 sprites per batch: " + size);
		}
		this.name = "spritebatch";
		this.source = src;
		this.font = LSystem.getSystemGameFont();
		this.colorTexture = LSystem.base().graphics().finalColorTex();
		this.mesh = new MeshDefault();
		this.shader = defaultShader;
		this.expandVertices = new ExpandVertices(size);
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
		color = LColor.toFloatBits(r, g, b, alpha == 1f ? a : (int) (alpha * 255));
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

	public void drawString(IFont spriteFont, String text, float px, float py, LColor color, float rotation,
			float originx, float originy, float scale) {
		IFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = 2;
		if (rotation == 0f) {
			drawString(text, px - (originx * scale), (py + heigh) - (originy * scale), scale, scale, originx, originy,
					rotation, color);
		} else {
			drawString(text, px, (py + heigh), scale, scale, originx, originy, rotation, color);
		}
		setFont(old);
	}

	public void drawString(IFont spriteFont, String text, Vector2f position, LColor color, float rotation,
			Vector2f origin, float scale) {
		IFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = 2;
		if (rotation == 0f) {
			drawString(text, position.x - (origin.x * scale), (position.y + heigh) - (origin.y * scale), scale, scale,
					origin.x, origin.y, rotation, color);
		} else {
			drawString(text, position.x, (position.y + heigh), scale, scale, origin.x, origin.y, rotation, color);
		}
		setFont(old);
	}

	public void drawString(IFont spriteFont, String text, Vector2f position, LColor color) {
		IFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = 2;
		drawString(text, position.x, (position.y + heigh), 1f, 1f, 0f, 0f, 0f, color);
		setFont(old);
	}

	public void drawString(IFont spriteFont, String text, float x, float y, LColor color) {
		IFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = 2;
		drawString(text, x, (y + heigh), 1f, 1f, 0f, 0f, 0f, color);
		setFont(old);
	}

	public void drawString(IFont spriteFont, String text, Vector2f position, LColor color, float rotation,
			Vector2f origin, Vector2f scale) {
		IFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = 2;
		if (rotation == 0f) {
			drawString(text, position.x - (origin.x * scale.x), (position.y + heigh) - (origin.y * scale.y), scale.x,
					scale.y, origin.x, origin.y, rotation, color);
		} else {
			drawString(text, position.x, (position.y + heigh), scale.x, scale.y, origin.x, origin.y, rotation, color);
		}
		setFont(old);
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

	public void drawString(String mes, float x, float y, float rotation, LColor c) {
		drawString(mes, x, y, 1f, 1f, 0, 0, rotation, c);
	}

	public void drawString(String mes, float x, float y, float sx, float sy, Vector2f origin, float rotation,
			LColor c) {
		drawString(mes, x, y, sx, sy, origin.x, origin.y, rotation, c);
	}

	public void drawString(String mes, float x, float y, Vector2f origin, float rotation, LColor c) {
		drawString(mes, x, y, 1f, 1f, origin.x, origin.y, rotation, c);
	}

	public void drawString(String mes, float x, float y, Vector2f origin, LColor c) {
		drawString(mes, x, y, 1f, 1f, origin.x, origin.y, 0, c);
	}

	public void drawString(String mes, float x, float y, float scaleX, float scaleY, float ax, float ay, float rotation,
			LColor c) {
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
		font.drawString(LSystem.base().display().GL(), mes, x + offsetX,
				_use_ascent ? y - font.getAscent() : y + offsetY, scaleX, scaleX, ax, ay, rotation, c);
	}

	public void setUseAscent(boolean a) {
		this._use_ascent = a;
	}

	public boolean getUseAscent() {
		return this._use_ascent;
	}

	public void begin() {
		if (!isLoaded) {
			if (shader == null) {
				shader = LSystem.createShader(source.vertexShader(), source.fragmentShader());
				ownsShader = true;
			}
			isLoaded = true;
		}
		LSystem.mainEndDraw();
		if (drawing) {
			throw LSystem.runThrow("SpriteBatch.end must be called before begin.");
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
			throw LSystem.runThrow("SpriteBatch.begin must be called before end.");
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
			} else if (idx == expandVertices.length()) {
				submit();
			}
			invTexWidth = (1f / texture.width()) * texture.widthRatio;
			invTexHeight = (1f / texture.height()) * texture.heightRatio;
		} else if (texture != lastTexture) {
			submit();
			lastTexture = texture;
			invTexWidth = (1f / texture.width()) * texture.widthRatio;
			invTexHeight = (1f / texture.height()) * texture.heightRatio;
		} else if (idx == expandVertices.length()) {
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
			mesh.post(name, expandVertices.getSize(), customShader != null ? customShader : shader,
					expandVertices.getVertices(), idx, count);
		} catch (Exception e) {
			throw LSystem.runThrow(e.getMessage());
		} finally {
			if (expandVertices.expand(this.idx)) {
				mesh.reset(name, expandVertices.length());
			}
			GLUtils.setBlendMode(gl, old);
			LSystem.mainBeginDraw();
			if (!lockSubmit) {
				idx = 0;
			}
		}
	}

	private final String name;

	public void close() {
		if (ownsShader && shader != null) {
			shader.close();
		}
		if (customShader != null) {
			customShader.close();
		}
		if (mesh != null) {
			mesh.dispose(name, expandVertices.getSize());
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
		if (texture.isCopy()) {
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

	public void drawScale(LTexture texture, float x, float y, float width, float height, float scaleX, float scaleY,
			float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, 0, 0, texture.width(),
				texture.height(), false, false);
	}

	public void drawScale(LTexture texture, float x, float y, float width, float height, float scaleX, float scaleY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, 0, 0, 0, texture.width(),
				texture.height(), false, false);
	}

	public void drawScaleFlipX(LTexture texture, float x, float y, float width, float height, float scaleX,
			float scaleY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, 0, 0, 0, texture.width(),
				texture.height(), true, false);
	}

	public void drawScaleFlipX(LTexture texture, float x, float y, float width, float height, float scaleX,
			float scaleY, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, 0, 0, texture.width(),
				texture.height(), true, false);
	}

	public void drawScaleFlipY(LTexture texture, float x, float y, float width, float height, float scaleX,
			float scaleY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, 0, 0, 0, texture.width(),
				texture.height(), false, true);
	}

	public void drawScaleFlipY(LTexture texture, float x, float y, float width, float height, float scaleX,
			float scaleY, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, 0, 0, texture.width(),
				texture.height(), false, true);
	}

	public void draw(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.width() / 2, texture.height() / 2, texture.width(), texture.height(), 1f, 1f,
				rotation, 0, 0, texture.width(), texture.height(), false, false);
	}

	public void draw(LTexture texture, float x, float y, float width, float height, float rotation) {
		if (rotation == 0 && texture.width() == width && texture.height() == height) {
			draw(texture, x, y, width, height);
		} else {
			draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, texture.width(),
					texture.height(), false, false);
		}
	}

	public void draw(LTexture texture, float x, float y, float rotation, float srcX, float srcY, float srcWidth,
			float srcHeight) {
		draw(texture, x, y, srcWidth / 2, srcHeight / 2, texture.width(), texture.height(), 1f, 1f, rotation, srcX,
				srcY, srcWidth, srcHeight, false, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin, float width, float height, float scale,
			float rotation, RectBox src, boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, width, height, scale, scale, rotation, src.x, src.y, src.width,
				src.height, flipX, flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin, float scale, float rotation, RectBox src,
			boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height, scale, scale, rotation, src.x, src.y,
				src.width, src.height, flipX, flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin, float scale, RectBox src, boolean flipX,
			boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height, scale, scale, 0, src.x, src.y, src.width,
				src.height, flipX, flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin, RectBox src, boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, origin.x, origin.y, src.width, src.height, 1f, 1f, 0, src.x, src.y, src.width,
				src.height, flipX, flipY, false);
	}

	public void draw(LTexture texture, Vector2f pos, RectBox src, boolean flipX, boolean flipY) {
		draw(texture, pos.x, pos.y, src.width / 2, src.height / 2, src.width, src.height, 1f, 1f, 0, src.x, src.y,
				src.width, src.height, flipX, flipY, false);
	}

	public void draw(LTexture texture, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation, boolean flipX, boolean flipY) {
		draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, 0, 0, texture.getWidth(),
				texture.getHeight(), flipX, flipY, false);
	}

	public void draw(LTexture texture, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation, float srcX, float srcY, float srcWidth, float srcHeight,
			boolean flipX, boolean flipY) {
		draw(texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight,
				flipX, flipY, false);
	}

	public void draw(LTexture texture, float x, float y, float originX, float originY, float scaleX, float scaleY,
			float rotation, float srcX, float srcY, float srcWidth, float srcHeight, boolean flipX, boolean flipY) {
		draw(texture, x, y, originX, originY, srcWidth, srcHeight, scaleX, scaleY, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY, false);
	}

	public void draw(LTexture texture, Vector2f position, RectBox src, LColor c, float rotation, Vector2f origin,
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
		if (src != null) {
			draw(texture, position.x, position.y, origin.x, origin.y, src.width, src.height, scale.x, scale.y, rotation,
					src.x, src.y, src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, position.x, position.y, origin.x, origin.y, texture.width(), texture.height(), scale.x,
					scale.y, rotation, 0, 0, texture.width(), texture.height(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, RectBox src, LColor c, float rotation, float sx, float sy,
			float scale, SpriteEffects effects) {

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
			draw(texture, position.x, position.y, sx, sy, src.width, src.height, scale, scale, rotation, src.x, src.y,
					src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, position.x, position.y, sx, sy, texture.width(), texture.height(), scale, scale, rotation, 0,
					0, texture.width(), texture.height(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, RectBox src, LColor c, float rotation, Vector2f origin,
			float scale, SpriteEffects effects) {
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
			draw(texture, position.x, position.y, origin.x, origin.y, src.width, src.height, scale, scale, rotation,
					src.x, src.y, src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, position.x, position.y, origin.x, origin.y, texture.width(), texture.height(), scale, scale,
					rotation, 0, 0, texture.width(), texture.height(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, float px, float py, float srcX, float srcY, float srcWidth, float srcHeight,
			LColor c, float rotation, float originX, float originY, float scale, SpriteEffects effects) {

		if (effects == SpriteEffects.None && rotation == 0f && originX == 0f && originY == 0f && scale == 1f) {
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
		draw(texture, px, py, originX, originY, srcWidth, srcHeight, scale, scale, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(LTexture texture, float px, float py, RectBox src, LColor c, float rotation, Vector2f origin,
			float scale, SpriteEffects effects) {
		draw(texture, px, py, src, c, rotation, origin.x, origin.y, scale, effects);
	}

	public void draw(LTexture texture, float px, float py, RectBox src, LColor c, float rotation, float ox, float oy,
			float scale, SpriteEffects effects) {
		draw(texture, px, py, src, c, rotation, ox, oy, scale, scale, effects);
	}

	public void draw(LTexture texture, float px, float py, RectBox src, LColor c, float rotation, float ox, float oy,
			float scaleX, float scaleY, SpriteEffects effects) {
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
			draw(texture, px, py, ox, oy, src.width, src.height, scaleX, scaleY, rotation, src.x, src.y, src.width,
					src.height, flipX, flipY, true);
		} else {
			draw(texture, px, py, ox, oy, texture.width(), texture.height(), scaleX, scaleY, rotation, 0, 0,
					texture.width(), texture.height(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, LColor c, float rotation, Vector2f origin, float scale,
			SpriteEffects effects) {
		draw(texture, position, c, rotation, origin, Vector2f.at(scale, scale), effects);
	}

	public void draw(LTexture texture, Vector2f position, LColor c, float rotation, Vector2f origin, Vector2f scale,
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

		draw(texture, position.x, position.y, origin.x, origin.y, texture.width(), texture.height(), scale.x, scale.y,
				rotation, 0, 0, texture.width(), texture.height(), flipX, flipY, true);

		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, LColor c, float rotation, float originX, float originY,
			float scale, SpriteEffects effects) {
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

		draw(texture, position.x, position.y, originX, originY, texture.width(), texture.height(), scale, scale,
				rotation, 0, 0, texture.width(), texture.height(), flipX, flipY, true);

		setColor(old);
	}

	public void draw(LTexture texture, float posX, float posY, float srcX, float srcY, float srcWidth, float srcHeight,
			LColor c, float rotation, float originX, float originY, float scaleX, float scaleY, SpriteEffects effects) {
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
		draw(texture, posX, posY, originX, originY, srcWidth, srcHeight, scaleX, scaleY, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(LTexture texture, Vector2f position, float srcX, float srcY, float srcWidth, float srcHeight,
			LColor c, float rotation, Vector2f origin, Vector2f scale, SpriteEffects effects) {
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
		draw(texture, position.x, position.y, origin.x, origin.y, srcWidth, srcHeight, scale.x, scale.y, rotation, srcX,
				srcY, srcWidth, srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(LTexture texture, RectBox dst, RectBox src, LColor c, float rotation, Vector2f origin,
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
			draw(texture, dst.x, dst.y, origin.x, origin.y, dst.width, dst.height, 1f, 1f, rotation, src.x, src.y,
					src.width, src.height, flipX, flipY, true);
		} else {
			draw(texture, dst.x, dst.y, origin.x, origin.y, dst.width, dst.height, 1f, 1f, rotation, 0, 0,
					texture.width(), texture.height(), flipX, flipY, true);
		}
		setColor(old);
	}

	public void draw(LTexture texture, float dstX, float dstY, float dstWidth, float dstHeight, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor c, float rotation, float originX, float originY,
			SpriteEffects effects) {
		if (effects == SpriteEffects.None && rotation == 0 && originX == 0 && originY == 0) {
			draw(texture, dstX, dstY, dstWidth, dstHeight, srcX, srcY, srcWidth, srcHeight, c);
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
		draw(texture, dstX, dstY, originX, originY, dstWidth, dstHeight, 1f, 1f, rotation, srcX, srcY, srcWidth,
				srcHeight, flipX, flipY, true);
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation, float srcX, float srcY, float srcWidth, float srcHeight,
			boolean flipX, boolean flipY, boolean off) {

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

		expandVertices.setVertice(idx++, x1);
		expandVertices.setVertice(idx++, y1);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v);

		expandVertices.setVertice(idx++, x2);
		expandVertices.setVertice(idx++, y2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, x3);
		expandVertices.setVertice(idx++, y3);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, x4);
		expandVertices.setVertice(idx++, y4);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v);

		this.idx = idx;
	}

	public void draw(LTexture texture, float x, float y, float width, float height, float rotation, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, width, height, rotation);
		setColor(old);
	}

	public void drawFlipX(LTexture texture, float x, float y) {
		draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y) {
		draw(texture, x, y, texture.width(), texture.height(), 0, 0, texture.width(), texture.height(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width, float height) {
		draw(texture, x, y, width, height, 0, 0, texture.width(), texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float width, float height) {
		draw(texture, x, y, width, height, 0, 0, texture.width(), texture.height(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.width() / 2, texture.height() / 2, texture.width(), texture.height(), 1f, 1f,
				rotation, 0, 0, texture.width(), texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float rotation) {
		draw(texture, x, y, texture.width() / 2, texture.height() / 2, texture.width(), texture.height(), 1f, 1f,
				rotation, 0, 0, texture.width(), texture.height(), false, true);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width, float height, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, texture.width(),
				texture.height(), true, false);
	}

	public void drawFlipX(LTexture texture, float x, float y, float width, float height, float scaleX, float scaleY,
			float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, 0, 0, texture.width(),
				texture.height(), true, false);
	}

	public void drawFlipY(LTexture texture, float x, float y, float width, float height, float rotation) {
		draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, texture.width(),
				texture.height(), false, true);
	}

	public void drawFlip(LTexture texture, float x, float y, float width, float height, float scaleX, float scaleY,
			float rotation, boolean flipX, boolean flipY) {
		draw(texture, x, y, width / 2, height / 2, width, height, scaleX, scaleY, rotation, 0, 0, texture.width(),
				texture.height(), flipX, flipY);
	}

	public void draw(LTexture texture, RectBox dstBox, RectBox srcBox, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, dstBox.x, dstBox.y, dstBox.width, dstBox.height, srcBox.x, srcBox.y, srcBox.width, srcBox.height,
				false, false);
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight) {
		draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
	}

	public void draw(LTexture texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
		setColor(old);
	}

	public void drawEmbedded(LTexture texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, LColor c) {
		draw(texture, x, y, width - x, height - y, srcX, srcY, srcWidth - srcX, srcHeight - srcY, c);
	}

	public void draw(LTexture texture, float x, float y, float width, float height, float srcX, float srcY,
			float srcWidth, float srcHeight, boolean flipX, boolean flipY) {

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

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v);

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v);

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
			draw(texture, pos.x, pos.y, srcBox.x, srcBox.y, srcBox.width, srcBox.height);
		}
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float srcX, float srcY, float srcWidth, float srcHeight,
			LColor c) {
		float old = color;
		if (!c.equals(LColor.white)) {
			setColor(c);
		}
		draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
		setColor(old);
	}

	public void draw(LTexture texture, float x, float y, float srcX, float srcY, float srcWidth, float srcHeight) {

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

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v);

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v);

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

	public void draw(LTexture texture, float x, float y, float width, float height) {

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

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v);

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v);

		this.idx = idx;
	}

	public void draw(LTexture texture, float[] spriteVertices, int offset, int length) {

		if (checkTexture(texture)) {
			return;
		}

		int remainingVertices = expandVertices.length() - idx;
		if (remainingVertices == 0) {
			submit();
			remainingVertices = expandVertices.length();
		}
		int vertexCount = MathUtils.min(remainingVertices, length - offset);
		System.arraycopy(spriteVertices, offset, expandVertices.getVertices(), idx, vertexCount);
		offset += vertexCount;
		idx += vertexCount;

		while (offset < length) {
			submit();
			vertexCount = MathUtils.min(expandVertices.length(), length - offset);
			System.arraycopy(spriteVertices, offset, expandVertices.getVertices(), 0, vertexCount);
			offset += vertexCount;
			idx += vertexCount;
		}
	}

	public void draw(LTextureRegion region, float x, float y, float rotation) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight(), rotation);
	}

	public void draw(LTextureRegion region, float x, float y, float width, float height, float rotation) {
		draw(region, x, y, region.getRegionWidth() / 2, region.getRegionHeight() / 2, width, height, 1f, 1f, rotation);
	}

	public void draw(LTextureRegion region, float x, float y) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
	}

	public void draw(LTextureRegion region, float x, float y, float width, float height) {

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

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v);

		expandVertices.setVertice(idx++, x);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, fy2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, fx2);
		expandVertices.setVertice(idx++, y);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v);

		this.idx = idx;
	}

	public void draw(LTextureRegion region, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation) {

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

		expandVertices.setVertice(idx++, x1);
		expandVertices.setVertice(idx++, y1);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v);

		expandVertices.setVertice(idx++, x2);
		expandVertices.setVertice(idx++, y2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, x3);
		expandVertices.setVertice(idx++, y3);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, x4);
		expandVertices.setVertice(idx++, y4);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v);

		this.idx = idx;
	}

	public void draw(LTextureRegion region, float x, float y, float originX, float originY, float width, float height,
			float scaleX, float scaleY, float rotation, boolean clockwise) {

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

		expandVertices.setVertice(idx++, x1);
		expandVertices.setVertice(idx++, y1);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u1);
		expandVertices.setVertice(idx++, v1);

		expandVertices.setVertice(idx++, x2);
		expandVertices.setVertice(idx++, y2);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u2);
		expandVertices.setVertice(idx++, v2);

		expandVertices.setVertice(idx++, x3);
		expandVertices.setVertice(idx++, y3);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u3);
		expandVertices.setVertice(idx++, v3);

		expandVertices.setVertice(idx++, x4);
		expandVertices.setVertice(idx++, y4);
		expandVertices.setVertice(idx++, color);
		expandVertices.setVertice(idx++, u4);
		expandVertices.setVertice(idx++, v4);

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

	public void drawArc(float x1, float y1, float width, float height, float start, float end) {
		drawArcImpl(x1, y1, width, height, start, end);
	}

	public void fillArc(float x1, float y1, float width, float height, float start, float end) {
		fillArcImpl(x1, y1, width, height, start, end);
	}

	public void drawRect(float x, float y, float width, float height) {
		drawRectImpl(x, y, width, height);

	}

	public final void drawRoundRect(float x, float y, float width, float height, int radius) {
		drawRoundRectImpl(x, y, width, height, radius, radius);

	}

	public final void fillRoundRect(float x, float y, float width, float height, int radius) {
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

	public float getFontOffsetX() {
		return offsetX;
	}

	public void setFontOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getFontOffsetY() {
		return offsetY;
	}

	public void setFontOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

}
