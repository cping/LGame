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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.collision.CollisionHelper;
import loon.action.map.Config;
import loon.action.map.Side;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 残像效果构建用类,就是上古游戏梦幻模拟战里骑士冲锋那类效果……
 */
public class AfterImageEffect extends BaseAbstractEffect {

	public final static int LINE = 0;

	public final static int CURVE = 1;

	protected final class AfterObject {

		float alpha = 1f;

		float x = 0f;

		float y = 0f;

		float width = 0f;

		float height = 0f;

	}

	private LTexture _afterTexture;

	private boolean _displayCompleted;

	private boolean _moveOrbitReverse;

	private boolean _inited;

	private boolean _looping;

	private boolean _playing;

	private int _moveOrbit = 0;

	private int _indexNext = 0;

	private int _moveDir = Config.EMPTY;

	private float _orbitValue = 0f;

	private float _startX, _startY, _startWidth, _startHeight;

	private TArray<AfterImageEffect.AfterObject> _afterObjects;

	private float _interval = 0f;

	private int _count = 0;

	public AfterImageEffect(String path, float startX, float startY, int count) {
		this(Config.TRIGHT, path, startX, startY, count);
	}

	public AfterImageEffect(LTexture tex, float startX, float startY, int count) {
		this(Config.TRIGHT, tex, startX, startY, count);
	}

	public AfterImageEffect(int dir, String path, float startX, float startY, int count) {
		this(dir, LTextures.loadTexture(path), startX, startY, count);
	}

	public AfterImageEffect(int dir, String path, float startX, float startY, float startW, float startH, int count) {
		this(dir, LTextures.loadTexture(path), startX, startY, startW, startH, count);
	}

	public AfterImageEffect(int dir, LTexture tex, float startX, float startY, int count) {
		this(dir, tex, startX, startY, tex.getWidth(), tex.getHeight(), count);
	}

	public AfterImageEffect(int dir, LTexture tex, float startX, float startY, float startW, float startH, int count) {
		this(dir, tex, 0f, 0f, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), startX, startY, startW,
				startH, count);
	}

	public AfterImageEffect(int dir, LTexture tex, float displayX, float displayY, float displayW, float displayH,
			float startX, float startY, float startW, float startH, int count) {
		this.setRepaint(true);
		this.setLocation(displayX, displayY);
		this.setSize(displayW, displayH);
		this.setTexture(tex);
		this.setInterval(0f);
		this.setCount(count);
		this.setStartLocation(startX, startY);
		this.setOrbitValue(MathUtils.min(startW, startH) / 6f);
		this._moveDir = dir;
		this._startWidth = startW;
		this._startHeight = startH;
	}

	public AfterImageEffect setStartLocation(float x, float y) {
		setStartX(x);
		setStartY(y);
		return this;
	}

	public int getModeDirection() {
		return _moveDir;
	}

	public AfterImageEffect makeEffect() {
		createAfterObjects(_startX, _startY, _startWidth, _startHeight);
		return this;
	}

	@Override
	public AfterImageEffect setTexture(LTexture tex) {
		this._afterTexture = tex;
		return this;
	}

	@Override
	public AfterImageEffect setTexture(String path) {
		this._afterTexture = LTextures.loadTexture(path);
		return this;
	}

	@Override
	public LTexture getBitmap() {
		return this._afterTexture;
	}

	public AfterImageEffect setInterval(float i) {
		this._interval = i;
		return this;
	}

	public float getInterval() {
		return this._interval;
	}

	public AfterImageEffect setCount(int c) {
		this._count = c + 1;
		return this;
	}

	public int getCount() {
		return _count - 1;
	}

	public AfterImageEffect createAfterObjects(float x, float y, float w, float h) {
		if (_afterObjects == null) {
			_afterObjects = new TArray<AfterImageEffect.AfterObject>(_count);
		} else {
			_afterObjects.clear();
		}
		for (int i = 0; i < _count; i++) {
			final AfterObject after = new AfterObject();
			after.width = w;
			after.height = h;
			after.alpha = (float) i / _count;
			switch (_moveDir) {
			case Config.UP:
				after.x = +(i * (w + _interval));
				after.y = -(i * (h + _interval));
				updateOrbit(after, i, true, true);
				break;
			case Config.LEFT:
				after.x = -(i * (w + _interval));
				after.y = -(i * (h + _interval));
				updateOrbit(after, i, false, true);
				break;
			default:
			case Config.RIGHT:
				after.x = +(i * (w + _interval));
				after.y = +(i * (h + _interval));
				updateOrbit(after, i, false, true);
				break;
			case Config.DOWN:
				after.x = -(i * (w + _interval));
				after.y = +(i * (h + _interval));
				updateOrbit(after, i, true, true);
				break;
			case Config.TUP:
				after.y = -(i * (h + _interval));
				updateOrbit(after, i, true, false);
				break;
			case Config.TLEFT:
				after.x = -(i * (w + _interval));
				updateOrbit(after, i, false, true);
				break;
			case Config.TRIGHT:
				after.x = +(i * (w + _interval));
				updateOrbit(after, i, false, true);
				break;
			case Config.TDOWN:
				after.y = +(i * (h + _interval));
				updateOrbit(after, i, true, false);
				break;
			}
			_afterObjects.add(after);
		}
		return this;
	}

	private void updateOrbit(AfterObject o, int idx, boolean c, boolean r) {
		switch (_moveOrbit) {
		case LINE:
			break;
		case CURVE:
			if (c) {
				if (!_moveOrbitReverse) {
					if (idx % 2 == 0) {
						o.x += _orbitValue;
					} else {
						o.x -= _orbitValue;
					}
				} else {
					if (idx % 2 != 0) {
						o.x += _orbitValue;
					} else {
						o.x -= _orbitValue;
					}
				}
			}
			if (r) {
				if (!_moveOrbitReverse) {
					if (idx % 2 == 0) {
						o.y += _orbitValue;
					} else {
						o.y -= _orbitValue;
					}
				} else {
					if (idx % 2 != 0) {
						o.y += _orbitValue;
					} else {
						o.y -= _orbitValue;
					}
				}
			}
			break;
		}
	}

	public AfterImageEffect start() {
		if (_playing) {
			return this;
		}
		if (!_inited) {
			makeEffect();
			_inited = true;
		}
		_playing = true;
		return this;
	}

	public AfterImageEffect restart() {
		if (!_playing) {
			clearData();
			start();
		}
		return this;
	}

	@Override
	public void onUpdate(long e) {
		if (!_inited) {
			return;
		}
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(e)) {
			if (!_displayCompleted) {
				if (_indexNext < _count - 1) {
					this._indexNext++;
				} else {
					this._displayCompleted = true;
				}
			}
			if (_displayCompleted && _afterObjects.size > 1) {
				this._afterObjects.shift();
				this._indexNext = _afterObjects.size;
			} else if (_afterObjects.size == 1) {
				final AfterObject o = this._afterObjects.last();
				if (o != null) {
					o.alpha = 1f;
					this._indexNext = 1;
					if (!_looping) {
						this._completed = true;
						this._playing = false;
					} else {
						this._playing = false;
						this._moveDir = Side.getOppositeSide(_moveDir);
						this._moveOrbitReverse = !_moveOrbitReverse;
						this.setStartX(_startX + o.x);
						this.setStartY(_startY + o.y);
						this.restart();
					}
				}
			}
		}
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (!_inited) {
			return;
		}
		final float newX = drawX(sx);
		final float newY = drawY(sy);
		if (!_completed) {
			final float oldAlpha = g.getAlpha();
			for (int i = 0; i < _indexNext; i++) {
				final AfterObject o = _afterObjects.get(i);
				if (o != null) {
					g.setAlpha(o.alpha);
					g.draw(_afterTexture, newX + (_startX + o.x), newY + (_startY + o.y), o.width, o.height);
				}
			}
			g.setAlpha(oldAlpha);
		} else {
			final AfterObject o = _afterObjects.last();
			if (o != null) {
				g.draw(_afterTexture, newX + (_startX + o.x), newY + (_startY + o.y), o.width, o.height);
			}
		}
	}

	public int getAfterObjectIndex() {
		return MathUtils.min(_indexNext, _afterObjects.size) - 1;
	}

	public float getAfterObjectX() {
		final AfterObject o = _afterObjects.get(getAfterObjectIndex());
		if (o != null) {
			return drawX(o.x);
		}
		return 0f;
	}

	public float getAfterObjectY() {
		final AfterObject o = _afterObjects.get(getAfterObjectIndex());
		if (o != null) {
			return drawY(o.y);
		}
		return 0f;
	}

	public float getAfterObjectWidth() {
		final AfterObject o = _afterObjects.get(getAfterObjectIndex());
		if (o != null) {
			return o.width;
		}
		return 0f;
	}

	public float getAfterObjectHeight() {
		final AfterObject o = _afterObjects.get(getAfterObjectIndex());
		if (o != null) {
			return o.height;
		}
		return 0f;
	}

	public boolean containsAfterObject(float x, float y) {
		return containsAfterObject(x, y, 1f, 1f);
	}

	public boolean containsAfterObject(float x, float y, float w, float h) {
		final AfterObject o = _afterObjects.get(getAfterObjectIndex());
		if (o != null) {
			return CollisionHelper.contains(drawX(_startX + o.x), drawY(_startY + o.y), o.width, o.height, x, y, w, h);
		}
		return false;
	}

	public boolean intersectsAfterObject(float x, float y) {
		return intersectsAfterObject(x, y, 1f, 1f);
	}

	public boolean intersectsAfterObject(float x, float y, float w, float h) {
		final AfterObject o = _afterObjects.get(getAfterObjectIndex());
		if (o != null) {
			return CollisionHelper.intersects(drawX(_startX + o.x), drawY(_startY + o.y), o.width, o.height, x, y, w,
					h);
		}
		return false;
	}
	
	public float getStartX() {
		return _startX;
	}

	public AfterImageEffect setStartX(float sx) {
		this._startX = sx;
		return this;
	}

	public float getStartY() {
		return _startY;
	}

	public AfterImageEffect setStartY(float sy) {
		this._startY = sy;
		return this;
	}

	public float getStartWidth() {
		return _startWidth;
	}

	public AfterImageEffect setStartWidth(float sw) {
		this._startWidth = sw;
		return this;
	}

	public float getStartHeight() {
		return _startHeight;
	}

	public AfterImageEffect setStartHeight(float sh) {
		this._startHeight = sh;
		return this;
	}

	public boolean isPlaying() {
		return _playing;
	}

	public boolean isMoveLooping() {
		return _looping;
	}

	public AfterImageEffect setMoveLoop(boolean l) {
		this._looping = l;
		return this;
	}

	public AfterImageEffect clearData() {
		this._indexNext = 0;
		this._completed = false;
		this._displayCompleted = false;
		this._inited = _playing = false;
		return this;
	}

	public float getOrbitValue() {
		return _orbitValue;
	}

	public AfterImageEffect setOrbitValue(float o) {
		this._orbitValue = o;
		return this;
	}

	public int getMoveOrbit() {
		return _moveOrbit;
	}

	public AfterImageEffect setMoveOrbit(int m) {
		this._moveOrbit = m;
		return this;
	}

	@Override
	public AfterImageEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	public AfterImageEffect reset() {
		super.reset();
		this.clearData();
		this.start();
		return this;
	}

}