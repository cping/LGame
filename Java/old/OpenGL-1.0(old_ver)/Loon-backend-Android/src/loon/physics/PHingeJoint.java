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
import loon.utils.MathUtils;

public class PHingeJoint extends PJoint {

	private Vector2f anchor1;
	private Vector2f anchor2;
	private float angI;
	private float angM;
	private PBody b1;
	private PBody b2;
	private boolean enableLimit;
	private boolean enableMotor;
	private Vector2f impulse;
	private float limI;
	private int limitState;
	private Vector2f localAnchor1;
	private Vector2f localAnchor2;
	private float localAngle;
	private PTransformer mass;
	private float maxAngle;
	private float minAngle;
	private float motI;
	private float motorSpeed;
	private float motorTorque;
	private Vector2f relAnchor1;
	private Vector2f relAnchor2;
	private float rest;
	private float targetAngleSpeed;

	public PHingeJoint(PBody b1, PBody b2, float rel1x, float rel1y,
			float rel2x, float rel2y) {
		this.b1 = b1;
		this.b2 = b2;
		localAngle = b2.ang - b1.ang;
		localAnchor1 = new Vector2f(rel1x, rel1y);
		localAnchor2 = new Vector2f(rel2x, rel2y);
		b1.mAng.transpose().mulEqual(localAnchor1);
		b2.mAng.transpose().mulEqual(localAnchor2);
		anchor1 = b1.mAng.mul(localAnchor1);
		anchor1.addLocal(b1.pos);
		anchor2 = b2.mAng.mul(localAnchor2);
		anchor2.addLocal(b2.pos);
		type = PJointType.HINGE_JOINT;
		mass = new PTransformer();
		impulse = new Vector2f();
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

	public float getLimitRestitution(float restitution) {
		return rest;
	}

	public float getMaxAngle() {
		return maxAngle;
	}

	public float getMinAngle() {
		return minAngle;
	}

	public float getMotorSpeed() {
		return motorSpeed;
	}

	public float getMotorTorque() {
		return motorTorque;
	}

	public Vector2f getRelativeAnchorPoint1() {
		return relAnchor1.clone();
	}

	public Vector2f getRelativeAnchorPoint2() {
		return relAnchor2.clone();
	}

	public boolean isEnableLimit() {
		return enableLimit;
	}

	public boolean isEnableMotor() {
		return enableMotor;
	}

	@Override
	void preSolve(float dt) {
		relAnchor1 = b1.mAng.mul(localAnchor1);
		relAnchor2 = b2.mAng.mul(localAnchor2);
		anchor1.set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
		anchor2.set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
		mass = PTransformer.calcEffectiveMass(b1, b2, relAnchor1, relAnchor2);
		angM = 1.0F / (b1.invI + b2.invI);
		float ang = b2.ang - b1.ang - localAngle;
		if (!enableMotor) {
			motI = 0.0F;
		}
		if (enableLimit) {
			if (ang < minAngle) {
				if (limitState != -1) {
					limI = 0.0F;
				}
				limitState = -1;
				if (b2.angVel - b1.angVel < 0.0F) {
					targetAngleSpeed = (b2.angVel - b1.angVel) * -rest;
				} else {
					targetAngleSpeed = 0.0F;
				}
			} else if (ang > maxAngle) {
				if (limitState != 1) {
					limI = 0.0F;
				}
				limitState = 1;
				if (b2.angVel - b1.angVel > 0.0F) {
					targetAngleSpeed = (b2.angVel - b1.angVel) * -rest;
				} else {
					targetAngleSpeed = 0.0F;
				}
			} else {
				limI = limitState = 0;
			}
		} else {
			limI = limitState = 0;
		}
		angI = 0.0F;
		b1.applyImpulse(impulse.x, impulse.y, anchor1.x, anchor1.y);
		b2.applyImpulse(-impulse.x, -impulse.y, anchor2.x, anchor2.y);
		b1.applyTorque(motI + limI);
		b2.applyTorque(-motI - limI);
	}

	public void setEnableLimit(boolean enable) {
		enableLimit = enable;
	}

	public void setEnableMotor(boolean enable) {
		enableMotor = enable;
	}

	public void setLimitAngle(float minAngle, float maxAngle) {
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}

	public void setLimitRestitution(float restitution) {
		rest = restitution;
	}

	public void setMaxAngle(float maxAngle) {
		this.maxAngle = maxAngle;
	}

	public void setMinAngle(float minAngle) {
		this.minAngle = minAngle;
	}

	public void setMotor(float speed, float torque) {
		motorSpeed = speed;
		motorTorque = torque;
	}

	public void setMotorSpeed(float speed) {
		motorSpeed = speed;
	}

	public void setMotorTorque(float torque) {
		motorTorque = torque;
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
		if (enableLimit && limitState != 0) {
			float over = b2.ang - b1.ang - localAngle;
			if (over < minAngle) {
				over += 0.008F;
				over = ((over - minAngle) + b2.correctAngVel)
						- b1.correctAngVel;
				float torque = over * 0.2F * angM;
				float subAngleImpulse = angI;
				angI = MathUtils.min(angI + torque, 0.0F);
				torque = angI - subAngleImpulse;
				b1.positionCorrection(torque);
				b2.positionCorrection(-torque);
			}
			if (over > maxAngle) {
				over -= 0.008F;
				over = ((over - maxAngle) + b2.correctAngVel)
						- b1.correctAngVel;
				float torque = over * 0.2F * angM;
				float subAngleImpulse = angI;
				angI = MathUtils.max(angI + torque, 0.0F);
				torque = angI - subAngleImpulse;
				b1.positionCorrection(torque);
				b2.positionCorrection(-torque);
			}
		}
		Vector2f force = anchor2.sub(anchor1);
		force.subLocal(PTransformer.calcRelativeCorrectVelocity(b1, b2,
				relAnchor1, relAnchor2));
		float length = force.length();
		force.normalize();
		force.mulLocal(Math.max(length * 0.2F - 0.002F, 0.0F));
		mass.mulEqual(force);
		b1.positionCorrection(force.x, force.y, anchor1.x, anchor1.y);
		b2.positionCorrection(-force.x, -force.y, anchor2.x, anchor2.y);
	}

	@Override
	void solveVelocity(float dt) {
		Vector2f relVel = PTransformer.calcRelativeVelocity(b1, b2, relAnchor1,
				relAnchor2);
		Vector2f force = mass.mul(relVel).negate();
		impulse.addLocal(force);
		b1.applyImpulse(force.x, force.y, anchor1.x, anchor1.y);
		b2.applyImpulse(-force.x, -force.y, anchor2.x, anchor2.y);
		if (enableMotor) {
			float angRelVel = b2.angVel - b1.angVel - motorSpeed;
			float torque = angM * angRelVel;
			float subMotorI = motI;
			motI = MathUtils.clamp(motI + torque, -motorTorque * dt,
					motorTorque * dt);
			torque = motI - subMotorI;
			b1.applyTorque(torque);
			b2.applyTorque(-torque);
		}
		if (enableLimit && limitState != 0) {
			float angRelVel = b2.angVel - b1.angVel - targetAngleSpeed;
			float torque = angM * angRelVel;
			float subLimitI = limI;
			if (limitState == -1) {
				limI = MathUtils.min(limI + torque, 0.0F);
			} else {
				if (limitState == 1) {
					limI = MathUtils.max(limI + torque, 0.0F);
				}
			}
			torque = limI - subLimitI;
			b1.applyTorque(torque);
			b2.applyTorque(-torque);
		}
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
