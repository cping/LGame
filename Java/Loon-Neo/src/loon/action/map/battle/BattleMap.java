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
package loon.action.map.battle;

import java.util.Comparator;

import loon.action.map.battle.BattlePathFinder.PathResult;
import loon.action.map.battle.BattleType.ObjectState;
import loon.canvas.LColor;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 专属战斗用地图，和普通地图最大区别是，此地图全部由高度可控动画对象构建(也就是复杂特效可以具体到单个瓦片)
 */
public class BattleMap {

	private static class ObjectComparator implements Comparator<BattleMapObject> {

		@Override
		public int compare(BattleMapObject o1, BattleMapObject o2) {
			return MathUtils.compare(o1.renderPriority, o2.renderPriority);
		}

	}

	private final static ObjectComparator OBJ_COMPARATOR = new ObjectComparator();

	public final static float CAMERA_SMOOTH_FACTOR = 0.1f;

	private GameEventBus<GameEventType> _eventBus;

	private BattlePathFinder _pathFinder;

	private BattleTile[][] _mapTiles;

	private TArray<BattleMapObject> _objects = new TArray<BattleMapObject>();

	private BattleMapObject _cameraTarget = null;

	private BattleSelectManager _selectionManager;

	private boolean _dragging = false;

	private int _dragStartX, _dragStartY;

	private IsoConfig _isoConfig;

	private final int _map_width, _map_height;

	private int _screenWidth, _screenHeight;

	private final LColor _lightColor = new LColor();

	public BattleMap(GameEventBus<GameEventType> events, IsoConfig config, int maxMapWidth, int maxMapHeight,
			int screenWidth, int screenHeight) {
		this._eventBus = events;
		this._isoConfig = config;
		this._map_width = maxMapWidth;
		this._map_height = maxMapHeight;
		this._screenWidth = screenWidth;
		this._screenHeight = screenHeight;
	}

	public void createMap(GameEventBus<PathResult> pathResult, GameEventBus<BattleMapObject> bus, BattleTile[][] maps,
			int mapWidth, int mapHeight) {
		_selectionManager = new BattleSelectManager(bus);
		_pathFinder = new BattlePathFinder(pathResult, maps, mapWidth, mapHeight);
	}

	protected void sortObjects() {
		_objects.sort(OBJ_COMPARATOR);
	}

	protected void drawMap(GLEx g, float deltaTime) {
		final int startX = MathUtils.max(0, (int) (_isoConfig.offsetX / _isoConfig.tileWidth) - 1);
		final int endX = MathUtils.min(_map_width,
				(int) ((_isoConfig.offsetX + _screenWidth) / _isoConfig.tileWidth) + 1);
		final int startY = MathUtils.max(0, (int) (_isoConfig.offsetY / _isoConfig.tileHeight) - 1);
		final int endY = MathUtils.min(_map_height,
				(int) ((_isoConfig.offsetY + _screenHeight) / _isoConfig.tileHeight) + 1);
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				BattleTile tile = _mapTiles[i][j];
				if (tile.isVisible) {
					// 获取瓦片屏幕位置
					Vector2f tilePos = tile.getScreenPosition();
					float drawX = tilePos.x - _isoConfig.offsetX;
					float drawY = tilePos.y - _isoConfig.offsetY;
					// 更新瓦片动画
					if (tile.bgAnim != null) {
						tile.bgAnim.update(deltaTime);
					}
					if (tile.groundAnim != null) {
						tile.groundAnim.update(deltaTime);
					}
					if (tile.effectAnim != null) {
						tile.effectAnim.update(deltaTime);
					}
					// 更新瓦片亮度
					tile.updateBrightness();
					// 应用光照亮度
					_lightColor.setColor(tile.brightness, tile.brightness, tile.brightness, 1f);
					// 绘制背景层
					if (tile.bgAnim != null) {
						g.draw(tile.bgAnim.getSpriteImage(), drawX, drawY, _isoConfig.tileWidth,
								_isoConfig.tileHeight / 2, _lightColor);
					}
					// 绘制地表层
					if (tile.groundAnim != null) {
						g.draw(tile.groundAnim.getSpriteImage(), drawX, drawY, _isoConfig.tileWidth,
								_isoConfig.tileHeight / 2, _lightColor);
					}
					// 绘制特效层
					if (tile.effectAnim != null) {
						g.draw(tile.effectAnim.getSpriteImage(), drawX, drawY, _isoConfig.tileWidth,
								_isoConfig.tileHeight / 2, _lightColor);
					}

				}
			}
		}
	}

	protected void updateCamera(float deltaTime) {
		if (_cameraTarget != null && _cameraTarget.state != ObjectState.DEAD) {
			Vector2f targetScreenPos = _cameraTarget.getInterpolatedPosition();

			float targetOffsetX = targetScreenPos.x - _screenWidth / 2;
			float targetOffsetY = targetScreenPos.y - _screenHeight / 2;

			_isoConfig.offsetX += (targetOffsetX - _isoConfig.offsetX) * CAMERA_SMOOTH_FACTOR;
			_isoConfig.offsetY += (targetOffsetY - _isoConfig.offsetY) * CAMERA_SMOOTH_FACTOR;

			float maxOffsetX = (_map_width * _isoConfig.tileWidth) - _screenWidth;
			float maxOffsetY = (_map_height * _isoConfig.tileHeight / 2) - _screenHeight;

			_isoConfig.offsetX = Math.max(0, MathUtils.min(_isoConfig.offsetX, maxOffsetX));
			_isoConfig.offsetY = Math.max(0, MathUtils.min(_isoConfig.offsetY, maxOffsetY));
		}
	}

	public GameEventBus<GameEventType> getEventBus() {
		return _eventBus;
	}

	public BattlePathFinder getPathFinder() {
		return _pathFinder;
	}

	public void setPathFinder(BattlePathFinder p) {
		this._pathFinder = p;
	}

	public TArray<BattleMapObject> getObjects() {
		return _objects;
	}

	public BattleMapObject getCameraTarget() {
		return _cameraTarget;
	}

	public void setCameraTarget(BattleMapObject t) {
		this._cameraTarget = t;
	}

	public BattleSelectManager getSelectionManager() {
		return _selectionManager;
	}

	public boolean isDragging() {
		return _dragging;
	}

	public void setDragging(boolean dragging) {
		this._dragging = dragging;
	}

	public int getDragStartX() {
		return _dragStartX;
	}

	public void setDragStartX(int dragStartX) {
		this._dragStartX = dragStartX;
	}

	public int getDragStartY() {
		return _dragStartY;
	}

	public void setDragStartY(int dragStartY) {
		this._dragStartY = dragStartY;
	}

	public IsoConfig getIsoConfig() {
		return _isoConfig;
	}

	public void setIsoConfig(IsoConfig s) {
		this._isoConfig = s;
	}

}
