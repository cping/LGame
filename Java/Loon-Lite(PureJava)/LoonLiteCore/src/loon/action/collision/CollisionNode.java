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

import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.RectBox;

public class CollisionNode implements ActionBind {

	private CollisionObject _actor;

	private BSPCollisionNode _node;

	private CollisionNode _next;

	private CollisionNode _prev;

	private boolean _mark;

	public CollisionNode(CollisionObject actor, BSPCollisionNode node) {
		this._actor = actor;
		this._node = node;
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

	@Override
	public Field2D getField2D() {
		return _actor.getField2D();
	}

	@Override
	public void setVisible(boolean v) {
		_actor.setVisible(v);
	}

	@Override
	public boolean isVisible() {
		return _actor.isVisible();
	}

	@Override
	public int x() {
		return _actor.x();
	}

	@Override
	public int y() {
		return _actor.y();
	}

	@Override
	public float getX() {
		return _actor.getX();
	}

	@Override
	public float getY() {
		return _actor.getY();
	}

	@Override
	public float getScaleX() {
		return _actor.getScaleX();
	}

	@Override
	public float getScaleY() {
		return _actor.getScaleY();
	}

	@Override
	public void setColor(LColor color) {
		_actor.setColor(color);
	}

	@Override
	public LColor getColor() {
		return _actor.getColor();
	}

	@Override
	public void setScale(float sx, float sy) {
		_actor.setScale(sx, sy);
	}

	@Override
	public float getRotation() {
		return _actor.getRotation();
	}

	@Override
	public void setRotation(float r) {
		_actor.setRotation(r);
	}

	@Override
	public float getWidth() {
		return _actor.getWidth();
	}

	@Override
	public float getHeight() {
		return _actor.getHeight();
	}

	@Override
	public ActionBind setSize(float w, float h) {
		_actor.setSize(w, h);
		return this;
	}

	@Override
	public float getAlpha() {
		return _actor.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		_actor.setAlpha(alpha);
	}

	@Override
	public void setLocation(float x, float y) {
		_actor.setLocation(x, y);
	}

	@Override
	public void setX(float x) {
		_actor.setX(x);
	}

	@Override
	public void setY(float y) {
		_actor.setY(y);
	}

	@Override
	public boolean isBounded() {
		return _actor.isBounded();
	}

	@Override
	public boolean isContainer() {
		return _actor.isContainer();
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return _actor.inContains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return _actor.getRectBox();
	}

	@Override
	public float getContainerWidth() {
		return _actor.getContainerWidth();
	}

	@Override
	public float getContainerHeight() {
		return _actor.getContainerHeight();
	}

	@Override
	public ActionTween selfAction() {
		return _actor.selfAction();
	}

	@Override
	public boolean isActionCompleted() {
		return _actor.isActionCompleted();
	}

	@Override
	public int getLayer() {
		return _actor.getLayer();
	}
}
