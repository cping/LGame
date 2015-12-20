package org.loon.framework.android.game.core.graphics.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */

@SuppressWarnings({"unchecked","rawtypes"})
public class CollisionManager implements CollisionChecker {

	private HashMap freeObjects = new HashMap();

	private HashSet collisionClasses = new HashSet();

	private CollisionChecker collisionChecker = new BSPCollisionChecker();

	public void initialize(int cellSize) {
		this.collisionChecker.initialize(cellSize);
	}

	public void clear() {
		synchronized (collisionChecker) {
			if (collisionChecker != null) {
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

	private void makeCollisionObjects(Class cls, boolean includeSubclasses) {
		Set entries;
		Iterator it;
		Entry entry;
		if (cls == null) {
			entries = this.freeObjects.entrySet();
			it = entries.iterator();
			while (it.hasNext()) {
				entry = (Entry) it.next();
				Iterator itr = ((LinkedList) entry.getValue()).iterator();

				while (itr.hasNext()) {
					Actor actor = (Actor) itr.next();
					this.collisionChecker.addObject(actor);
				}

				this.collisionClasses.add(entry.getKey());
			}
			this.freeObjects.clear();
		} else if (!this.collisionClasses.contains(cls)) {
			List entries2 = (List) this.freeObjects.remove(cls);
			if (entries2 != null) {
				this.collisionClasses.add(cls);
				it = entries2.iterator();

				while (it.hasNext()) {
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
				if (cls.isAssignableFrom((Class) entry.getKey())) {
					this.makeCollisionObjects((Class) entry.getKey(), false);
				}
			}
		}
	}

	private void prepareForCollision(Actor actor, Class cls) {
		this.makeCollisionObjects(actor.getClass(), false);
		this.makeCollisionObjects(cls, true);
	}

	public void addObject(Actor actor) {
		Class cls = actor.getClass();
		if (this.collisionClasses.contains(cls)) {
			this.collisionChecker.addObject(actor);
		} else {
			LinkedList classSet = (LinkedList) this.freeObjects.get(cls);
			if (classSet == null) {
				classSet = new LinkedList();
				this.freeObjects.put(cls, classSet);
			}
			classSet.add(actor);
		}
	}

	public List getIntersectingObjects(Actor actor, Class cls) {
		this.prepareForCollision(actor, cls);
		return this.collisionChecker.getIntersectingObjects(actor, cls);
	}

	public List getNeighbours(Actor actor, int distance, boolean diag, Class cls) {
		this.prepareForCollision(actor, cls);
		return this.collisionChecker.getNeighbours(actor, distance, diag, cls);
	}

	public List getObjects(Class cls) {
		List result = this.collisionChecker.getObjects(cls);
		Set entries = this.freeObjects.entrySet();
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			if (cls == null || cls.isAssignableFrom((Class) entry.getKey())) {
				result.addAll((Collection) entry.getValue());
			}
		}
		return result;
	}

	public List getObjectsAt(int x, int y, Class cls) {
		this.makeCollisionObjects(cls, true);
		return this.collisionChecker.getObjectsAt(x, y, cls);
	}

	public List getObjectsInRange(int x, int y, int r, Class cls) {
		this.makeCollisionObjects(cls, true);
		return this.collisionChecker.getObjectsInRange(x, y, r, cls);
	}

	public List getObjectsList() {
		return this.getObjects((Class) null);
	}

	public Actor getOnlyIntersectingObject(Actor object, Class cls) {
		this.prepareForCollision(object, cls);
		return this.collisionChecker.getOnlyIntersectingObject(object, cls);
	}

	public Actor getOnlyObjectAt(Actor object, int dx, int dy, Class cls) {
		this.prepareForCollision(object, cls);
		return this.collisionChecker.getOnlyObjectAt(object, dx, dy, cls);
	}

	public void removeObject(Actor object) {
		LinkedList classSet = (LinkedList) this.freeObjects.get(object
				.getClass());
		if (classSet != null) {
			classSet.remove(object);
		} else {
			this.collisionChecker.removeObject(object);
		}
	}

	public void removeObject(Class cls) {
		LinkedList classSet = (LinkedList) this.freeObjects.get(cls);
		if (collisionClasses != null) {
			collisionClasses.remove(cls);
		}
		if (classSet != null) {
			classSet.remove(cls);
		}
	}

	public void updateObjectLocation(Actor object, int oldX, int oldY) {
		if (!this.freeObjects.containsKey(object.getClass())) {
			this.collisionChecker.updateObjectLocation(object, oldX, oldY);
		}
	}

	public void updateObjectSize(Actor object) {
		if (!this.freeObjects.containsKey(object.getClass())) {
			this.collisionChecker.updateObjectSize(object);
		}

	}
}
