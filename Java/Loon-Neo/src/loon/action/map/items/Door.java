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
package loon.action.map.items;

public class Door {

	private boolean _open;

	private TileRoom _r1;

	private TileRoom _r2;

	private String _lockedMessage;

	public Door(TileRoom r1, TileRoom r2) {
		this(r1, r2, "Room" + r1.getId(), "Room" + r2.getId());
	}

	public Door(TileRoom r1, TileRoom r2, String dir1, String dir2) {
		this._r1 = r1;
		this._r2 = r2;
		_r1.addDoor(dir1, this);
		_r2.addDoor(dir2, this);
		this._lockedMessage = "";
	}

	public Door(TileRoom r1, TileRoom r2, String dir1, String dir2, String m) {
		this(r1, r2, dir1, dir2);
		this._lockedMessage = m;
	}

	public String getLockedMessage() {
		return _lockedMessage;
	}

	public Door setLockedMessage(String m) {
		this._lockedMessage = m;
		return this;
	}

	public TileRoom getRoom1() {
		return _r1;
	}

	public TileRoom getRoom2() {
		return _r2;
	}

	public Door setOpen(boolean o) {
		this._open = o;
		return this;
	}

	public boolean isOpen() {
		return _open;
	}

}
