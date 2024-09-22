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
package loon.action.map.ldtk;

import loon.Json;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.opengl.GLEx;
import loon.utils.StringUtils;

public class LDTKTileLayer extends LDTKLayer implements LRelease {

	private LDTKTile[] _mapTiles;

	private LTexture _mapTexture;

	private LDTKMap _map;

	public LDTKTileLayer(LDTKMap map, Json.Object v, boolean intGrid) {
		super(v);
		this._map = map;
		String tilesetRelPath = v.getString("__tilesetRelPath");
		if (!StringUtils.isEmpty(map.getDir()) && tilesetRelPath.indexOf(map.getDir()) == -1) {
			tilesetRelPath = map.getDir() + LSystem.FS + tilesetRelPath;
		}
		this._mapTexture = LTextures.loadTexture(tilesetRelPath);
		Json.Array tiles = v.getArray(intGrid ? "autoLayerTiles" : "gridTiles");
		this._mapTiles = new LDTKTile[tiles.length()];
		for (int i = 0; i < tiles.length(); i++) {
			Json.Object tileValue = tiles.getObject(i);
			Json.Array pixelPosition = tileValue.getArray("px");
			Json.Array sourcePosition = tileValue.getArray("src");
			final int flipFlags = tileValue.getInt("f");
			boolean flipX = false, flipY = false;
			switch (flipFlags) {
			case 0:
				flipX = flipY = false;
				break;
			case 1:
				flipX = true;
				flipY = false;
			case 2:
				flipX = false;
				flipY = true;
			default:
				flipX = flipY = true;
				break;
			}
			float posX = sourcePosition.getNumber(0);
			float posY = sourcePosition.getNumber(1);
			LTexture tile = _mapTexture.sub(posX, posY, _gridSize, _gridSize);
			float pixelX = pixelPosition.getNumber(0);
			float pixelY = pixelPosition.getNumber(1);
			_mapTiles[i] = new LDTKTile(tile, pixelX, pixelY, _gridSize, _gridSize);
			_mapTiles[i].flip(flipX, flipY);
		}
	}

	public void draw(GLEx g) {
		draw(g, 0, 0);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		final float old = g.alpha();
		g.setAlpha(_opacity);
		for (int i = _mapTiles.length - 1; i > -1; i--) {
			LDTKTile tile = _mapTiles[i];
			if (tile != null) {
				g.draw(tile.getTexture(), tile.getX() + offsetX, tile.getY() + offsetY, tile.getDirection());

			}
		}
		g.setAlpha(old);
	}

	public LDTKMap getMap() {
		return _map;
	}

	public LDTKTile[] getTiles() {
		return _mapTiles;
	}

	public LTexture getTilemapTexture() {
		return _mapTexture;
	}

	@Override
	public void close() {
		if (_mapTexture != null) {
			_mapTexture.close(true);
		}
	}
}
