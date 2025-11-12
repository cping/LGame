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

import loon.LSystem;
import loon.LTexture;
import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXMap.StaggerAxis;
import loon.action.map.tmx.TMXMap.StaggerIndex;
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.TMXTileSet;
import loon.action.map.tmx.tiles.TMXMapTile;
import loon.action.map.tmx.tiles.TMXTile;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 六角形(六边形)地图纹理渲染器
 */
public class TMXHexagonalMapRenderer extends TMXMapRenderer {

	private final static float[][] offsetsStaggerX = new float[][] { { 0, 0 }, { 1, -1 }, { 1, 0 }, { 2, 0 } };

	private final static float[][] offsetsStaggerY = new float[][] { { 0, 0 }, { -1, 1 }, { 0, 1 }, { 0, 2 } };

	private final Vector2f[] centers = new Vector2f[4];

	private boolean staggerAxisX;

	private boolean staggerAxisY;

	private boolean staggerIndexEven;

	private float hexSideLength;

	private float sideLengthX;

	private float sideLengthY;

	private float sideOffsetX;

	private float sideOffsetY;

	private float columnWidth;

	private float rowHeight;

	public TMXHexagonalMapRenderer(TMXMap map, float w, float h) {
		super(map, w, h);
	}

	public TMXHexagonalMapRenderer(TMXMap map) {
		super(map);

		StaggerAxis axis = map.getStaggerAxis();
		staggerAxisX = axis.equals(StaggerAxis.AXIS_X);
		staggerAxisY = axis.equals(StaggerAxis.AXIS_Y);
		StaggerIndex index = map.getStaggerIndex();
		staggerIndexEven = index.equals(StaggerIndex.EVEN);
		if (!staggerAxisX && map.getHeight() % 2 == 0) {
			staggerIndexEven = !staggerIndexEven;
		}

		int hexlength = map.getHexSideLength();

		this.sideLengthX = 0;
		this.sideLengthY = 0;

		if (staggerAxisX) {
			this.sideLengthX = hexlength;
		} else {
			this.sideLengthY = hexlength;
		}

		this.sideOffsetX = (map.getTileWidth() - this.sideLengthX) / 2;
		this.sideOffsetY = (map.getTileHeight() - this.sideLengthY) / 2;

		this.columnWidth = this.sideOffsetX + this.sideLengthX;
		this.rowHeight = this.sideOffsetY + this.sideLengthY;

		if (staggerAxisX) {
			hexlength = map.getTileWidth();
			if (hexlength != 0) {
				hexSideLength = 0.5f * hexlength;
			} else {
				hexSideLength = 0.5f * map.getTileWidth();
			}
		} else {
			hexlength = map.getTileHeight();
			if (hexlength != 0) {
				hexSideLength = 0.5f * hexlength;
			} else {
				hexSideLength = 0.5f * map.getTileHeight();
			}
		}

		for (int i = 0; i < centers.length; i++) {
			centers[i] = new Vector2f();
		}
	}

	public boolean isStaggerAxisX() {
		return staggerAxisX;
	}

	public boolean isStaggerAxisY() {
		return staggerAxisY;
	}

	@Override
	protected void renderImageLayer(GLEx g, TMXImageLayer imageLayer) {
		if (!imageLayer.isVisible()) {
			return;
		}
		final float layerHexLength = hexSideLength;
		final int mapHeight = map.getHeight();
		final int tileHeight = map.getTileHeight();
		final int layerTileHeight = tileHeight;
		final float heightPixels = (mapHeight * tileHeight);
		final float hexMapHeightPixels = ((heightPixels * (3f / 4f)) + (layerHexLength * 0.5f));
		final float halfTileHeight = layerTileHeight * 0.5f;
		float imageLayerYOffset = 0;
		if (staggerAxisX) {
			imageLayerYOffset = halfTileHeight;
		} else {
			imageLayerYOffset = -(heightPixels - hexMapHeightPixels);
		}
		final LTexture current = textureMap.get(imageLayer.getSource());
		final float posX = (imageLayer.getRenderOffsetX() + _objectLocation.x) * scaleX;
		final float posY = (imageLayer.getRenderOffsetY() + imageLayerYOffset + _objectLocation.y) * scaleY;
		g.draw(current, posX, posY, imageLayer.getWidth() * map.getTileWidth(),
				imageLayer.getHeight() * map.getTileHeight(), imageLayer.getTileLayerColor(baseColor), scaleX, scaleY,
				false, false);
	}

	@Override
	protected void renderTileLayer(GLEx g, TMXTileLayer tileLayer) {

		synchronized (this) {
			if (!tileLayer.isVisible()) {
				return;
			}
			final float viewWidth = MathUtils.min(getViewWidth(), getWidth());
			final float viewHeight = MathUtils.min(getViewHeight(), getHeight());
			final int screenWidth = MathUtils.iceil(viewWidth - _objectLocation.x);
			final int screenHeight = MathUtils.iceil(viewHeight - _objectLocation.y);
			final int tx = MathUtils.iceil((getRenderX() + _objectLocation.x) / map.getTileWidth());
			final int ty = MathUtils.iceil((getRenderY() + _objectLocation.y) / map.getTileHeight());
			final int windowWidth = MathUtils.iceil(screenWidth / map.getTileWidth() / scaleX) + 2;
			final int windowHeight = MathUtils.iceil(screenHeight / map.getTileHeight() / scaleY) + 2;

			final int layerWidth = tileLayer.getWidth();
			final int layerHeight = tileLayer.getHeight();

			final float layerTileWidth = tileLayer.getTileWidth();
			final float layerTileHeight = tileLayer.getTileHeight();

			final float layerOffsetX = tileLayer.getRenderOffsetX() - (tileLayer.getParallaxX() - 1f);
			final float layerOffsetY = -tileLayer.getRenderOffsetY() - (tileLayer.getParallaxY() - 1f);

			final float layerHexLength = hexSideLength;

			final boolean saveCache = textureMap.size == 1 && allowCache;

			_texCurrent = textureMap.get(map.getTileset(tileIndex).getSource());
			_texBatch = _texCurrent.getTextureBatch();

			boolean isCached = false;

			final LColor drawColor = tileLayer.getTileLayerColor(baseColor);

			try {

				if (saveCache) {
					int hashCode = 1;
					hashCode = LSystem.unite(hashCode, tx);
					hashCode = LSystem.unite(hashCode, ty);
					hashCode = LSystem.unite(hashCode, windowWidth);
					hashCode = LSystem.unite(hashCode, windowHeight);
					hashCode = LSystem.unite(hashCode, layerWidth);
					hashCode = LSystem.unite(hashCode, layerHeight);
					hashCode = LSystem.unite(hashCode, layerTileWidth);
					hashCode = LSystem.unite(hashCode, layerTileHeight);
					hashCode = LSystem.unite(hashCode, layerOffsetX);
					hashCode = LSystem.unite(hashCode, layerOffsetY);
					hashCode = LSystem.unite(hashCode, layerHexLength);
					hashCode = LSystem.unite(hashCode, scaleX);
					hashCode = LSystem.unite(hashCode, scaleY);
					hashCode = LSystem.unite(hashCode, staggerAxisX);
					hashCode = LSystem.unite(hashCode, tileLayer.isDirty());
					hashCode = LSystem.unite(hashCode, _objectRotation);

					if (isCached = postCache(_texBatch, hashCode)) {
						return;
					}

				} else {
					_texBatch.begin();
				}

				_texBatch.setColor(drawColor);

				if (staggerAxisX) {
					final float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2;
					final float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2;
					final float layerTileHeight50 = layerTileHeight * 0.5f;

					final int ya = MathUtils.max(0,
							MathUtils.ifloor((0f - layerTileHeight50 - layerOffsetX) / layerTileHeight));
					final int yb = MathUtils.min(layerHeight,
							MathUtils.ifloor((0f + screenHeight + layerTileHeight - layerOffsetX) / layerTileHeight));

					final int xa = MathUtils.max(0,
							MathUtils.ifloor(((0f - tileWidthLowerCorner - layerOffsetY) / tileWidthUpperCorner)));
					final int xb = MathUtils.min(layerWidth, MathUtils
							.ifloor((0f + screenWidth + tileWidthUpperCorner - layerOffsetY) / tileWidthUpperCorner));

					final int maxXa = (staggerIndexEven == (xa % 2 == 0)) ? xa + 1 : xa;
					final int maxXb = (staggerIndexEven == (xa % 2 == 0)) ? xa : xa + 1;

					for (int row = yb - 1; row >= ya; row--) {
						for (int col = maxXa; col < xb; col += 2) {
							float tileX = tileWidthUpperCorner * col + layerOffsetX;
							float tileY = layerTileHeight50 + (layerTileHeight * row) + layerOffsetY;
							drawTile(tileLayer, col, row, tileX, tileY);
						}

						for (int col = maxXb; col < xb; col += 2) {
							float tileX = tileWidthUpperCorner * col + layerOffsetX;
							float tileY = layerTileHeight * row + layerOffsetY;
							drawTile(tileLayer, col, row, tileX, tileY);
						}
					}
				} else {
					final float tileHeightLowerCorner = (layerTileHeight - layerHexLength) / 2f;
					final float tileHeightUpperCorner = (layerTileHeight + layerHexLength) / 2f + layerTileWidth / 6f;
					final float layerTileWidth50 = layerTileWidth * 0.5f;

					final int maxYa = MathUtils.max(0,
							MathUtils.ifloor(((0f - tileHeightLowerCorner - layerOffsetX) / tileHeightUpperCorner)));
					final int maxYb = MathUtils.min(layerHeight, MathUtils.ifloor(
							(0f + screenHeight + tileHeightUpperCorner - layerOffsetX) / tileHeightUpperCorner));

					final int maxXa = MathUtils.max(0,
							MathUtils.ifloor(((0f - layerTileWidth50 - layerOffsetY) / layerTileWidth)));
					final int maxXb = MathUtils.min(layerWidth,
							MathUtils.ifloor((0f + screenWidth + layerTileWidth - layerOffsetY) / layerTileWidth));

					float shiftX = 0;
					for (int y = maxYb - 1; y >= maxYa; y--) {
						if ((y % 2 == 0) == staggerIndexEven) {
							shiftX = layerTileWidth50;
						} else {
							shiftX = 0;
						}
						for (int x = maxXa; x < maxXb; x++) {
							drawTile(tileLayer, x, y, layerTileWidth * x + shiftX + layerOffsetX,
									tileHeightUpperCorner * y + layerOffsetY);

						}
					}
				}

			} finally {
				if (!isCached) {
					_texBatch.end();
					if (saveCache) {
						saveCache(_texBatch);
					}
				}
			}
		}
	}

	private void drawTile(final TMXTileLayer tileLayer, final int x, final int y, float offsetX, float offsetY) {
		if (tileLayer != null) {

			TMXMapTile mapTile = tileLayer.getTile(x, y);

			if (mapTile == null || mapTile.getTileSetID() == -1) {
				return;
			}

			TMXTileSet tileSet = map.getTileset(mapTile.getTileSetID());
			TMXTile tile = tileSet.getTile(mapTile.getGID() - tileSet.getFirstGID());

			LTexture texture = textureMap.get(tileSet.getSource());

			if (texture.getID() != _texCurrent.getID()) {
				_texBatch.end();
				_texCurrent = texture;
				_texBatch = _texCurrent.getTextureBatch();
				_texBatch.begin();
				_texBatch.checkTexture(_texCurrent);
			}

			int tileID = mapTile.getGID() - tileSet.getFirstGID();
			if (tile != null && tile.isAnimated()) {
				tileID = tileAnimators.get(tile).getCurrentFrame().getTileID();
			}

			int numColsPerRow = tileSet.getImage().getWidth() / tileSet.getTileWidth();

			int tileSetCol = tileID % numColsPerRow;
			int tileSetRow = tileID / numColsPerRow;

			float tileWidth = tileSet.getTileWidth();
			float tileHeight = tileSet.getTileHeight();

			float srcX = (tileSet.getMargin() + (tileSet.getTileWidth() + tileSet.getSpacing()) * tileSetCol);
			float srcY = (tileSet.getMargin() + (tileSet.getTileHeight() + tileSet.getSpacing()) * tileSetRow);
			float srcWidth = srcX + tileWidth;
			float srcHeight = srcY + tileHeight;

			boolean flipX = mapTile.isFlippedHorizontally();
			boolean flipY = mapTile.isFlippedVertically();
			boolean flipZ = mapTile.isFlippedDiagonally();

			if (flipZ) {
				flipX = !flipX;
				flipY = !flipY;
			}

			_texBatch.draw((x + offsetX + _objectLocation.x) * scaleX, (y + offsetY + _objectLocation.y) * scaleY, -1f,
					-1f, 0f, 0f, tileWidth, tileHeight, scaleX, scaleY, this._objectRotation, srcX, srcY, srcWidth,
					srcHeight, flipX, flipY);
		}

	}

	public Vector2f pixelToGrid(float offset) {
		return pixelToGrid(offset, tempLocation);
	}

	public Vector2f pixelToGrid(float offset, Vector2f out) {
		if (out == null) {
			out = new Vector2f();
		}
		float q = out.x;
		float r = out.y;
		float s = -q - r;
		float qi = MathUtils.round(q);
		float ri = MathUtils.round(r);
		float si = MathUtils.round(s);

		float qdiff = MathUtils.abs(qi - q);
		float rdiff = MathUtils.abs(ri - r);
		float sdiff = MathUtils.abs(si - s);
		if (qdiff > rdiff && qdiff > sdiff) {
			qi = -ri - si;
		} else if (rdiff > sdiff) {
			ri = -qi - si;
		}
		out.x = qi + (ri + offset * ri) / 2f;
		out.y = ri;
		return out;
	}

	public Vector2f gridToPixel(int row, int col, float offset) {
		return gridToPixel(row, col, offset, tempLocation);
	}

	public Vector2f gridToPixel(int row, int col, float offset, Vector2f out) {
		if (out == null) {
			out = new Vector2f();
		}
		row = (int) (row - (col + offset * col) / 2);
		out.x = row;
		out.y = col;
		return out;
	}

	@Override
	public Vector2f pixelToTileCoords(float x, float y) {
		return pixelToTileCoords(x, y, tempLocation);
	}

	public Vector2f pixelToTileCoords(float x, float y, Vector2f out) {

		if (out == null) {
			out = new Vector2f();
		}
		x = offsetXPixel(x);
		y = offsetYPixel(y);
		final StaggerIndex index = map.getStaggerIndex();

		if (staggerAxisX) {
			x = x - ((index == StaggerIndex.ODD) ? this.sideLengthX : map.getTileWidth());
		} else {
			y = y - ((index == StaggerIndex.ODD) ? this.sideOffsetY : map.getTileHeight());
		}

		final Vector2f referencePoint = new Vector2f(MathUtils.floor(x / (map.getTileWidth() + this.sideLengthX)),
				MathUtils.floor(y / (map.getTileHeight() + this.sideLengthY)));

		final Vector2f rel = new Vector2f(x - referencePoint.x * (map.getTileWidth() + this.sideLengthX),
				y - referencePoint.y * (map.getTileHeight() + this.sideLengthY));

		if (staggerAxisX) {
			referencePoint.x = referencePoint.x * 2;
			if (index == StaggerIndex.EVEN) {
				++referencePoint.x;
			}
		} else {
			referencePoint.y = referencePoint.y * 2;
			if (index == StaggerIndex.EVEN) {
				++referencePoint.y;
			}
		}

		float left, top, centerX, centerY;

		if (staggerAxisX) {
			left = this.sideLengthX / 2;
			centerX = left + this.columnWidth;
			centerY = map.getTileHeight() / 2;

			this.centers[0].set(left, centerY);
			this.centers[1].set(centerX, centerY - this.rowHeight);
			this.centers[2].set(centerX, centerY + this.rowHeight);
			this.centers[3].set(centerX + this.columnWidth, centerY);
		} else {
			top = this.sideLengthY / 2;
			centerX = map.getTileWidth() / 2;
			centerY = top + this.rowHeight;
			this.centers[0].set(centerX, top);
			this.centers[1].set(centerX - this.columnWidth, centerY);
			this.centers[2].set(centerX + this.columnWidth, centerY);
			this.centers[3].set(centerX, centerY + this.rowHeight);
		}

		int nearest = 0;
		float minDist = Float.MAX_VALUE;
		float dc;
		for (int i = 0; i < centers.length; ++i) {
			dc = MathUtils.pow(this.centers[i].x - rel.x, 2) + MathUtils.pow(this.centers[i].y - rel.y, 2);
			if (dc < minDist) {
				minDist = dc;
				nearest = i;
			}
		}
		float[][] offsets = (staggerAxisX) ? offsetsStaggerX : offsetsStaggerY;

		out.x = referencePoint.x + offsets[nearest][0];
		out.y = referencePoint.y + offsets[nearest][1];

		return out;
	}

	@Override
	public Vector2f tileToPixelCoords(float q, float r) {
		return tileToPixelCoords(q, r, tempLocation);
	}

	public Vector2f tileToPixelCoords(float q, float r, Vector2f out) {
		if (out == null) {
			out = new Vector2f();
		}
		final StaggerIndex index = map.getStaggerIndex();
		float x, y;
		if (staggerAxisX) {
			x = q * this.columnWidth;
			if (index == StaggerIndex.ODD) {
				y = r * (map.getTileHeight() + this.sideLengthY);
				y = y + (this.rowHeight * MathUtils.isAnd(q, 1));
			} else {
				y = r * (map.getTileHeight() + this.sideLengthY);
				y = y + (this.rowHeight * (1f - MathUtils.isAnd(q, 1)));
			}
		} else {
			y = r * this.rowHeight;
			if (index == StaggerIndex.ODD) {
				x = q * (map.getTileWidth() + this.sideLengthX);
				x = x + (this.columnWidth * MathUtils.isAnd(r, 1));
			} else {
				x = q * (map.getTileWidth() + this.sideLengthX);
				x = x + (this.columnWidth * (1f - MathUtils.isAnd(r, 1)));
			}
		}
		out.x = x;
		out.y = y;
		return out.mulSelf(scaleX, scaleY);
	}

}
