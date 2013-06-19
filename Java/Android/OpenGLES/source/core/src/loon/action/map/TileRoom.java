package loon.action.map;

import java.util.ArrayList;
import java.util.HashMap;

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
public class TileRoom {

	class RoomLink {

		private TileRoom room;

		int x, y;

		public RoomLink(TileRoom room, int x, int y) {
			this.x = x;
			this.y = y;
			this.room = room;
		}

		@Override
		public int hashCode() {
			return this.room.hashCode() + this.x + this.y;
		}

		@Override
		public boolean equals(Object other) {
			RoomLink o = (RoomLink) other;
			return (o.room == this.room) && (this.x == o.x) && (this.y == o.y);
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}
	}

	private int x;

	private int y;

	private int width;

	private int height;

	private HashMap<TileRoom, RoomLink> connected = new HashMap<TileRoom, RoomLink>();

	private ArrayList<TileRoom> connectedRooms = new ArrayList<TileRoom>();

	private boolean locked = false;

	private boolean joined;

	public TileRoom(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setLock(boolean l) {
		this.locked = l;
	}

	public boolean isLock() {
		return this.locked;
	}

	public int getDoorX(TileRoom other) {
		RoomLink record = this.connected.get(other);
		return record.x;
	}

	public int getDoorY(TileRoom other) {
		RoomLink record = this.connected.get(other);
		return record.y;
	}

	public void connect(TileRoom other, int x, int y) {
		RoomLink record = new RoomLink(other, x, y);
		if (this.connected.get(other) == null) {
			this.connected.put(other, record);
			this.connectedRooms.add(other);
			other.connect(this, x, y);
		}
	}

	public boolean contains(int xp, int yp) {
		return (xp >= this.x) && (yp >= this.y) && (xp < this.x + this.width)
				&& (yp < this.y + this.height);
	}

	public HashMap<TileRoom, RoomLink> connected() {
		return this.connected;
	}

	public int getCenterX() {
		return this.x + this.width / 2;
	}

	public int getCenterY() {
		return this.y + this.height / 2;
	}

	public ArrayList<TileRoom> connectedRooms() {
		return this.connectedRooms;
	}

	public RoomLink getDoor(TileRoom room) {
		return this.connected.get(room);
	}

	public void convert(Field2D field, int in, int out) {
		for (int xp = 0; xp < this.width; xp++) {
			for (int yp = 0; yp < this.height; yp++) {
				if (field.getType(this.x + xp, this.y + yp) == in) {
					field.setType(this.x + xp, this.y + yp, out);
				}
			}
		}
	}

	public boolean isJoined() {
		return this.joined;
	}

	public void setJoined(boolean j) {
		this.joined = j;
	}
}
