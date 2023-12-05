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
package loon.action.map.tmx.tiles;

import loon.LSystem;
import loon.action.map.tmx.TMXMap;

public class TMXMapTile {
	private int tileSetID;
	private int id;
	private int gid;

	private boolean flippedHorizontally;
	private boolean flippedVertically;
	private boolean flippedDiagonally;

	public TMXMapTile(int gid, int tileSetFirstID, int tileSetID) {

		this.tileSetID = tileSetID;

		flippedHorizontally = (gid & TMXMap.FLIPPED_HORIZONTALLY_FLAG) != 0;
		flippedVertically = (gid & TMXMap.FLIPPED_VERTICALLY_FLAG) != 0;
		flippedDiagonally = (gid & TMXMap.FLIPPED_DIAGONALLY_FLAG) != 0;

		this.gid = (int) (gid & ~(TMXMap.FLIPPED_HORIZONTALLY_FLAG | TMXMap.FLIPPED_VERTICALLY_FLAG
				| TMXMap.FLIPPED_DIAGONALLY_FLAG));
		this.id = gid - tileSetFirstID;
	}

	public int getTileSetID() {
		return tileSetID;
	}

	public int getID() {
		return id;
	}

	public int getGID() {
		return gid;
	}

	public boolean isFlippedHorizontally() {
		return flippedHorizontally;
	}

	public boolean isFlippedVertically() {
		return flippedVertically;
	}

	public boolean isFlippedDiagonally() {
		return flippedDiagonally;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = LSystem.unite(result, gid);
		result = LSystem.unite(result, tileSetID);
		result = LSystem.unite(result, flippedHorizontally);
		result = LSystem.unite(result, flippedVertically);
		result = LSystem.unite(result, flippedDiagonally);
		return result;
	}
}
