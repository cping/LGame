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
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.RectF;
import loon.opengl.GLEx;
import loon.utils.TArray;

/**
 * 网格线专用Entity,用于生成一组指定大小,粗细,颜色的网格到游戏中显示(地图显示网格之类时使用)
 *
 * <pre>
 * add(new GridEntity(0, 0, 480, 320, 32, 32, LColor.yellow));
 * </pre>
 */
public class GridEntity extends Entity {

	private RectBox _gridRect;

	private TArray<RectF> _gridLines;

	private float _gridScale;

	private float _cellWidth;

	private float _cellHeight;

	private float _lineWidth;

	private boolean _drity;

	private boolean _drawCache;

	private boolean _alltextures;

	public GridEntity() {
		this(LColor.green);
	}

	public GridEntity(LColor color) {
		this(LSystem.viewSize.getRect(), LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(float x, float y, float w, float h, LColor color) {
		this(RectBox.at(x, y, w, h), LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(float x, float y, float w, float h, int cellW, int cellH, LColor color) {
		this(RectBox.at(x, y, w, h), cellW, cellH, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(RectBox viewRect, LColor color) {
		this(viewRect, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(RectBox viewRect, float scale, float lineWidth, LColor color) {
		this(viewRect, LSystem.LAYER_TILE_SIZE, LSystem.LAYER_TILE_SIZE, 0f, 0f, scale, lineWidth, color);
	}

	public GridEntity(RectBox viewRect, int cellWidth, int cellHeight, float scale, float lineWidth, LColor color) {
		this(viewRect, cellWidth, cellHeight, 0f, 0f, scale, lineWidth, color);
	}

	public GridEntity(RectBox viewRect, int cellWidth, int cellHeight, float offsetX, float offsetY, float scale,
			float lineWidth, LColor color) {
		this.setOffset(offsetX, offsetY);
		this.setLocation(viewRect.x, viewRect.y);
		this.setSize(viewRect.width, viewRect.height);
		this.setColor(color);
		this._cellWidth = cellWidth;
		this._cellHeight = cellHeight;
		this._gridRect = new RectBox(viewRect.x / _cellWidth, viewRect.y / _cellHeight, viewRect.width / _cellWidth,
				viewRect.height / _cellHeight);
		this._gridScale = scale;
		this._lineWidth = lineWidth;
		this._drawCache = true;
		this._alltextures = true;
		this._repaintDraw = true;
		_drity = true;
	}

	public GridEntity pack() {
		if (_drity) {
			if (_drawCache && _image != null) {
				_image.close();
			}
			if (_gridLines == null) {
				_gridLines = new TArray<>();
			} else {
				_gridLines.clear();
			}
			for (int x = 0; x < _gridRect.width() + 1; x++) {
				_gridLines.add(new RectF(x * _cellWidth * _gridScale, 0, x * _cellWidth * _gridScale,
						_gridRect.height() * _cellHeight * _gridScale));
			}
			for (int y = 0; y < _gridRect.height() + 1; y++) {
				_gridLines.add(new RectF(0, y * _cellHeight * _gridScale, _gridRect.width() * _cellWidth * _gridScale,
						y * _cellHeight * _gridScale));
			}
			_drity = false;
		}
		return this;
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		pack();
		if (_drawCache) {
			if (_image == null || _image.isClosed()) {
				Image img = Image.createImage(width(), height());
				Canvas canvas = img.getCanvas();
				draw(canvas, 0, 0);
				_image = img.texture();
			} else {
				g.draw(_image, drawX(offsetX), drawY(offsetY));
			}
		} else {
			draw(g, offsetX, offsetY);
		}
	}

	protected void draw(Canvas g, float offsetX, float offsetY) {
		int tint = g.getFillColor();
		g.setStrokeWidth(_lineWidth);
		g.setColor(_baseColor);
		for (int i = 0; i < _gridLines.size; i++) {
			RectF line = _gridLines.get(i);
			g.drawLine(drawX(line.x + offsetX), drawY(line.y + offsetY), drawX(line.width + offsetX),
					drawY(line.height + offsetY));
		}
		g.setFillColor(tint);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		float lw = g.getLineWidth();
		int tint = g.color();
		g.setLineWidth(_lineWidth);
		g.setColor(_baseColor);
		for (int i = 0; i < _gridLines.size; i++) {
			RectF line = _gridLines.get(i);
			g.drawLine(drawX(line.x + offsetX), drawY(line.y + offsetY), drawX(line.width + offsetX),
					drawY(line.height + offsetY));
		}
		g.setLineWidth(lw);
		g.setTint(tint);
	}

	public TArray<RectF> getGridLines() {
		return _gridLines.cpy();
	}

	public boolean isDrity() {
		return _drity;
	}

	public GridEntity setDrity(boolean d) {
		this._drity = d;
		return this;
	}

	public boolean isAlltextures() {
		return _alltextures;
	}

	public GridEntity setAlltextures(boolean alltextures) {
		this._alltextures = alltextures;
		return this;
	}

	public boolean isDrawCache() {
		return _drawCache;
	}

	public GridEntity setDrawCache(boolean drawCache) {
		this._drawCache = drawCache;
		return this;
	}

	public float getCellWidth() {
		return _cellWidth;
	}

	public GridEntity setCellWidth(float w) {
		this._cellWidth = w;
		this.setDrity(true);
		return this;
	}

	public float getCellHeight() {
		return _cellHeight;
	}

	public GridEntity setCellHeight(float h) {
		this._cellHeight = h;
		this.setDrity(true);
		return this;
	}

	public float getLineWidth() {
		return _lineWidth;
	}

	public GridEntity setLineWidth(float lineWidth) {
		this._lineWidth = lineWidth;
		this.setDrity(true);
		return this;
	}

}
