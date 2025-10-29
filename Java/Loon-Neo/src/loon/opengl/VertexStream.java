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
package loon.opengl;

import loon.LRelease;
import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.Clip;
import loon.geom.RectF;
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ShortArray;

/**
 * 纹理顶点坐标处理用工具类，用于顶点数据和三角形数据相关的不规则形状添加
 */
public class VertexStream implements LRelease {

	public final static void addTile(VertexStream vs, boolean repeatX, boolean repeatY) {
		LTexture tex = vs._curTex;
		createTile(vs, vs._contentRect, vs._uvRect, tex.getWidth(), tex.getHeight(), repeatX, repeatY);
	}

	public final static void createTile(VertexStream vs, RectF drawRect, RectF uvRect, float sourceW, float sourceH,
			boolean repeatX, boolean repeatY) {

		final float hc = repeatX ? (MathUtils.ceil(drawRect.width / sourceW) - 1) : 0;
		final float vc = repeatY ? (MathUtils.ceil(drawRect.height / sourceH) - 1) : 0;
		final float tailWidth = drawRect.width - hc * sourceW;
		final float tailHeight = drawRect.height - vc * sourceH;

		final RectF tmpRect = new RectF();
		final RectF tmpUV = new RectF();

		final int qi = vs.getVertCount();

		for (int i = 0; i <= hc; i++) {
			for (int j = 0; j <= vc; j++) {
				tmpRect.set(drawRect.x + i * sourceW, drawRect.y + j * sourceH, (i < hc) ? sourceW : tailWidth,
						(j < vc) ? sourceH : tailHeight);

				tmpUV.set(uvRect.x, uvRect.y, (i < hc || !repeatX) ? uvRect.width : uvRect.width * tailWidth / sourceW,
						(j < vc || !repeatY) ? uvRect.height : uvRect.height * tailHeight / sourceH);

				vs.addQuad(tmpRect, null, tmpUV);
			}
		}
		vs.triangulateQuad(qi);

	}

	public final static void addRoundedRect(VertexStream vs) {
		addRoundedRect(vs, 6, 6, 6, 6);
	}

	public final static void addRoundedRect(VertexStream vs, float rb, float lb, float lt, float rt) {
		float x = vs._contentRect.x;
		float y = vs._contentRect.y;
		float w = vs._contentRect.width;
		float h = vs._contentRect.height;
		float radiusX = w / 2;
		float radiusY = h / 2;
		float cornerMaxRadius = MathUtils.min(radiusX, radiusY);
		float centerX = x + radiusX;
		float centerY = y + radiusY;

		vs.addVert(centerX, centerY);

		int cnt = vs.getVertCount();
		for (int i = 0; i < 4; i++) {
			float radius = 0;
			switch (i) {
			case 0:
				radius = rb;
				break;
			case 1:
				radius = lb;
				break;
			case 2:
				radius = lt;
				break;
			case 3:
				radius = rt;
				break;
			}
			radius = MathUtils.min(cornerMaxRadius, radius);

			float offsetX = 0;
			float offsetY = 0;

			if (i == 0 || i == 3) {
				offsetX = w - radius * 2;
			}
			if (i == 0 || i == 1) {
				offsetY = h - radius * 2;
			}

			offsetX += x;
			offsetY += y;

			if (radius != 0) {
				int partNumSides = MathUtils.max(1, MathUtils.ceil(MathUtils.PI * radius / 8)) + 1;
				float angleDelta = MathUtils.PI / 2 / partNumSides;
				float angle = MathUtils.PI / 2 * i;
				float startAngle = angle;

				for (int j = 1; j <= partNumSides; j++) {
					if (j == partNumSides) {
						angle = startAngle + MathUtils.PI / 2;
					}
					vs.addVert(offsetX + MathUtils.cos(angle) * radius + radius,
							offsetY + MathUtils.sin(angle) * radius + radius);
					angle += angleDelta;
				}
			} else {
				vs.addVert(offsetX, offsetY);
			}
		}
		cnt = vs.getVertCount() - cnt;

		for (int i = 0; i < cnt; i++) {
			vs.addTriangle((short) 0, (short) (i + 1), (short) (i == cnt - 1 ? 1 : i + 2));
		}
	}

	public final static void addCircle(VertexStream vs) {
		RectF rect = vs._contentRect;
		float radiusX = rect.width / 2;
		float radiusY = rect.height / 2;
		int sides = MathUtils.ceil(MathUtils.PI * (radiusX + radiusY) / 4);
		float angleDelta = 2f * MathUtils.PI / sides;
		float angle = 0;
		float centerX = rect.x + radiusX;
		float centerY = rect.y + radiusY;
		vs.addVert(centerX, centerY);
		for (int i = 0; i < sides; i++) {
			float vx = MathUtils.cos(angle) * radiusX + centerX;
			float vy = MathUtils.sin(angle) * radiusY + centerY;
			vs.addVert(vx, vy);
			angle += angleDelta;
		}

		for (int i = 0; i < sides; i++) {
			if (i != sides - 1) {
				vs.addTriangle((short) 0, (short) (i + 1), (short) (i + 2));
			} else {
				vs.addTriangle((short) 0, (short) (i + 1), (short) 1);
			}
		}
	}

	public final static void addPolygon(VertexStream vs, LColor fillColor, LColor lineColor, LColor centerColor,
			float rotation, float lineWidth, int sides) {
		LColor color = fillColor == null ? vs._color : fillColor;
		LColor lcolor = lineColor == null ? vs._color : lineColor;

		float angleDelta = 2 * MathUtils.PI / sides;
		float angle = MathUtils.degToRad(rotation);
		float radius = MathUtils.min(vs._contentRect.width / 2, vs._contentRect.height / 2);

		float centerX = radius + vs._contentRect.x;
		float centerY = radius + vs._contentRect.y;
		vs.addVert(centerX, centerY, centerColor == null ? color : centerColor);
		for (int i = 0; i < sides; i++) {
			float r = radius;
			float xv = centerX + MathUtils.cos(angle) * (r - lineWidth);
			float yv = centerY + MathUtils.sin(angle) * (r - lineWidth);
			vs.addVert(xv, yv, color);
			if (lineWidth > 0) {
				vs.addVert(xv, yv, lcolor);
				vs.addVert(MathUtils.cos(angle) * r + centerX, MathUtils.sin(angle) * r + centerY, lcolor);
			}
			angle += angleDelta;
		}

		if (lineWidth > 0) {
			int tmp = sides * 3;
			for (int i = 0; i < tmp; i += 3) {
				if (i != tmp - 3) {
					vs.addTriangle((short) 0, (short) ((i + 1)), (short) (i + 4));
					vs.addTriangle((short) (i + 5), (short) (i + 2), (short) (i + 3));
					vs.addTriangle((short) (i + 3), (short) (i + 6), (short) (i + 5));
				} else {
					vs.addTriangle((short) 0, (short) (i + 1), (short) 1);
					vs.addTriangle((short) 2, (short) (i + 2), (short) (i + 3));
					vs.addTriangle((short) (i + 3), (short) 3, (short) 2);
				}
			}
		} else {
			for (int i = 0; i < sides; i++) {
				vs.addTriangle((short) 0, (short) (i + 1), (short) ((i == sides - 1) ? 1 : i + 2));
			}
		}
	}

	private final static int ADDED_VERTEX_COUNT = 5;

	private final ExpandVertices _vertices;

	private final ShortArray _indices;

	private final Vector2f _tempPos;

	private final RectF _contentRect;

	private final RectF _uvRect;

	private final LColor _color;

	private LTexture _curTex;

	private int _vindex;

	private int _iindex;

	public VertexStream() {
		this(80, 30);
	}

	public VertexStream(int vertCount, int indiceCount) {
		this._contentRect = new RectF();
		this._uvRect = new RectF();
		this._color = new LColor();
		this._vertices = new ExpandVertices(vertCount);
		this._indices = new ShortArray(indiceCount);
		this._tempPos = new Vector2f();
	}

	public void setContextRect(float x, float y, float w, float h) {
		_contentRect.set(x, y, w, h);
	}

	public void setTexture(LTexture m) {
		_curTex = m;
		if (_curTex != null) {
			final Clip clip = _curTex.getClip();
			if (clip.getDisplayWidth() == clip.getRegionWidth() && clip.getDisplayHeight() == clip.getRegionHeight()) {
				this._uvRect.set(clip.xOff(), clip.yOff(), clip.widthRatio(), clip.heightRatio());
			} else {
				float sx = clip.widthRatio() / _curTex.getWidth();
				float sy = clip.heightRatio() / _curTex.getHeight();
				this._uvRect.set(clip.xOff() - _curTex.xOff() * sx, clip.yOff() - _curTex.yOff() * sy,
						_curTex.getWidth() * sx, _curTex.getHeight() * sy);
			}
		} else {
			this._uvRect.set(0f, 0f, 1f, 1f);
		}
		this._vindex = 0;
		this._iindex = 0;
	}

	public void addVert(float x, float y) {
		addVert(x, y, _color);
	}

	public void addVert(float x, float y, LColor color) {
		addVert(x, y, color, -1f, -1f);
	}

	public void addVert(float x, float y, float u, float v) {
		addVert(x, y, _color, -1f, -1f);
	}

	public void addVert(float x, float y, LColor color, float u, float v) {

		int idx = this._vindex;

		this._vertices.setVertice(idx, x);
		this._vertices.setVertice(idx + 1, y);

		if (color != null) {
			this._vertices.setVertice(idx + 2, color.toFloatBits());
		} else {
			this._vertices.setVertice(idx + 2, LColor.white.toFloatBits());
		}

		if (u != -1) {
			this._vertices.setVertice(idx + 3, u);
		} else {
			this._vertices.setVertice(idx + 3, MathUtils.lerp(this._uvRect.x, this._uvRect.getRight(),
					(x - this._contentRect.x) / (this._contentRect.width)));
		}
		if (v != -1) {
			this._vertices.setVertice(idx + 4, v);
		} else {
			this._vertices.setVertice(idx + 4, MathUtils.lerp(this._uvRect.y, this._uvRect.getBottom(),
					(y - this._contentRect.y) / (this._contentRect.height)));
		}

		_vindex += ADDED_VERTEX_COUNT;
	}

	private void updateIndice(int addCount) {
		if (this._iindex + addCount < this._indices.length) {
			return;
		}
		int ip = this._iindex + MathUtils.max(30, addCount);
		this._indices.ensureCapacity(ip);
	}

	public void addQuad(RectF rect, LColor color, RectF uvRect) {
		if (uvRect != null) {
			this.addVert(rect.x, rect.y, color, uvRect.x, uvRect.y);
			this.addVert(rect.getRight(), rect.y, color, uvRect.getRight(), uvRect.y);
			this.addVert(rect.getRight(), rect.getBottom(), color, uvRect.getRight(), uvRect.getBottom());
			this.addVert(rect.x, rect.getBottom(), color, uvRect.x, uvRect.getBottom());
		} else {
			this.addVert(rect.x, rect.y, color, -1, -1);
			this.addVert(rect.getRight(), rect.y, color, -1, -1);
			this.addVert(rect.getRight(), rect.getBottom(), color, -1, -1);
			this.addVert(rect.x, rect.getBottom(), color, -1, -1);
		}
	}

	public void addTriangle(short idx0, short idx1, short idx2) {
		this.updateIndice(3);

		int idx = this._iindex;
		this._iindex += 3;

		this._indices.set(idx, idx0);
		this._indices.set(idx + 1, idx1);
		this._indices.set(idx + 2, idx2);
	}

	public void addTriangles(short[] indices) {
		this.updateIndice(indices.length);
		ShortArray arr = this._indices;
		int idx = this._iindex;
		int n = indices.length;
		this._iindex += n;
		for (int i = 0; i < n; i++) {
			arr.set(idx + i, indices[i]);
		}
	}

	public void addTriangles(ShortArray indices) {
		this.updateIndice(indices.length);
		ShortArray arr = this._indices;
		int idx = this._iindex;
		int n = indices.length;
		this._iindex += n;
		for (int i = 0; i < n; i++) {
			arr.set(idx + i, indices.get(i));
		}
	}

	public void triangulateQuad(int baseIndex) {
		int cnt = this._vindex / ADDED_VERTEX_COUNT;
		if (baseIndex < 0) {
			baseIndex = cnt + baseIndex;
		}
		int icnt = (cnt - baseIndex) / 4 * 6;
		this.updateIndice(icnt);
		ShortArray arr = this._indices;
		for (int i = baseIndex, j = this._iindex; i < cnt; i += 4, j += 6) {
			arr.set(j, (short) i);
			arr.set(j + 1, (short) (i + 1));
			arr.set(j + 2, (short) (i + 2));
			arr.set(j + 3, (short) (i + 2));
			arr.set(j + 4, (short) (i + 3));
			arr.set(j + 5, (short) i);
		}
		this._iindex += icnt;
	}

	public VertexStream clear() {
		_vertices.clear();
		_indices.clear();
		_vindex = 0;
		_iindex = 0;
		return this;
	}

	public LColor getColor() {
		return _color;
	}

	public void setColor(float r, float g, float b, float a) {
		_color.setColor(r, g, b, a);
	}

	public void setColor(LColor c) {
		_color.setColor(c);
	}

	public Vector2f getPos(int index) {
		if (index < 0) {
			index = this._vindex / ADDED_VERTEX_COUNT + index;
		}
		index *= ADDED_VERTEX_COUNT;
		return _tempPos.set(this._vertices.getVertices(index), this._vertices.getVertices(index + 1));
	}

	public int getVertIndexCount() {
		return _vindex / _vertices.vertexSize();
	}

	public int getVertCount() {
		return _vindex / ADDED_VERTEX_COUNT;
	}

	public int getVertSize() {
		return _vindex;
	}

	public int getIndiSize() {
		return _iindex;
	}

	public RectF getContentRect() {
		return _contentRect;
	}

	public RectF getUVRect() {
		return _uvRect;
	}

	public short[] getIndicesData() {
		return _indices.items;
	}

	public float[] getVerticesData() {
		return _vertices.getVertices();
	}

	public float[] getVertices() {
		return _vertices.cpy(_vindex);
	}

	public short[] getIndices() {
		return CollectionUtils.copyOf(_indices.items, _iindex);
	}

	public LTexture getTexture() {
		return _curTex;
	}

	@Override
	public void close() {
		_vertices.close();
		_indices.close();
		if (_curTex != null) {
			_curTex.close();
		}
	}

}
