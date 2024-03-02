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
package loon.action.map.battle;

import loon.action.map.items.Role;
import loon.geom.Vector2f;

public class BattleAction {

	private BattleActionType _actionType;

	private Role _actionSource = null;

	private Role _actionTarget = null;

	private float _actionCost = 1f;

	private Object _tag = null;

	private Vector2f _actionMoveOffset = Vector2f.ZERO();

	private Vector2f _originalPos = Vector2f.ZERO();

	public BattleAction(Role src, Role dst) {
		this(BattleActionType.None, src, dst);
	}

	public BattleAction(BattleActionType type, Role src, Role dst) {
		this(type, src, dst, 1f);
	}

	public BattleAction(Role src, Role dst, float cost) {
		this(BattleActionType.None, src, dst, cost);
	}

	public BattleAction(BattleActionType type, Role src, Role dst, float cost) {
		this._actionType = type;
		this._actionSource = src;
		this._actionTarget = dst;
		this._actionCost = cost;
		this._originalPos = Vector2f.at(src.getX(), src.getY());
	}

	public boolean hasAttackTarget() {
		return (_actionTarget != null && !_actionTarget.equals(_actionSource));
	}

	public Role getActionSource() {
		return _actionSource;
	}

	public BattleAction setActionSource(Role a) {
		this._actionSource = a;
		return this;
	}

	public Role getActionTarget() {
		return _actionTarget;
	}

	public BattleAction setActionTarget(Role a) {
		this._actionTarget = a;
		return this;
	}

	public float getActionCost() {
		return _actionCost;
	}

	public void setActionCost(float a) {
		this._actionCost = a;
	}

	public Vector2f getOriginalPos() {
		return _originalPos;
	}

	public Vector2f getTargetPosition() {
		if (!hasAttackTarget()) {
			return _originalPos;
		}
		return Vector2f.at(_actionTarget.getX() + _actionMoveOffset.x, _actionTarget.getY() + _actionMoveOffset.y);
	}

	public void setOriginalPos(Vector2f o) {
		this._originalPos = o;
	}

	public Vector2f getActionMoveOffset() {
		return _actionMoveOffset;
	}

	public BattleAction setActionMoveOffset(Vector2f a) {
		this._actionMoveOffset = a;
		return this;
	}

	public Vector2f getSourceOriginalPosition() {
		return _originalPos;
	}

	public BattleActionType getActionType() {
		return _actionType;
	}

	public BattleAction setActionType(BattleActionType t) {
		this._actionType = t;
		return this;
	}

	public Object getTag() {
		return _tag;
	}

	public BattleAction setTag(Object t) {
		this._tag = t;
		return this;
	}
}
