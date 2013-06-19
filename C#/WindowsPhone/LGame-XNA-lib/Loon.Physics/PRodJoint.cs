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
namespace Loon.Physics {

	public class PRodJoint : PJoint {
	
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
	
		public PRodJoint(PBody b1_0, PBody b2_1, float rel1x, float rel1y, float rel2x,
				float rel2y, float distance) {
			this.b1 = b1_0;
			this.b2 = b2_1;
			localAnchor1 = new Vector2f(rel1x, rel1y);
			localAnchor2 = new Vector2f(rel2x, rel2y);
			b1_0.mAng.Transpose().MulEqual(localAnchor1);
			b2_1.mAng.Transpose().MulEqual(localAnchor2);
			dist = distance;
			anchor1 = b1_0.mAng.Mul(localAnchor1).Add(b1_0.pos);
			anchor2 = b2_1.mAng.Mul(localAnchor2).Add(b2_1.pos);
			normal = anchor1.Sub(anchor2);
			normal.Normalize();
			type = Physics.PJointType.ROD_JOINT;
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
	
		public float GetDistance() {
			return dist;
		}
	
		public Vector2f getRelativeAnchorPoint1() {
			return relAnchor1.Clone();
		}
	
		public Vector2f getRelativeAnchorPoint2() {
			return relAnchor2.Clone();
		}
	
		private float Max(float v, float max) {
			return (v <= max) ? max : v;
		}
	
		private float Min(float v, float min) {
			return (v >= min) ? min : v;
		}
	
		internal override void PreSolve(float dt) {
			relAnchor1 = b1.mAng.Mul(localAnchor1);
			relAnchor2 = b2.mAng.Mul(localAnchor2);
			anchor1.Set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
			anchor2.Set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
			normal = anchor2.Sub(anchor1);
			length = normal.Length();
			normal.Normalize();
			mass = PTransformer.CalcEffectiveMass(b1, b2, relAnchor1, relAnchor2,
					normal);
			b1.ApplyImpulse(normal.x * norI, normal.y * norI, anchor1.x, anchor1.y);
			b2.ApplyImpulse(normal.x * -norI, normal.y * -norI, anchor2.x,
					anchor2.y);
		}
	
		public void SetDistance(float distance) {
			dist = distance;
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
			float rvn = normal.Dot(PTransformer.CalcRelativeCorrectVelocity(b1, b2,
					relAnchor1, relAnchor2));
			float impulse = -mass * ((dist - length) + rvn) * 0.2F;
			if (impulse > 0.0F)
				impulse = Max(impulse - 0.002F, 0.0F);
			else if (impulse < 0.0F)
				impulse = Min(impulse + 0.002F, 0.0F);
			float forceX = normal.x * impulse;
			float forceY = normal.y * impulse;
			b1.PositionCorrection(forceX, forceY, anchor1.x, anchor1.y);
			b2.PositionCorrection(-forceX, -forceY, anchor2.x, anchor2.y);
		}
	
		internal override void SolveVelocity(float dt) {
			float rn = normal.Dot(PTransformer.CalcRelativeVelocity(b1, b2,
					relAnchor1, relAnchor2));
			float impulse = -mass * rn;
			norI += impulse;
			float forceX = normal.x * impulse;
			float forceY = normal.y * impulse;
			b1.ApplyImpulse(forceX, forceY, anchor1.x, anchor1.y);
			b2.ApplyImpulse(-forceX, -forceY, anchor2.x, anchor2.y);
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
