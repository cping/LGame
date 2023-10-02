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

import loon.LSysException;
import loon.utils.CollectionUtils;
import loon.utils.LIterator;
import loon.utils.SortedList;

public class ActorTreeSet {

	protected boolean isDirty;

	private final SortedList<ActorSet> _subSets = new SortedList<>();

	private final ActorSet _generalSet = new ActorSet();

	public ActorTreeSet() {
		this._subSets.add(this._generalSet);
	}

	public void clear() {
		if (_subSets != null) {
			_subSets.clear();
		}
		if (_generalSet != null) {
			_generalSet.clear();
		}
	}

	public LIterator<Actor> iterator() {
		return new TasIterator(this._subSets);
	}

	public Actor getOnlyCollisionObjectsAt(float x, float y) {
		for (LIterator<Actor> it = iterator(); it.hasNext();) {
			Actor a = it.next();
			if (a.getRectBox().intersects(x, y)) {
				return a;
			}
		}
		return null;
	}

	public Actor getOnlyCollisionObjectsAt(float x, float y, Object tag) {
		for (LIterator<Actor> it = iterator(); it.hasNext();) {
			Actor a = it.next();
			if (a.getRectBox().intersects(x, y) && a.getTag() == tag) {
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
			if (actor.getRectBox().intersects(x, y)) {
				int actorSeq = actor.getLastPaintSeqNum();
				if (actorSeq > seq) {
					tmp = actor;
					seq = actorSeq;
				}
				idx++;
			}
		}
		if (idx == 0) {
			if (tmp.getRectBox().intersects(x, y)) {
				return tmp;
			} else {
				return null;
			}
		}
		return tmp;
	}

	public int size() {
		int size = 0;
		for (LIterator<ActorSet> i = this._subSets.listIterator(); i.hasNext(); size += (i
				.next()).size()) {
		}
		return size;
	}

	public boolean add(Actor o) {
		if (o == null) {
			throw new LSysException("Null actor !");
		} else {
			return this._generalSet.add(o);
		}
	}

	public boolean remove(Actor o) {
		return this._generalSet.remove(o);
	}

	public boolean contains(Actor o) {
		return this._generalSet.contains(o);
	}

	public Object[] toActors() {
		return _generalSet.toArray();
	}

	public void sendToFront(Actor actor) {
		if (_generalSet != null) {
			synchronized (_generalSet) {
				Actor[] o = _generalSet.toArray();
				int size = o.length;
				if (o == null || size <= 0 || (o[size - 1] == actor)) {
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
				_generalSet.clear();
				_generalSet.addAll(o);
			}
		}
	}

	public void sendToBack(Actor actor) {
		if (_generalSet != null) {
			synchronized (_generalSet) {
				Actor[] o = _generalSet.toArray();
				int size = o.length;
				if (o == null || size <= 0 || (o[0] == actor)) {
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
				_generalSet.clear();
				_generalSet.addAll(o);
			}
		}
	}

	class TasIterator implements LIterator<Actor> {

		private LIterator<ActorSet> setIterator;

		private ActorSet currentSet;

		private LIterator<Actor> actorIterator;

		TasIterator(SortedList<ActorSet> soered) {
			this.setIterator = soered.newListIterator();
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
