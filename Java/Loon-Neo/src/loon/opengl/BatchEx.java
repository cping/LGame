/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.opengl;

import loon.canvas.LColor;
import loon.canvas.PixmapFImpl;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.utils.FloatArray;
import loon.utils.MathUtils;

public abstract class BatchEx<T> extends PixmapFImpl {

	private FloatArray _shapes = new FloatArray();

	public BatchEx(float tx, float ty, RectBox clip, float w, float h, int skip) {
		super(tx, ty, clip, w, h, skip);
	}

	public final BatchEx<T> shape(Polygon p) {
		shape(p.getPoints(), p.size());
		return this;
	}

	public final BatchEx<T> shape(FloatArray vertices) {
		shape(vertices.items, vertices.size());
		return this;
	}

	public final BatchEx<T> shape(float[] vertices, int length) {
		if (length < 2 * 3) {
			return this;
		}
		for (int i = 2; i < length - 4; i += 4) {
			quad(vertices[0], vertices[1], vertices[i], vertices[i + 1], vertices[i + 2], vertices[i + 3],
					vertices[i + 4], vertices[i + 5]);
		}
		return this;
	}

	public final BatchEx<T> light(float x, float y, int sides, float start, LColor center, LColor edge, float alpha) {
		return light(x, y, sides, start, 0, center, edge, alpha);
	}

	public final BatchEx<T> light(float x, float y, int sides, float start, float end, LColor center, LColor edge,
			float alpha) {
		float centerf = center.cpy().setAlpha(alpha).toFloatBits(), edgef = edge.cpy().setAlpha(alpha).toFloatBits();
		x = x + start;
		y = y + start;
		sides = MathUtils.ceil(sides / 2f) * 2;
		float space = 360f / sides;
		for (int i = 0; i < sides; i += 2) {
			float px = MathUtils.translateX(space * i + end, start);
			float py = MathUtils.translateY(space * i + end, start);
			float px2 = MathUtils.translateX(space * (i + 1) + end, start);
			float py2 = MathUtils.translateY(space * (i + 1) + end, start);
			float px3 = MathUtils.translateX(space * (i + 2) + end, start);
			float py3 = MathUtils.translateY(space * (i + 2) + end, start);
			quad(x, y, centerf, x + px, y + py, edgef, x + px2, y + py2, edgef, x + px3, y + py3, edgef);
		}
		return this;
	}

	public final BatchEx<T> shadow(float x, float y, float width, float height, float blur, float opacity,
			float color) {
		x = x + width / 2;
		y = y + height / 2;
		final float center = LColor.toFloatBits(0, 0, 0, opacity);
		final float inside = blur / 2f, outside = blur;
		final float x1 = x - MathUtils.max(width / 2f - inside, 0), y1 = y - MathUtils.max(height / 2f - inside, 0);
		final float x2 = x + MathUtils.max(width / 2f - inside, 0), y2 = y + MathUtils.max(height / 2f - inside, 0);
		final float bx1 = x1 - outside, by1 = y1 - outside, bx2 = x2 + outside, by2 = y2 + outside;
		quad(x1, y1, center, x2, y1, center, x2, y2, center, x1, y2, center);
		quad(x1, y1, center, bx1, by1, color, bx2, by1, color, x2, y1, center);
		quad(x2, y1, center, bx2, by1, color, bx2, by2, color, x2, y2, center);
		quad(x1, y2, center, bx1, by2, color, bx2, by2, color, x2, y2, center);
		quad(x1, y1, center, bx1, by1, color, bx1, by2, color, x1, y2, center);
		return this;
	}

	public final BatchEx<T> triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		return quad(x1, y1, x2, y2, x3, y3, x3, y3);
	}

	public final BatchEx<T> line(float x1, float y1, float x2, float y2, float width) {
		final float nw = width / 2f;
		final float dx = x2 - x1, dy = y2 - y1;
		final float len = MathUtils.mag(dx, dy);
		final float wx = dx / len * nw, wy = dy / len * nw;
		return quad(x1 - wx - wy, y1 - wy + wx, x1 - wx + wy, y1 - wy - wx, x2 + wx + wy, y2 + wy - wx, x2 + wx - wy,
				y2 + wy + wx

		);
	}

	public final BatchEx<T> shapeBegin() {
		_shapes.clear();
		return this;
	}

	public final BatchEx<T> shapePoint(float x, float y) {
		_shapes.add(x, y);
		return this;
	}

	public final BatchEx<T> shapeEnd() {
		shape(_shapes.items, _shapes.size());
		return this;
	}

	public final void shape(float x, float y, float start) {
		shape(x, y, 32, start, 0f);
	}

	public final void shape(float x, float y, int sides, float start) {
		shape(x, y, sides, start, 0f);
	}

	public final void shape(float x, float y, int sides, float start, float end) {
		start /= 2;
		x += start;
		y += start;
		if (sides == 3) {
			float space = 360f / 3;
			float px = MathUtils.translateX(end, start);
			float py = MathUtils.translateY(end, start);
			float px2 = MathUtils.translateX(space + end, start);
			float py2 = MathUtils.translateY(space + end, start);
			float px3 = MathUtils.translateX(space * (2) + end, start);
			float py3 = MathUtils.translateY(space * (2) + end, start);
			triangle(x + px, y + py, x + px2, y + py2, x + px3, y + py3);
		} else {
			float space = 360f / sides;
			for (int i = 0; i < sides - 1; i += 2) {
				float px = MathUtils.translateX(space * i + end, start);
				float py = MathUtils.translateY(space * i + end, start);
				float px2 = MathUtils.translateX(space * (i + 1) + end, start);
				float py2 = MathUtils.translateY(space * (i + 1) + end, start);
				float px3 = MathUtils.translateX(space * (i + 2) + end, start);
				float py3 = MathUtils.translateY(space * (i + 2) + end, start);
				quad(x, y, x + px, y + py, x + px2, y + py2, x + px3, y + py3);
			}
			int mod = sides % 2;
			if (mod == 0 || sides < 4) {
				return;
			}
			int i = sides - 1;
			float px = MathUtils.translateX(space * i + end, start);
			float py = MathUtils.translateY(space * i + end, start);
			float px2 = MathUtils.translateX(space * (i + 1) + end, start);
			float py2 = MathUtils.translateY(space * (i + 1) + end, start);
			triangle(x, y, x + px, y + py, x + px2, y + py2);
		}
	}

	public final void arcToFill(float x, float y, float start) {
		arcToFill(x, y, start, 0f);
	}

	public final void arcToFill(float x, float y, float start, float end) {
		arcToFill(x, y, start, end, 32);
	}

	public final void arcToFill(float x, float y, float start, float end, int sides) {
		start /= 2;
		x += start;
		y += start;
		shapeBegin();
		shapePoint(x, y);
		for (int i = 0; i <= sides; i++) {
			float a = (float) i / sides * 360f + end;
			float x1 = MathUtils.translateX(a, start);
			float y1 = MathUtils.translateY(a, start);
			shapePoint(x + x1, y + y1);
		}
		shapePoint(x, y);
		shapeEnd();
	}

	@Override
	protected void drawLineImpl(float x1, float y1, float x2, float y2) {
		line(x1, y1, x2, y2, 1f);
	}

	public abstract BatchEx<T> quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

	public abstract BatchEx<T> quad(float x1, float y1, float c1, float x2, float y2, float c2, float x3, float y3,
			float c3, float x4, float y4, float c4);

}
