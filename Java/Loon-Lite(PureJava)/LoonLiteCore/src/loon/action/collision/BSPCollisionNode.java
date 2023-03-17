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
package loon.action.collision;

import loon.geom.RectBox;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Keys;
import loon.utils.TArray;

public final class BSPCollisionNode {

	private ObjectMap<CollisionObject, CollisionNode> _actors;

	private BSPCollisionNode _parent;

	private RectBox _area;

	private float _splitAxis;

	private float _splitPos;

	private BSPCollisionNode _left;

	private BSPCollisionNode _right;

	private boolean _areaRipple;

	public BSPCollisionNode(RectBox _area, int _splitAxis, int _splitPos) {
		this._area = _area;
		this._splitAxis = _splitAxis;
		this._splitPos = _splitPos;
		this._actors = new ObjectMap<>();
	}

	public void setChild(int side, BSPCollisionNode child) {
		if (side == 0) {
			this._left = child;
			if (child != null) {
				child._parent = this;
			}
		} else {
			this._right = child;
			if (child != null) {
				child._parent = this;
			}
		}
	}

	public void clear() {
		for (CollisionNode node : _actors.values()) {
			if (node != null) {
				node.dispose();
				node = null;
			}
		}
		_actors.clear();
	}

	public void setArea(RectBox _area) {
		this._area = _area;
		this._areaRipple = true;
	}

	public void setSplitAxis(float axis) {
		if (axis != this._splitAxis) {
			this._splitAxis = axis;
			this._areaRipple = true;
		}
	}

	public void setSplitPos(float pos) {
		if (pos != this._splitPos) {
			this._splitPos = pos;
			this._areaRipple = true;
		}

	}

	public float getSplitAxis() {
		return this._splitAxis;
	}

	public float getSplitPos() {
		return this._splitPos;
	}

	public RectBox getLeftArea() {
		return this._splitAxis == 0 ? new RectBox(this._area.getX(),
				this._area.getY(), this._splitPos - this._area.getX(),
				this._area.getHeight()) : new RectBox(this._area.getX(),
				this._area.getY(), this._area.getWidth(), this._splitPos
						- this._area.getY());
	}

	public RectBox getRightArea() {
		return this._splitAxis == 0 ? new RectBox(this._splitPos,
				this._area.getY(), this._area.getRight() - this._splitPos,
				this._area.getHeight()) : new RectBox(this._area.getX(),
				this._splitPos, this._area.getWidth(), this._area.getBottom()
						- this._splitPos);
	}

	public RectBox getArea() {
		return this._area;
	}

	private void resizeChildren() {
		if (this._left != null) {
			this._left.setArea(this.getLeftArea());
		}

		if (this._right != null) {
			this._right.setArea(this.getRightArea());
		}

	}

	public BSPCollisionNode getLeft() {
		if (this._areaRipple) {
			this.resizeChildren();
			this._areaRipple = false;
		}
		return this._left;
	}

	public BSPCollisionNode getRight() {
		if (this._areaRipple) {
			this.resizeChildren();
			this._areaRipple = false;
		}
		return this._right;
	}

	public BSPCollisionNode getParent() {
		return this._parent;
	}

	public void setParent(BSPCollisionNode p) {
		this._parent = p;
	}

	public int getChildSide(BSPCollisionNode child) {
		return this._left == child ? 0 : 1;
	}

	public void addActor(CollisionObject actor) {
		this._actors.put(actor, new CollisionNode(actor, this));
	}

	public boolean containsActor(CollisionObject actor) {
		CollisionNode anode = this._actors.get(actor);
		if (anode != null) {
			anode.mark();
			return true;
		} else {
			return false;
		}
	}

	public void actorRemoved(CollisionObject actor) {
		this._actors.remove(actor);
	}

	public int numberActors() {
		return this._actors.size;
	}

	public boolean isEmpty() {
		return this._actors.size == 0;
	}

	public Entries<CollisionObject, CollisionNode> getEntriesIterator() {
		return this._actors.entries();
	}

	public Keys<CollisionObject> getActorsIterator() {
		return this._actors.keys();
	}

	public TArray<CollisionObject> getActorsList() {
		TArray<CollisionObject> result = new TArray<>();
		for (Keys<CollisionObject> key = this._actors.keys(); key.hasNext();) {
			result.add(key.next());
		}
		return result;
	}

}
