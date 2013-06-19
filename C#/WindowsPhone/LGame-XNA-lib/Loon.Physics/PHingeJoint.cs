/// <summary>
/// Copyright 2013 The Loon Authors
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
using Loon.Core.Geom;
using Loon.Utils;
namespace Loon.Physics {
	
	public class PHingeJoint : PJoint {
	
		private Vector2f anchor1;
		private Vector2f anchor2;
		private float angI;
		private float angM;
		private PBody b1;
		private PBody b2;
		private bool enableLimit;
		private bool enableMotor;
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
	
		public PHingeJoint(PBody b1_0, PBody b2_1, float rel1x, float rel1y,
				float rel2x, float rel2y) {
			this.b1 = b1_0;
			this.b2 = b2_1;
			localAngle = b2_1.ang - b1_0.ang;
			localAnchor1 = new Vector2f(rel1x, rel1y);
			localAnchor2 = new Vector2f(rel2x, rel2y);
			b1_0.mAng.Transpose().MulEqual(localAnchor1);
			b2_1.mAng.Transpose().MulEqual(localAnchor2);
			anchor1 = b1_0.mAng.Mul(localAnchor1);
			anchor1.AddLocal(b1_0.pos);
			anchor2 = b2_1.mAng.Mul(localAnchor2);
			anchor2.AddLocal(b2_1.pos);
			type = Physics.PJointType.HINGE_JOINT;
			mass = new PTransformer();
			impulse = new Vector2f();
		}
	
		public Vector2f GetAnchorPoint1() {
			return anchor1.Clone();
		}
	
		public Vector2f GetAnchorPoint2() {
			return anchor2.Clone();
		}
	
		public PBody GetBody1() {
			return b1;
		}
	
		public PBody GetBody2() {
			return b2;
		}
	
		public float GetLimitRestitution(float restitution) {
			return rest;
		}
	
		public float GetMaxAngle() {
			return maxAngle;
		}
	
		public float GetMinAngle() {
			return minAngle;
		}
	
		public float GetMotorSpeed() {
			return motorSpeed;
		}
	
		public float GetMotorTorque() {
			return motorTorque;
		}
	
		public Vector2f GetRelativeAnchorPoint1() {
			return relAnchor1.Clone();
		}
	
		public Vector2f GetRelativeAnchorPoint2() {
			return relAnchor2.Clone();
		}
	
		public bool IsEnableLimit() {
			return enableLimit;
		}
	
		public bool IsEnableMotor() {
			return enableMotor;
		}
	
		internal override void PreSolve(float dt) {
			relAnchor1 = b1.mAng.Mul(localAnchor1);
			relAnchor2 = b2.mAng.Mul(localAnchor2);
			anchor1.Set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
			anchor2.Set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
			mass = PTransformer.CalcEffectiveMass(b1, b2, relAnchor1, relAnchor2);
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
			b1.ApplyImpulse(impulse.x, impulse.y, anchor1.x, anchor1.y);
			b2.ApplyImpulse(-impulse.x, -impulse.y, anchor2.x, anchor2.y);
			b1.ApplyTorque(motI + limI);
			b2.ApplyTorque(-motI - limI);
		}
	
		public void SetEnableLimit(bool enable) {
			enableLimit = enable;
		}
	
		public void SetEnableMotor(bool enable) {
			enableMotor = enable;
		}
	
		public void SetLimitAngle(float minAngle_0, float maxAngle_1) {
			this.minAngle = minAngle_0;
			this.maxAngle = maxAngle_1;
		}
	
		public void SetLimitRestitution(float restitution) {
			rest = restitution;
		}
	
		public void SetMaxAngle(float maxAngle_0) {
			this.maxAngle = maxAngle_0;
		}
	
		public void SetMinAngle(float minAngle_0) {
			this.minAngle = minAngle_0;
		}
	
		public void SetMotor(float speed, float torque) {
			motorSpeed = speed;
			motorTorque = torque;
		}
	
		public void SetMotorSpeed(float speed) {
			motorSpeed = speed;
		}
	
		public void SetMotorTorque(float torque) {
			motorTorque = torque;
		}
	
		public void SetRelativeAnchorPoint1(float relx, float rely) {
			localAnchor1.Set(relx, rely);
			b1.mAng.Transpose().MulEqual(localAnchor1);
		}
	
		public void SetRelativeAnchorPoint2(float relx, float rely) {
			localAnchor2.Set(relx, rely);
			b2.mAng.Transpose().MulEqual(localAnchor2);
		}
	
		internal override void SolvePosition() {
			if (enableLimit && limitState != 0) {
				float over = b2.ang - b1.ang - localAngle;
				if (over < minAngle) {
					over += 0.008F;
					over = ((over - minAngle) + b2.correctAngVel)
							- b1.correctAngVel;
					float torque = over * 0.2F * angM;
					float subAngleImpulse = angI;
					angI = MathUtils.Min(angI + torque, 0.0F);
					torque = angI - subAngleImpulse;
					b1.PositionCorrection(torque);
					b2.PositionCorrection(-torque);
				}
				if (over > maxAngle) {
					over -= 0.008F;
					over = ((over - maxAngle) + b2.correctAngVel)
							- b1.correctAngVel;
					float torque_0 = over * 0.2F * angM;
					float subAngleImpulse_1 = angI;
					angI = MathUtils.Max(angI + torque_0, 0.0F);
					torque_0 = angI - subAngleImpulse_1;
					b1.PositionCorrection(torque_0);
					b2.PositionCorrection(-torque_0);
				}
			}
			Vector2f force = anchor2.Sub(anchor1);
			force.SubLocal(PTransformer.CalcRelativeCorrectVelocity(b1, b2,
					relAnchor1, relAnchor2));
			float length = force.Length();
			force.Normalize();
			force.MulLocal(System.Math.Max(length * 0.2F - 0.002F,0.0F));
			mass.MulEqual(force);
			b1.PositionCorrection(force.x, force.y, anchor1.x, anchor1.y);
			b2.PositionCorrection(-force.x, -force.y, anchor2.x, anchor2.y);
		}
	
		internal override void SolveVelocity(float dt) {
			Vector2f relVel = PTransformer.CalcRelativeVelocity(b1, b2, relAnchor1,
					relAnchor2);
			Vector2f force = mass.Mul(relVel).Negate();
			impulse.AddLocal(force);
			b1.ApplyImpulse(force.x, force.y, anchor1.x, anchor1.y);
			b2.ApplyImpulse(-force.x, -force.y, anchor2.x, anchor2.y);
			if (enableMotor) {
				float angRelVel = b2.angVel - b1.angVel - motorSpeed;
				float torque = angM * angRelVel;
				float subMotorI = motI;
				motI = MathUtils.Clamp(motI + torque, -motorTorque * dt,
						motorTorque * dt);
				torque = motI - subMotorI;
				b1.ApplyTorque(torque);
				b2.ApplyTorque(-torque);
			}
			if (enableLimit && limitState != 0) {
				float angRelVel_0 = b2.angVel - b1.angVel - targetAngleSpeed;
				float torque_1 = angM * angRelVel_0;
				float subLimitI = limI;
				if (limitState == -1) {
					limI = MathUtils.Min(limI + torque_1, 0.0F);
				} else {
					if (limitState == 1) {
						limI = MathUtils.Max(limI + torque_1, 0.0F);
					}
				}
				torque_1 = limI - subLimitI;
				b1.ApplyTorque(torque_1);
				b2.ApplyTorque(-torque_1);
			}
		}
	
		internal override void Update() {
			relAnchor1 = b1.mAng.Mul(localAnchor1);
			relAnchor2 = b2.mAng.Mul(localAnchor2);
			anchor1.Set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
			anchor2.Set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
			if (b1.rem || b2.rem || b1.fix && b2.fix) {
				rem = true;
			}
		}
	
	}
}
