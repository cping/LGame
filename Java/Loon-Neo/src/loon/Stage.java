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
package loon;

import loon.action.ActionBind;
import loon.action.map.TileMap;
import loon.action.sprite.ActionObject;
import loon.action.sprite.Animation;
import loon.action.sprite.JumpObject;
import loon.action.sprite.MoveObject;
import loon.action.sprite.effect.ScrollEffect;
import loon.events.GameTouch;
import loon.events.UpdateListener;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

/**
 * 一个Screen的衍生抽象类,除了create函数,什么都不必实现.
 * 
 * 希望纯组件构建游戏时(也就是一个create接口满足一切时)可以使用此类派生画面
 */
public abstract class Stage extends Screen {

	private float _drawPosX;

	private float _drawPosY;

	private ScrollEffect _scrollBackground;

	private UpdateListener _updateListener;

	private TArray<ActionObject> _objects;

	private TArray<ActionObject> _pendingAdd;

	private TArray<ActionObject> _pendingRemove;

	private TArray<TileMap> _childTiles;

	private TileMap _currentTileMap;

	private Vector2f _currentOffset;

	private ActionBind _currentFollow;

	private StateManager _stateManager;

	private boolean _existing;

	private float _percent;

	private float _maxPercent;

	public final Stage setPercentMaximum(float max) {
		this._maxPercent = MathUtils.clamp(max, 0f, 100f);
		return this;
	}

	public final Stage setPercent(float cur, float max) {
		this._percent = MathUtils.clamp(cur, 0f, 100f);
		this._maxPercent = MathUtils.clamp(max, 0f, 100f);
		return this;
	}

	public final Stage updatePercent(float num) {
		this._percent = MathUtils.clamp(num, 0f, 100f) / _maxPercent;
		return this;
	}

	public final Stage addPercent() {
		return updatePercent(_percent++);
	}

	public final Stage removePercent() {
		return updatePercent(_percent--);
	}

	public final Stage resetPercent() {
		this._percent = 0f;
		return this;
	}

	public final int getMaximumPercent() {
		return (int) _maxPercent;
	}

	public final int getPercent() {
		return (int) _percent;
	}

	protected StateManager createStateManager() {
		if (_stateManager == null) {
			_stateManager = new StateManager();
			_existing = true;
		}
		return this._stateManager;
	}

	public StateManager getStateManager() {
		return createStateManager();
	}

	public boolean peekStateEquals(String name) {
		return peekState().getName().equals(name);
	}

	public State peekState() {
		this._stateManager = createStateManager();
		return _stateManager.peek();
	}

	public Stage playState(String name) {
		this._stateManager = createStateManager();
		_stateManager.play(name);
		return this;
	}

	public Stage playState(int idx) {
		this._stateManager = createStateManager();
		_stateManager.play(idx);
		return this;
	}

	public Stage playNextState() {
		this._stateManager = createStateManager();
		_stateManager.playNext();
		return this;
	}

	public Stage playBackState() {
		this._stateManager = createStateManager();
		_stateManager.playBack();
		return this;
	}
	
	public Stage removeState(String name) {
		this._stateManager = createStateManager();
		_stateManager.remove(name);
		return this;
	}

	public Stage removeState(int idx) {
		this._stateManager = createStateManager();
		_stateManager.remove(idx);
		return this;
	}

	public Stage addState(State state) {
		return addState(null, state);
	}

	public Stage addState(String name, State state) {
		this._stateManager = createStateManager();
		_stateManager.add(name, state);
		return this;
	}

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	public void commits() {
		if (isClosed()) {
			return;
		}
		if (_pendingAdd != null) {
			final int additionCount = _pendingAdd.size;
			if (additionCount > 0) {
				for (int i = 0; i < additionCount; i++) {
					_objects.add(_pendingAdd.get(i));
				}
				_pendingAdd.clear();
			}
		}
		if (_pendingRemove != null) {
			final int removalCount = _pendingRemove.size;
			if (removalCount > 0) {
				for (int i = 0; i < removalCount; i++) {
					_objects.remove(_pendingRemove.get(i));
				}
				_pendingRemove.clear();
			}
		}
	}

	public abstract void create();

	@Override
	public void onLoad() {
		try {
			this._objects = new TArray<ActionObject>();
			this._pendingAdd = new TArray<ActionObject>();
			this._pendingRemove = new TArray<ActionObject>();
			this._childTiles = new TArray<TileMap>();
			this._currentOffset = Vector2f.ZERO();
			create();
			if (_existing) {
				_stateManager.load();
			}
		} catch (Throwable cause) {
			LSystem.error("Screen create failure", cause);
		}
	}

	@Override
	public void alter(LTimerContext timer) {
		if (_scrollBackground != null) {
			_scrollBackground.update(timer.timeSinceLastUpdate);
		}
		if (_currentFollow != null && _childTiles != null && _childTiles.size > 0) {
			for (TileMap tile : _childTiles) {
				float offsetX = getHalfWidth() - _currentFollow.getX();
				offsetX = MathUtils.min(offsetX, 0);
				offsetX = MathUtils.max(offsetX, getWidth() - tile.getWidth());

				float offsetY = getHalfHeight() - _currentFollow.getY();
				offsetY = MathUtils.min(offsetY, 0);
				offsetY = MathUtils.max(offsetY, getHeight() - tile.getHeight());

				setOffset(tile, offsetX, offsetY);
				tile.update(timer.timeSinceLastUpdate);
			}
		}
		if (_objects != null && _objects.size > 0) {
			for (ActionObject o : _objects) {
				if (_updateListener != null) {
					_updateListener.act(o, timer.timeSinceLastUpdate);
				}
				o.update(timer.timeSinceLastUpdate);
			}
		}
		update(timer);
		commits();
		if (_existing) {
			_stateManager.update(timer.getMilliseconds());
		}
	}

	@Override
	public void draw(GLEx g) {
		background(g);
		if (_scrollBackground != null) {
			_scrollBackground.paint(g);
		}
		if (_childTiles != null && _childTiles.size > 0) {
			for (TileMap tile : _childTiles) {
				tile.draw(g, _currentOffset.x(), _currentOffset.y());
			}
		}
		if (_objects != null && _objects.size > 0) {
			for (ActionObject o : _objects) {
				_drawPosX = o.getX() + _currentOffset.x;
				_drawPosY = o.getY() + _currentOffset.y;
				if (intersects(_drawPosX, _drawPosY, o.getWidth(), o.getHeight()) || contains(_drawPosX, _drawPosY)) {
					o.createUI(g, _currentOffset.x, _currentOffset.y);
				}
			}
		}
		paint(g);
		if (_existing) {
			_stateManager.paint(g);
		}
	}

	public void background(GLEx g) {
	}

	public void paint(GLEx g) {
	}

	public void update(LTimerContext timer) {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void touchDown(GameTouch e) {
	}

	@Override
	public void touchUp(GameTouch e) {
	}

	@Override
	public void touchMove(GameTouch e) {
	}

	@Override
	public void touchDrag(GameTouch e) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void pause() {
	}

	public TileMap getIndexTile() {
		return this._currentTileMap;
	}

	public Stage setIndexTile(TileMap indexTile) {
		this._currentTileMap = indexTile;
		return this;
	}

	public Stage follow(ActionObject o) {
		this._currentFollow = o;
		return this;
	}

	public Stage setOffset(TileMap tile, float sx, float sy) {
		_currentOffset.set(sx, sy);
		tile.setOffset(_currentOffset);
		return this;
	}

	public final Vector2f getOffset() {
		return _currentOffset;
	}

	public Stage putTileMap(TileMap t) {
		_childTiles.add(t);
		return this;
	}

	public Stage removeTileMap(TileMap t) {
		_childTiles.remove(t);
		return this;
	}

	public Stage addTileObject(ActionObject o) {
		add(o);
		return this;
	}

	public JumpObject addJumpObject(float x, float y, float w, float h, Animation a) {
		JumpObject o = null;
		if (_currentTileMap != null) {
			o = new JumpObject(x, y, w, h, a, _currentTileMap);
		} else if (_childTiles.size > 0) {
			o = new JumpObject(x, y, w, h, a, _childTiles.get(0));
		} else {
			return null;
		}
		add(o);
		return o;
	}

	public MoveObject addMoveObject(float x, float y, float w, float h, Animation a) {
		MoveObject o = null;
		if (_currentTileMap != null) {
			o = new MoveObject(x, y, w, h, a, _currentTileMap);
		} else if (_childTiles.size > 0) {
			o = new MoveObject(x, y, w, h, a, _childTiles.get(0));
		} else {
			return null;
		}
		add(o);
		return o;
	}

	public Stage removeTileObject(ActionObject o) {
		remove(o);
		return this;
	}

	public ActionObject add(ActionObject o) {
		_pendingAdd.add(o);
		return o;
	}

	public ActionObject remove(ActionObject o) {
		_pendingRemove.add(o);
		return o;
	}

	public Stage removeTileObjects() {
		final int count = _objects.size;
		final Object[] objectArray = _objects.toArray();
		for (int i = 0; i < count; i++) {
			ActionObject o = (ActionObject) objectArray[i];
			_pendingRemove.add(o);
		}
		_pendingAdd.clear();
		return this;
	}

	public ActionObject findObject(float x, float y) {
		for (ActionObject o : _objects) {
			if ((o.getX() == x && o.getY() == y) || o.getRectBox().contains(x, y)) {
				return o;
			}
		}
		return null;
	}

	public UpdateListener getUpdateListener() {
		return _updateListener;
	}

	public Stage setUpdateListener(UpdateListener update) {
		this._updateListener = update;
		return this;
	}

	public Stage setScrollBackground(ScrollEffect _scrollBackground) {
		this._scrollBackground = _scrollBackground;
		return this;
	}

	public Stage setScrollBackground(int dir, String path) {
		this._scrollBackground = new ScrollEffect(dir, path);
		return this;
	}

	public ScrollEffect getScrollBackground() {
		return _scrollBackground;
	}

	public void dispose() {
	}

	@Override
	public void close() {
		this._existing = false;
		if (this._pendingAdd != null) {
			this._pendingAdd.clear();
		}
		if (this._pendingRemove != null) {
			this._pendingRemove.clear();
		}
		if (this._childTiles != null) {
			this._childTiles.clear();
		}
		if (_objects != null) {
			for (int i = 0; i < _objects.size; i++) {
				ActionObject obj = _objects.get(i);
				if (obj != null) {
					obj.close();
				}
			}
			_objects.clear();
		}
		if (_stateManager != null) {
			_stateManager.close();
			_stateManager = null;
		}
		if (_scrollBackground != null) {
			_scrollBackground.close();
			_scrollBackground = null;
		}
		_percent = _maxPercent = 0;
		dispose();
	}

}
