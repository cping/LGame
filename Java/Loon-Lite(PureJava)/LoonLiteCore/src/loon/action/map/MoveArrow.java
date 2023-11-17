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
package loon.action.map;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.ConfigReader;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;

/**
 * 地图移动路径箭头渲染用类
 */
public class MoveArrow implements LRelease {

	private final ObjectMap<String, LTexture> _childArrows;
	private final ObjectMap<String, RectBox> _childRectArrows;
	private final TArray<ArrowMovePos> _arrowResult;

	private final LTexture _image;
	private final float _tileSize;

	private float _offsetX = 1f;
	private float _offsetY = 1f;

	private boolean _dirty;

	protected String left = "left";
	protected String right = "right";
	protected String up = "up";
	protected String down = "down";
	protected String leftStub = "leftStub";
	protected String rightStub = "rightStub";
	protected String upStub = "upStub";
	protected String downStub = "downStub";
	protected String upLeft = "upLeft";
	protected String upRight = "upRight";
	protected String downLeft = "downLeft";
	protected String downRight = "downRight";
	protected String horiz = "horiz";
	protected String vert = "vert";

	protected RectBox leftRect;
	protected RectBox rightRect;
	protected RectBox upRect;
	protected RectBox downRect;
	protected RectBox leftStubRect;
	protected RectBox rightStubRect;
	protected RectBox upStubRect;
	protected RectBox downStubRect;
	protected RectBox upLeftRect;
	protected RectBox upRightRect;
	protected RectBox downLeftRect;
	protected RectBox downRightRect;
	protected RectBox horizRect;
	protected RectBox vertRect;

	public static class ArrowMovePos {

		LTexture tex;

		float x;

		float y;

		public ArrowMovePos(LTexture tex, float x, float y) {
			this.tex = tex;
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public LTexture texture() {
			return tex;
		}
	}

	public MoveArrow(String path) {
		this(path, LSystem.LAYER_TILE_SIZE);
	}

	public MoveArrow(String path, int tileSize) {
		this(LTextures.loadTexture(path), tileSize);
	}

	public MoveArrow(LTexture tex) {
		this(tex, LSystem.LAYER_TILE_SIZE);
	}

	public MoveArrow(LTexture tex, int tileSize) {
		_childArrows = new ObjectMap<String, LTexture>();
		_childRectArrows = new ObjectMap<String, RectBox>();
		_arrowResult = new TArray<MoveArrow.ArrowMovePos>();
		_image = tex;
		_tileSize = tileSize;
	}

	protected RectBox updateRect(String key, float x, float y, float w, float h) {
		RectBox rect = _childRectArrows.get(key);
		if (rect == null) {
			rect = new RectBox(x, y, w, h);
			_childRectArrows.put(key, rect);
			_dirty = true;
		} else {
			if (!rect.equals(x, y, w, h)) {
				rect.set(x, y, w, h);
				_dirty = true;
			}
		}
		return rect;
	}

	public MoveArrow setLeftRect(float x, float y, float w, float h) {
		updateRect(left, x, y, w, h);
		return this;
	}

	public MoveArrow setRightRect(float x, float y, float w, float h) {
		updateRect(right, x, y, w, h);
		return this;
	}

	public MoveArrow setUpRect(float x, float y, float w, float h) {
		updateRect(up, x, y, w, h);
		return this;
	}

	public MoveArrow setDownRect(float x, float y, float w, float h) {
		updateRect(down, x, y, w, h);
		return this;
	}

	public MoveArrow setLeftStubRect(float x, float y, float w, float h) {
		updateRect(leftStub, x, y, w, h);
		return this;
	}

	public MoveArrow setRightStubRect(float x, float y, float w, float h) {
		updateRect(rightStub, x, y, w, h);
		return this;
	}

	public MoveArrow setUpStubRect(float x, float y, float w, float h) {
		updateRect(upStub, x, y, w, h);
		return this;
	}

	public MoveArrow setDownStubRect(float x, float y, float w, float h) {
		updateRect(downStub, x, y, w, h);
		return this;
	}

	public MoveArrow setUpLeftRect(float x, float y, float w, float h) {
		updateRect(upLeft, x, y, w, h);
		return this;
	}

	public MoveArrow setUpRightRect(float x, float y, float w, float h) {
		updateRect(upRight, x, y, w, h);
		return this;
	}

	public MoveArrow setDownLeftRect(float x, float y, float w, float h) {
		updateRect(downLeft, x, y, w, h);
		return this;
	}

	public MoveArrow setDownRightRect(float x, float y, float w, float h) {
		updateRect(downRight, x, y, w, h);
		return this;
	}

	public MoveArrow setHorizRect(float x, float y, float w, float h) {
		updateRect(horiz, x, y, w, h);
		return this;
	}

	public MoveArrow setVertRect(float x, float y, float w, float h) {
		updateRect(vert, x, y, w, h);
		return this;
	}

	protected void setConfigKey(ConfigReader config, String key) {
		float[] v = config.getFloatValues(key);
		if (v != null && v.length > 3) {
			updateRect(key, _offsetX + v[0], _offsetY + v[1], v[3] - _offsetX, v[3] - _offsetY);
		}
	}

	protected MoveArrow setConfig(ConfigReader config) {
		setConfigKey(config, left);
		setConfigKey(config, right);
		setConfigKey(config, up);
		setConfigKey(config, down);
		setConfigKey(config, leftStub);
		setConfigKey(config, rightStub);
		setConfigKey(config, upStub);
		setConfigKey(config, downStub);
		setConfigKey(config, upLeft);
		setConfigKey(config, upRight);
		setConfigKey(config, downLeft);
		setConfigKey(config, downRight);
		setConfigKey(config, horiz);
		setConfigKey(config, vert);
		return this;
	}

	/**
	 * 以指定字符串内容设定箭头拆分
	 * 
	 * @param context "left=xx,xx,xx,xx;right=xx,xx,xx,xx;..."
	 * @return
	 */
	public MoveArrow setContext(String context) {
		return setConfig(ConfigReader.parse(context));
	}

	/**
	 * 以指定配置文件设定箭头拆分
	 * 
	 * @param path
	 * @return
	 */
	public MoveArrow setPath(String path) {
		return setConfig(ConfigReader.shared(path));
	}

	/**
	 * 默认的图片拆分模式1(此处对应的上下左右为图片接口位置的上下左右,而不是箭头上下左右)
	 * 
	 * @return
	 */
	public MoveArrow defaultSet1() {
		setLeftRect(_offsetX, _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setRightRect(_tileSize + _offsetX, _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpRect(_tileSize * 2 + _offsetX, _offsetY, _tileSize - _offsetX, _tileSize - _offsetY - 1);
		setDownRect(_tileSize * 3 + _offsetX, _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setLeftStubRect(_offsetX, _tileSize + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setRightStubRect(_tileSize + _offsetX, _tileSize + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpStubRect(_tileSize * 2 + _offsetX, _tileSize + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownStubRect(_tileSize * 3 + _offsetX, _tileSize + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpLeftRect(_offsetX, _tileSize * 2 + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpRightRect(_tileSize + _offsetX, _tileSize * 2 + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownLeftRect(_tileSize * 2 + _offsetX, _tileSize * 2 + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownRightRect(_tileSize * 3 + _offsetX, _tileSize * 2 + _offsetY, _tileSize - _offsetX,
				_tileSize - _offsetY);
		setHorizRect(_offsetX, _tileSize * 3 + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		setVertRect(_tileSize + _offsetX, _tileSize * 3 + _offsetY, _tileSize - _offsetX, _tileSize - _offsetY);
		return this;
	}

	/**
	 * 默认的图片拆分模式2(此处对应的上下左右为图片接口位置的上下左右,而不是箭头上下左右)
	 * 
	 * @return
	 */
	public MoveArrow defaultSet2() {
		setLeftRect(_offsetX + _tileSize * 4, _offsetY + _tileSize * 1, _tileSize - _offsetX, _tileSize - _offsetY);
		setRightRect(_offsetX + _tileSize * 1, _offsetY + _tileSize * 2, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpRect(_offsetX, _offsetY + _tileSize * 2, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownRect(_offsetX + _tileSize * 2, _offsetY + _tileSize * 2, _tileSize - _offsetX, _tileSize - _offsetY);
		setLeftStubRect(_offsetX, _offsetY + _tileSize * 1, _tileSize - _offsetX, _tileSize - _offsetY);
		setRightStubRect(_offsetX + _tileSize * 2, _offsetY + _tileSize * 1, _tileSize - _offsetX,
				_tileSize - _offsetY);
		setUpStubRect(_offsetX + _tileSize * 1, _offsetY + _tileSize * 1, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownStubRect(_offsetX + _tileSize * 3, _offsetY + _tileSize * 1, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpLeftRect(_offsetX + _tileSize * 3, _offsetY + _tileSize * 2, _tileSize - _offsetX, _tileSize - _offsetY);
		setUpRightRect(_offsetX, _offsetY + _tileSize * 3, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownLeftRect(_offsetX + _tileSize * 1, _offsetY + _tileSize * 3, _tileSize - _offsetX, _tileSize - _offsetY);
		setDownRightRect(_offsetX + _tileSize * 3, _offsetY + _tileSize * 3, _tileSize - _offsetX,
				_tileSize - _offsetY);
		setHorizRect(_offsetX + _tileSize * 4, _offsetY + _tileSize * 2, _tileSize - _offsetX, _tileSize - _offsetY);
		setVertRect(_offsetX + _tileSize * 2, _offsetY + _tileSize * 3, _tileSize - _offsetX, _tileSize - _offsetY);
		return this;
	}

	public MoveArrow create() {
		if (_dirty) {
			_childArrows.clear();
			for (Entries<String, RectBox> iter = _childRectArrows.entries(); iter.hasNext();) {
				Entry<String, RectBox> entry = iter.next();
				_childArrows.put(entry.key, _image.copy(entry.value));
			}
			_dirty = false;
		}
		return this;
	}

	public MoveArrow clear() {
		_childArrows.clear();
		_childRectArrows.clear();
		_arrowResult.clear();
		_dirty = false;
		return this;
	}

	public LTexture getLeftRect() {
		return _childArrows.get(left);
	}

	public LTexture getRightRect() {
		return _childArrows.get(right);
	}

	public LTexture getUpRect() {
		return _childArrows.get(up);
	}

	public LTexture getDownRect() {
		return _childArrows.get(down);
	}

	public LTexture getLeftStubRect() {
		return _childArrows.get(leftStub);
	}

	public LTexture getRightStubRect() {
		return _childArrows.get(rightStub);
	}

	public LTexture getUpStubRect() {
		return _childArrows.get(upStub);
	}

	public LTexture getDownStubRect() {
		return _childArrows.get(downStub);
	}

	public LTexture getUpLeftRect() {
		return _childArrows.get(upLeft);
	}

	public LTexture getUpRightRect() {
		return _childArrows.get(upRight);
	}

	public LTexture getDownLeftRect() {
		return _childArrows.get(downLeft);
	}

	public LTexture getDownRightRect() {
		return _childArrows.get(downRight);
	}

	public LTexture getHorizRect() {
		return _childArrows.get(horiz);
	}

	public LTexture getVertRect() {
		return _childArrows.get(vert);
	}

	public MoveArrow dirty() {
		_dirty = true;
		return this;
	}

	public TArray<ArrowMovePos> updatePath(TArray<Vector2f> list) {
		if (_dirty) {
			create();
			_arrowResult.clear();
			for (int i = 0; i < list.size; i++) {

				final Vector2f pos = list.get(i);

				boolean l = false;
				boolean r = false;
				boolean u = false;
				boolean d = false;

				final Vector2f prev = i - 1 >= 0 ? list.get(i - 1) : null;
				if (prev != null) {
					float dx = pos.x - prev.x;
					float dy = pos.y - prev.y;
					if (dx == 1) {
						l = true;
					} else if (dx == -1) {
						r = true;
					}
					if (dy == 1) {
						u = true;
					} else if (dy == -1) {
						d = true;
					}
				}

				final Vector2f next = i + 1 < list.size ? list.get(i + 1) : null;
				if (next != null) {
					float dx = next.x - pos.x;
					float dy = next.y - pos.y;
					if (dx == -1) {
						l = true;
					} else if (dx == 1) {
						r = true;
					}
					if (dy == -1) {
						u = true;
					} else if (dy == 1) {
						d = true;
					}
				}

				if (l || r || u || d) {
					String direction = null;
					if (l && r) {
						direction = horiz;
					} else if (u && d) {
						direction = vert;
					} else if (u && l) {
						direction = upLeft;
					} else if (u && r) {
						direction = upRight;
					} else if (d && l) {
						direction = downLeft;
					} else if (d && r) {
						direction = downRight;
					} else if (l && i == 0) {
						direction = leftStub;
					} else if (r && i == 0) {
						direction = rightStub;
					} else if (u && i == 0) {
						direction = upStub;
					} else if (d && i == 0) {
						direction = downStub;
					} else if (l) {
						direction = left;
					} else if (r) {
						direction = right;
					} else if (u) {
						direction = up;
					} else if (d) {
						direction = down;
					}
					if (direction != null) {
						_arrowResult.add(new ArrowMovePos(_childArrows.get(direction), pos.x, pos.y));
					}
				}
			}
		}
		return _arrowResult;
	}

	public void draw(GLEx g, float tileX, float tileY, LColor color) {
		draw(_arrowResult, g, 0f, 0f, tileX, tileY, color);
	}

	public void draw(GLEx g, float x, float y, float tileX, float tileY, LColor color) {
		draw(_arrowResult, g, x, y, tileX, tileY, color);
	}

	public void draw(TArray<ArrowMovePos> list, GLEx g, float x, float y, float tileX, float tileY, LColor color) {
		if (list == null) {
			return;
		}
		final int size = list.size - 1;
		for (int i = size; i > -1; i--) {
			final ArrowMovePos pos = list.get(i);
			g.draw(pos.texture(), (pos.x * tileX) + x + _offsetX, (pos.y * tileY) + y + _offsetY, tileX + _offsetX,
					tileY + _offsetY, color);
		}
	}

	public MoveArrow setOffset(float s) {
		return setOffset(s, s);
	}

	public MoveArrow setOffset(float sx, float sy) {
		_offsetX = sx;
		_offsetY = sy;
		return this;
	}

	public float getOffsetX() {
		return _offsetX;
	}

	public MoveArrow setOffsetX(float x) {
		this._offsetX = x;
		return this;
	}

	public float getOffsetY() {
		return _offsetY;
	}

	public MoveArrow setOffsetY(float y) {
		this._offsetY = y;
		return this;
	}

	@Override
	public void close() {
		_childArrows.close();
		_childRectArrows.close();
		_arrowResult.close();
		if (_image != null) {
			_image.close(true);
		}
	}

}
