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
package loon.action.sprite;

import loon.LTexture;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.geom.PointF;
import loon.utils.MathUtils;
import loon.LSystem;

public class ImageBackground extends Background {

	private PointF _scrollDrag;

	public ImageBackground(LTexture tex, float x, float y, float w, float h) {
		super(x, y, w, h);
		setTexture(tex);
		_scrollDrag = new PointF();
	}

	public ImageBackground(String path, float x, float y, float w, float h) {
		this(LSystem.loadTexture(path), x, y, w, h);
	}

	public ImageBackground(String path) {
		this(LSystem.loadTexture(path));
	}

	public ImageBackground(LTexture texture) {
		this(texture, 0, 0, texture.getWidth(), texture.getHeight());
	}

	public ImageBackground scrollDown(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min((this._offset.y + distance),
				(MathUtils.max(0, this.getContainerHeight() - this.getHeight())));
		if (this._offset.y >= 0) {
			this._offset.y = 0;
		}
		return this;
	}

	public ImageBackground scrollLeft(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min(this._offset.x - distance, this.getX());
		float limitX = (getContainerWidth() - getWidth());
		if (this._offset.x <= limitX) {
			this._offset.x = limitX;
		}
		return this;
	}

	public ImageBackground scrollRight(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min((this._offset.x + distance),
				(MathUtils.max(0, this.getWidth() - getContainerWidth())));
		if (this._offset.x >= 0) {
			this._offset.x = 0;
		}
		return this;
	}

	public ImageBackground scrollUp(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min(this._offset.y - distance, 0);
		float limitY = (getContainerHeight() - getHeight());
		if (this._offset.y <= limitY) {
			this._offset.y = limitY;
		}
		return this;
	}

	public ImageBackground scrollLeftUp(float distance) {
		this.scrollUp(distance);
		this.scrollLeft(distance);
		return this;
	}

	public ImageBackground scrollRightDown(float distance) {
		this.scrollDown(distance);
		this.scrollRight(distance);
		return this;
	}

	public ImageBackground scrollClear() {
		if (!this._offset.equals(0f, 0f)) {
			this._offset.set(0, 0);
		}
		return this;
	}

	public ImageBackground scrollBy(float x, float y) {
		this._offset.x += x;
		this._offset.y += y;
		return this;
	}

	public ImageBackground scroll(float x, float y) {
		return scroll(x, y, 6f);
	}

	public ImageBackground scroll(float x, float y, float distance) {
		if (_scrollDrag.x == 0f && _scrollDrag.y == 0f) {
			_scrollDrag.set(x, y);
			return this;
		}
		return scroll(_scrollDrag.x, _scrollDrag.y, x, y, distance);
	}

	public ImageBackground scroll(float x1, float y1, float x2, float y2) {
		return scroll(x1, y1, x2, y2, 6f);
	}

	public ImageBackground scroll(float x1, float y1, float x2, float y2, float distance) {
		int dir = Field2D.getDirection(x1, y1, x2, y2);
		switch (dir) {
		case Config.RIGHT:
		case Config.TRIGHT:
			scrollLeft(distance);
			break;
		case Config.LEFT:
		case Config.TLEFT:
			scrollRight(distance);
			break;
		case Config.DOWN:
		case Config.TDOWN:
			scrollUp(distance);
			break;
		case Config.UP:
		case Config.TUP:
			scrollDown(distance);
			break;
		default:
			break;
		}
		_scrollDrag.set(x2, y2);
		return this;
	}

	public float scrollX() {
		return _offset.x;
	}

	public float scrollY() {
		return _offset.y;
	}
	
	@Override
	public void onResize(){
		super.onResize();
		this._offset.set(0f);
		this._scrollDrag.set(0f);
	}

}
