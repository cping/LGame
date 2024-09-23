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
package loon.action.map;

import loon.LObject;
import loon.geom.Vector2f;

/**
 * 2D地图专用的碰撞管理接口
 */
public interface TileMapCollision {

	Vector2f getTileCollision(LObject<?> o, float newX, float newY);

	int tilesToPixelsX(float x);

	int tilesToPixelsY(float y);

	int pixelsToTilesWidth(float x);

	int pixelsToTilesHeight(float y);

	boolean isHit(int px, int py);

	boolean isPixelHit(int px, int py);

	boolean isPixelTUp(int px, int py);

	boolean isPixelTRight(int px, int py);

	boolean isPixelTLeft(int px, int py);

	boolean isPixelTDown(int px, int py);

	Field2D getField2D();

	Vector2f getOffset();

	int getTileWidth();

	int getTileHeight();

	float getHeight();

	float getWidth();

	int getRow();

	int getCol();

	int[][] getMap();
}
