/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.collision;

import loon.geom.Vector2f;
import loon.utils.LIterator;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.ObjectSet;
import loon.utils.SortedList;
import loon.utils.TArray;

/**
 * CollisionManager是一個手动的碰撞管理器,用来管理与检测指定[CollisionObject]对象的碰撞行为
 */
public class CollisionManager implements CollisionChecker {

	private final ObjectMap<String, SortedList<CollisionObject>> _freeObjects = new ObjectMap<String, SortedList<CollisionObject>>();

	private final ObjectSet<String> _collisionClasses = new ObjectSet<String>();

	private final CollisionChecker _collisionChecker = new BSPCollisionChecker();

	public CollisionManager() {
		initialize(32);
	}

	@Override
	public void initialize(int cellSize) {
		this._collisionChecker.initialize(cellSize);
	}

	@Override
	public void initialize(int cellSizeX, int cellSizeY) {
		this._collisionChecker.initialize(cellSizeX, cellSizeY);
	}

	@Override
	public void clear() {
		synchronized (CollisionManager.class) {
			if (_collisionChecker != null) {
				_collisionChecker.dispose();
				_collisionChecker.clear();
			}
			if (_freeObjects != null) {
				_freeObjects.clear();
			}
			if (_collisionClasses != null) {
				_collisionClasses.clear();
			}
		}
	}

	private void makeCollisionObjects(String flag, boolean includeSubclasses) {
		if (flag == null) {
			Entries<String, SortedList<CollisionObject>> entries = this._freeObjects.entries();
			for (; entries.hasNext();) {
				Entry<String, SortedList<CollisionObject>> entry = entries.next();
				LIterator<CollisionObject> itr = (entry.value).listIterator();
				for (; itr.hasNext();) {
					CollisionObject actor = itr.next();
					this._collisionChecker.addObject(actor);
				}
				this._collisionClasses.add(entry.key);
			}
			this._freeObjects.clear();
		} else if (!this._collisionClasses.contains(flag)) {
			SortedList<CollisionObject> entries2 = this._freeObjects.remove(flag);
			if (entries2 != null) {
				this._collisionClasses.add(flag);
				LIterator<CollisionObject> it = entries2.listIterator();
				for (; it.hasNext();) {
					CollisionObject entry1 = it.next();
					this._collisionChecker.addObject(entry1);
				}
			}
		}
		if (includeSubclasses) {
			Entries<String, SortedList<CollisionObject>> entries = this._freeObjects.entries();
			for (; entries.hasNext();) {
				Entry<String, SortedList<CollisionObject>> entry = entries.next();
				if (flag != null && flag.equals(entry.key)) {
					this.makeCollisionObjects(entry.key, false);
				}
			}
		}
	}

	private void prepareForCollision(CollisionObject actor, String flag) {
		this.makeCollisionObjects(actor.getObjectFlag(), false);
		this.makeCollisionObjects(flag, true);
	}

	@Override
	public void addObject(CollisionObject actor) {
		String flag = actor.getObjectFlag();
		if (this._collisionClasses.contains(flag)) {
			this._collisionChecker.addObject(actor);
		} else {
			SortedList<CollisionObject> classSet = this._freeObjects.get(flag);
			if (classSet == null) {
				classSet = new SortedList<CollisionObject>();
				this._freeObjects.put(flag, classSet);
			}
			classSet.add(actor);
		}
	}

	@Override
	public TArray<CollisionObject> getIntersectingObjects(CollisionObject actor, String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this._collisionChecker.getIntersectingObjects(actor, flag);
		}
	}

	@Override
	public TArray<CollisionObject> getNeighbours(CollisionObject actor, float distance, boolean diag, String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this._collisionChecker.getNeighbours(actor, distance, diag, flag);
		}
	}

	@Override
	public TArray<CollisionObject> getObjects(String flag) {
		TArray<CollisionObject> result = this._collisionChecker.getObjects(flag);
		Entries<String, SortedList<CollisionObject>> entries = this._freeObjects.entries();
		for (; entries.hasNext();) {
			Entry<String, SortedList<CollisionObject>> entry = entries.next();
			if (flag == null || flag.equals(entry.key)) {
				for (LIterator<CollisionObject> it = entry.value.listIterator(); it.hasNext();) {
					result.add(it.next());
				}
			}
		}
		return result;
	}

	@Override
	public TArray<CollisionObject> getObjectsAt(float x, float y, String flag) {
		this.makeCollisionObjects(flag, true);
		return this._collisionChecker.getObjectsAt(x, y, flag);

	}

	@Override
	public TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String flag) {
		this.makeCollisionObjects(flag, true);
		return this._collisionChecker.getObjectsInRange(x, y, r, flag);

	}

	@Override
	public TArray<CollisionObject> getObjectsList() {
		return this.getObjects((String) null);
	}

	@Override
	public CollisionObject getOnlyIntersectingObject(CollisionObject obj, String flag) {
		this.prepareForCollision(obj, flag);
		return this._collisionChecker.getOnlyIntersectingObject(obj, flag);
	}

	@Override
	public CollisionObject getOnlyObjectAt(CollisionObject obj, float dx, float dy, String flag) {
		this.prepareForCollision(obj, flag);
		return this._collisionChecker.getOnlyObjectAt(obj, dx, dy, flag);
	}

	@Override
	public void removeObject(CollisionObject obj) {
		SortedList<CollisionObject> classSet = this._freeObjects.get(obj.getObjectFlag());
		if (classSet != null) {
			classSet.remove(obj);
		} else {
			this._collisionChecker.removeObject(obj);
		}
	}

	public void removeObject(String flag) {
		SortedList<CollisionObject> classSet = this._freeObjects.get(flag);
		if (_collisionClasses != null) {
			_collisionClasses.remove(flag);
		}
		if (classSet != null) {
			classSet.remove(flag);
		}
	}

	@Override
	public void updateObjectLocation(CollisionObject obj, float oldX, float oldY) {
		if (!this._freeObjects.containsKey(obj.getObjectFlag())) {
			this._collisionChecker.updateObjectLocation(obj, oldX, oldY);
		}
	}

	@Override
	public void updateObjectSize(CollisionObject obj) {
		if (!this._freeObjects.containsKey(obj.getObjectFlag())) {
			this._collisionChecker.updateObjectSize(obj);
		}
	}

	@Override
	public LIterator<CollisionObject> getActorsIterator() {
		return _collisionChecker.getActorsIterator();
	}

	@Override
	public TArray<CollisionObject> getActorsList() {
		return _collisionChecker.getActorsList();
	}

	@Override
	public void setOffsetPos(float x, float y) {
		_collisionChecker.setOffsetPos(x, y);
	}

	@Override
	public void setOffsetX(float x) {
		_collisionChecker.setOffsetX(x);
	}

	@Override
	public void setOffsetY(float y) {
		_collisionChecker.setOffsetY(y);
	}

	@Override
	public Vector2f getOffsetPos() {
		return _collisionChecker.getOffsetPos();
	}

	@Override
	public int numberActors() {
		return _collisionChecker.numberActors();
	}

	@Override
	public void setInTheLayer(boolean yes) {
		_collisionChecker.setInTheLayer(yes);
	}

	@Override
	public boolean getInTheLayer() {
		return _collisionChecker.getInTheLayer();
	}


	@Override
	public void dispose() {
		if (_freeObjects != null) {
			_freeObjects.clear();
		}
		if (_collisionClasses != null) {
			_collisionClasses.clear();
		}
		if (_collisionChecker != null) {
			_collisionChecker.dispose();
			_collisionChecker.clear();
		}
	}
}
