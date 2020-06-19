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
import loon.opengl.BlendState;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 六角形(六边形)地图纹理渲染器
 */
public class TMXHexagonalMapRenderer extends TMXMapRenderer {

	public TMXHexagonalMapRenderer(TMXMap map) {
		super(map);
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
		float posX = (imageLayer.getY() * tileWidth / 2)
				+ (imageLayer.getX() * tileWidth / 2) + _location.x;
		float posY = (imageLayer.getX() * tileHeight / 2)
				- (imageLayer.getY() * tileHeight / 2) + _location.y;
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

			int tx = _location.x() / map.getTileWidth();
			int ty = _location.y() / map.getTileHeight();
			float windowWidth = LSystem.viewSize.getWidth()
					/ map.getTileWidth() * 3f;
			float windowHeight = LSystem.viewSize.getHeight()
					/ map.getTileHeight() * 3f;
			float threeWidth = tileLayer.getWidth() * 3f;
			float threeHeight = tileLayer.getHeight() * 3f;

			boolean onlyTexture = textureMap.size == 1;

			LTexture current = textureMap.get(map.getTileset(0).getImage()
					.getSource());
			LTextureBatch batch = current.getTextureBatch();

			float tmpAlpha = baseColor.a;
			boolean cache = false;
			baseColor.a *= opacity;

			try {

				if (onlyTexture) {
					int hashCode = 1;
					hashCode = LSystem.unite(hashCode, tx);
					hashCode = LSystem.unite(hashCode, ty);
					hashCode = LSystem.unite(hashCode, windowWidth);
					hashCode = LSystem.unite(hashCode, windowHeight);
					hashCode = LSystem.unite(hashCode, scaleX);
					hashCode = LSystem.unite(hashCode, scaleY);
					hashCode = LSystem.unite(hashCode, _rotation);

					if (hashCode != lastHashCode) {
						lastHashCode = hashCode;
						batch.disposeLastCache();
						batch.begin();
					} else {
						if (batch.existCache()) {
							batch.setBlendState(BlendState.AlphaBlend);
							batch.postCache(baseColor, 0);
							cache = true;
							return;
						} else {
							batch.begin();
						}
					}

				} else {
					batch.begin();
				}
				batch.setBlendState(BlendState.AlphaBlend);
				batch.setColor(baseColor);

				for (int y = 0; y < tileLayer.getHeight(); y++) {
					for (int x = 0; x < tileLayer.getWidth(); x++) {

						if ((tx + x < 0) || (ty + y < 0)) {
							continue;
						}
						if ((tx + x >= threeWidth) || (ty + y >= threeHeight)) {
							continue;
						}
						if ((tx + x - 1 >= windowWidth)
								|| (ty + y - 1 >= windowHeight)) {
							continue;
						}

						TMXMapTile mapTile = tileLayer.getTile(x, y);

						if (mapTile.getTileSetID() == -1) {
							continue;
						}

						TMXTileSet tileSet = map.getTileset(mapTile
								.getTileSetID());
						TMXTile tile = tileSet.getTile(mapTile.getGID()
								- tileSet.getFirstGID());

						LTexture texture = textureMap.get(tileSet.getImage()
								.getSource());

						if (texture.getID() != current.getID()) {
							batch.end();
							current = texture;
							batch = current.getTextureBatch();
							batch.begin();
							batch.setBlendState(BlendState.AlphaBlend);
							batch.checkTexture(current);
						}

						int tileID = mapTile.getGID() - tileSet.getFirstGID();
						if (tile != null && tile.isAnimated()) {
							tileID = tileAnimators.get(tile).getCurrentFrame()
									.getTileID();
						}

						int numColsPerRow = tileSet.getImage().getWidth()
								/ tileSet.getTileWidth();

						int tileSetCol = tileID % numColsPerRow;
						int tileSetRow = tileID / numColsPerRow;

						float tileWidth = tileSet.getTileWidth();
						float tileHeight = tileSet.getTileHeight();

						float srcX = (tileSet.getMargin() + (tileSet
								.getTileWidth() + tileSet.getSpacing())
								* tileSetCol);
						float srcY = (tileSet.getMargin() + (tileSet
								.getTileHeight() + tileSet.getSpacing())
								* tileSetRow);
						float srcWidth = srcX + tileWidth;
						float srcHeight = srcY + tileHeight;

						float xOff = srcX * batch.getInvTexWidth()
								+ texture.xOff();
						float widthRatio = srcWidth * batch.getInvTexWidth();
						float yOff = srcY * batch.getInvTexHeight()
								+ texture.yOff();
						float heightRatio = srcHeight * batch.getInvTexHeight();

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

						float nx = x;
						if (y % 2 == 1) {
							nx = x + 0.5f;
						}

						float nx1 = _location.x + nx * tileWidth * 0.75f
								* scaleX;
						float ny1 = _location.y + y * tileWidth * 0.5f * scaleY;

						float nx2 = (nx1 + tileWidth) * scaleX;
						float ny2 = (ny1 + tileHeight) * scaleY;

						float uvCorrectionX = (0.2f / tileSet.getImage()
								.getWidth());
						float uvCorrectionY = (0.2f / tileSet.getImage()
								.getHeight());

						if (_rotation != 0f || scaleX != 1f || scaleY != 1f) {

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

							if (_rotation != 0) {
								final float cos = MathUtils.cosDeg(_rotation);
								final float sin = MathUtils.sinDeg(_rotation);

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

							batch.glVertex2f(x1, y1);
							batch.glColor4f();
							batch.glTexCoord2f(xOff + uvCorrectionX, yOff
									+ uvCorrectionY);

							batch.glVertex2f(x2, y2);
							batch.glColor4f();
							batch.glTexCoord2f(xOff + uvCorrectionX,
									heightRatio - uvCorrectionY);

							batch.glVertex2f(x3, y3);
							batch.glColor4f();
							batch.glTexCoord2f(widthRatio - uvCorrectionX,
									heightRatio - uvCorrectionY);

							batch.glVertex2f(x4, y4);
							batch.glColor4f();
							batch.glTexCoord2f(widthRatio - uvCorrectionX, yOff
									+ uvCorrectionY);

						} else {
							batch.glVertex2f(nx1, ny1);
							batch.glColor4f();
							batch.glTexCoord2f(xOff + uvCorrectionX, yOff
									+ uvCorrectionY);

							batch.glVertex2f(nx1, ny2);
							batch.glColor4f();
							batch.glTexCoord2f(xOff + uvCorrectionX,
									heightRatio - uvCorrectionY);

							batch.glVertex2f(nx2, ny2);
							batch.glColor4f();
							batch.glTexCoord2f(widthRatio - uvCorrectionX,
									heightRatio - uvCorrectionY);

							batch.glVertex2f(nx2, ny1);
							batch.glColor4f();
							batch.glTexCoord2f(widthRatio - uvCorrectionX, yOff
									+ uvCorrectionY);
						}

					}
				}
			} finally {
				if (!cache) {
					batch.end();
					if (onlyTexture) {
						batch.newCache();
					}
				}
				baseColor.a = tmpAlpha;
			}
		}
	}

}
