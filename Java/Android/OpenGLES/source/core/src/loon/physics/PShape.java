/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.physics;

import loon.core.geom.AABB;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.utils.MathUtils;

public abstract class PShape {

	AABB _aabb;
	float _ang;
	float _dens;
	float _fric;
	float _localAng;
	float ii;
	float mm;
	Vector2f _localPos;
	PTransformer _mAng;
	PBody _parent;
	Vector2f _pos;
	boolean _rem;
	float _rest;
	PSortableAABB _sapAABB;
	PShapeType _type;
	LColor _color;
	LColor _strokeColor;
	private boolean _rnd;

	public PShape() {
		this(true);
	}

	public PShape(boolean randColor) {
		_fric = 0.5F;
		_rest = 0.5F;
		_localPos = new Vector2f();
		_pos = new Vector2f();
		_mAng = new PTransformer();
		_aabb = new AABB();
		_sapAABB = new PSortableAABB();
		_type = PShapeType.NULL_SHAPE;
		_rnd = randColor;
		if (randColor) {
			setColor((int) (MathUtils.random() * 160F + 96F),
					(int) (MathUtils.random() * 160F + 96F),
					(int) (MathUtils.random() * 160F + 96F));
		}
	}

	abstract void calcAABB();

	public AABB getAABB() {
		return _aabb;
	}

	public float getAngle() {
		return _ang;
	}

	public float getDensity() {
		return _dens;
	}

	public float getFriction() {
		return _fric;
	}

	public float getLocalAngle() {
		return _localAng;
	}

	public Vector2f getLocalPosition() {
		return _localPos.clone();
	}

	public Vector2f getPosition() {
		return _pos.clone();
	}

	public float getRestitution() {
		return _rest;
	}

	public PShapeType getShapeType() {
		return _type;
	}

	public void setAngle(float angle) {
		_localAng = angle;
	}

	public void setDensity(float density) {
		_dens = density;
		if (_parent != null)
			_parent.calcMassData();
	}

	public void setFriction(float friction) {
		_fric = friction;
	}

	public void setPosition(float px, float py) {
		_localPos.set(px, py);
		if (_parent != null) {
			_localPos.subLocal(_parent.pos);
			_parent.correctCenterOfGravity();
			_parent.calcMassData();
		}
	}

	public void setRestitution(float restitution) {
		_rest = restitution;
	}

	abstract void update();

	public void setColor(int r, int g, int b) {
		if (_rnd) {
			_color = new LColor(r, g, b);
			_strokeColor = new LColor((int) (r * 0.375F),
					(int) (g * 0.375F), (int) (b * 0.375F));
		}
	}

	public LColor getColor() {
		if (_rnd) {
			return _color;
		}
		return LColor.black;
	}

	public LColor getStrokeColor() {
		return _strokeColor;
	}

}
