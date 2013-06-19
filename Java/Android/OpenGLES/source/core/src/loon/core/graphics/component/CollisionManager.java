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
package loon.core.graphics.component;

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

	private void makeCollisionObjects(Class cls, boolean includeSubclasses) {
		Set entries;
		Iterator it;
		Entry entry;
		if (cls == null) {
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
		} else if (!this.collisionClasses.contains(cls)) {
			List entries2 = (List) this.freeObjects.remove(cls);
			if (entries2 != null) {
				this.collisionClasses.add(cls);
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

	@Override
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

	@Override
	public List getIntersectingObjects(Actor actor, Class cls) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, cls);
			return this.collisionChecker.getIntersectingObjects(actor, cls);
		}
	}

	@Override
	public List getNeighbours(Actor actor, float distance, boolean diag,
			Class cls) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(actor, cls);
			return this.collisionChecker.getNeighbours(actor, distance, diag,
					cls);
		}
	}

	@Override
	public List getObjects(Class cls) {
		synchronized (CollisionManager.class) {
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
	}

	@Override
	public List getObjectsAt(float x, float y, Class cls) {
		synchronized (CollisionManager.class) {
			this.makeCollisionObjects(cls, true);
			return this.collisionChecker.getObjectsAt(x, y, cls);
		}
	}

	@Override
	public List getObjectsInRange(float x, float y, float r, Class cls) {
		synchronized (CollisionManager.class) {
			this.makeCollisionObjects(cls, true);
			return this.collisionChecker.getObjectsInRange(x, y, r, cls);
		}

	}

	@Override
	public List getObjectsList() {
		synchronized (CollisionManager.class) {
			return this.getObjects((Class) null);

		}
	}

	@Override
	public Actor getOnlyIntersectingObject(Actor object, Class cls) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(object, cls);
			return this.collisionChecker.getOnlyIntersectingObject(object, cls);
		}
	}

	@Override
	public Actor getOnlyObjectAt(Actor object, float dx, float dy, Class cls) {
		synchronized (CollisionManager.class) {
			this.prepareForCollision(object, cls);
			return this.collisionChecker.getOnlyObjectAt(object, dx, dy, cls);
		}
	}

	@Override
	public void removeObject(Actor object) {
		synchronized (CollisionManager.class) {
			LinkedList classSet = (LinkedList) this.freeObjects.get(object
					.getClass());
			if (classSet != null) {
				classSet.remove(object);
			} else {
				this.collisionChecker.removeObject(object);
			}
		}
	}

	public void removeObject(Class cls) {
		synchronized (CollisionManager.class) {
			LinkedList classSet = (LinkedList) this.freeObjects.get(cls);
			if (collisionClasses != null) {
				collisionClasses.remove(cls);
			}
			if (classSet != null) {
				classSet.remove(cls);
			}
		}
	}

	@Override
	public void updateObjectLocation(Actor object, float oldX, float oldY) {
		synchronized (CollisionManager.class) {
			if (!this.freeObjects.containsKey(object.getClass())) {
				this.collisionChecker.updateObjectLocation(object, oldX, oldY);
			}
		}
	}

	@Override
	public void updateObjectSize(Actor object) {
		synchronized (CollisionManager.class) {
			if (!this.freeObjects.containsKey(object.getClass())) {
				this.collisionChecker.updateObjectSize(object);
			}
		}
	}

	@Override
	public void dispose() {
		synchronized (CollisionManager.class) {
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
	}

	@Override
	public Iterator getActorsIterator() {
		synchronized (CollisionManager.class) {
			return collisionChecker.getActorsIterator();
		}
	}

	@Override
	public List getActorsList() {
		synchronized (CollisionManager.class) {
			return collisionChecker.getActorsList();
		}
	}

}
