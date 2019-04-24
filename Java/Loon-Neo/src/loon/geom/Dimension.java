/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.geom;

import loon.action.map.Field2D;

public class Dimension {

	public float width = -1, height = -1;

	private Matrix4 matrix4;

	private RectBox rect;

	private boolean dirty = false;

	public Dimension() {
		this(-1, -1);
	}

	public Dimension(float w, float h) {
		width = w;
		height = h;
		dirty = true;
	}

	public Dimension(Dimension d) {
		width = d.getWidth();
		height = d.getHeight();
		dirty = true;
	}

	public RectBox getRect() {
		if (rect == null) {
			rect = new RectBox(0, 0, width, height);
		} else {
			rect.setBounds(0, 0, width, height);
		}
		return rect;
	}

	public Matrix4 getMatrix() {
		if (dirty) {
			if (matrix4 == null) {
				matrix4 = new Matrix4();
			}
			matrix4.setToOrtho2D(0, 0, width, height);
			dirty = false;
		}
		return matrix4;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean contains(float x, float y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public float height() {
		return height;
	}

	public float width() {
		return width;
	}

	public Field2D newField2D() {
		return newField2D(16, 16);
	}

	public Field2D newField2D(int tileWidth, int tileHeight) {
		int w = getWidth() / tileWidth;
		int h = getHeight() / tileHeight;
		int[][] tmp = new int[h][w];
		Field2D field2d = new Field2D(tmp, tileWidth, tileHeight);
		return field2d;
	}

	public int getHeight() {
		return (int) height;
	}

	public int getWidth() {
		return (int) width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.dirty = true;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.dirty = true;
	}

	public void setSize(Dimension d) {
		this.width = d.getWidth();
		this.height = d.getHeight();
		this.dirty = true;
	}

	public void setHeight(int height) {
		this.height = height;
		this.dirty = true;
	}

	public Dimension cpy() {
		return new Dimension(width, height);
	}

	public boolean isLandscape() {
		return this.height < this.width;
	}

	public boolean isPortrait() {
		return this.height >= this.width;
	}

	@Override
	public String toString() {
		return "(" + width + "," + height + ")";
	}

}
