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
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.ObjectMap;
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

	private int _width;

	private int _height;

	private LDTKMap _mapIn;

	private LColor _backgroundColor;

	private LDTKNeighbours _neighbours;

	public LDTKLevel(Json.Object root, LDTKTypes types, LDTKMap mapIn) {
		this._identifier = root.getString("identifier");
		this._uid = root.getInt("uid");
		this._backgroundColor = new LColor(root.getString("__bgColor"));
		this._x = root.getNumber("worldX");
		this._y = root.getNumber("worldY");
		this._width = root.getInt("pxWid");
		this._height = root.getInt("pxHei");
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
			layer = new LDTKTileLayer(_mapIn, layerJson, false);
			break;
		case LAYER_TYPE_INTGRID:
			_layerType = LDTKLayerType.IntGrid;
			layer = new LDTKTileLayer(_mapIn, layerJson, true);
			break;
		case LAYER_TYPE_ENTITY:
			_layerType = LDTKLayerType.Entities;
			layer = new LDTKEntityLayer(_mapIn, layerJson, types);
			break;
		default:
			_layerType = LDTKLayerType.IntGrid;
			layer = new LDTKTileLayer(_mapIn, layerJson, true);
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

	public LColor getBackgroundColor() {
		return this._backgroundColor;
	}

	public LDTKLayer getLayerByName(String name) {
		if (_layerNames.containsKey(name)) {
			return _layerNames.get(name);
		}
		throw new LSysException("Could not find layer with name " + name + " in level " + _identifier);
	}

	public void draw(GLEx g) {
		draw(g, 0f, 0f);
	}

	public void draw(GLEx g, float offsetX, float offsetY) {
		for (int i = _tileLayers.size - 1; i > -1; i--) {
			final LDTKTileLayer layer = _tileLayers.get(i);
			final float pixelX = layer.getPixelOffsetX();
			final float pixelY = layer.getPixelOffsetY();
			layer.draw(g, getX() + offsetX + pixelX, getY() + offsetY + pixelY);
		}
	}

	public Vector2f getWorldPosition(int idx) {
		LDTKLayer layer = _layers.get(idx);
		float offsetX = layer.getPixelOffsetX();
		float offsetY = layer.getPixelOffsetY();
		float posX = this._x;
		float posY = this._y;
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
		return x >= _x && y >= _y && x <= _x + _width && y <= _y + _height;
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
		return _x;
	}

	public float getY() {
		return _y;
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

	@Override
	public void close() {
		for (int i = 0; i < _tileLayers.size; i++) {
			_tileLayers.get(i).close();
		}
		_tileLayers.clear();
		_entityLayers.clear();
		_layers.clear();

	}
}
