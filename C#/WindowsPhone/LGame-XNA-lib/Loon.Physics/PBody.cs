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

	public class PBody {
	
		internal AABB aabb;
		internal float ang;
		internal float angVel;
		internal float correctAngVel;
		internal Vector2f correctVel;
		internal bool fix;
		internal float i;
		internal float invI;
		internal float invM;
		internal float m;
		internal PTransformer mAng;
		internal int numShapes;
		internal Vector2f pos;
		internal bool rem;
		internal PShape[] shapes;
		internal Vector2f vel;
		internal PPhysWorld w;
		internal object tag;
	
		public PBody(float angle, bool fixate, PShape[] ss) {
			pos = new Vector2f();
			vel = new Vector2f();
			correctVel = new Vector2f();
			mAng = new PTransformer();
			aabb = new AABB();
			ang = angle;
			numShapes = ss.Length;
			shapes = new PShape[1024];
			for (int i_0 = 0; i_0 < numShapes; i_0++) {
				shapes[i_0] = ss[i_0];
				shapes[i_0]._parent = this;
				if (shapes[i_0]._type == PShapeType.CONCAVE_SHAPE) {
					PConcavePolygonShape cp = (PConcavePolygonShape) shapes[i_0];
					for (int j = 0; j < cp.numConvexes; j++) {
						cp.convexes[j]._parent = this;
					}
	
				}
			}
			fix = fixate;
			CalcMassData();
		}
	
		public void AddShape(PShape s) {
			if (w != null) {
				w.AddShape(s);
			}
			shapes[numShapes] = s;
			s._localPos.SubLocal(pos);
			s._parent = this;
			if (s._type == PShapeType.CONCAVE_SHAPE) {
				PConcavePolygonShape cp = (PConcavePolygonShape) s;
				for (int i_0 = 0; i_0 < cp.numConvexes; i_0++) {
					cp.convexes[i_0]._parent = this;
				}
			}
			numShapes++;
			CalcMassData();
		}
	
		public void ApplyForce(float fx, float fy) {
			if (fix) {
				return;
			} else {
				vel.x += fx * invM;
				vel.y += fy * invM;
				return;
			}
		}
	
		public void ApplyImpulse(float fx, float fy, float px, float py) {
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
	
		public void ApplyTorque(float torque) {
			if (fix) {
				return;
			} else {
				angVel += torque * invI;
				return;
			}
		}
	
		internal void CalcMassData() {
			CorrectCenterOfGravity();
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
	
		internal void CorrectCenterOfGravity() {
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
			for (int j_0 = 0; j_0 < numShapes; j_0++) {
				shapes[j_0]._localPos.x -= cx;
				shapes[j_0]._localPos.y -= cy;
			}
	
		}
	
		public float GetAngularVelocity() {
			return angVel;
		}
	
		public Vector2f getPosition() {
			return pos;
		}
	
		public PShape[] GetShapes() {
			PShape[] result = new PShape[numShapes];
			System.Array.Copy((shapes),0,(result),0,numShapes);
			return result;
		}
	
		public int Size() {
			return numShapes;
		}
	
		public PShape[] Inner_shapes() {
			return shapes;
		}
	
		public Vector2f getVelocity() {
			return vel;
		}
	
		public bool IsFixate() {
			return fix;
		}
	
		private float Max(float v1, float v2) {
			return (v1 <= v2) ? v2 : v1;
		}
	
		private float Min(float v1, float v2) {
			return (v1 >= v2) ? v2 : v1;
		}
	
		internal void PositionCorrection(float torque) {
			if (fix) {
				return;
			} else {
				correctAngVel += torque * invI;
				return;
			}
		}
	
		internal void PositionCorrection(float fx, float fy, float px, float py) {
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
	
		public void Remove() {
			this.rem = true;
		}
	
		public void RemoveShape(PShape s) {
			for (int i_0 = 0; i_0 < numShapes; i_0++) {
				if (shapes[i_0] != s) {
					continue;
				}
				s._rem = true;
				s._parent = null;
				s._localPos.AddLocal(pos);
				if (i_0 != numShapes - 1) {
					System.Array.Copy((shapes),i_0 + 1,(shapes),i_0,numShapes - i_0 - 1);
				}
				break;
			}
			numShapes--;
			CalcMassData();
		}
	
		public void SetAngularVelocity(float v) {
			angVel = v;
		}
	
		public void SetFixate(bool fixate) {
			if (fix == fixate) {
				return;
			} else {
				fix = fixate;
				CalcMassData();
				return;
			}
		}
	
		public void SetVelocity(float vx, float vy) {
			vel.Set(vx, vy);
		}
	
		internal void Update() {
			float twoPI = MathUtils.TWO_PI;
			ang = (ang + twoPI) % twoPI;
			mAng.SetRotate(ang);
			for (int i_0 = 0; i_0 < numShapes; i_0++) {
				PShape s = shapes[i_0];
				s._pos.Set(s._localPos.x, s._localPos.y);
				mAng.MulEqual(s._pos);
				s._pos.AddLocal(pos);
				s._localAng = (s._localAng + twoPI) % twoPI;
				s._ang = ang + s._localAng;
				s._mAng.SetRotate(s._ang);
				s.Update();
				s.CalcAABB();
				s._sapAABB.Update();
				if (i_0 == 0) {
					aabb.Set(s._aabb.minX, s._aabb.minY, s._aabb.maxX, s._aabb.maxY);
				} else {
					aabb.Set(Min(aabb.minX, s._aabb.minX),
							Min(aabb.minY, s._aabb.minY),
							Max(aabb.maxX, s._aabb.maxX),
							Max(aabb.maxY, s._aabb.maxY));
				}
			}
	
		}
	
		public object GetTag() {
			return tag;
		}
	
		public void SetTag(object tag_0) {
			this.tag = tag_0;
		}
	
	}
}
