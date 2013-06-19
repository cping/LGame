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
using System.Text;
namespace Loon.Physics {
	
	public class PTransformer {
	
		public float e00;
		public float e01;
		public float e10;
		public float e11;
	
		public PTransformer() {
			SetIdentity();
		}
	
		public PTransformer(float e00_0, float e01_1, float e10_2, float e11_3) {
			Set(e00_0, e01_1, e10_2, e11_3);
		}
	
		public PTransformer Add(PTransformer m) {
	
			return new PTransformer(e00 + m.e00, e01 + m.e01, e10 + m.e10, e11
					+ m.e11);
		}
	
		public void AddEqual(PTransformer m) {
			e00 += m.e00;
			e01 += m.e01;
			e10 += m.e10;
			e11 += m.e11;
		}
	
		public virtual PTransformer Clone() {
			return new PTransformer(e00, e01, e10, e11);
		}
	
		public PTransformer Invert() {
			float d = e00 * e11 - e10 * e01;
			if (d != 0.0F)
				d = 1.0F / d;
			else
				return new PTransformer();
			PTransformer ret = new PTransformer(d * e11, -d * e01, -d * e10, d
					* e00);
			return ret;
		}
	
		public void InvertEqual() {
			float d = e00 * e11 - e10 * e01;
			if (d != 0.0F)
				d = 1.0F / d;
			else
				SetIdentity();
			Set(d * e11, -d * e01, -d * e10, d * e00);
		}
	
		public PTransformer Mul(PTransformer m) {
			float t11 = e00 * m.e00 + e01 * m.e10;
			float t12 = e00 * m.e01 + e01 * m.e11;
			float t13 = e10 * m.e00 + e11 * m.e10;
			float t14 = e10 * m.e01 + e11 * m.e11;
			return new PTransformer(t11, t12, t13, t14);
		}
	
		public Vector2f Mul(Vector2f v) {
			return new Vector2f(e00 * v.x + e10 * v.y, e01 * v.x + e11 * v.y);
		}
	
		public void MulEqual(PTransformer m) {
			float t11 = e00 * m.e00 + e01 * m.e10;
			float t12 = e00 * m.e01 + e01 * m.e11;
			float t13 = e10 * m.e00 + e11 * m.e10;
			float t14 = e10 * m.e01 + e11 * m.e11;
			Set(t11, t12, t13, t14);
		}
	
		public void MulEqual(Vector2f v) {
			v.Set(e00 * v.x + e10 * v.y, e01 * v.x + e11 * v.y);
		}
	
		public void Set(float e00_0, float e01_1, float e10_2, float e11_3) {
			this.e00 = e00_0;
			this.e01 = e01_1;
			this.e10 = e10_2;
			this.e11 = e11_3;
		}
	
		public void SetIdentity() {
			e01 = e10 = 0.0F;
			e00 = e11 = 1.0F;
		}
	
		public void SetRotate(float theta) {
			float sin = (float) System.Math.Sin(theta);
			float cos = (float) System.Math.Cos(theta);
			e00 = cos;
			e01 = sin;
			e10 = -sin;
			e11 = cos;
		}
	
		public override string ToString() {
            return (new StringBuilder("[[")).Append(e00).Append(", ").Append(e01)
                    .Append("], [").Append(e10).Append(", ").Append(e11)
                    .Append("]]").ToString();
		}
	
		public PTransformer Transpose() {
			return new PTransformer(e00, e10, e01, e11);
		}
	
		public void TransposeEqual() {
			Set(e00, e10, e01, e11);
		}
	
		public static PTransformer CalcEffectiveMass(PBody b1, PBody b2,
				Vector2f r1, Vector2f r2) {
			PTransformer mass = new PTransformer(b1.invM + b2.invM + b1.invI * r1.y
					* r1.y + b2.invI * r2.y * r2.y, -b1.invI * r1.x * r1.y
					- b2.invI * r2.x * r2.y, -b1.invI * r1.x * r1.y - b2.invI
					* r2.x * r2.y, b1.invM + b2.invM + b1.invI * r1.x * r1.x
					+ b2.invI * r2.x * r2.x);
			mass.InvertEqual();
			return mass;
		}
	
		public static float CalcEffectiveMass(PBody b1, PBody b2, Vector2f r1,
				Vector2f r2, Vector2f normal) {
			float rn1 = normal.Dot(r1);
			float rn2 = normal.Dot(r2);
			return 1.0F / (b1.invM + b2.invM + b1.invI
					* ((r1.x * r1.x + r1.y * r1.y) - rn1 * rn1) + b2.invI
					* ((r2.x * r2.x + r2.y * r2.y) - rn2 * rn2));
		}
	
		public static PTransformer CalcEffectiveMass(PBody b, Vector2f r) {
			PTransformer mass = new PTransformer(b.invM + b.invI * r.y * r.y,
					-b.invI * r.x * r.y, -b.invI * r.x * r.y, b.invM + b.invI * r.x
							* r.x);
			mass.InvertEqual();
			return mass;
		}
	
		public static Vector2f CalcRelativeCorrectVelocity(PBody b1, PBody b2,
				Vector2f r1, Vector2f r2) {
			Vector2f relVel = b1.correctVel.Clone();
			relVel.x -= b2.correctVel.x;
			relVel.y -= b2.correctVel.y;
			relVel.x += -b1.correctAngVel * r1.y;
			relVel.y += b1.correctAngVel * r1.x;
			relVel.x -= -b2.correctAngVel * r2.y;
			relVel.y -= b2.correctAngVel * r2.x;
			return relVel;
		}
	
		public static Vector2f CalcRelativeVelocity(PBody b1, PBody b2,
				Vector2f r1, Vector2f r2) {
			Vector2f relVel = b1.vel.Clone();
			relVel.x -= b2.vel.x;
			relVel.y -= b2.vel.y;
			relVel.x += -b1.angVel * r1.y;
			relVel.y += b1.angVel * r1.x;
			relVel.x -= -b2.angVel * r2.y;
			relVel.y -= b2.angVel * r2.x;
			return relVel;
		}
	
	}
}
