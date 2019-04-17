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
import loon.LSystem;
import loon.Screen;
import loon.action.map.Config;
import loon.action.map.TileMap;
import loon.event.GameKey;
import loon.geom.AABB;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.GLEx;
import loon.physics.PBody;
import loon.physics.PPhysManager;
import loon.physics.PShape;
import loon.physics.PWorldBox;
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

	private boolean usePhysics = false;

	private PPhysManager _manager;

	private PWorldBox _box;

	private boolean _useGLEx = false;

	private boolean _fixed = false;

	private float _dt = LSystem.DEFAULT_EASE_DELAY;

	private RectBox _physicsRect;

	private ObjectMap<ActionObject, PBody> _bodys = new ObjectMap<ActionObject, PBody>(
			CollectionUtils.INITIAL_CAPACITY);

	private void limitWorld(boolean _fixed) {
		if (_fixed) {
			if (this._box == null) {
				this._box = new PWorldBox(_manager, 0f, 0f, getWidth(), getHeight());
			}
			if (_physicsRect != null) {
				this._box.set(_physicsRect.x, _physicsRect.y, _physicsRect.width, _physicsRect.height);
			}
			this._box.build();
		} else {
			if (_box != null) {
				this._box.removeWorld();
			}
		}
	}

	public PPhysManager getPhysicsManager() {
		if (!usePhysics) {
			throw LSystem.runThrow("You do not set the physics engine !");
		}
		return _manager;
	}

	public boolean isPhysics() {
		return usePhysics;
	}

	public void setPhysicsRect(float x, float y, float w, float h) {
		if (this._physicsRect == null) {
			this._physicsRect = new RectBox(x, y, w, h);
		} else {
			this._physicsRect.setBounds(x, y, w, h);
		}
	}

	public void setPhysics(boolean fix, PPhysManager man) {
		this._manager = man;
		this._fixed = fix;
		this.limitWorld(_fixed);
		this.usePhysics = true;
	}

	public void setPhysics(boolean fix, float scale, float gx, float gy) {
		if (_manager == null) {
			this._manager = new PPhysManager(scale, gx, gy);
		} else {
			this._manager.scale = scale;
			this._manager.gravity.set(gx, gy);
		}
		this._manager.setEnableGravity(true);
		this._manager.setStart(true);
		this._fixed = fix;
		this.limitWorld(_fixed);
		this.usePhysics = true;
	}

	public void setPhysics(boolean fix) {
		setPhysics(fix, 10F);
	}

	public void setPhysics(boolean fix, float scale) {
		if (_manager == null) {
			this._manager = new PPhysManager(scale);
		} else {
			this._manager.scale = scale;
		}
		this._manager.setEnableGravity(true);
		this._manager.setStart(true);
		this._fixed = fix;
		this.limitWorld(_fixed);
		this.usePhysics = true;
	}

	public float getTimeStep() {
		return this._dt;
	}

	public void setTimeStep(float dt) {
		this._dt = dt;
	}

	public abstract void onResume();

	@Override
	public void resume() {
		if (usePhysics) {
			_manager.setStart(true);
			_manager.setEnableGravity(true);
		}
		onResume();
	}

	public abstract void onPause();

	@Override
	public void pause() {
		if (usePhysics) {
			_manager.setStart(false);
			_manager.setEnableGravity(false);
		}
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
		if (usePhysics) {
			unbindPhysics(object);
		}
		return object;
	}

	public void removeTileObjects() {
		final int count = objects.size;
		final Object[] objectArray = objects.toArray();
		for (int i = 0; i < count; i++) {
			ActionObject o = (ActionObject) objectArray[i];
			pendingRemove.add(o);
			if (usePhysics) {
				unbindPhysics(o);
			}
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

	public PBody findPhysics(ActionObject o) {
		if (usePhysics) {
			PBody body = _bodys.get(o);
			return body;
		} else {
			throw LSystem.runThrow("You do not set the physics engine !");
		}
	}

	public void unbindPhysics(ActionObject o) {
		if (usePhysics) {
			PBody body = _bodys.remove(o);
			if (body != null) {
				body.setTag(null);
				_manager.world.removeBody(body);
			}
		}
	}

	public PBody addPhysics(boolean fix, ActionObject o, float density) {
		return bindPhysics(fix, add(o), density);
	}

	public PBody addPhysics(boolean fix, ActionObject o) {
		return bindPhysics(fix, add(o), 1F);
	}

	public PBody addTexturePhysics(boolean fix, ActionObject o, float density) throws Exception {
		return bindTexturePhysics(fix, add(o), density);
	}

	public PBody addTexturePhysics(boolean fix, ActionObject o) throws Exception {
		return bindTexturePhysics(fix, add(o), 1F);
	}

	public PBody bindPhysics(boolean fix, ActionObject o, float density) {
		if (usePhysics) {
			PBody body = _manager.addBox(fix, o.getRectBox(), MathUtils.toRadians(o.getRotation()), density);
			body.setTag(o);
			_bodys.put(o, body);
			return body;
		} else {
			throw LSystem.runThrow("You do not set the physics engine !");
		}
	}

	public PBody addCirclePhysics(boolean fix, ActionObject o, float density) {
		return bindCirclePhysics(fix, add(o), density);
	}

	public PBody addCirclePhysics(boolean fix, ActionObject o) {
		return bindCirclePhysics(fix, add(o), 1F);
	}

	public PBody bindCirclePhysics(boolean fix, ActionObject o) {
		return bindCirclePhysics(fix, add(o), 1F);
	}

	public PBody bindCirclePhysics(boolean fix, ActionObject o, float density) {
		if (usePhysics) {
			RectBox rect = o.getRectBox();
			float r = (rect.width + rect.height) / 4;
			PBody body = _manager.addCircle(fix, o.x(), o.y(), r, MathUtils.toRadians(o.getRotation()), density);
			body.setTag(o);
			_bodys.put(o, body);
			return body;
		} else {
			throw LSystem.runThrow("You do not set the physics engine !");
		}
	}

	public PBody bindTexturePhysics(boolean fix, ActionObject o, float density) throws Exception {
		if (usePhysics) {
			PBody body = _manager.addShape(fix, o.getAnimation().getSpriteImage(), MathUtils.toRadians(o.getRotation()),
					density);
			if (body.size() > 0) {
				body.inner_shapes()[0].setPosition(o.x() / _manager.scale, o.y() / _manager.scale);
			}
			body.setTag(o);
			_bodys.put(o, body);
			return body;
		} else {
			throw LSystem.runThrow("You do not set the physics engine !");
		}
	}

	public PBody bindTexturePhysics(boolean fix, ActionObject o) throws Exception {
		return bindTexturePhysics(fix, o, 1F);
	}

	public PBody bindPhysics(boolean fix, ActionObject o) {
		return bindPhysics(fix, o, 1F);
	}

	public PBody bindPhysics(PBody body, ActionObject o) {
		if (usePhysics) {
			body.setTag(o);
			_manager.addBody(body);
			_bodys.put(o, body);
			return body;
		} else {
			throw LSystem.runThrow("You do not set the physics engine !");
		}
	}

	protected void updating(LTimerContext timer) {

	}

	@Override
	public void alter(LTimerContext timer) {
		if (!isOnLoadComplete()) {
			return;
		}
		if (usePhysics) {
			if (_dt < 0) {
				_manager.step(timer.getMilliseconds());
			} else {
				_manager.step(_dt);
			}
		}
		if (follow != null) {
			if (usePhysics) {
				_manager.offset(follow.getX(), follow.getY());
			}
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
				if (usePhysics) {
					PBody body = _bodys.get(o);
					if (body != null) {
						PShape shape = body.inner_shapes()[0];
						final float rotation = (shape.getAngle() * MathUtils.RAD_TO_DEG) % 360;
						AABB aabb = shape.getAABB();

						o.setLocation(_manager.getScreenX(aabb.minX), _manager.getScreenY(aabb.minY));
						o.setRotation(rotation);
					}
				}
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
		if (usePhysics) {
			_manager.setStart(false);
			_manager.setEnableGravity(false);
			_bodys.clear();
		}
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
		usePhysics = false;
		_manager = null;
		_box = null;
		_fixed = false;
		tiles.clear();
		dispose();
	}

}
