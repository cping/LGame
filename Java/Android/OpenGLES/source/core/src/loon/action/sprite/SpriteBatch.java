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

import java.util.HashMap;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Shape;
import loon.core.geom.Triangle;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLAttributes;
import loon.core.graphics.opengl.GLBatch;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLMesh;
import loon.core.graphics.opengl.LSTRDictionary;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.graphics.opengl.GLAttributes.Usage;
import loon.core.graphics.opengl.GLMesh.VertexDataType;
import loon.utils.MathUtils;

public class SpriteBatch implements LRelease {

	public void draw(SpriteFont font, CharSequence cs, float x, float y) {
		font.drawString(this, cs, x, y);
	}

	public void draw(SpriteFont font, CharSequence cs, float x, float y,
			LColor color) {
		font.drawString(this, cs, x, y, color);
	}

	/**
	 * Sample: batch.draw(font, "Test", new Vector2f(150, 150), LColor.red, 0,
	 * Vector2f.Zero, new Vector2f(1f, 1f), SpriteEffects.None);
	 * 
	 * @param font
	 * @param cs
	 * @param local
	 * @param color
	 * @param rotation
	 * @param origin
	 * @param scale
	 * @param spriteEffects
	 */
	public void draw(SpriteFont font, CharSequence cs, Vector2f local,
			LColor color, float rotation, Vector2f origin, Vector2f scale,
			SpriteEffects spriteEffects) {
		font.drawString(this, cs, local, color, rotation, origin, scale,
				spriteEffects);
	}

	public static enum SpriteEffects {
		None, FlipHorizontally, FlipVertically;
	}

	static class TextureLine {

		private Vector2f pstart = new Vector2f();

		private Vector2f pend = new Vector2f();

		private float pstrokeWidth;

		private float pangle;

		private Vector2f pdirection;

		private Vector2f pcentre;

		private float plength;

		private boolean pchanged;

		public TextureLine() {
			pchanged = true;
			if (whitePixel == null) {
				whitePixel = TextureUtils.createTexture(1, 1, LColor.white);
			}
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

	private HashMap<Integer, SpriteBatch.TextureLine> lineLazy = new HashMap<Integer, SpriteBatch.TextureLine>(
			1000);

	private LColor tempColor = new LColor(1f, 1f, 1f, 1f);

	public float color = LColor.white.toFloatBits();

	private GLMesh mesh;

	private GLMesh[] buffers;

	private LTexture lastTexture = null;

	private int idx = 0;

	private int currBufferIdx = 0;

	private final float[] vertices;

	private boolean drawing = false;

	public int renderCalls = 0;

	public int totalRenderCalls = 0;

	public int maxSpritesInBatch = 0;

	public static final int VERTEX_SIZE = 2 + 1 + 2;

	public static final int SPRITE_SIZE = 4 * VERTEX_SIZE;

	private static LTexture whitePixel;

	private float alpha = 1f;

	private float invTexWidth;

	private float invTexHeight;

	public SpriteBatch() {
		this(1000);
	}

	public SpriteBatch(int size) {
		this(size, 1);
	}

	public SpriteBatch(int size, int buffers) {
		this.buffers = new GLMesh[buffers];
		for (int i = 0; i < buffers; i++) {
			this.buffers[i] = new GLMesh(VertexDataType.VertexArray, false,
					size * 4, size * 6, new GLAttributes.VertexAttribute(
							Usage.Position, 2, "POSITION"),
					new GLAttributes.VertexAttribute(Usage.ColorPacked, 4,
							"COLOR"), new GLAttributes.VertexAttribute(
							Usage.TextureCoordinates, 2, "TEXCOORD"));
		}
		this.vertices = new float[size * SPRITE_SIZE];
		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i + 0] = (short) (j + 0);
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = (short) (j + 0);
		}
		for (int i = 0; i < buffers; i++) {
			this.buffers[i].setIndices(indices);
		}
		this.mesh = this.buffers[0];
	}

	public void halfAlpha() {
		color = 1.7014117E38f;
		alpha = 0.5f;
	}

	public void resetColor() {
		color = -1.7014117E38f;
		alpha = 1f;
	}

	public static enum BlendState {
		Additive, AlphaBlend, NonPremultiplied, Opaque;
	}

	private BlendState lastBlendState = BlendState.NonPremultiplied;

	private int mode;

	public void begin() {
		if (drawing) {
			throw new IllegalStateException("Not implemented end !");
		}
		synchronized (SpriteBatch.class) {
			mode = GLEx.self.getBlendMode();
			GLEx.self.glTex2DEnable();
			renderCalls = 0;
			idx = 0;
			lastTexture = null;
			drawing = true;
		}
	}

	public void end() {
		checkDrawing();
		if (idx > 0) {
			submit();
		}
		lastTexture = null;
		idx = 0;
		drawing = false;
		GLEx.self.setBlendMode(mode);
		GLEx.self.glTex2DDisable();
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
		color = Float.intBitsToFloat(v & 0xfeffffff);
	}

	public void setColor(float color) {
		this.color = color;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		int intBits = Float.floatToRawIntBits(color);
		int r = (intBits & 0xff);
		int g = ((intBits >>> 8) & 0xff);
		int b = ((intBits >>> 16) & 0xff);
		int a = (int) (alpha * 255);
		color = LColor.toFloatBits(r, g, b, a);
	}

	public float getAlpha() {
		return alpha;
	}

	public LColor getColor() {
		int intBits = Float.floatToRawIntBits(color);
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

	private GLBatch batch = new GLBatch(1000);

	public void drawSpriteBounds(SpriteRegion sprite, LColor color) {
		float[] vertices = sprite.getVertices();

		float x1 = vertices[0];
		float y1 = vertices[1];

		float x2 = vertices[5];
		float y2 = vertices[6];

		float x3 = vertices[10];
		float y3 = vertices[11];

		float x4 = vertices[15];
		float y4 = vertices[16];

		setColor(color);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x4, y4);
		drawLine(x4, y4, x1, y1);
		resetColor();
	}

	public final void draw(Shape shape) {
		float[] points = shape.getPoints();
		if (points.length == 0) {
			return;
		}
		submit();
		LColor color = getColor();
		batch.begin(GL.GL_LINE_STRIP);
		for (int i = 0; i < points.length; i += 2) {
			batch.color(color);
			batch.vertex(points[i], points[i + 1]);
		}
		if (shape.closed()) {
			batch.color(color);
			batch.vertex(points[0], points[1]);
		}
		batch.end();
	}

	public final void fill(Shape shape) {
		if (shape == null) {
			return;
		}
		Triangle tris = shape.getTriangles();
		if (tris.getTriangleCount() == 0) {
			return;
		}
		submit();
		LColor color = getColor();
		batch.begin(GL.GL_TRIANGLES);
		for (int i = 0; i < tris.getTriangleCount(); i++) {
			for (int p = 0; p < 3; p++) {
				float[] pt = tris.getTrianglePoint(i, p);
				batch.color(color);
				batch.vertex(pt[0], pt[1]);
			}
		}
		batch.end();
	}

	public void fillPolygon(float xPoints[], float yPoints[], int nPoints) {
		submit();
		LColor color = getColor();
		batch.begin(GL.GL_POLYGON);
		for (int i = 0; i < nPoints; i++) {
			batch.color(color);
			batch.vertex(xPoints[i], yPoints[i]);
		}
		batch.end();
	}

	public void drawPolygon(float[] xPoints, float[] yPoints, int nPoints) {
		submit();
		LColor color = getColor();
		batch.begin(GL.GL_LINE_LOOP);
		for (int i = 0; i < nPoints; i++) {
			batch.color(color);
			batch.vertex(xPoints[i], yPoints[i]);
		}
		batch.end();
	}

	public void drawOval(float x1, float y1, float width, float height) {
		this.drawArc(x1, y1, width, height, 32, 0, 360);
	}

	public void fillOval(float x1, float y1, float width, float height) {
		this.fillArc(x1, y1, width, height, 32, 0, 360);
	}

	public void drawArc(RectBox rect, int segments, float start, float end) {
		drawArc(rect.x, rect.y, rect.width, rect.height, segments, start, end);
	}

	public void drawArc(float x1, float y1, float width, float height,
			int segments, float start, float end) {
		submit();
		LColor color = getColor();
		while (end < start) {
			end += 360;
		}
		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);
		batch.begin(GL.GL_LINE_STRIP);
		int step = 360 / segments;
		for (float a = start; a < (end + step); a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}
			float x = (cx + (MathUtils.cos(MathUtils.toRadians(ang)) * width / 2.0f));
			float y = (cy + (MathUtils.sin(MathUtils.toRadians(ang)) * height / 2.0f));
			batch.color(color);
			batch.vertex(x, y);
		}
		batch.end();
	}

	public final void fillArc(float x1, float y1, float width, float height,
			float start, float end) {
		fillArc(x1, y1, width, height, 40, start, end);
	}

	public final void fillArc(float x1, float y1, float width, float height,
			int segments, float start, float end) {
		submit();
		LColor color = getColor();
		while (end < start) {
			end += 360;
		}
		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);
		batch.begin(GL.GL_TRIANGLE_FAN);
		int step = 360 / segments;
		batch.vertex(cx, cy);
		for (float a = start; a < (end + step); a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}

			float x = (cx + (MathUtils.cos(MathUtils.toRadians(ang)) * width / 2.0f));
			float y = (cy + (MathUtils.sin(MathUtils.toRadians(ang)) * height / 2.0f));
			batch.color(color);
			batch.vertex(x, y);
		}
		batch.end();
	}

	public final void drawRoundRect(float x, float y, float width,
			float height, int radius) {
		drawRoundRect(x, y, width, height, radius, 40);
	}

	public final void drawRoundRect(float x, float y, float width,
			float height, int radius, int segs) {
		if (radius < 0) {
			throw new IllegalArgumentException("radius > 0");
		}
		if (radius == 0) {
			drawRect(x, y, width, height);
			return;
		}
		int mr = (int) MathUtils.min(width, height) / 2;
		if (radius > mr) {
			radius = mr;
		}
		drawLine(x + radius, y, x + width - radius, y);
		drawLine(x, y + radius, x, y + height - radius);
		drawLine(x + width, y + radius, x + width, y + height - radius);
		drawLine(x + radius, y + height, x + width - radius, y + height);
		float d = radius * 2;
		drawArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		drawArc(x, y + height - d, d, d, segs, 90, 180);
		drawArc(x + width - d, y, d, d, segs, 270, 360);
		drawArc(x, y, d, d, segs, 180, 270);
	}

	public final void fillRoundRect(float x, float y, float width,
			float height, int cornerRadius) {
		fillRoundRect(x, y, width, height, cornerRadius, 40);
	}

	public final void fillRoundRect(float x, float y, float width,
			float height, int radius, int segs) {
		if (radius < 0) {
			throw new IllegalArgumentException("radius > 0");
		}
		if (radius == 0) {
			fillRect(x, y, width, height);
			return;
		}
		int mr = (int) MathUtils.min(width, height) / 2;
		if (radius > mr) {
			radius = mr;
		}
		float d = radius * 2;
		fillRect(x + radius, y, width - d, radius);
		fillRect(x, y + radius, radius, height - d);
		fillRect(x + width - radius, y + radius, radius, height - d);
		fillRect(x + radius, y + height - radius, width - d, radius);
		fillRect(x + radius, y + radius, width - d, height - d);
		fillArc(x + width - d, y + height - d, d, d, segs, 0, 90);
		fillArc(x, y + height - d, d, d, segs, 90, 180);
		fillArc(x + width - d, y, d, d, segs, 270, 360);
		fillArc(x, y, d, d, segs, 180, 270);
	}

	public void fillRect(float x, float y, float width, float height) {
		LColor color = getColor();
		submit();
		batch.begin(GL.GL_TRIANGLE_FAN);
		{
			batch.color(color);
			batch.vertex(x, y);
			batch.color(color);
			batch.vertex(x + width, y);
			batch.color(color);
			batch.vertex(x + width, y + height);
			batch.color(color);
			batch.vertex(x, y + height);
		}
		batch.end();
	}

	// 因为效率关系，矩形区域绘制与GLEx类处理方式不同，改为纹理渲染
	public void drawRect(float x, float y, float width, float height) {
		drawLine(x, y, x + width, y);
		drawLine(x + width, y, x + width, y + height);
		drawLine(x + width, y + height, x, y + height);
		drawLine(x, y + height, x, y);
	}

	public void drawPoint(int x, int y, LColor c) {
		float old = color;
		setColor(c);
		drawLine(x, y, x + 1, y + 1);
		setColor(old);
	}

	public void drawPoints(int[] x, int[] y, LColor c) {
		int size = y.length;
		for (int i = 0; i < size; i++) {
			drawPoint(x[i], y[i], c);
		}
	}

	public void drawPoints(int[] x, int[] y) {
		int size = y.length;
		for (int i = 0; i < size; i++) {
			drawPoint(x[i], y[i]);
		}
	}

	public void drawPoint(int x, int y) {
		drawLine(x, y, x + 1, y + 1);
	}

	public void drawLine(float x1, float y1, float x2, float y2) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, x1);
		hashCode = LSystem.unite(hashCode, y1);
		hashCode = LSystem.unite(hashCode, x2);
		hashCode = LSystem.unite(hashCode, y2);
		TextureLine line = lineLazy.get(hashCode);
		if (line == null) {
			line = new TextureLine();
			line.setStart(x1, y1);
			line.setEnd(x2, y2);
			line.setStrokeWidth(1f);
			lineLazy.put(hashCode, line);
		}
		line.draw(this);
	}

	private void checkTexture(final LTexture texture) {
		checkDrawing();
		if (!texture.isLoaded()) {
			texture.loadTexture();
		}
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
		checkTexture(texture);

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

		checkTexture(texture);

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
		checkTexture(texture);

		float u = srcX * invTexWidth + texture.xOff;
		float v = srcY * invTexHeight + texture.yOff;
		float u2 = (srcX + srcWidth) * invTexWidth;
		float v2 = (srcY + srcHeight) * invTexHeight;
		final float fx2 = x + srcWidth;
		final float fy2 = y + srcHeight;

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
	}

	public void draw(LTexture texture, float x, float y) {
		if (texture == null) {
			return;
		}
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
		if (texture == null) {
			return;
		}
		checkTexture(texture);

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
	}

	public void draw(LTexture texture, float[] spriteVertices, int offset,
			int length) {

		checkTexture(texture);

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
		checkTexture(region.getTexture());

		final float fx2 = x + width;
		final float fy2 = y + height;
		final float u = region.xOff;
		final float v = region.yOff;
		final float u2 = region.widthRatio;
		final float v2 = region.heightRatio;

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
	}

	public void draw(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation) {

		checkTexture(region.getTexture());

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
	}

	public void draw(LTextureRegion region, float x, float y, float originX,
			float originY, float width, float height, float scaleX,
			float scaleY, float rotation, boolean clockwise) {

		checkTexture(region.getTexture());

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
	}

	private LFont font = LFont.getDefaultFont();

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public void drawString(LFont spriteFont, String text, float px, float py,
			LColor color, float rotation, float originx, float originy,
			float scale) {
		LFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = ((spriteFont.getHeight() - 2));
		if (rotation == 0f) {
			drawString(text, px - (originx * scale), (py + heigh)
					- (originy * scale), scale, scale, originx, originy,
					rotation, color);
		} else {
			drawString(text, px, (py + heigh), scale, scale, originx, originy,
					rotation, color);
		}
		setFont(old);
	}

	public void drawString(LFont spriteFont, String text, Vector2f position,
			LColor color, float rotation, Vector2f origin, float scale) {
		LFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = ((spriteFont.getHeight() - 2));
		if (rotation == 0f) {
			drawString(text, position.x - (origin.x * scale),
					(position.y + heigh) - (origin.y * scale), scale, scale,
					origin.x, origin.y, rotation, color);
		} else {
			drawString(text, position.x, (position.y + heigh), scale, scale,
					origin.x, origin.y, rotation, color);
		}
		setFont(old);
	}

	public void drawString(LFont spriteFont, String text, Vector2f position,
			LColor color) {
		LFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = (spriteFont.getHeight() - 2);
		drawString(text, position.x, (position.y + heigh), 1f, 1f, 0f, 0f, 0f,
				color);
		setFont(old);
	}

	public void drawString(LFont spriteFont, String text, float x, float y,
			LColor color) {
		LFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = (spriteFont.getHeight() - 2);
		drawString(text, x, (y + heigh), 1f, 1f, 0f, 0f, 0f, color);
		setFont(old);
	}

	public void drawString(LFont spriteFont, String text, Vector2f position,
			LColor color, float rotation, Vector2f origin, Vector2f scale) {
		LFont old = font;
		if (spriteFont != null) {
			setFont(spriteFont);
		}
		int heigh = ((spriteFont.getHeight() - 2));
		if (rotation == 0f) {
			drawString(text, position.x - (origin.x * scale.x),
					(position.y + heigh) - (origin.y * scale.y), scale.x,
					scale.y, origin.x, origin.y, rotation, color);
		} else {
			drawString(text, position.x, (position.y + heigh), scale.x,
					scale.y, origin.x, origin.y, rotation, color);
		}
		setFont(old);
	}

	private boolean lockSubmit = false;

	public void drawString(String mes, float x, float y, float scaleX,
			float scaleY, float ax, float ay, float rotation, LColor c) {
		if (!drawing) {
			throw new IllegalStateException("Not implemented begin !");
		}
		if (c == null) {
			return;
		}
		if (mes == null || mes.length() == 0) {
			return;
		}
		if (!lockSubmit) {
			submit();
		}
		y = y + font.getAscent();
		LSTRDictionary.drawString(font, mes, x, y, scaleX, scaleX, ax, ay,
				rotation, c);
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

	private void checkDrawing() {
		if (!drawing) {
			throw new IllegalStateException("Not implemented begin !");
		}
	}

	public void flush() {
		submit();
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
		} else {
			GLEx.self.GL_REPLACE();
		}
		if (color == -1.7014117E38f) {
			GLEx.self.GL_MODULATE();
		} else {
			GLEx.self.GL_REPLACE();
		}
	}

	public void flush(BlendState state) {
		submit(state);
	}

	private void submit() {
		submit(lastBlendState);
	}

	private void submit(BlendState state) {
		if (idx == 0) {
			return;
		}
		synchronized (SpriteBatch.class) {
			renderCalls++;
			totalRenderCalls++;
			int spritesInBatch = idx / 20;
			if (spritesInBatch > maxSpritesInBatch) {
				maxSpritesInBatch = spritesInBatch;
			}
			GLEx.self.bind(lastTexture);
			mesh.setVertices(vertices, 0, idx);
			mesh.getIndicesBuffer().position(0);
			mesh.getIndicesBuffer().limit(spritesInBatch * 6);
			setBlendState(state);
			mesh.render(GL.GL_TRIANGLES, 0, spritesInBatch * 6);
			idx = 0;
			currBufferIdx++;
			if (currBufferIdx == buffers.length) {
				currBufferIdx = 0;
			}
			mesh = buffers[currBufferIdx];
		}
	}

	public boolean isLockSubmit() {
		return lockSubmit;
	}

	public void setLockSubmit(boolean lockSubmit) {
		this.lockSubmit = lockSubmit;
	}

	@Override
	public void dispose() {
		for (int i = 0; i < buffers.length; i++) {
			buffers[i].dispose();
		}
		if (lineLazy != null) {
			lineLazy.clear();
		}
		if (whitePixel != null) {
			whitePixel.destroy();
		}
		if (batch != null) {
			batch.dispose();
		}
	}

}
