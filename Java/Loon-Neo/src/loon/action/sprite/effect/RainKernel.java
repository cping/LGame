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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

/**
 * 图片雨的具体实现效果
 */
public class RainKernel implements IKernel {

	private boolean _exist;

	private LTexture _rain;

	private int _id;

	private float _offsetX, _offsetY;

	private float _x, _y, _width, _height;

	private float _rainWidth, _rainHeight;

	public RainKernel(LTexturePack pack, int n, int w, int h) {
		this(pack.getTexture(LSystem.getSystemImagePath() + "rain_" + n), n, w, h, -1f);
	}

	public RainKernel(LTexture texture, int n, int w, int h, float r) {
		this._rain = texture;
		this._rainWidth = _rain.width();
		this._rainHeight = _rain.height();
		this._width = w;
		this._height = h;
		this._offsetX = 0;
		if (r == -1f) {
			_offsetY = (5 - n) * 30 + 75 + MathUtils.random() * 15;
		} else {
			_offsetY = r;
		}
	}

	@Override
	public int id() {
		return _id;
	}

	public RainKernel make() {
		_exist = true;
		_x = MathUtils.random() * _width;
		_y = -_rainHeight;
		return this;
	}

	@Override
	public void update() {
		if (!_exist) {
			if (MathUtils.random() < 0.002f) {
				make();
			}
		} else {
			_x += _offsetX;
			_y += _offsetY;
			if (_y >= _height) {
				_x = MathUtils.random() * _width;
				_y = -_rainHeight * MathUtils.random();
			}
		}
	}

	@Override
	public void draw(GLEx g, float mx, float my) {
		if (_exist) {
			_rain.draw(mx + _x, my + _y);
		}
	}

	@Override
	public LTexture get() {
		return _rain;
	}

	@Override
	public float getHeight() {
		return _rainHeight;
	}

	@Override
	public float getWidth() {
		return _rainWidth;
	}

	public boolean isClosed() {
		return _rain == null || _rain.isClosed();
	}

	@Override
	public void close() {
		if (_rain != null) {
			_rain.close();
			_rain = null;
		}
	}

}
