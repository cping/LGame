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
import loon.PlayerUtils;
import loon.Screen;
import loon.LTextureBatch.Cache;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXMapLayer;
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.TMXTileSet;
import loon.action.map.tmx.tiles.TMXAnimationFrame;
import loon.action.map.tmx.tiles.TMXTile;
import loon.action.sprite.ISprite;
import loon.action.sprite.SpriteCollisionListener;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.events.ResizeListener;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.TimeUtils;

/**
 * TMX地图渲染用基本抽象类,所有TMX地图文件的渲染皆由此类的子类负责具体实现
 */
public abstract class TMXMapRenderer extends LObject<ISprite> implements Sized, ISprite {

	protected LTextureBatch _texBatch;

	protected LTexture _texCurrent;

	private ResizeListener<TMXMapRenderer> _resizeListener;

	private SpriteCollisionListener _collSpriteListener;

	protected Vector2f _mapLocation = new Vector2f();

	protected Vector2f _offset = new Vector2f();
	protected float _fixedWidthOffset = 0f;
	protected float _fixedHeightOffset = 0f;

	protected Sprites sprites = null;

	protected int lastHashCode = 1;

	protected static class TileAnimator {

		private TMXTile tile;

		private int currentFrameIndex;
		private float elapsedDuration;

		public TileAnimator(TMXTile tile) {
			this.tile = tile;
			elapsedDuration = 0;
			currentFrameIndex = 0;
		}

		public void update(long delta) {
			elapsedDuration += TimeUtils.convert(delta, TimeUtils.getDefaultTimeUnit(), TimeUtils.Unit.MILLIS);

			if (elapsedDuration >= tile.getFrames().get(currentFrameIndex).getDuration()) {
				currentFrameIndex = (currentFrameIndex + 1) % tile.getFrames().size;
				elapsedDuration = 0;
			}
		}

		public TMXAnimationFrame getCurrentFrame() {
			return tile.getFrames().get(currentFrameIndex);
		}
	}

	protected abstract void renderTileLayer(GLEx gl, TMXTileLayer tileLayer);

	protected abstract void renderImageLayer(GLEx gl, TMXImageLayer imageLayer);

	protected TMXMap map;

	protected IntMap<LTextureBatch.Cache> textureCaches;
	protected ObjectMap<String, LTexture> textureMap;
	protected ObjectMap<TMXTile, TileAnimator> tileAnimators;

	protected boolean visible;
	protected float scaleX = 1f;
	protected float scaleY = 1f;

	protected LColor baseColor = new LColor(LColor.white);

	protected boolean allowCache;

	public TMXMapRenderer(TMXMap map) {
		this.textureCaches = new IntMap<LTextureBatch.Cache>();
		this.textureMap = new ObjectMap<String, LTexture>();
		this.tileAnimators = new ObjectMap<TMXTile, TileAnimator>();
		this.visible = allowCache = true;
		this.map = map;

		for (TMXTileSet tileSet : map.getTileSets()) {
			String path = tileSet.getImage().getSource();
			if (!textureMap.containsKey(path)) {
				textureMap.put(path, LSystem.loadTexture(path));
			}
			for (TMXTile tile : tileSet.getTiles()) {
				if (tile.isAnimated()) {
					TileAnimator animator = new TileAnimator(tile);
					tileAnimators.put(tile, animator);
				}
			}
		}

		for (TMXImageLayer imageLayer : map.getImageLayers()) {
			String path = imageLayer.getImage().getSource();
			if (!textureMap.containsKey(path)) {
				textureMap.put(path, LSystem.loadTexture(path));
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
			batch.setBlendState(BlendState.AlphaBlend);
			batch.postCache(cache, baseColor, 0);
			return true;
		} else {
			batch.begin();
		}
		return false;
	}

	public void update(long delta) {
		for (TileAnimator animator : tileAnimators.values()) {
			animator.update(delta);
		}
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

	public LColor getColor() {
		return baseColor;
	}

	@Override
	public float getWidth() {
		return (map.getWidth() * map.getTileWidth() * scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return (map.getHeight() * map.getTileHeight() * scaleY) - _fixedHeightOffset;
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
	public void createUI(GLEx g, float offsetX, float offsetY) {
		float tmp = g.alpha();
		float tmpAlpha = baseColor.a;
		int color = g.color();
		g.setAlpha(_objectAlpha);
		baseColor.a = _objectAlpha;
		g.setColor(baseColor);
		renderBackgroundColor(g);
		float ox = getX();
		float oy = getY();
		setLocation(ox + offsetX + _offset.x, oy + offsetY + _offset.y);
		for (TMXMapLayer mapLayer : map.getLayers()) {
			if (mapLayer instanceof TMXTileLayer) {
				renderTileLayer(g, (TMXTileLayer) mapLayer);
			}
			if (mapLayer instanceof TMXImageLayer) {
				renderImageLayer(g, (TMXImageLayer) mapLayer);
			}
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
	public int getZ() {
		return getZOrder();
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
	public boolean showShadow() {
		return false;
	}

	public boolean isClosed() {
		return isDisposed();
	}

	public boolean isAllowCache() {
		return allowCache;
	}

	public TMXMapRenderer setAllowCache(boolean a) {
		this.allowCache = a;
		return this;
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
	public boolean autoXYSort() {
		return false;
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
	public void close() {
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
		_resizeListener = null;
		_collSpriteListener = null;
		setState(State.DISPOSED);
	}

}
