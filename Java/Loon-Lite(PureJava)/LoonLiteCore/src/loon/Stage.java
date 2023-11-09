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
import loon.action.map.Field2D;
import loon.action.map.Level;
import loon.action.map.TileMap;
import loon.action.sprite.ActionObject;
import loon.action.sprite.Animation;
import loon.action.sprite.JumpObject;
import loon.action.sprite.MoveObject;
import loon.action.sprite.TextureObject;
import loon.action.sprite.effect.ScrollEffect;
import loon.component.LLayer;
import loon.events.GameTouch;
import loon.events.UpdateListener;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.res.loaders.PreloadAssets;
import loon.utils.timer.LTimerContext;

/**
 * 一个Screen的衍生抽象类,除了create函数,什么都不必实现.
 * 
 * 希望纯组件构建游戏时(也就是一个create接口满足一切时)可以使用此类派生画面
 */
public abstract class Stage extends Screen {

	/**
	 * 预加载进度管理器
	 */
	protected class PreloadProcess extends RealtimeProcess {

		private Stage _stage;

		private PreloadAssets _assets;

		private float _maxValue;

		public PreloadProcess(Stage stage, PreloadAssets assets, float max) {
			this._stage = stage;
			this._assets = assets;
			this._maxValue = max;
		}

		@Override
		public void run(LTimerContext time) {

			if (!_assets.completed()) {
				_assets.detection();
			}

			_stage.updatePercent((_maxValue - _assets.waiting()), _maxValue);
			_stage.preloadProgress(_stage._percent);

			if (_assets.completed()) {
				_stage.create();
				_stage.createState();
				kill();
			}

		}
	}

	private float _drawPosX;

	private float _drawPosY;

	private int _defTileSize = 32;

	private ScrollEffect _scrollBackground;

	private UpdateListener _updateListener;

	private PreloadAssets _preAssets;

	private TArray<ActionObject> _objects;

	private TArray<ActionObject> _pendingAdd;

	private TArray<ActionObject> _pendingRemove;

	private TArray<TileMap> _childTiles;

	private Level _currentLevel;

	private TileMap _currentTileMap;

	private Vector2f _currentOffset;

	private ActionBind _currentFollow;

	private StateManager _stateManager;

	private boolean _existing;

	private float _preMaxFileCount;

	private float _percent;

	private float _maxPercent;

	private long _preloadInterval;

	public final Stage setPercentMax(float max) {
		this._maxPercent = MathUtils.clamp(max, 0f, LSystem.DEFAULT_MAX_PRE_SIZE);
		return this;
	}

	public final Stage setPercent(float cur) {
		return setPercent(cur, LSystem.DEFAULT_MAX_PRE_SIZE);
	}

	public final Stage setPercent(float cur, float max) {
		this._percent = MathUtils.clamp(cur, 0f, LSystem.DEFAULT_MAX_PRE_SIZE);
		this._maxPercent = MathUtils.clamp(max, 0f, LSystem.DEFAULT_MAX_PRE_SIZE);
		return this;
	}

	public final Stage updatePercent(float num) {
		return updatePercent(num, _maxPercent);
	}

	public final Stage updatePercent(float num, float max) {
		this._percent = MathUtils.clamp(num, 0f, LSystem.DEFAULT_MAX_PRE_SIZE) / max;
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

	public final float getMaxPercent() {
		return _maxPercent;
	}

	public final float getPercent() {
		return _percent;
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

	/**
	 * 资源预加载用函数,异步加载指定资源
	 * 
	 * @param assets
	 */
	protected void preload(PreloadAssets assets) {
	}

	/**
	 * 预载资源已完成进度
	 * 
	 * @param percent
	 */
	protected void preloadProgress(float percent) {

	}

	public PreloadAssets getPreloadAssets() {
		return this._preAssets;
	}

	@Override
	public void onLoad() {
		try {
			this._objects = new TArray<ActionObject>();
			this._preAssets = new PreloadAssets();
			this._pendingAdd = new TArray<ActionObject>();
			this._pendingRemove = new TArray<ActionObject>();
			this._childTiles = new TArray<TileMap>();
			this._currentOffset = Vector2f.ZERO();

			this.preload(_preAssets);

			this.setPercentMax(this._preMaxFileCount = _preAssets.waiting());

			if (_preMaxFileCount > LSystem.DEFAULT_MAX_PRE_SIZE) {
				throw new LSysException(
						"The count of preloaded data cannot be greater than " + LSystem.DEFAULT_MAX_PRE_SIZE);
			}

			if (_preMaxFileCount == 0) {
				this.create();
				this.createState();
			} else {
				PreloadProcess preload = new PreloadProcess(this, _preAssets, this._preMaxFileCount);
				preload.setProcessType(GameProcessType.Preload);
				preload.setDelay(this._preloadInterval);
				RealtimeProcessManager.get().addProcess(preload);
			}
		} catch (Throwable cause) {
			LSystem.error("Screen create failure", cause);
		}
	}

	private void createState() {
		if (_existing) {
			_stateManager.load();
		}
	}

	public LLayer createLayer(int w, int h) {
		final LLayer llayer = new LLayer(w, h);
		add(llayer);
		return llayer;
	}

	public LLayer createLayer() {
		return createLayer(getWidth(), getHeight());
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
		process(timer.timeSinceLastUpdate);
	}

	public void process(long elapsedTime) {
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

	public Stage createLevel(int tw, int th, String[]... map) {
		_currentLevel = new Level(tw, th, map);
		return this;
	}

	public Stage createLevel(int tw, int th, int[][] map) {
		_currentLevel = new Level(tw, th, map);
		return this;
	}

	public Stage createLevel(Field2D map) {
		_currentLevel = new Level(map);
		return this;
	}

	public Stage createLevel() {
		_currentLevel = new Level();
		return this;
	}

	public TileMap createTileMap() {
		if (_currentLevel != null) {
			_currentTileMap = new TileMap(_currentLevel.getMap());
		} else {
			_currentTileMap = new TileMap(LSystem.viewSize.newField2D(_defTileSize, _defTileSize));
		}
		return _currentTileMap;
	}

	public Stage addLevel(int tw, int th, String... s) {
		if (_currentLevel != null) {
			_currentLevel.addMap(tw, th, s);
		}
		return this;
	}

	public Stage addLevel(int tw, int th, int[][] map) {
		if (_currentLevel != null) {
			_currentLevel.addMap(tw, th, map);
		}
		return this;
	}

	public Field2D removeLevel(int idx) {
		if (_currentLevel != null) {
			_currentLevel.removeMap(idx);
		}
		return null;
	}

	public Stage addLevel(Field2D map) {
		if (_currentLevel != null) {
			_currentLevel.addMap(map);
		}
		return this;
	}

	public Stage setLevelIndex(int idx) {
		if (_currentLevel != null) {
			_currentLevel.setCurrentIndex(idx);
		}
		return this;
	}

	public int getLevelIndex() {
		if (_currentLevel != null) {
			return _currentLevel.getCurrentIndex();
		}
		return -1;
	}

	public TileMap getIndexTile() {
		return this._currentTileMap;
	}

	public Stage setIndexTile(TileMap indexTile) {
		this._currentTileMap = indexTile;
		return this;
	}

	public Field2D getLevelMap() {
		if (this._currentTileMap != null) {
			return this._currentTileMap.getField2D();
		}
		if (this._childTiles.size > 0) {
			return this._childTiles.get(0).getField2D();
		}
		if (this._currentLevel != null) {
			return this._currentLevel.getMap();
		} else if (this._currentLevel == null) {
			createLevel();
			return this._currentLevel.getMap();
		}
		return null;
	}

	public int getDefaultTileSize() {
		return _defTileSize;
	}

	public Stage setDefaultTileSize(int t) {
		this._defTileSize = t;
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

	public JumpObject addJumpObject(float x, float y, float w, float h, Animation a) {
		JumpObject o = null;
		if (_currentTileMap != null) {
			o = new JumpObject(x, y, w, h, a, _currentTileMap);
		} else if (_childTiles.size > 0) {
			o = new JumpObject(x, y, w, h, a, _childTiles.get(0));
		} else {
			o = new JumpObject(x, y, w, h, a, createTileMap());
		}
		addTileObject(o);
		return o;
	}

	public JumpObject addJumpObject(float x, float y, float w, float h, String path) {
		return addJumpObject(x, y, w, h, Animation.getDefaultAnimation(path));
	}

	public JumpObject addJumpObject(float x, float y, Animation a) {
		Vector2f size = getTileSize();
		return addJumpObject(x, y, size.x, size.y, a);
	}

	public JumpObject addJumpObject(float x, float y, String path) {
		Vector2f size = getTileSize();
		return addJumpObject(x, y, size.x, size.y, path);
	}

	public MoveObject addMoveObject(float x, float y, float w, float h, Animation a) {
		MoveObject o = null;
		if (_currentTileMap != null) {
			o = new MoveObject(x, y, w, h, a, _currentTileMap);
		} else if (_childTiles.size > 0) {
			o = new MoveObject(x, y, w, h, a, _childTiles.get(0));
		} else {
			o = new MoveObject(x, y, w, h, a, createTileMap());
		}
		addTileObject(o);
		return o;
	}

	public MoveObject addMoveObject(float x, float y, float w, float h, String path) {
		return addMoveObject(x, y, w, h, Animation.getDefaultAnimation(path));
	}

	public MoveObject addMoveObject(float x, float y, Animation a) {
		Vector2f size = getTileSize();
		return addMoveObject(x, y, size.x, size.y, a);
	}

	public MoveObject addMoveObject(float x, float y, String path) {
		Vector2f size = getTileSize();
		return addMoveObject(x, y, size.x, size.y, path);
	}

	public TextureObject addTextureObject(float x, float y, float w, float h, LTexture tex) {
		TextureObject o = null;
		if (_currentTileMap != null) {
			o = new TextureObject(x, y, w, h, tex, _currentTileMap);
		} else if (_childTiles.size > 0) {
			o = new TextureObject(x, y, w, h, tex, _childTiles.get(0));
		} else {
			o = new TextureObject(x, y, w, h, tex, createTileMap());
		}
		addTileObject(o);
		return o;
	}

	public TextureObject addTextureObject(float x, float y, float w, float h, String path) {
		return addTextureObject(x, y, w, h, LTextures.loadTexture(path));
	}

	public TextureObject addTextureObject(float x, float y, LTexture tex) {
		Vector2f size = getTileSize();
		return addTextureObject(x, y, size.x, size.y, tex);
	}

	public TextureObject addTextureObject(float x, float y, String path) {
		Vector2f size = getTileSize();
		return addTextureObject(x, y, size.x, size.y, path);
	}

	protected Vector2f getTileSize() {
		final Vector2f size = new Vector2f();
		if (_currentLevel != null) {
			size.set(_currentLevel.getMap().getTileWidth(), _currentLevel.getMap().getTileHeight());
		} else if (_currentTileMap != null) {
			size.set(_currentTileMap.getTileWidth(), _currentTileMap.getTileHeight());
		} else if (_childTiles.size > 0) {
			size.set(_childTiles.get(0).getTileWidth(), _childTiles.get(0).getTileHeight());
		} else {
			size.set(_defTileSize);
		}
		return size;
	}

	public ActionObject addTileObject(ActionObject o) {
		_pendingAdd.add(o);
		return o;
	}

	public ActionObject removeTileObject(ActionObject o) {
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
			if ((o.getX() == x && o.getY() == y) || o.getRectBox().intersects(x, y)) {
				return o;
			}
		}
		return null;
	}

	public float getPreloadInterval() {
		return _preloadInterval / LSystem.SECOND;
	}

	public Stage setPreloadInterval(float second) {
		this._preloadInterval = (long) (LSystem.SECOND * second);
		return this;
	}

	public UpdateListener getUpdateListener() {
		return _updateListener;
	}

	public Stage setUpdateListener(UpdateListener update) {
		this._updateListener = update;
		return this;
	}

	public Stage setScrollBackground(ScrollEffect scrollBackground) {
		this._scrollBackground = scrollBackground;
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
		if (this._currentLevel != null) {
			this._currentLevel.close();
			this._currentLevel = null;
		}
		if (this._currentTileMap != null) {
			this._currentTileMap.close();
			this._currentTileMap = null;
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
		if (_preAssets != null) {
			_preAssets.close();
			_preAssets = null;
		}
		_preMaxFileCount = _percent = _maxPercent = 0;
		_preloadInterval = 0;
		dispose();
	}

}
