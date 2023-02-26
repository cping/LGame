/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import loon.LObject;
import loon.LSysException;
import loon.LSystem;
import loon.Screen;
import loon.action.map.Config;
import loon.action.map.TileMap;
import loon.events.GameKey;
import loon.geom.AABB;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.GLEx;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

/**
 * 该类为0.3.3版最新增加的Screen类，图形渲染使用单一的SpriteBatch，相较于使用GLEx，更适合多纹理渲染。
 * <p>
 * 
 * 并且支持直接绑定Loon的物理引擎。
 * 
 */
public abstract class SpriteBatchScreen extends Screen implements Config {

	private float objX = 0, objY = 0;

	private SpriteBatch _batch;

	private TArray<ActionObject> objects;

	private TArray<ActionObject> pendingAdd;

	private TArray<ActionObject> pendingRemove;

	private TArray<TileMap> tiles = new TArray<TileMap>(10);

	private Vector2f offset = new Vector2f();

	private LObject<?> follow;

	private TileMap indexTile;

	protected UpdateListener updateListener;

	private boolean _useGLEx = false;

	private boolean _fixed = false;

	private float _dt = LSystem.DEFAULT_EASE_DELAY;


	public float getTimeStep() {
		return this._dt;
	}

	public void setTimeStep(float dt) {
		this._dt = dt;
	}

	public abstract void onResume();

	@Override
	public void resume() {
		onResume();
	}

	public abstract void onPause();

	@Override
	public void pause() {
		onPause();
	}

	public boolean isFixed() {
		return _fixed;
	}

	public interface UpdateListener {

		public void act(ActionObject obj, long elapsedTime);

	}

	public SpriteBatchScreen() {
		super();
	}

	public SpriteBatch getSpriteBatch() {
		return _batch;
	}

	protected void init() {
		this.objects = new TArray<ActionObject>(10);
		this.pendingAdd = new TArray<ActionObject>(10);
		this.pendingRemove = new TArray<ActionObject>(10);
		this._dt = 1 / LSystem.base().setting.fps;
	}

	public void commits() {
		if (isClosed()) {
			return;
		}
		final int additionCount = pendingAdd.size;
		if (additionCount > 0) {
			for (int i = 0; i < additionCount; i++) {
				ActionObject object = pendingAdd.get(i);
				objects.add(object);
			}
			pendingAdd.clear();
		}
		final int removalCount = pendingRemove.size;
		if (removalCount > 0) {
			for (int i = 0; i < removalCount; i++) {
				ActionObject object = pendingRemove.get(i);
				objects.remove(object);
			}
			pendingRemove.clear();
		}
	}

	public ActionObject add(ActionObject object) {
		pendingAdd.add(object);
		return object;
	}

	public ActionObject remove(ActionObject object) {
		pendingRemove.add(object);
		return object;
	}

	public void removeTileObjects() {
		final int count = objects.size;
		final Object[] objectArray = objects.toArray();
		for (int i = 0; i < count; i++) {
			ActionObject o = (ActionObject) objectArray[i];
			pendingRemove.add(o);
		}
		pendingAdd.clear();
	}

	public ActionObject findObject(float x, float y) {
		for (ActionObject o : objects) {
			if ((o.getX() == x && o.getY() == y) || o.getRectBox().contains(x, y)) {
				return o;
			}
		}
		return null;
	}

	public TileMap getIndexTile() {
		return indexTile;
	}

	public void setIndexTile(TileMap indexTile) {
		this.indexTile = indexTile;
	}

	public void follow(LObject<?> o) {
		this.follow = o;
	}

	public final void onLoad() {
		try {
			init();
			if (_batch == null) {
				_batch = new SpriteBatch(512);
			}
			_batch.setBlendState(BlendState.Null);
			onLoading();
		} catch (Throwable cause) {
			LSystem.error("SpriteBatchScreen onLoad exception", cause);
		}
	}

	protected void onLoading() {

	}

	@Override
	public final void onLoaded() {
		// 最先绘制用户画面
		setFristOrder(DRAW_USER_PAINT());
		// 其次绘制精灵
		setSecondOrder(DRAW_SPRITE_PAINT());
		// 最后绘制桌面
		setLastOrder(DRAW_DESKTOP_PAINT());
		create();
	}

	public abstract void create();

	public void setOffset(TileMap tile, float sx, float sy) {
		offset.set(sx, sy);
		tile.setOffset(offset);
	}

	public final Vector2f getOffset() {
		return offset;
	}

	public void putTileMap(TileMap t) {
		tiles.add(t);
	}

	public void removeTileMap(TileMap t) {
		tiles.remove(t);
	}

	public void addTileObject(ActionObject o) {
		add(o);
	}

	public JumpObject addJumpObject(float x, float y, float w, float h, Animation a) {
		JumpObject o = null;
		if (indexTile != null) {
			o = new JumpObject(x, y, w, h, a, indexTile);
		} else if (tiles.size > 0) {
			o = new JumpObject(x, y, w, h, a, tiles.get(0));
		} else {
			return null;
		}
		add(o);
		return o;
	}

	public MoveObject addMoveObject(float x, float y, float w, float h, Animation a) {
		MoveObject o = null;
		if (indexTile != null) {
			o = new MoveObject(x, y, w, h, a, indexTile);
		} else if (tiles.size > 0) {
			o = new MoveObject(x, y, w, h, a, tiles.get(0));
		} else {
			return null;
		}
		add(o);
		return o;
	}

	public void removeTileObject(ActionObject o) {
		remove(o);
	}

	protected void updating(LTimerContext timer) {

	}

	@Override
	public void alter(LTimerContext timer) {
		if (!isOnLoadComplete()) {
			return;
		}
		if (follow != null) {
			for (TileMap tile : tiles) {
				float offsetX = getHalfWidth() - follow.getX();
				offsetX = MathUtils.min(offsetX, 0);
				offsetX = MathUtils.max(offsetX, getWidth() - tile.getWidth());

				float offsetY = getHalfHeight() - follow.getY();
				offsetY = MathUtils.min(offsetY, 0);
				offsetY = MathUtils.max(offsetY, getHeight() - tile.getHeight());

				setOffset(tile, offsetX, offsetY);
				tile.update(elapsedTime);
			}
		}
		updating(timer);
		if (objects != null) {
			for (ActionObject o : objects) {
				o.update(elapsedTime);
				if (updateListener != null) {
					updateListener.act(o, elapsedTime);
				}
			}
		}
		update(elapsedTime);
		commits();
	}

	protected void drawing(GLEx g, SpriteBatch batch) {

	}

	@Override
	public final void draw(GLEx g) {
		if (isOnLoadComplete()) {
			if (_batch == null || _useGLEx) {
				for (TileMap tile : tiles) {
					tile.draw(g, _batch, offset.x(), offset.y());
				}
				for (ActionObject o : objects) {
					objX = o.getX() + offset.x;
					objY = o.getY() + offset.y;
					if (intersects(objX, objY, o.getWidth(), o.getHeight()) || contains(objX, objY)) {
						o.draw(g, offset.x, offset.y);
					}
				}
				drawing(g, _batch);
			} else {
				synchronized (_batch) {
					try {
						_batch.begin();
						before(_batch);
						for (TileMap tile : tiles) {
							tile.draw(g, _batch, offset.x(), offset.y());
						}
						for (ActionObject o : objects) {
							objX = o.getX() + offset.x;
							objY = o.getY() + offset.y;
							if (intersects(objX, objY, o.getWidth(), o.getHeight()) || contains(objX, objY)) {
								o.draw(_batch, offset.x, offset.y);
							}
						}
						drawing(g, _batch);
						after(_batch);
					} finally {
						_batch.end();
					}
				}
			}
		}

	}

	public abstract void after(SpriteBatch _batch);

	public abstract void before(SpriteBatch _batch);

	@Override
	public final void onKeyDown(GameKey e) {
		press(e);
	}

	public abstract void press(GameKey e);

	@Override
	public final void onKeyUp(GameKey e) {
		release(e);
	}

	public abstract void release(GameKey e);

	public abstract void update(long elapsedTime);

	public abstract void dispose();

	public boolean isUseGLEx() {
		return _useGLEx;
	}

	public void setUseGLEx(boolean u) {
		this._useGLEx = u;
	}

	@Override
	public void close() {
		if (_batch != null) {
			_batch.close();
			_batch = null;
		}
		if (indexTile != null) {
			indexTile.close();
			indexTile = null;
		}
		if (objects != null) {
			objects.clear();
			objects = null;
		}
		if (pendingAdd != null) {
			pendingAdd.clear();
			pendingAdd = null;
		}
		if (pendingRemove != null) {
			pendingRemove.clear();
			pendingRemove = null;
		}
		updateListener = null;
		_fixed = false;
		tiles.clear();
		dispose();
	}

}
