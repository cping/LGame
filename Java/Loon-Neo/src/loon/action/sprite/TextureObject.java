/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.LTexture;
import loon.action.map.Field2D;
import loon.action.map.Level;
import loon.action.map.TileMap;

public class TextureObject extends ActionObject {

	public final static TextureObject create(float x, float y, LTexture tex2d, Level tiles) {
		return create(x, y, tex2d, tiles.getMap());
	}

	public final static TextureObject create(float x, float y, String path, Level tiles) {
		return create(x, y, path, tiles.getMap());
	}

	public final static TextureObject create(float x, float y, LTexture tex2d, Field2D tiles) {
		if (tiles == null) {
			return new TextureObject(x, y, tex2d);
		}
		return new TextureObject(x, y, tiles.getTileWidth(), tiles.getTileHeight(), tex2d);
	}

	public final static TextureObject create(float x, float y, String path, Field2D tiles) {
		if (tiles == null) {
			return new TextureObject(x, y, path);
		}
		return new TextureObject(x, y, tiles.getTileWidth(), tiles.getTileHeight(), path);
	}

	public final static TextureObject create(float x, float y, LTexture tex2d, TileMap tiles) {
		if (tiles == null) {
			return new TextureObject(x, y, tex2d, tiles);
		}
		return new TextureObject(x, y, tiles.getTileWidth(), tiles.getTileHeight(), tex2d, tiles);
	}

	public final static TextureObject create(float x, float y, String path, TileMap tiles) {
		if (tiles == null) {
			return new TextureObject(x, y, path, tiles);
		}
		return new TextureObject(x, y, tiles.getTileWidth(), tiles.getTileHeight(), path, tiles);
	}

	public TextureObject(float x, float y, LTexture tex2d) {
		super(x, y, tex2d.width(), tex2d.height(), Animation.getDefaultAnimation(tex2d), null);
	}

	public TextureObject(float x, float y, float w, float h, LTexture tex2d) {
		super(x, y, w, h, Animation.getDefaultAnimation(tex2d), null);
	}

	public TextureObject(float x, float y, String file) {
		super(x, y, Animation.getDefaultAnimation(file), null);
	}

	public TextureObject(float x, float y, float w, float h, String file) {
		super(x, y, w, h, Animation.getDefaultAnimation(file), null);
	}

	public TextureObject(float x, float y, LTexture tex2d, TileMap map) {
		super(x, y, tex2d.width(), tex2d.height(), Animation.getDefaultAnimation(tex2d), map);
	}

	public TextureObject(float x, float y, String file, TileMap map) {
		super(x, y, Animation.getDefaultAnimation(file), map);
	}

	public TextureObject(float x, float y, float w, float h, LTexture tex2d, TileMap map) {
		super(x, y, w, h, Animation.getDefaultAnimation(tex2d), map);
	}

	public TextureObject(float x, float y, float w, float h, String file, TileMap map) {
		super(x, y, w, h, Animation.getDefaultAnimation(file), map);
	}
}
