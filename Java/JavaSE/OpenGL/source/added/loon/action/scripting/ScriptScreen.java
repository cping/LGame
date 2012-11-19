package loon.action.scripting;

import java.util.HashMap;
import java.util.LinkedList;

import loon.action.map.AStarFindHeuristic;
import loon.action.map.Field2D;
import loon.action.scripting.pack.PackAnimation;
import loon.action.scripting.pack.PackSprite;
import loon.action.scripting.pack.PackSprites;
import loon.action.scripting.pack.PackTile;
import loon.action.scripting.pack.PackTileFactory;
import loon.action.scripting.pack.PackTileMap;
import loon.action.scripting.pack.PackView;
import loon.core.geom.Vector2f;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexturePack;
import loon.core.input.LTouch;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;


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
public abstract class ScriptScreen extends Screen {

	final class Go {

		LTimer timer = new LTimer(0);

		float speed = 1.5f;

		int direction;

		boolean isComplete;

		PackSprite sprite;

		LinkedList<Vector2f> findPath;

		float startX, startY, endX, endY, moveX, moveY;

		public boolean isComplete() {
			return findPath == null || findPath.size() == 0 || isComplete;
		}

		public int getWidth() {
			if (sprite != null) {
				return sprite.getWidth();
			}
			return 0;
		}

		public int getHeight() {
			if (sprite != null) {
				return sprite.getHeight();
			}
			return 0;
		}

		public void update() {
			if (sprite != null) {
				sprite.setLocation(startX, startY);
				sprite.setDirection(direction);
			}
		}

	}

	private HashMap<PackSprite, Go> paths;

	private String packName, mapName;

	private String scriptFile;

	private ScriptFactory scripts;

	private PackSprites packSprites;

	private Callback callback;

	private PackView view;

	private LTexturePack tempPack;

	private PackTileMap tempMap;

	private boolean isPack, isMap;

	private PackTileFactory tileFactory;

	public ScriptScreen(String res) {
		if (res == null) {
			throw new RuntimeException("script name is null !");
		}
		this.scriptFile = res;
	}

	public void onCreate(int width, int height) {
		super.onCreate(width, height);
		this.isPack = false;
		this.isMap = false;
		if (paths != null) {
			paths.clear();
			paths = null;
		}
		this.paths = new HashMap<PackSprite, Go>(10);
		if (packSprites != null) {
			packSprites.clear();
			packSprites = null;
		}
		this.packSprites = new PackSprites();
		if (scripts != null) {
			scripts.clear();
			scripts = null;
		}
		this.scripts = new ScriptFactory(this, scriptFile);
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
		if (scripts != null) {
			scripts.setCallback(callback);
		}
	}

	public Callback onCallback() {
		return null;
	}

	public final void onLoad() {
		if (scripts != null) {
			scripts.load();
			packName = scripts.getPackName();
			mapName = scripts.getMapName();
			onLoading();
		}
	}

	public Go getGo(PackSprite sprite) {
		Go go = paths.get(sprite);
		if (go != null) {
			return go;
		}
		return null;
	}

	public void stopMove(PackSprite sprite) {
		Go go = paths.get(sprite);
		if (go != null) {
			go.isComplete = true;
		}
	}

	public void updateMoveSpeed(PackSprite sprite, float speed) {
		Go go = paths.get(sprite);
		if (go != null) {
			go.speed = speed;
		}
	}

	public void updateMoveDelay(PackSprite sprite, long delay) {
		Go go = paths.get(sprite);
		if (go != null) {
			go.timer.setDelay(delay);
		}
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			LTouch touch) {
		toMove(sprite, heuristic, touch.x(), touch.y(), true);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			LTouch touch, boolean flag) {
		toMove(sprite, heuristic, touch.x(), touch.y(), flag);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic, int x,
			int y) {
		toMove(sprite, heuristic, x, y, true);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic, int x,
			int y, boolean flag) {
		toMove(sprite, heuristic, 0, 2f, x, y, flag);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			LTouch touch, float speed) {
		toMove(sprite, heuristic, 0, speed, touch.x(), touch.y(), true);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			long delay, float speed, LTouch touch) {
		toMove(sprite, heuristic, delay, speed, touch.x(), touch.y(), true);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			long delay, float speed, LTouch touch, boolean flag) {
		toMove(sprite, heuristic, delay, speed, touch.x(), touch.y(), flag);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			long delay, float speed, int x, int y) {
		toMove(sprite, heuristic, delay, speed, x, y, true);
	}

	public void toMove(PackSprite sprite, AStarFindHeuristic heuristic,
			long delay, float speed, int x, int y, boolean flag) {
		toMove(findAStar(heuristic, sprite.x(), sprite.y(), x, y, flag),
				sprite, delay, speed);
	}

	public void toMove(LinkedList<Vector2f> path, PackSprite sprite,
			long delay, float speed) {
		synchronized (ScriptScreen.class) {
			Go go = paths.get(sprite);
			if (go == null) {
				go = new Go();
				paths.put(sprite, go);
			}
			if (go.findPath != null) {
				go.findPath.clear();
			}
			go.findPath = path;
			go.sprite = sprite;
			go.timer.setDelay(delay);
			go.speed = speed;
		}
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, LTouch touch) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.findAStar(heuristic, x1, y1, touch);
		}
		return null;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, boolean flag, LTouch touch) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.findAStar(heuristic, x1, y1, flag, touch);
		}
		return null;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, int x2, int y2) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.findAStar(heuristic, x1, y1, x2, y2);
		}
		return null;
	}

	public LinkedList<Vector2f> findAStar(AStarFindHeuristic heuristic, int x1,
			int y1, int x2, int y2, boolean flag) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.findAStar(heuristic, x1, y1, x2, y2, flag);
		}
		return null;
	}

	public void setLimit(int[] list) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.limit(list);
		}
	}

	public int[] getLimit() {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getLimit();
		}
		return null;
	}

	public void subMap(int x, int y, int w, int h) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.sub(x, y, w, h);
		}
	}

	public void subMap(int x, int y) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.sub(x, y);
		}
	}

	public void setMapPos(int x, int y) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.setPos(x, y);
		}
	}

	public PackSprite addPackSprite(String name, long delay, float x, float y) {
		return addPackSprite(name, getAnimation(name), delay, x, y);
	}

	public PackSprite addPackSprite(String name, float x, float y) {
		return addPackSprite(name, getAnimation(name), 150, x, y);
	}

	public PackSprite addPackSprite(String name, PackAnimation animation,
			float x, float y) {
		return addPackSprite(name, animation, 150, x, y);
	}

	public PackSprite addPackSprite(String name, PackAnimation animation,
			long delay, float x, float y) {
		if (packSprites != null) {
			if (animation != null) {
				PackSprite sprite = new PackSprite(animation);
				packSprites.add(sprite, x, y);
				sprite.setName(name);
				sprite.setDelay(delay);
				return sprite;
			} else {
				return null;
			}
		}
		return null;
	}

	public void removePackSprite(PackSprite sprite) {
		if (packSprites != null) {
			packSprites.remove(sprite);
		}
	}

	public PackSprite findPackSprite(String name) {
		return packSprites.find(name);
	}

	public void setMapBlockSize(int tileWidth, int tileHeight) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.setBlockSize(tileWidth, tileHeight);
		}
	}

	public int getMapWidth() {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getWidth();
		}
		return 0;
	}

	public int getMapHeight() {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getHeight();
		}
		return 0;
	}

	public PackTile getTile(int x, int y) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getTile(x, y);
		}
		return null;
	}

	public void setTile(int x, int y, PackTile type) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.setTile(x, y, type);
		}
	}

	public boolean collidesWith(PackSprite sprite) {
		return !canMove(sprite, sprite.getX(), sprite.getY());
	}

	public int count(PackTile element) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.count(element);
		}
		return 0;
	}

	public boolean canMove(PackSprite sprite, float x, float y) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.canMove(sprite, x, y);
		}
		return false;
	}

	public void touch(PackSprite sprite) {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			map.touch(sprite);
		}
	}

	public int getTileWidth() {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getTileWidth();
		}
		return 0;
	}

	public int getTileHeight() {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getTileHeight();
		}
		return 0;
	}

	public Field2D getField2D() {
		PackTileMap map = getMap(mapName);
		if (map != null) {
			return map.getField2D();
		}
		return null;
	}

	public abstract void onLoading();

	public final void onLoaded() {
		if (callback == null) {
			callback = onCallback();
		}
		if (scripts != null) {
			scripts.setCallback(callback);
		}
		this.setView(null);
		this.setPackName(packName);
		this.setMapName(mapName);
	}

	public void callScript(String name) {
		if (scripts != null) {
			if (packSprites != null) {
				packSprites.clear();
			}
			scripts.clear();
			scripts.call(name);
		}
	}

	public void clearScript() {
		if (scripts != null) {
			scripts.clear();
		}
	}

	public void clearSprite() {
		if (packSprites != null) {
			packSprites.clear();
		}
	}

	public void setView(PackView view) {
		if (view == null) {
			view = PackView.EmptyView.getInstance();
		}
		if (packSprites != null) {
			packSprites.setView(view);
		}
		PackTileMap map = scripts.getMap(mapName, tileFactory);
		if (map != null) {
			map.setView(view);
		}
		this.view = view;
	}

	public PackView getView() {
		return view;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String name) {
		this.mapName = name;
		if (name != null && scripts != null) {
			this.scripts.setMapName(name);
			this.mapName = name;
			this.tempMap = scripts.getMap(mapName, tileFactory);
			if (tempMap != null) {
				isMap = true;
			} else {
				isMap = false;
			}
		}
	}

	public void setPackName(String name) {
		if (name != null && scripts != null) {
			this.scripts.setPackName(name);
			this.packName = name;
			this.tempPack = scripts.getPack(packName);
			if (tempPack != null) {
				isPack = true;
			} else {
				isPack = false;
			}
		}
	}

	public String getPackName() {
		return this.packName;
	}

	public PackAnimation getAnimation(String name) {
		return scripts.getAnimation(name);
	}

	public LTexturePack getPack(String name) {
		return scripts.getPack(name);
	}

	public PackTileMap getMap() {
		return scripts.getMap(mapName);
	}

	public PackTileMap getMap(String name) {
		return scripts.getMap(name, tileFactory);
	}

	public Script getScript(String name) {
		return scripts.getScript(name);
	}

	private void going() {
		if (!isMap) {
			return;
		}
		if (paths.size() > 0) {
			Field2D field2D = getField2D();
			if (field2D == null) {
				return;
			}
			synchronized (paths) {
				for (Go go : paths.values()) {
					if (go == null || go.findPath == null) {
						continue;
					}
					if (go.isComplete()) {
						paths.remove(go.sprite);
						return;
					}
					if (go.timer.action(elapsedTime)) {
						synchronized (go.findPath) {
							if (go.endX == go.startX && go.endY == go.startY) {
								if (go.findPath != null) {
									if (go.findPath.size() > 1) {
										Vector2f moveStart = (Vector2f) go.findPath
												.get(0);
										Vector2f moveEnd = (Vector2f) go.findPath
												.get(1);
										go.startX = field2D
												.tilesToWidthPixels(moveStart
														.x());
										go.startY = field2D
												.tilesToHeightPixels(moveStart
														.y());
										go.endX = moveEnd.x()
												* field2D.getTileWidth();
										go.endY = moveEnd.y()
												* field2D.getTileHeight();
										go.moveX = moveEnd.x() - moveStart.x();
										go.moveY = moveEnd.y() - moveStart.y();
										go.direction = Field2D.getDirection(
												(int) go.moveX, (int) go.moveY);
										go.findPath.remove(0);
									} else {
										go.findPath.clear();
									}
								}
							}
							switch (go.direction) {
							case Field2D.TUP:
								go.startY -= go.speed;
								if (go.startY < go.endY) {
									go.startY = go.endY;
								}
								break;
							case Field2D.TDOWN:
								go.startY += go.speed;
								if (go.startY > go.endY) {
									go.startY = go.endY;
								}
								break;
							case Field2D.TLEFT:
								go.startX -= go.speed;
								if (go.startX < go.endX) {
									go.startX = go.endX;
								}
								break;
							case Field2D.TRIGHT:
								go.startX += go.speed;
								if (go.startX > go.endX) {
									go.startX = go.endX;
								}
								break;
							case Field2D.UP:
								go.startX += go.speed;
								go.startY -= go.speed;
								if (go.startX > go.endX) {
									go.startX = go.endX;
								}
								if (go.startY < go.endY) {
									go.startY = go.endY;
								}
								break;
							case Field2D.DOWN:
								go.startX -= go.speed;
								go.startY += go.speed;
								if (go.startX < go.endX) {
									go.startX = go.endX;
								}
								if (go.startY > go.endY) {
									go.startY = go.endY;
								}
								break;
							case Field2D.LEFT:
								go.startX -= go.speed;
								go.startY -= go.speed;
								if (go.startX < go.endX) {
									go.startX = go.endX;
								}
								if (go.startY < go.endY) {
									go.startY = go.endY;
								}
								break;
							case Field2D.RIGHT:
								go.startX += go.speed;
								go.startY += go.speed;
								if (go.startX > go.endX) {
									go.startX = go.endX;
								}
								if (go.startY > go.endY) {
									go.startY = go.endY;
								}
								break;
							}
							go.update();
						}
					}
				}
			}
		}
	}

	public final void alter(LTimerContext timer) {
		if (isOnLoadComplete()) {
			going();
			scripts.update();
			update(timer.getTimeSinceLastUpdate());
		}
	}

	public abstract void update(long elapsedTime);

	public final void draw(GLEx g) {
		if (isOnLoadComplete()) {
			if (isPack) {
				if (isMap) {
					if (!tempMap.drawSub(tempPack)) {
						packSprites.draw(tempPack, elapsedTime);
						return;
					}
				}
				if (packSprites.size() > 0) {
					tempPack.glBegin();
					packSprites.draw(tempPack, elapsedTime);
					tempPack.glEnd();
				}
			}
			paint(g);
		}
	}

	public abstract void paint(GLEx g);

	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String script) {
		this.scriptFile = script;
	}

	public PackTileFactory getTileFactory() {
		return tileFactory;
	}

	public void setTileFactory(PackTileFactory tileFactory) {
		this.tileFactory = tileFactory;
		PackTileMap map = getMap(mapName);
		if (map != null) {
			PackTileFactory factory = map.getTileFactory();
			if (factory != null && !"simple".equals(factory.getName())) {
				map.setTileFactory(tileFactory);
			}
		}
	}

	public boolean isMap() {
		return isMap;
	}

	public boolean isPack() {
		return isPack;
	}

	public PackSprites getPackSprites() {
		return packSprites;
	}

	public ScriptFactory getScriptFactory() {
		return scripts;
	}

	public void dispose() {
		this.isPack = false;
		this.isMap = false;
		if (paths != null) {
			paths.clear();
			paths = null;
		}
		if (scripts != null) {
			scripts.dispose();
			scripts = null;
		}
		if (packSprites != null) {
			packSprites.clear();
			packSprites = null;
		}
		if (paths != null) {
			paths.clear();
			paths = null;
		}
	}

}
