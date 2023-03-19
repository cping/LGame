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
package loon.component;

import loon.LTexture;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 用于显示敌人所在位置的雷达效果组件,通常和地图相关游戏混合使用
 */
public class LRadar extends LComponent {

	public static enum Mode {
		Circle, Quad, Hexagon, Octagon, Decagon
	}

	private static class Drop {

		protected float x;
		protected float y;
		protected float radius;
		protected float oldradius;
		protected LColor color;
		protected float alpha = 255;

		public Drop(float x, float y, float radius, LColor color) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.oldradius = radius;
			this.color = color;
		}

		public int newAlpha() {
			return LColor.alpha(color.getARGB(), alpha / 255f);
		}

	}

	private Mode _drawMode;

	private LColor _circleColor;

	private LColor _sweepColor;

	private LColor _dropColor;

	private LTexture _groundTexture;

	private final int _circleCount;

	private final int _raindropCount;

	private boolean showCross;

	private boolean showDrop;

	private boolean showScanning;

	private boolean showBoard;

	private boolean showRandDrop;

	private boolean showDash;

	private boolean showGround;

	private boolean _initial;

	private int _dropLimits;

	private float _radius;

	private float _speed;

	private float _flicker;

	private float _lineWidth;

	private final TArray<Drop> _randomDrops;

	private final TArray<Drop> _drops;

	private float _degrees;

	public LRadar(int x, int y) {
		this(x, y, 120, 120);
	}

	public LRadar(Mode mode, int x, int y) {
		this(mode, x, y, 120, 120);
	}

	public LRadar(int x, int y, int width, int height) {
		this(Mode.Circle, x, y, width, height);
	}

	public LRadar(Mode mode, int x, int y, int width, int height) {
		this(mode, LColor.red, LColor.blue, LColor.red, true, true, true, true, false, true, 4, 4, 3f, 3f, 15, 3f, x, y,
				width, height);
	}

	public LRadar(Mode mode, LColor circleColor, LColor dropColor, LColor sweepColor, int x, int y, int width,
			int height) {
		this(mode, circleColor, dropColor, sweepColor, true, true, true, true, false, true, 4, 4, 3f, 3f, 15, 3f, x, y,
				width, height);
	}

	public LRadar(Mode mode, boolean scan, boolean cross, boolean drop, boolean board, boolean rand, boolean ground,
			int circleCount, int x, int y, int width, int height) {
		this(mode, LColor.red, LColor.blue, LColor.red, scan, cross, drop, board, rand, ground, 4, 4, 3f, 3f, 15, 3f, x,
				y, width, height);
	}

	public LRadar(Mode mode, LColor circleColor, LColor dropColor, LColor sweepColor, boolean scan, boolean cross,
			boolean drop, boolean board, boolean rand, boolean ground, int circleCount, int raindropCount, float speed,
			float flicker, int dropSize, float lineWidth, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.setCircleColor(circleColor);
		this.setDropColor(dropColor);
		this.setSweepColor(sweepColor);
		if (mode != null) {
			this._drawMode = mode;
		} else {
			this._drawMode = Mode.Circle;
		}
		this._drops = new TArray<LRadar.Drop>();
		this._randomDrops = new TArray<Drop>();
		this._circleCount = MathUtils.max(1, circleCount);
		this._raindropCount = MathUtils.max(1, raindropCount);
		this._speed = speed;
		this._flicker = flicker;
		this._dropLimits = dropSize;
		this._lineWidth = lineWidth;
		if (width <= 120 && height <= 120) {
			this._lineWidth = 1f;
		}
		this.showScanning = scan;
		this.showCross = cross;
		this.showDrop = drop;
		this.showBoard = board;
		this.showRandDrop = rand;
		this.showGround = ground;
		this._initial = true;
	}

	/**
	 * 转化Field2D数组地图中的指定索引到雷达组件中显示
	 * 
	 * @param map
	 * @param color
	 * @param radius
	 * @param flags
	 * @return
	 */
	public LRadar addField2DToDrop(Field2D map, LColor color, float radius, int... flags) {
		if (map == null) {
			return this;
		}
		final float radarWidth = getWidth();
		final float radarHeight = getHeight();
		final float mapWidth = map.getDrawWidth();
		final float mapHeight = map.getDrawHeight();
		final float widthScale = radarWidth / mapWidth;
		final float heightScale = radarHeight / mapHeight;
		for (int i = 0; i < map.getWidth(); i++) {
			for (int j = 0; j < map.getHeight(); j++) {
				int flag = map.getTileType(i, j);
				for (int v : flags) {
					if (v == flag) {
						final float newX = (map.tilesToWidthPixels(i) * widthScale)
								+ (map.getTileHalfWidth() * widthScale);
						final float newY = (map.tilesToHeightPixels(j) * heightScale)
								+ (map.getTileHalfHeight() * heightScale);
						addDrop(newX, newY, radius, color);
					}
				}
			}
		}
		return this;
	}

	public LRadar addActionBindToDrop(TArray<ActionBind> actions, int screenWidth, int screenHeight, int tileWidth,
			int tileHeight, LColor color, float radius) {
		if (actions == null) {
			return this;
		}
		final float radarWidth = getWidth();
		final float radarHeight = getHeight();
		final float mapWidth = screenWidth;
		final float mapHeight = screenHeight;
		final float widthScale = radarWidth / mapWidth;
		final float heightScale = radarHeight / mapHeight;

		for (ActionBind act : actions) {
			if (act != null && act.isVisible() && act.getAlpha() > 0f) {
				final float newX = (MathUtils.floor(act.getX() / tileWidth) * tileWidth * widthScale)
						+ (tileWidth / 2 * widthScale);
				final float newY = (MathUtils.floor(act.getY() / tileHeight) * tileHeight * heightScale)
						+ (tileHeight / 2 * heightScale);
				addDrop(newX, newY, radius, color);
			}
		}

		return this;
	}

	public LRadar updateField2DToDrop(Field2D map, LColor color, int... flags) {
		clearDrop();
		return addField2DToDrop(map, color, 0, flags);
	}

	public LRadar addField2DToDrop(Field2D map, LColor color, int... flags) {
		return addField2DToDrop(map, color, 0, flags);
	}

	public LRadar updateActionBindToDrop(TArray<ActionBind> actions, int screenWidth, int screenHeight, int tileWidth,
			int tileHeight, LColor color) {
		clearDrop();
		return addActionBindToDrop(actions, screenWidth, screenHeight, tileWidth, tileHeight, color, 0f);
	}

	public LRadar addActionBindToDrop(TArray<ActionBind> actions, int screenWidth, int screenHeight, int tileWidth,
			int tileHeight, LColor color) {
		return addActionBindToDrop(actions, screenWidth, screenHeight, tileWidth, tileHeight, color, 0f);
	}

	public LRadar addDrop(float x, float y, LColor color) {
		return addDrop(x, y, 0f, color);
	}

	public LRadar addDrop(float x, float y, float radius, LColor color) {
		_drops.add(new Drop(x, y, radius, color));
		return this;
	}

	public LRadar clearDrop() {
		_drops.clear();
		return this;
	}

	public LRadar removeDrop(float x, float y) {
		for (int i = _drops.size - 1; i > -1; i--) {
			Drop d = _drops.get(i);
			if (d != null) {
				if (d.x == x && d.y == y) {
					_drops.remove(d);
				}
			}
		}
		return this;
	}

	public LColor getSweepColor() {
		return this._sweepColor;
	}

	public LColor getDropColor() {
		return this._dropColor;
	}

	public LColor getCircleColor() {
		return this._circleColor;
	}

	public LRadar setSweepColor(LColor color) {
		this._sweepColor = new LColor(color);
		return this;
	}

	public LRadar setDropColor(LColor color) {
		this._dropColor = new LColor(color);
		return this;
	}

	public LRadar setCircleColor(LColor color) {
		this._circleColor = new LColor(color);
		return this;
	}

	private void checkRadius() {
		float width = getWidth();
		float height = getHeight();
		this._radius = MathUtils.abs(MathUtils.min(width, height) - 6);
	}

	@Override
	public LComponent validateResize() {
		super.validateResize();
		if (_initial) {
			createGround();
		}
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (_component_isClose) {
			return;
		}

		checkRadius();
		createGround();

		if (showGround && _groundTexture != null) {
			g.draw(_groundTexture, x, y, getWidth(), getHeight());
		}

		float cx = x + (getWidth() / 2);
		float cy = y + (getHeight() / 2);

		float lineWidth = g.getLineWidth();
		g.setLineWidth(_lineWidth);
		if (showBoard) {
			g.drawRect(x, y, getWidth(), getHeight(), _circleColor);
		}
		drawCircle(g, cx, cy, _radius);

		if (showCross) {
			drawCross(g, cx, cy, _radius / 2);
		}

		if (showScanning) {
			if (showDrop) {
				drawDrop(g, cx, cy, _radius);
			}
			drawSweep(g, cx, cy, _radius);
			_degrees = (_degrees + (360f / _speed / 60f)) % 360;

		}
		g.setLineWidth(lineWidth);
	}

	private void drawSweep(GLEx g, float cx, float cy, float radius) {
		int color = g.color();
		float newRadius = radius - _lineWidth;
		float alpha = _sweepColor.a;
		_sweepColor.a = 0.5f;
		g.setColor(_sweepColor);
		g.fillArc(cx - newRadius / 2f, cy - newRadius / 2f, newRadius, newRadius, _degrees, 45);
		g.setColor(color);
		_sweepColor.a = alpha;
	}

	private void createGround() {
		if (showGround && _groundTexture == null) {
			int rw = width();
			int rh = height();
			Image img = Image.createImage(rw, rh);
			Canvas canvas = img.getCanvas();
			canvas.setColor(LColor.green.darker().setAlpha(0.8f));
			if (showBoard) {
				canvas.fillRect(0, 0, rw, rh);
				canvas.setColor(LColor.darkGray);
				int line = _circleCount * 2;
				int tw = rw / line;
				int th = rh / line;
				for (int i = 0; i < line; i++) {
					canvas.drawLine(i * tw, 0, i * tw, rh);
				}
				for (int j = 0; j < line; j++) {
					canvas.drawLine(0, j * th, rw, j * th);
				}
			} else {
				canvas.fillCircle(_radius / 2 + _lineWidth, _radius / 2 + _lineWidth, _radius / 2);
			}
			canvas.close();
			_groundTexture = img.texture();
		}
	}

	private void createRandDot(float cx, float cy, float radius) {
		if (_randomDrops.size() < _raindropCount) {
			boolean probability = (int) (MathUtils.random() * _dropLimits) == 0;
			if (probability) {
				float x = 0;
				float y = 0;
				int xOffset = (int) (MathUtils.random() * (radius - _dropLimits));
				int yOffset = (int) (MathUtils.random() * (int) MathUtils
						.sqrt(1f * (radius - _dropLimits) * (radius - _dropLimits) - xOffset * xOffset));

				if ((int) (MathUtils.random() * 2) == 0) {
					x = cx - xOffset;
				} else {
					x = cx + xOffset;
				}

				if ((int) (MathUtils.random() * 2) == 0) {
					y = cy - yOffset;
				} else {
					y = cy + yOffset;
				}

				if (contains(x, y)) {
					_randomDrops.add(new Drop(x, y, 0, _dropColor));
				}
			}
		}
	}

	private void drawCircle(GLEx g, float cx, float cy, float radius) {
		int amount = -1;
		for (int i = 0; i < _circleCount; i++) {
			float newRadius = radius - (radius / _circleCount * i);
			switch (_drawMode) {
			default:
			case Circle:
				amount = -1;
				break;
			case Quad:
				amount = 4;
				break;
			case Hexagon:
				amount = 6;
				break;
			case Octagon:
				amount = 8;
				break;
			case Decagon:
				amount = 10;
				break;
			}
			if (amount != -1) {
				if (showDash) {
					g.drawDashRhombus(amount, cx - newRadius / 2, cy - newRadius / 2, newRadius, _circleColor);
				} else {
					g.drawRhombus(amount, cx - newRadius / 2, cy - newRadius / 2, newRadius, _circleColor);
				}
			} else {
				if (showDash) {
					g.drawDashCircle(cx - newRadius / 2, cy - newRadius / 2, newRadius, _circleCount, _circleColor);
				} else {
					g.drawCircle(cx - newRadius / 2, cy - newRadius / 2, newRadius, _circleColor);
				}
			}
		}
	}

	private void drawCross(GLEx g, float cx, float cy, float radius) {
		g.drawLine(cx - radius, cy, cx + radius, cy, _circleColor);
		g.drawLine(cx, cy - radius, cx, cy + radius, _circleColor);
	}

	private void drawDrop(GLEx g, float cx, float cy, float radius) {
		int color = g.color();
		if (showRandDrop) {
			createRandDot(cx, cy, radius);
			for (Drop d : _randomDrops) {
				_dropColor.setColor(d.newAlpha());
				g.fillCircle(d.x - d.radius / 2f, d.y - d.radius / 2f, d.radius, _dropColor);
				d.radius += 1f * _dropLimits / 60f / _flicker;
				d.alpha -= 1f * 255f / 60f / _flicker;
			}
			removeRandDrop();
		}
		for (Drop d : _drops) {
			_dropColor.setColor(d.newAlpha());
			g.fillCircle(getX() + d.x - d.radius / 2f, getY() + d.y - d.radius / 2f, d.radius, _dropColor);
			d.radius += 1f * _dropLimits / 60f / _flicker;
			d.alpha -= 1f * 255f / 60f / _flicker;
			if (d.alpha < 0) {
				d.alpha = 255f;
				d.radius = d.oldradius;
				_dropColor.setAlpha(1f);
			}
		}
		g.setColor(color);
	}

	private void removeRandDrop() {
		for (int i = _randomDrops.size - 1; i > -1; i--) {
			Drop d = _randomDrops.get(i);
			if (d.radius > _dropLimits || d.alpha < 0) {
				_randomDrops.remove(d);
			}
		}
	}

	public boolean isShowCross() {
		return showCross;
	}

	public LRadar setShowCross(boolean showCross) {
		this.showCross = showCross;
		return this;
	}

	public boolean isShowDrop() {
		return showDrop;
	}

	public LRadar setShowDrop(boolean showDrop) {
		this.showDrop = showDrop;
		return this;
	}

	public boolean isShowDash() {
		return showDash;
	}

	public LRadar setShowDash(boolean showDash) {
		this.showDash = showDash;
		return this;
	}

	public boolean isShowBoard() {
		return showBoard;
	}

	public LRadar setShowBoard(boolean showBoard) {
		this.showBoard = showBoard;
		return this;
	}

	public boolean isShowScanning() {
		return showScanning;
	}

	public LRadar setShowScanning(boolean showScanning) {
		this.showScanning = showScanning;
		return this;
	}

	public boolean isShowRandDrop() {
		return showRandDrop;
	}

	public LRadar setShowRandDrop(boolean showRandDrop) {
		this.showRandDrop = showRandDrop;
		return this;
	}

	public boolean isShowGround() {
		return showGround;
	}

	public LRadar setShowGround(boolean showGround) {
		this.showGround = showGround;
		return this;
	}

	public Mode getDrawMode() {
		return _drawMode;
	}

	public LRadar setDrawMode(Mode mode) {
		this._drawMode = mode;
		return this;
	}

	public LTexture getGroundTexture() {
		return _groundTexture;
	}

	public void setGroundTexture(LTexture groundTexture) {
		this._groundTexture = groundTexture;
	}

	@Override
	public String getUIName() {
		return "Radar";
	}

	@Override
	public void destory() {
		if (_groundTexture != null) {
			_groundTexture.close();
			_groundTexture = null;
		}
	}

}
