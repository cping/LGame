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
import loon.geom.AABB;
import loon.geom.RectBox;
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

	private RectBox gridRect;

	private TArray<AABB> gridLines;

	private float gridScale;

	private float cellWidth;

	private float cellHeight;

	private float lineWidth;

	private boolean drity;

	private boolean drawCache;

	private boolean alltextures;

	public GridEntity() {
		this(LColor.green);
	}
	
	public GridEntity(LColor color) {
		this(LSystem.viewSize.getRect(), 32, 32, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(float x, float y, float w, float h, LColor color) {
		this(RectBox.at(x, y, w, h), 32, 32, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(float x, float y, float w, float h, int cellW, int cellH, LColor color) {
		this(RectBox.at(x, y, w, h), cellW, cellH, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(RectBox viewRect, LColor color) {
		this(viewRect, 32, 32, 0f, 0f, 1f, 1f, color);
	}

	public GridEntity(RectBox viewRect, float scale, float lineWidth, LColor color) {
		this(viewRect, 32, 32, 0f, 0f, scale, lineWidth, color);
	}

	public GridEntity(RectBox viewRect, int cellWidth, int cellHeight, float scale, float lineWidth, LColor color) {
		this(viewRect, cellWidth, cellHeight, 0f, 0f, scale, lineWidth, color);
	}

	public GridEntity(RectBox viewRect, int cellWidth, int cellHeight, float offsetX, float offsetY, float scale,
			float lineWidth, LColor color) {
		super();
		this.setOffset(offsetX, offsetY);
		this.setLocation(viewRect.x, viewRect.y);
		this.setSize(viewRect.width, viewRect.height);
		this.setColor(color);
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.gridRect = new RectBox(viewRect.x / cellWidth, viewRect.y / cellHeight, viewRect.width / cellWidth,
				viewRect.height / cellHeight);
		this.gridScale = scale;
		this.lineWidth = lineWidth;
		this.drawCache = true;
		this.alltextures = true;
		this._repaintDraw = true;
		drity = true;
	}

	public void pack() {
		if (drity) {
			if (drawCache && _image != null) {
				_image.close();
			}
			if (gridLines == null) {
				gridLines = new TArray<AABB>();
			} else {
				gridLines.clear();
			}
			for (int x = 0; x < gridRect.width() + 1; x++) {
				gridLines.add(new AABB(x * cellWidth * gridScale, 0, x * cellWidth * gridScale,
						gridRect.height() * cellHeight * gridScale));
			}
			for (int y = 0; y < gridRect.height() + 1; y++) {
				gridLines.add(new AABB(0, y * cellHeight * gridScale, gridRect.width() * cellWidth * gridScale,
						y * cellHeight * gridScale));
			}
			drity = false;
		}
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		pack();
		if (drawCache) {
			if (_image == null || _image.isClosed()) {
				Image img = Image.createImage(width(), height());
				Canvas canvas = img.getCanvas();
				draw(canvas, 0, 0);
				_image = img.onHaveToClose(true).texture();
			} else {
				g.draw(_image, drawX(offsetX), drawY(offsetY));
			}
			return;
		}
		draw(g, offsetX, offsetY);
	}

	protected void draw(Canvas g, float offsetX, float offsetY) {
		int tint = g.getFillColor();
		g.setStrokeWidth(lineWidth);
		g.setColor(_baseColor);
		for (int i = 0; i < gridLines.size; i++) {
			AABB line = gridLines.get(i);
			g.drawLine(drawX(line.minX + offsetX), drawY(line.minY + offsetY), drawX(line.maxX + offsetX),
					drawY(line.maxY + offsetY));
		}
		g.setFillColor(tint);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		boolean allTex = g.isAlltextures();
		float lw = g.getLineWidth();
		int tint = g.color();
		g.setAlltextures(alltextures);
		g.setLineWidth(lineWidth);
		g.setColor(_baseColor);
		for (int i = 0; i < gridLines.size; i++) {
			AABB line = gridLines.get(i);
			g.drawLine(drawX(line.minX + offsetX), drawY(line.minY + offsetY), drawX(line.maxX + offsetX),
					drawY(line.maxY + offsetY));
		}
		g.setAlltextures(allTex);
		g.setLineWidth(lw);
		g.setTint(tint);
	}

	public TArray<AABB> getGridLines() {
		return gridLines.cpy();
	}

	public boolean isDrity() {
		return drity;
	}

	public void setDrity(boolean drity) {
		this.drity = drity;
	}

	public boolean isAlltextures() {
		return alltextures;
	}

	public void setAlltextures(boolean alltextures) {
		this.alltextures = alltextures;
	}

	public boolean isDrawCache() {
		return drawCache;
	}

	public void setDrawCache(boolean drawCache) {
		this.drawCache = drawCache;
	}

	public float getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(float cellWidth) {
		this.cellWidth = cellWidth;
		this.setDrity(true);
	}

	public float getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(float cellHeight) {
		this.cellHeight = cellHeight;
		this.setDrity(true);
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
		this.setDrity(true);
	}

}
