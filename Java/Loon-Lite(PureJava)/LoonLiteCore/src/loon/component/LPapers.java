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
import loon.LTextures;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class LPapers extends LContainer {

	private float _xOffset;
	private float _yOffset;
	private int _rowHeight;
	private int _columnWidth;
	private int _rows;
	private int _columns;

	private final TArray<LTexture> _images;

	private boolean _updateList;

	public LPapers(int x, int y) {
		this(x, y, 0, 0, -1, -1, (LTexture) null, (LTexture) null);
	}

	public LPapers(int x, int y, int w, int h) {
		this(x, y, w, h, -1, -1, (LTexture) null, (LTexture) null);
	}

	public LPapers(int x, int y, int w, int h, int row, int col) {
		this(x, y, w, h, row, col, (LTexture) null, (LTexture) null);
	}

	public LPapers(int x, int y, int w, int h, int row, int col, LTexture background, LTexture... images) {
		super(x, y, w, h);
		this._images = new TArray<>();
		this._rows = row;
		this._columns = col;
		this.setBackground(background);
		this.customRendering = false;
		if (this._rows == -1 && this._columns == -1) {
			_updateList = true;
		}
		if (images != null) {
			for (LTexture tex : images) {
				if (tex != null) {
					_images.add(tex);
				}
			}
		}
		if (this._rows != -1 && this._columns != -1) {
			if (this._rows == 1) {
				this._rowHeight = (int) this.getHeight();
				this._yOffset = 0;
			} else {
				this._rowHeight = (int) (this.getHeight() / this._rows);
				this._yOffset = this.getHeight() / (this._rows - 1) * 1 / 10;
			}
			if (this._columns == 1) {
				this._columnWidth = (int) this.getWidth();
				this._xOffset = 0;
			} else {
				this._columnWidth = (int) (this.getWidth() / this._columns);
				this._xOffset = this.getWidth() / (this._columns - 1) * 1 / 10;
			}
		} else {
			updateList();
		}
	}

	private int getMaxRowsHeight() {
		int height = 0;
		for (LTexture tex : _images) {
			if (tex != null) {
				height = MathUtils.max(height, tex.getHeight());
			}
		}
		return height;
	}

	private int getMaxColumnsWidth() {
		int width = 0;
		for (LTexture tex : _images) {
			if (tex != null) {
				width = MathUtils.max(width, tex.getWidth());
			}
		}
		return width;
	}

	private void updateList() {
		final int size = this._images.size;
		if (size != 0 && _updateList) {

			if (getWidth() <= 1 || getHeight() <= 1) {
				this.setSize(getScreenWidth(), getScreenHeight());
			}

			final int width = getMaxColumnsWidth();
			final int height = getMaxRowsHeight();

			this._rows = (int) (this.getHeight() / height);
			this._columns = (int) (this.getWidth() / width);

			this._rowHeight = height;
			if (_rows > 1) {
				this._yOffset = height / (this._rows - 1) * 1 / 10;
			}
			this._columnWidth = width;
			if (_columns > 1) {
				this._xOffset = width / (this._columns - 1) * 1 / 10;
			}
		}
	}

	public LPapers add(String path) {
		return add(LTextures.loadTexture(path));
	}

	public LPapers add(LTexture tex) {
		_images.add(tex);
		updateList();
		return this;
	}

	public LPapers remove(LTexture tex) {
		_images.remove(tex);
		updateList();
		return this;
	}

	public int getRowHeight() {
		return this._rowHeight;
	}

	public LPapers setRowHeight(int row) {
		this._rowHeight = row;
		return this;
	}

	public int getColumnWidth() {
		return this._columnWidth;
	}

	public LPapers setColumnWidth(int col) {
		this._columnWidth = col;
		return this;
	}

	public LPapers setXOffset(float offx) {
		this._xOffset = offx;
		return this;
	}

	public LPapers setYOffset(float offy) {
		this._yOffset = offy;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		final int size = _images.size;
		if (size > 0) {
			int idx = -1;

			for (int j = 0; j < this._rows; j++) {
				for (int i = 0; i < this._columns; i++) {
					LTexture img;
					if (idx < size - 1) {
						idx++;
						img = this._images.get(idx);
					} else {
						img = null;
					}
					if (img != null) {
						g.draw(img,
								x + i * (this.getColumnWidth() + this._xOffset)
										+ (this.getColumnWidth() - img.getWidth()) / 2,
								y + j * (this.getRowHeight() + this._yOffset)
										+ (this.getRowHeight() - img.getHeight()) / 2);
					}
				}
			}

		}
	}

	@Override
	public String getUIName() {
		return "Papers";
	}

	@Override
	public void destory() {
		_images.clear();
	}
}
