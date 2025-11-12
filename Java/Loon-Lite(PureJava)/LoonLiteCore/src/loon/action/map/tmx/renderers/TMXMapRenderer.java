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
package loon.action.map.tmx.renderers;

import loon.LObject;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextureBatch;
import loon.LTextureBatch.Cache;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.action.map.tmx.TMXImage;
import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXMapLayer;
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.TMXTileSet;
import loon.action.map.tmx.tiles.TMXAnimation;
import loon.action.map.tmx.tiles.TMXTile;
import loon.action.sprite.ISprite;
import loon.action.sprite.SpriteCollisionListener;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.events.DrawListener;
import loon.events.DrawLoop;
import loon.events.ResizeListener;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

/**
 * TMX地图渲染用基本抽象类,所有TMX地图文件的渲染皆由此类的子类负责具体实现
 */
public abstract class TMXMapRenderer extends LObject<ISprite> implements Sized, ISprite {

	private final PointF _scrollDrag = new PointF();

	private DrawListener<TMXMapRenderer> _drawListener;

	private Vector2f _followOffset = new Vector2f();

	private ActionBind _follow;

	protected LTexture _texCurrent;

	protected LTextureBatch _texBatch;

	private ResizeListener<TMXMapRenderer> _resizeListener;

	private SpriteCollisionListener _collSpriteListener;

	private float _viewWidth;

	private float _viewHeight;

	private Vector2f _tempScreenPos = new Vector2f();

	protected Vector2f _mapLocation = new Vector2f();

	protected Vector2f _offset = new Vector2f();

	protected float _fixedWidthOffset = 0f;

	protected float _fixedHeightOffset = 0f;

	protected Sprites sprites = null;

	protected int lastHashCode = 1;

	protected int tileIndex = 0;

	protected abstract void renderTileLayer(GLEx gl, TMXTileLayer tileLayer);

	protected abstract void renderImageLayer(GLEx gl, TMXImageLayer imageLayer);

	protected TMXMap map;

	protected IntMap<Cache> textureCaches;
	protected ObjectMap<String, LTexture> textureMap;
	protected ObjectMap<TMXTile, TMXAnimation> tileAnimators;

	protected boolean visible;
	protected float scaleX = 1f;
	protected float scaleY = 1f;

	protected LColor baseColor = new LColor(LColor.white);

	protected boolean allowCache;

	protected Vector2f tempLocation = new Vector2f();

	public TMXMapRenderer(TMXMap map) {
		this(map, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TMXMapRenderer(TMXMap map, float viewW, float viewH) {
		this.setViewSize(viewW, viewH);
		this.textureCaches = new IntMap<LTextureBatch.Cache>();
		this.textureMap = new ObjectMap<String, LTexture>();
		this.tileAnimators = new ObjectMap<TMXTile, TMXAnimation>();
		this.visible = allowCache = true;
		this.map = map;

		for (TMXTileSet tileSet : map.getTileSets()) {
			TMXImage image = tileSet.getImage();
			if (image != null) {
				String path = image.getSource();
				if (!textureMap.containsKey(path)) {
					textureMap.put(path, image.getImage());
				}
			}
			for (TMXTile tile : tileSet.getTiles()) {
				if (tile.isAnimated()) {
					TMXAnimation animator = new TMXAnimation(tile);
					tileAnimators.put(tile, animator);
				}
			}
		}

		for (TMXImageLayer imageLayer : map.getImageLayers()) {
			TMXImage image = imageLayer.getImage();
			if (image != null) {
				String path = image.getSource();
				if (!textureMap.containsKey(path)) {
					textureMap.put(path, LSystem.loadTexture(path));
				}
			}
		}
	}

	public static TMXMapRenderer create(TMXMap map) {
		switch (map.getOrientation()) {
		case ISOMETRIC:
			return new TMXIsometricMapRenderer(map);
		case ORTHOGONAL:
			return new TMXOrthogonalMapRenderer(map);
		case HEXAGONAL:
			return new TMXHexagonalMapRenderer(map);
		case STAGGERED:
			return new TMXStaggeredMapRenderer(map);
		default:
			break;
		}
		throw new LSysException(
				"A TmxMapRenderer has not yet been implemented for " + map.getOrientation() + " orientation");
	}

	public TMXMapRenderer saveCache(LTextureBatch batch) {
		if (!allowCache) {
			return this;
		}
		if (batch != null) {
			textureCaches.put(lastHashCode, batch.newCache());
		}
		return this;
	}

	public boolean postCache(LTextureBatch batch, int hashCode) {
		if (!allowCache) {
			return false;
		}
		Cache cache = textureCaches.get(lastHashCode = hashCode);
		if (cache == null || cache.isClosed()) {
			batch.begin();
		} else if (cache != null && !cache.isClosed()) {
			batch.postCache(cache, baseColor, 0);
			return true;
		} else {
			batch.begin();
		}
		return false;
	}

	@Override
	public void update(long delta) {
		for (TMXAnimation animator : tileAnimators.values()) {
			animator.update(delta);
		}
	}

	public int getTileIndex() {
		return tileIndex;
	}

	public TMXMapRenderer setTileIndex(int idx) {
		tileIndex = idx;
		return this;
	}

	public float centerX() {
		return ((getX() + getViewWidth()) - (getX() + getWidth())) / 2f;
	}

	public float centerY() {
		return ((getY() + getViewHeight()) - (getY() + getHeight())) / 2f;
	}

	public boolean inBounds(float x, float y) {
		float offX = MathUtils.min(this._offset.x);
		float offY = MathUtils.min(this._offset.y);
		if (x < offX) {
			return false;
		}
		if (x >= offX + (getViewWidth() - getWidth())) {
			return false;
		}
		if (y < offY) {
			return false;
		}
		if (y >= offY + (getViewHeight() - getHeight())) {
			return false;
		}
		return true;
	}

	public TMXMapRenderer scrollDown(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min((this._offset.y + distance),
				(MathUtils.max(0, this.getViewHeight() - this.getHeight())));
		if (this._offset.y >= 0) {
			this._offset.y = 0;
		}
		return this;
	}

	public TMXMapRenderer scrollLeft(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min(this._offset.x - distance, this.getX());
		float limitX = (getViewWidth() - getWidth());
		if (this._offset.x <= limitX) {
			this._offset.x = limitX;
		}
		return this;
	}

	public TMXMapRenderer scrollRight(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min((this._offset.x + distance),
				(MathUtils.max(0, this.getWidth() - getViewWidth())));
		if (this._offset.x >= 0) {
			this._offset.x = 0;
		}
		return this;
	}

	public TMXMapRenderer scrollUp(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min(this._offset.y - distance, 0);
		float limitY = (getViewHeight() - getHeight());
		if (this._offset.y <= limitY) {
			this._offset.y = limitY;
		}
		return this;
	}

	public TMXMapRenderer scrollLeftUp(float distance) {
		this.scrollUp(distance);
		this.scrollLeft(distance);
		return this;
	}

	public TMXMapRenderer scrollRightDown(float distance) {
		this.scrollDown(distance);
		this.scrollRight(distance);
		return this;
	}

	public TMXMapRenderer scrollClear() {
		if (!this._offset.equals(0f, 0f)) {
			this._offset.set(0, 0);
		}
		return this;
	}

	public TMXMapRenderer scroll(float x, float y) {
		return scroll(x, y, 4f);
	}

	public ActionBind getFollow() {
		return _follow;
	}

	public TMXMapRenderer setFollow(ActionBind follow) {
		this._follow = follow;
		return this;
	}

	public TMXMapRenderer setFollowOffset(float x, float y) {
		this._followOffset.set(x, y);
		return this;
	}

	public TMXMapRenderer setFollowOffset(Vector2f offset) {
		this._followOffset.set(offset);
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

	public TMXMapRenderer setOffsetX(float sx) {
		this._offset.setX(sx);
		return this;
	}

	public TMXMapRenderer setOffsetY(float sy) {
		this._offset.setY(sy);
		return this;
	}

	public TMXMapRenderer setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public TMXMapRenderer followActionObject() {
		if (_follow != null) {
			float offsetX = limitOffsetX(_follow.getX());
			float offsetY = limitOffsetY(_follow.getY());
			if (offsetX != 0 || offsetY != 0) {
				setOffset(offsetX, offsetY);
				setOffset(_offset);
			}
		}
		return this;
	}

	public Vector2f getFollowOffset() {
		return this._followOffset;
	}

	public TMXMapRenderer followDonot() {
		return setFollow(null);
	}

	public TMXMapRenderer followAction(ActionBind follow) {
		return setFollow(follow);
	}

	public TMXMapRenderer scroll(float x, float y, float distance) {
		if (_scrollDrag.x == 0f && _scrollDrag.y == 0f) {
			_scrollDrag.set(x, y);
			return this;
		}
		return scroll(_scrollDrag.x, _scrollDrag.y, x, y, distance);
	}

	public TMXMapRenderer scroll(float x1, float y1, float x2, float y2) {
		return scroll(x1, y1, x2, y2, 4f);
	}

	public TMXMapRenderer scroll(float x1, float y1, float x2, float y2, float distance) {
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

	protected void renderBackgroundColor(GLEx gl) {
		gl.fillRect(_objectLocation.x, _objectLocation.y, map.getWidth() * map.getTileWidth(),
				map.getHeight() * map.getTileHeight(), map.getBackgroundColor());
	}

	public TMXMapRenderer setLocationToTilePosX(int x) {
		setX(x / map.getTileWidth());
		return this;
	}

	public TMXMapRenderer setLocationToTilePosY(int y) {
		setY(y / map.getTileHeight());
		return this;
	}

	@Override
	public void setColor(LColor c) {
		baseColor.setColor(c);
	}

	@Override
	public LColor getColor() {
		return baseColor;
	}

	public float getMapWidth() {
		return map.getWidth() * map.getTileWidth();
	}

	public float getMapHeight() {
		return map.getHeight() * map.getTileHeight();
	}

	@Override
	public float getWidth() {
		return MathUtils.iceil((getMapWidth() * scaleX) - _fixedWidthOffset);
	}

	@Override
	public float getHeight() {
		return MathUtils.iceil((getMapHeight() * scaleY) - _fixedHeightOffset);
	}

	public void renderImageLayers(GLEx gl, int... layerIDs) {
		if (layerIDs == null || layerIDs.length == 0) {
			for (TMXImageLayer imageLayer : map.getImageLayers())
				renderImageLayer(gl, imageLayer);
		} else {
			for (int layerIndex : layerIDs) {
				if (layerIndex < map.getNumImageLayers()) {
					renderImageLayer(gl, map.getImageLayer(layerIndex));
				}
			}
		}
	}

	public void renderTileLayers(GLEx gl, int... layerIDs) {
		if (layerIDs == null || layerIDs.length == 0) {
			for (TMXTileLayer tileLayer : map.getTileLayers()) {
				renderTileLayer(gl, tileLayer);
			}
		} else {
			for (int layerIndex : layerIDs) {
				if (layerIndex > map.getNumTileLayers()) {
					renderTileLayer(gl, map.getTileLayer(layerIndex));
				}
			}
		}
	}

	public TMXMap getMap() {
		return map;
	}

	protected float getRenderX() {
		return map.getRenderOffsetX();
	}

	protected float getRenderY() {
		return map.getRenderOffsetY();
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}

	@Override
	public void createUI(final GLEx g, final float offsetX, final float offsetY) {
		followActionObject();
		final float tmp = g.alpha();
		final float tmpAlpha = baseColor.a;
		final int color = g.color();
		g.setAlpha(_objectAlpha);
		baseColor.a = _objectAlpha;
		g.setColor(baseColor);
		renderBackgroundColor(g);
		final float ox = getX();
		final float oy = getY();
		setLocation(ox + offsetX + _offset.x, oy + offsetY + _offset.y);
		for (TMXMapLayer mapLayer : map.getLayers()) {
			if (mapLayer instanceof TMXTileLayer) {
				renderTileLayer(g, (TMXTileLayer) mapLayer);
			}
			if (mapLayer instanceof TMXImageLayer) {
				renderImageLayer(g, (TMXImageLayer) mapLayer);
			}
		}
		if (_drawListener != null) {
			_drawListener.draw(g, _objectLocation.x, _objectLocation.y);
		}
		setLocation(ox, oy);
		baseColor.a = tmpAlpha;
		g.setColor(color);
		g.setAlpha(tmp);
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		ObjectMap.Keys<String> keys = textureMap.keys();
		return textureMap.size > 0 ? textureMap.get(keys.next()) : null;
	}

	@Override
	public Field2D getField2D() {
		TArray<Field2D> fileds = map.newIDField2Ds();
		return fileds.size > 0 ? fileds.get(0) : null;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	public TMXMapRenderer setScale(float scale) {
		this.setScale(scale, scale);
		return this;
	}

	@Override
	public void setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public TMXMapRenderer setSize(float w, float h) {
		if ((MathUtils.equal(scaleX, 1f) && MathUtils.equal(scaleY, 1f)) || !MathUtils.equal(_viewWidth, w)
				|| !MathUtils.equal(_viewHeight, h)) {
			setViewSize(w, h);
			setViewScale(w, h);
		}
		return this;
	}

	public void setViewScale(float w, float h) {
		setScale(w / getMapWidth(), h / getMapHeight());
	}

	public void setViewSize(float w, float h) {
		_viewWidth = w;
		_viewHeight = h;
	}

	public float getViewWidth() {
		return _viewWidth;
	}

	public float getViewHeight() {
		return _viewHeight;
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
		return getCollisionArea().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	public RectBox getView() {
		return getCollisionArea();
	}

	@Override
	public int hashCode() {
		int result = map.getTileSets().size;
		result = LSystem.unite(result, _objectLocation.x);
		result = LSystem.unite(result, _objectLocation.y);
		result = LSystem.unite(result, map.getTileWidth());
		result = LSystem.unite(result, map.getTileHeight());
		result = LSystem.unite(result, scaleX);
		result = LSystem.unite(result, scaleY);
		result = LSystem.unite(result, _objectRotation);
		return result;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	@Override
	public ISprite setSprites(Sprites ss) {
		if (this.sprites == ss) {
			return this;
		}
		this.sprites = ss;
		return this;
	}

	@Override
	public Sprites getSprites() {
		return this.sprites;
	}

	@Override
	public Screen getScreen() {
		if (this.sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this.sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this.sprites.getScreen();
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

	public Vector2f getMapToPixel(float tileX, float tileY) {
		return getMapToPixel(tileX, tileY, _tempScreenPos);
	}

	public Vector2f getMapToPixel(float tileX, float tileY, Vector2f newPos) {
		if (newPos == null) {
			newPos = new Vector2f();
		}
		switch (map.getOrientation()) {
		case ORTHOGONAL:
			newPos.x = tileX * map.getTileWidth();
			newPos.y = tileY * map.getTileHeight();
			break;
		case ISOMETRIC:
			newPos.x = map.getWidth() / 2 - (tileY - tileX) * map.getTileWidthHalf();
			newPos.y = (tileY + tileX) * map.getTileHeightHalf();
			break;
		case STAGGERED:
			tileX = MathUtils.ifloor(tileX);
			tileY = MathUtils.ifloor(tileY);
			newPos.x = tileX * map.getTileWidth() + (MathUtils.ifloor(tileY) & 1) * map.getTileWidthHalf();
			newPos.y = tileY * map.getTileHeightHalf();
			break;
		case HEXAGONAL:
			tileX = MathUtils.ifloor(tileX);
			tileY = MathUtils.ifloor(tileY);
			int nTileHeight = map.getTileHeight() * 2 / 3;
			newPos.x = (tileX * map.getTileWidth() + tileY % 2 * map.getTileWidthHalf()) % this.map.getWidth();
			newPos.y = (tileY * nTileHeight) % this.map.getHeight();
			break;
		}
		newPos.x = (newPos.x + this.getX()) * this.getScaleX();
		newPos.y = (newPos.y + this.getY()) * this.getScaleY();
		return newPos;
	}

	public Vector2f getPixelToMap(float screenX, float screenY) {
		return getPixelToMap(screenX, screenY, _tempScreenPos);
	}

	public Vector2f getPixelToMap(float screenX, float screenY, Vector2f newPos) {
		if (newPos == null) {
			newPos = new Vector2f();
		}
		float newY = 0;
		float newX = 0;
		int newTileW = this.map.getTileWidth();
		int newTileH = this.map.getTileHeight();
		screenX = screenX / this.getScaleX() - this.getScreen().getX();
		screenY = screenY / this.getScaleY() - this.getScreen().getY();
		switch (this.map.getOrientation()) {
		case ORTHOGONAL:
			newX = screenX / newTileW;
			newY = screenY / newTileH;
			newPos.x = newX;
			newPos.y = newY;
			break;
		case ISOMETRIC:
			float tDirX = screenX - this.map.getWidth() / 2;
			float tDirY = screenY;
			newY = -(tDirX / newTileW - tDirY / newTileH);
			newX = tDirX / newTileW + tDirY / newTileH;
			newPos.x = newX;
			newPos.y = newY;
			break;
		case STAGGERED:
			float cx, cy, rx, ry;
			cx = MathUtils.ifloor(screenX / newTileW) * newTileW + newTileW / 2;
			cy = MathUtils.ifloor(screenY / newTileH) * newTileH + newTileH / 2;
			rx = (screenX - cx) * newTileH / 2;
			ry = (screenY - cy) * newTileW / 2;
			if (MathUtils.abs(rx) + MathUtils.abs(ry) <= newTileW * newTileH / 4) {
				newX = MathUtils.floor(screenX / newTileW);
				newY = MathUtils.floor(screenY / newTileH) * 2;
			} else {
				screenX = screenX - newTileW / 2;
				newX = MathUtils.floor(screenX / newTileW) + 1;
				screenY = screenY - newTileH / 2;
				newY = MathUtils.floor(screenY / newTileH) * 2 + 1;
			}
			newPos.x = newX - (MathUtils.ifloor(newY) & 1);
			newPos.y = newY;
			break;
		case HEXAGONAL:
			int tTileHeight = newTileH * 2 / 3;
			newY = screenY / tTileHeight;
			newX = (screenX - newY % 2 * map.getTileWidthHalf()) / newTileW;
			newPos.x = newX;
			newPos.y = newY;
			break;
		}
		return newPos;
	}

	public abstract Vector2f pixelToTileCoords(float x, float y);

	public abstract Vector2f tileToPixelCoords(float x, float y);

	@Override
	public boolean collides(ISprite e) {
		if (e == null || !e.isVisible()) {
			return false;
		}
		return getRectBox().intersects(e.getCollisionBox());
	}

	@Override
	public boolean collidesX(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox a = new RectBox(rectSelf.getX(), 0, rectSelf.getWidth(), rectSelf.getHeight());
		RectBox rectDst = getRectBox();
		RectBox b = new RectBox(rectDst.getX(), 0, rectDst.getWidth(), rectDst.getHeight());
		return a.intersects(b);
	}

	@Override
	public boolean collidesY(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox a = new RectBox(0, rectSelf.getY(), rectSelf.getWidth(), rectSelf.getHeight());
		RectBox rectDst = getRectBox();
		RectBox b = new RectBox(0, rectDst.getY(), rectDst.getWidth(), rectDst.getHeight());
		return a.intersects(b);
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		if (_collSpriteListener != null) {
			_collSpriteListener.onCollideUpdate(coll, dir);
		}
	}

	@Override
	public TMXMapRenderer triggerCollision(SpriteCollisionListener sc) {
		this._collSpriteListener = sc;
		return this;
	}

	@Override
	public void onResize() {
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
	}

	public ResizeListener<TMXMapRenderer> getResizeListener() {
		return _resizeListener;
	}

	public TMXMapRenderer setResizeListener(ResizeListener<TMXMapRenderer> listener) {
		this._resizeListener = listener;
		return this;
	}

	@Override
	public TMXMapRenderer setOffset(Vector2f v) {
		if (v != null) {
			this._offset = v;
		}
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

	public boolean isAllowCache() {
		return allowCache;
	}

	public TMXMapRenderer setAllowCache(boolean a) {
		this.allowCache = a;
		return this;
	}

	@Override
	public boolean showShadow() {
		return false;
	}

	@Override
	public ISprite resetAnchor() {
		return this;
	}

	@Override
	public ISprite setAnchor(float sx, float sy) {
		return this;
	}

	public Vector2f offsetPixels(float x, float y) {
		return new Vector2f(offsetXPixel(x), offsetYPixel(y));
	}

	public int getPixelX(float x) {
		return MathUtils.iceil((x - _objectLocation.x) / scaleX);
	}

	public int getPixelY(float y) {
		return MathUtils.iceil((y - _objectLocation.y) / scaleY);
	}

	public int offsetXPixel(float x) {
		return MathUtils.iceil((x - _offset.x - _objectLocation.x) / scaleX);
	}

	public int offsetYPixel(float y) {
		return MathUtils.iceil((y - _offset.y - _objectLocation.y) / scaleY);
	}

	@Override
	public boolean autoXYSort() {
		return false;
	}

	public DrawListener<TMXMapRenderer> getDrawListener() {
		return _drawListener;
	}

	public TMXMapRenderer setDrawListener(DrawListener<TMXMapRenderer> drawListener) {
		this._drawListener = drawListener;
		return this;
	}

	public DrawLoop<TMXMapRenderer> getDrawable() {
		if (_drawListener != null && _drawListener instanceof DrawLoop) {
			return ((DrawLoop<TMXMapRenderer>) _drawListener);
		}
		return null;
	}

	public DrawLoop<TMXMapRenderer> drawable(DrawLoop.Drawable draw) {
		DrawLoop<TMXMapRenderer> loop = null;
		if (_drawListener != null && _drawListener instanceof DrawLoop) {
			loop = getDrawable().onDrawable(draw);
		} else {
			setDrawListener(loop = new DrawLoop<TMXMapRenderer>(this, draw));
		}
		return loop;
	}

	@Override
	public ISprite buildToScreen() {
		if (sprites != null) {
			sprites.add(this);
			return this;
		}
		getScreen().add(this);
		return this;
	}

	@Override
	public ISprite removeFromScreen() {
		if (sprites != null) {
			sprites.remove(this);
			return this;
		}
		getScreen().remove(this);
		return this;
	}

	@Override
	protected void _onDestroy() {
		visible = false;
		if (textureMap != null) {
			for (LTexture texture : textureMap.values()) {
				texture.close();
			}
			textureMap.clear();
		}
		if (tileAnimators != null) {
			tileAnimators.clear();
		}
		if (textureCaches != null) {
			for (Cache cache : textureCaches) {
				if (cache != null) {
					cache.close();
				}
			}
			textureCaches.clear();
		}
		lastHashCode = 1;
		_drawListener = null;
		_resizeListener = null;
		_collSpriteListener = null;
	}

}
