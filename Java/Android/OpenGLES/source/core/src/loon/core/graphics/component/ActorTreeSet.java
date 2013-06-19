package loon.core.graphics.component;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import loon.utils.CollectionUtils;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ActorTreeSet extends AbstractSet {

	private static final Comparator DEFAULT_COMPARATOR = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			return 0;
		}
	};

	boolean isDirty;

	private LinkedList subSets = new LinkedList();

	private ActorSet generalSet = new ActorSet();

	private ActorTreeSet.TasIterator iterator;

	public ActorTreeSet() {
		this.subSets.add(this.generalSet);
		this.iterator = new ActorTreeSet.TasIterator();
	}

	@Override
	public Iterator iterator() {
		iterator.reset();
		return iterator;
	}

	public Iterator newIterator() {
		return new ActorTreeSet.TasIterator();
	}

	public Actor getOnlyCollisionObjectsAt(float x, float y) {
		for (Iterator it = newIterator(); it.hasNext();) {
			Actor a = (Actor) it.next();
			if (a.getRectBox().contains(x, y)) {
				return a;
			}
		}
		return null;
	}

	public Actor getOnlyCollisionObjectsAt(float x, float y, Object tag) {
		for (Iterator it = newIterator(); it.hasNext();) {
			Actor a = (Actor) it.next();
			if (a.getRectBox().contains(x, y) && a.getTag() == tag) {
				return a;
			}
		}
		return null;
	}

	public Actor getSynchronizedObject(float x, float y) {
		Iterator<Actor> iter = newIterator();
		Actor tmp = iter.next();
		if (tmp == null) {
			return null;
		}
		int seq = tmp.getLastPaintSeqNum();
		int idx = 0;
		for (; iter.hasNext();) {
			Actor actor = iter.next();
			if (actor.getRectBox().contains(x, y)) {
				int actorSeq = actor.getLastPaintSeqNum();
				if (actorSeq > seq) {
					tmp = actor;
					seq = actorSeq;
				}
				idx++;
			}
		}
		if (idx == 0) {
			if (tmp.getRectBox().contains(x, y)) {
				return tmp;
			} else {
				return null;
			}
		}
		return tmp;
	}

	@Override
	public int size() {
		int size = 0;
		for (Iterator i = this.subSets.iterator(); i.hasNext(); size += ((ActorSet) i
				.next()).size()) {
		}
		return size;
	}

	public boolean add(Actor o) {
		if (o == null) {
			throw new RuntimeException("Null actor !");
		} else {
			return this.generalSet.add(o);
		}
	}

	public boolean remove(Actor o) {
		return this.generalSet.remove(o);
	}

	public boolean contains(Actor o) {
		return this.generalSet.contains(o);
	}

	public Object[] toActors() {
		return generalSet.toArray();
	}

	public void sendToFront(Actor actor) {
		if (generalSet != null) {
			synchronized (generalSet) {
				Object[] o = generalSet.toArray();
				int size = o.length;
				if (o == null || size <= 0) {
					return;
				}
				if (o[size - 1] == actor) {
					return;
				}
				for (int i = 0; i < size; i++) {
					if (o[i] == actor) {
						o = (Object[]) CollectionUtils.cut(o, i);
						o = (Object[]) CollectionUtils.expand(o, 1, true);
						o[size - 1] = actor;
						Arrays.sort(o, DEFAULT_COMPARATOR);
						break;
					}
				}
				generalSet.clear();
				generalSet.addAll(o);
			}
		}
	}

	public void sendToBack(Actor actor) {
		if (generalSet != null) {
			synchronized (generalSet) {
				Object[] o = generalSet.toArray();
				int size = o.length;
				if (o == null || size <= 0) {
					return;
				}
				if (o[0] == actor) {
					return;
				}
				for (int i = 0; i < size; i++) {
					if (o[i] == actor) {
						o = (Object[]) CollectionUtils.cut(o, i);
						o = (Object[]) CollectionUtils.expand(o, 1, false);
						o[0] = actor;
						Arrays.sort(o, DEFAULT_COMPARATOR);
						break;
					}
				}
				generalSet.clear();
				generalSet.addAll(o);
			}
		}
	}

	class TasIterator implements Iterator {

		private Iterator setIterator;

		private ActorSet currentSet;

		private Iterator actorIterator;

		public TasIterator() {
			reset();
		}

		public void reset() {
			this.setIterator = ActorTreeSet.this.subSets.iterator();
			for (this.currentSet = (ActorSet) this.setIterator.next(); this.currentSet
					.isEmpty() && this.setIterator.hasNext(); this.currentSet = (ActorSet) this.setIterator
					.next()) {
			}
			this.actorIterator = this.currentSet.iterator();
		}

		@Override
		public void remove() {
			this.actorIterator.remove();
		}

		@Override
		public Object next() {
			return this.actorIterator.next();
		}

		@Override
		public boolean hasNext() {
			if (this.actorIterator.hasNext()) {
				return true;
			} else if (!this.setIterator.hasNext()) {
				return false;
			} else {
				while (this.setIterator.hasNext()) {
					this.currentSet = (ActorSet) this.setIterator.next();
					if (!this.currentSet.isEmpty()) {
						break;
					}
				}
				this.actorIterator = this.currentSet.iterator();
				return this.actorIterator.hasNext();
			}
		}
	}
}
