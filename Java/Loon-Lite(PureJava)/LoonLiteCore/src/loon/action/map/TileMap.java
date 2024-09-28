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
package loon.action.map;

import loon.LObject;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D.MapSwitchMaker;
import loon.action.map.colider.TileImpl;
import loon.action.map.items.Attribute;
import loon.action.sprite.Animation;
import loon.action.sprite.ISprite;
import loon.action.sprite.MoveControl;
import loon.action.sprite.SpriteCollisionListener;
import loon.action.sprite.Sprites;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.events.DrawListener;
import loon.events.ResizeListener;
import loon.geom.Affine2f;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.opengl.LTexturePackClip;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 一个简单(易于操作)的二维数组地图构造以及显示类.复杂地图请使用tmx包
 */
public class TileMap extends LObject<ISprite> implements TileMapCollision, Sized, ISprite {

	private int lastOffsetX, lastOffsetY;

	private int firstTileX;

	private int firstTileY;

	private int lastTileX;

	private int lastTileY;

	public DrawListener<TileMap> _drawListener;

	private LTexturePack _texturePack;

	private LTexture _background;

	// 地图自身存储子精灵的的Sprites
	private Sprites _mapSprites;

	// 显示Map的上级Sprites
	private Sprites _screenSprites;

	private ResizeListener<TileMap> _resizeListener;

	private SpriteCollisionListener _collSpriteListener;

	private TArray<TileImpl> _arrays = new TArray<TileImpl>(10);

	private TArray<Animation> _animations = new TArray<Animation>();

	private final int _pixelInWidth, _pixelInHeight;

	private final Field2D _field2d;

	private final PointF _scrollDrag = new PointF();

	private float _fixedWidthOffset = 0f;

	private float _fixedHeightOffset = 0f;

	private float _scaleX = 1f, _scaleY = 1f;

	private boolean _active, _dirty;

	private boolean _visible, _roll;

	private boolean _playAnimation;

	private ActionBind _follow;

	private Vector2f _offset = new Vector2f(0f, 0f);

	private LColor _baseColor = LColor.white;

	public TileMap(String fileName, int tileWidth, int tileHeight) {
		this(fileName, tileWidth, tileHeight, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(String fileName, int tileWidth, int tileHeight, int mWidth, int mHeight) {
		this(TileMapConfig.loadAthwartArray(fileName), tileWidth, tileHeight, mWidth, mHeight);
	}

	public TileMap(String fileName, Screen screen, int tileWidth, int tileHeight, int mWidth, int mHeight) {
		this(TileMapConfig.loadAthwartArray(fileName), screen, tileWidth, tileHeight, mWidth, mHeight);
	}

	public TileMap(int[][] maps, int tileWidth, int tileHeight, int mWidth, int mHeight) {
		this(new Field2D(maps, tileWidth, tileHeight), mWidth, mHeight);
	}

	public TileMap(int[][] maps, Screen screen, int tileWidth, int tileHeight, int mWidth, int mHeight) {
		this(new Field2D(maps, tileWidth, tileHeight), screen, mWidth, mHeight);
	}

	public TileMap(int[][] maps, int tileWidth, int tileHeight) {
		this(maps, tileWidth, tileHeight, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(int[][] maps, Screen screen, int tileWidth, int tileHeight) {
		this(maps, screen, tileWidth, tileHeight, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(Field2D field2d) {
		this(field2d, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(Field2D field2d, Screen screen) {
		this(field2d, screen, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(Field2D field2d, int mWidth, int mHeight) {
		this(field2d, null, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public TileMap(Field2D field2d, Screen screen, int mWidth, int mHeight) {
		this._field2d = field2d;
		if (field2d != null && mWidth == -1 && mHeight == -1) {
			this._pixelInWidth = field2d.getViewWidth();
			this._pixelInHeight = field2d.getViewHeight();
		} else {
			this._pixelInWidth = mWidth;
			this._pixelInHeight = mHeight;
		}
		if (field2d == null) {
			this._offset = new Vector2f(0, 0);
		} else {
			this._offset = field2d.getOffset();
		}
		this._texturePack = new LTexturePack();
		this.lastOffsetX = -1;
		this.lastOffsetY = -1;
		this._scaleX = this._scaleY = 1f;
		this._active = true;
		this._dirty = true;
		this._visible = true;
		this._mapSprites = new Sprites("TileMapSprites", screen == null ? LSystem.getProcess().getScreen() : screen,
				_pixelInWidth, _pixelInHeight);
	}

	public static TileMap loadCharsMap(String resName, int tileWidth, int tileHeight) {
		return new TileMap(TileMapConfig.loadCharsField(resName, tileWidth, tileHeight));
	}

	public TileMap setImagePackAuto(String fileName, int tileWidth, int tileHeight) {
		if (_texturePack != null) {
			_texturePack.close();
			_texturePack = null;
		}
		_texturePack = new LTexturePack(fileName, LTexturePackClip.getTextureSplit(fileName, tileWidth, tileHeight));
		_texturePack.packed();
		return this;
	}

	public TileMap setImagePack(String fileName, LTexturePackClip[] clips) {
		return setImagePack(fileName, new TArray<>(clips));
	}

	public TileMap setImagePack(String fileName, TArray<LTexturePackClip> clips) {
		if (_texturePack != null) {
			_texturePack.close();
			_texturePack = null;
		}
		this._active = false;
		this._dirty = true;
		_texturePack = new LTexturePack(fileName, clips);
		_texturePack.packed();
		return this;
	}

	public <T extends LRelease> TileMap setImagePack(TileAllocation<T> allocation) {
		if (allocation == null) {
			return this;
		}
		return setImagePack(allocation.getPath(), allocation.getClips());
	}

	public TileMap setImagePack(String file) {
		if (_texturePack != null) {
			_texturePack.close();
			_texturePack = null;
		}
		this._active = false;
		this._dirty = true;
		_texturePack = new LTexturePack(file);
		_texturePack.packed();
		return this;
	}

	public TileMap removeTile(int id) {
		final int size = _arrays.size;
		final TArray<TileImpl> tiles = _arrays;
		for (int i = size - 1; i > -1; i--) {
			TileImpl tile = tiles.get(i);
			if (tile != null && tile.getId() == id) {
				if (tile.isAnimation()) {
					_animations.remove(tile.getAnimation());
				}
				tiles.removeIndex(i);
			}
		}
		this._arrays = tiles;
		if (_animations.size == 0) {
			_playAnimation = false;
		}
		this._dirty = true;
		return this;
	}

	public int putAnimationTile(int id, Animation animation, Attribute attribute) {
		if (_active) {
			TileImpl tile = new TileImpl(id);
			tile.setImgId(-1);
			tile.setAttribute(attribute);
			if (animation != null && animation.getTotalFrames() > 0) {
				tile.setAnimation(animation);
				_playAnimation = true;
			}
			_animations.add(animation);
			_arrays.add(tile);
			_dirty = true;
			return tile.getImgId();
		} else {
			throw new LSysException("Map is no longer active, you can not add new tiles !");
		}
	}

	public int putAnimationTile(int id, String res, int w, int h, int timer) {
		return putAnimationTile(id, Animation.getDefaultAnimation(res, w, h, timer), null);
	}

	public int putAnimationTile(int id, Animation animation) {
		return putAnimationTile(id, animation, null);
	}

	public int putTile(int id, Image img, Attribute attribute) {
		if (_active) {
			TileImpl tile = new TileImpl(id);
			tile.setImgId(_texturePack.putImage(img));
			tile.setAttribute(attribute);
			_arrays.add(tile);
			_dirty = true;
			return tile.getImgId();
		} else {
			throw new LSysException("Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, Image img) {
		return putTile(id, img, null);
	}

	public int putTile(int id, LTexture img, Attribute attribute) {
		if (_active) {
			TileImpl tile = new TileImpl(id);
			tile.setImgId(_texturePack.putImage(img));
			tile.setAttribute(attribute);
			_arrays.add(tile);
			_dirty = true;
			return tile.getImgId();
		} else {
			throw new LSysException("Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, LTexture img) {
		return putTile(id, img, null);
	}

	public int putTile(int id, String res, Attribute attribute) {
		if (_active) {
			TileImpl tile = new TileImpl(id);
			tile.setImgId(_texturePack.putImage(res));
			tile.setAttribute(attribute);
			_arrays.add(tile);
			_dirty = true;
			return tile.getImgId();
		} else {
			throw new LSysException("Map is no longer active, you can not add new tiles !");
		}
	}

	public int putTile(int id, String res) {
		return putTile(id, res, null);
	}

	public TileMap putTile(int id, int imgId, Attribute attribute) {
		if (_active) {
			TileImpl tile = new TileImpl(id);
			tile.setImgId(imgId);
			tile.setAttribute(attribute);
			_arrays.add(tile);
			_dirty = true;
		} else {
			throw new LSysException("Map is no longer active, you can not add new tiles !");
		}
		return this;
	}

	public TileMap putTile(int id, int imgId) {
		return putTile(id, imgId, null);
	}

	public TileImpl getTile(int id) {
		final int size = _arrays.size;
		final TArray<TileImpl> tiles = _arrays;
		for (int i = size - 1; i > -1; i--) {
			TileImpl tile = tiles.get(i);
			if (tile != null && tile.getId() == id) {
				return tile;
			}
		}
		return null;
	}

	@Override
	public int[][] getMap() {
		return _field2d.getMap();
	}

	public boolean isActive() {
		return _active;
	}

	public boolean isValid(int x, int y) {
		return this._field2d.inside(x, y);
	}

	public TileMap pack() {
		completed();
		return this;
	}

	public TileMap completed() {
		if (_texturePack != null) {
			if (!_texturePack.isPacked()) {
				_texturePack.packed();
			}
			final int[] list = _texturePack.getIdList();
			_active = true;
			_dirty = true;
			for (int i = 0; i < list.length; i++) {
				int id = list[i];
				putTile(id, id);
			}
		}
		return this;
	}

	public TileMap replaceType(int oldid, int newid) {
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

	public TileMap setTileID(int x, int y, int id) {
		if (x >= 0 && x < _field2d.getWidth() && y >= 0 && y < _field2d.getHeight()) {
			_field2d.setTileType(x, y, id);
		}
		return this;
	}

	public TileMap addMapSprite(ISprite sprite) {
		_mapSprites.add(sprite);
		return this;
	}

	public TileMap addMapSpriteAt(ISprite sprite, float x, float y) {
		_mapSprites.addAt(sprite, x, y);
		return this;
	}

	public TileMap removeMapSprite(int idx) {
		_mapSprites.remove(idx);
		return this;
	}

	public TileMap removeMapSprite(ISprite sprite) {
		_mapSprites.remove(sprite);
		return this;
	}

	public TileMap removeMapSprite(int start, int end) {
		_mapSprites.remove(start, end);
		return this;
	}

	public void draw(GLEx g) {
		if (this._roll) {
			this._offset = this.toRollPosition(this._offset);
		}
		draw(g, x() + _offset.x(), y() + _offset.y());
	}

	public void draw(GLEx g, int offsetX, int offsetY) {

		if (_background != null) {
			g.draw(_background, offsetX, offsetY);
		}

		if (!_active || _texturePack == null) {
			completed();
			return;
		}

		this._dirty = this._dirty || !_texturePack.existCache();

		if (!_dirty && lastOffsetX == offsetX && lastOffsetY == offsetY) {

			_texturePack.postCache();

			if (_playAnimation) {
				final int tileWidth = _field2d.getTileWidth();
				final int tileHeight = _field2d.getTileHeight();
				final int[][] maps = _field2d.getMap();
				for (int i = firstTileX; i < lastTileX; i++) {
					for (int j = firstTileY; j < lastTileY; j++) {
						if (i > -1 && j > -1 && i < _field2d.getWidth() && j < _field2d.getHeight()) {
							int id = maps[j][i];
							final float posX = _field2d.tilesToWidthPixels(i) + offsetX;
							final float posY = _field2d.tilesToHeightPixels(j) + offsetY;
							final TArray<TileImpl> tiles = _arrays;
							final int size = tiles.size;
							for (int n = 0; n < size; n++) {
								TileImpl tile = tiles.get(n);
								if (tile.isAnimation() && tile.getId() == id) {
									g.draw(tile.getAnimation().getSpriteImage(), posX, posY, tileWidth, tileHeight,
											_baseColor);
								}
							}
						}
					}
				}
			}
		} else {
			if (_arrays.size == 0) {
				throw new LSysException("Not to add any tiles !");
			}

			_texturePack.glBegin();

			firstTileX = _field2d.pixelsToTilesWidth(-offsetX);
			firstTileY = _field2d.pixelsToTilesHeight(-offsetY);

			lastTileX = firstTileX + _field2d.pixelsToTilesWidth(_pixelInWidth) + 1;
			lastTileX = MathUtils.min(lastTileX, _field2d.getWidth());
			lastTileY = firstTileY + _field2d.pixelsToTilesHeight(_pixelInHeight) + 1;
			lastTileY = MathUtils.min(lastTileY, _field2d.getHeight());

			final int width = _field2d.getWidth();
			final int height = _field2d.getHeight();
			final int tileWidth = _field2d.getTileWidth();
			final int tileHeight = _field2d.getTileHeight();
			final int[][] maps = _field2d.getMap();
			for (int i = firstTileX; i < lastTileX; i++) {
				for (int j = firstTileY; j < lastTileY; j++) {
					if (i > -1 && j > -1 && i < width && j < height) {
						int id = maps[j][i];
						final float posX = _field2d.tilesToWidthPixels(i) + offsetX;
						final float posY = _field2d.tilesToHeightPixels(j) + offsetY;
						final TArray<TileImpl> tiles = _arrays;
						final int size = tiles.size;
						for (int n = 0; n < size; n++) {
							TileImpl tile = tiles.get(n);
							if (_playAnimation) {
								if (tile.getId() == id) {
									if (tile.isAnimation()) {
										g.draw(tile.getAnimation().getSpriteImage(), posX, posY, tileWidth, tileHeight,
												_baseColor);
									} else {
										_texturePack.draw(tile.getImgId(), posX, posY, tileWidth, tileHeight,
												_baseColor);
									}
								}
							} else if (tile.getId() == id) {
								_texturePack.draw(tile.getImgId(), posX, posY, tileWidth, tileHeight);
							}
						}
					}
				}
			}

			_texturePack.glEnd();
			_texturePack.saveCache();

			lastOffsetX = offsetX;
			lastOffsetY = offsetY;
			_dirty = false;
		}

		if (_drawListener != null) {
			_drawListener.draw(g, offsetX, offsetY);
		}
	}

	public float centerX() {
		return ((getContainerX() + getContainerWidth()) - (getX() + getWidth())) / 2f;
	}

	public float centerY() {
		return ((getContainerY() + getContainerHeight()) - (getY() + getHeight())) / 2f;
	}

	public TileMap scrollDown(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min((this._offset.y + distance),
				(MathUtils.max(0, this.getContainerHeight() - this.getHeight())));
		if (this._offset.y >= 0) {
			this._offset.y = 0;
		}
		return this;
	}

	public TileMap scrollLeft(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min(this._offset.x - distance, this.getX());
		float limitX = (getContainerWidth() - getWidth());
		if (this._offset.x <= limitX) {
			this._offset.x = limitX;
		}
		return this;
	}

	public TileMap scrollRight(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.x = MathUtils.min((this._offset.x + distance),
				(MathUtils.max(0, this.getWidth() - getContainerWidth())));
		if (this._offset.x >= 0) {
			this._offset.x = 0;
		}
		return this;
	}

	public TileMap scrollUp(float distance) {
		if (distance == 0) {
			return this;
		}
		this._offset.y = MathUtils.min(this._offset.y - distance, 0);
		float limitY = (getContainerHeight() - getHeight());
		if (this._offset.y <= limitY) {
			this._offset.y = limitY;
		}
		return this;
	}

	public TileMap scrollLeftUp(float distance) {
		this.scrollUp(distance);
		this.scrollLeft(distance);
		return this;
	}

	public TileMap scrollRightDown(float distance) {
		this.scrollDown(distance);
		this.scrollRight(distance);
		return this;
	}

	public TileMap scrollClear() {
		if (!this._offset.equals(0f, 0f)) {
			this._offset.set(0, 0);
		}
		return this;
	}

	public TileMap scroll(float x, float y) {
		return scroll(x, y, 4f);
	}

	public TileMap scroll(float x, float y, float distance) {
		if (_scrollDrag.x == 0f && _scrollDrag.y == 0f) {
			_scrollDrag.set(x, y);
			return this;
		}
		return scroll(_scrollDrag.x, _scrollDrag.y, x, y, distance);
	}

	public TileMap scroll(float x1, float y1, float x2, float y2) {
		return scroll(x1, y1, x2, y2, 4f);
	}

	public TileMap scroll(float x1, float y1, float x2, float y2, float distance) {
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

	public TileMap setLimit(int[] limit) {
		_field2d.setLimit(limit);
		return this;
	}

	public TileMap setAllowMove(int[] args) {
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

	public boolean isPixelTUp(int px, int py) {
		return isPixelHit(px, py, 0, -1);
	}

	public boolean isPixelTRight(int px, int py) {
		return isPixelHit(px, py, 1, 0);
	}

	public boolean isPixelTLeft(int px, int py) {
		return isPixelHit(px, py, -1, 0);
	}

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
		float x = (sx + _offset.getX());
		float y = (sy + _offset.getY());
		Vector2f tileCoordinates = pixelsToTiles(x, y);
		return getTileID(MathUtils.round(tileCoordinates.getX()), MathUtils.round(tileCoordinates.getY()));
	}

	public Vector2f pixelsToTiles(float x, float y) {
		float xprime = x / _field2d.getTileWidth() - 1;
		float yprime = y / _field2d.getTileHeight() - 1;
		return new Vector2f(xprime, yprime);
	}

	public int tilesToPixelsX(float x) {
		return _field2d.tilesToWidthPixels(x);
	}

	public int tilesToPixelsY(float y) {
		return _field2d.tilesToHeightPixels(y);
	}

	public int pixelsToTilesWidth(float x) {
		return _field2d.pixelsToTilesWidth(x);
	}

	public int pixelsToTilesHeight(float y) {
		return _field2d.pixelsToTilesHeight(y);
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
		float xprime = x * _field2d.getTileWidth() - _offset.getX();
		float yprime = y * _field2d.getTileHeight() - _offset.getY();
		return new Vector2f(xprime, yprime);
	}

	public TileMap switchMap(MapSwitchMaker ms) {
		_field2d.switchMap(ms);
		return this;
	}

	/**
	 * 地图居中偏移
	 *
	 * @return
	 */
	public TileMap centerOffset() {
		this._offset.set(centerX(), centerY());
		return this;
	}

	/**
	 * 设定偏移量
	 *
	 * @param x
	 * @param y
	 */
	public TileMap setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	/**
	 * 设定偏移量
	 *
	 * @param _offset
	 */
	@Override
	public TileMap setOffset(Vector2f offset) {
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
		return (_field2d.getHeight() * _field2d.getTileWidth() * _scaleY) - _fixedHeightOffset;
	}

	@Override
	public float getWidth() {
		return (_field2d.getWidth() * _field2d.getTileHeight() * _scaleX) - _fixedWidthOffset;
	}

	@Override
	public int getRow() {
		return _field2d.getWidth();
	}

	@Override
	public int getCol() {
		return _field2d.getHeight();
	}

	public float getFlippedX() {
		return getContainerWidth() - (getScreenX() + getWidth());
	}

	public float getFlippedY() {
		return getContainerHeight() - (getScreenY() + getHeight());
	}

	public TileMap setMapValues(int v) {
		_field2d.setValues(v);
		return this;
	}

	public Field2D getNewField2D() {
		return new Field2D(_field2d);
	}

	public DrawListener<TileMap> getListener() {
		return _drawListener;
	}

	public TileMap setListener(DrawListener<TileMap> l) {
		this._drawListener = l;
		return this;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public TileMap setDirty(boolean dirty) {
		this._dirty = dirty;
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
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!_visible) {
			return;
		}
		boolean update = (_objectRotation != 0) || !(_scaleX == 1f && _scaleY == 1f);
		int blend = g.getBlendMode();
		int tmp = g.color();
		try {
			g.setAlpha(_objectAlpha);
			if (this._roll) {
				this._offset = toRollPosition(this._offset);
			}
			float newX = this._objectLocation.x + offsetX + _offset.getX();
			float newY = this._objectLocation.y + offsetY + _offset.getY();
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				if (_objectRotation != 0) {
					final float rotationCenterX = newX + getWidth() / 2f;
					final float rotationCenterY = newY + getHeight() / 2f;
					tx.translate(rotationCenterX, rotationCenterY);
					tx.preRotate(_objectRotation);
					tx.translate(-rotationCenterX, -rotationCenterY);
				}
				if ((_scaleX != 1) || (_scaleY != 1)) {
					final float scaleCenterX = newX + getWidth() / 2f;
					final float scaleCenterY = newY + getHeight() / 2f;
					tx.translate(scaleCenterX, scaleCenterY);
					tx.preScale(_scaleX, _scaleY);
					tx.translate(-scaleCenterX, -scaleCenterY);
				}
			}
			followActionObject();
			final int moveX = MathUtils.ifloor(newX);
			final int moveY = MathUtils.ifloor(newY);
			draw(g, moveX, moveY);
			if (_mapSprites != null) {
				_mapSprites.paintPos(g, moveX, moveY);
			}
		} catch (Throwable ex) {
			LSystem.error("Array2D TileMap error !", ex);
		} finally {
			if (update) {
				g.restoreTx();
			}
			g.setBlendMode(blend);
			g.setColor(tmp);
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x() + _offset.x, y() + _offset.y, _field2d.getTileWidth() * _field2d.getWidth(),
				_field2d.getTileHeight() * _field2d.getHeight());
	}

	@Override
	public LTexture getBitmap() {
		return _texturePack.getTexture();
	}

	@Override
	public void update(long elapsedTime) {
		if (_playAnimation && _animations.size > 0) {
			final int size = _animations.size;
			final TArray<Animation> ans = _animations;
			for (int i = size - 1; i > -1; i--) {
				Animation an = ans.get(i);
				if (an != null) {
					an.update(elapsedTime);
				}
			}
		}
		if (_mapSprites != null) {
			_mapSprites.update(elapsedTime);
		}
		if (_drawListener != null) {
			_drawListener.update(elapsedTime);
		}
	}

	public TileMap startAnimation() {
		_playAnimation = true;
		return this;
	}

	public TileMap stopAnimation() {
		_playAnimation = false;
		return this;
	}

	protected float limitOffsetX(float newOffsetX) {
		float offsetX = getContainerWidth() / 2 - newOffsetX;
		offsetX = MathUtils.min(offsetX, 0);
		offsetX = MathUtils.max(offsetX, getContainerWidth() - getWidth());
		return offsetX;
	}

	protected float limitOffsetY(float newOffsetY) {
		float offsetY = getContainerHeight() / 2 - newOffsetY;
		offsetY = MathUtils.min(offsetY, 0);
		offsetY = MathUtils.max(offsetY, getContainerHeight() - getHeight());
		return offsetY;
	}

	public TileMap followActionObject() {
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
			this._dirty = true;
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
		return _scaleX;
	}

	@Override
	public float getScaleY() {
		return _scaleY;
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}

	@Override
	public void setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
	}

	@Override
	public TileMap setSize(float w, float h) {
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

	public TileMap setFollow(ActionBind follow) {
		this._follow = follow;
		return this;
	}

	public TileMap followDonot() {
		return setFollow(null);
	}

	public TileMap followAction(ActionBind follow) {
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
		return MathUtils.iceil((x - _objectLocation.x) / _scaleX);
	}

	public int getPixelY(float y) {
		return MathUtils.iceil((y - _objectLocation.y) / _scaleY);
	}

	public int offsetXPixel(float x) {
		return MathUtils.iceil((x - _offset.x - _objectLocation.x) / _scaleX);
	}

	public int offsetYPixel(float y) {
		return MathUtils.iceil((y - _offset.y - _objectLocation.y) / _scaleY);
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
		if (pos.x < 0f) {
			pos.y += _field2d.getViewHeight();
		}
		return pos;
	}

	public boolean isRoll() {
		return _roll;
	}

	public TileMap setRoll(boolean roll) {
		this._roll = roll;
		return this;
	}

	public LTexture getBackground() {
		return this._background;
	}

	public TileMap setBackground(LTexture bg) {
		this._background = bg;
		return this;
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

	public TileMap setMapSprites(Sprites s) {
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
	public TileMap triggerCollision(SpriteCollisionListener sc) {
		this._collSpriteListener = sc;
		return this;
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

	public ResizeListener<TileMap> getResizeListener() {
		return _resizeListener;
	}

	public TileMap setResizeListener(ResizeListener<TileMap> listener) {
		this._resizeListener = listener;
		return this;
	}

	public TileMap setOffsetX(float sx) {
		this._offset.setX(sx);
		return this;
	}

	public TileMap setOffsetY(float sy) {
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
	public boolean showShadow() {
		return false;
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

	public boolean isClosed() {
		return isDisposed();
	}

	@Override
	public String toString() {
		return _field2d.toString();
	}

	@Override
	public void close() {
		_visible = false;
		_playAnimation = false;
		_roll = false;
		_animations.clear();
		if (_texturePack != null) {
			_texturePack.close();
			_texturePack = null;
		}
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
		setState(State.DISPOSED);
	}

}
