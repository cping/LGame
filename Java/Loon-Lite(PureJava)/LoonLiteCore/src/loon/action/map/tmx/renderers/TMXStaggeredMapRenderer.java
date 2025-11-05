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
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 斜视视角（45度角）交错地图纹理渲染器
 *
 */
public class TMXStaggeredMapRenderer extends TMXMapRenderer {

	public TMXStaggeredMapRenderer(TMXMap map) {
		super(map);
	}

	private Vector2f orthoToIso(float x, float y) {
		_mapLocation.x = (x - y) * map.getTileWidth() / 2 + getRenderX();
		_mapLocation.y = (x + y) * map.getTileHeight() / 2 + getRenderY();
		return _mapLocation.addSelf(map.getWidth() * map.getTileWidth() / 2, 0);
	}

	@Override
	protected void renderImageLayer(GLEx g, TMXImageLayer imageLayer) {
		if (!imageLayer.isVisible()) {
			return;
		}
		LTexture current = textureMap.get(imageLayer.getImage().getSource());
		float tileWidth = map.getTileWidth();
		float tileHeight = map.getTileHeight();
		float posX = (imageLayer.getRenderOffsetY() * tileWidth / 2) + (imageLayer.getRenderOffsetX() * tileWidth / 2)
				+ getRenderX();
		float posY = (imageLayer.getRenderOffsetX() * tileHeight / 2) - (imageLayer.getRenderOffsetY() * tileHeight / 2)
				+ getRenderY();
		g.draw(current, posX + _objectLocation.x, posY + _objectLocation.y, imageLayer.getWidth() * map.getTileWidth(),
				imageLayer.getHeight() * map.getTileHeight(), imageLayer.getTileLayerColor(baseColor));
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
			final int windowWidth = MathUtils.iceil(screenWidth / map.getTileWidth() / scaleX * 2f) + 1;
			final int windowHeight = MathUtils.iceil(screenHeight / map.getTileHeight() / scaleY * 2f) + 1;

			final int layerWidth = tileLayer.getWidth();
			final int layerHeight = tileLayer.getHeight();

			final float layerTileWidth = tileLayer.getTileWidth();
			final float layerTileHeight = tileLayer.getTileHeight();

			final float layerOffsetX = tileLayer.getRenderOffsetX() - (tileLayer.getParallaxX() - 1f);
			final float layerOffsetY = tileLayer.getRenderOffsetY() - (tileLayer.getParallaxY() - 1f);

			final boolean saveCache = textureMap.size == 1 && allowCache;

			LTexture current = textureMap.get(map.getTileset(0).getImage().getSource());
			LTextureBatch texBatch = current.getTextureBatch();

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

				texBatch.setColor(drawColor);

				for (int x = 0; x < tileLayer.getWidth(); x++) {
					for (int y = 0; y < tileLayer.getHeight(); y++) {
						if ((tx + x < -windowWidth) || (ty + y < -windowHeight)) {
							continue;
						}
						if ((x - tx > windowWidth) || (y - ty > windowHeight)) {
							continue;
						}

						TMXMapTile mapTile = tileLayer.getTile(x, y);

						if (mapTile == null || mapTile.getTileSetID() == -1) {
							continue;
						}

						TMXTileSet tileSet = map.getTileset(mapTile.getTileSetID());
						TMXTile tile = tileSet.getTile(mapTile.getGID() - tileSet.getFirstGID());

						LTexture texture = textureMap.get(tileSet.getImage().getSource());

						if (texture.getID() != current.getID()) {
							texBatch.end();
							current = texture;
							texBatch = current.getTextureBatch();
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

						float tileWidth = tileSet.getTileWidth();
						float tileHeight = tileSet.getTileHeight();

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
						Vector2f pos = orthoToIso(x, y);
						texBatch.draw(pos.x, pos.y, -1f, -1f, 0f, 0f, tileWidth, tileHeight, scaleX, scaleY,
								this._objectRotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);

					}
				}
			} finally {
				if (!isCached) {
					texBatch.end();
					if (saveCache) {
						saveCache(texBatch);
					}
				}
			}
		}
	}

	@Override
	public Vector2f pixelToTileCoords(float x, float y) {
		return pixelToTileCoords(x, y, map.getWidth() * (map.getTileWidth() / 2f));
	}

	public Vector2f pixelToTileCoords(float x, float y, float offset) {
		return pixelToTileCoords(x, y, offset, tempLocation);
	}

	public Vector2f pixelToTileCoords(float x, float y, Vector2f out) {
		return pixelToTileCoords(x, y, map.getWidth() * (map.getTileWidth() / 2f), out);
	}

	public Vector2f pixelToTileCoords(float x, float y, float offset, Vector2f out) {
		if (out == null) {
			out = new Vector2f();
		}
		x -= _objectLocation.x;
		y -= _objectLocation.y;
		out.x = this.pixelToTileX(x, y, offset);
		out.y = this.pixelToTileY(y, x, offset);
		return out;
	}

	public int pixelToTileX(float x, float y, float offset) {
		return (int) ((y / map.getTileHeight()) + ((x - offset) / map.getTileWidth()));
	}

	public int pixelToTileY(float y, float x, float offset) {
		return (int) ((y / map.getTileHeight()) - ((x - offset) / map.getTileWidth()));
	}

	@Override
	public Vector2f tileToPixelCoords(float tileX, float tileY) {
		return tileToPixelCoords(tileX, tileY, map.getWidth() * (map.getTileWidth() / 2f));
	}

	public Vector2f tileToPixelCoords(float tileX, float tileY, float offset) {
		return tileToPixelCoords(tileX, tileY, map.getTileWidth() / 2f, map.getTileHeight() / 2f, offset, tempLocation);
	}

	public Vector2f tileToPixelCoords(float tileX, float tileY, float hitWidth, float hitHeight, float offset) {
		return tileToPixelCoords(tileX, tileY, hitWidth, hitHeight, offset, tempLocation);
	}

	public Vector2f tileToPixelCoords(float tileX, float tileY, Vector2f out) {
		return tileToPixelCoords(tileX, tileY, map.getWidth() * (map.getTileWidth() / 2f), out);
	}

	public Vector2f tileToPixelCoords(float tileX, float tileY, float offset, Vector2f out) {
		return tileToPixelCoords(tileX, tileY, map.getTileWidth() / 2f, map.getTileHeight() / 2f, offset, out);
	}

	public Vector2f tileToPixelCoords(float tileX, float tileY, float hitWidth, float hitHeight, float offset,
			Vector2f out) {
		if (out == null) {
			out = new Vector2f();
		}
		out.x = (tileX - tileY) * hitWidth + offset;
		out.y = (tileX + tileY) * hitHeight;
		return out;
	}
}
