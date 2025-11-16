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
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 黑幕过渡效果,瓦片从向中心处螺旋集中或向外螺旋扩散最终消失
 */
public class FadeSpiralEffect extends BaseAbstractEffect {

	private float _tileSizeWidth, _tileSizeHeight;

	private int _tilewidth;
	private int _tileheight;
	private int _speed;
	private int _tilescovered = 0;

	private boolean[][] _conversions;
	private int _cx = 0;
	private int _cy = 0;

	private int _state = 1;

	private int _type;

	public FadeSpiralEffect(int type) {
		this(type, 1, LColor.black);
	}

	public FadeSpiralEffect(int type, LColor c) {
		this(type, 1, c);
	}

	public FadeSpiralEffect(int type, int speed, LColor c) {
		this(type, speed, c, LSystem.viewSize.getTileWidthSize(10f, 5f), LSystem.viewSize.getTileHeightSize(5f, 10f));
	}

	public FadeSpiralEffect(int type, int speed, LColor c, float tw, float th) {
		this(type, speed, c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), tw, th);
	}

	public FadeSpiralEffect(int type, int speed, LColor c, float width, float height, float tw, float th) {
		this._type = type;
		this._speed = speed;
		this._tileSizeWidth = tw;
		this._tileSizeHeight = th;
		this._tilewidth = MathUtils.ifloor(width / tw) + 1;
		this._tileheight = MathUtils.ifloor(height / th) + 1;
		this._conversions = new boolean[_tilewidth][_tileheight];
		this.reset();
		this.setDelay(30);
		this.setRepaint(true);
		this.setColor(c);
		this.setSize(width, height);
	}

	@Override
	public FadeSpiralEffect reset() {
		super.reset();
		int tmp = _baseColor.getARGB();
		if (_type == ISprite.TYPE_FADE_IN) {
			for (int x = 0; x < _tilewidth; x++) {
				for (int y = 0; y < _tileheight; y++) {
					_conversions[x][y] = true;
				}
			}
		} else {
			for (int x = 0; x < _tilewidth; x++) {
				for (int y = 0; y < _tileheight; y++) {
					_conversions[x][y] = false;
				}
			}
		}
		this._state = 1;
		this._cx = 0;
		this._cy = 0;
		this._tilescovered = 0;
		_baseColor.setColor(tmp);
		return this;
	}

	public boolean finished() {
		return _tilescovered >= (_tilewidth * _tileheight);
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_type == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height, _baseColor);
			return;
		}
		if (_type == TYPE_FADE_IN && _completed) {
			return;
		}
		for (int x = 0; x < _tilewidth; x++) {
			for (int y = 0; y < _tileheight; y++) {
				if (_conversions[x][y]) {
					g.fillRect(drawX(x * _tileSizeWidth + offsetX), drawY(y * _tileSizeHeight + offsetY),
							_tileSizeWidth, _tileSizeHeight, _baseColor);
				}
			}
		}
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			if (_type == ISprite.TYPE_FADE_IN) {
				for (int i = 0; i < _speed; i++) {
					if (_conversions[_cx][_cy]) {
						_conversions[_cx][_cy] = false;
						_tilescovered++;
					}
					switch (_state) {
					case 0:
						_cy--;
						if (_cy <= -1 || (!_conversions[_cx][_cy])) {
							_cy++;
							_state = 2;
						}
						break;
					case 1:
						_cy++;
						if (_cy >= _tileheight || (!_conversions[_cx][_cy])) {
							_cy--;
							_state = 3;
						}
						break;
					case 2:
						_cx--;
						if (_cx <= -1 || (!_conversions[_cx][_cy])) {
							_cx++;
							_state = 1;
						}
						break;
					case 3:
						_cx++;
						if (_cx >= _tilewidth || (!_conversions[_cx][_cy])) {
							_cx--;
							_state = 0;
						}
						break;
					}
				}
			} else {
				for (int i = 0; i < _speed; i++) {
					if (!_conversions[_cx][_cy]) {
						_conversions[_cx][_cy] = true;
						_tilescovered++;
					}
					switch (_state) {
					case 0:
						_cy--;
						if (_cy <= -1 || (_conversions[_cx][_cy])) {
							_cy++;
							_state = 2;
						}
						break;
					case 1:
						_cy++;
						if (_cy >= _tileheight || (_conversions[_cx][_cy])) {
							_cy--;
							_state = 3;
						}
						break;
					case 2:
						_cx--;
						if (_cx <= -1 || (_conversions[_cx][_cy])) {
							_cx++;
							_state = 1;
						}
						break;
					case 3:
						_cx++;
						if (_cx >= _tilewidth || (_conversions[_cx][_cy])) {
							_cx--;
							_state = 0;
						}
						break;
					}
				}
			}
			if (finished()) {
				_completed = true;
			}
		}
	}

	@Override
	public FadeSpiralEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		_conversions = null;
	}

}
