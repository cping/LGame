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

import loon.BaseIO;
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
import loon.geom.Sized;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
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

	private String filePath;
	private String tilesLocation;
	private String tmxType;
	private LColor backgroundColor;

	private double version;
	private double tiledversion;

	private boolean renderOffsetDirty;

	private Orientation orientation;
	private RenderOrder renderOrder;
	private StaggerAxis staggerAxis;
	private StaggerIndex staggerIndex;

	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;
	private int infinite;
	private int nextObjectID;
	private int nextlayerid;
	private int hexSideLength;

	private float offsetX;
	private float offsetY;
	private float renderOffsetX;
	private float renderOffsetY;

	private TArray<TMXMapLayer> layers;
	private TArray<TMXTileLayer> tileLayers;
	private TArray<TMXImageLayer> imageLayers;
	private TArray<TMXObjectLayer> objectLayers;
	private TArray<TMXTileSet> tileSets;

	private TMXProperties properties;

	public TMXMap(final String path, final String location) {
		version = 1.0f;

		layers = new TArray<TMXMapLayer>();
		tileLayers = new TArray<TMXTileLayer>();
		imageLayers = new TArray<TMXImageLayer>();
		objectLayers = new TArray<TMXObjectLayer>();
		tileSets = new TArray<TMXTileSet>();

		properties = new TMXProperties();

		orientation = Orientation.ORTHOGONAL;
		renderOrder = RenderOrder.LEFT_UP;
		staggerAxis = StaggerAxis.NONE;
		staggerIndex = StaggerIndex.NONE;

		backgroundColor = new LColor(LColor.TRANSPARENT);

		this.filePath = path;
		this.tilesLocation = location;
		this.updateRenderOffset();
		final String ext = PathUtils.getExtension(filePath).trim().toLowerCase();
		if ("xml".equals(ext) || "tmx".equals(ext) || StringUtils.isNullOrEmpty(ext)) {
			parserXml(this.filePath, this.tilesLocation);
		} else if ("json".equals(ext) || "tmj".equals(ext)) {
			parserJson(this.filePath, this.tilesLocation);
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
		Object jsonObj = BaseIO.loadJsonObject(path);
		if (jsonObj != null) {
			if (jsonObj instanceof Json.Object) {
				Json.Object json = (Json.Object) jsonObj;
				String typeCode = json.getString("type");
				if (!typeCode.equals("map")) {
					throw new LSysException("Invalid TMJ map file. The first child must be a <map> element.");
				}
				parseTMJ(json, local);
			}
		}
	}

	/**
	 * 返回一个tmx的具体渲染对象（此对象为Sprite,可以直接插入Screen,如普通Sprite同样操作）
	 *
	 * @return
	 */
	public TMXMapRenderer getMapRenderer() {
		switch (this.orientation) {
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

	public Orientation getOrientation() {
		return orientation;
	}

	public RenderOrder getRenderOrder() {
		return renderOrder;
	}

	public StaggerAxis getStaggerAxis() {
		return staggerAxis;
	}

	public StaggerIndex getStaggerIndex() {
		return staggerIndex;
	}

	public String getFilePath() {
		return filePath;
	}

	public LColor getBackgroundColor() {
		return backgroundColor;
	}

	public double getVersion() {
		return version;
	}

	public double getTiledversion() {
		return tiledversion;
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
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public TMXMap setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		updateRenderOffset();
		return this;
	}

	public TMXMap setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		updateRenderOffset();
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getNextObjectID() {
		return nextObjectID;
	}

	public int getNextlayerid() {
		return nextlayerid;
	}

	public int getInfinite() {
		return infinite;
	}

	public int getHexSideLength() {
		return hexSideLength;
	}

	public TMXMapLayer getLayer(int index) {
		return layers.get(index);
	}

	public int getNumLayers() {
		return layers.size;
	}

	public TArray<TMXMapLayer> getLayers() {
		return layers;
	}

	public TMXTileLayer getTileLayer(int index) {
		return tileLayers.get(index);
	}

	public int getNumTileLayers() {
		return tileLayers.size;
	}

	public float getRenderOffsetX() {
		if (renderOffsetDirty) {
			calcRenderOffsets();
		}
		return renderOffsetX;
	}

	public float getRenderOffsetY() {
		if (renderOffsetDirty) {
			calcRenderOffsets();
		}
		return renderOffsetY;
	}

	protected void calcRenderOffsets() {
		renderOffsetX = offsetX;
		renderOffsetY = offsetY;
		renderOffsetDirty = false;
	}

	public TArray<TMXTileLayer> getTileLayers() {
		return tileLayers;
	}

	public TMXObjectLayer getObjectLayer(int index) {
		return objectLayers.get(index);
	}

	public int getNumObjectLayers() {
		return objectLayers.size;
	}

	public TArray<TMXObjectLayer> getObjectLayers() {
		return objectLayers;
	}

	public TMXImageLayer getImageLayer(int index) {
		return imageLayers.get(index);
	}

	public int getNumImageLayers() {
		return imageLayers.size;
	}

	public TArray<TMXImageLayer> getImageLayers() {
		return imageLayers;
	}

	public void updateRenderOffset() {
		renderOffsetDirty = true;
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
		return tileSets.get(index);
	}

	public int getNumTileSets() {
		return tileSets.size;
	}

	public TArray<TMXTileSet> getTileSets() {
		return tileSets;
	}

	public TMXProperties getProperties() {
		return properties;
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

		version = json.getDouble("version", 0);
		tiledversion = json.getDouble("tiledversion", 0);

		offsetX = json.getNumber("x", 0);
		offsetY = json.getNumber("y", 0);
		offsetX = json.getNumber("offsetx", offsetX);
		offsetY = json.getNumber("offsety", offsetY);

		tmxType = json.getString("type", null);
		width = json.getInt("width", 0);
		height = json.getInt("height", 0);
		tileWidth = json.getInt("tilewidth", 0);
		tileHeight = json.getInt("tileheight", 0);
		infinite = json.getInt("infinite", 0);

		nextlayerid = json.getInt("nextlayerid", 0);
		nextObjectID = json.getInt("nextobjectid", 0);

		if (json.containsKey("background")) {
			backgroundColor = new LColor(json.getString("background", LColor.white.toString()).trim());
		}

		orientation = Orientation.valueOf(json.getString("orientation", "ORTHOGONAL").trim().toUpperCase());

		if (json.containsKey("renderorder")) {
			switch (json.getString("renderorder", LSystem.EMPTY).trim().toLowerCase()) {
			case "right-down":
				renderOrder = RenderOrder.RIGHT_DOWN;
				break;
			case "right-up":
				renderOrder = RenderOrder.RIGHT_UP;
				break;
			case "left-down":
				renderOrder = RenderOrder.LEFT_DOWN;
				break;
			case "left-up":
				renderOrder = RenderOrder.LEFT_UP;
				break;
			}
		}

		if (json.containsKey("staggeraxis")) {
			switch (json.getString("staggeraxis", LSystem.EMPTY).trim().toLowerCase()) {
			case "x":
				staggerAxis = StaggerAxis.AXIS_X;
				break;
			case "y":
				staggerAxis = StaggerAxis.AXIS_Y;
				break;
			}
		}

		if (json.containsKey("staggerindex")) {
			switch (json.getString("staggerindex", LSystem.EMPTY).trim().toLowerCase()) {
			case "even":
				staggerIndex = StaggerIndex.EVEN;
				break;
			case "odd":
				staggerIndex = StaggerIndex.ODD;
				break;
			}
		}

		hexSideLength = json.getInt("hexsidelength", 0);

		if (json.containsKey("properties")) {
			Json.Array propertiesArray = json.getArray("properties", null);
			if (propertiesArray != null) {
				properties.parse(propertiesArray);
			}
		}

		if (json.containsKey("tilesets")) {
			Json.Array tilesetsArray = json.getArray("tilesets", null);
			if (tilesetsArray != null) {
				for (int i = 0; i < tilesetsArray.length(); i++) {
					TMXTileSet tileSet = new TMXTileSet();
					tileSet.parse(tilesetsArray.getObject(i), tilesLocation);
					tileSets.add(tileSet);
				}
			}
		}

		if (json.containsKey("layers")) {
			Json.Array layersArray = json.getArray("layers", null);
			if (layersArray != null) {
				for (int i = 0; i < layersArray.length(); i++) {
					final Json.Object obj = layersArray.getObject(i);
					TMXTileLayer tileLayer = new TMXTileLayer(this);
					tileLayer.parse(layersArray.getObject(i));
					tileLayers.add(tileLayer);
					parseObjects(obj);
					parseImages(json);
				}
			}
		}

		parseObjects(json);
		parseImages(json);

		layers.addAll(tileLayers);
		layers.addAll(imageLayers);
		layers.addAll(objectLayers);
	}

	private void parseObjects(Json.Object obj) {
		if (obj.containsKey("objects")) {
			Json.Array objectgroupsArray = obj.getArray("objects", null);
			if (objectgroupsArray != null) {
				for (int i = 0; i < objectgroupsArray.length(); i++) {
					TMXObjectLayer objectLayer = new TMXObjectLayer(this);
					objectLayer.parse(objectgroupsArray.getObject(i));
					objectLayers.add(objectLayer);
				}
			}
		}
	}

	private void parseImages(Json.Object obj) {
		if (obj.containsKey("imagelayers")) {
			Json.Array imagelayersArray = obj.getArray("imagelayers", null);
			if (imagelayersArray != null) {
				for (int i = 0; i < imagelayersArray.length(); i++) {
					TMXImageLayer imageLayer = new TMXImageLayer(this);
					imageLayer.parse(imagelayersArray.getObject(i));
					imageLayers.add(imageLayer);
				}
			}
		}
	}

	private void parseTMX(XMLElement element, String tilesLocation) {

		version = element.getDoubleAttribute("version", 0);
		tiledversion = element.getDoubleAttribute("tiledversion", 0);

		offsetX = element.getFloatAttribute("x", 0);
		offsetY = element.getFloatAttribute("y", 0);
		offsetX = element.getFloatAttribute("offsetx", offsetX);
		offsetY = element.getFloatAttribute("offsety", offsetY);

		tmxType = element.getAttribute("type", null);

		width = element.getIntAttribute("width", 0);
		height = element.getIntAttribute("height", 0);
		tileWidth = element.getIntAttribute("tilewidth", 0);
		tileHeight = element.getIntAttribute("tileheight", 0);
		infinite = element.getIntAttribute("infinite", 0);

		nextlayerid = element.getIntAttribute("nextlayerid", 0);
		nextObjectID = element.getIntAttribute("nextobjectid", 0);

		if (element.hasAttribute("background")) {
			backgroundColor = new LColor(element.getAttribute("background", LColor.white.toString()).trim());
		}

		orientation = Orientation.valueOf(element.getAttribute("orientation", "ORTHOGONAL").trim().toUpperCase());

		if (element.hasAttribute("renderorder")) {
			switch (element.getAttribute("renderorder", LSystem.EMPTY).trim().toLowerCase()) {
			case "right-down":
				renderOrder = RenderOrder.RIGHT_DOWN;
				break;
			case "right-up":
				renderOrder = RenderOrder.RIGHT_UP;
				break;
			case "left-down":
				renderOrder = RenderOrder.LEFT_DOWN;
				break;
			case "left-up":
				renderOrder = RenderOrder.LEFT_UP;
				break;
			}
		}

		if (element.hasAttribute("staggeraxis")) {
			switch (element.getAttribute("staggeraxis", LSystem.EMPTY).trim().toLowerCase()) {
			case "x":
				staggerAxis = StaggerAxis.AXIS_X;
				break;
			case "y":
				staggerAxis = StaggerAxis.AXIS_Y;
				break;
			}
		}

		if (element.hasAttribute("staggerindex")) {
			switch (element.getAttribute("staggerindex", LSystem.EMPTY).trim().toLowerCase()) {
			case "even":
				staggerIndex = StaggerIndex.EVEN;
				break;
			case "odd":
				staggerIndex = StaggerIndex.ODD;
				break;
			}
		}

		hexSideLength = element.getIntAttribute("hexsidelength", 0);

		TArray<XMLElement> list = element.list();

		for (XMLElement node : list) {

			String name = node.getName().trim().toLowerCase();

			switch (name) {
			case "properties":
				properties.parse(node);
				break;

			case "tileset":
				TMXTileSet tileSet = new TMXTileSet();
				tileSet.parse(node, tilesLocation);
				tileSets.add(tileSet);
				break;

			case "layer":
				TMXTileLayer tileLayer = new TMXTileLayer(this);
				tileLayer.parse(node);
				tileLayers.add(tileLayer);
				break;

			case "imagelayer":
				TMXImageLayer imageLayer = new TMXImageLayer(this);
				imageLayer.parse(node);
				imageLayers.add(imageLayer);
				break;

			case "objectgroup":
				TMXObjectLayer objectLayer = new TMXObjectLayer(this);
				objectLayer.parse(node);
				objectLayers.add(objectLayer);
				break;
			}

		}

		layers.addAll(tileLayers);
		layers.addAll(imageLayers);
		layers.addAll(objectLayers);
	}

	public String getTmxType() {
		return tmxType;
	}

	public String getTilesLocation() {
		return tilesLocation;
	}

	public void setTilesLocation(String tilesLocation) {
		this.tilesLocation = tilesLocation;
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
		TArray<Field2D> list = new TArray<Field2D>(tileLayers.size);
		for (TMXTileLayer layer : tileLayers) {
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

}
