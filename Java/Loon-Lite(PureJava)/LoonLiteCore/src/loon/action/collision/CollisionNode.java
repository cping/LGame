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
import loon.utils.MathUtils;

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
		return null;
	}

	@Override
	public void setVisible(boolean v) {

	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public int x() {
		return MathUtils.ifloor(_actor.getX());
	}

	@Override
	public int y() {
		return MathUtils.ifloor(_actor.getY());
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
		return 1f;
	}

	@Override
	public float getScaleY() {
		return 1f;
	}

	@Override
	public void setColor(LColor color) {

	}

	@Override
	public LColor getColor() {
		return null;
	}

	@Override
	public void setScale(float sx, float sy) {
	}

	@Override
	public float getRotation() {
		return 0f;
	}

	@Override
	public void setRotation(float r) {
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
		return this;
	}

	@Override
	public float getAlpha() {
		return 0;
	}

	@Override
	public void setAlpha(float alpha) {
	}

	@Override
	public void setLocation(float x, float y) {
	}

	@Override
	public void setX(float x) {

	}

	@Override
	public void setY(float y) {

	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return _actor.getRectBox().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return _actor.getRectBox();
	}

	@Override
	public float getContainerWidth() {
		return 0;
	}

	@Override
	public float getContainerHeight() {
		return 0;
	}

	@Override
	public ActionTween selfAction() {
		return null;
	}

	@Override
	public boolean isActionCompleted() {
		return false;
	}
}
