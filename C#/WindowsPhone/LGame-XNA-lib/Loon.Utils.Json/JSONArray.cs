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

namespace Loon.Utils.Json {

    using Loon.Utils.Collection;
    using System.Collections.Generic;
    using System;
    using System.Text;

	public class JSONArray {
	
		private Array<object> list;
	
		internal JSONArray() {
            this.list = new Array<object>();
		}
	
		public static JSONArray FromObject(params object[] objs) {
			JSONArray json = new JSONArray();
			foreach (object s  in  objs) {
				json.list.Add(s);
			}
			return json;
		}
	
		public static JSONArray FromObject<T0>(List<T0> objs) {
			JSONArray json = new JSONArray();
			foreach (object s  in  objs) {
				json.list.Add(s);
			}
			return json;
		}
	
		public static JSONArray FromObject<T0>(Loon.Java.Generics.JavaListInterface.ISet<T0> objs) {
			JSONArray json = new JSONArray();
			foreach (object s  in  objs) {
				json.list.Add(s);
			}
			return json;
		}
	
		public int Length() {
			return list.Size();
		}
	
		public int Size() {
			return list.Size();
		}
	
		public bool IsEmpty() {
			return list.IsEmpty();
		}
	
		public object Get(int index) {
			return list.Get(index);
		}
	
		public Int32 GetInt(int index) {
			object ob = list.Get(index);
			return (ob  is  Int32) ? (Int32) ob :  default(Int32);
		}
	
		public Int64 GetLong(int index) {
			object ob = list.Get(index);
			return (ob  is  Int64) ? (Int64) ob :  default(Int64);
		}
	
		public Double GetDouble(int index) {
			object ob = list.Get(index);
			return (ob  is  Double) ? (Double) ob :  default(Double);
		}
	
		public object GetBool(int index) {
			object ob = list.Get(index);
			return (ob  is  Boolean) ? (Boolean) ob :  default(object);
		}
	
		public JSONArray GetArray(int index) {
			object ob = list.Get(index);
			return (ob  is  JSONArray) ? (JSONArray) ob : null;
		}
	
		public JSONObject GetObject(int index) {
			object ob = list.Get(index);
			return (ob  is  JSONObject) ? (JSONObject) ob : null;
		}
	
		public bool IsNull(int index) {
			return list.Get(index) == null;
		}
	
		public Array<object> Array() {
			return list.Copy();
		}
	
		public override String ToString() {
            StringBuilder str = new StringBuilder().Append('{');
            int size = list.Size(), i = 0;

            ArrayNode<Object> o = this.list.Node().next;
            for (; o != this.list.Node(); )
            {
                if (o.data is String)
                {
                    str.Append('"').Append(o.data).Append('"');
                }
                else
                {
                    str.Append(o.data);
                }
                if (++i < size)
                {
                    str.Append(',');
                }
                o = o.next;
            }
            str.Append('}');

            return str.ToString();
		}
	
	}
}
