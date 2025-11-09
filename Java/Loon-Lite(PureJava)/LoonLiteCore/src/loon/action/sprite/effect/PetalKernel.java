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

public class PetalKernel implements IKernel {

	private boolean _exist;

	private LTexture _sakura;

	private float _offsetX, _offsetY, _speed;

	private float _x, _y, _width, _height;

	private float _sakuraWidth, _sakuraHeight;

	private int _id;

	public PetalKernel(LTexturePack pack, int n, int w, int h) {
		this(pack.getTexture(LSystem.getSystemImagePath() + "sakura_" + n), n, w, h, -1f);
	}

	public PetalKernel(LTexture texture, int n, int w, int h, float r) {
		this._id = n;
		this._sakura = texture;
		this._sakuraWidth = _sakura.width();
		this._sakuraHeight = _sakura.height();
		this._width = w;
		this._height = h;
		this._offsetX = 0;
		if (r == -1f) {
			this._offsetY = n * 0.6f + 1.9f + MathUtils.random() * 0.2f;
		} else {
			this._offsetY = r;
		}
		_speed = MathUtils.random();
	}

	@Override
	public int id() {
		return _id;
	}

	public void make() {
		this._exist = true;
		this._x = MathUtils.random() * _width;
		this._y = -_sakuraHeight;
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
			_offsetX += _speed;
			_speed += (MathUtils.random() - 0.5f) * 0.3f;
			if (_offsetX >= 1.5f) {
				_offsetX = 1.5f;
			}
			if (_offsetX <= -1.5) {
				_offsetX = -1.5f;
			}
			if (_speed >= 0.2f) {
				_speed = 0.2f;
			}
			if (_speed <= -0.2f) {
				_speed = -0.2f;
			}
			if (_y >= _height) {
				_y = -(MathUtils.random() * 1) - _sakuraHeight;
				_x = (MathUtils.random() * (_width - 1));
			}
		}
	}

	@Override
	public void draw(GLEx g, float mx, float my) {
		if (_exist) {
			_sakura.draw(mx + _x, my + _y);
		}
	}

	@Override
	public LTexture get() {
		return _sakura;
	}

	@Override
	public float getHeight() {
		return _sakuraHeight;
	}

	@Override
	public float getWidth() {
		return _sakuraWidth;
	}

	public boolean isClosed() {
		return _sakura == null || _sakura.isClosed();
	}

	@Override
	public void close() {
		if (_sakura != null) {
			_sakura.close();
			_sakura = null;
		}
	}

}
