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
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class LDTKLevel implements LRelease {

	public static final String LAYER_TYPE_TILES = "Tiles";

	public static final String LAYER_TYPE_INTGRID = "IntGrid";

	public static final String LAYER_TYPE_ENTITY = "Entities";

	public static final String LAYER_TYPE_AUTOLAYER = "AutoLayer";

	private LDTKLayerType _layerType;

	private String _identifier;

	private TArray<LDTKLayer> _layers;

	private TArray<LDTKEntityLayer> _entityLayers;

	private TArray<LDTKTileLayer> _tileLayers;

	private ObjectMap<String, LDTKLayer> _layerNames;

	private int _uid;

	private float _x;

	private float _y;

	private float _bgPivotX;

	private float _bgPivotY;

	private float _posX;

	private float _posY;

	private int _width;

	private int _height;

	private int _depth;

	private LDTKMap _mapIn;

	private LColor _backgroundColor;

	private LColor _smartColor;

	private LDTKNeighbours _neighbours;

	private String _bgRelPath;

	private String _bgPosMode;

	private LTexture _bgTexture;

	private boolean _useBg;

	private boolean _useImageBackground;

	private LDTKBackgroundPos _bgPos;

	public LDTKLevel(Json.Object root, LDTKTypes types, LDTKMap mapIn) {
		this._identifier = root.getString("identifier");
		this._uid = root.getInt("uid");
		this._backgroundColor = new LColor(root.getString("__bgColor"));
		this._x = root.getNumber("worldX");
		this._y = root.getNumber("worldY");
		this._depth = root.getInt("worldDepth");
		this._width = root.getInt("pxWid");
		this._height = root.getInt("pxHei");
		this._bgRelPath = root.getString("bgRelPath");
		this._bgPosMode = root.getString("bgPos");
		this._bgPivotX = root.getNumber("bgPivotX");
		this._bgPivotY = root.getNumber("bgPivotY");
		this._smartColor = new LColor(root.getString("__smartColor"));
		if (!StringUtils.isEmpty(mapIn.getDir()) && _bgRelPath.indexOf(mapIn.getDir()) == -1) {
			_bgRelPath = mapIn.getDir() + LSystem.FS + _bgRelPath;
		}
		if (!StringUtils.isEmpty(PathUtils.getExtension(_bgRelPath))) {
			_useBg = true;
		}
		this._bgPos = new LDTKBackgroundPos(root.getObject("__bgPos"));
		this._useImageBackground = _bgPos._supportBackground;
		this._mapIn = mapIn;
		this._layers = new TArray<LDTKLayer>();
		this._entityLayers = new TArray<LDTKEntityLayer>();
		this._tileLayers = new TArray<LDTKTileLayer>();
		this._neighbours = new LDTKNeighbours();
		this._layerNames = new ObjectMap<String, LDTKLayer>();
		Json.Array layersJson = root.getArray("layerInstances");
		for (int i = 0; i < layersJson.length(); i++) {
			parseLayer(layersJson.getObject(i), types);
		}
		parseNeighbours(root.getArray("__neighbours"));
	}

	private void parseLayer(Json.Object layerJson, LDTKTypes types) {
		LDTKLayer layer;
		switch (layerJson.getString("__type")) {
		case LAYER_TYPE_TILES:
			_layerType = LDTKLayerType.Tiles;
			layer = new LDTKTileLayer(_mapIn, this, layerJson, false);
			break;
		case LAYER_TYPE_INTGRID:
			_layerType = LDTKLayerType.IntGrid;
			layer = new LDTKTileLayer(_mapIn, this, layerJson, true);
			break;
		case LAYER_TYPE_ENTITY:
			_layerType = LDTKLayerType.Entities;
			layer = new LDTKEntityLayer(_mapIn, this, layerJson, types);
			break;
		default:
			_layerType = LDTKLayerType.IntGrid;
			layer = new LDTKTileLayer(_mapIn, this, layerJson, true);
			return;
		}
		_layers.add(layer);
		if (layer instanceof LDTKTileLayer) {
			_tileLayers.add((LDTKTileLayer) layer);
		} else {
			_entityLayers.add((LDTKEntityLayer) layer);
		}
		_layerNames.put(layer.getId(), layer);
	}

	public LDTKBackgroundPos getBackgroundPos() {
		return this._bgPos;
	}

	public LColor getSmartColor() {
		return this._smartColor;
	}

	public float getBackgroundPivotX() {
		return this._bgPivotX;
	}

	public float getBackgroundPivotY() {
		return this._bgPivotY;
	}

	public String getBackgroundPosMode() {
		return this._bgPosMode;
	}

	public int getGridSize(int idx) {
		return _layers.get(idx)._gridSize;
	}

	public LDTKLayerType getLayerType() {
		return this._layerType;
	}

	private void parseNeighbours(Json.Array neighboursJson) {
		for (int i = 0; i < neighboursJson.length(); i++) {
			Json.Object neighbourJson = neighboursJson.getObject(i);
			_neighbours.add(neighbourJson.getInt("levelUid"), neighbourJson.getString("dir").charAt(0));
		}
	}

	public String getBackgroundRelPath() {
		return this._bgRelPath;
	}

	public LColor getBackgroundColor() {
		return this._backgroundColor;
	}

	public TArray<LDTKLayer> getLayersByName(String name) {
		TArray<LDTKLayer> list = new TArray<LDTKLayer>();
		for (Entries<String, LDTKLayer> it = _layerNames.entries(); it.hasNext();) {
			Entry<String, LDTKLayer> o = it.next();
			if (name.equals(o.getKey()) && o != null) {
				list.add(o.getValue());
			}
		}
		return list;
	}

	public TArray<String> getLayerNames() {
		TArray<String> list = new TArray<String>();
		for (Entries<String, LDTKLayer> it = _layerNames.entries(); it.hasNext();) {
			Entry<String, LDTKLayer> o = it.next();
			if (o != null) {
				list.add(o.getKey());
			}
		}
		return list;
	}

	public LDTKLayer getLayerByName(String name) {
		if (_layerNames.containsKey(name)) {
			return _layerNames.get(name);
		}
		throw new LSysException("Could not find layer with name " + name + " in level " + _identifier);
	}

	public boolean isUseImageBackground() {
		return _useImageBackground;
	}

	public LDTKLevel setUseImageBackground(boolean ib) {
		this._useImageBackground = ib;
		return this;
	}

	public void draw(GLEx g) {
		draw(g, 0f, 0f);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		if (_useImageBackground && _bgPos._supportBackground) {
			LDTKBackgroundPos pos = _bgPos;
			g.draw(getBackgroundTexture(), offsetX + pos._top + getX(), offsetY + pos._left + getY(),
					getWidth() * pos._scale.x, getHeight() * pos._scale.y, pos._cropRect.x, pos._cropRect.y,
					pos._cropRect.width, pos._cropRect.height);
		}
		for (int i = _tileLayers.size - 1; i > -1; i--) {
			final LDTKTileLayer layer = _tileLayers.get(i);
			final float pixelX = layer.getPixelOffsetX();
			final float pixelY = layer.getPixelOffsetY();
			layer.draw(g, getX() + offsetX + pixelX, getY() + offsetY + pixelY);
		}
	}

	public LTexture getBackgroundTexture() {
		if (_useBg) {
			if (_bgTexture == null) {
				_bgTexture = LTextures.loadTexture(_bgRelPath);
			}
		}
		return _bgTexture;
	}

	public int getDepth() {
		return _depth;
	}

	public Vector2f getWorldPosition(int idx) {
		LDTKLayer layer = _layers.get(idx);
		float offsetX = layer.getPixelOffsetX();
		float offsetY = layer.getPixelOffsetY();
		float posX = this.getX();
		float posY = this.getY();
		float mapX = _mapIn.getPosX();
		float mapY = _mapIn.getPosY();
		return new Vector2f(mapX + offsetX + posX, mapY + offsetY + posY);
	}

	public boolean contains(XY point) {
		if (point == null) {
			return false;
		}
		return contains(point.getX(), point.getY());
	}

	public boolean contains(float x, float y) {
		return x >= getX() && y >= getY() && x <= getX() + _width && y <= getY() + _height;
	}

	public String getIdentifier() {
		return _identifier;
	}

	public int getUid() {
		return _uid;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public float getX() {
		return _x + _posX;
	}

	public float getY() {
		return _y + _posY;
	}

	public LDTKLevel setPosX(float x) {
		this._posX = x;
		return this;
	}

	public LDTKLevel setPosY(float y) {
		this._posY = y;
		return this;
	}

	public LDTKLevel pos(float x, float y) {
		setPosX(x);
		setPosY(y);
		return this;
	}

	public float getFlippedX() {
		return _mapIn.getMaxWidth() - (_x + _width);
	}

	public float getFlippedY() {
		return _mapIn.getMaxHeight() - (_y + _height);
	}

	public TArray<LDTKEntityLayer> getEntityLayers() {
		return _entityLayers;
	}

	public TArray<LDTKLayer> getLayers() {
		return _layers;
	}

	public TArray<LDTKTileLayer> getTileLayers() {
		return _tileLayers;
	}

	public LDTKNeighbours getNeighbours() {
		return _neighbours;
	}

	public LDTKLayer getLayer(int layerID) {
		return _tileLayers.get(layerID);
	}

	public int getPixelsAtFieldType(int layerID, float x, float y) {
		LDTKLayer layer = _tileLayers.get(layerID);
		if (layer instanceof LDTKTileLayer) {
			return ((LDTKTileLayer) layer).getPixelsAtFieldType(x, y);
		}
		return -1;
	}

	public int getTilesAtFieldType(int layerID, int x, int y) {
		LDTKLayer layer = _tileLayers.get(layerID);
		if (layer instanceof LDTKTileLayer) {
			return ((LDTKTileLayer) layer).getTilesAtFieldType(x, y);
		}
		return -1;
	}

	public LDTKTile getPixelsAtTile(int layerID, float x, float y) {
		LDTKLayer layer = _tileLayers.get(layerID);
		if (layer instanceof LDTKTileLayer) {
			return ((LDTKTileLayer) layer).getPixelsAtTile(x, y);
		}
		return null;
	}

	public LDTKTile getTilePosAtTile(int layerID, int x, int y) {
		LDTKLayer layer = _tileLayers.get(layerID);
		if (layer instanceof LDTKTileLayer) {
			return ((LDTKTileLayer) layer).getTilePosAtTile(x, y);
		}
		return null;
	}

	@Override
	public void close() {
		for (int i = 0; i < _tileLayers.size; i++) {
			_tileLayers.get(i).close();
		}
		_tileLayers.clear();
		_entityLayers.clear();
		_layers.clear();
		if (_bgTexture != null) {
			_bgTexture.close();
			_bgTexture = null;
		}
		_useBg = false;
	}
}
