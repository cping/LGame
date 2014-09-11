
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

public class TileHelper {

	protected int tileWidth = 0;
	
	protected int tileHeight = 0;
	
	public TileHelper(int tileWidth, int tileHeight) {
		super();
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public TileHelper(int tileWidth, int tileHeight, int offsetX, int offsetY) {
		super();
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
}
