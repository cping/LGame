/// <summary>
/// Copyright 2008 - 2012
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
/// @project loon
/// @email javachenpeng@yahoo.com
namespace Loon.Core.Geom {
	
	public class FloatValue {
	
		private float value_ren;
	
		public FloatValue(float v) {
			this.value_ren = v;
		}
	
		public float Get() {
			return value_ren;
		}
	
		public void Set(float v) {
			this.value_ren = v;
		}
	
		public override string ToString() {
			return value_ren.ToString();
		}
	
	}
}
