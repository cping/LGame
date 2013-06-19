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
	
	public class PSpringJoint : PJoint {
	
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
	
		public PSpringJoint(PBody b1_0, PBody i_2, float rel1x, float rel1y,
				float rel2x, float rel2y, float distance, float strength,
				float damping) {
			this.b1 = b1_0;
			this.b2 = i_2;
			str = strength;
			damp = damping;
			localAnchor1 = new Vector2f(rel1x, rel1y);
			localAnchor2 = new Vector2f(rel2x, rel2y);
			b1_0.mAng.Transpose().MulEqual(localAnchor1);
			i_2.mAng.Transpose().MulEqual(localAnchor2);
			dist = distance;
			anchor1 = b1_0.mAng.Mul(localAnchor1).Add(b1_0.pos);
			anchor2 = i_2.mAng.Mul(localAnchor2).Add(i_2.pos);
			normal = anchor1.Sub(anchor2);
			normal.Normalize();
			type = Physics.PJointType.SPRING_JOINT;
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
	
		public float GetDamping() {
			return damp;
		}
	
		public float GetDistance() {
			return dist;
		}
	
		public Vector2f GetRelativeAnchorPoint1() {
			return relAnchor1.Clone();
		}
	
		public Vector2f GetRelativeAnchorPoint2() {
			return relAnchor2.Clone();
		}
	
		public float GetStrength() {
			return str;
		}
	
		internal override void PreSolve(float i_0) {
			relAnchor1 = b1.mAng.Mul(localAnchor1);
			relAnchor2 = b2.mAng.Mul(localAnchor2);
			anchor1.Set(relAnchor1.x + b1.pos.x, relAnchor1.y + b1.pos.y);
			anchor2.Set(relAnchor2.x + b2.pos.x, relAnchor2.y + b2.pos.y);
			normal = anchor2.Sub(anchor1);
			float over = dist - normal.Length();
			normal.Normalize();
			mass = PTransformer.CalcEffectiveMass(b1, b2, relAnchor1, relAnchor2,
					normal);
			float k = mass * 1000F * str;
			force = -over * k;
			force += PTransformer.CalcRelativeVelocity(b1, b2, relAnchor1,
					relAnchor2).Dot(normal)
					* damp * -(float) System.Math.Sqrt(k * mass) * 2.0F;
			force *= i_0;
			b1.ApplyImpulse(normal.x * force, normal.y * force, anchor1.x,
					anchor1.y);
			b2.ApplyImpulse(normal.x * -force, normal.y * -force, anchor2.x,
					anchor2.y);
		}
	
		public void SetDamping(float damping) {
			damp = damping;
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
	
		public void SetStrength(float strength) {
			str = strength;
		}
	
		internal override void SolvePosition() {
		}
	
		internal override void SolveVelocity(float f) {
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
