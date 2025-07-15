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

import loon.LSystem;
import loon.action.map.Field2D;
import loon.utils.MathUtils;

public class Dimension {

	public float width = -1, height = -1;

	private RectBox _rect;

	private boolean _dirty;

	public Dimension() {
		this(-1, -1);
	}

	public Dimension(Dimension d) {
		this(d.getWidth(), d.getHeight());
	}

	public Dimension(float w, float h) {
		this.width = w;
		this.height = h;
		this._dirty = true;
	}

	public RectBox getRect() {
		if (this._rect == null) {
			this._rect = new RectBox(0, 0, width, height);
		} else {
			this._rect.setBounds(0, 0, width, height);
		}
		return this._rect;
	}

	public float getTileWidthSize() {
		return getTileWidthSize(15f, 10f);
	}

	public float getTileWidthSize(float landscape, float portrait) {
		if (isLandscape()) {
			return width / landscape;
		}
		return width / portrait;
	}

	public float getTileHeightSize() {
		return getTileHeightSize(10f, 15f);
	}

	public float getTileHeightSize(float landscape, float portrait) {
		if (isLandscape()) {
			return height / landscape;
		}
		return height / portrait;
	}

	public boolean isDirty() {
		return this._dirty;
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
		return newField2D(tileWidth, tileHeight, 0);
	}

	public Field2D newField2D(int tileWidth, int tileHeight, int fill) {
		return new Field2D(getWidth() / tileWidth, getHeight() / tileHeight, tileWidth, tileHeight, fill);
	}

	public int getHeight() {
		return (int) height;
	}

	public int getWidth() {
		return (int) width;
	}

	public int getZoomHeight() {
		return MathUtils.ifloor(LSystem.getScaleHeight() * height);
	}

	public int getZoomWidth() {
		return MathUtils.ifloor(LSystem.getScaleWidth() * width);
	}

	public Dimension setWidth(int w) {
		if (this.width != w) {
			this._dirty = true;
		}
		this.width = w;
		return this;
	}

	public Dimension setSize(int w, int h) {
		if (this.width != w || this.height != h) {
			this._dirty = true;
		}
		this.width = w;
		this.height = h;
		return this;
	}

	public Dimension setSize(Dimension d) {
		if (d == null) {
			return this;
		}
		return setSize(d.getWidth(), d.getHeight());
	}

	public Dimension setHeight(int h) {
		if (this.height != h) {
			this._dirty = true;
		}
		this.height = h;
		return this;
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
