package loon.action.scripting.pack;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.Field2D;
import loon.action.scripting.ScriptScreen;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LLight;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLUtils;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.input.LTouch;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class PackTileMap extends LLight implements LRelease {

	class SimplePackTileFactory extends PackTileFactory {

		HashMap<Integer, PackTile> tiles = new HashMap<Integer, PackTile>(10);

		public void add(int id, PackTile tile) {
			tiles.put(id, tile);
		}

		public PackTile getTile(int id) {
			return tiles.get(id);
		}

		public boolean contains(int id) {
			return tiles.containsKey(id);
		}

		public String getName() {
			return "simple";
		}

	}

	private String bindPackName;

	private int lazyHashCode = 1;

	private int posX, posY;

	private int sx, sy;

	private PackView view;

	private PackTileFactory tileFactory;

	private int[] limit;

	private boolean init, dirty;

	private int tileWidth = 32;

	private int tileHeight = 32;

	private int width, height;

	private String name;

	private PackTile[][] tiles;

	private Field2D field;

	private XMLElement element;

	private Screen screen;

	private int limitWidth, limitHeight;

	private int minX, minY, maxX, maxY;

	public PackTileMap(String res) {
		this(res, null);
	}

	public PackTileMap(InputStream in) {
		this(in, null);
	}

	public PackTileMap(XMLElement element) {
		this(element, null);
	}

	public PackTileMap(PackTileFactory factory, XMLElement element) {
		this(factory, element, null);
	}

	public PackTileMap(String res, Screen screen) {
		this(XMLParser.parse(res).getRoot(), screen);
	}

	public PackTileMap(InputStream in, Screen screen) {
		this(XMLParser.parse(in).getRoot(), screen);
	}

	public PackTileMap(XMLElement element, Screen screen) {
		this(null, element, screen);
	}

	public PackTileMap(PackTileFactory factory, XMLElement element,
			Screen screen) {
		this.screen = screen;
		this.tileFactory = factory;
		this.element = element;
		this.dirty = true;
	}

	public void init(PackTileFactory tileFactory) {
		if (init) {
			return;
		}

		this.name = element.getAttribute("name", null);

		this.bindPackName = element.getAttribute("bind", null);

		this.tileWidth = element.getIntAttribute("tw", tileWidth);
		if (tileWidth < 32) {
			tileWidth = 32;
		}

		this.tileHeight = element.getIntAttribute("th", tileHeight);
		if (tileHeight < 32) {
			tileHeight = 32;
		}
		String limits = element.getAttribute("limit", "");

		String[] list = StringUtils.split(limits, ",");
		if (list != null && list.length > 0) {
			int size = list.length;
			limit = new int[size];
			for (int i = 0; i < size; i++) {
				try {
					limit[i] = Integer.parseInt(list[i]);
				} catch (Exception e) {
					limit[i] = -1;
				}
			}
		}

		if (tileFactory == null) {

			SimplePackTileFactory packTile = new SimplePackTileFactory();

			for (Iterator<?> it = element.elements("tile"); it.hasNext();) {
				XMLElement child = (XMLElement) it.next();
				final int id = child.getIntAttribute("id", -1);
				final int imgId = child.getIntAttribute("blockid", -1);
				final float sx = child.getIntAttribute("x", 0);
				final float sy = child.getIntAttribute("y", 0);
				final float sw = child.getIntAttribute("w", 0);
				final float sh = child.getIntAttribute("h", 0);
				final boolean sub = (sx != 0 || sy != 0 || sw != 0 || sh != 0);
				final String imgName = child.getAttribute("blockname", "");

				if (!packTile.contains(id)) {

					PackTile tile = new PackTile() {

						boolean solid;

						public void draw(LTexturePack pack, float x, float y,
								LColor[] c) {
							if (c == null) {
								if (imgId != -1) {
									if (sub) {
										pack.draw(imgId, x, y, tileWidth,
												tileHeight, sx, sy, sw, sh);
									} else {
										pack.draw(imgId, x, y);
									}
								} else {
									if (sub) {
										pack.draw(imgName, x, y, tileWidth,
												tileHeight, sx, sy, sw, sh);
									} else {
										pack.draw(imgName, x, y);
									}
								}
							} else {
								if (imgId != -1) {
									if (sub) {
										pack.drawOnlyBatch(imgId, x, y,
												tileWidth, tileHeight, sx, sy,
												sw, sh, c);
									} else {
										pack.drawOnlyBatch(imgId, x, y, c);
									}
								} else {
									if (sub) {
										pack.drawOnlyBatch(imgName, x, y,
												tileWidth, tileHeight, sx, sy,
												sw, sh, c);
									} else {
										pack.drawOnlyBatch(imgName, x, y, c);
									}
								}
							}
						}

						public boolean isSolid() {
							return solid;
						}

						public void setSolid(boolean s) {
							this.solid = s;
						}

						public int width() {
							return tileWidth;
						}

						public int height() {
							return tileHeight;
						}

						public void update(long t) {

						}

					};

					packTile.add(id, tile);
				}
			}

			this.tileFactory = packTile;
		}
		ArrayList<int[]> records = new ArrayList<int[]>(10);
		for (Iterator<?> e = element.elements("b"); e.hasNext();) {
			String result = ((XMLElement) e.next()).getAttribute("d", "");
			if (!"".equals(result)) {
				String[] stringArray = result.split(",");
				int size = stringArray.length;
				int[] intArray = new int[size];
				for (int i = 0; i < size; i++) {
					intArray[i] = Integer.parseInt(stringArray[i]);
				}
				records.add(intArray);
			}
		}
		if (records.size() > 0) {
			int col = records.size();
			int[][] result = new int[col][];
			for (int i = 0; i < col; i++) {
				result[i] = (int[]) records.get(i);
			}
			this.width = result[0].length;
			this.height = result.length;
			this.field = new Field2D(result, tileWidth, tileHeight);
		}

		if (screen != null) {
			limitWidth = screen.getWidth() / tileWidth;
			limitHeight = screen.getHeight() / tileHeight;
		} else {
			limitWidth = width;
			limitHeight = height;
		}

		this.maxLightSize(width, height);
		this.maxX = limitWidth;
		this.maxY = limitHeight;
		this.init = true;
	}

	public void sub(int x, int y, int w, int h) {
		this.minX = x;
		this.minY = y;
		this.maxX = w;
		this.maxY = h;
	}

	public void sub(int x, int y) {
		sub(x, y, limitWidth, limitHeight);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void update() {
		if (!dirty) {
			return;
		}

		if (field != null) {
			int[][] maps = field.getMap();
			if (tiles == null) {
				this.tiles = new PackTile[this.width][this.height];
			}
			if (limit == null) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						tiles[x][y] = tileFactory.getTile(maps[y][x]);
						if (tiles[x][y] != null && maps[y][x] == -1) {
							tiles[x][y].setSolid(true);
						}
					}
				}
			} else {
				field.setLimit(limit);
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int id = maps[y][x];
						if (tiles[x][y] == null) {
							tiles[x][y] = tileFactory.getTile(id);
						}
						if (tiles[x][y] != null) {
							if (limit != null) {
								for (int i = 0; i < limit.length; i++) {
									if (id == limit[i]) {
										tiles[x][y].setSolid(true);
									}
								}
							}
						}
					}
				}
			}
		}
		dirty = false;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, LTouch touch) {
		if (field != null) {
			return findAStar(heuristic, x1, y1, touch.x(), touch.y());
		}
		return null;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, int x2, int y2) {
		if (field != null) {
			return findAStar(heuristic, x1, y1, x2, y2, true);
		}
		return null;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, boolean flag, LTouch touch) {
		if (field != null) {
			return findAStar(heuristic, x1, y1, touch.x(), touch.y(), flag);
		}
		return null;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, int x2, int y2, boolean flag) {
		if (field != null) {
			return AStarFinder.find(heuristic, field,
					field.pixelsToTilesWidth(x1),
					field.pixelsToTilesHeight(y1),
					field.pixelsToTilesWidth(x2),
					field.pixelsToTilesHeight(y2), flag);
		}
		return null;
	}

	public void limit(int[] list) {
		if (field != null) {
			this.limit = list;
			this.dirty = true;
		}
	}

	public void setBlockSize(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public int getWidth() {
		return width * tileWidth;
	}

	public int getHeight() {
		return height * tileHeight;
	}

	public PackTile getTile(int x, int y) {
		return tiles[x][y];
	}

	public void setTile(int x, int y, PackTile type) {
		tiles[x][y] = type;
	}

	public boolean collidesWith(PackSprite sprite) {
		return !canMove(sprite, sprite.getX(), sprite.getY());
	}

	public int count(PackTile element) {
		int result = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (element == tiles[x][y]) {
					result++;
				}
			}
		}
		return result;
	}

	public boolean canMove(PackSprite sprite, float x1, float y1) {
		float w = sprite.getWidth();
		float h = sprite.getHeight();
		float sx = x1 - w;
		float sy = y1 - h;
		for (int x = MathUtils.max(MathUtils.floor(sx / tileWidth), 0); x <= MathUtils
				.min(MathUtils.floor((sx + w - 1) / tileWidth), width - 1); x++) {
			for (int y = MathUtils.max(MathUtils.floor(sy / tileHeight), 0); y <= MathUtils
					.min(MathUtils.floor((sy + h - 1) / tileHeight), height - 1); y++) {
				if (tiles[x][y].isSolid()) {
					return false;
				}
			}
		}
		return true;
	}

	public void touch(PackSprite sprite) {
		float w = sprite.getWidth();
		float h = sprite.getHeight();
		float sx = sprite.getX() - w;
		float sy = sprite.getY() - h;
		for (int x = MathUtils.max(MathUtils.floor(sx / tileWidth), 0); x <= MathUtils
				.min(MathUtils.floor((sx + w - 1) / tileWidth), width - 1); x++) {
			for (int y = MathUtils.max(MathUtils.floor(sy / tileHeight), 0); y <= MathUtils
					.min(MathUtils.floor((sy + h - 1) / tileHeight), height - 1); y++) {
				tiles[x][y] = tiles[x][y].touch(sprite, x * tileWidth, y
						* tileHeight);
			}
		}
	}

	public boolean drawSub(LTexturePack pack) {
		return draw(pack, minX, minY, maxX, maxY);
	}

	public boolean draw(LTexturePack pack) {
		return draw(pack, 0, 0);
	}

	public boolean draw(LTexturePack pack, int minX, int minY) {
		return draw(pack, minX, minY, limitWidth - minX, limitHeight - minY);
	}

	public boolean draw(final LTexturePack pack, int minX, int minY, int maxX,
			int maxY) {

		int lazy = 1;

		lazy = LSystem.unite(lazy, minX);
		lazy = LSystem.unite(lazy, minY);
		lazy = LSystem.unite(lazy, maxX);
		lazy = LSystem.unite(lazy, maxY);
		lazy = LSystem.unite(lazy, dirty);
		lazy = LSystem.unite(lazy, isLightDirty);

		if (dirty) {
			update();
		}

		LTexturePack currentPack = null;
		long elapsedTime = 0;
		int size = 0;
		if (screen != null) {
			if (bindPackName == null
					|| pack.getName().equalsIgnoreCase(bindPackName)) {
				currentPack = pack;
			} else {
				if (screen instanceof Screen) {
					ScriptScreen script = (ScriptScreen) screen;
					currentPack = script.getPack(bindPackName);
					if (currentPack == null) {
						currentPack = pack;
					}
					size = script.getPackSprites().size();
				}
			}
			elapsedTime = screen.elapsedTime;
		} else {
			currentPack = pack;
		}

		if (lightingOn) {
			GLUtils.setShadeModelSmooth(GLEx.gl10);
		}

		boolean update = (size != 0 && currentPack == pack);

		GLEx gl = GLEx.self;

		int old = gl.getBlendMode();
		gl.setBlendMode(GL.MODE_SPEED);

		if (update || lazy != lazyHashCode) {
			currentPack.glBegin();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					PackTile tile = tiles[x][y];
					if (tile != null) {
						sx = minX + x;
						sy = minY + y;
						if (sx + tile.width() < minX || sx > maxX
								|| sy + tile.height() < minY || sy > maxY) {
							continue;
						}
						if (lightingOn) {
							setLightColor(sx, sy);
						}
						tile.update(elapsedTime);
						tile.draw(currentPack,
								posX + view.worldToRealX(sx * tileWidth), posY
										+ view.worldToRealY(sy * tileHeight),
								colors);
					}
				}
			}
			currentPack.glEnd();
			lazyHashCode = lazy;

		} else {

			currentPack.glCache();
		}

		gl.setBlendMode(old);

		if (lightingOn) {
			GLUtils.setShadeModelFlat(GLEx.gl10);
		}

		return update;

	}

	public String getName() {
		return name;
	}

	public PackTileFactory getTileFactory() {
		return tileFactory;
	}

	public void setTileFactory(PackTileFactory tileFactory) {
		this.tileFactory = tileFactory;
		this.dirty = true;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public Field2D getField2D() {
		return field;
	}

	public int[] getLimit() {
		return limit;
	}

	public PackView getView() {
		return view;
	}

	public void setView(PackView view) {
		this.view = view;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public void setPos(int x, int y) {
		this.posX = x;
		this.posY = y;
	}

	public Screen getScreen() {
		return screen;
	}

	public void dispose() {
		if (tiles != null) {
			tiles = null;
		}
		if (field != null) {
			field = null;
		}
		if (tileFactory != null) {
			if (tileFactory instanceof SimplePackTileFactory) {
				tileFactory = null;
			}
		}
		dirty = false;
		init = false;
	}

}
