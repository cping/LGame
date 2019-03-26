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
package loon.component;

import loon.utils.LIterator;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;
import loon.utils.ObjectSet;
import loon.utils.SortedList;
import loon.utils.TArray;

public class CollisionManager implements CollisionChecker {

	private ObjectMap<String, SortedList<Actor>> freeObjects = new ObjectMap<String, SortedList<Actor>>();

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
			Entries<String, SortedList<Actor>> entries = this.freeObjects
					.entries();
			for (; entries.hasNext();) {
				Entry<String, SortedList<Actor>> entry = entries.next();
				LIterator<Actor> itr = (entry.value).listIterator();
				for (; itr.hasNext();) {
					Actor actor = itr.next();
					this.collisionChecker.addObject(actor);
				}
				this.collisionClasses.add(entry.key);
			}
			this.freeObjects.clear();
		} else if (!this.collisionClasses.contains(flag)) {
			SortedList<Actor> entries2 = this.freeObjects.remove(flag);
			if (entries2 != null) {
				this.collisionClasses.add(flag);
				LIterator<Actor> it = entries2.listIterator();
				for (; it.hasNext();) {
					Actor entry1 = it.next();
					this.collisionChecker.addObject(entry1);
				}
			}
		}
		if (includeSubclasses) {
			Entries<String, SortedList<Actor>> entries = this.freeObjects
					.entries();
			for (; entries.hasNext();) {
				Entry<String, SortedList<Actor>> entry = entries.next();
				if (flag != null && flag.equals(entry.key)) {
					this.makeCollisionObjects(entry.key, false);
				}
			}
		}
	}

	private void prepareForCollision(Actor actor, String flag) {
		this.makeCollisionObjects(actor.getFlag(), false);
		this.makeCollisionObjects(flag, true);
	}

	@Override
	public void addObject(Actor actor) {
		String flag = actor.getFlag();
		if (this.collisionClasses.contains(flag)) {
			this.collisionChecker.addObject(actor);
		} else {
			SortedList<Actor> classSet = this.freeObjects.get(flag);
			if (classSet == null) {
				classSet = new SortedList<Actor>();
				this.freeObjects.put(flag, classSet);
			}
			classSet.add(actor);
		}
	}

	@Override
	public TArray<Actor> getIntersectingObjects(Actor actor, String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this.collisionChecker.getIntersectingObjects(actor, flag);
		}
	}

	@Override
	public TArray<Actor> getNeighbours(Actor actor, float distance, boolean diag,
			String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this.collisionChecker.getNeighbours(actor, distance, diag,
					flag);
		}
	}

	@Override
	public TArray<Actor> getObjects(String flag) {
		TArray<Actor> result = this.collisionChecker.getObjects(flag);
		Entries<String, SortedList<Actor>> entries = this.freeObjects.entries();
		for (; entries.hasNext();) {
			Entry<String, SortedList<Actor>> entry = entries.next();
			if (flag == null || flag.equals(entry.key)) {
				for (LIterator<Actor> it = entry.value.listIterator(); it
						.hasNext();) {
					result.add(it.next());
				}
			}
		}
		return result;
	}

	@Override
	public TArray<Actor> getObjectsAt(float x, float y, String flag) {
		this.makeCollisionObjects(flag, true);
		return this.collisionChecker.getObjectsAt(x, y, flag);

	}

	@Override
	public TArray<Actor> getObjectsInRange(float x, float y, float r, String flag) {
		this.makeCollisionObjects(flag, true);
		return this.collisionChecker.getObjectsInRange(x, y, r, flag);

	}

	@Override
	public TArray<Actor> getObjectsList() {
		return this.getObjects((String) null);
	}

	@Override
	public Actor getOnlyIntersectingObject(Actor object, String flag) {
		this.prepareForCollision(object, flag);
		return this.collisionChecker.getOnlyIntersectingObject(object, flag);
	}

	@Override
	public Actor getOnlyObjectAt(Actor object, float dx, float dy, String flag) {
		this.prepareForCollision(object, flag);
		return this.collisionChecker.getOnlyObjectAt(object, dx, dy, flag);
	}

	@Override
	public void removeObject(Actor object) {
		SortedList<Actor> classSet = this.freeObjects.get(object
				.getFlag());
		if (classSet != null) {
			classSet.remove(object);
		} else {
			this.collisionChecker.removeObject(object);
		}
	}

	public void removeObject(String flag) {
		SortedList<Actor> classSet = this.freeObjects.get(flag);
		if (collisionClasses != null) {
			collisionClasses.remove(flag);
		}
		if (classSet != null) {
			classSet.remove(flag);
		}
	}

	@Override
	public void updateObjectLocation(Actor object, float oldX, float oldY) {
		if (!this.freeObjects.containsKey(object.getFlag())) {
			this.collisionChecker.updateObjectLocation(object, oldX, oldY);
		}
	}

	@Override
	public void updateObjectSize(Actor object) {
		if (!this.freeObjects.containsKey(object.getFlag())) {
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
	public LIterator<Actor> getActorsIterator() {
		return collisionChecker.getActorsIterator();
	}

	@Override
	public TArray<Actor> getActorsList() {
		return collisionChecker.getActorsList();
	}

}
