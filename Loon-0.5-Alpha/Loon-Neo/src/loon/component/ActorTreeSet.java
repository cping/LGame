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
package loon.component;

import java.util.Arrays;

import loon.utils.CollectionUtils;
import loon.utils.LIterator;
import loon.utils.SortedList;

public class ActorTreeSet {

	boolean isDirty;

	private SortedList<ActorSet> subSets = new SortedList<ActorSet>();

	private ActorSet generalSet = new ActorSet();

	private ActorTreeSet.TasIterator iterator;

	public ActorTreeSet() {
		this.subSets.add(this.generalSet);
		this.iterator = new ActorTreeSet.TasIterator();
		this.iterator.reset(this.subSets);
	}

	public void clear() {
		if (subSets != null) {
			subSets.clear();
		}
		if (generalSet != null) {
			generalSet.clear();
		}
	}

	public LIterator<Actor> iterator() {
		iterator.reset(this.subSets);
		return iterator;
	}

	public Actor getOnlyCollisionObjectsAt(float x, float y) {
		for (LIterator<Actor> it = iterator(); it.hasNext();) {
			Actor a = it.next();
			if (a.getRectBox().contains(x, y)) {
				return a;
			}
		}
		return null;
	}

	public Actor getOnlyCollisionObjectsAt(float x, float y, Object tag) {
		for (LIterator<Actor> it = iterator(); it.hasNext();) {
			Actor a = (Actor) it.next();
			if (a.getRectBox().contains(x, y) && a.getTag() == tag) {
				return a;
			}
		}
		return null;
	}

	public Actor getSynchronizedObject(float x, float y) {
		LIterator<Actor> iter = iterator();
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

	public int size() {
		int size = 0;
		for (LIterator<ActorSet> i = this.subSets.listIterator(); i.hasNext(); size += (i
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
				Actor[] o = generalSet.toArray();
				int size = o.length;
				if (o == null || size <= 0) {
					return;
				}
				if (o[size - 1] == actor) {
					return;
				}
				for (int i = 0; i < size; i++) {
					if (o[i] == actor) {
						o = CollectionUtils.cut(o, i);
						o = CollectionUtils.expand(o, 1, true);
						o[size - 1] = actor;
						Arrays.sort(o);
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
				Actor[] o = generalSet.toArray();
				int size = o.length;
				if (o == null || size <= 0) {
					return;
				}
				if (o[0] == actor) {
					return;
				}
				for (int i = 0; i < size; i++) {
					if (o[i] == actor) {
						o = CollectionUtils.cut(o, i);
						o = CollectionUtils.expand(o, 1, false);
						o[0] = actor;
						Arrays.sort(o);
						break;
					}
				}
				generalSet.clear();
				generalSet.addAll(o);
			}
		}
	}

	class TasIterator implements LIterator<Actor> {

		private LIterator<ActorSet> setIterator;

		private ActorSet currentSet;

		private LIterator<Actor> actorIterator;

		public TasIterator() {
	
		}

		public void reset(SortedList<ActorSet> soered) {
			this.setIterator = soered.listIterator();
			for (this.currentSet = this.setIterator.next(); this.currentSet
					.size() == 0 && this.setIterator.hasNext(); this.currentSet = this.setIterator
					.next()) {
			}
			this.actorIterator = this.currentSet.iterator();
		}

		@Override
		public void remove() {
			this.actorIterator.remove();
		}

		@Override
		public Actor next() {
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
					this.currentSet = this.setIterator.next();
					if (this.currentSet.size() != 0) {
						break;
					}
				}
				this.actorIterator = this.currentSet.iterator();
				return this.actorIterator.hasNext();
			}
		}

	}
}
