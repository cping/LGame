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

	private float drawPosX;

	private float drawPosY;

	private ScrollEffect scrollBackground;

	private UpdateListener updateListener;

	private TArray<ActionObject> objects;

	private TArray<ActionObject> pendingAdd;

	private TArray<ActionObject> pendingRemove;

	private TArray<TileMap> tiles;

	private TileMap currentTileMap;

	private Vector2f offset;

	private ActionBind follow;

	private StateManager stateManager;

	private boolean existing;

	private float percent;

	private float maxPercent;

	public final Stage setPercentMaximum(float max) {
		this.maxPercent = MathUtils.clamp(max, 0f, 100f);
		return this;
	}

	public final Stage setPercent(float cur, float max) {
		this.percent = MathUtils.clamp(cur, 0f, 100f);
		this.maxPercent = MathUtils.clamp(max, 0f, 100f);
		return this;
	}

	public final Stage updatePercent(float num) {
		this.percent = MathUtils.clamp(num, 0f, 100f) / maxPercent;
		return this;
	}

	public final Stage addPercent() {
		return updatePercent(percent++);
	}

	public final Stage removePercent() {
		return updatePercent(percent--);
	}

	public final Stage resetPercent() {
		this.percent = 0f;
		return this;
	}

	public final int getMaximumPercent() {
		return (int) maxPercent;
	}

	public final int getPercent() {
		return (int) percent;
	}

	protected StateManager createStateManager() {
		if (stateManager == null) {
			stateManager = new StateManager();
			existing = true;
		}
		return this.stateManager;
	}

	public StateManager getStateManager() {
		return createStateManager();
	}

	public boolean peekStateEquals(String name) {
		return peekState().getName().equals(name);
	}

	public State peekState() {
		this.stateManager = createStateManager();
		return stateManager.peek();
	}

	public Stage playState(String name) {
		this.stateManager = createStateManager();
		stateManager.play(name);
		return this;
	}

	public Stage playState(int idx) {
		this.stateManager = createStateManager();
		stateManager.play(idx);
		return this;
	}

	public Stage removeState(String name) {
		this.stateManager = createStateManager();
		stateManager.remove(name);
		return this;
	}

	public Stage removeState(int idx) {
		this.stateManager = createStateManager();
		stateManager.remove(idx);
		return this;
	}

	public Stage addState(State state) {
		return addState(null, state);
	}

	public Stage addState(String name, State state) {
		this.stateManager = createStateManager();
		stateManager.add(name, state);
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
		if (pendingAdd != null) {
			final int additionCount = pendingAdd.size;
			if (additionCount > 0) {
				for (int i = 0; i < additionCount; i++) {
					objects.add(pendingAdd.get(i));
				}
				pendingAdd.clear();
			}
		}
		if (pendingRemove != null) {
			final int removalCount = pendingRemove.size;
			if (removalCount > 0) {
				for (int i = 0; i < removalCount; i++) {
					objects.remove(pendingRemove.get(i));
				}
				pendingRemove.clear();
			}
		}
	}

	public abstract void create();

	@Override
	public void onLoad() {
		try {
			this.objects = new TArray<ActionObject>();
			this.pendingAdd = new TArray<ActionObject>();
			this.pendingRemove = new TArray<ActionObject>();
			this.tiles = new TArray<TileMap>();
			this.offset = Vector2f.ZERO();
			create();
			if (existing) {
				stateManager.load();
			}
		} catch (Throwable cause) {
			LSystem.error("Screen create failure", cause);
		}
	}

	@Override
	public void alter(LTimerContext timer) {
		if (scrollBackground != null) {
			scrollBackground.update(timer.timeSinceLastUpdate);
		}
		if (follow != null && tiles != null && tiles.size > 0) {
			for (TileMap tile : tiles) {
				float offsetX = getHalfWidth() - follow.getX();
				offsetX = MathUtils.min(offsetX, 0);
				offsetX = MathUtils.max(offsetX, getWidth() - tile.getWidth());

				float offsetY = getHalfHeight() - follow.getY();
				offsetY = MathUtils.min(offsetY, 0);
				offsetY = MathUtils.max(offsetY, getHeight() - tile.getHeight());

				setOffset(tile, offsetX, offsetY);
				tile.update(timer.timeSinceLastUpdate);
			}
		}
		if (objects != null && objects.size > 0) {
			for (ActionObject o : objects) {
				if (updateListener != null) {
					updateListener.act(o, timer.timeSinceLastUpdate);
				}
				o.update(timer.timeSinceLastUpdate);
			}
		}
		update(timer);
		commits();
		if (existing) {
			stateManager.update(timer.getMilliseconds());
		}
	}

	@Override
	public void draw(GLEx g) {
		background(g);
		if (scrollBackground != null) {
			scrollBackground.paint(g);
		}
		if (tiles != null && tiles.size > 0) {
			for (TileMap tile : tiles) {
				tile.draw(g, offset.x(), offset.y());
			}
		}
		if (objects != null && objects.size > 0) {
			for (ActionObject o : objects) {
				drawPosX = o.getX() + offset.x;
				drawPosY = o.getY() + offset.y;
				if (intersects(drawPosX, drawPosY, o.getWidth(), o.getHeight()) || contains(drawPosX, drawPosY)) {
					o.createUI(g, offset.x, offset.y);
				}
			}
		}
		paint(g);
		if (existing) {
			stateManager.paint(g);
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
		return this.currentTileMap;
	}

	public Stage setIndexTile(TileMap indexTile) {
		this.currentTileMap = indexTile;
		return this;
	}

	public Stage follow(ActionObject o) {
		this.follow = o;
		return this;
	}

	public Stage setOffset(TileMap tile, float sx, float sy) {
		offset.set(sx, sy);
		tile.setOffset(offset);
		return this;
	}

	public final Vector2f getOffset() {
		return offset;
	}

	public Stage putTileMap(TileMap t) {
		tiles.add(t);
		return this;
	}

	public Stage removeTileMap(TileMap t) {
		tiles.remove(t);
		return this;
	}

	public Stage addTileObject(ActionObject o) {
		add(o);
		return this;
	}

	public JumpObject addJumpObject(float x, float y, float w, float h, Animation a) {
		JumpObject o = null;
		if (currentTileMap != null) {
			o = new JumpObject(x, y, w, h, a, currentTileMap);
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
		if (currentTileMap != null) {
			o = new MoveObject(x, y, w, h, a, currentTileMap);
		} else if (tiles.size > 0) {
			o = new MoveObject(x, y, w, h, a, tiles.get(0));
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
		pendingAdd.add(o);
		return o;
	}

	public ActionObject remove(ActionObject o) {
		pendingRemove.add(o);
		return o;
	}

	public Stage removeTileObjects() {
		final int count = objects.size;
		final Object[] objectArray = objects.toArray();
		for (int i = 0; i < count; i++) {
			ActionObject o = (ActionObject) objectArray[i];
			pendingRemove.add(o);
		}
		pendingAdd.clear();
		return this;
	}

	public ActionObject findObject(float x, float y) {
		for (ActionObject o : objects) {
			if ((o.getX() == x && o.getY() == y) || o.getRectBox().contains(x, y)) {
				return o;
			}
		}
		return null;
	}

	public UpdateListener getUpdateListener() {
		return updateListener;
	}

	public Stage setUpdateListener(UpdateListener update) {
		this.updateListener = update;
		return this;
	}

	public Stage setScrollBackground(ScrollEffect scrollBackground) {
		this.scrollBackground = scrollBackground;
		return this;
	}

	public Stage setScrollBackground(int dir, String path) {
		this.scrollBackground = new ScrollEffect(dir, path);
		return this;
	}

	public ScrollEffect getScrollBackground() {
		return scrollBackground;
	}

	public void dispose() {
	}

	@Override
	public void close() {
		this.existing = false;
		if (this.pendingAdd != null) {
			this.pendingAdd.clear();
		}
		if (this.pendingRemove != null) {
			this.pendingRemove.clear();
		}
		if (this.tiles != null) {
			this.tiles.clear();
		}
		if (objects != null) {
			for (int i = 0; i < objects.size; i++) {
				ActionObject obj = objects.get(i);
				if (obj != null) {
					obj.close();
				}
			}
			objects.clear();
		}
		if (stateManager != null) {
			stateManager.close();
			stateManager = null;
		}
		if (scrollBackground != null) {
			scrollBackground.close();
			scrollBackground = null;
		}
		percent = maxPercent = 0;
		dispose();
	}

}
