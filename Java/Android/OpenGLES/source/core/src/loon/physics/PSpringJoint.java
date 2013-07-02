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

import loon.core.geom.Vector2f;

public class PSpringJoint extends PJoint {

	private Vector2f anchor1;
	private Vector2f anchor2;
	private PBody b1;
	private PBody b2;
	private float damp;
	private float dist;
	private float force;
	private Vector2f localAnchor1;
	private Vector2f localAnchor2;
	private float mass;
	private Vector2f normal;
	private Vector2f relAnchor1;
	private Vector2f relAnchor2;
	private float str;

	public PSpringJoint(PBody b1, PBody b2, float rel1x, float rel1y,
			float rel2x, float rel2y, float distance, float strength,
			float damping) {
		this.b1 = b1;
		this.b2 = b2;
		str = strength;
		damp = damping;
		localAnchor1 = new Vector2f(rel1x, rel1y);
		localAnchor2 = new Vector2f(rel2x, rel2y);
		b1.mAng.transpose().mulEqual(localAnchor1);
		b2.mAng.transpose().mulEqual(localAnchor2);
		dist = distance;
		anchor1 = b1.mAng.mul(localAnchor1).add(b1.pos);
		anchor2 = b2.mAng.mul(localAnchor2).add(b2.pos);
		normal = anchor1.sub(anchor2);
		normal.normalize();
		type = PJointType.SPRING_JOINT;
	}

	public Vector2f getAnchorPoint1() {
		return anchor1.clone();
	}

	public Vector2f getAnchorPoint2() {
		return anchor2.clone();
	}

	public PBody getBody1() {
		return b1;
	}

	public PBody getBody2() {
		return b2;
	}

	public float getDamping() {
		return damp;
	}

	public float getDistance() {
		return dist;
	}

	public Vector2f getRelativeAnchorPoint1() {
		return relAnchor1.clone();
	}

	public Vector2f getRelativeAnchorPoint2() {
		return relAnchor2.clone();
	}

	public float getStrength() {
		return str;
	}

	@Override
	void preSolve(float dt) {
		relAnchor1 = b1.mAng.mul(localAnchor1);
		relAnchor2 = b2.mAng.mul(localAnchor2);
		anchor1.set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
		anchor2.set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
		normal = anchor2.sub(anchor1);
		float over = dist - normal.length();
		normal.normalize();
		mass = PTransformer.calcEffectiveMass(b1, b2, relAnchor1, relAnchor2,
				normal);
		float k = mass * 1000F * str;
		force = -over * k;
		force += PTransformer.calcRelativeVelocity(b1, b2, relAnchor1,
				relAnchor2).dot(normal)
				* damp * -(float) Math.sqrt(k * mass) * 2.0F;
		force *= dt;
		b1.applyImpulse(normal.x * force, normal.y * force, anchor1.x,
				anchor1.y);
		b2.applyImpulse(normal.x * -force, normal.y * -force, anchor2.x,
				anchor2.y);
	}

	public void setDamping(float damping) {
		damp = damping;
	}

	public void setDistance(float distance) {
		dist = distance;
	}

	public void setRelativeAnchorPoint1(float relx, float rely) {
		localAnchor1.set(relx, rely);
		b1.mAng.transpose().mulEqual(localAnchor1);
	}

	public void setRelativeAnchorPoint2(float relx, float rely) {
		localAnchor2.set(relx, rely);
		b2.mAng.transpose().mulEqual(localAnchor2);
	}

	public void setStrength(float strength) {
		str = strength;
	}

	@Override
	void solvePosition() {
	}

	@Override
	void solveVelocity(float f) {
	}

	@Override
	void update() {
		relAnchor1 = b1.mAng.mul(localAnchor1);
		relAnchor2 = b2.mAng.mul(localAnchor2);
		anchor1.set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
		anchor2.set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
		if (b1.rem || b2.rem || b1.fix && b2.fix){
			rem = true;
		}
	}

}
