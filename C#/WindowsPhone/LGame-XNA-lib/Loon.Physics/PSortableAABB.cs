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
	
	public class PSortableAABB {
	
		internal AABB aabb;
		internal PSortableObject beginX;
		internal PSortableObject beginY;
		internal PSortableObject endX;
		internal PSortableObject endY;
		internal PShape parent;
		internal PSweepAndPrune sap;
		internal bool set;
	
		public PSortableAABB() {
		}
	
		internal void Remove() {
			if (!set) {
				return;
			} else {
				sap.RemoveObject(beginX, beginY);
				sap.RemoveObject(endX, endY);
				return;
			}
		}
	
		public void Set(PSweepAndPrune sap, PShape s, AABB aabb) {
			set = true;
			this.sap = sap;
			parent = s;
			this.aabb = aabb;
			beginX = new PSortableObject(s, this, aabb.minX, true);
			beginY = new PSortableObject(s, this, aabb.minY, true);
			endX = new PSortableObject(s, this, aabb.maxX, false);
			endY = new PSortableObject(s, this, aabb.maxY, false);
			sap.AddObject(beginX, beginY);
			sap.AddObject(endX, endY);
		}
	
		internal void Update() {
			if (!set) {
				return;
			} else {
				beginX.value_ren = aabb.minX;
				beginY.value_ren = aabb.minY;
				endX.value_ren = aabb.maxX;
				endY.value_ren = aabb.maxY;
				return;
			}
		}
	
	}
}
