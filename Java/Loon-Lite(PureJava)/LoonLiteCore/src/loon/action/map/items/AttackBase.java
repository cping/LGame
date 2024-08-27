/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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

import loon.LSystem;
import loon.geom.PointI;
import loon.utils.TArray;

public class AttackBase {

	private String _name;

	private AttackScale _attack;

	private float _damage;

	private float _hitRate;

	private boolean _locked;

	protected TArray<PointI> _range;

	public AttackBase() {
		this(0f);
	}

	public AttackBase(float damage) {
		this(LSystem.UNKNOWN, AttackScale.Single, damage);
	}

	public AttackBase(AttackScale s, float damage) {
		this(LSystem.UNKNOWN, s, damage);
	}

	public AttackBase(String n, AttackScale s, float damage) {
		this._range = new TArray<PointI>();
		this._name = n;
		this._attack = s;
	}

	public AttackBase setLock(boolean l) {
		this._locked = l;
		return this;
	}

	public TArray<PointI> getRange() {
		return _range;
	}

	public String getName() {
		return _name;
	}

	public AttackBase setName(String name) {
		this._name = name;
		return this;
	}

	public AttackScale getAttack() {
		return _attack;
	}

	public AttackBase setAttack(AttackScale a) {
		this._attack = a;
		return this;
	}

	public float getDamage() {
		return _damage;
	}

	public AttackBase setDamage(float d) {
		this._damage = d;
		return this;
	}

	public float getHitRate() {
		return _hitRate;
	}

	public AttackBase setHitRate(float r) {
		this._hitRate = r;
		return this;
	}

	public boolean isLocked() {
		return _locked;
	}

}
