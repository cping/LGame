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
import loon.action.map.tmx.TMXTileLayer;
import loon.action.map.tmx.TMXTileSet;
import loon.action.map.tmx.tiles.TMXMapTile;
import loon.action.map.tmx.tiles.TMXTile;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 直角(2D平面)地图纹理渲染器
 */
public class TMXOrthogonalMapRenderer extends TMXMapRenderer {

	public TMXOrthogonalMapRenderer(TMXMap map) {
		super(map);
	}

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
		LTexture originalTexture = textureMap.get(imageLayer.getImage().getSource());
		g.draw(originalTexture, imageLayer.getRenderOffsetX(), imageLayer.getRenderOffsetY(),
				imageLayer.getWidth() * map.getTileWidth(), imageLayer.getHeight() * map.getTileHeight(), baseColor);
		baseColor.a = tmpAlpha;
	}

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
			final int windowWidth = (int) (screenWidth / map.getTileWidth() / scaleX);
			final int windowHeight = (int) (screenHeight / map.getTileHeight() / scaleY);

			final int layerWidth = tileLayer.getWidth();
			final int layerHeight = tileLayer.getHeight();

			final float layerTileWidth = tileLayer.getTileWidth();
			final float layerTileHeight = tileLayer.getTileHeight();

			final float layerOffsetX = tileLayer.getRenderOffsetX() - (tileLayer.getParallaxX() - 1f);
			final float layerOffsetY = tileLayer.getRenderOffsetY() - (tileLayer.getParallaxY() - 1f);

			final boolean saveCache = textureMap.size == 1 && allowCache;

			LTexture current = textureMap.get(map.getTileset(0).getImage().getSource());
			LTextureBatch texBatch = null;// current.getTextureBatch();

			float tmpAlpha = baseColor.a;
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
					hashCode = LSystem.unite(hashCode, scaleX);
					hashCode = LSystem.unite(hashCode, scaleY);
					hashCode = LSystem.unite(hashCode, tileLayer.isDirty());
					hashCode = LSystem.unite(hashCode, _objectRotation);

					if (isCached = postCache(texBatch, hashCode)) {
						return;
					}

				} else {
					texBatch.begin();
				}

				texBatch.setColor(baseColor);
				for (int x = 0; x < layerWidth; x++) {
					for (int y = 0; y < layerHeight; y++) {
						if ((tx + x < 0) || (ty + y < 0)) {
							continue;
						}
						if ((tx + x >= layerWidth) || (ty + y >= layerHeight)) {
							continue;
						}
						if ((x >= windowWidth) || (y >= windowHeight)) {
							continue;
						}
						TMXMapTile mapTile = tileLayer.getTile(x, y);

						if (mapTile.getTileSetID() == -1) {
							continue;
						}

						TMXTileSet tileSet = map.getTileset(mapTile.getTileSetID());
						TMXTile tile = tileSet.getTile(mapTile.getGID() - tileSet.getFirstGID());

						LTexture texture = textureMap.get(tileSet.getImage().getSource());

						if (texture.getID() != current.getID()) {
							texBatch.end();
							current = texture;
							texBatch = null;//current.getTextureBatch();
							texBatch.begin();
		
							texBatch.checkTexture(current);
						}

						int tileID = mapTile.getGID() - tileSet.getFirstGID();

						if (tile != null && tile.isAnimated()) {
							tileID = tileAnimators.get(tile).getCurrentFrame().getTileID();
						}

						int numColsPerRow = tileSet.getImage().getWidth() / tileSet.getTileWidth();

						int tileSetCol = tileID % numColsPerRow;
						int tileSetRow = tileID / numColsPerRow;

						float tileWidth = map.getTileWidth();
						float tileHeight = map.getTileHeight();

						float posX = (x * tileWidth + getRenderX()) * scaleX;
						float posY = (y * tileHeight + getRenderY()) * scaleY;

						float srcX = (tileSet.getMargin()
								+ (tileSet.getTileWidth() + tileSet.getSpacing()) * tileSetCol);
						float srcY = (tileSet.getMargin()
								+ (tileSet.getTileHeight() + tileSet.getSpacing()) * tileSetRow);
						float srcWidth = srcX + tileWidth;
						float srcHeight = srcY + tileHeight;


						boolean flipX = mapTile.isFlippedHorizontally();
						boolean flipY = mapTile.isFlippedVertically();
						boolean flipZ = mapTile.isFlippedDiagonally();

						if (flipZ) {
							flipX = !flipX;
							flipY = !flipY;
						}


						float uvCorrectionX = (0.2f / tileSet.getImage().getWidth());
						float uvCorrectionY = (0.2f / tileSet.getImage().getHeight());

						if (_objectRotation != 0f || scaleX != 1f || scaleY != 1f) {

							float originX = tileWidth / 2;
							float originY = tileHeight / 2;
							final float worldOriginX = posX + tileWidth / 2;
							final float worldOriginY = posY + tileHeight / 2;
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

						} else {}

					}
				}
			} finally {
				if (!isCached) {
					texBatch.end();
					if (saveCache) {
						saveCache(texBatch);
					}
				}
				baseColor.a = tmpAlpha;
			}
		}

	}

}
