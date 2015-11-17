package loon.action.map.tmx;

import java.util.ArrayList;
import java.util.List;

import loon.action.map.tmx.renderers.TMXIsometricMapRenderer;
import loon.action.map.tmx.renderers.TMXMapRenderer;
import loon.action.map.tmx.renderers.TMXOrthogonalMapRenderer;
import loon.canvas.LColor;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class TMXMap {

	/**
	 * 该TiledMap类的渲染方向
	 */
	public enum Orientation {
		ORTHOGONAL, ISOMETRIC, STAGGERED, HEXAGONAL
	}

	/**
	 * 该TiledMap类的渲染呈现顺序
	 */
	public enum RenderOrder {
		RIGHT_DOWN, RIGHT_UP, LEFT_DOWN, LEFT_UP
	}

	/**
	 * 此地图的交错轴（地图不是六角形时生效）
	 */
	public enum StaggerAxis {
		AXIS_X, AXIS_Y, NONE
	}

	/**
	 * 此地图的错开模式。适用于六边形和等距交错地图。
	 */
	public enum StaggerIndex {
		NONE, EVEN, ODD
	}

	public static final long FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
	public static final long FLIPPED_VERTICALLY_FLAG = 0x40000000;
	public static final long FLIPPED_DIAGONALLY_FLAG = 0x20000000;

	private String filePath;
	private String tilesLocation;
	private LColor backgroundColor;

	private double version;

	private Orientation orientation;
	private RenderOrder renderOrder;
	private StaggerAxis staggerAxis;
	private StaggerIndex staggerIndex;

	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;
	private int nextObjectID;
	private int hexSideLength;

	private List<TMXMapLayer> layers;
	private List<TMXTileLayer> tileLayers;
	private List<TMXImageLayer> imageLayers;
	private List<TMXObjectLayer> objectLayers;
	private List<TMXTileSet> tileSets;

	private TMXProperties properties;

	public TMXMap(String filePath, String tilesLocation) {
		version = 1.0f;

		layers = new ArrayList<TMXMapLayer>();
		tileLayers = new ArrayList<TMXTileLayer>();
		imageLayers = new ArrayList<TMXImageLayer>();
		objectLayers = new ArrayList<TMXObjectLayer>();
		tileSets = new ArrayList<TMXTileSet>();

		properties = new TMXProperties();

		orientation = Orientation.ORTHOGONAL;
		renderOrder = RenderOrder.LEFT_UP;
		staggerAxis = StaggerAxis.NONE;
		staggerIndex = StaggerIndex.NONE;

		backgroundColor = new LColor(LColor.TRANSPARENT);

		this.filePath = filePath;
		this.tilesLocation = tilesLocation;

		XMLDocument doc = XMLParser.parse(filePath);
		XMLElement docElement = doc.getRoot();

		if (!docElement.getName().equals("map")) {
			throw new RuntimeException(
					"Invalid TMX map file. The first child must be a <map> element.");
		}

		parse(docElement, tilesLocation);
	}

	public TMXMapRenderer getMapRenderer() {
		switch (this.orientation) {
		case ISOMETRIC:
			return new TMXIsometricMapRenderer(this);
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

	public int getHexSideLength() {
		return hexSideLength;
	}

	public TMXMapLayer getLayer(int index) {
		return layers.get(index);
	}

	public int getNumLayers() {
		return layers.size();
	}

	public List<TMXMapLayer> getLayers() {
		return layers;
	}

	public TMXTileLayer getTileLayer(int index) {
		return tileLayers.get(index);
	}

	public int getNumTileLayers() {
		return tileLayers.size();
	}

	public List<TMXTileLayer> getTileLayers() {
		return tileLayers;
	}

	public TMXObjectLayer getObjectLayer(int index) {
		return objectLayers.get(index);
	}

	public int getNumObjectLayers() {
		return objectLayers.size();
	}

	public List<TMXObjectLayer> getObjectLayers() {
		return objectLayers;
	}

	public TMXImageLayer getImageLayer(int index) {
		return imageLayers.get(index);
	}

	public int getNumImageLayers() {
		return imageLayers.size();
	}

	public List<TMXImageLayer> getImageLayers() {
		return imageLayers;
	}

	public int findTileSetIndex(int gid) {
		gid &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);

		for (int i = getNumTileSets() - 1; i >= 0; i--) {
			if (gid >= getTileset(i).getFirstGID()){
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
		return tileSets.size();
	}

	public List<TMXTileSet> getTileSets() {
		return tileSets;
	}

	public TMXProperties getProperties() {
		return properties;
	}

	private void parse(XMLElement element, String tilesLocation) {

		version = element.getDoubleAttribute("version", 0);
		width = element.getIntAttribute("width", 0);
		height = element.getIntAttribute("height", 0);
		tileWidth = element.getIntAttribute("tilewidth", 0);
		tileHeight = element.getIntAttribute("tileheight", 0);
		nextObjectID = element.getIntAttribute("nextobjectid", 0);

		if (element.hasAttribute("background")) {
			String hexColor = element.getAttribute("background",
					LColor.white.toString()).trim();
			if (hexColor.startsWith("#")) {
				hexColor = hexColor.substring(1);
			}
			backgroundColor = new LColor(Integer.parseInt(hexColor, 16));
		}

		orientation = Orientation
				.valueOf(element.getAttribute("orientation", "ORTHOGONAL")
						.trim().toUpperCase());

		if (element.hasAttribute("renderorder")) {
			switch (element.getAttribute("renderorder", "").trim()
					.toLowerCase()) {
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
			switch (element.getAttribute("staggeraxis", "RIGHT_DOWN").trim()
					.toLowerCase()) {
			case "x":
				staggerAxis = StaggerAxis.AXIS_X;
				break;
			case "y":
				staggerAxis = StaggerAxis.AXIS_Y;
				break;
			}
		}

		if (element.hasAttribute("staggerindex")) {
			switch (element.getAttribute("staggerindex", "").trim()
					.toLowerCase()) {
			case "even":
				staggerIndex = StaggerIndex.EVEN;
				break;
			case "odd":
				staggerIndex = StaggerIndex.ODD;
				break;
			}
		}

		hexSideLength = element.getIntAttribute("hexsidelength", 0);

		ArrayList<XMLElement> list = element.list();

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

	public String getTilesLocation() {
		return tilesLocation;
	}

	public void setTilesLocation(String tilesLocation) {
		this.tilesLocation = tilesLocation;
	}
}
