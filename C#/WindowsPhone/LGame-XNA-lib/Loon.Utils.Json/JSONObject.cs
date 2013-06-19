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
using Loon.Utils.Collection;
using System.Text;
namespace Loon.Utils.Json {
	
	public class JSONObject {
	
		private ArrayMap _map = new ArrayMap();
	
		public object Get(string key) {
			return _map.Get(key);
		}
	
		public object Get(int idx) {
			return _map.Get(idx);
		}
	
		public ArrayMap.Entry[] entrys() {
			return _map.ToEntrys();
		}
	
		public bool HasKey(string key) {
			return (_map.Get(key) != null);
		}
	
		public JSONObject Put(string key, object value_ren) {
			_map.Put(key, value_ren);
			return this;
		}
	
		public object Remove(string key) {
			return _map.Remove(key);
		}
	
		public object Remove(int idx) {
			return _map.Remove(idx);
		}
	
		public override string ToString() {
			StringBuilder buf = new StringBuilder();
			buf.Append("{");
			bool first = true;
			for (int i = 0; i < _map.Size(); i++) {
				object value_ren = _map.Get(i);
				if (!first) {
					buf.Append(",");
				}
				first = false;
				buf.Append("\"").Append(_map.GetKey(i)).Append("\"").Append(":");
				if (value_ren   is  string) {
					buf.Append("\"").Append(value_ren.ToString()).Append("\"");
				} else {
					buf.Append(value_ren.ToString());
				}
			}
			buf.Append("}");
			return buf.ToString();
		}
	}
}
