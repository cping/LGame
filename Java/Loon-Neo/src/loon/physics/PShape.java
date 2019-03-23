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

import loon.canvas.LColor;
import loon.geom.AABB;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public abstract class PShape {

	protected AABB _aabb;
	
	protected float _ang;
	
	protected float _dens;
	
	protected float _fric;
	
	protected float _localAng;
	
	protected float ii, mm;
	
	protected Vector2f _localPos;
	
	protected PTransformer _mAng;
	
	protected PBody _parent;
	
	protected Vector2f _pos;
	
	protected boolean _rem;
	
	protected float _rest;
	
	protected PSortableAABB _sapAABB;
	
	protected PShapeType _type;
	
	protected LColor _color;
	
	protected LColor _strokeColor;
	
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
			setColor((int) (MathUtils.random() * 160F + 96F), (int) (MathUtils.random() * 160F + 96F),
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
		return _localPos.cpy();
	}

	public Vector2f getPosition() {
		return _pos.cpy();
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
			_strokeColor = new LColor((int) ((float) r * 0.375F), (int) ((float) g * 0.375F),
					(int) ((float) b * 0.375F));
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
