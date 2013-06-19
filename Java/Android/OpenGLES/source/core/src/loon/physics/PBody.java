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
import loon.utils.MathUtils;

public class PBody {

	AABB aabb;
	float ang;
	float angVel;
	float correctAngVel;
	Vector2f correctVel;
	boolean fix;
	float i;
	float invI;
	float invM;
	float m;
	PTransformer mAng;
	int numShapes;
	Vector2f pos;
	boolean rem;
	PShape[] shapes;
	Vector2f vel;
	PPhysWorld w;
	Object tag;

	public PBody(float angle, boolean fixate, PShape[] ss) {
		pos = new Vector2f();
		vel = new Vector2f();
		correctVel = new Vector2f();
		mAng = new PTransformer();
		aabb = new AABB();
		ang = angle;
		numShapes = ss.length;
		shapes = new PShape[1024];
		for (int i = 0; i < numShapes; i++) {
			shapes[i] = ss[i];
			shapes[i]._parent = this;
			if (shapes[i]._type == PShapeType.CONCAVE_SHAPE) {
				PConcavePolygonShape cp = (PConcavePolygonShape) shapes[i];
				for (int j = 0; j < cp.numConvexes; j++){
					cp.convexes[j]._parent = this;
				}

			}
		}
		fix = fixate;
		calcMassData();
	}

	public void addShape(PShape s) {
		if (w != null) {
			w.addShape(s);
		}
		shapes[numShapes] = s;
		s._localPos.subLocal(pos);
		s._parent = this;
		if (s._type == PShapeType.CONCAVE_SHAPE) {
			PConcavePolygonShape cp = (PConcavePolygonShape) s;
			for (int i = 0; i < cp.numConvexes; i++) {
				cp.convexes[i]._parent = this;
			}
		}
		numShapes++;
		calcMassData();
	}

	public void applyForce(float fx, float fy) {
		if (fix) {
			return;
		} else {
			vel.x += fx * invM;
			vel.y += fy * invM;
			return;
		}
	}

	public void applyImpulse(float fx, float fy, float px, float py) {
		if (fix) {
			return;
		} else {
			vel.x += fx * invM;
			vel.y += fy * invM;
			px -= pos.x;
			py -= pos.y;
			angVel += (px * fy - py * fx) * invI;
			return;
		}
	}

	public void applyTorque(float torque) {
		if (fix) {
			return;
		} else {
			angVel += torque * invI;
			return;
		}
	}

	void calcMassData() {
		correctCenterOfGravity();
		if (!fix) {
			m = i = 0.0F;
			for (int j = 0; j < numShapes; j++) {
				m += shapes[j].mm * shapes[j]._dens;
				i += shapes[j].ii * shapes[j]._dens;
				i += (shapes[j]._localPos.x * shapes[j]._localPos.x + shapes[j]._localPos.y
						* shapes[j]._localPos.y)
						* shapes[j].mm * shapes[j]._dens;
			}

			invM = 1.0F / m;
			invI = 1.0F / i;
		} else {
			m = invM = 0.0F;
			i = invI = 0.0F;
		}
	}

	void correctCenterOfGravity() {
		float cy;
		float cx = cy = 0.0F;
		float total = 0.0F;
		for (int j = 0; j < numShapes; j++) {
			total += shapes[j].mm * shapes[j]._dens;
			cx += shapes[j]._localPos.x * shapes[j].mm * shapes[j]._dens;
			cy += shapes[j]._localPos.y * shapes[j].mm * shapes[j]._dens;
		}

		if (numShapes > 0) {
			total = 1.0F / total;
			cx *= total;
			cy *= total;
		}
		pos.x += cx;
		pos.y += cy;
		for (int j = 0; j < numShapes; j++) {
			shapes[j]._localPos.x -= cx;
			shapes[j]._localPos.y -= cy;
		}

	}

	public float getAngularVelocity() {
		return angVel;
	}

	public Vector2f getPosition() {
		return pos;
	}

	public PShape[] getShapes() {
		PShape result[] = new PShape[numShapes];
		System.arraycopy(shapes, 0, result, 0, numShapes);
		return result;
	}
	
	public int size(){
		return numShapes;
	}

	public PShape[] inner_shapes() {
		return shapes;
	}
	
	public Vector2f getVelocity() {
		return vel;
	}

	public boolean isFixate() {
		return fix;
	}

	private float max(float v1, float v2) {
		return v1 <= v2 ? v2 : v1;
	}

	private float min(float v1, float v2) {
		return v1 >= v2 ? v2 : v1;
	}

	void positionCorrection(float torque) {
		if (fix) {
			return;
		} else {
			correctAngVel += torque * invI;
			return;
		}
	}

	void positionCorrection(float fx, float fy, float px, float py) {
		if (fix) {
			return;
		} else {
			correctVel.x += fx * invM;
			correctVel.y += fy * invM;
			px -= pos.x;
			py -= pos.y;
			correctAngVel += (px * fy - py * fx) * invI;
			return;
		}
	}

	public void remove() {
		this.rem = true;
	}

	public void removeShape(PShape s) {
		for (int i = 0; i < numShapes; i++) {
			if (shapes[i] != s) {
				continue;
			}
			s._rem = true;
			s._parent = null;
			s._localPos.addLocal(pos);
			if (i != numShapes - 1) {
				System.arraycopy(shapes, i + 1, shapes, i, numShapes - i - 1);
			}
			break;
		}
		numShapes--;
		calcMassData();
	}

	public void setAngularVelocity(float v) {
		angVel = v;
	}

	public void setFixate(boolean fixate) {
		if (fix == fixate) {
			return;
		} else {
			fix = fixate;
			calcMassData();
			return;
		}
	}

	public void setVelocity(float vx, float vy) {
		vel.set(vx, vy);
	}

	void update() {
		float twoPI = MathUtils.TWO_PI;
		ang = (ang + twoPI) % twoPI;
		mAng.setRotate(ang);
		for (int i = 0; i < numShapes; i++) {
			PShape s = shapes[i];
			s._pos.set(s._localPos.x, s._localPos.y);
			mAng.mulEqual(s._pos);
			s._pos.addLocal(pos);
			s._localAng = (s._localAng + twoPI) % twoPI;
			s._ang = ang + s._localAng;
			s._mAng.setRotate(s._ang);
			s.update();
			s.calcAABB();
			s._sapAABB.update();
			if (i == 0) {
				aabb.set(s._aabb.minX, s._aabb.minY, s._aabb.maxX, s._aabb.maxY);
			} else {
				aabb.set(min(aabb.minX, s._aabb.minX),
						min(aabb.minY, s._aabb.minY),
						max(aabb.maxX, s._aabb.maxX),
						max(aabb.maxY, s._aabb.maxY));
			}
		}

	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

}
