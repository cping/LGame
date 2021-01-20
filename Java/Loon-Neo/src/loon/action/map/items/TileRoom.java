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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.action.map.items;

import loon.action.map.Field2D;
import loon.utils.ObjectMap;
import loon.utils.TArray;

/**
 * 瓦片房间用类,用于记录特定瓦片的地图转换关系
 */
public class TileRoom {

	class RoomLink {

		private TileRoom _room;

		int _roomx, _roomy;

		public RoomLink(TileRoom room, int x, int y) {
			this._roomx = x;
			this._roomy = y;
			this._room = room;
		}

		@Override
		public int hashCode() {
			return this._room.hashCode() + this._roomx + this._roomy;
		}

		@Override
		public boolean equals(Object other) {
			RoomLink o = (RoomLink) other;
			return (o._room == this._room) && (this._roomx == o._roomx) && (this._roomy == o._roomy);
		}

		public int getX() {
			return this._roomx;
		}

		public int getY() {
			return this._roomy;
		}
	}

	private int _roomId;

	private int _roomx;

	private int _roomy;

	private int _roomwidth;

	private int _roomheight;

	private ObjectMap<TileRoom, RoomLink> _connected = new ObjectMap<TileRoom, RoomLink>();

	private TArray<TileRoom> _connectedRooms = new TArray<TileRoom>();

	private boolean _locked = false;

	private boolean _joined;

	public TileRoom(int i, int _roomx, int _roomy, int _roomwidth, int _roomheight) {
		this._roomId = i;
		this._roomx = _roomx;
		this._roomy = _roomy;
		this._roomwidth = _roomwidth;
		this._roomheight = _roomheight;
	}

	public int getWidth() {
		return this._roomwidth;
	}

	public int getHeight() {
		return this._roomheight;
	}

	public int getX() {
		return this._roomx;
	}

	public int getY() {
		return this._roomy;
	}

	public TileRoom setLock(boolean l) {
		this._locked = l;
		return this;
	}

	public boolean isLock() {
		return this._locked;
	}

	public int getDoorX(TileRoom other) {
		RoomLink record = this._connected.get(other);
		return record._roomx;
	}

	public int getDoorY(TileRoom other) {
		RoomLink record = this._connected.get(other);
		return record._roomy;
	}

	public TileRoom connect(TileRoom other, int _roomx, int _roomy) {
		RoomLink record = new RoomLink(other, _roomx, _roomy);
		if (this._connected.get(other) == null) {
			this._connected.put(other, record);
			this._connectedRooms.add(other);
			other.connect(this, _roomx, _roomy);
		}
		return this;
	}

	public boolean contains(int xp, int yp) {
		return (xp >= this._roomx) && (yp >= this._roomy) && (xp < this._roomx + this._roomwidth) && (yp < this._roomy + this._roomheight);
	}

	public ObjectMap<TileRoom, RoomLink> _connected() {
		return this._connected;
	}

	public int getCenterX() {
		return this._roomx + this._roomwidth / 2;
	}

	public int getCenterY() {
		return this._roomy + this._roomheight / 2;
	}

	public TArray<TileRoom> _connectedRooms() {
		return this._connectedRooms;
	}

	public RoomLink getDoor(TileRoom room) {
		return this._connected.get(room);
	}

	public TileRoom convert(Field2D field, int in, int out) {
		for (int xp = 0; xp < this._roomwidth; xp++) {
			for (int yp = 0; yp < this._roomheight; yp++) {
				if (field.getTileType(this._roomx + xp, this._roomy + yp) == in) {
					field.setTileType(this._roomx + xp, this._roomy + yp, out);
				}
			}
		}
		return this;
	}

	public boolean isJoined() {
		return this._joined;
	}

	public TileRoom setJoined(boolean j) {
		this._joined = j;
		return this;
	}

	public int getId() {
		return _roomId;
	}

	public void setId(int i) {
		this._roomId = i;
	}
}
