package org.loon.framework.javase.game.action;

import java.util.HashMap;
import java.util.LinkedList;

import org.loon.framework.javase.game.action.map.AStarFinder;
import org.loon.framework.javase.game.action.map.Field2D;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.geom.Vector2D;
import org.loon.framework.javase.game.core.graphics.component.Actor;
import org.loon.framework.javase.game.core.graphics.component.ActorLayer;
import org.loon.framework.javase.game.utils.CollectionUtils;

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
public class MoveTo extends ActionEvent {

	private final static HashMap<Integer, LinkedList<Vector2D>> pathCache = new HashMap<Integer, LinkedList<Vector2D>>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	private Vector2D startLocation, endLocation;

	private Field2D layerMap;

	private boolean flag, useCache, synchroLayerField;

	private LinkedList<Vector2D> tmp_path;

	private int startX, startY, endX, endY, moveX, moveY;

	private int direction, speed;

	public MoveTo(final Field2D map, int x, int y, boolean flag) {
		this.startLocation = new Vector2D();
		this.endLocation = new Vector2D(x, y);
		this.layerMap = map;
		this.flag = flag;
		this.speed = 4;
		this.useCache = true;
		this.synchroLayerField = false;
	}

	public MoveTo(final Field2D map, Vector2D pos, boolean flag) {
		this(map, pos.x(), pos.y(), flag);
	}

	public void onLoad() {
		if (layerMap == null || original == null) {
			return;
		}
		if (!original.getRectBox().contains(endLocation.x(), endLocation.y())) {
			if (useCache) {
				synchronized (pathCache) {
					if (pathCache.size() > LSystem.DEFAULT_MAX_CACHE_SIZE * 10) {
						pathCache.clear();
					}
					int key = hashCode();
					LinkedList<Vector2D> final_path = pathCache.get(key);
					if (final_path == null) {
						final_path = AStarFinder
								.find(
										layerMap,
										layerMap
												.pixelsToTilesWidth(startLocation
														.x()),
										layerMap
												.pixelsToTilesHeight(startLocation
														.y()),
										layerMap.pixelsToTilesWidth(endLocation
												.x()),
										layerMap
												.pixelsToTilesHeight(endLocation
														.y()), flag);
						pathCache.put(key, final_path);
					}
					tmp_path = new LinkedList<Vector2D>();
					tmp_path.addAll(final_path);
				}
			} else {
				tmp_path = AStarFinder.find(layerMap, layerMap
						.pixelsToTilesWidth(startLocation.x()), layerMap
						.pixelsToTilesHeight(startLocation.y()), layerMap
						.pixelsToTilesWidth(endLocation.x()), layerMap
						.pixelsToTilesHeight(endLocation.y()), flag);

			}
		}
	}

	public void clearPath() {
		if (tmp_path != null) {
			synchronized (tmp_path) {
				tmp_path.clear();
				tmp_path = null;
			}
		}
	}

	public static void clearPathCache() {
		if (pathCache != null) {
			synchronized (pathCache) {
				pathCache.clear();
			}
		}
	}

	public int hashCode() {
		if (layerMap == null || original == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, flag);
		hashCode = LSystem.unite(hashCode, layerMap
				.pixelsToTilesWidth((int) original.getX()));
		hashCode = LSystem.unite(hashCode, layerMap
				.pixelsToTilesHeight((int) original.getY()));
		hashCode = LSystem.unite(hashCode, layerMap
				.pixelsToTilesWidth(endLocation.x()));
		hashCode = LSystem.unite(hashCode, layerMap
				.pixelsToTilesHeight(endLocation.y()));
		hashCode = LSystem.unite(hashCode, layerMap.getWidth());
		hashCode = LSystem.unite(hashCode, layerMap.getHeight());
		hashCode = LSystem.unite(hashCode, layerMap.getTileWidth());
		hashCode = LSystem.unite(hashCode, layerMap.getTileHeight());
		hashCode = LSystem.unite(hashCode, CollectionUtils.hashCode(layerMap
				.getMap()));
		return hashCode;
	}

	public void start(Actor target) {
		super.start(target);
		startLocation.set(target.getX(), target.getY());
	}

	public LinkedList<Vector2D> getPath() {
		return tmp_path;
	}

	public int getDirection() {
		return direction;
	}

	public void setField2D(Field2D field) {
		if (field != null) {
			this.layerMap = field;
		}
	}

	public Field2D getField2D() {
		return layerMap;
	}

	public void update(long elapsedTime) {
		if (layerMap == null || original == null || tmp_path == null) {
			return;
		}
		synchronized (tmp_path) {
			if (synchroLayerField) {
				if (original != null) {
					ActorLayer layer = original.getLLayer();
					Field2D field = layer.getField2D();
					if (field != null && layerMap != field) {
						this.layerMap = field;
					}
				}
			}
			if (endX == startX && endY == startY) {
				if (tmp_path.size() > 1) {
					Vector2D moveStart = (Vector2D) tmp_path.get(0);
					Vector2D moveEnd = (Vector2D) tmp_path.get(1);
					startX = layerMap.tilesToWidthPixels(moveStart.x());
					startY = layerMap.tilesToHeightPixels(moveStart.y());
					endX = moveEnd.x() * layerMap.getTileWidth();
					endY = moveEnd.y() * layerMap.getTileHeight();
					moveX = moveEnd.x() - moveStart.x();
					moveY = moveEnd.y() - moveStart.y();
					if (moveX > -2 && moveY > -2 && moveX < 2 && moveY < 2) {
						direction = Field2D.getDirection(moveX, moveY);
					}
				}
				tmp_path.remove(0);
			}
			switch (direction) {
			case Field2D.TUP:
				startY -= speed;
				if (startY < endY) {
					startY = endY;
				}
				break;
			case Field2D.TDOWN:
				startY += speed;
				if (startY > endY) {
					startY = endY;
				}
				break;
			case Field2D.TLEFT:
				startX -= speed;
				if (startX < endX) {
					startX = endX;
				}
				break;
			case Field2D.TRIGHT:
				startX += speed;
				if (startX > endX) {
					startX = endX;
				}
				break;
			case Field2D.UP:
				startX += speed;
				startY -= speed;
				if (startX > endX) {
					startX = endX;
				}
				if (startY < endY) {
					startY = endY;
				}
				break;
			case Field2D.DOWN:
				startX -= speed;
				startY += speed;
				if (startX < endX) {
					startX = endX;
				}
				if (startY > endY) {
					startY = endY;
				}
				break;
			case Field2D.LEFT:
				startX -= speed;
				startY -= speed;
				if (startX < endX) {
					startX = endX;
				}
				if (startY < endY) {
					startY = endY;
				}
				break;
			case Field2D.RIGHT:
				startX += speed;
				startY += speed;
				if (startX > endX) {
					startX = endX;
				}
				if (startY > endY) {
					startY = endY;
				}
				break;
			}
			original.setLocation(startX, startY);
		}
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isComplete() {
		return tmp_path == null || tmp_path.size() == 0 || isComplete
				|| original == null;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isSynchroLayerField() {
		return synchroLayerField;
	}

	public void setSynchroLayerField(boolean syn) {
		this.synchroLayerField = syn;
	}
}
