/**
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.action.map.colider;

public class TileHelper implements Tile {

	protected int id = -1;

	protected int tileX = 0;

	protected int tileY = 0;

	protected int tileWidth = 0;

	protected int tileHeight = 0;

	public TileHelper(int x, int y) {
		this(-1, x, y, 32, 32);
	}

	public TileHelper(int id, int x, int y) {
		this(id, x, y, 32, 32);
	}

	public TileHelper(int id, int x, int y, int tileWidth, int tileHeight) {
		this.id = id;
		this.tileX = x;
		this.tileY = y;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public int getTileSizeX() {
		return tileWidth;
	}

	public void setTileSizeX(int tileSizeX) {
		this.tileWidth = tileSizeX;
	}

	public int getTileSizeY() {
		return tileHeight;
	}

	public void setTileSizeY(int tileSizeY) {
		this.tileHeight = tileSizeY;
	}

	@Override
	public int getX() {
		return tileY;
	}

	@Override
	public int getY() {
		return tileY;
	}

	@Override
	public int getWidth() {
		return tileWidth;
	}

	@Override
	public int getHeight() {
		return tileHeight;
	}

	@Override
	public void setX(int x) {
		this.tileX = x;
	}

	@Override
	public void setY(int y) {
		this.tileY = y;
	}

	@Override
	public void setWidth(int w) {
		this.tileWidth = w;
	}

	@Override
	public void setHeight(int h) {
		this.tileHeight = h;
	}

	public void setWidth(float w) {
		setWidth((int)w);
	}

	public void setHeight(float h) {
		setHeight((int)h);
	}
	
	@Override
	public Tile at(int x, int y) {
		return new TileHelper(-1, x, y, tileWidth, tileHeight);
	}

	@Override
	public Tile at(int id, int x, int y) {
		return new TileHelper(id, x, y, tileWidth, tileHeight);
	}

}
