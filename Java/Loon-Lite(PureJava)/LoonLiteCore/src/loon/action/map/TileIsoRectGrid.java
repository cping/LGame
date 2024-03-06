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
package loon.action.map;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.sprite.ISprite;
import loon.action.sprite.SpriteCollisionListener;
import loon.action.sprite.Sprites;
import loon.canvas.LColor;
import loon.events.DrawListener;
import loon.events.EventActionTN;
import loon.events.ResizeListener;
import loon.font.IFont;
import loon.geom.Affine2f;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.Sized;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 工具用类,同时绘制多个斜角网格区域用
 */
public class TileIsoRectGrid extends LObject<ISprite> implements Sized, ISprite {

	private final PointF _scrollDrag = new PointF();

	private EventActionTN<TileIsoRectGrid, Long> _event;

	private TileIsoRect[][] _grids;

	private boolean _closed;

	private boolean _visible;

	private boolean _showCoordinate;

	private boolean _sinUpdate;

	private boolean _roll;

	private int _pixelInWidth;

	private int _pixelInHeight;

	private int _tileWidth;

	private int _tileHeight;

	private int _rows;

	private int _cols;

	private float _initAngle;

	private float _initWidth;

	private float _initHeight;

	private float _offsetX;

	private float _offsetY;

	private float _fixedWidthOffset = 0f;

	private float _fixedHeightOffset = 0f;

	private float _angle;

	private float _scaleX = 1f, _scaleY = 1f;

	public DrawListener<TileIsoRectGrid> _drawListener;

	private Field2D _field2d;

	private LTexture _background;

	private ResizeListener<TileIsoRectGrid> _resizeListener;

	private SpriteCollisionListener _collSpriteListener;

	private ActionBind _follow;

	private Sprites _mapSprites;

	private Sprites _screenSprites;

	private LColor _color;

	private LColor _fontColor;

	private Vector2f _offset = new Vector2f();

	private IFont _font;

	public TileIsoRectGrid(int row, int col, float px, float py, float tw, float th) {
		this(row, col, px, py, tw, th, 0.5f);
	}

	public TileIsoRectGrid(int row, int col, float px, float py, float tw, float th, float angle) {
		this(row, col, px, py, tw, th, angle, false);
	}

	public TileIsoRectGrid(int row, int col, float px, float py, float tw, float th, float angle, boolean sin) {
		this._grids = new TileIsoRect[col][row];
		this._rows = row;
		this._cols = col;
		this._initAngle = angle;
		this._initWidth = tw;
		this._initHeight = th;
		this._sinUpdate = sin;
		this._scaleX = _scaleY = 1f;
		this._visible = true;
		this.setLocation(px, py);
		this.setAngle(angle, row, col, px, py, tw, th, sin);
	}

	public void draw(GLEx g) {
		draw(g, 0f, 0f);
	}

	public Vector2f toRollPosition(Vector2f pos) {
		pos.x = pos.x % ((getWidth()));
		pos.y = pos.y % ((getHeight()));
		if (pos.x < 0f) {
			pos.x += getWidth();
		}
		if (pos.x < 0f) {
			pos.y += getHeight();
		}
		return pos;
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!_visible || _closed) {
			return;
		}
		boolean update = (_objectRotation != 0) || !(_scaleX == 1f && _scaleY == 1f);
		int tmp = g.color();
		try {
			g.setAlpha(_objectAlpha);
			if (this._roll) {
				this._offset = toRollPosition(this._offset);
			}
			float newX = this._objectLocation.x + _offsetX + _offset.getX();
			float newY = this._objectLocation.y + _offsetY + _offset.getY();
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
			LSystem.error("TileIsoRectGrid error !", ex);
		} finally {
			if (update) {
				g.restoreTx();
			}
			g.setColor(tmp);
		}
	}

	public void draw(GLEx g, float offx, float offy) {
		if (!_visible || _closed) {
			return;
		}
		if (_background != null) {
			g.draw(_background, offx, offy);
		}
		final IFont font = g.getFont();
		if (_font != null) {
			g.setFont(_font);
		}
		final int color = g.color();
		final boolean saveColor = _color != null;
		if (saveColor) {
			g.setColor(_color);
		}
		final boolean updateTrans = (offx != 0f || offy != 0f);
		if (updateTrans) {
			g.translate(offx, offy);
		}
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				if (_showCoordinate) {
					_grids[x][y].draw(g, x + "," + y, _fontColor);
				} else {
					_grids[x][y].draw(g);
				}
			}
		}
		if (_drawListener != null) {
			_drawListener.draw(g, offx, offy);
		}
		if (updateTrans) {
			g.translate(-offx, -offy);
		}
		if (saveColor) {
			g.setColor(color);
		}
		g.setFont(font);
	}

	@Override
	public void update(long elapsedTime) {
		if (_event != null) {
			_event.update(this, Long.valueOf(elapsedTime));
		}
		if (_mapSprites != null) {
			_mapSprites.update(elapsedTime);
		}
		if (_drawListener != null) {
			_drawListener.update(elapsedTime);
		}
	}

	public DrawListener<TileIsoRectGrid> getListener() {
		return _drawListener;
	}

	public TileIsoRectGrid setListener(DrawListener<TileIsoRectGrid> l) {
		this._drawListener = l;
		return this;
	}

	public TileIsoRectGrid setAngle(float newAngle) {
		return setAngle(newAngle, 0f, 0f);
	}

	public TileIsoRectGrid setAngle(float px, float py) {
		return setAngle(_initAngle, px, py);
	}

	public TileIsoRectGrid setAngle(float newAngle, float px, float py) {
		return setAngle(newAngle, px, py, _sinUpdate);
	}

	public TileIsoRectGrid setAngle(float newAngle, float px, float py, boolean sin) {
		return setAngle(newAngle, _rows, _cols, px, py, _initWidth, _initHeight, sin);
	}

	public TileIsoRectGrid setAngle(float angle, int row, int col, float px, float py, float tw, float th,
			boolean sin) {
		final float newAngle = sin ? MathUtils.sin(angle) : angle;
		final float newX = newAngle * tw;
		final float newY = newAngle * th;
		float newHalfTileWidth = 0f;
		float newHalfTileHeight = 0f;
		if (newAngle < 1f) {
			newHalfTileWidth = tw * newAngle;
			newHalfTileHeight = th * newAngle;
		} else {
			newHalfTileWidth = tw / 2f + newX / 2f;
			newHalfTileHeight = th / 2f + newY / 2f;
		}
		for (int x = 0; x < col; x++) {
			for (int y = 0; y < row; y++) {
				float hx = px + (x + y) * newHalfTileWidth;
				float hy = 0;
				if (newAngle < 1f) {
					if (MathUtils.equal(newHalfTileWidth, newHalfTileHeight)) {
						hy = py + x * newHalfTileWidth - y * newHalfTileHeight + (th * row) * newAngle
								- newHalfTileHeight;
					} else if (newHalfTileWidth > newHalfTileHeight) {
						hy = py + x * newHalfTileWidth / 2f - y * newHalfTileHeight + (th * row) * newAngle
								- newHalfTileHeight;
					} else if (newHalfTileWidth < newHalfTileHeight) {
						hy = py + x * (newHalfTileHeight * newAngle + tw / 2f) - y * newHalfTileHeight
								+ (th * row) * newAngle - newHalfTileHeight;
					}
				} else {
					if (MathUtils.equal(newHalfTileWidth, newHalfTileHeight)) {
						hy = py + x * newHalfTileWidth - y * newHalfTileHeight + (th * row) / 2f + (newY * row) / 2f
								- newHalfTileHeight;
					} else if (newHalfTileWidth > newHalfTileHeight) {
						hy = py + x * newHalfTileWidth / 2f - y * newHalfTileHeight + (th * row) * newAngle
								- newHalfTileHeight;
					} else if (newHalfTileWidth < newHalfTileHeight) {
						hy = py + x * (newHalfTileWidth * newAngle + tw / 2f) - y * newHalfTileHeight + (th * row) / 2f
								+ (newY * row) / 2f - newHalfTileHeight;
					}
				}
				_grids[x][y] = TileIsoRect.createPos(hx, hy, tw, th, angle);
			}
		}
		this._tileWidth = MathUtils.iceil(newHalfTileWidth + tw / 2f);
		this._tileHeight = MathUtils.iceil(newHalfTileHeight + th / 2f);
		this._pixelInWidth = MathUtils.iceil(_rows * _tileWidth);
		this._pixelInHeight = MathUtils.iceil(_cols * _tileHeight);
		this._angle = angle;
		return this;
	}

	public int getPixelInWidth() {
		return this._pixelInWidth;
	}

	public int getPixelInHeight() {
		return this._pixelInHeight;
	}

	public TileIsoRect getTile(int x, int y) {
		if (x >= 0 && x < _rows && y >= 0 && y < _cols) {
			return _grids[y][x];
		}
		return null;
	}

	public TileIsoRect pixelToTile(float px, float py) {
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				TileIsoRect rect = _grids[x][y];
				if (rect != null && rect.isVisible() && rect.getRect().contains(px, py)) {
					return rect;
				}
			}
		}
		return null;
	}

	public TArray<TileIsoRect> findFlag(int flag) {
		final TArray<TileIsoRect> rects = new TArray<TileIsoRect>();
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				TileIsoRect rect = _grids[x][y];
				if (rect != null && rect.isVisible() && flag == rect.getFlag()) {
					rects.add(rect);
				}
			}
		}
		return rects;
	}

	public TArray<TileIsoRect> findTag(Object tag) {
		final TArray<TileIsoRect> rects = new TArray<TileIsoRect>();
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				TileIsoRect rect = _grids[x][y];
				if (rect != null && rect.isVisible() && (tag == rect.getTag() || tag.equals(rect.getTag()))) {
					rects.add(rect);
				}
			}
		}
		return rects;
	}

	public boolean contains(float px, float py) {
		return pixelToTile(px, py) != null;
	}

	public float getAngle() {
		return _angle;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	public TileIsoRectGrid setEvent(EventActionTN<TileIsoRectGrid, Long> e) {
		_event = e;
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

	public TileIsoRectGrid followActionObject() {
		if (_follow != null) {
			float offsetX = limitOffsetX(_follow.getX());
			float offsetY = limitOffsetY(_follow.getY());
			if (offsetX != 0 || offsetY != 0) {
				setOffset(offsetX, offsetY);
			}
		}
		return this;
	}

	public float centerX() {
		return (getContainerWidth() - getWidth()) / 2f;
	}

	public float centerY() {
		return (getContainerHeight() - getHeight()) / 2f;
	}

	public TileIsoRectGrid scrollDown(float distance) {
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

	public TileIsoRectGrid scrollLeft(float distance) {
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

	public TileIsoRectGrid scrollRight(float distance) {
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

	public TileIsoRectGrid scrollUp(float distance) {
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

	public TileIsoRectGrid scrollLeftUp(float distance) {
		this.scrollUp(distance);
		this.scrollLeft(distance);
		return this;
	}

	public TileIsoRectGrid scrollRightDown(float distance) {
		this.scrollDown(distance);
		this.scrollRight(distance);
		return this;
	}

	public TileIsoRectGrid scrollClear() {
		if (!this._offset.equals(0f, 0f)) {
			this._offset.set(0, 0);
		}
		return this;
	}

	public TileIsoRectGrid scroll(float x, float y) {
		return scroll(x, y, 4f);
	}

	public TileIsoRectGrid scroll(float x, float y, float distance) {
		if (_scrollDrag.x == 0f && _scrollDrag.y == 0f) {
			_scrollDrag.set(x, y);
			return this;
		}
		return scroll(_scrollDrag.x, _scrollDrag.y, x, y, distance);
	}

	public TileIsoRectGrid scroll(float x1, float y1, float x2, float y2) {
		return scroll(x1, y1, x2, y2, 4f);
	}

	public TileIsoRectGrid scroll(float x1, float y1, float x2, float y2, float distance) {
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

	public TileIsoRectGrid addMapSprite(ISprite sprite) {
		_mapSprites.add(sprite);
		return this;
	}

	public TileIsoRectGrid addMapSpriteAt(ISprite sprite, float x, float y) {
		_mapSprites.addAt(sprite, x, y);
		return this;
	}

	public TileIsoRectGrid removeMapSprite(int idx) {
		_mapSprites.remove(idx);
		return this;
	}

	public TileIsoRectGrid removeMapSprite(ISprite sprite) {
		_mapSprites.remove(sprite);
		return this;
	}

	public TileIsoRectGrid removeMapSprite(int start, int end) {
		_mapSprites.remove(start, end);
		return this;
	}

	public Vector2f pixelsToTiles(float x, float y) {
		float xprime = x / getTileWidth() - 1;
		float yprime = y / getTileHeight() - 1;
		return new Vector2f(xprime, yprime);
	}

	public PointI tilePixels(float x, float y) {
		int newX = getPixelX(x);
		int newY = getPixelY(y);
		return new PointI(newX, newY);
	}

	public TileIsoRectGrid centerOffset() {
		this._offset.set(centerX(), centerY());
		return this;
	}

	public TileIsoRectGrid setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	@Override
	public TileIsoRectGrid setOffset(Vector2f _offset) {
		this._offset.set(_offset);
		return this;
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public LColor getFontColor() {
		return _fontColor;
	}

	public TileIsoRectGrid setFontColor(LColor c) {
		this._fontColor = c;
		return this;
	}

	public IFont getFont() {
		return _font;
	}

	public TileIsoRectGrid setFont(IFont f) {
		this._font = f;
		return this;
	}

	public TileIsoRectGrid setFill(boolean f) {
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				_grids[x][y].setFill(f);
			}
		}
		return this;
	}

	public boolean isShowCoordinate() {
		return _showCoordinate;
	}

	public TileIsoRectGrid setShowCoordinate(boolean c) {
		this._showCoordinate = c;
		return this;
	}

	@Override
	public LColor getColor() {
		return new LColor(_color);
	}

	@Override
	public void setColor(LColor c) {
		_color = c;
	}

	@Override
	public Field2D getField2D() {
		if (_field2d == null) {
			int[][] maps = new int[_cols][_rows];
			for (int x = 0; x < _cols; x++) {
				for (int y = 0; y < _rows; y++) {
					TileIsoRect rect = _grids[x][y];
					if (rect != null) {
						if (!rect.isVisible()) {
							maps[x][y] = -1;
						} else {
							maps[x][y] = rect.getId();
						}
					}
				}
			}
			_field2d = new Field2D(maps, _tileWidth, _tileHeight);
			_field2d.setLimit(-1);
		} else {
			for (int x = 0; x < _rows; x++) {
				for (int y = 0; y < _cols; y++) {
					TileIsoRect rect = _grids[y][x];
					if (rect != null) {
						if (!rect.isVisible()) {
							_field2d.setTileType(x, y, -1);
						} else {
							_field2d.setTileType(x, y, rect.getId());
						}
					}
				}
			}
		}
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

	@Override
	public void setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
	}

	@Override
	public TileIsoRectGrid setSize(float w, float h) {
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

	public TileIsoRectGrid setFollow(ActionBind _follow) {
		this._follow = _follow;
		return this;
	}

	public TileIsoRectGrid followDonot() {
		return setFollow(null);
	}

	public TileIsoRectGrid followAction(ActionBind _follow) {
		return setFollow(_follow);
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
		return ((((x >= 0) && (x < getWidth())) && (y >= 0)) && (y < getHeight()));
	}

	public boolean isRoll() {
		return _roll;
	}

	public TileIsoRectGrid setRoll(boolean roll) {
		this._roll = roll;
		return this;
	}

	public LTexture getBackground() {
		return this._background;
	}

	public TileIsoRectGrid setBackground(LTexture bg) {
		this._background = bg;
		return this;
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

	public TileIsoRectGrid setMapSprites(Sprites s) {
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
	public TileIsoRectGrid triggerCollision(SpriteCollisionListener sc) {
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

	public ResizeListener<TileIsoRectGrid> getResizeListener() {
		return _resizeListener;
	}

	public TileIsoRectGrid setResizeListener(ResizeListener<TileIsoRectGrid> listener) {
		this._resizeListener = listener;
		return this;
	}

	public TileIsoRectGrid setOffsetX(float sx) {
		this._offset.setX(sx);
		return this;
	}

	public TileIsoRectGrid setOffsetY(float sy) {
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

	public int getTileWidth() {
		return _tileWidth;
	}

	public int getTileHeight() {
		return _tileHeight;
	}

	@Override
	public float getWidth() {
		return (_pixelInWidth * _scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return (_pixelInHeight * _scaleY) - _fixedHeightOffset;
	}

	public int getRow() {
		return _rows;
	}

	public int getCol() {
		return _cols;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x() + _offset.x, y() + _offset.y, _pixelInWidth, _pixelInHeight);
	}

	@Override
	public LTexture getBitmap() {
		return _background;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		_visible = false;
		_roll = false;
		for (int x = 0; x < _cols; x++) {
			for (int y = 0; y < _rows; y++) {
				_grids[x][y].close();
			}
		}
		_closed = true;
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
