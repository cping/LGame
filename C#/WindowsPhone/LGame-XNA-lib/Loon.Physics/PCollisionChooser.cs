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
	
	public class PCollisionChooser : PCollider {
	
		public virtual int Collide(PShape s1, PShape s2, PContact[] cs) {
			PCollider collider = null;
			bool flip = false;
			switch (s1._type) {
			case Physics.PShapeType.BOX_SHAPE:
			case Physics.PShapeType.CONVEX_SHAPE:
				switch (s2._type) {
				case Physics.PShapeType.BOX_SHAPE:
				case Physics.PShapeType.CONVEX_SHAPE:
					collider = new PPolygonPolygonCollider();
					break;
				case Physics.PShapeType.CIRCLE_SHAPE:
					collider = new PCirclePolygonCollider();
					flip = true;
					break;
				case Physics.PShapeType.CONCAVE_SHAPE:
				default:
					break;
				}
				break;
			case Physics.PShapeType.CIRCLE_SHAPE:
				switch (s2._type) {
				case Physics.PShapeType.BOX_SHAPE:
				case Physics.PShapeType.CONVEX_SHAPE:
					collider = new PCirclePolygonCollider();
					break;
				case Physics.PShapeType.CIRCLE_SHAPE:
					collider = new PCircleCirlceCollider();
					break;
				case Physics.PShapeType.CONCAVE_SHAPE:
				default:
					break;
				}
				break;
			case Physics.PShapeType.CONCAVE_SHAPE:
			default:
				break;
			}
			if (collider == null) {
				return 0;
			}
			if (flip) {
				int res = collider.Collide(s2, s1, cs);
				if (res > 0) {
					for (int i = 0; i < res; i++) {
						cs[i].normal.NegateLocal();
					}
				}
				return res;
			}
			return collider.Collide(s1, s2, cs);
		}
	}}
