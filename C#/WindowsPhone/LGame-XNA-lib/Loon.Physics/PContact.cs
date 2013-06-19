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
	
	public class PContact {
	
		internal float corI;
		internal PContactData data;
		internal Vector2f localRel1;
		internal Vector2f localRel2;
		internal float massN;
		internal float massT;
		internal float norI;
		internal Vector2f normal;
		internal float overlap;
		internal Vector2f pos;
		internal Vector2f rel1;
		internal Vector2f rel2;
		internal Vector2f relPosVel;
		internal Vector2f relVel;
		internal Vector2f tangent;
		internal float tanI;
		internal float targetVelocity;
	
		public PContact() {
			rel1 = new Vector2f();
			rel2 = new Vector2f();
			localRel1 = new Vector2f();
			localRel2 = new Vector2f();
			pos = new Vector2f();
			normal = new Vector2f();
			tangent = new Vector2f();
			relVel = new Vector2f();
			relPosVel = new Vector2f();
			data = new PContactData();
		}
	
		public Vector2f GetNormal() {
			return normal.Clone();
		}
	
		public float GetOverlap() {
			return overlap;
		}
	
		public Vector2f GetPosition() {
			return pos.Clone();
		}
	
		public Vector2f GetRelativeVelocity() {
			return relVel.Clone();
		}
	
		public Vector2f GetTangent() {
			return tangent.Clone();
		}
	
	}
}
