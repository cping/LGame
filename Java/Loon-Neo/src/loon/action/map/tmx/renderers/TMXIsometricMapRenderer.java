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
import loon.geom.Vector2f;
import loon.opengl.BlendState;
import loon.opengl.GLEx;

/**
 * 标准斜视视角（标准45度角）地图纹理渲染器
 *
 */
public class TMXIsometricMapRenderer extends TMXMapRenderer {

	private Vector2f tempVector = new Vector2f();

	public TMXIsometricMapRenderer(TMXMap map) {
		super(map);
	}

	private Vector2f orthoToIso(float x, float y) {
		tempVector.x = (x - y) * map.getTileWidth() / 2 + _location.x;
		tempVector.y = (x + y) * map.getTileHeight() / 2 + _location.y;
		return tempVector.addSelf(map.getWidth() * map.getTileWidth() / 2, 0);
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
					/ map.getTileWidth() * 2f;
			float windowHeight = LSystem.viewSize.getHeight()
					/ map.getTileHeight() * 2f;
			float doubleWidth = tileLayer.getWidth() * 2f;
			float doubleHeight = tileLayer.getHeight() * 2f;

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
				for (int x = 0; x < tileLayer.getWidth(); x++) {
					for (int y = 0; y < tileLayer.getHeight(); y++) {

						if ((tx + x < 0) || (ty + y < 0)) {
							continue;
						}
						if ((tx + x >= doubleWidth) || (ty + y >= doubleHeight)) {
							continue;
						}
						if ((tx + x >= windowWidth) || (ty + y >= windowHeight)) {
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
								+ texture.xOff;
						float widthRatio = srcWidth * batch.getInvTexWidth();
						float yOff = srcY * batch.getInvTexHeight()
								+ texture.yOff;
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

						float uvCorrectionX = (0.5f / tileSet.getImage()
								.getWidth());
						float uvCorrectionY = (0.5f / tileSet.getImage()
								.getHeight());

						if (_rotation != 0f || scaleX != 1f || scaleY != 1f) {

							if (_rotation != 0f) {

								batch.glVertex2f(orthoToIso(x, y).addSelf(
										-tileWidth / 2, 0).newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(xOff + uvCorrectionX, yOff
										+ uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(flipZ ? tileWidth : 0,
												flipZ ? 0 : tileHeight)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(xOff + uvCorrectionX,
										heightRatio - uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(tileWidth, tileHeight)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(widthRatio - uvCorrectionX,
										heightRatio - uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(flipZ ? 0 : tileWidth,
												flipZ ? tileHeight : 0)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(widthRatio - uvCorrectionX,
										yOff + uvCorrectionY);

							} else if (scaleX != 1f || srcY != 1f) {

								batch.glVertex2f(orthoToIso(x, y).addSelf(
										-tileWidth / 2, 0).mul(scaleX, scaleY));
								batch.glColor4f();
								batch.glTexCoord2f(xOff + uvCorrectionX, yOff
										+ uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(flipZ ? tileWidth : 0,
												flipZ ? 0 : tileHeight)
										.mul(scaleX, scaleY));
								batch.glColor4f();
								batch.glTexCoord2f(xOff + uvCorrectionX,
										heightRatio - uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(tileWidth, tileHeight)
										.mul(scaleX, scaleY));
								batch.glColor4f();
								batch.glTexCoord2f(widthRatio - uvCorrectionX,
										heightRatio - uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(flipZ ? 0 : tileWidth,
												flipZ ? tileHeight : 0)
										.mul(scaleX, scaleY));
								batch.glColor4f();
								batch.glTexCoord2f(widthRatio - uvCorrectionX,
										yOff + uvCorrectionY);

							} else {
								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.mul(scaleX, scaleY)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(xOff + uvCorrectionX, yOff
										+ uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(flipZ ? tileWidth : 0,
												flipZ ? 0 : tileHeight)
										.mul(scaleX, scaleY)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(xOff + uvCorrectionX,
										heightRatio - uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(tileWidth, tileHeight)
										.mul(scaleX, scaleY)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(widthRatio - uvCorrectionX,
										heightRatio - uvCorrectionY);

								batch.glVertex2f(orthoToIso(x, y)
										.addSelf(-tileWidth / 2, 0)
										.addSelf(flipZ ? 0 : tileWidth,
												flipZ ? tileHeight : 0)
										.mul(scaleX, scaleY)
										.newRotate(_rotation));
								batch.glColor4f();
								batch.glTexCoord2f(widthRatio - uvCorrectionX,
										yOff + uvCorrectionY);

							}
						} else {
							batch.glVertex2f(orthoToIso(x, y).addSelf(
									-tileWidth / 2, 0));
							batch.glColor4f();
							batch.glTexCoord2f(xOff + uvCorrectionX, yOff
									+ uvCorrectionY);

							batch.glVertex2f(orthoToIso(x, y).addSelf(
									-tileWidth / 2, 0).addSelf(
									flipZ ? tileWidth : 0,
									flipZ ? 0 : tileHeight));
							batch.glColor4f();
							batch.glTexCoord2f(xOff + uvCorrectionX,
									heightRatio - uvCorrectionY);

							batch.glVertex2f(orthoToIso(x, y).addSelf(
									-tileWidth / 2, 0).addSelf(tileWidth,
									tileHeight));
							batch.glColor4f();
							batch.glTexCoord2f(widthRatio - uvCorrectionX,
									heightRatio - uvCorrectionY);

							batch.glVertex2f(orthoToIso(x, y).addSelf(
									-tileWidth / 2, 0).addSelf(
									flipZ ? 0 : tileWidth,
									flipZ ? tileHeight : 0));
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
