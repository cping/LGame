/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.collision;

public class CollisionNode {

	private CollisionObject _actor;

	private BSPCollisionNode _node;

	private CollisionNode _next;

	private CollisionNode _prev;

	private boolean _mark;

	public CollisionNode(CollisionObject _actor, BSPCollisionNode _node) {
		this._actor = _actor;
		this._node = _node;
		CollisionNode first = BSPCollisionChecker.getNodeForActor(_actor);
		this._next = first;
		BSPCollisionChecker.setNodeForActor(_actor, this);
		if (this._next != null) {
			this._next._prev = this;
		}

		this._mark = true;
	}

	public CollisionNode clearMark() {
		this._mark = false;
		return this;
	}

	public CollisionNode mark() {
		this._mark = true;
		return this;
	}

	public boolean checkMark() {
		boolean markVal = this._mark;
		this._mark = false;
		return markVal;
	}

	public CollisionObject getActor() {
		return this._actor;
	}

	public BSPCollisionNode getBSPNode() {
		return this._node;
	}

	public CollisionNode getNext() {
		return this._next;
	}

	public CollisionNode remove() {
		this.removed();
		this._node.actorRemoved(this._actor);
		return this;
	}

	public CollisionNode removed() {
		if (this._prev == null) {
			BSPCollisionChecker.setNodeForActor(this._actor, this._next);
		} else {
			this._prev._next = this._next;
		}
		if (this._next != null) {
			this._next._prev = this._prev;
		}
		return this;
	}

	public CollisionNode dispose() {
		if (_node != null) {
			_node = null;
		}
		if (_next != null) {
			_next.dispose();
			_next = null;
		}
		if (_prev != null) {
			_prev.dispose();
			_prev = null;
		}
		return this;
	}
}
