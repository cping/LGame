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
	
	public class PCircleShape : PShape {
	
		internal float rad;
		private Loon.Core.Geom.Circle mcircle;
	
		public PCircleShape(float px, float py, float radius, float angle,
				float density) {
			_dens = density;
			_type = PShapeType.CIRCLE_SHAPE;
			_localPos.Set(px, py);
			rad = radius;
			_localAng = angle;
			mm = rad * rad * 3.141593F * _dens;
			ii = (mm * rad * rad * 2.0F) / 5F;
			this.mcircle = new Loon.Core.Geom.Circle(px, py, radius);
		}
	
		internal override void CalcAABB() {
			if (_parent == null) {
				return;
			} else {
				_aabb.Set(_pos.x - rad, _pos.y - rad, _pos.x + rad, _pos.y + rad);
				return;
			}
		}
	
		public float GetRadius() {
			return rad;
		}
	
		internal override void Update() {
		}
	
		public Loon.Core.Geom.Circle GetCircle() {
			return mcircle;
		}
	
	}
}
