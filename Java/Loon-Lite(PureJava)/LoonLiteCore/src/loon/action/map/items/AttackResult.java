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
package loon.action.map.items;

public class AttackResult {

	private AttackType _type;

	private AttackBase _attackBase;

	public AttackResult(AttackType atype, AttackBase abase) {
		this._type = atype;
		this._attackBase = abase;
	}

	public AttackResult(AttackType atype, float damage) {
		this._type = atype;
		this._attackBase = new AttackBase(damage);
	}

	public AttackResult setAttackType(AttackType atype) {
		this._type = atype;
		return this;
	}

	public AttackType getAttackType() {
		return _type;
	}

	public AttackResult setAttackBase(AttackBase abase) {
		this._attackBase = abase;
		return this;
	}

	public AttackBase getAttackBase() {
		return _attackBase;
	}

}
