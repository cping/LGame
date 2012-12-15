package loon.core.graphics.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import loon.core.geom.RectBox;


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
public final class BSPCollisionNode  {

	private HashMap<Actor, ActorNode> actors;

	private BSPCollisionNode parent;

	private RectBox area;

	private float splitAxis;

	private float splitPos;

	private BSPCollisionNode left;

	private BSPCollisionNode right;

	private boolean areaRipple;

	public BSPCollisionNode(RectBox area, int splitAxis, int splitPos) {
		this.area = area;
		this.splitAxis = splitAxis;
		this.splitPos = splitPos;
		this.actors = new HashMap<Actor, ActorNode>();
	}

	public void setChild(int side, BSPCollisionNode child) {
		if (side == 0) {
			this.left = child;
			if (child != null) {
				child.parent = this;
			}
		} else {
			this.right = child;
			if (child != null) {
				child.parent = this;
			}
		}
	}

	public void clear() {
		for (ActorNode node : actors.values()) {
			if (node != null) {
				node.dispose();
				node = null;
			}
		}
		actors.clear();
	}

	public void setArea(RectBox area) {
		this.area = area;
		this.areaRipple = true;
	}

	public void setSplitAxis(float axis) {
		if (axis != this.splitAxis) {
			this.splitAxis = axis;
			this.areaRipple = true;
		}
	}

	public void setSplitPos(float pos) {
		if (pos != this.splitPos) {
			this.splitPos = pos;
			this.areaRipple = true;
		}

	}

	public float getSplitAxis() {
		return this.splitAxis;
	}

	public float getSplitPos() {
		return this.splitPos;
	}

	public RectBox getLeftArea() {
		return this.splitAxis == 0 ? new RectBox(this.area.getX(),
				this.area.getY(), this.splitPos - this.area.getX(),
				this.area.getHeight()) : new RectBox(this.area.getX(),
				this.area.getY(), this.area.getWidth(), this.splitPos
						- this.area.getY());
	}

	public RectBox getRightArea() {
		return this.splitAxis == 0 ? new RectBox(this.splitPos,
				this.area.getY(), this.area.getRight() - this.splitPos,
				this.area.getHeight()) : new RectBox(this.area.getX(),
				this.splitPos, this.area.getWidth(), this.area.getBottom()
						- this.splitPos);
	}

	public RectBox getArea() {
		return this.area;
	}

	private void resizeChildren() {
		if (this.left != null) {
			this.left.setArea(this.getLeftArea());
		}

		if (this.right != null) {
			this.right.setArea(this.getRightArea());
		}

	}

	public BSPCollisionNode getLeft() {
		if (this.areaRipple) {
			this.resizeChildren();
			this.areaRipple = false;
		}
		return this.left;
	}

	public BSPCollisionNode getRight() {
		if (this.areaRipple) {
			this.resizeChildren();
			this.areaRipple = false;
		}
		return this.right;
	}

	public BSPCollisionNode getParent() {
		return this.parent;
	}

	public void setParent(BSPCollisionNode parent) {
		this.parent = parent;
	}

	public int getChildSide(BSPCollisionNode child) {
		return this.left == child ? 0 : 1;
	}

	public void addActor(Actor actor) {
		this.actors.put(actor, new ActorNode(actor, this));
	}

	public boolean containsActor(Actor actor) {
		ActorNode anode = this.actors.get(actor);
		if (anode != null) {
			anode.mark();
			return true;
		} else {
			return false;
		}
	}

	public void actorRemoved(Actor actor) {
		this.actors.remove(actor);
	}

	public int numberActors() {
		return this.actors.size();
	}

	public boolean isEmpty() {
		return this.actors.isEmpty();
	}

	public Iterator getEntriesIterator() {
		return this.actors.entrySet().iterator();
	}

	public Iterator getActorsIterator() {
		return this.actors.keySet().iterator();
	}

	public List getActorsList() {
		return new ArrayList(this.actors.keySet());
	}

}
