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

import loon.utils.LIterator;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.ObjectSet;
import loon.utils.SortedList;
import loon.utils.TArray;

public class CollisionManager implements CollisionChecker {

	private ObjectMap<String, SortedList<CollisionObject>> freeObjects = new ObjectMap<String, SortedList<CollisionObject>>();

	private ObjectSet<String> collisionClasses = new ObjectSet<String>();

	private CollisionChecker collisionChecker = new BSPCollisionChecker();

	@Override
	public void initialize(int cellSize) {
		this.collisionChecker.initialize(cellSize);
	}

	@Override
	public void clear() {
		synchronized (CollisionManager.class) {
			if (collisionChecker != null) {
				collisionChecker.dispose();
				collisionChecker.clear();
			}
			if (freeObjects != null) {
				freeObjects.clear();
			}
			if (collisionClasses != null) {
				collisionClasses.clear();
			}
		}
	}

	private void makeCollisionObjects(String flag, boolean includeSubclasses) {
		if (flag == null) {
			Entries<String, SortedList<CollisionObject>> entries = this.freeObjects
					.entries();
			for (; entries.hasNext();) {
				Entry<String, SortedList<CollisionObject>> entry = entries.next();
				LIterator<CollisionObject> itr = (entry.value).listIterator();
				for (; itr.hasNext();) {
					CollisionObject actor = itr.next();
					this.collisionChecker.addObject(actor);
				}
				this.collisionClasses.add(entry.key);
			}
			this.freeObjects.clear();
		} else if (!this.collisionClasses.contains(flag)) {
			SortedList<CollisionObject> entries2 = this.freeObjects.remove(flag);
			if (entries2 != null) {
				this.collisionClasses.add(flag);
				LIterator<CollisionObject> it = entries2.listIterator();
				for (; it.hasNext();) {
					CollisionObject entry1 = it.next();
					this.collisionChecker.addObject(entry1);
				}
			}
		}
		if (includeSubclasses) {
			Entries<String, SortedList<CollisionObject>> entries = this.freeObjects
					.entries();
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
		if (this.collisionClasses.contains(flag)) {
			this.collisionChecker.addObject(actor);
		} else {
			SortedList<CollisionObject> classSet = this.freeObjects.get(flag);
			if (classSet == null) {
				classSet = new SortedList<CollisionObject>();
				this.freeObjects.put(flag, classSet);
			}
			classSet.add(actor);
		}
	}

	@Override
	public TArray<CollisionObject> getIntersectingObjects(CollisionObject actor, String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this.collisionChecker.getIntersectingObjects(actor, flag);
		}
	}

	@Override
	public TArray<CollisionObject> getNeighbours(CollisionObject actor, float distance, boolean diag,
			String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this.collisionChecker.getNeighbours(actor, distance, diag,
					flag);
		}
	}

	@Override
	public TArray<CollisionObject> getObjects(String flag) {
		TArray<CollisionObject> result = this.collisionChecker.getObjects(flag);
		Entries<String, SortedList<CollisionObject>> entries = this.freeObjects.entries();
		for (; entries.hasNext();) {
			Entry<String, SortedList<CollisionObject>> entry = entries.next();
			if (flag == null || flag.equals(entry.key)) {
				for (LIterator<CollisionObject> it = entry.value.listIterator(); it
						.hasNext();) {
					result.add(it.next());
				}
			}
		}
		return result;
	}

	@Override
	public TArray<CollisionObject> getObjectsAt(float x, float y, String flag) {
		this.makeCollisionObjects(flag, true);
		return this.collisionChecker.getObjectsAt(x, y, flag);

	}

	@Override
	public TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String flag) {
		this.makeCollisionObjects(flag, true);
		return this.collisionChecker.getObjectsInRange(x, y, r, flag);

	}

	@Override
	public TArray<CollisionObject> getObjectsList() {
		return this.getObjects((String) null);
	}

	@Override
	public CollisionObject getOnlyIntersectingObject(CollisionObject object, String flag) {
		this.prepareForCollision(object, flag);
		return this.collisionChecker.getOnlyIntersectingObject(object, flag);
	}

	@Override
	public CollisionObject getOnlyObjectAt(CollisionObject object, float dx, float dy, String flag) {
		this.prepareForCollision(object, flag);
		return this.collisionChecker.getOnlyObjectAt(object, dx, dy, flag);
	}

	@Override
	public void removeObject(CollisionObject object) {
		SortedList<CollisionObject> classSet = this.freeObjects.get(object
				.getObjectFlag());
		if (classSet != null) {
			classSet.remove(object);
		} else {
			this.collisionChecker.removeObject(object);
		}
	}

	public void removeObject(String flag) {
		SortedList<CollisionObject> classSet = this.freeObjects.get(flag);
		if (collisionClasses != null) {
			collisionClasses.remove(flag);
		}
		if (classSet != null) {
			classSet.remove(flag);
		}
	}

	@Override
	public void updateObjectLocation(CollisionObject object, float oldX, float oldY) {
		if (!this.freeObjects.containsKey(object.getObjectFlag())) {
			this.collisionChecker.updateObjectLocation(object, oldX, oldY);
		}
	}

	@Override
	public void updateObjectSize(CollisionObject object) {
		if (!this.freeObjects.containsKey(object.getObjectFlag())) {
			this.collisionChecker.updateObjectSize(object);
		}
	}

	@Override
	public void dispose() {
		if (freeObjects != null) {
			freeObjects.clear();
			freeObjects = null;
		}
		if (collisionClasses != null) {
			collisionClasses.clear();
			collisionClasses = null;
		}
		if (collisionChecker != null) {
			collisionChecker.dispose();
			collisionChecker.clear();
			collisionChecker = null;
		}
	}

	@Override
	public LIterator<CollisionObject> getActorsIterator() {
		return collisionChecker.getActorsIterator();
	}

	@Override
	public TArray<CollisionObject> getActorsList() {
		return collisionChecker.getActorsList();
	}

}
