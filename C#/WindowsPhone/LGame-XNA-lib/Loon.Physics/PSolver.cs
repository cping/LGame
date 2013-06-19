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
namespace Loon.Physics {
	
	
	public class PSolver {
	
		internal PBody b1;
		internal PBody b2;
		internal PContact[] cs;
		internal float fric;
		internal int numContacts;
		internal bool rem;
		internal float rest;
		internal PShape s1;
		internal PShape s2;
	
		public PSolver(PShape shape1, PShape shape2, PContact[] contacts, int num) {
			s1 = shape1;
			s2 = shape2;
			b1 = s1._parent;
			b2 = s2._parent;
			fric = (float) System.Math.Sqrt(s1._fric * s2._fric);
			rest = (float) System.Math.Sqrt(s1._rest * s2._rest);
			cs = contacts;
			numContacts = num;
			for (int i = 0; i < numContacts; i++) {
				PContact c = cs[i];
				c.rel1 = c.pos.Sub(b1.pos);
				c.rel2 = c.pos.Sub(b2.pos);
				c.massN = PTransformer.CalcEffectiveMass(b1, b2, c.rel1, c.rel2,
						c.normal);
				c.massT = PTransformer.CalcEffectiveMass(b1, b2, c.rel1, c.rel2,
						c.tangent);
				c.relVel = PTransformer
						.CalcRelativeVelocity(b1, b2, c.rel1, c.rel2);
				float rvn = c.relVel.Dot(c.normal);
				if (rvn < -0.5F)
					c.targetVelocity = System.Math.Max(rest * -rvn,0.0F);
				else
					c.targetVelocity = 0.0F;
				c.tangent.Set(c.normal.y, -c.normal.x);
				c.localRel1.Set(c.rel1.x, c.rel1.y);
				c.localRel2.Set(c.rel2.x, c.rel2.y);
				b1.mAng.Transpose().MulEqual(c.localRel1);
				b2.mAng.Transpose().MulEqual(c.localRel2);
			}
	
		}
	
		private float Clamp(float v, float min, float max) {
			return (v <= max) ? (v >= min) ? v : min : max;
		}
	
		public PContact[] GetContacts() {
			PContact[] c = new PContact[numContacts];
			for (int i = 0; i < numContacts; i++)
				c[i] = cs[i];
	
			return c;
		}
	
		private float Max(float v, float max) {
			return (v >= max) ? v : max;
		}
	
		internal void PreSolve() {
			rem = true;
			for (int i = 0; i < numContacts; i++) {
				PContact c = cs[i];
				b1.ApplyImpulse(c.normal.x * c.norI + c.tangent.x * c.tanI,
						c.normal.y * c.norI + c.tangent.y * c.tanI, c.pos.x,
						c.pos.y);
				b2.ApplyImpulse(c.normal.x * -c.norI + c.tangent.x * -c.tanI,
						c.normal.y * -c.norI + c.tangent.y * -c.tanI, c.pos.x,
						c.pos.y);
				c.corI = 0.0F;
			}
	
		}
	
		internal void SolvePosition() {
			for (int i = 0; i < numContacts; i++) {
				PContact c = cs[i];
				c.relPosVel = PTransformer.CalcRelativeCorrectVelocity(b1, b2,
						c.rel1, c.rel2);
				float rvn = c.normal.Dot(c.relPosVel);
				float subCorrectI = -c.massN * 0.2F * (rvn + c.overlap + 0.002F);
				float newCorrectI = Max(c.corI + subCorrectI, 0.0F);
				subCorrectI = newCorrectI - c.corI;
				float forceX = c.normal.x * subCorrectI;
				float forceY = c.normal.y * subCorrectI;
				b1.PositionCorrection(forceX, forceY, c.pos.x, c.pos.y);
				b2.PositionCorrection(-forceX, -forceY, c.pos.x, c.pos.y);
				c.corI = newCorrectI;
			}
	
		}
	
		internal void SolveVelocity() {
			for (int i = 0; i < numContacts; i++) {
				PContact c = cs[i];
				c.relVel = PTransformer
						.CalcRelativeVelocity(b1, b2, c.rel1, c.rel2);
				float rvn = c.normal.x * c.relVel.x + c.normal.y * c.relVel.y;
				float subNormalI = -c.massN * (rvn - c.targetVelocity);
				float newNormalI = Max(c.norI + subNormalI, 0.0F);
				subNormalI = newNormalI - c.norI;
				float forceX = c.normal.x * subNormalI;
				float forceY = c.normal.y * subNormalI;
				b1.ApplyImpulse(forceX, forceY, c.pos.x, c.pos.y);
				b2.ApplyImpulse(-forceX, -forceY, c.pos.x, c.pos.y);
				c.norI = newNormalI;
			}
	
			for (int i_0 = 0; i_0 < numContacts; i_0++) {
				PContact c_1 = cs[i_0];
				c_1.relVel = PTransformer
						.CalcRelativeVelocity(b1, b2, c_1.rel1, c_1.rel2);
				float rvt = c_1.tangent.x * c_1.relVel.x + c_1.tangent.y * c_1.relVel.y;
				float maxFriction = c_1.norI * fric;
				float subTangentI = c_1.massT * -rvt;
				float newTangentI = Clamp(c_1.tanI + subTangentI, -maxFriction,
						maxFriction);
				subTangentI = newTangentI - c_1.tanI;
				float forceX_2 = c_1.tangent.x * subTangentI;
				float forceY_3 = c_1.tangent.y * subTangentI;
				b1.ApplyImpulse(forceX_2, forceY_3, c_1.pos.x, c_1.pos.y);
				b2.ApplyImpulse(-forceX_2, -forceY_3, c_1.pos.x, c_1.pos.y);
				c_1.tanI = newTangentI;
			}
	
		}
	
		internal void Update(PContact[] contacts, int num) {
			PContact[] old = cs;
			int oldNumContacts = numContacts;
			fric = (float) System.Math.Sqrt(s1._fric * s2._fric);
			rest = (float) System.Math.Sqrt(s1._rest * s2._rest);
			cs = contacts;
			numContacts = num;
			for (int i = 0; i < numContacts; i++) {
				PContact c = cs[i];
				c.rel1 = c.pos.Sub(b1.pos);
				c.rel2 = c.pos.Sub(b2.pos);
				c.massN = PTransformer.CalcEffectiveMass(b1, b2, c.rel1, c.rel2,
						c.normal);
				c.massT = PTransformer.CalcEffectiveMass(b1, b2, c.rel1, c.rel2,
						c.tangent);
				c.tangent.Set(c.normal.y, -c.normal.x);
				c.localRel1.Set(c.rel1.x, c.rel1.y);
				c.localRel2.Set(c.rel2.x, c.rel2.y);
				b1.mAng.Transpose().MulEqual(c.localRel1);
				b2.mAng.Transpose().MulEqual(c.localRel2);
			}
	
			for (int i_0 = 0; i_0 < oldNumContacts; i_0++) {
				for (int j = 0; j < numContacts; j++)
					if (old[i_0].data.id == cs[j].data.id
							&& old[i_0].data.flip == cs[j].data.flip) {
						cs[j].norI = old[i_0].norI;
						cs[j].tanI = old[i_0].tanI;
					}
	
			}
	
			rem = false;
		}
	
	}
}
