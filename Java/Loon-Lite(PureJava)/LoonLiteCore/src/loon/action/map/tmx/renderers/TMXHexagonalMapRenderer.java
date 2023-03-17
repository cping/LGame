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
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 六角形(六边形)地图纹理渲染器
 */
public class TMXHexagonalMapRenderer extends TMXMapRenderer {

	private boolean staggerAxisX;

	private boolean staggerIndexEven;

	private float hexSideLength;

	public TMXHexagonalMapRenderer(TMXMap map) {
		super(map);

		StaggerAxis axis = map.getStaggerAxis();
		staggerAxisX = axis.equals(StaggerAxis.AXIS_X);
		StaggerIndex index = map.getStaggerIndex();
		staggerIndexEven = index.equals(StaggerIndex.EVEN);
		if (!staggerAxisX && map.getHeight() % 2 == 0) {
			staggerIndexEven = !staggerIndexEven;
		}

		int hexlength = map.getHexSideLength();
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

	}

	@Override
	protected void renderImageLayer(GLEx g, TMXImageLayer imageLayer) {
		if (!imageLayer.isVisible()) {
			return;
		}
		float opacity = imageLayer.getOpacity();
		if (opacity <= 0f) {
			return;
		}
		if (opacity > 1f) {
			opacity = 1f;
		}
		float tmpAlpha = baseColor.a;
		baseColor.a *= opacity;
		LTexture current = textureMap.get(imageLayer.getImage().getSource());
		float tileWidth = map.getTileWidth();
		float tileHeight = map.getTileHeight();
		float posX = (imageLayer.getRenderOffsetY() * tileWidth / 2) + (imageLayer.getRenderOffsetX() * tileWidth / 2)
				+ getRenderX();
		float posY = (imageLayer.getRenderOffsetX() * tileHeight / 2) - (imageLayer.getRenderOffsetY() * tileHeight / 2)
				+ getRenderY();
		g.draw(current, posX, posY, imageLayer.getWidth() * map.getTileWidth(),
				imageLayer.getHeight() * map.getTileHeight(), baseColor);
		baseColor.a = tmpAlpha;
	}

	@Override
	protected void renderTileLayer(GLEx g, TMXTileLayer tileLayer) {

		synchronized (this) {
			if (!tileLayer.isVisible()) {
				return;
			}
			float opacity = tileLayer.getOpacity();
			if (opacity <= 0f) {
				return;
			}
			if (opacity > 1f) {
				opacity = 1f;
			}

			final int screenWidth = LSystem.viewSize.getWidth();
			final int screenHeight = LSystem.viewSize.getHeight();

			final int tx = (int) (getRenderX() / map.getTileWidth());
			final int ty = (int) (getRenderY() / map.getTileHeight());
			final float windowWidth = screenWidth / map.getTileWidth();
			final float windowHeight = screenHeight / map.getTileHeight();

			final int layerWidth = tileLayer.getWidth();
			final int layerHeight = tileLayer.getHeight();

			final float layerTileWidth = tileLayer.getTileWidth();
			final float layerTileHeight = tileLayer.getTileHeight();

			final float layerOffsetX = tileLayer.getRenderOffsetX() - (tileLayer.getParallaxX() - 1f);
			final float layerOffsetY = -tileLayer.getRenderOffsetY() - (tileLayer.getParallaxY() - 1f);

			final float layerHexLength = hexSideLength;

			final boolean saveCache = textureMap.size == 1 && allowCache;

			_texCurrent = textureMap.get(map.getTileset(0).getImage().getSource());
			_texBatch = _texCurrent.getTextureBatch();

			final float tmpAlpha = baseColor.a;
			boolean isCached = false;
			baseColor.a *= opacity;

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

				_texBatch.setColor(baseColor);

				if (staggerAxisX) {
					final float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2;
					final float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2;
					final float layerTileHeight50 = layerTileHeight * 0.5f;

					final int ya = MathUtils.max(0, (int) ((0f - layerTileHeight50 - layerOffsetX) / layerTileHeight));
					final int yb = MathUtils.min(layerHeight,
							(int) ((0f + screenHeight + layerTileHeight - layerOffsetX) / layerTileHeight));

					final int xa = MathUtils.max(0,
							(int) (((0f - tileWidthLowerCorner - layerOffsetY) / tileWidthUpperCorner)));
					final int xb = MathUtils.min(layerWidth,
							(int) ((0f + screenWidth + tileWidthUpperCorner - layerOffsetY) / tileWidthUpperCorner));

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
							(int) (((0f - tileHeightLowerCorner - layerOffsetX) / tileHeightUpperCorner)));
					final int maxYb = MathUtils.min(layerHeight,
							(int) ((0f + screenHeight + tileHeightUpperCorner - layerOffsetX) / tileHeightUpperCorner));

					final int maxXa = MathUtils.max(0,
							(int) (((0f - layerTileWidth50 - layerOffsetY) / layerTileWidth)));
					final int maxXb = MathUtils.min(layerWidth,
							(int) ((0f + screenWidth + layerTileWidth - layerOffsetY) / layerTileWidth));

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
				baseColor.a = tmpAlpha;
			}
		}
	}

	private void drawTile(final TMXTileLayer tileLayer, final int x, final int y, float offsetX, float offsetY) {
		if (tileLayer != null) {

			TMXMapTile mapTile = tileLayer.getTile(x, y);

			if (mapTile.getTileSetID() == -1) {
				return;
			}

			TMXTileSet tileSet = map.getTileset(mapTile.getTileSetID());
			TMXTile tile = tileSet.getTile(mapTile.getGID() - tileSet.getFirstGID());

			LTexture texture = textureMap.get(tileSet.getImage().getSource());

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

			_texBatch.draw(x + offsetX, y + offsetY, -1f, -1f, 0f, 0f, tileWidth, tileHeight, scaleX, scaleY,
					this._objectRotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY, baseColor);
		}

	}
}
