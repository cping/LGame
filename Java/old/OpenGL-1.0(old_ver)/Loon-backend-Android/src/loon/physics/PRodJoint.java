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

public class PRodJoint extends PJoint {

	private Vector2f anchor1;
	private Vector2f anchor2;
	private PBody b1;
	private PBody b2;
	private float dist;
	private float length;
	private Vector2f localAnchor1;
	private Vector2f localAnchor2;
	private float mass;
	private float norI;
	private Vector2f normal;
	private Vector2f relAnchor1;
	private Vector2f relAnchor2;

	public PRodJoint(PBody b1, PBody b2, float rel1x, float rel1y, float rel2x,
			float rel2y, float distance) {
		this.b1 = b1;
		this.b2 = b2;
		localAnchor1 = new Vector2f(rel1x, rel1y);
		localAnchor2 = new Vector2f(rel2x, rel2y);
		b1.mAng.transpose().mulEqual(localAnchor1);
		b2.mAng.transpose().mulEqual(localAnchor2);
		dist = distance;
		anchor1 = b1.mAng.mul(localAnchor1).add(b1.pos);
		anchor2 = b2.mAng.mul(localAnchor2).add(b2.pos);
		normal = anchor1.sub(anchor2);
		normal.normalize();
		type = PJointType.ROD_JOINT;
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

	public float getDistance() {
		return dist;
	}

	public Vector2f getRelativeAnchorPoint1() {
		return relAnchor1.clone();
	}

	public Vector2f getRelativeAnchorPoint2() {
		return relAnchor2.clone();
	}

	private float max(float v, float max) {
		return v <= max ? max : v;
	}

	private float min(float v, float min) {
		return v >= min ? min : v;
	}

	@Override
	void preSolve(float dt) {
		relAnchor1 = b1.mAng.mul(localAnchor1);
		relAnchor2 = b2.mAng.mul(localAnchor2);
		anchor1.set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
		anchor2.set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
		normal = anchor2.sub(anchor1);
		length = normal.length();
		normal.normalize();
		mass = PTransformer.calcEffectiveMass(b1, b2, relAnchor1, relAnchor2,
				normal);
		b1.applyImpulse(normal.x * norI, normal.y * norI, anchor1.x, anchor1.y);
		b2.applyImpulse(normal.x * -norI, normal.y * -norI, anchor2.x,
				anchor2.y);
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

	@Override
	void solvePosition() {
		float rvn = normal.dot(PTransformer.calcRelativeCorrectVelocity(b1, b2,
				relAnchor1, relAnchor2));
		float impulse = -mass * ((dist - length) + rvn) * 0.2F;
		if (impulse > 0.0F)
			impulse = max(impulse - 0.002F, 0.0F);
		else if (impulse < 0.0F)
			impulse = min(impulse + 0.002F, 0.0F);
		float forceX = normal.x * impulse;
		float forceY = normal.y * impulse;
		b1.positionCorrection(forceX, forceY, anchor1.x, anchor1.y);
		b2.positionCorrection(-forceX, -forceY, anchor2.x, anchor2.y);
	}

	@Override
	void solveVelocity(float dt) {
		float rn = normal.dot(PTransformer.calcRelativeVelocity(b1, b2,
				relAnchor1, relAnchor2));
		float impulse = -mass * rn;
		norI += impulse;
		float forceX = normal.x * impulse;
		float forceY = normal.y * impulse;
		b1.applyImpulse(forceX, forceY, anchor1.x, anchor1.y);
		b2.applyImpulse(-forceX, -forceY, anchor2.x, anchor2.y);
	}

	@Override
	void update() {
		relAnchor1 = b1.mAng.mul(localAnchor1);
		relAnchor2 = b2.mAng.mul(localAnchor2);
		anchor1.set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
		anchor2.set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
		if (b1.rem || b2.rem || b1.fix && b2.fix) {
			rem = true;
		}
	}

}
