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

	public static class Flips {

		public float angle = 0f;
		public float cos = 0f;
		public float sin = 0f;
		public float newX = 0f;
		public float newY = 0f;
		public boolean flip = false;

	}

	private int _tileSetID;
	private int _id;
	private int _gid;

	private boolean _flippedHorizontally;
	private boolean _flippedVertically;
	private boolean _flippedDiagonally;

	public TMXMapTile(int gid, int tileSetFirstID, int tileSetID) {

		this._tileSetID = tileSetID;

		_flippedHorizontally = (gid & TMXMap.FLIPPED_HORIZONTALLY_FLAG) != 0;
		_flippedVertically = (gid & TMXMap.FLIPPED_VERTICALLY_FLAG) != 0;
		_flippedDiagonally = (gid & TMXMap.FLIPPED_DIAGONALLY_FLAG) != 0;

		this._gid = (int) (gid & ~(TMXMap.FLIPPED_HORIZONTALLY_FLAG | TMXMap.FLIPPED_VERTICALLY_FLAG
				| TMXMap.FLIPPED_DIAGONALLY_FLAG));
		this._id = gid - tileSetFirstID;
	}

	public int getTileSetID() {
		return _tileSetID;
	}

	public int getID() {
		return _id;
	}

	public int getGID() {
		return _gid;
	}

	public boolean isFlippedHorizontally() {
		return _flippedHorizontally;
	}

	public boolean isFlippedVertically() {
		return _flippedVertically;
	}

	public boolean isFlippedDiagonally() {
		return _flippedDiagonally;
	}

	public Flips getFlips() {
		return getFlips(0f, 0f, 0f, 0f);
	}

	public Flips getFlips(float x, float y, float cx, float cy) {
		Flips flips = new Flips();
		if (!_flippedDiagonally && !_flippedVertically && !_flippedHorizontally) {
			flips.angle = 0f;
			flips.cos = 1f;
			flips.sin = 0f;
			flips.flip = false;
		} else if (!_flippedDiagonally && !_flippedVertically && _flippedHorizontally) {
			flips.angle = 0f;
			flips.cos = 1f;
			flips.sin = 0f;
			flips.flip = true;
		} else if (_flippedDiagonally && !_flippedVertically && _flippedHorizontally) {
			flips.angle = 1f;
			flips.cos = 0f;
			flips.sin = 1f;
			flips.flip = false;
		} else if (_flippedDiagonally && _flippedVertically && _flippedHorizontally) {
			flips.angle = 1f;
			flips.cos = 0f;
			flips.sin = 1f;
			flips.flip = true;
		} else if (!_flippedDiagonally && _flippedVertically && _flippedHorizontally) {
			flips.angle = 2f;
			flips.cos = -1f;
			flips.sin = 0f;
			flips.flip = false;
		} else if (!_flippedDiagonally && _flippedVertically && !_flippedHorizontally) {
			flips.angle = 2f;
			flips.cos = -1f;
			flips.sin = 0f;
			flips.flip = true;
		} else if (_flippedDiagonally && _flippedVertically && !_flippedHorizontally) {
			flips.angle = 3f;
			flips.cos = 0f;
			flips.sin = -1f;
			flips.flip = false;
		} else if (_flippedDiagonally && !_flippedVertically && !_flippedHorizontally) {
			flips.angle = 3f;
			flips.cos = 0f;
			flips.sin = -1f;
			flips.flip = true;
		}
		flips.newX = cx + (x - cx) * flips.cos - (y - cy) * flips.sin;
		flips.newY = cy + (x - cx) * flips.sin + (y - cy) * flips.cos;
		return flips;
	}

	@Override
	public int hashCode() {
		int result = _id;
		result = LSystem.unite(result, _gid);
		result = LSystem.unite(result, _tileSetID);
		result = LSystem.unite(result, _flippedHorizontally);
		result = LSystem.unite(result, _flippedVertically);
		result = LSystem.unite(result, _flippedDiagonally);
		return result;
	}
}
