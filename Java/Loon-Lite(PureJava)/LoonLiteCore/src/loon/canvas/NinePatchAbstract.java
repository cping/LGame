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
package loon.canvas;

import java.util.Comparator;

import loon.LSysException;
import loon.canvas.Row.RowType;
import loon.geom.Padding;
import loon.geom.RectI;
import loon.geom.Region;
import loon.utils.InsertionSorter;
import loon.utils.TArray;

/**
 * 9grid算法的抽象实现，用于为不同渲染对象上绘制出九宫拆分图并呈现
 */
public abstract class NinePatchAbstract<K, V> {

	public static enum Repeat {
		HORIZONTAL, VERTICAL
	}

	private final static InsertionSorter<Row> rowSorter = new InsertionSorter<Row>();

	private final static Comparator<Row> rowComparator = new Comparator<Row>() {
		@Override
		public int compare(final Row ea, final Row eb) {
			return ea.compareTo(eb);
		}
	};

	private int _lastWidth;
	private int _lastHeight;
	private int _patchWidth;
	private int _patchHeight;
	private int _horizontalPatchNum;
	private int _verticalPatchNum;
	private TArray<TArray<Row>> columns;
	private Padding padding;
	private K image;
	private Repeat repeatType;

	public NinePatchAbstract(K img) {
		this(img, null);
	}

	public NinePatchAbstract(K img, Repeat r) {
		this.image = toImage(img);
		this.allPatch(image);
		this.repeatType = r;
	}

	public void drawNinePatch(V g2d, int x, int y, int scaledWidth, int scaledHeight) {
		if (scaledWidth <= 1 || scaledHeight <= 1) {
			return;
		}
		try {
			if (_lastWidth != scaledWidth || _lastHeight != scaledHeight) {
				_lastWidth = scaledWidth;
				_lastHeight = scaledHeight;
				resetData(scaledWidth, scaledHeight);
			}

			if (_patchWidth == scaledWidth && _patchHeight == scaledHeight) {
				draw(g2d, image, x, y, scaledWidth, scaledHeight);
				return;
			}

			pos(g2d, x, y);

			int startX = 0;
			int startY = 0;
			int minWidth = _patchWidth;
			int minHeight = _patchHeight;

			if (_horizontalPatchNum > 1) {
				minWidth = (_patchWidth / _horizontalPatchNum);
			}

			if (_verticalPatchNum > 1) {
				minHeight = (_patchHeight / _verticalPatchNum);
			}

			int columnCount = 0;

			// 逐行渲染
			for (TArray<Row> rows : columns) {
				int rowCount = 0;

				int height = _patchHeight;

				boolean isFirst = true;

				int preRowHeight = 0;

				if (startY >= scaledHeight) {
					break;
				}

				for (Row row : rows) {
					RectI rect = row.getRect();

					int width = rect.width;

					if (startX >= scaledWidth) {
						break;
					}

					if (RowType.HORIZONTALPATCH == row.getType() || RowType.TILEPATCH == row.getType()) {

						width = (_patchWidth - minWidth * (rowCount + 1));

						if (width < minWidth) {
							width = _patchWidth - (minWidth * rowCount);
						} else {
							width = minWidth;
						}

						rowCount++;
					} else if (RowType.HORIZONTALPATCH == row.getType()) {

						if (isFirst) {
							height = (_patchHeight - minHeight * (columnCount + 1));

							if (height < minHeight) {
								height = _patchHeight - (minHeight * columnCount);
							} else {
								height = minHeight;
							}

							columnCount++;

							isFirst = false;
						}
					}

					// 固定区域
					if (RowType.FIX == row.getType()) {
						draw(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, rect.width,
								rect.height);

						startX += rect.width;

						preRowHeight = rect.height;
						// 水平拉伸
					} else if (RowType.HORIZONTALPATCH == row.getType()) {

						draw(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width, rect.height);

						startX += width;

						preRowHeight = rect.height;
						// 垂直拉伸
					} else if (RowType.VERTICALPATCH == row.getType()) {

						draw(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, rect.width, height);

						startX += rect.width;

						preRowHeight = height;
						// 平铺
					} else if (RowType.TILEPATCH == row.getType()) {

						if (repeatType != null) {
							repeatImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width,
									height);
						} else {
							draw(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width, height);
						}

						startX += width;

						preRowHeight = height;
					}
				}

				startX = 0;
				startY += preRowHeight;
			}
		} catch (Throwable e) {
			throw new LSysException("drawNinePatch() exception", e);
		} finally {
			pos(g2d, -x, -y);
		}
	}

	public TArray<TArray<Row>> allColumn(NinePatchRegion xRegions, NinePatchRegion yRegions) {
		boolean isPatchY = false;
		int i = 0;
		int j = 0;
		int patchNum = yRegions.getPatchRegions().size();
		int fixNum = yRegions.getFixedRegions().size();

		Region yRegion = null;

		TArray<TArray<Row>> columns = new TArray<TArray<Row>>();

		do {
			yRegion = null;

			if (isPatchY && patchNum >= j + 1) {
				yRegion = yRegions.getPatchRegions().get(j++);
			}

			if (!isPatchY && fixNum >= i + 1) {
				yRegion = yRegions.getFixedRegions().get(i++);
			}

			if (yRegion != null) {
				columns.add(allRow(yRegion, xRegions, isPatchY));
			}

			isPatchY = !isPatchY;
		} while (yRegion != null);

		return columns;
	}

	public TArray<Row> allRow(Region yRegion, NinePatchRegion xRegions, boolean isPatchY) {
		boolean isPatchX = false;
		int i = 0;
		int j = 0;
		int patchNum = xRegions.getPatchRegions().size();
		int fixNum = xRegions.getFixedRegions().size();

		Region xRegion = null;

		TArray<Row> column = new TArray<Row>();

		do {
			xRegion = null;

			if (isPatchX && patchNum >= j + 1) {
				xRegion = xRegions.getPatchRegions().get(j++);
			}

			if (!isPatchX && fixNum >= i + 1) {
				xRegion = xRegions.getFixedRegions().get(i++);
			}

			if (xRegion != null) {
				Row.RowType rowType = getRowType(isPatchX, isPatchY);

				int height = yRegion.getEnd() - yRegion.getStart();

				int width = xRegion.getEnd() - xRegion.getStart();

				RectI rect = new RectI(xRegion.getStart() + 1, yRegion.getStart() + 1, width, height);

				Row row = new Row(rect, rowType);

				column.add(row);
			}

			isPatchX = !isPatchX;
		} while (xRegion != null);

		rowSorter.sort(column, rowComparator);

		return column;
	}

	public Padding getPadding(int w, int h, TArray<Region> xRegions, TArray<Region> yRegions) {
		Region xRegion = xRegions.get(0);
		Region yRegion = yRegions.get(0);

		int left = xRegion.getStart();
		int top = yRegion.getStart();
		int right = w - xRegion.getEnd();
		int bottom = h - yRegion.getEnd();

		return new Padding(left, top, right, bottom);
	}

	public NinePatchRegion getPatches(int[] pixels) {
		int start = 0;

		int lastPixel = pixels[0];

		TArray<Region> fixArea = new TArray<Region>();

		TArray<Region> patchArea = new TArray<Region>();

		for (int i = 1; i <= pixels.length; i++) {
			if (i < pixels.length && lastPixel == pixels[i]) {
				continue;
			}

			Region region = new Region(start, i);

			if (LColor.TRANSPARENT == lastPixel) {
				patchArea.add(region);
			} else {
				fixArea.add(region);
			}

			start = i;

			if (i < pixels.length) {
				lastPixel = pixels[i];
			}
		}

		if (start == 0) {
			Region region = new Region(start, pixels.length);

			if (LColor.TRANSPARENT == lastPixel) {
				patchArea.add(region);
			} else {
				fixArea.add(region);
			}
		}

		return new NinePatchRegion(fixArea, patchArea);
	}

	public void repeatImage(V g2d, K image, int x, int y, int sw, int sh, int dx, int dy, int dw, int dh) {
		if (repeatType == null) {
			return;
		}

		if (repeatType == Repeat.HORIZONTAL) {
			int hornaizeW = dw;

			do {
				if (hornaizeW - sw < 0) {
					sw = hornaizeW;
				}

				hornaizeW -= sw;

				draw(g2d, image, x, y, sw, sh, dx, dy, sw, dh);

				dx += sw;

			} while (hornaizeW > 0);
		} else if (repeatType == Repeat.VERTICAL) {
			int verticalH = dh;
			do {
				if (verticalH - sh < 0) {
					sh = verticalH;
				}

				verticalH -= sh;

				draw(g2d, image, x, y, sw, sh, dx, dy, dw, sh);

				dy += sh;

			} while (verticalH > 0);
		}
	}

	protected void allPatch(K image) {

		int width = getImageWidth(image) - 2;
		int height = getImageHeight(image) - 2;

		int[] row = null;
		int[] column = null;

		column = getPixels(image, 0, 1, 1, height);

		NinePatchRegion left = getPatches(column);

		row = getPixels(image, 1, 0, width, 1);

		NinePatchRegion top = getPatches(row);

		this._horizontalPatchNum = top.getPatchRegions().size();
		this._verticalPatchNum = left.getPatchRegions().size();
		this.columns = allColumn(top, left);

		row = getPixels(image, 1, height + 1, width, 1);
		column = getPixels(image, width + 1, 1, 1, height);

		NinePatchRegion bottom = getPatches(row);
		NinePatchRegion right = getPatches(column);

		this.padding = getPadding(width, height, bottom.getPatchRegions(), right.getPatchRegions());
	}

	protected K toImage(K image) {
		return image;
	}

	private RowType getRowType(boolean isPatchX, boolean isPatchY) {
		if (!isPatchX && !isPatchY) {
			return RowType.FIX;
		} else if (!isPatchX && isPatchY) {
			return RowType.VERTICALPATCH;
		} else if (isPatchX && !isPatchY) {
			return RowType.HORIZONTALPATCH;
		} else {
			return RowType.TILEPATCH;
		}
	}

	public Padding getPadding() {
		return padding;
	}

	private void resetData(int scaleWidth, int scaleHeight) {
		this._patchWidth = scaleWidth;
		this._patchHeight = scaleHeight;
		boolean isFirst = true;
		boolean isNewColumn = true;

		for (TArray<Row> rows : columns) {
			for (Row row : rows) {
				if (RowType.FIX == row.getType() && isFirst) {
					_patchWidth -= row.getRect().width;
				}
				if (RowType.FIX == row.getType() && isNewColumn) {
					_patchHeight -= row.getRect().height;
					isNewColumn = false;
				}
			}

			isNewColumn = true;
			isFirst = false;
		}
	}

	public abstract int[] getPixels(K img, int x, int y, int w, int h);

	public abstract int getImageWidth(K img);

	public abstract int getImageHeight(K img);

	public abstract void pos(V g2d, int x, int y);

	public abstract void draw(V g2d, K img, int x, int y, int scaledWidth, int scaledHeight);

	public abstract void draw(V g2d, K img, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh);

}
