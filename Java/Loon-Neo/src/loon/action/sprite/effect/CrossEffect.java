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

import loon.LTexture;
import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;

/**
 * 0.3.2起新增类，百叶窗特效 0--竖屏,1--横屏
 */
public class CrossEffect extends BaseAbstractEffect {

	private boolean _createTexture;

	private LColor _crossColor;

	private LTexture _otexture, _ntexture;

	private int _count, _code;

	private int _maxcount = 16;

	private int _part;

	private int _left;

	private int _right;

	private LTexture _tmp;

	public CrossEffect(int c, String fileName) {
		this(c, LSystem.loadTexture(fileName));
	}

	public CrossEffect(int c, String oldImgPath, String newImgPath) {
		this(c, LSystem.loadTexture(oldImgPath), LSystem.loadTexture(newImgPath));
	}

	public CrossEffect(int c, LTexture o) {
		this(c, o, null);
	}

	public CrossEffect(int c, LTexture o, LTexture n) {
		this._code = c;
		this._otexture = o;
		this._ntexture = n;
		init(o.getWidth(), o.getHeight());
	}

	public CrossEffect(int c, LColor color, float w, float h) {
		this._code = c;
		this._otexture = this._ntexture = null;
		this._crossColor = color;
		this._createTexture = true;
		this.init(w, h);
	}

	protected void init(float w, float h) {
		this._width = w;
		this._height = h;
		if (_width > _height) {
			_maxcount = 16;
		} else {
			_maxcount = 8;
		}
		this.setDelay(160);
		this.setRepaint(true);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_createTexture) {
			return;
		}
		if (this._count > this._maxcount) {
			this._completed = true;
		}
		if (_timer.action(elapsedTime)) {
			_count++;
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_createTexture) {
			if (_crossColor != null) {
				_otexture = TextureUtils.createTexture(width(), height(), _crossColor);
			}
			_createTexture = false;
			return;
		}
		if (_completed) {
			if (_ntexture != null) {
				g.draw(_ntexture, drawX(offsetX), drawY(offsetY));
			}
			return;
		}
		_part = 0;
		_left = 0;
		_right = 0;
		_tmp = null;
		switch (_code) {
		default:
			_part = (int) (_width / this._maxcount / 2);
			for (int i = 0; i <= this._maxcount; i++) {
				if (i <= this._count) {
					_tmp = this._ntexture;
					if (_tmp == null) {
						continue;
					}
				} else {
					_tmp = this._otexture;
				}
				_left = i * 2 * _part;
				_right = (int) (_width - ((i + 1) * 2 - 1) * _part);
				g.draw(_tmp, drawX(offsetX + _left), drawY(offsetY), _part, _height, _left, 0, _left + _part, _height);
				g.draw(_tmp, drawX(offsetX + _right), drawY(offsetY), _part, _height, _right, 0, _right + _part,
						_height);
			}
			break;
		case 1:
			_part = (int) (_height / this._maxcount / 2);
			for (int i = 0; i <= this._maxcount; i++) {
				if (i <= this._count) {
					_tmp = this._ntexture;
					if (_tmp == null) {
						continue;
					}
				} else {
					_tmp = this._otexture;
				}
				int up = i * 2 * _part;
				int down = (int) (_height - ((i + 1) * 2 - 1) * _part);
				g.draw(_tmp, drawX(offsetX), drawY(up), _width, _part, 0, up, _width, up + _part);
				g.draw(_tmp, drawX(offsetY), drawY(down), _width, _part, 0, down, _width, down + _part);
			}
			break;
		}

	}

	@Override
	public CrossEffect reset() {
		super.reset();
		this._count = 0;
		return this;
	}

	@Override
	public LTexture getBitmap() {
		return _otexture;
	}

	public int getMaxCount() {
		return _maxcount;
	}

	public CrossEffect setMaxCount(int maxcount) {
		this._maxcount = maxcount;
		return this;
	}

	@Override
	public CrossEffect setAutoRemoved(boolean a) {
		super.setAutoRemoved(a);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (_otexture != null) {
			_otexture.close();
			_otexture = null;
		}
		if (_ntexture != null) {
			_ntexture.close();
			_ntexture = null;
		}
	}

}
