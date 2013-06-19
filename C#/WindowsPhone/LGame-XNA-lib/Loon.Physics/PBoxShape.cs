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
	
	public class PBoxShape : PConvexPolygonShape {
	
		private float width, height;
	
		public PBoxShape(float px, float py, float width_0, float height_1,
				float angle, float density):base(new float[] { px - width_0 * 0.5F, px + width_0 * 0.5F,
					px + width_0 * 0.5F, px - width_0 * 0.5F }, new float[] {
					py - height_1 * 0.5F, py - height_1 * 0.5F, py + height_1 * 0.5F,
					py + height_1 * 0.5F }, density) {
			
			this._type = Physics.PShapeType.BOX_SHAPE;
			this._localAng = angle;
			this.width = width_0;
			this.height = height_1;
	
		}
	
		public float GetWidth() {
			return width;
		}
	
		public float GetHeight() {
			return height;
		}
	
	}
}
