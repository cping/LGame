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
package loon.geom;

import loon.LSystem;
import loon.utils.MathUtils;

/**
 * 脏矩用计算类,会合并所有矩形范围后产生一个单独的矩形区域
 */
public class DirtyRect {

	private float _screenWidth;

	private float _screenHeight;

	private RectBox _boundingBox;

	public DirtyRect() {
		this(0f, 0f, 0f, 0f);
	}

	public DirtyRect(RectBox rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public DirtyRect(float x, float y, float w, float h) {
		this(x, y, w, h, LSystem.viewSize.width, LSystem.viewSize.height);
	}

	public DirtyRect(RectBox rect, float screenW, float screenH) {
		this(rect.x, rect.y, rect.width, rect.height, screenW, screenH);
	}

	public DirtyRect(float x, float y, float w, float h, float screenW, float screenH) {
		this._boundingBox = new RectBox(x, y, w, h);
		this._screenWidth = screenW;
		this._screenHeight = screenH;
	}

	public int x() {
		return _boundingBox.x();
	}

	public int y() {
		return _boundingBox.y();
	}

	public int width() {
		return _boundingBox.width;
	}

	public int height() {
		return _boundingBox.height;
	}

	public DirtyRect clear() {
		_boundingBox.set(0f, 0f, 0f, 0f);
		return this;
	}

	public DirtyRect fill(int w, int h) {
		_boundingBox.set(0f, 0f, w, h);
		setSize(w, h);
		return this;
	}

	public DirtyRect setSize(float w, float h) {
		this._screenWidth = w;
		this._screenHeight = h;
		return this;
	}

	public DirtyRect add(RectBox old) {
		if (old == null) {
			return this;
		}
		if (old.width == 0 || old.height == 0) {
			return this;
		}
		final RectBox src = old.cpy();
		if (src.x < 0) {
			if (src.width < -src.x) {
				return this;
			}
			src.width += src.x;
			src.x = 0;
		}
		if (src.y < 0) {
			if (src.height < -src.y) {
				return this;
			}
			src.height += src.y;
			src.y = 0;
		}

		if (src.x >= _screenWidth) {
			return this;
		}
		if (src.x + src.width >= _screenWidth) {
			src.width = MathUtils.floor(_screenWidth - src.x);
		}

		if (src.y >= _screenHeight) {
			return this;
		}
		if (src.y + src.height >= _screenHeight) {
			src.height = MathUtils.floor(_screenHeight - src.y);
		}

		this._boundingBox = calcBoundingBox(_boundingBox, src);
		return this;
	}

	public RectBox calcBoundingBox(RectBox src, RectBox dst) {
		if (src == null || dst == null) {
			return new RectBox();
		}
		if (dst.width == 0 || dst.height == 0) {
			return src;
		}
		if (src.width == 0 || src.height == 0) {
			return dst;
		}
		if (src.x > dst.x) {
			src.width += src.x - dst.x;
			src.x = dst.x;
		}
		if (src.y > dst.y) {
			src.height += src.y - dst.y;
			src.y = dst.y;
		}
		if (src.x + src.width < dst.x + dst.width) {
			src.width = MathUtils.floor(dst.x + dst.width - src.x);
		}
		if (src.y + src.height < dst.y + dst.height) {
			src.height = MathUtils.floor(dst.y + dst.height - src.y);
		}
		return src;
	}

	public float getScreenWidth() {
		return _screenWidth;
	}

	public float getScreenHeight() {
		return _screenHeight;
	}

	public RectBox getBoundingBox() {
		return _boundingBox;
	}

	@Override
	public String toString() {
		return _boundingBox.toString();
	}
}
