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

import loon.LSysException;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class LColorPicker extends LComponent {

	private final static int[] DefaultColors = new int[] { 0x000000, 0x333333, 0x666666, 0x999999, 0xCCCCCC, 0xFFFFFF,
			0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0x00FFFF, 0xFF00FF };

	private final TArray<LColor> _colors = new TArray<LColor>();

	private final int _colorRow;

	private final int _colorCol;

	private final int _gridSize;

	private int _selected;

	private LTexture _cachePicker;

	private boolean _initPicker;

	public LColorPicker(int x, int y) {
		this(x, y, 15);
	}

	public LColorPicker(int x, int y, int gridSize) {
		this(x, y, 20, 12, gridSize);
	}

	public LColorPicker(int x, int y, int _colorRow, int _colorCol, int gridSize) {
		super(x, y, _colorRow * gridSize, _colorCol * gridSize);
		if (_colorCol < 1) {
			throw new LSysException("The color column only has a minimum of 1 ！");
		}
		if (_colorCol > 12) {
			throw new LSysException("The color column only has a maximum of 12 ！");
		}
		this._colorRow = _colorRow;
		this._colorCol = _colorCol;
		this._gridSize = gridSize;
		this._selected = -1;
	}

	protected LTexture createColorPickerCache() {
		Image img = Image.createImage(getWidth(), getHeight());
		Canvas g = img.getCanvas();
		for (int i = 0; i < _colorCol; i++) {
			for (int j = 0; j < _colorRow; j++) {
				int color = 0;
				if (j == 0) {
					color = DefaultColors[i];
				} else if (j == 1) {
					color = 0;
				} else {
					color = (((i * 3 + j / 6) % 3 << 0) + ((i / 6) << 0) * 3) * 0x33 << 16 | j % 6 * 0x33 << 8
							| (i << 0) % 6 * 0x33;
				}
				LColor newColor = new LColor(color);
				_colors.add(newColor);
				final int tx = j * _gridSize;
				final int ty = i * _gridSize;
				g.setColor(newColor);
				g.fillRect(tx, ty, _gridSize, _gridSize);
				g.setColor(LColor.white);
				g.strokeRect(tx, ty, _gridSize - 1, _gridSize - 1);
			}
		}
		return img.texture();
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!_initPicker) {
			_cachePicker = createColorPickerCache();
			_initPicker = true;
			freeRes().add(_cachePicker);
		}
		g.draw(_cachePicker, x, y);
		final Vector2f point = getUITouchXY();
		if (contains(point.x + x, point.y + y)) {
			_selected = getColorIndexSelected();
			for (int i = 0; i < _colorCol; i++) {
				for (int j = 0; j < _colorRow; j++) {
					int tx = j * _gridSize;
					int ty = i * _gridSize;
					if ((j + _colorRow * i) == _selected) {
						final int nx = x + tx - _gridSize / 2;
						final int ny = y + ty - _gridSize / 2;
						final int newTile = _gridSize * 2;
						g.fillRect(nx, ny, newTile, newTile, _colors.get(getColorIndex(tx, ty)));
						g.drawRect(nx, ny, newTile, newTile, LColor.lightGray);
					}

				}
			}
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		_selected = getColorIndexSelected();
		final Vector2f pos = getUITouchXY();
		final int x = MathUtils.floor(pos.x / this._gridSize);
		final int y = MathUtils.floor(pos.y / this._gridSize);
		onColorClickd(x, y, this._colors.get(x + _colorRow * y));
	}

	protected void onColorClickd(int tileX, int tileY, LColor color) {

	}

	public String getColorHex() {
		return getColorSelected().toString();
	}

	public String getColorCSS() {
		return getColorSelected().toCSS();
	}

	public int getColorIndexSelected() {
		final Vector2f pos = getUITouchXY();
		return getColorIndex(pos.x, pos.y);
	}

	public int getColorIndex(float x, float y) {
		final int tx = MathUtils.floor(x / this._gridSize);
		final int ty = MathUtils.floor(y / this._gridSize);
		return tx + _colorRow * ty;
	}

	public LColor getColorSelected() {
		return this._colors.get(getColorIndexSelected());
	}

	@Override
	public String getUIName() {
		return "ColorPicker";
	}

	@Override
	public void destory() {
		_initPicker = false;
	}

}
