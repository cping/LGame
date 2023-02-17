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
import loon.LTextureBatch;
import loon.action.map.tmx.TMXImageLayer;
import loon.action.map.tmx.TMXMap;
import loon.action.map.tmx.TMXMap.StaggerAxis;
import loon.action.map.tmx.TMXMap.StaggerIndex;
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.TMXTileSet;
import loon.action.map.tmx.tiles.TMXMapTile;
import loon.action.map.tmx.tiles.TMXTile;
import loon.opengl.BlendState;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 六角形(六边形)地图纹理渲染器
 */
public class TMXHexagonalMapRenderer extends TMXMapRenderer {

	private LTexture texCurrent;

	private LTextureBatch texBatch;

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

			final boolean onlyTexture = textureMap.size == 1;

			texCurrent = textureMap.get(map.getTileset(0).getImage().getSource());
			texBatch = texCurrent.getTextureBatch();

			final float tmpAlpha = baseColor.a;
			boolean isCached = false;
			baseColor.a *= opacity;

			try {

				if (onlyTexture) {
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

					if (hashCode != lastHashCode) {
						lastHashCode = hashCode;
						texBatch.disposeLastCache();
						texBatch.begin();
					} else {
						if (texBatch.existCache()) {
							texBatch.setBlendState(BlendState.AlphaBlend);
							texBatch.postCache(baseColor, 0);
							isCached = true;
							return;
						} else {
							texBatch.begin();
						}
					}

				} else {
					texBatch.begin();
				}
				texBatch.setBlendState(BlendState.AlphaBlend);
				texBatch.setColor(baseColor);

				if (staggerAxisX) {
					final float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2 ;
					final float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2;
					final float layerTileHeight50 = layerTileHeight * 0.5f;

					final int ya = MathUtils.max(0,
							(int) ((0f - layerTileHeight50 - layerOffsetX) / layerTileHeight));
					final int yb = MathUtils.min(layerHeight,
							(int) ((0f + screenHeight + layerTileHeight - layerOffsetX) / layerTileHeight));

					final int xa = MathUtils.max(0,
							(int) (((0f - tileWidthLowerCorner - layerOffsetY) / tileWidthUpperCorner)));
					final int xb = MathUtils.min(layerWidth,
							(int) ((0f + screenWidth + tileWidthUpperCorner - layerOffsetY) / tileWidthUpperCorner));

					final int maxXa = (staggerIndexEven == ( xa % 2 == 0)) ?  xa + 1 :  xa;
					final int maxXb = (staggerIndexEven == ( xa % 2 == 0)) ?  xa :  xa + 1;

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
					texBatch.end();
					if (onlyTexture) {
						texBatch.newCache();
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

			if (texture.getID() != texCurrent.getID()) {
				texBatch.end();
				texCurrent = texture;
				texBatch = texCurrent.getTextureBatch();
				texBatch.begin();
				texBatch.setBlendState(BlendState.AlphaBlend);
				texBatch.checkTexture(texCurrent);
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

			float xOff = srcX * texBatch.getInvTexWidth() + texture.xOff();
			float widthRatio = srcWidth * texBatch.getInvTexWidth();
			float yOff = srcY * texBatch.getInvTexHeight() + texture.yOff();
			float heightRatio = srcHeight * texBatch.getInvTexHeight();

			boolean flipX = mapTile.isFlippedHorizontally();
			boolean flipY = mapTile.isFlippedVertically();
			boolean flipZ = mapTile.isFlippedDiagonally();

			if (flipZ) {
				flipX = !flipX;
				flipY = !flipY;
			}

			if (flipX) {
				float temp = xOff;
				xOff = widthRatio;
				widthRatio = temp;
			}

			if (flipY) {
				float temp = yOff;
				yOff = heightRatio;
				heightRatio = temp;
			}

			float nx1 = offsetX * scaleX;
			float ny1 = offsetY * scaleY;

			float nx2 = (nx1 + tileWidth) * scaleX;
			float ny2 = (ny1 + tileHeight) * scaleY;

			float uvCorrectionX = (0.2f / tileSet.getImage().getWidth());
			float uvCorrectionY = (0.2f / tileSet.getImage().getHeight());

			if (_objectRotation != 0f || scaleX != 1f || scaleY != 1f) {

				float originX = tileWidth / 2;
				float originY = tileHeight / 2;
				final float worldOriginX = nx1 + tileWidth / 2;
				final float worldOriginY = ny1 + tileHeight / 2;
				float fx = -originX;
				float fy = -originY;
				float fx2 = tileWidth - originX;
				float fy2 = tileHeight - originY;

				if (scaleX != 1 || scaleY != 1) {
					fx *= scaleX;
					fy *= scaleY;
					fx2 *= scaleX;
					fy2 *= scaleY;
				}

				final float p1x = fx;
				final float p1y = fy;
				final float p2x = fx;
				final float p2y = fy2;
				final float p3x = fx2;
				final float p3y = fy2;
				final float p4x = fx2;
				final float p4y = fy;

				float x1;
				float y1;
				float x2;
				float y2;
				float x3;
				float y3;
				float x4;
				float y4;

				if (_objectRotation != 0) {
					final float cos = MathUtils.cosDeg(_objectRotation);
					final float sin = MathUtils.sinDeg(_objectRotation);

					x1 = cos * p1x - sin * p1y;
					y1 = sin * p1x + cos * p1y;

					x2 = cos * p2x - sin * p2y;
					y2 = sin * p2x + cos * p2y;

					x3 = cos * p3x - sin * p3y;
					y3 = sin * p3x + cos * p3y;

					x4 = x1 + (x3 - x2);
					y4 = y3 - (y2 - y1);
				} else {
					x1 = p1x;
					y1 = p1y;

					x2 = p2x;
					y2 = p2y;

					x3 = p3x;
					y3 = p3y;

					x4 = p4x;
					y4 = p4y;
				}

				x1 += worldOriginX;
				y1 += worldOriginY;
				x2 += worldOriginX;
				y2 += worldOriginY;
				x3 += worldOriginX;
				y3 += worldOriginY;
				x4 += worldOriginX;
				y4 += worldOriginY;

				if (flipZ) {
					x2 += tileWidth;
					y2 += tileHeight;
					x4 += tileWidth;
					y4 += tileHeight;
				}

				texBatch.glVertex2f(x1, y1);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(xOff + uvCorrectionX, yOff + uvCorrectionY);

				texBatch.glVertex2f(x2, y2);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(xOff + uvCorrectionX, heightRatio - uvCorrectionY);

				texBatch.glVertex2f(x3, y3);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(widthRatio - uvCorrectionX, heightRatio - uvCorrectionY);

				texBatch.glVertex2f(x4, y4);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(widthRatio - uvCorrectionX, yOff + uvCorrectionY);

			} else {
				texBatch.glVertex2f(nx1, ny1);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(xOff + uvCorrectionX, yOff + uvCorrectionY);

				texBatch.glVertex2f(nx1, ny2);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(xOff + uvCorrectionX, heightRatio - uvCorrectionY);

				texBatch.glVertex2f(nx2, ny2);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(widthRatio - uvCorrectionX, heightRatio - uvCorrectionY);

				texBatch.glVertex2f(nx2, ny1);
				texBatch.glColor4f();
				texBatch.glTexCoord2f(widthRatio - uvCorrectionX, yOff + uvCorrectionY);
			}

		}
	}
}
