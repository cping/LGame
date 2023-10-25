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
import loon.geom.RectF;
import loon.geom.Vector2f;
import loon.geom.XYZW;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;

/**
 * 瓦片房间用类,用于记录特定瓦片的地图转换关系
 */
public class TileRoom extends RectF {

	public static final int ALL = 0;
	public static final int LEFT = 1;
	public static final int TOP = 2;
	public static final int RIGHT = 3;
	public static final int BOTTOM = 4;

	public static class RoomLink {

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

	private ObjectMap<String, Door> _exits = new ObjectMap<String, Door>();

	private ObjectMap<TileRoom, RoomLink> _connected = new ObjectMap<TileRoom, RoomLink>();

	private TArray<TileRoom> _connectedRooms = new TArray<TileRoom>();

	private boolean _locked = false;

	private boolean _joined;

	public TileRoom(int id, XYZW rect) {
		this(id, rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public TileRoom(int id, float roomx, float roomy, float roomwidth, float roomheight) {
		this._roomId = id;
		this.set(roomx, roomy, roomwidth, roomheight);
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

	public boolean addRoom(TileRoom other) {
		if (other == null) {
			return false;
		}
		if (_connectedRooms.contains(other)) {
			return true;
		}
		RectF f = getIntersection(other);
		if ((f.width == 0 && f.height >= 2) || (f.height == 0 && f.width >= 2)) {
			_connectedRooms.add(other);
			other._connectedRooms.add(this);
			return true;
		}
		return false;
	}

	public boolean connect(TileRoom room) {
		if ((_connectedRooms.contains(room) || addRoom(room)) && !_connected.containsKey(room) && canConnect(room)) {
			_connected.put(room, null);
			room._connected.put(this, null);
			return true;
		}
		return false;
	}

	public TileRoom connect(TileRoom other, int roomx, int roomy) {
		RoomLink record = new RoomLink(other, roomx, roomy);
		if (this._connected.get(other) == null) {
			this._connected.put(other, record);
			this._connectedRooms.add(other);
			other.connect(this, roomx, roomy);
		}
		return this;
	}

	public TileRoom set(TileRoom other) {
		super.set(other);
		final int size = other._connectedRooms.size;
		for (int i = 0; i < size; i++) {
			TileRoom r = other._connectedRooms.get(i);
			_connectedRooms.add(r);
			r._connectedRooms.remove(other);
			r._connectedRooms.add(this);
		}
		for (Entries<TileRoom, RoomLink> it = other._connected.iterator(); it.hasNext();) {
			Entry<TileRoom, RoomLink> v = it.next();
			TileRoom r = v.getKey();
			RoomLink l = v.getValue();
			r._connected.remove(other);
			r._connected.put(this, l);
			_connected.put(r, l);
		}
		return this;
	}

	public ObjectMap<TileRoom, RoomLink> connected() {
		return this._connected;
	}

	public int getCenterX() {
		return (int) super.centerX();
	}

	public int getCenterY() {
		return (int) super.centerY();
	}

	public Vector2f center() {
		return new Vector2f((getX() + getWidth()) / 2 + (((getWidth() - getX()) % 2) == 1 ? MathUtils.nextInt(2) : 0),
				(getY() + getHeight()) / 2 + (((getHeight() - getY()) % 2) == 1 ? MathUtils.nextInt(2) : 0));
	}

	public Vector2f pointInside(Vector2f from, int n) {
		Vector2f step = new Vector2f(from);
		if (from.x == getX()) {
			step.move(n, 0f);
		} else if (from.x == getWidth()) {
			step.move(-n, 0f);
		} else if (from.y == getY()) {
			step.move(0f, n);
		} else if (from.y == getHeight()) {
			step.move(0f, -n);
		}
		return step;
	}

	public Vector2f randomPos() {
		return randomPos(1);
	}

	public Vector2f randomPos(int m) {
		return new Vector2f(MathUtils.random(getX() + m, getWidth() - m),
				MathUtils.random(getY() + m, getHeight() - m));
	}

	public boolean inside(Vector2f p) {
		return p.x > getX() && p.y > getY() && p.x < getWidth() && p.y < getHeight();
	}

	public TArray<TileRoom> connectedRooms() {
		return this._connectedRooms;
	}

	public RoomLink getDoor(TileRoom room) {
		return this._connected.get(room);
	}

	public TileRoom convert(Field2D field, int ins, int outs) {
		final int w = MathUtils.ifloor(this.getWidth());
		final int h = MathUtils.ifloor(this.getHeight());
		for (int xp = 0; xp < w; xp++) {
			for (int yp = 0; yp < h; yp++) {
				final int nx = MathUtils.ifloor(this.getX() + xp);
				final int ny = MathUtils.ifloor(this.getY() + yp);
				if (field.getTileType(nx, ny) == ins) {
					field.setTileType(nx, ny, outs);
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

	public TileRoom setId(int i) {
		this._roomId = i;
		return this;
	}

	public int minConnections(int direction) {
		if (direction == ALL) {
			return 1;
		} else {
			return 0;
		}
	}

	public int maxConnections(int direction) {
		if (direction == ALL) {
			return 16;
		} else {
			return 4;
		}
	}

	public int curConnections(int direction) {
		if (direction == ALL) {
			return _connected.size();
		} else {
			int total = 0;
			for (Entries<TileRoom, RoomLink> it = _connected.iterator(); it.hasNext();) {
				Entry<TileRoom, RoomLink> v = it.next();
				TileRoom r = v.getKey();
				RectF f = getIntersection(r);
				if (direction == LEFT && f.getWidth() == 0 && f.left() == left()) {
					total++;
				} else if (direction == TOP && f.getHeight() == 0 && f.top() == top()) {
					total++;
				} else if (direction == RIGHT && f.getWidth() == 0 && f.right() == right()) {
					total++;
				} else if (direction == BOTTOM && f.getHeight() == 0 && f.bottom() == bottom()) {
					total++;
				}
			}
			return total;
		}
	}

	public int remConnections(int direction) {
		if (curConnections(ALL) >= maxConnections(ALL)) {
			return 0;
		} else {
			return maxConnections(direction) - curConnections(direction);
		}
	}

	public boolean canConnect(Vector2f p) {
		return (p.x == x || p.x == width) != (p.y == y || p.y == height);
	}

	public boolean canConnect(int direction) {
		return remConnections(direction) > 0;
	}

	public boolean canConnect(TileRoom r) {
		RectF f = getIntersection(r);
		boolean foundPoint = false;
		for (Vector2f p : f.getAllPoints()) {
			if (canConnect(p) && r.canConnect(p)) {
				foundPoint = true;
				break;
			}
		}
		if (!foundPoint) {
			return false;
		}
		if (f.getWidth() == 0 && f.left() == left()) {
			return canConnect(LEFT) && r.canConnect(RIGHT);
		} else if (f.getHeight() == 0 && f.top() == top()) {
			return canConnect(TOP) && r.canConnect(BOTTOM);
		} else if (f.getWidth() == 0 && f.right() == right()) {
			return canConnect(RIGHT) && r.canConnect(LEFT);
		} else if (f.getHeight() == 0 && f.bottom() == bottom()) {
			return canConnect(BOTTOM) && r.canConnect(TOP);
		} else {
			return false;
		}
	}

	public Door getExit(String direction) {
		return _exits.get(direction);
	}

	public TileRoom addDoor(String direction, Door d) {
		_exits.put(direction, d);
		return this;
	}

}
