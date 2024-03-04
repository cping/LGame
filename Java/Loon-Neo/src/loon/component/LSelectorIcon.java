/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.component;

import java.util.Iterator;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.ActionBind;
import loon.action.collision.CollisionHelper;
import loon.action.collision.CollisionObject;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMapConfig;
import loon.action.sprite.SpriteBatch;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.events.CallFunction;
import loon.events.SysKey;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.opengl.GLEx;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 可用区域绘制类，可以绘制战棋之类游戏可移动与不可移动区域，也可以单纯让选中位置闪啊闪的UI
 */
public class LSelectorIcon extends LComponent {

	private CallFunction _function;

	private int tileWidth;
	private int tileHeight;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private int step;

	private int minAlpha;

	private int maxAlpha;

	private float _iconAlpha;
	private float _iconAlphaRate;

	private boolean _increaseAlpha;
	private boolean _flashAlpha;
	private boolean _drawBorder;
	private boolean _drawImageNotColored;

	private LColor _borderColor;

	private int[][] _gridLayout;
	private int _gridWidth;
	private int _gridHeight;

	private float _gridSpaceSize;
	private float _gridCenterX;
	private float _gridCenterY;

	private final IntMap<LColor> _colorCaches;

	private final IntMap<LTexture> _imageCaches;

	private final LColor _tempColor = new LColor();

	public LSelectorIcon(float x, float y, int size) {
		this((int) x, (int) y, size);
	}

	public LSelectorIcon(int x, int y, int size) {
		this(x, y, size, size, LColor.white);
	}

	public LSelectorIcon(int x, int y, int tw, int th) {
		this(x, y, tw, th, LColor.white);
	}

	public LSelectorIcon(int x, int y, int size, LColor color) {
		this(x, y, size, size, color.lighter(), color);
	}

	public LSelectorIcon(int x, int y, int tw, int th, LColor color) {
		this(x, y, tw, th, color.lighter(), color);
	}

	public LSelectorIcon(int x, int y, int tw, int th, LColor b, LColor c) {
		super(x, y, tw, th);
		this._colorCaches = new IntMap<LColor>();
		this._imageCaches = new IntMap<LTexture>();
		this._objectLocation.x = x / tw;
		this._objectLocation.y = y / tw;
		this.minX = 0;
		this.minY = 0;
		this.maxX = getScreenWidth() / tw;
		this.maxY = getScreenHeight() / th;
		this.step = 1;
		this.minAlpha = 35;
		this.maxAlpha = 235;
		this.tileWidth = tw;
		this.tileHeight = th;
		this._iconAlphaRate = 1f;
		this._gridCenterX = -1f;
		this._gridCenterY = -1f;
		this._borderColor = b;
		this._component_baseColor = c;
		this._drawBorder = true;
		this._flashAlpha = true;
		this.setElastic(false);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (_objectLocation != null && tileWidth != 0 && tileHeight != 0) {
			this._objectLocation.x = x / tileWidth;
			this._objectLocation.y = y / tileHeight;
			this.moveSide();
		}
	}

	public void draw(Canvas g, float tx, float ty, int offsetX, int offsetY) {
		final float newX = offsetX + (tx * tileWidth);
		final float newY = offsetY + (ty * tileHeight);
		final int fill = g.getFillColor();
		final int stroke = g.getStrokeColor();
		g.setColor(_component_baseColor.getRed(), _component_baseColor.getGreen(), _component_baseColor.getBlue(),
				MathUtils.ifloor(MathUtils.limit((maxAlpha - _iconAlpha), minAlpha, maxAlpha)));
		g.fillRect(fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth), fixGridSize(tileHeight));
		if (_drawBorder) {
			g.setColor(_borderColor.getRed(), _borderColor.getGreen(), _borderColor.getBlue(),
					MathUtils.ifloor(_iconAlpha));
			g.strokeRect(fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth) - 1, fixGridSize(tileHeight) - 1);
			g.strokeRect(fixGridPos(newX) - 1, fixGridPos(newY) - 1, fixGridSize(tileWidth) + 1,
					fixGridSize(tileHeight) + 1);
			g.setFillColor(fill);
		}
		g.setStrokeColor(stroke);
	}

	public void draw(SpriteBatch batch, float tx, float ty, int offsetX, int offsetY) {
		final float newX = offsetX + (tx * tileWidth);
		final float newY = offsetY + (ty * tileHeight);
		final float color = batch.getFloatColor();
		batch.setColor(_component_baseColor.getRed(), _component_baseColor.getGreen(), _component_baseColor.getBlue(),
				MathUtils.ifloor(MathUtils.limit((maxAlpha - _iconAlpha), minAlpha, maxAlpha)));
		batch.fillRect(fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth), fixGridSize(tileHeight));
		if (_drawBorder) {
			batch.setColor(_borderColor.getRed(), _borderColor.getGreen(), _borderColor.getBlue(),
					MathUtils.ifloor(_iconAlpha));
			batch.drawRect(fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth) - 1, fixGridSize(tileHeight) - 1);
			batch.drawRect(fixGridPos(newX) - 1, fixGridPos(newY) - 1, fixGridSize(tileWidth) + 1,
					fixGridSize(tileHeight) + 1);
		}
		batch.setColor(color);
	}

	public void draw(GLEx g, LTexture tex, float tx, float ty, float offsetX, float offsetY, int baseColor,
			int borderColor) {
		final float newX = MathUtils.ifloor(offsetX + (tx * tileWidth));
		final float newY = MathUtils.ifloor(offsetY + (ty * tileHeight));
		final int color = g.color();
		final boolean drawImage = (tex != null);
		if (_drawImageNotColored && drawImage) {
			g.draw(tex, fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth), fixGridSize(tileHeight));
		} else {
			g.fillRect(fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth), fixGridSize(tileHeight), baseColor);
			if (drawImage) {
				g.draw(tex, fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth), fixGridSize(tileHeight),
						_tempColor.setColor(baseColor));
			}
		}
		if (_drawBorder) {
			g.drawRect(fixGridPos(newX), fixGridPos(newY), fixGridSize(tileWidth) - 1, fixGridSize(tileHeight) - 1,
					borderColor);
			g.drawRect(fixGridPos(newX) - 1, fixGridPos(newY) - 1, fixGridSize(tileWidth) + 1,
					fixGridSize(tileHeight) + 1, borderColor);
		}
		g.setColor(color);
	}

	public LSelectorIcon setGridPos(int tx, int ty) {
		_objectLocation.set(tx, ty);
		moveSide();
		return this;
	}

	private float fixGridPos(float v) {
		return v + _gridSpaceSize;
	}

	private float fixGridSize(float v) {
		return v - _gridSpaceSize * 2f;
	}

	private void moveSide() {
		if (_gridLayout == null) {
			if (_objectLocation.x < minX) {
				_objectLocation.x = minX;
			}
			if (_objectLocation.x > maxX - 1) {
				_objectLocation.x = maxX - 1;
			}
			if (_objectLocation.y < minY) {
				_objectLocation.y = minY;
			}
			if (_objectLocation.y > maxY - 1) {
				_objectLocation.y = maxY - 1;
			}
		}
	}

	public LSelectorIcon move(int direction) {
		_objectLocation.move_multiples(direction, step);
		moveSide();
		return this;
	}

	public LSelectorIcon setX(int x) {
		_objectLocation.setX(x);
		return this;
	}

	public LSelectorIcon setY(int y) {
		_objectLocation.setY(y);
		return this;
	}

	public LSelectorIcon _objectLocation(int x, int y) {
		_objectLocation.set(x, y);
		return this;
	}

	public LSelectorIcon setMoveLimit(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.moveSide();
		return this;
	}

	public LSelectorIcon bindColor(int flag, LColor color) {
		_colorCaches.put(flag, color);
		return this;
	}

	public LSelectorIcon bindImage(int flag, String path) {
		return bindImage(flag, LTextures.loadTexture(path));
	}

	public LSelectorIcon bindImage(int flag, LTexture tex) {
		_imageCaches.put(flag, tex);
		return this;
	}

	public LSelectorIcon clearColors() {
		_colorCaches.clear();
		return this;
	}

	public LSelectorIcon clearImages() {
		_imageCaches.clear();
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (_flashAlpha) {
			if (_increaseAlpha) {
				if (_iconAlpha + _iconAlphaRate <= maxAlpha)
					_iconAlpha += _iconAlphaRate;
				else {
					_iconAlpha = maxAlpha;
					_increaseAlpha = false;
				}
			} else {
				if (_iconAlpha - _iconAlphaRate >= minAlpha)
					_iconAlpha -= _iconAlphaRate;
				else {
					_iconAlpha = minAlpha;
					_increaseAlpha = true;
				}
			}
		} else {
			_iconAlpha = (maxAlpha - minAlpha) / 2f;
		}
	}

	public int getMinX() {
		return minX;
	}

	public LSelectorIcon setMinX(int minX) {
		this.minX = minX;
		return this;
	}

	public int getMinY() {
		return minY;
	}

	public LSelectorIcon setMinY(int minY) {
		this.minY = minY;
		return this;
	}

	public int getMaxX() {
		return maxX;
	}

	public LSelectorIcon setMaxX(int maxX) {
		this.maxX = maxX;
		return this;
	}

	public int getMaxY() {
		return maxY;
	}

	public LSelectorIcon setGridLayout(String chars) {
		if (chars == null) {
			return this;
		}
		return setGridLayout(StringUtils.split(chars, LSystem.LF));
	}

	public LSelectorIcon setGridLayout(String... chars) {
		if (chars == null) {
			return this;
		}
		return setGridLayout(TileMapConfig.loadStringMap(LSystem.SPACE, chars));
	}

	public LSelectorIcon setGridLayout(Field2D map) {
		if (map == null) {
			return this;
		}
		return setGridLayout(map.getMap());
	}

	public LSelectorIcon setGridLayout(int[][] grids) {
		if (grids == null) {
			return this;
		}
		_gridLayout = grids;
		_gridWidth = _gridLayout[0].length;
		_gridHeight = _gridLayout.length;
		return this;
	}

	public int getGridWidth() {
		return _gridWidth;
	}

	public int getGridHeight() {
		return _gridHeight;
	}

	public int[][] getGridLayout() {
		return _gridLayout;
	}

	public LSelectorIcon setMaxY(int maxY) {
		this.maxY = maxY;
		return this;
	}

	public float getAlphaRate() {
		return _iconAlphaRate;
	}

	public LSelectorIcon setAlphaRate(float a) {
		this._iconAlphaRate = a;
		return this;
	}

	public boolean isIncreaseAlpha() {
		return _increaseAlpha;
	}

	public LSelectorIcon setIncreaseAlpha(boolean increaseAlpha) {
		this._increaseAlpha = increaseAlpha;
		return this;
	}

	public LColor getBackgroundColor() {
		return _component_baseColor.cpy();
	}

	public LSelectorIcon setBackgroundColor(LColor c) {
		this._component_baseColor = c;
		return this;
	}

	public LColor getBorderColor() {
		return _borderColor.cpy();
	}

	public LSelectorIcon setBorderColor(LColor b) {
		this._borderColor = b;
		return this;
	}

	@Override
	public void processTouchReleased() {
		super.processTouchReleased();
		if (_function != null) {
			_function.call(this);
		}
	}

	@Override
	public void processKeyReleased() {
		if (input != null) {
			int key = input.getKeyReleased();
			switch (key) {
			case SysKey.UP:
				move(Config.TUP);
				break;
			case SysKey.DOWN:
				move(Config.TDOWN);
				break;
			case SysKey.LEFT:
				move(Config.TLEFT);
				break;
			case SysKey.RIGHT:
				move(Config.TRIGHT);
				break;
			}
		}
		if (_function != null) {
			_function.call(this);
		}
	}

	public CallFunction getFunction() {
		return _function;
	}

	public LSelectorIcon setFunction(CallFunction function) {
		this._function = function;
		return this;
	}

	public boolean contains(RectBox rect) {
		return contains(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public boolean contains(float x, float y, float w, float h) {
		return intersects(x, y, w, h);
	}

	public boolean intersects(RectBox rect) {
		return intersects(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public boolean intersects(float x, float y, float w, float h) {
		if (_gridLayout == null) {
			return super.contains(x, y, w, h);
		}
		final float drawCenterX = _gridCenterX == -1 ? ((_gridWidth / 2 + 0.5f) * tileWidth) : _gridCenterX;
		final float drawCenterY = _gridCenterY == -1 ? ((_gridHeight / 2 + 0.5f) * tileHeight) : _gridCenterY;
		float newX = 0f;
		float newY = 0f;
		if (_objectSuper != null) {
			newX += this._objectSuper.getScreenX();
			newY += this._objectSuper.getScreenY();
		}
		newX += _objectLocation.x * tileWidth - drawCenterX;
		newY += _objectLocation.y * tileHeight - drawCenterY;
		for (int i = 0; i < _gridWidth; i++) {
			for (int j = 0; j < _gridHeight; j++) {
				int flag = _gridLayout[j][i];
				if (flag != LSystem.SPACE) {
					boolean result = CollisionHelper.intersects(newX + i * tileWidth, newY + j * tileHeight, tileWidth,
							tileHeight, x, y, w, h);
					if (result) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean intersects(CollisionObject obj) {
		return intersects(obj.getRectBox());
	}

	@Override
	public boolean intersects(Shape rect) {
		return intersects(rect.getRect());
	}

	@Override
	public boolean contains(CollisionObject o) {
		return contains(o.getRectBox());
	}

	@Override
	public boolean collided(Shape rect) {
		return contains(rect);
	}

	@Override
	public boolean intersects(LComponent comp) {
		return (this._component_visible) && (comp != null && comp.isVisible()) && intersects(comp.getCollisionBox());
	}

	@Override
	public boolean contains(LComponent comp) {
		return (this._component_visible) && (comp != null && comp.isVisible()) && contains(comp.getCollisionBox());
	}

	public TArray<RectBox> getGrids() {
		TArray<RectBox> rects = new TArray<RectBox>();
		for (int i = 0; i < _gridWidth; i++) {
			for (int j = 0; j < _gridHeight; j++) {
				int flag = _gridLayout[j][i];
				if (flag != LSystem.SPACE) {
					RectBox rect = RectBox.at(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
					rects.add(rect);
				}
			}
		}
		return rects;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		if (_component_isClose) {
			return;
		}
		if (!_component_visible) {
			return;
		}
		if (_objectSuper != null) {
			x = this._objectSuper.getScreenX();
			y = this._objectSuper.getScreenY();
		} else {
			x = 0;
			y = 0;
		}
		drawGrid(g, x, y);
	}

	public LSelectorIcon moveTo(float x, float y) {
		return moveTo(x, y, tileWidth, tileHeight);
	}

	public LSelectorIcon moveTo(float x, float y, float w, float h) {
		float newX = 0f;
		float newY = 0f;
		if (_gridLayout == null) {
			newX = MathUtils.ifloor(x - (w - tileWidth) / 2f);
			newY = MathUtils.ifloor(y - (h - tileHeight) / 2f);
		} else {
			newX = MathUtils.ifloor(x + w / 2f);
			newY = MathUtils.ifloor(y + h / 2f);
		}
		setLocation(newX, newY);
		return this;
	}

	public LSelectorIcon moveTo(ActionBind bind) {
		if (bind == null) {
			return this;
		}
		if (bind == this) {
			return this;
		}
		return moveTo(bind.getX(), bind.getY(), bind.getWidth(), bind.getHeight());
	}

	public void drawGrid(GLEx g, int x, int y) {
		final float oldAlpha = g.alpha();
		int newAlpha = MathUtils.ifloor(_iconAlpha);
		final int borderColor = _drawBorder
				? LColor.getARGB(_borderColor.getRed(), _borderColor.getGreen(), _borderColor.getBlue(), newAlpha)
				: LColor.TRANSPARENT;
		if (_flashAlpha) {
			newAlpha = MathUtils.ifloor(MathUtils.limit((maxAlpha - _iconAlpha), minAlpha, maxAlpha));
		}
		final int baseColor = LColor.getARGB(_component_baseColor.getRed(), _component_baseColor.getGreen(),
				_component_baseColor.getBlue(), newAlpha);
		if (_gridLayout != null) {
			final float offsetX = x + getOffsetX();
			final float offsetY = y + getOffsetY();
			final float drawCenterX = _gridCenterX == -1 ? ((_gridWidth / 2 + 0.5f) * tileWidth) : _gridCenterX;
			final float drawCenterY = _gridCenterY == -1 ? ((_gridHeight / 2 + 0.5f) * tileHeight) : _gridCenterY;
			final int drawPosX = MathUtils.ifloor(offsetX - drawCenterX);
			final int drawPosY = MathUtils.ifloor(offsetY - drawCenterY);
			final IntMap<LTexture> listImages = this._imageCaches;
			final IntMap<LColor> listColors = this._colorCaches;
			for (int i = 0; i < _gridWidth; i++) {
				for (int j = 0; j < _gridHeight; j++) {
					int flag = _gridLayout[j][i];
					if (flag != LSystem.SPACE) {
						final LColor currentColor = listColors.get(flag);
						int newColor = baseColor;
						if (currentColor != null) {
							newColor = LColor.getARGB(currentColor.getRed(), currentColor.getGreen(),
									currentColor.getBlue(), newAlpha);
						}
						LTexture tex = listImages.get(flag);
						if (tex == null) {
							tex = _background;
						}
						draw(g, tex, _objectLocation.x + i, _objectLocation.y + j, drawPosX, drawPosY, newColor,
								borderColor);
					}
				}
			}
		} else {
			draw(g, _background, _objectLocation.x, _objectLocation.y, MathUtils.ifloor(x + getOffsetX()),
					MathUtils.ifloor(y + getOffsetY()), baseColor, borderColor);
		}
		g.setAlpha(oldAlpha);
	}

	public LSelectorIcon setTexture(String path) {
		return setTexture(LTextures.loadTexture(path));
	}

	public LSelectorIcon setTexture(LTexture tex) {
		onlyBackground(tex);
		return this;
	}

	public boolean isFlashAlpha() {
		return _flashAlpha;
	}

	public LSelectorIcon setFlashAlpha(boolean f) {
		this._flashAlpha = f;
		return this;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getStep() {
		return step;
	}

	public LSelectorIcon setStep(int s) {
		this.step = s;
		return this;
	}

	public int getMinAlpha() {
		return minAlpha;
	}

	public LSelectorIcon setMinAlpha(int minAlpha) {
		this.minAlpha = minAlpha;
		return this;
	}

	public int getMaxAlpha() {
		return maxAlpha;
	}

	public LSelectorIcon setMaxAlpha(int maxAlpha) {
		this.maxAlpha = maxAlpha;
		return this;
	}

	public boolean isDrawBorder() {
		return _drawBorder;
	}

	public LSelectorIcon setDrawBorder(boolean d) {
		this._drawBorder = d;
		return this;
	}

	public float getGridCenterX() {
		return _gridCenterX;
	}

	public LSelectorIcon setGridCenterX(float cx) {
		this._gridCenterX = cx;
		return this;
	}

	public float getGridCenterY() {
		return _gridCenterY;
	}

	public LSelectorIcon setGridCenterY(float cy) {
		this._gridCenterY = cy;
		return this;
	}

	public boolean isDrawImageNotColored() {
		return _drawImageNotColored;
	}

	public LSelectorIcon setDrawImageNotColored(boolean d) {
		this._drawImageNotColored = d;
		return this;
	}

	public float getGridSpaceSize() {
		return _gridSpaceSize;
	}

	public LSelectorIcon setGridSpaceSize(float s) {
		this._gridSpaceSize = s;
		return this;
	}

	@Override
	public String getUIName() {
		return "SelectorIcon";
	}

	@Override
	public void destory() {
		_colorCaches.clear();
		for (Iterator<LTexture> it = _imageCaches.iterator(); it.hasNext();) {
			LTexture tex = it.next();
			if (tex != null) {
				tex.close();
			}
		}
		_imageCaches.clear();
	}

}
