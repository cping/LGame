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
package loon.action.map.tmx;

import loon.Json;
import loon.LSysException;
import loon.LSystem;
import loon.action.map.Field2D;
import loon.action.map.tmx.renderers.TMXHexagonalMapRenderer;
import loon.action.map.tmx.renderers.TMXIsometricMapRenderer;
import loon.action.map.tmx.renderers.TMXMapRenderer;
import loon.action.map.tmx.renderers.TMXOrthogonalMapRenderer;
import loon.action.map.tmx.renderers.TMXStaggeredMapRenderer;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.geom.Sized;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.res.TextResource;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class TMXMap implements Sized {

	/**
	 * 该TiledMap类的渲染（瓦片显示方向）模式
	 */
	public static enum Orientation {
		ORTHOGONAL, ISOMETRIC, STAGGERED, HEXAGONAL
	}

	/**
	 * 该TiledMap类的渲染呈现顺序
	 */
	public static enum RenderOrder {
		RIGHT_DOWN, RIGHT_UP, LEFT_DOWN, LEFT_UP
	}

	/**
	 * 此地图的交错轴（地图不是六角形时生效）
	 */
	public static enum StaggerAxis {
		AXIS_X, AXIS_Y, NONE
	}

	/**
	 * 此地图的错开模式。适用于六边形和等距交错地图。
	 */
	public static enum StaggerIndex {
		NONE, EVEN, ODD
	}

	public static final long FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
	public static final long FLIPPED_VERTICALLY_FLAG = 0x40000000;
	public static final long FLIPPED_DIAGONALLY_FLAG = 0x20000000;

	private String _filePath;
	private String _tilesLocation;
	private String _tmxType;
	private LColor _backgroundColor;

	private double _version;
	private double _tiledversion;

	private boolean _renderOffsetDirty;

	private Orientation _orientation;
	private RenderOrder _renderOrder;
	private StaggerAxis _staggerAxis;
	private StaggerIndex _staggerIndex;

	private int _width;
	private int _height;

	private int _tileWidth;
	private int _tileHeight;

	private int _tileWidthHalf;
	private int _tileHeightHalf;

	private int _widthInPixels;
	private int _heightInPixels;
	private int _infinite;
	private int _nextObjectID;
	private int _nextlayerid;
	private int _hexSideLength;

	private float _offsetX;
	private float _offsetY;
	private float _renderOffsetX;
	private float _renderOffsetY;

	private TArray<TMXMapLayer> _layers;
	private TArray<TMXTileLayer> _tileLayers;
	private TArray<TMXImageLayer> _imageLayers;
	private TArray<TMXObjectLayer> _objectLayers;
	private TArray<TMXTileSet> _tileSets;

	private TMXProperties _properties;

	public TMXMap(final String path) {
		this(path, LSystem.EMPTY);
	}

	public TMXMap(final String path, final String location) {
		_version = 1.0f;

		_layers = new TArray<TMXMapLayer>();
		_tileLayers = new TArray<TMXTileLayer>();
		_imageLayers = new TArray<TMXImageLayer>();
		_objectLayers = new TArray<TMXObjectLayer>();
		_tileSets = new TArray<TMXTileSet>();

		_properties = new TMXProperties();

		_orientation = Orientation.ORTHOGONAL;
		_renderOrder = RenderOrder.LEFT_UP;
		_staggerAxis = StaggerAxis.NONE;
		_staggerIndex = StaggerIndex.NONE;

		_backgroundColor = new LColor(LColor.TRANSPARENT);

		this._filePath = path;
		final String newLocation = PathUtils.getDirName(_filePath);
		if (StringUtils.isEmpty(newLocation)) {
			this._tilesLocation = location;
		} else if (StringUtils.isEmpty(location)) {
			this._tilesLocation = newLocation;
		}

		this.updateRenderOffset();
		final String ext = PathUtils.getExtension(_filePath).trim().toLowerCase();
		if ("xml".equals(ext) || "tmx".equals(ext) || StringUtils.isNullOrEmpty(ext)) {
			parserXml(this._filePath, this._tilesLocation);
		} else if ("json".equals(ext) || "tmj".equals(ext)) {
			parserJson(this._filePath, this._tilesLocation);
		}
	}

	/**
	 * 解析xml格式地图文件
	 * 
	 * @param path
	 * @param local
	 */
	protected void parserXml(String path, String local) {
		XMLDocument doc = XMLParser.parse(path);
		if (doc != null) {
			XMLElement docElement = doc.getRoot();
			if (docElement != null) {
				if (!docElement.getName().equals("map")) {
					throw new LSysException("Invalid TMX map file. The first child must be a <map> element.");
				}
				parseTMX(docElement, local);
			}
		}
	}

	/**
	 * 解析json格式地图文件
	 * 
	 * @param path
	 * @param local
	 */
	protected void parserJson(String path, String local) {
		Object jsonObj = TextResource.get().loadJsonObject(path);
		if (jsonObj != null) {
			if (jsonObj instanceof Json.Object) {
				Json.Object json = (Json.Object) jsonObj;
				String typeCode = json.getString("type");
				if (!typeCode.equals("map")) {
					throw new LSysException("Invalid TMJ map file. The first child must be a <map> element.");
				}
				parseTMJ(json, local);
			} else {
				throw new LSysException("Invalid TMJ map file. ");
			}
		}
	}

	/**
	 * 返回一个tmx的具体渲染对象（此对象为Sprite,可以直接插入Screen,如普通Sprite同样操作）
	 *
	 * @return
	 */
	public TMXMapRenderer getMapRenderer() {
		switch (this._orientation) {
		case ISOMETRIC:
			return new TMXIsometricMapRenderer(this);
		case HEXAGONAL:
			return new TMXHexagonalMapRenderer(this);
		case STAGGERED:
			return new TMXStaggeredMapRenderer(this);
		default:
			return new TMXOrthogonalMapRenderer(this);
		}
	}

	public TMXMapRenderer getMapRenderer(float w, float h) {
		switch (this._orientation) {
		case ISOMETRIC:
			return new TMXIsometricMapRenderer(this, w, h);
		case HEXAGONAL:
			return new TMXHexagonalMapRenderer(this, w, h);
		case STAGGERED:
			return new TMXStaggeredMapRenderer(this, w, h);
		default:
			return new TMXOrthogonalMapRenderer(this, w, h);
		}
	}

	public Orientation getOrientation() {
		return _orientation;
	}

	public RenderOrder getRenderOrder() {
		return _renderOrder;
	}

	public StaggerAxis getStaggerAxis() {
		return _staggerAxis;
	}

	public StaggerIndex getStaggerIndex() {
		return _staggerIndex;
	}

	public String getFilePath() {
		return _filePath;
	}

	public LColor getBackgroundColor() {
		return _backgroundColor;
	}

	public double getVersion() {
		return _version;
	}

	public double getTiledversion() {
		return _tiledversion;
	}

	@Override
	public float getX() {
		return getOffsetX();
	}

	@Override
	public float getY() {
		return getOffsetY();
	}

	public float getOffsetX() {
		return _offsetX;
	}

	public float getOffsetY() {
		return _offsetY;
	}

	public TMXMap setOffsetX(float offsetX) {
		this._offsetX = offsetX;
		updateRenderOffset();
		return this;
	}

	public TMXMap setOffsetY(float offsetY) {
		this._offsetY = offsetY;
		updateRenderOffset();
		return this;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public int getTileWidth() {
		return _tileWidth;
	}

	public int getTileHeight() {
		return _tileHeight;
	}

	public int getTileWidthHalf() {
		return _tileWidthHalf;
	}

	public int getTileHeightHalf() {
		return _tileHeightHalf;
	}

	public int getNextObjectID() {
		return _nextObjectID;
	}

	public int getNextlayerid() {
		return _nextlayerid;
	}

	public int getInfinite() {
		return _infinite;
	}

	public int getHexSideLength() {
		return _hexSideLength;
	}

	public TMXMapLayer getLayer(int index) {
		return _layers.get(index);
	}

	public TMXTileLayer getTileLayer(int index) {
		return _tileLayers.get(index);
	}

	public boolean getLayerVisibility(int index) {
		TMXMapLayer layer = _layers.get(index);
		if (layer != null) {
			return layer.visible;
		}
		return false;
	}

	public boolean getTileLayerVisibility(int index) {
		TMXTileLayer layer = _tileLayers.get(index);
		if (layer != null) {
			return layer.visible;
		}
		return false;
	}

	public int getNumLayers() {
		return _layers.size;
	}

	public TArray<TMXMapLayer> getLayers() {
		return _layers;
	}

	public int getNumTileLayers() {
		return _tileLayers.size;
	}

	public float getRenderOffsetX() {
		if (_renderOffsetDirty) {
			calcRenderOffsets();
		}
		return _renderOffsetX;
	}

	public float getRenderOffsetY() {
		if (_renderOffsetDirty) {
			calcRenderOffsets();
		}
		return _renderOffsetY;
	}

	protected void calcRenderOffsets() {
		_renderOffsetX = _offsetX;
		_renderOffsetY = _offsetY;
		_renderOffsetDirty = false;
	}

	public TArray<TMXTileLayer> getTileLayers() {
		return _tileLayers;
	}

	public TMXObjectLayer getObjectLayer(int index) {
		return _objectLayers.get(index);
	}

	public int getNumObjectLayers() {
		return _objectLayers.size;
	}

	public TArray<TMXObjectLayer> getObjectLayers() {
		return _objectLayers;
	}

	public TMXImageLayer getImageLayer(int index) {
		return _imageLayers.get(index);
	}

	public int getNumImageLayers() {
		return _imageLayers.size;
	}

	public TArray<TMXImageLayer> getImageLayers() {
		return _imageLayers;
	}

	public void updateRenderOffset() {
		_renderOffsetDirty = true;
	}

	public int findTileSetIndex(int gid) {
		gid &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);

		for (int i = getNumTileSets() - 1; i >= 0; i--) {
			if (gid >= getTileset(i).getFirstGID()) {
				return i;
			}
		}

		return -1;
	}

	public TMXTileSet findTileset(int gid) {
		for (int i = getNumTileSets() - 1; i >= 0; i--) {
			if (gid >= getTileset(i).getFirstGID())
				return getTileset(i);
		}

		return null;
	}

	public TMXTileSet getTileset(int index) {
		return _tileSets.get(index);
	}

	public int getNumTileSets() {
		return _tileSets.size;
	}

	public TArray<TMXTileSet> getTileSets() {
		return _tileSets;
	}

	public TMXProperties getProperties() {
		return _properties;
	}

	@Override
	public int getZ() {
		return 0;
	}

	@Override
	public float left() {
		return getX();
	}

	@Override
	public float top() {
		return getY();
	}

	@Override
	public float right() {
		return getWidth();
	}

	@Override
	public float bottom() {
		return getHeight();
	}

	private void parseTMJ(Json.Object json, String tilesLocation) {

		_version = json.getDouble("version", 0);
		_tiledversion = json.getDouble("tiledversion", 0);

		_offsetX = json.getNumber("x", 0);
		_offsetY = json.getNumber("y", 0);
		_offsetX = json.getNumber("offsetx", _offsetX);
		_offsetY = json.getNumber("offsety", _offsetY);

		_tmxType = json.getString("type", null);
		_width = json.getInt("width", 0);
		_height = json.getInt("height", 0);
		_tileWidth = json.getInt("tilewidth", 0);
		_tileHeight = json.getInt("tileheight", 0);

		_tileWidthHalf = _tileWidth / 2;
		_tileHeightHalf = _tileHeight / 2;

		_widthInPixels = _width * _tileWidth;
		_heightInPixels = _height * _tileHeight;

		_infinite = json.getInt("infinite", 0);

		_nextlayerid = json.getInt("nextlayerid", 0);
		_nextObjectID = json.getInt("nextobjectid", 0);

		if (json.containsKey("background")) {
			_backgroundColor = new LColor(json.getString("background", LColor.white.toString()).trim());
		}

		_orientation = Orientation.valueOf(json.getString("orientation", "ORTHOGONAL").trim().toUpperCase());
		if (_orientation != null && _orientation == Orientation.STAGGERED) {
			if (_height > 1) {
				this._widthInPixels += _tileWidth / 2;
				this._heightInPixels = _heightInPixels / 2 + _tileHeight / 2;
			}
		}

		if (json.containsKey("renderorder")) {
			switch (json.getString("renderorder", LSystem.EMPTY).trim().toLowerCase()) {
			case "right-down":
				_renderOrder = RenderOrder.RIGHT_DOWN;
				break;
			case "right-up":
				_renderOrder = RenderOrder.RIGHT_UP;
				break;
			case "left-down":
				_renderOrder = RenderOrder.LEFT_DOWN;
				break;
			case "left-up":
				_renderOrder = RenderOrder.LEFT_UP;
				break;
			}
		}

		if (json.containsKey("staggeraxis")) {
			switch (json.getString("staggeraxis", LSystem.EMPTY).trim().toLowerCase()) {
			case "x":
				_staggerAxis = StaggerAxis.AXIS_X;
				break;
			case "y":
				_staggerAxis = StaggerAxis.AXIS_Y;
				break;
			}
		}

		if (json.containsKey("staggerindex")) {
			switch (json.getString("staggerindex", LSystem.EMPTY).trim().toLowerCase()) {
			case "even":
				_staggerIndex = StaggerIndex.EVEN;
				break;
			case "odd":
				_staggerIndex = StaggerIndex.ODD;
				break;
			}
		}

		_hexSideLength = json.getInt("hexsidelength", 0);

		if (json.containsKey("properties")) {
			Json.Array propertiesArray = json.getArray("properties", null);
			if (propertiesArray != null) {
				_properties.parse(propertiesArray);
			}
		}

		parseTiles(json);
		parseLayers(json);
		parseObjects(json);
		parseImages(json);

		_layers.addAll(_tileLayers);
		_layers.addAll(_imageLayers);
		_layers.addAll(_objectLayers);
	}

	private void parseLayers(Json.Object layer) {
		if (layer.containsKey("layers")) {
			Json.Array layersArray = layer.getArray("layers", null);
			if (layersArray != null) {
				for (int i = 0; i < layersArray.length(); i++) {
					final Json.Object obj = layersArray.getObject(i);
					TMXTileLayer tileLayer = new TMXTileLayer(this);
					tileLayer.parse(obj);
					_tileLayers.add(tileLayer);

					parseTiles(obj);
					parseObjects(obj);
					parseImages(obj);
				}
			}
		}
	}

	private void parseTiles(Json.Object obj) {
		Json.Array tilesetsArray = null;
		if (obj.containsKey("tilesets")) {
			tilesetsArray = obj.getArray("tilesets", null);
		} else if (obj.containsKey("tiles")) {
			tilesetsArray = obj.getArray("tiles", null);
		}
		if (tilesetsArray != null) {
			for (int i = 0; i < tilesetsArray.length(); i++) {
				TMXTileSet tileSet = new TMXTileSet();
				tileSet.parse(tilesetsArray.getObject(i), _tilesLocation);
				_tileSets.add(tileSet);
			}
		}
	}

	private void parseObjects(Json.Object obj) {
		Json.Array objectgroupsArray = null;
		if (obj.containsKey("objectgroups")) {
			objectgroupsArray = obj.getArray("objectgroups", null);
		} else if (obj.containsKey("objects")) {
			objectgroupsArray = obj.getArray("objects", null);
		}
		if (objectgroupsArray != null) {
			for (int i = 0; i < objectgroupsArray.length(); i++) {
				TMXObjectLayer objectLayer = new TMXObjectLayer(this);
				objectLayer.parse(objectgroupsArray.getObject(i));
				_objectLayers.add(objectLayer);
			}
		}
	}

	private void parseImages(Json.Object obj) {
		Json.Array imagelayersArray = null;
		if (obj.containsKey("imagelayers")) {
			imagelayersArray = obj.getArray("imagelayers", null);
		} else if (obj.containsKey("images")) {
			imagelayersArray = obj.getArray("images", null);
		}
		if (imagelayersArray != null) {
			for (int i = 0; i < imagelayersArray.length(); i++) {
				TMXImageLayer imageLayer = new TMXImageLayer(this);
				imageLayer.parse(imagelayersArray.getObject(i));
				_imageLayers.add(imageLayer);
			}
		}
	}

	private void parseTMX(XMLElement element, String tilesLocation) {

		_version = element.getDoubleAttribute("version", 0);
		_tiledversion = element.getDoubleAttribute("tiledversion", 0);

		_offsetX = element.getFloatAttribute("x", 0);
		_offsetY = element.getFloatAttribute("y", 0);
		_offsetX = element.getFloatAttribute("offsetx", _offsetX);
		_offsetY = element.getFloatAttribute("offsety", _offsetY);

		_tmxType = element.getAttribute("type", null);

		_width = element.getIntAttribute("width", 0);
		_height = element.getIntAttribute("height", 0);
		_tileWidth = element.getIntAttribute("tilewidth", 0);
		_tileHeight = element.getIntAttribute("tileheight", 0);
		_infinite = element.getIntAttribute("infinite", 0);

		_tileWidthHalf = _tileWidth / 2;
		_tileHeightHalf = _tileHeight / 2;

		_widthInPixels = _width * _tileWidth;
		_heightInPixels = _height * _tileHeight;

		_nextlayerid = element.getIntAttribute("nextlayerid", 0);
		_nextObjectID = element.getIntAttribute("nextobjectid", 0);

		if (element.hasAttribute("background")) {
			_backgroundColor = new LColor(element.getAttribute("background", LColor.white.toString()).trim());
		}

		_orientation = Orientation.valueOf(element.getAttribute("orientation", "ORTHOGONAL").trim().toUpperCase());
		if (_orientation != null && _orientation == Orientation.STAGGERED) {
			if (_height > 1) {
				this._widthInPixels += _tileWidth / 2;
				this._heightInPixels = _heightInPixels / 2 + _tileHeight / 2;
			}
		}
		if (element.hasAttribute("renderorder")) {
			switch (element.getAttribute("renderorder", LSystem.EMPTY).trim().toLowerCase()) {
			case "right-down":
				_renderOrder = RenderOrder.RIGHT_DOWN;
				break;
			case "right-up":
				_renderOrder = RenderOrder.RIGHT_UP;
				break;
			case "left-down":
				_renderOrder = RenderOrder.LEFT_DOWN;
				break;
			case "left-up":
				_renderOrder = RenderOrder.LEFT_UP;
				break;
			}
		}

		if (element.hasAttribute("staggeraxis")) {
			switch (element.getAttribute("staggeraxis", LSystem.EMPTY).trim().toLowerCase()) {
			case "x":
				_staggerAxis = StaggerAxis.AXIS_X;
				break;
			case "y":
				_staggerAxis = StaggerAxis.AXIS_Y;
				break;
			}
		}

		if (element.hasAttribute("staggerindex")) {
			switch (element.getAttribute("staggerindex", LSystem.EMPTY).trim().toLowerCase()) {
			case "even":
				_staggerIndex = StaggerIndex.EVEN;
				break;
			case "odd":
				_staggerIndex = StaggerIndex.ODD;
				break;
			}
		}

		_hexSideLength = element.getIntAttribute("hexsidelength", 0);

		TArray<XMLElement> list = element.list();

		for (XMLElement node : list) {

			String name = node.getName().trim().toLowerCase();

			switch (name) {
			case "properties":
				_properties.parse(node);
				break;

			case "tileset":
				TMXTileSet tileSet = new TMXTileSet();
				tileSet.parse(node, tilesLocation);
				_tileSets.add(tileSet);
				break;

			case "layer":
				TMXTileLayer tileLayer = new TMXTileLayer(this);
				tileLayer.parse(node);
				_tileLayers.add(tileLayer);
				break;

			case "imagelayer":
				TMXImageLayer imageLayer = new TMXImageLayer(this);
				imageLayer.parse(node);
				_imageLayers.add(imageLayer);
				break;

			case "objectgroup":
				TMXObjectLayer objectLayer = new TMXObjectLayer(this);
				objectLayer.parse(node);
				_objectLayers.add(objectLayer);
				break;
			}

		}

		_layers.addAll(_tileLayers);
		_layers.addAll(_imageLayers);
		_layers.addAll(_objectLayers);
	}

	public String getTmxType() {
		return _tmxType;
	}

	public String getTilesLocation() {
		return _tilesLocation;
	}

	public void setTilesLocation(String tilesLocation) {
		this._tilesLocation = tilesLocation;
	}

	public TArray<Field2D> newGIDField2Ds() {
		return newField2Ds(0);
	}

	public TArray<Field2D> newTileSetIDField2Ds() {
		return newField2Ds(1);
	}

	public TArray<Field2D> newIDField2Ds() {
		return newField2Ds(2);
	}

	public TArray<Field2D> newField2Ds(int mode) {
		final TArray<Field2D> list = new TArray<Field2D>(_tileLayers.size);
		for (TMXTileLayer layer : _tileLayers) {
			switch (mode) {
			case 0:
				list.add(layer.newGIDField2D());
				break;
			case 1:
				list.add(layer.newTileSetIDField2D());
				break;
			default:
				list.add(layer.newIDField2D());
				break;
			}
		}
		return list;
	}

	public int getWidthInPixels() {
		return _widthInPixels;
	}

	public int getHeightInPixels() {
		return _heightInPixels;
	}

	public PointI pixelsStaggeredToMap(float x, float y, boolean even) {
		return pixelsStaggeredToMap(x, y, _tileWidth, _tileHeight, even);
	}

	public PointI pixelsStaggeredToMap(float x, float y, float tileWidth, float tileHeight, boolean even) {
		float newX = x;
		float newY = y;
		if (even) {
			newX = MathUtils.ifloor((newX + tileWidth) / tileHeight) - 1f;
			newY = (MathUtils.ifloor((y + tileHeight) / tileHeight) - 1f) * 2f;
		} else {
			newX = MathUtils.ifloor((x + tileWidth / 2f) / tileWidth) - 1f;
			newY = ((MathUtils.ifloor(y + tileHeight / 2f) / tileHeight) - 1f) * 2f;
		}
		return new PointI(MathUtils.ifloor(newX), MathUtils.ifloor(newY));
	}

	public PointI pixelsIsometricToMap(float x, float y) {
		return pixelsIsometricToMap(x, y, _tileWidth, _tileHeight);
	}

	public PointI pixelsIsometricToMap(float x, float y, float tileWidth, float tileHeight) {
		float newX = x / tileWidth;
		float newY = (y - tileHeight / 2f) / tileHeight + x;
		newX -= newY - newX;
		return new PointI(MathUtils.ifloor(newX), MathUtils.ifloor(newY));
	}

	public PointI tilesIsometricToPixels(float x, float y) {
		return tilesIsometricToPixels(x, y, _tileWidth, _tileHeight);
	}

	public PointI tilesIsometricToPixels(float x, float y, float tileWidth, float tileHeight) {
		float newX = tileWidth * x / 2f + _height * tileWidth / 2f - y * tileWidth / 2f;
		float newY = tileHeight * y / 2f + _width * tileHeight / 2f - x * tileHeight / 2f;
		return new PointI(MathUtils.ifloor(newX), MathUtils.ifloor(newY));
	}

	public PointI pixelsHexagonToMap(float x, float y) {
		return pixelsHexagonToMap(x, y, _tileWidth, _tileHeight);
	}

	public PointI pixelsHexagonToMap(float x, float y, float tileWidth, float tileHeight) {
		float row = (y / tileHeight);
		float column = 0f;
		final boolean rowEven = row % 2 == 0;
		if (rowEven) {
			column = (x / tileWidth);
		} else {
			column = ((x - _widthInPixels / 2f) / tileWidth);
		}
		float newX = 0f;
		float newY = y - (row * tileHeight);
		if (rowEven) {
			newX = x - (column * tileWidth);
		} else {
			newX = (x - (column * tileWidth)) - _widthInPixels / 2f;
		}
		return new PointI(MathUtils.ifloor(newX), MathUtils.ifloor(newY));
	}

	public PointI pixelsOrthogonalToMap(float x, float y) {
		return pixelsOrthogonalToMap(x, y, _tileWidth, _tileHeight);
	}

	public PointI pixelsOrthogonalToMap(float x, float y, float tileWidth, float tileHeight) {
		float newX = x / tileWidth;
		float newY = y / tileHeight;
		return new PointI(MathUtils.ifloor(newX), MathUtils.ifloor(newY));
	}

}
