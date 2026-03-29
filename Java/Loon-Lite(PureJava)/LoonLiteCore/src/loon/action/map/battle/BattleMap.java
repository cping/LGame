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

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.collision.CollisionHelper;
import loon.action.map.Direction;
import loon.action.map.Field2D;
import loon.action.map.TileMapCollision;
import loon.action.map.Field2D.MapSwitchMaker;
import loon.action.map.battle.BattlePathFinder.PathResult;
import loon.action.map.battle.BattleTile.EffectService;
import loon.action.map.battle.BattleTile.SkillService;
import loon.action.map.battle.BattleTileMake.TileAnimation;
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.sprite.ISprite;
import loon.action.sprite.MoveControl;
import loon.action.sprite.SpriteCollisionListener;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.events.DrawListener;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.events.ResizeListener;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.opengl.GLEx;
import loon.utils.ISOUtils;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.ISOUtils.IsoResult;
import loon.utils.timer.Duration;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 专属战斗用地图，和普通地图最大区别是，此地图全部由高度可控动画对象构建(也就是复杂特效可以具体到单个瓦片)
 */
public class BattleMap extends LObject<ISprite> implements TileMapCollision, Sized, ISprite {

	private static class ObjectComparator implements Comparator<BattleMapObject> {

		@Override
		public int compare(BattleMapObject o1, BattleMapObject o2) {
			return MathUtils.compare(o1.renderPriority, o2.renderPriority);
		}

	}

	private boolean _playAnimation;

	private ActionBind _follow;
	private boolean _visible;

	private final static ObjectComparator OBJ_COMPARATOR = new ObjectComparator();

	public final static float CAMERA_SMOOTH_FACTOR = 0.1f;

	private float cameraMoveSpeed = 1f;

	private final PointF _scrollDrag = new PointF();

	private final Field2D _field2d;

	private Vector2f _followOffset = new Vector2f();

	private Vector2f _offset = new Vector2f();

	private GameEventBus<GameEventType> _eventBus;

	private BattlePathFinder _pathFinder;

	private BattleTile[][] _mapTiles;

	// 地图自身存储子精灵的的Sprites
	private Sprites _mapSprites;

	// 显示Map的上级Sprites
	private Sprites _screenSprites;

	private TArray<BattleMapObject> _objects = new TArray<BattleMapObject>();

	private BattleMapObject _cameraTarget = null;

	private BattleSelectManager _selectionManager;

	private float _fixedWidthOffset = 0f;

	private float _fixedHeightOffset = 0f;

	private boolean _dragging = false;

	private boolean _roll;

	private int _dragStartX, _dragStartY;

	private int _pixelInWidth, _pixelInHeight;

	private IsoConfig _isoConfig;

	private final LColor _lightColor = new LColor();

	private float _deltaTime = LSystem.MIN_SECONE_SPEED_FIXED;

	public DrawListener<BattleMap> _drawListener;

	private LTexture _background;

	private ResizeListener<BattleMap> _resizeListener;

	private SpriteCollisionListener _collSpriteListener;

	private LColor _baseColor = LColor.white;

	private BattleTileMake _tileMake;

	private Vector2f _tempPosition = new Vector2f();

	private IsoResult _tempIsoResult = new IsoResult();

	public BattleMap(BattleTileMake make, Field2D field2d, Screen screen, GameEventBus<GameEventType> events,
			IsoConfig config) {
		this(make, field2d, 0, 0, screen, events, config);
	}

	public BattleMap(BattleTileMake make, Field2D field2d, int x, int y, Screen screen,
			GameEventBus<GameEventType> events, IsoConfig config) {
		this(make, field2d, screen, x, y, events, config, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BattleMap(BattleTileMake make, Field2D field2d, int x, int y, Screen screen,
			GameEventBus<GameEventType> events, IsoConfig config, int screenWidth, int screenHeight) {
		this(make, field2d, screen, x, y, events, config, screenWidth, screenHeight);
	}

	public BattleMap(BattleTileMake make, Field2D field2d, Screen screen, int x, int y,
			GameEventBus<GameEventType> events, IsoConfig config, int screenWidth, int screenHeight) {
		_tileMake = make;
		if (config == null) {
			config = IsoConfig.defaultConfig();
		}
		this._field2d = field2d;
		if (field2d != null && screenWidth == -1 && screenHeight == -1) {
			this._pixelInWidth = field2d.getViewWidth();
			this._pixelInHeight = field2d.getViewHeight();
		} else {
			this._pixelInWidth = screenWidth;
			this._pixelInHeight = screenHeight;
		}
		if (field2d == null) {
			this._offset = new Vector2f(0, 0);
		} else {
			this._offset = field2d.getOffset();
			config.tileWidth = field2d.getTileWidth();
			config.tileHeight = field2d.getTileHeight();
		}
		this._eventBus = events;
		this._isoConfig = config;
		this._visible = _playAnimation = true;
		this._mapSprites = new Sprites("BattleMapSprites", screen == null ? LSystem.getProcess().getScreen() : screen,
				_pixelInWidth, _pixelInHeight);
		if (x == 0 && y == 0) {
			this.fixMapLocationToCenter();
		} else {
			this.setLocation(x, y);
		}
	}

	public void fixMapLocationToLeftTop() {
		fixMapLocationToOrigin("leftTop");
	}

	public void fixMapLocationToLeftBottom() {
		fixMapLocationToOrigin("leftBottom");
	}

	public void fixMapLocationToRightTop() {
		fixMapLocationToOrigin("rightTop");
	}

	public void fixMapLocationToRightBottom() {
		fixMapLocationToOrigin("rightBottom");
	}

	public void fixMapLocationToCenter() {
		fixMapLocationToOrigin("center");
	}

	public void fixMapLocationToOrigin(String style) {
		ObjectMap<String, Vector2f> offsets = ISOUtils.alignIsoMapOffsets(_field2d.getWidth(), _field2d.getHeight(),
				_pixelInWidth, _pixelInHeight, _isoConfig);
		if (offsets != null) {
			Vector2f centerOffset = offsets.get(style);
			if (centerOffset != null) {
				setLocation(centerOffset);
			}
		}
	}

	@Override
	public void update(long elapsedTime) {
		_deltaTime = MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED);
		if (_mapSprites != null) {
			_mapSprites.update(elapsedTime);
		}
		if (_drawListener != null) {
			_drawListener.update(elapsedTime);
		}
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}

	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!_visible) {
			return;
		}
		if (this._roll) {
			this._offset = toRollPosition(this._offset);
		}
		final float tileW = _isoConfig.tileWidth;
		final float tileH = _isoConfig.tileHeight;
		final int mapTileW = _field2d.getWidth();
		final int mapTileH = _field2d.getHeight();
		final float tileWidth = _isoConfig.tileWidth * _isoConfig.scaleX;
		final float tileHeight = _isoConfig.tileHeight * _isoConfig.scaleY;
		final float screenW = _pixelInWidth;
		final float screenH = _pixelInHeight;
		final float drawMapX = this._objectLocation.x + offsetX + _offset.getX();
		final float drawMapY = this._objectLocation.y + offsetY + _offset.getY();
		followActionObject();
		final int posOffsetX = MathUtils.ifloor(drawMapX);
		final int posOffsetY = MathUtils.ifloor(drawMapY);
		if (_background != null) {
			g.draw(_background, offsetX, offsetY, _baseColor);
		}
		final float worldLTX = -drawMapX;
		final float worldLTY = -drawMapY;
		final float worldRBX = worldLTX + screenW;
		final float worldRBY = worldLTY + screenH;
		int startX = MathUtils.ifloor(worldLTX / tileW);
		int startY = MathUtils.ifloor(worldLTY / tileH);
		int endX = MathUtils.iceil(worldRBX / tileW);
		int endY = MathUtils.iceil(worldRBY / tileH);
		final int dynamicMarginX = mapTileW / 2 + 4;
		final int dynamicMarginY = mapTileH / 2 + 4;
		startX -= dynamicMarginX;
		startY -= dynamicMarginY;
		endX += dynamicMarginX;
		endY += dynamicMarginY;
		endX = MathUtils.min(endX, mapTileW);
		endY = MathUtils.min(endY, mapTileH);
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				if (i < 0 || j < 0 || i >= mapTileW || j >= mapTileH) {
					continue;
				}
				BattleTile tile = _mapTiles[i][j];
				if (tile == null || !tile.isVisible) {
					continue;
				}
				Vector2f tilePos = tile.getScreenPosition(_tempPosition, _tempIsoResult);
				final float drawX = tilePos.x - _isoConfig.offsetX + posOffsetX;
				final float drawY = tilePos.y - _isoConfig.offsetY + posOffsetY;
				if (!CollisionHelper.checkAABBvsAABB(0, 0, screenW, screenH, drawX, drawY, tileWidth, tileHeight)) {
					continue;
				}
				if (_playAnimation) {
					tile.update(_deltaTime);
					tile.updateBrightness();
					_lightColor.setColor(tile.brightness, tile.brightness, tile.brightness, 1f);
				}
				tile.paint(g, drawX, drawY, tileWidth, tileHeight, _lightColor);
			}
		}
		if (_mapSprites != null) {
			_mapSprites.paintPos(g, posOffsetX, posOffsetY);
		}
	}

	public void createMap(GameEventBus<PathResult> pathResult, GameEventBus<BattleMapObject> bus) {
		createMap(pathResult, bus, null, null);
	}

	public void createMap(GameEventBus<PathResult> pathResult, GameEventBus<BattleMapObject> bus,
			EffectService effectService, SkillService skillService) {
		BattleTile[][] maps = generateMap(effectService, skillService);
		createMap(pathResult, bus, maps, _field2d.getWidth(), _field2d.getHeight());
	}

	public void createMap(GameEventBus<PathResult> pathResult, GameEventBus<BattleMapObject> bus, BattleTile[][] maps,
			int mapWidth, int mapHeight) {
		_selectionManager = new BattleSelectManager(bus);
		_pathFinder = new BattlePathFinder(pathResult, maps, mapWidth, mapHeight);
		_mapTiles = maps;
	}

	public BattleTileMake getTileMake() {
		return _tileMake;
	}

	public BattleTile[][] generateMap(EffectService effectService, SkillService skillService) {
		int width = _field2d.getWidth();
		int height = _field2d.getHeight();
		int tileWidth = _field2d.getTileWidth();
		int tileHeight = _field2d.getTileHeight();
		BattleTile[][] newMap = new BattleTile[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int id = _field2d.getTileType(x, y);
				BattleTileType tileType = BattleTileType.getById(id);
				BattleTile tile = new BattleTile(x, y, tileWidth, tileHeight, _isoConfig, tileType, effectService,
						skillService);
				if (_tileMake != null) {
					TileAnimation ani = _tileMake.getTileAnimation(id);
					if (ani != null) {
						if (ani.backgroundAnim != null) {
							tile.bgAnim = ani.backgroundAnim.cpy();
						}
						if (ani.groundAnim != null) {
							tile.groundAnim = ani.groundAnim.cpy();
						}
						if (ani.effectAnim != null) {
							tile.effectAnim = ani.effectAnim.cpy();
						}
					}
				}
				newMap[x][y] = tile;
			}
		}
		return newMap;
	}

	protected void sortObjects() {
		_objects.sort(OBJ_COMPARATOR);
	}

	public Vector2f findTileXY(float touchX, float touchY) {
		int tx = MathUtils.floor(touchX - _offset.x - _objectLocation.x - _isoConfig.offsetX);
		int ty = MathUtils.floor(touchY - _offset.y - _objectLocation.y - _isoConfig.offsetY);
		Vector3f gridPos = ISOUtils.screenToGrid(tx, ty, _isoConfig);
		int gx = gridPos.x();
		int gy = gridPos.y();
		return _tempPosition.set(gx, gy);
	}

	public void handleCameraMovement(float deltaTime, Direction d) {
		if (d == Direction.LEFT) {
			_offset.x -= cameraMoveSpeed * deltaTime;
		}
		if (d == Direction.RIGHT) {
			_offset.x += cameraMoveSpeed * deltaTime;
		}
		if (d == Direction.UP) {
			_offset.y -= cameraMoveSpeed * deltaTime;
		}
		if (d == Direction.DOWN) {
			_offset.y += cameraMoveSpeed * deltaTime;
		}
		float maxOffsetX = (_field2d.getWidth() * _isoConfig.tileWidth) - _pixelInWidth;
		float maxOffsetY = (_field2d.getHeight() * _isoConfig.tileHeight / 2) - _pixelInHeight;
		_offset.x = MathUtils.max(0, MathUtils.min(_offset.x, maxOffsetX));
		_offset.y = MathUtils.max(0, MathUtils.min(_offset.y, maxOffsetY));
	}

	public void clickTile(float touchX, float touchY) {
		BattleTile clickedTile = findTileTouch(touchX, touchY);
		if (clickedTile != null && !_selectionManager._selectedObjects.isEmpty()) {
			clickedTile.isHighlighted = !clickedTile.isHighlighted;
			BattleMapObject selected = _selectionManager._selectedObjects.get(0);
			TArray<Vector2f> path = _pathFinder.findPath(selected.gridX, selected.gridY, clickedTile.gridX,
					clickedTile.gridY);
			selected.setPath(path);
		}
	}

	public BattleMapObject findObjectTouch(float touchX, float touchY) {
		for (BattleMapObject obj : _objects) {
			if (obj.state != ObjectState.DEAD) {
				Vector2f objPos = obj.getInterpolatedPosition();
				float drawX = objPos.x - _isoConfig.offsetX;
				float drawY = objPos.y - _isoConfig.offsetY;
				RectBox bounds = new RectBox(drawX - _isoConfig.tileWidth / 2, drawY - _isoConfig.tileHeight / 2,
						_isoConfig.tileWidth, _isoConfig.tileHeight);
				if (bounds.contains(touchX, touchY)) {
					return obj;
				}
			}
		}
		return null;
	}

	public BattleTile findTileTouch(float touchX, float touchY) {
		return findTileTouch(touchX, touchY, 0f, 0f);
	}

	/**
	 * 查找触屏位置的瓦片
	 * 
	 * @param touchX
	 * @param touchY
	 * @param tileWidth
	 * @param tileHeight
	 * @return
	 */
	public BattleTile findTileTouch(float touchX, float touchY, float tileWidth, float tileHeight) {
		Vector2f pos = findTileXY(touchX, touchY);
		int tx = pos.x();
		int ty = pos.y();
		if (tx >= 0 && tx < _field2d.getWidth() && ty >= 0 && ty < _field2d.getHeight()) {
			return _mapTiles[tx][ty];
		}
		return null;
	}

	protected void updateCamera(float deltaTime) {
		if (_cameraTarget != null && _cameraTarget.state != ObjectState.DEAD) {
			Vector2f targetScreenPos = _cameraTarget.getInterpolatedPosition();
			float targetOffsetX = targetScreenPos.x - _pixelInWidth / 2;
			float targetOffsetY = targetScreenPos.y - _pixelInHeight / 2;
			_offset.x += (targetOffsetX - _offset.x) * CAMERA_SMOOTH_FACTOR;
			_offset.y += (targetOffsetY - _offset.y) * CAMERA_SMOOTH_FACTOR;
			float maxOffsetX = (_field2d.getWidth() * _isoConfig.tileWidth * _isoConfig.scaleX) - _pixelInWidth;
			float maxOffsetY = (_field2d.getHeight() * _isoConfig.tileHeight * _isoConfig.scaleY) - _pixelInHeight;
			_offset.x = MathUtils.max(0, MathUtils.min(_offset.x, maxOffsetX));
			_offset.y = MathUtils.max(0, MathUtils.min(_offset.y, maxOffsetY));
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

	public float getWidthScale() {
		return _isoConfig.scaleX;
	}

	public void setWidthScale(float widthScale) {
		_isoConfig.scaleX = widthScale;
	}

	public float getHeightScale() {
		return _isoConfig.scaleY;
	}

	public void setHeightScale(float heightScale) {
		_isoConfig.scaleY = heightScale;
	}

	public BattleMap resizeScreen(int width, int height) {
		_pixelInWidth = width;
		_pixelInHeight = height;
		return this;
	}

	public float centerX() {
		return ((getContainerX() + getContainerWidth()) - (getX() + getWidth())) / 2f;
	}

	public float centerY() {
		return ((getContainerY() + getContainerHeight()) - (getY() + getHeight())) / 2f;
	}

	public boolean isContentPositionInBounds(float x, float y) {
		float offX = MathUtils.min(this._offset.x + _isoConfig.offsetX);
		float offY = MathUtils.min(this._offset.y + _isoConfig.offsetY);
		if (x < offX) {
			return false;
		}
		if (x >= offX + (getContainerWidth() - getWidth())) {
			return false;
		}
		if (y < offY) {
			return false;
		}
		if (y >= offY + (getContainerHeight() - getHeight())) {
			return false;
		}
		return true;
	}

	public BattleMap scrollDown(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = (this._offset.y + _isoConfig.offsetY + distance);
		return this;
	}

	public BattleMap scrollUp(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = (this._offset.y + _isoConfig.offsetY - distance);
		return this;
	}

	public BattleMap scrollLeft(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = this._offset.x + _isoConfig.offsetX - distance;
		return this;
	}

	public BattleMap scrollRight(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = this._offset.x + _isoConfig.offsetX + distance;
		return this;
	}

	public BattleMap scrollLeftUp(float distance) {
		this.scrollUp(distance);
		this.scrollLeft(distance);
		return this;
	}

	public BattleMap scrollRightDown(float distance) {
		this.scrollDown(distance);
		this.scrollRight(distance);
		return this;
	}

	public BattleMap scrollClear() {
		if (!this._offset.equals(0f, 0f)) {
			this._offset.set(0, 0);
		}
		return this;
	}

	public BattleMap scroll(float x, float y) {
		return scroll(x, y, 4f);
	}

	public BattleMap scroll(float x, float y, float distance) {
		if (_scrollDrag.x == 0f && _scrollDrag.y == 0f) {
			_scrollDrag.set(x, y);
			return this;
		}
		return scroll(_scrollDrag.x, _scrollDrag.y, x, y, distance);
	}

	public BattleMap scroll(float x1, float y1, float x2, float y2) {
		return scroll(x1, y1, x2, y2, 4f);
	}

	public BattleMap scroll(float x1, float y1, float x2, float y2, float distance) {
		if (this._follow != null) {
			return this;
		}
		if (x1 < x2 && x1 > centerX()) {
			scrollRight(distance);
		} else if (x1 > x2) {
			scrollLeft(distance);
		}
		if (y1 < y2 && y1 > centerY()) {
			scrollDown(distance);
		} else if (y1 > y2) {
			scrollUp(distance);
		}
		_scrollDrag.set(x2, y2);
		return this;
	}

	public int[] getLimit() {
		return _field2d.getLimit();
	}

	public BattleMap setLimit(int[] limit) {
		_field2d.setLimit(limit);
		return this;
	}

	public BattleMap setAllowMove(int[] args) {
		_field2d.setAllowMove(args);
		return this;
	}

	@Override
	public boolean isHit(int px, int py) {
		return _field2d.isHit(px, py);
	}

	public boolean isHit(Vector2f v) {
		return isHit(v.x(), v.y());
	}

	@Override
	public boolean isPixelHit(int px, int py) {
		return isPixelHit(px, py, 0, 0);
	}

	public boolean isPixelHit(int px, int py, int movePx, int movePy) {
		return isHit(_field2d.pixelsToTilesWidth(_field2d.offsetXPixel(px)) + movePx,
				_field2d.pixelsToTilesHeight(_field2d.offsetYPixel(py)) + movePy);
	}

	@Override
	public boolean isPixelTUp(int px, int py) {
		return isPixelHit(px, py, 0, -1);
	}

	@Override
	public boolean isPixelTRight(int px, int py) {
		return isPixelHit(px, py, 1, 0);
	}

	@Override
	public boolean isPixelTLeft(int px, int py) {
		return isPixelHit(px, py, -1, 0);
	}

	@Override
	public boolean isPixelTDown(int px, int py) {
		return isPixelHit(px, py, 0, 1);
	}

	@Override
	public Vector2f getTileCollision(LObject<?> o, float newX, float newY) {
		return _field2d.getTileCollision(o.getX(), o.getY(), o.getWidth(), o.getHeight(), newX, newY);
	}

	public int getTileIDFromPixels(Vector2f v) {
		return getTileIDFromPixels(v.x, v.y);
	}

	public int getTileIDFromPixels(float sx, float sy) {
		float x = (sx + _offset.getX() + _isoConfig.offsetX);
		float y = (sy + _offset.getY() + _isoConfig.offsetY);
		Vector2f tileCoordinates = pixelsToTiles(x, y);
		return getTileID(MathUtils.round(tileCoordinates.getX()), MathUtils.round(tileCoordinates.getY()));
	}

	@Override
	public int[][] getMap() {
		return _field2d.getMap();
	}

	public boolean isValid(int x, int y) {
		return this._field2d.inside(x, y);
	}

	public BattleMap replaceType(int oldid, int newid) {
		_field2d.replaceType(oldid, newid);
		return this;
	}

	public int getTileID(int x, int y) {
		if (x >= 0 && x < _field2d.getWidth() && y >= 0 && y < _field2d.getHeight()) {
			return _field2d.getTileType(x, y);
		} else {
			return -1;
		}
	}

	public BattleMap setTileID(int x, int y, int id) {
		if (x >= 0 && x < _field2d.getWidth() && y >= 0 && y < _field2d.getHeight()) {
			_field2d.setTileType(x, y, id);
		}
		return this;
	}

	public Vector2f pixelsToTiles(float x, float y) {
		float xprime = x / _isoConfig.scaleX / _field2d.getTileWidth() - 1;
		float yprime = y / _isoConfig.scaleY / _field2d.getTileHeight() - 1;
		return new Vector2f(xprime, yprime);
	}

	@Override
	public int tilesToPixelsX(float x) {
		return MathUtils.floor(_field2d.tilesToWidthPixels(x) * _isoConfig.scaleX);
	}

	@Override
	public int tilesToPixelsY(float y) {
		return MathUtils.floor(_field2d.tilesToHeightPixels(y) * _isoConfig.scaleY);
	}

	@Override
	public int pixelsToTilesWidth(float x) {
		return _field2d.pixelsToTilesWidth(x / _isoConfig.scaleX);
	}

	@Override
	public int pixelsToTilesHeight(float y) {
		return _field2d.pixelsToTilesHeight(y / _isoConfig.scaleY);
	}

	public PointI pixelsToTileMap(float x, float y) {
		int tileX = pixelsToTilesWidth(x);
		int tileY = pixelsToTilesHeight(y);
		return new PointI(tileX, tileY);
	}

	public PointI tilePixels(float x, float y) {
		int newX = getPixelX(x);
		int newY = getPixelY(y);
		return new PointI(newX, newY);
	}

	/**
	 * 转化地图到屏幕像素(不考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI tileMapToPixels(float x, float y) {
		int tileX = tilesToPixelsX(x);
		int tileY = tilesToPixelsY(y);
		return new PointI(tileX, tileY);
	}

	/**
	 * 转化地图到屏幕像素(考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI tileMapToScrollTilePixels(float x, float y) {
		int newX = toTileScrollPixelX(x);
		int newY = toTileScrollPixelX(y);
		return new PointI(newX, newY);
	}

	/**
	 * 转化屏幕像素到地图(考虑地图滚动)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public PointI pixelsToScrollTileMap(float x, float y) {
		int tileX = toPixelScrollTileX(x);
		int tileY = toPixelScrollTileY(y);
		return new PointI(tileX, tileY);
	}

	/**
	 * 转换坐标为像素坐标
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2f tilesToPixels(float x, float y) {
		float xprime = x * _field2d.getTileWidth() - _offset.getX() + _isoConfig.offsetX;
		float yprime = y * _field2d.getTileHeight() - _offset.getY() + _isoConfig.offsetY;
		return new Vector2f(xprime, yprime);
	}

	public BattleMap switchMap(MapSwitchMaker ms) {
		_field2d.switchMap(ms);
		return this;
	}

	/**
	 * 地图居中偏移
	 *
	 * @return
	 */
	public BattleMap centerOffset() {
		this._offset.set(centerX(), centerY());
		return this;
	}

	/**
	 * 设定偏移量
	 *
	 * @param x
	 * @param y
	 */
	public BattleMap setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	/**
	 * 设定偏移量
	 *
	 * @param offset
	 */
	@Override
	public BattleMap setOffset(Vector2f offset) {
		this._offset.set(offset);
		return this;
	}

	/**
	 * 获得瓦片位置
	 *
	 * @return
	 */
	@Override
	public Vector2f getOffset() {
		return _offset;
	}

	/**
	 * 设定跟随角色偏移量
	 *
	 * @param x
	 * @param y
	 */
	public BattleMap setFollowOffset(float x, float y) {
		this._followOffset.set(x, y);
		return this;
	}

	/**
	 * 设定跟随角色偏移量
	 *
	 * @param offset
	 */
	public BattleMap setFollowOffset(Vector2f offset) {
		this._followOffset.set(offset);
		return this;
	}

	public Vector2f getFollowOffset() {
		return this._followOffset;
	}

	@Override
	public int getTileWidth() {
		return _field2d.getTileWidth();
	}

	@Override
	public int getTileHeight() {
		return _field2d.getTileHeight();
	}

	@Override
	public float getHeight() {
		return (_field2d.getViewHeight() * _isoConfig.scaleY) - _fixedHeightOffset;
	}

	@Override
	public float getWidth() {
		return (_field2d.getViewHeight() * _isoConfig.scaleX) - _fixedWidthOffset;
	}

	@Override
	public int getRow() {
		return _field2d.getWidth();
	}

	@Override
	public int getCol() {
		return _field2d.getHeight();
	}

	public BattleMap setMapValues(int v) {
		_field2d.setValues(v);
		return this;
	}

	public Field2D getNewField2D() {
		return new Field2D(_field2d);
	}

	public DrawListener<BattleMap> getListener() {
		return _drawListener;
	}

	public BattleMap setListener(DrawListener<BattleMap> l) {
		this._drawListener = l;
		return this;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x() + _offset.x + _isoConfig.offsetX, y() + _offset.y + _isoConfig.offsetY,
				_field2d.getTileWidth() * _field2d.getWidth(), _field2d.getTileHeight() * _field2d.getHeight());
	}

	@Override
	public LTexture getBitmap() {
		return _background;
	}

	public BattleMap startAnimation() {
		_playAnimation = true;
		return this;
	}

	public BattleMap stopAnimation() {
		_playAnimation = false;
		return this;
	}

	protected float limitOffsetX(float newOffsetX) {
		float offsetX = getContainerWidth() / 2 - newOffsetX;
		offsetX = MathUtils.min(offsetX, 0);
		offsetX = MathUtils.max(offsetX, getContainerWidth() - getWidth());
		return offsetX + _followOffset.x;
	}

	protected float limitOffsetY(float newOffsetY) {
		float offsetY = getContainerHeight() / 2 - newOffsetY;
		offsetY = MathUtils.min(offsetY, 0);
		offsetY = MathUtils.max(offsetY, getContainerHeight() - getHeight());
		return offsetY + _followOffset.y;
	}

	public BattleMap followActionObject() {
		if (_follow != null) {
			float offsetX = limitOffsetX(_follow.getX());
			float offsetY = limitOffsetY(_follow.getY());
			if (offsetX != 0 || offsetY != 0) {
				setOffset(offsetX, offsetY);
				_field2d.setOffset(_offset);
			}
		}
		return this;
	}

	@Override
	public LColor getColor() {
		return new LColor(_baseColor);
	}

	@Override
	public void setColor(LColor c) {
		if (c != null && !c.equals(_baseColor)) {
			this._baseColor = c;
		}
	}

	public int getPixelsAtFieldType(Vector2f pos) {
		return _field2d.getPixelsAtFieldType(pos.x, pos.y);
	}

	public int getPixelsAtFieldType(float x, float y) {
		int itsX = pixelsToTilesWidth(x);
		int itsY = pixelsToTilesHeight(y);
		return _field2d.getPixelsAtFieldType(itsX, itsY);
	}

	@Override
	public Field2D getField2D() {
		return _field2d;
	}

	@Override
	public float getScaleX() {
		return _isoConfig.scaleX;
	}

	@Override
	public float getScaleY() {
		return _isoConfig.scaleY;
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}

	@Override
	public void setScale(float sx, float sy) {
		_isoConfig.setScale(sx, sy);
	}

	@Override
	public BattleMap setSize(float w, float h) {
		setScale(w / getWidth(), h / getHeight());
		return this;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return _field2d.getRect().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	public ActionBind getFollow() {
		return _follow;
	}

	public BattleMap setFollow(ActionBind follow) {
		this._follow = follow;
		return this;
	}

	public BattleMap followDonot() {
		return setFollow(null);
	}

	public BattleMap followAction(ActionBind follow) {
		return setFollow(follow);
	}

	public Vector2f toTilesScrollPixels(float x, float y) {
		return new Vector2f(toTileScrollPixelX(x), toTileScrollPixelY(y));
	}

	public int toTileScrollPixelX(float x) {
		return offsetXPixel(tilesToPixelsX(x));
	}

	public int toTileScrollPixelY(float y) {
		return offsetYPixel(tilesToPixelsY(y));
	}

	public Vector2f toPixelsScrollTiles(float x, float y) {
		return new Vector2f(toPixelScrollTileX(x), toPixelScrollTileY(y));
	}

	public int toPixelScrollTileX(float x) {
		return pixelsToTilesWidth(offsetXPixel(x));
	}

	public int toPixelScrollTileY(float y) {
		return pixelsToTilesHeight(offsetYPixel(y));
	}

	public Vector2f offsetPixels(float x, float y) {
		return new Vector2f(offsetXPixel(x), offsetYPixel(y));
	}

	public int getPixelX(float x) {
		return MathUtils.iceil((x - _objectLocation.x) / _isoConfig.scaleX);
	}

	public int getPixelY(float y) {
		return MathUtils.iceil((y - _objectLocation.y) / _isoConfig.scaleY);
	}

	public int offsetXPixel(float x) {
		return MathUtils.iceil((x - _offset.x - _objectLocation.x - _isoConfig.offsetX) / _isoConfig.scaleX);
	}

	public int offsetYPixel(float y) {
		return MathUtils.iceil((y - _offset.y - _objectLocation.y - _isoConfig.offsetY) / _isoConfig.scaleY);
	}

	public boolean inMap(int x, int y) {
		return ((((x >= 0) && (x < _pixelInWidth)) && (y >= 0)) && (y < _pixelInHeight));
	}

	public MoveControl followControl(ActionBind bind) {
		followAction(bind);
		return new MoveControl(bind, this._field2d);
	}

	public Vector2f toRollPosition(Vector2f pos) {
		pos.x = pos.x % _field2d.getViewWidth();
		pos.y = pos.y % _field2d.getViewHeight();
		if (pos.x < 0f) {
			pos.x += _field2d.getViewWidth();
		}
		if (pos.y < 0f) {
			pos.y += _field2d.getViewHeight();
		}
		return pos;
	}

	public boolean isRoll() {
		return _roll;
	}

	public BattleMap setRoll(boolean roll) {
		this._roll = roll;
		return this;
	}

	public LTexture getBackground() {
		return this._background;
	}

	public BattleMap setBackground(LTexture bg) {
		this._background = bg;
		return this;
	}

	public BattleMap setBackground(String path) {
		if (StringUtils.isEmpty(path)) {
			return this;
		}
		return this.setBackground(LTextures.loadTexture(path));
	}

	public BattleMap setBackground(String path, float w, float h) {
		if (StringUtils.isEmpty(path)) {
			return this;
		}
		return this.setBackground(LTextures.loadTexture(path).scale(w, h));
	}

	public boolean move(ActionBind o, float newX, float newY) {
		return move(o, newX, newY, true);
	}

	public boolean move(ActionBind o, float newX, float newY, boolean toMoved) {
		if (o == null) {
			return false;
		}
		float x = offsetXPixel(o.getX()) + newX;
		float y = offsetYPixel(o.getY()) + newY;
		if (!_field2d.checkTileCollision(o, x, y)) {
			if (toMoved) {
				o.setLocation(x, y);
			}
			return true;
		}
		return false;
	}

	public boolean moveX(ActionBind o, float newX) {
		return moveX(o, newX, true);
	}

	public boolean moveX(ActionBind o, float newX, boolean toMoved) {
		if (o == null) {
			return false;
		}
		float x = offsetXPixel(o.getX()) + newX;
		float y = offsetYPixel(o.getY());
		if (!_field2d.checkTileCollision(o, x, y)) {
			if (toMoved) {
				o.setLocation(x, y);
			}
			return true;
		}
		return false;
	}

	public boolean moveY(ActionBind o, float newY) {
		return moveY(o, newY, true);
	}

	public boolean moveY(ActionBind o, float newY, boolean toMoved) {
		if (o == null) {
			return false;
		}
		float x = offsetXPixel(o.getX());
		float y = offsetYPixel(o.getY()) + newY;
		if (!_field2d.checkTileCollision(o, x, y)) {
			if (toMoved) {
				o.setLocation(x, y);
			}
			return true;
		}
		return false;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	public Sprites getMapSprites() {
		return _mapSprites;
	}

	public BattleMap setMapSprites(Sprites s) {
		_mapSprites = s;
		return this;
	}

	@Override
	public ISprite setSprites(Sprites ss) {
		if (this._screenSprites == ss) {
			return this;
		}
		this._screenSprites = ss;
		return this;
	}

	@Override
	public Sprites getSprites() {
		return this._screenSprites;
	}

	@Override
	public Screen getScreen() {
		if (this._screenSprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this._screenSprites.getScreen() == null ? LSystem.getProcess().getScreen()
				: this._screenSprites.getScreen();
	}

	public float getScreenX() {
		float x = 0;
		ISprite parent = _objectSuper;
		if (parent != null) {
			x += parent.getX();
			for (; (parent = parent.getParent()) != null;) {
				x += parent.getX();
			}
		}
		return x + getX();
	}

	public float getScreenY() {
		float y = 0;
		ISprite parent = _objectSuper;
		if (parent != null) {
			y += parent.getY();
			for (; (parent = parent.getParent()) != null;) {
				y += parent.getY();
			}
		}
		return y + getY();
	}

	@Override
	public float getContainerX() {
		if (_objectSuper != null) {
			return getScreenX() - getX();
		}
		return this._screenSprites == null ? super.getContainerX() : this._screenSprites.getX();
	}

	@Override
	public float getContainerY() {
		if (_objectSuper != null) {
			return getScreenY() - getY();
		}
		return this._screenSprites == null ? super.getContainerY() : this._screenSprites.getY();
	}

	@Override
	public float getContainerWidth() {
		return this._screenSprites == null ? super.getContainerWidth() : this._screenSprites.getWidth();
	}

	@Override
	public float getContainerHeight() {
		return this._screenSprites == null ? super.getContainerHeight() : this._screenSprites.getHeight();
	}

	@Override
	public float getFixedWidthOffset() {
		return _fixedWidthOffset;
	}

	@Override
	public ISprite setFixedWidthOffset(float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
		return this;
	}

	@Override
	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	@Override
	public ISprite setFixedHeightOffset(float fixedHeightOffset) {
		this._fixedHeightOffset = fixedHeightOffset;
		return this;
	}

	@Override
	public boolean showShadow() {
		return false;
	}

	@Override
	public boolean collides(ISprite e) {
		if (e == null || !e.isVisible()) {
			return false;
		}
		return getRectBox().intersects(e.getCollisionBox());
	}

	public boolean collidesX(ISprite other, int epsilon) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		float selfLeft = rectSelf.getX();
		float selfRight = selfLeft + MathUtils.max(1, rectSelf.getWidth());
		RectBox rectOther = other.getRectBox();
		float otherLeft = rectOther.getX();
		float otherRight = otherLeft + MathUtils.max(1, rectOther.getWidth());
		return selfRight + epsilon >= otherLeft && otherRight + epsilon >= selfLeft;
	}

	public boolean collidesY(ISprite other, int epsilon) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		float selfTop = rectSelf.getY();
		float selfBottom = selfTop + MathUtils.max(1, rectSelf.getHeight());
		RectBox rectOther = other.getRectBox();
		float otherTop = rectOther.getY();
		float otherBottom = otherTop + MathUtils.max(1, rectOther.getHeight());
		return selfBottom + epsilon >= otherTop && otherBottom + epsilon >= selfTop;
	}

	@Override
	public boolean collidesX(ISprite other) {
		return collidesX(other, 1);
	}

	@Override
	public boolean collidesY(ISprite other) {
		return collidesY(other, 1);
	}

	@Override
	public BattleMap triggerCollision(SpriteCollisionListener sc) {
		this._collSpriteListener = sc;
		return this;
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		if (_collSpriteListener != null) {
			_collSpriteListener.onCollideUpdate(coll, dir);
		}
	}

	@Override
	public void onResize() {
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
		if (_mapSprites != null) {
			_mapSprites.resize(getWidth(), getHeight(), false);
		}
	}

	public ResizeListener<BattleMap> getResizeListener() {
		return _resizeListener;
	}

	public BattleMap setResizeListener(ResizeListener<BattleMap> listener) {
		this._resizeListener = listener;
		return this;
	}

	public BattleMap setOffsetX(float sx) {
		this._offset.setX(sx);
		return this;
	}

	public BattleMap setOffsetY(float sy) {
		this._offset.setY(sy);
		return this;
	}

	@Override
	public float getOffsetX() {
		return _offset.x;
	}

	@Override
	public float getOffsetY() {
		return _offset.y;
	}

	@Override
	public float left() {
		return getX();
	}

	@Override
	public float top() {
		return getY();
	}

	@Override
	public float right() {
		return getWidth();
	}

	@Override
	public float bottom() {
		return getHeight();
	}

	@Override
	public boolean autoXYSort() {
		return false;
	}

	@Override
	public ISprite buildToScreen() {
		if (_mapSprites != null) {
			_mapSprites.add(this);
			return this;
		}
		getScreen().add(this);
		return this;
	}

	@Override
	public ISprite removeFromScreen() {
		if (_mapSprites != null) {
			_mapSprites.remove(this);
			return this;
		}
		getScreen().remove(this);
		return this;
	}

	@Override
	public ISprite resetAnchor() {
		return this;
	}

	@Override
	public ISprite setAnchor(float sx, float sy) {
		return this;
	}

	@Override
	public String toString() {
		return _field2d.toString();
	}

	@Override
	protected void _onDestroy() {
		_visible = false;
		_playAnimation = false;
		_roll = false;
		if (_mapSprites != null) {
			_mapSprites.close();
			_mapSprites = null;
		}
		if (_background != null) {
			_background.close();
			_background = null;
		}
		_resizeListener = null;
		_collSpriteListener = null;
		removeActionEvents(this);
	}
}
