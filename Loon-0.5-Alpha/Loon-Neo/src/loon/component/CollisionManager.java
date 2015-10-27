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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CollisionManager implements CollisionChecker {

	private HashMap freeObjects = new HashMap();

	private HashSet collisionClasses = new HashSet();

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
		Set entries;
		Iterator it;
		Entry entry;
		if (flag == null) {
			entries = this.freeObjects.entrySet();
			it = entries.iterator();
			for (; it.hasNext();) {
				entry = (Entry) it.next();
				Iterator itr = ((LinkedList) entry.getValue()).iterator();
				for (; itr.hasNext();) {
					Actor actor = (Actor) itr.next();
					this.collisionChecker.addObject(actor);
				}
				this.collisionClasses.add(entry.getKey());
			}
			this.freeObjects.clear();
		} else if (!this.collisionClasses.contains(flag)) {
			List entries2 = (List) this.freeObjects.remove(flag);
			if (entries2 != null) {
				this.collisionClasses.add(flag);
				it = entries2.iterator();
				for (; it.hasNext();) {
					Actor entry1 = (Actor) it.next();
					this.collisionChecker.addObject(entry1);
				}
			}
		}
		if (includeSubclasses) {
			entries = this.freeObjects.entrySet();
			HashSet entries1 = new HashSet(entries);
			it = entries1.iterator();
			while (it.hasNext()) {
				entry = (Entry) it.next();
				if (flag != null
						&& flag.equals(
								(String) entry.getKey())) {
					this.makeCollisionObjects((String) entry.getKey(), false);
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
			LinkedList classSet = (LinkedList) this.freeObjects.get(flag);
			if (classSet == null) {
				classSet = new LinkedList();
				this.freeObjects.put(flag, classSet);
			}
			classSet.add(actor);
		}
	}

	@Override
	public List getIntersectingObjects(Actor actor, String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this.collisionChecker.getIntersectingObjects(actor, flag);
		}
	}

	@Override
	public List getNeighbours(Actor actor, float distance, boolean diag,
			String flag) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, flag);
			return this.collisionChecker.getNeighbours(actor, distance, diag,
					flag);
		}
	}

	@Override
	public List getObjects(String flag) {
			List result = this.collisionChecker.getObjects(flag);
			Set entries = this.freeObjects.entrySet();
			Iterator it = entries.iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				if (flag == null
						|| flag.equals(
								(String) entry.getKey())) {
					result.addAll((Collection) entry.getValue());
				}
			}
			return result;
	}

	@Override
	public List getObjectsAt(float x, float y, String flag) {
			this.makeCollisionObjects(flag, true);
			return this.collisionChecker.getObjectsAt(x, y, flag);
		
	}

	@Override
	public List getObjectsInRange(float x, float y, float r, String flag) {
			this.makeCollisionObjects(flag, true);
			return this.collisionChecker.getObjectsInRange(x, y, r, flag);

	}

	@Override
	public List getObjectsList() {
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
			LinkedList classSet = (LinkedList) this.freeObjects.get(object
					.getClass());
			if (classSet != null) {
				classSet.remove(object);
			} else {
				this.collisionChecker.removeObject(object);
			}
	}

	public void removeObject(String flag) {
			LinkedList classSet = (LinkedList) this.freeObjects.get(flag);
			if (collisionClasses != null) {
				collisionClasses.remove(flag);
			}
			if (classSet != null) {
				classSet.remove(flag);
			}
	}

	@Override
	public void updateObjectLocation(Actor object, float oldX, float oldY) {
			if (!this.freeObjects.containsKey(object.getClass())) {
				this.collisionChecker.updateObjectLocation(object, oldX, oldY);
			}
	}

	@Override
	public void updateObjectSize(Actor object) {
			if (!this.freeObjects.containsKey(object.getClass())) {
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
	public Iterator getActorsIterator() {
			return collisionChecker.getActorsIterator();
	}

	@Override
	public List getActorsList() {
			return collisionChecker.getActorsList();
	}

}
