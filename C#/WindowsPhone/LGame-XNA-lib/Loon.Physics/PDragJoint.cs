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

	public class PDragJoint : PJoint {
	
		private Vector2f anchor;
		private PBody b;
		private Vector2f dragPoint;
		private Vector2f localAnchor;
		private PTransformer mass;
		private Vector2f relAnchor;
	
		public PDragJoint(PBody b_0, float px, float py) {
			this.b = b_0;
			dragPoint = new Vector2f(px, py);
			localAnchor = new Vector2f(px - b_0.pos.x, py - b_0.pos.y);
			b_0.mAng.Transpose().MulEqual(localAnchor);
			anchor = b_0.mAng.Mul(localAnchor);
			anchor.AddLocal(b_0.pos);
			type = Physics.PJointType.DRAG_JOINT;
			mass = new PTransformer();
		}
	
		public Vector2f GetAnchorPoint() {
			return anchor.Clone();
		}
	
		public PBody GetBody() {
			return b;
		}
	
		public Vector2f GetDragPoint() {
			return dragPoint.Clone();
		}
	
		public Vector2f GetRelativeAnchorPoint() {
			return relAnchor.Clone();
		}
	
		internal override void PreSolve(float dt) {
			relAnchor = b.mAng.Mul(localAnchor);
			anchor.Set(relAnchor.x + b.pos.x, relAnchor.y + b.pos.y);
			mass = PTransformer.CalcEffectiveMass(b, relAnchor);
			Vector2f f = anchor.Sub(dragPoint);
			float k = b.m;
			f.MulLocal(-k * 20F);
			Vector2f relVel = b.vel.Clone();
			relVel.x += -b.angVel * relAnchor.y;
			relVel.y += b.angVel * relAnchor.x;
			relVel.MulLocal((float) System.Math.Sqrt(k * 20F * k));
			f.SubLocal(relVel);
			f.MulLocal(dt);
			mass.MulEqual(f);
			b.ApplyImpulse(f.x, f.y, anchor.x, anchor.y);
		}
	
		public void SetDragPosition(float px, float py) {
			dragPoint.Set(px, py);
		}
	
		public void SetRelativeAnchorPoint(float relx, float rely) {
			localAnchor.Set(relx, rely);
			b.mAng.Transpose().MulEqual(localAnchor);
		}
	
		internal override void SolvePosition() {
		}
	
		internal override void SolveVelocity(float f) {
		}
	
		internal override void Update() {
			relAnchor = b.mAng.Mul(localAnchor);
			anchor.Set(relAnchor.x + b.pos.x, relAnchor.y + b.pos.y);
			if (b.rem || b.fix) {
				rem = true;
			}
		}
	
	}
}
