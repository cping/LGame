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
/// @email£ºjavachenpeng@yahoo.com
using System;
using Loon.Utils;
namespace Loon.Core.Geom {
	
	
	public class Vector3f {
	
		private const long serialVersionUID = 1114108169708351982L;
	
		public float x;
	
		public float y;
	
		public float z;
	
		public static readonly Vector3f Right = new Vector3f(1f, 0f, 0f);
	
		public static readonly Vector3f Left = new Vector3f(-1f, 0f, 0f);
	
		public static readonly Vector3f Up = new Vector3f(0f, 1f, 0f);
	
		public static readonly Vector3f Down = new Vector3f(0f, -1f, 0f);
	
		public static readonly Vector3f Backward = new Vector3f(0f, 0f, 1f);
	
		public static readonly Vector3f Forward = new Vector3f(0f, 0f, -1f);
	
		private static Vector3f tmp = new Vector3f();
		private static Vector3f tmp2 = new Vector3f();
		private static Vector3f tmp3 = new Vector3f();
		public static readonly Vector3f X = new Vector3f(1, 0, 0);
		public static readonly Vector3f Y = new Vector3f(0, 1, 0);
		public static readonly Vector3f Z = new Vector3f(0, 0, 1);
		public static readonly Vector3f Zero = new Vector3f(0, 0, 0);
	
		public Vector3f() {
		}
	
		public Vector3f(float x_0, float y_1, float z_2) {
			this.Set(x_0, y_1, z_2);
		}
	
		public Vector3f(Vector3f vector) {
			this.Set(vector);
		}
	
		public Vector3f(float[] values) {
			this.Set(values[0], values[1], values[2]);
		}
	
		public Vector3f Set(float x_0, float y_1, float z_2) {
			this.x = x_0;
			this.y = y_1;
			this.z = z_2;
			return this;
		}
	
		public Vector3f Set(Vector3f vector) {
			return this.Set(vector.x, vector.y, vector.z);
		}
	
		public Vector3f Set(float[] values) {
			return this.Set(values[0], values[1], values[2]);
		}
	
		public Vector3f Cpy() {
			return new Vector3f(this);
		}
	
		public Vector3f Tmp() {
			return tmp.Set(this);
		}
	
		public Vector3f Tmp2() {
			return tmp2.Set(this);
		}
	
		internal Vector3f Tmp3() {
			return tmp3.Set(this);
		}
	
		public Vector3f Add(Vector3f vector) {
			return this.Add(vector.x, vector.y, vector.z);
		}
	
		public Vector3f Add(float x_0, float y_1, float z_2) {
			return this.Set(this.x + x_0, this.y + y_1, this.z + z_2);
		}
	
		public Vector3f Add(float values) {
			return this.Set(this.x + values, this.y + values, this.z + values);
		}
	
		public Vector3f Sub(Vector3f a_vec) {
			return this.Sub(a_vec.x, a_vec.y, a_vec.z);
		}
	
		public Vector3f Sub(float x_0, float y_1, float z_2) {
			return this.Set(this.x - x_0, this.y - y_1, this.z - z_2);
		}
	
		public Vector3f Sub(float value_ren) {
			return this.Set(this.x - value_ren, this.y - value_ren, this.z - value_ren);
		}
	
		public Vector3f Mul(float value_ren) {
			return this.Set(this.x * value_ren, this.y * value_ren, this.z * value_ren);
		}
	
		public Vector3f Div(float value_ren) {
			float d = 1 / value_ren;
			return this.Set(this.x * d, this.y * d, this.z * d);
		}
	
		public float Len() {
			return (float) Math.Sqrt(x * x + y * y + z * z);
		}
	
		public float Len2() {
			return x * x + y * y + z * z;
		}
	
		public bool Idt(Vector3f vector) {
			return x == vector.x && y == vector.y && z == vector.z;
		}
	
		public float Dst(Vector3f vector) {
			float a = vector.x - x;
			float b = vector.y - y;
			float c = vector.z - z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return (float) Math.Sqrt(a + b + c);
		}
	
		public Vector3f Nor() {
			float len = this.Len();
			if (len == 0) {
				return this;
			} else {
				return this.Div(len);
			}
		}
	
		public float Dot(Vector3f vector) {
			return x * vector.x + y * vector.y + z * vector.z;
		}
	
		public Vector3f Crs(Vector3f vector) {
			return this.Set(y * vector.z - z * vector.y, z * vector.x - x
					* vector.z, x * vector.y - y * vector.x);
		}
	
		public Vector3f Crs(float x_0, float y_1, float z_2) {
			return this.Set(this.y * z_2 - this.z * y_1, this.z * x_0 - this.x * z_2,
					this.x * y_1 - this.y * x_0);
		}
	
		public bool IsUnit() {
			return this.Len() == 1;
		}
	
		public bool IsZero() {
			return x == 0 && y == 0 && z == 0;
		}
	
		public override String ToString() {
			return x + "," + y + "," + z;
		}
	
		public float Dot(float x_0, float y_1, float z_2) {
			return this.x * x_0 + this.y * y_1 + this.z * z_2;
		}
	
		public float Dst2(Vector3f point) {
	
			float a = point.x - x;
			float b = point.y - y;
			float c = point.z - z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return a + b + c;
		}
	
		public float Dst2(float x_0, float y_1, float z_2) {
			float a = x_0 - this.x;
			float b = y_1 - this.y;
			float c = z_2 - this.z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return a + b + c;
		}
	
		public float Dst(float x_0, float y_1, float z_2) {
			return (float) Math.Sqrt(Dst2(x_0, y_1, z_2));
		}
	
		public override int GetHashCode() {
			int prime = 31;
			int result = 1;
			result = (int)(prime * result + BitConverter.Int64BitsToDouble((long)x));
			result = (int)(prime * result + BitConverter.Int64BitsToDouble((long)y));
			result = (int)( prime * result + BitConverter.Int64BitsToDouble((long)z));
			return result;
		}
	
		public override bool Equals(Object obj) {
			if ((Object) this == obj)
				return true;
			if (obj == null)
				return false;
			if ((Object) GetType() != (Object) obj.GetType())
				return false;
			Vector3f other = (Vector3f) obj;
			if (BitConverter.Int64BitsToDouble((long)x) != BitConverter.Int64BitsToDouble((long)other.x))
				return false;
			if (BitConverter.Int64BitsToDouble((long)y) != BitConverter.Int64BitsToDouble((long)other.y))
				return false;
			if (BitConverter.Int64BitsToDouble((long)z) != BitConverter.Int64BitsToDouble((long)other.z))
				return false;
			return true;
		}
	
		public static Vector3f Set(Vector3f vectorA, float x_0, float y_1, float z_2) {
			vectorA.x = x_0;
			vectorA.y = y_1;
			vectorA.z = z_2;
			return vectorA;
		}
	
		public static Vector3f Set(Vector3f vectorA, Vector3f vectorB) {
			return Set(vectorA, vectorB.x, vectorB.y, vectorB.z);
		}
	
		public static Vector3f Set(Vector3f vectorA, float[] values) {
			return Set(vectorA, values[0], values[1], values[2]);
		}
	
		public static Vector3f Cpy(Vector3f vectorA) {
			Vector3f newSVector = new Vector3f();
	
			newSVector.x = vectorA.x;
			newSVector.y = vectorA.y;
			newSVector.z = vectorA.z;
	
			return newSVector;
		}
	
		public static Vector3f Add(Vector3f vectorA, Vector3f vectorB) {
			vectorA.x += vectorB.x;
			vectorA.y += vectorB.y;
			vectorA.z += vectorB.z;
	
			return vectorA;
		}
	
		public static Vector3f Add(Vector3f vectorA, float x_0, float y_1, float z_2) {
			vectorA.x += x_0;
			vectorA.y += y_1;
			vectorA.z += z_2;
	
			return vectorA;
		}
	
		public static Vector3f Add(Vector3f vectorA, float value_ren) {
			vectorA.x += value_ren;
			vectorA.y += value_ren;
			vectorA.z += value_ren;
	
			return vectorA;
		}
	
		public static Vector3f Sub(Vector3f vectorA, Vector3f vectorB) {
			vectorA.x -= vectorB.x;
			vectorA.y -= vectorB.y;
			vectorA.z -= vectorB.z;
	
			return vectorA;
		}
	
		public static Vector3f Sub(Vector3f vectorA, float x_0, float y_1, float z_2) {
			vectorA.x -= x_0;
			vectorA.y -= y_1;
			vectorA.z -= z_2;
	
			return vectorA;
		}
	
		public static Vector3f Sub(Vector3f vectorA, float value_ren) {
			vectorA.x -= value_ren;
			vectorA.y -= value_ren;
			vectorA.z -= value_ren;
	
			return vectorA;
		}
	
		public static Vector3f Mul(Vector3f vectorA, float value_ren) {
			vectorA.x = value_ren * vectorA.x;
			vectorA.y = value_ren * vectorA.y;
			vectorA.z = value_ren * vectorA.z;
	
			return vectorA;
		}
	
		public static Vector3f Div(Vector3f vectorA, float value_ren) {
			float d = 1 / value_ren;
			vectorA.x = d * vectorA.x;
			vectorA.y = d * vectorA.y;
			vectorA.z = d * vectorA.z;
	
			return vectorA;
		}
	
		public static float Len(Vector3f vectorA) {
			return MathUtils.Sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y
					+ vectorA.z * vectorA.z);
		}
	
		public static float Len2(Vector3f vectorA) {
			return vectorA.x * vectorA.x + vectorA.y * vectorA.y + vectorA.z
					* vectorA.z;
		}
	
		public static bool Idt(Vector3f vectorA, Vector3f vectorB) {
			return vectorA.x == vectorB.x && vectorA.y == vectorB.y
					&& vectorA.z == vectorB.z;
		}
	
		public static float Dst(Vector3f vectorA, Vector3f vectorB) {
			float a = vectorB.x - vectorA.x;
			float b = vectorB.y - vectorA.y;
			float c = vectorB.z - vectorA.z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return MathUtils.Sqrt(a + b + c);
		}
	
		public static float Dst(Vector3f vectorA, float x_0, float y_1, float z_2) {
			float a = x_0 - vectorA.x;
			float b = y_1 - vectorA.y;
			float c = z_2 - vectorA.z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return MathUtils.Sqrt(a + b + c);
		}
	
		public static float Dot(Vector3f vectorA, Vector3f vectorB) {
			return vectorA.x * vectorB.x + vectorA.y * vectorB.y + vectorA.z
					* vectorB.z;
		}
	
		public static Vector3f Crs(Vector3f vectorA, Vector3f vectorB) {
			vectorA.x = vectorA.y * vectorB.z - vectorA.z * vectorB.y;
			vectorA.y = vectorA.z * vectorB.x - vectorA.x * vectorB.z;
			vectorA.z = vectorA.x * vectorB.y - vectorA.y * vectorB.x;
	
			return vectorA;
		}
	
		public static Vector3f Crs(Vector3f vectorA, float x_0, float y_1, float z_2) {
			vectorA.x = vectorA.y * z_2 - vectorA.z * y_1;
			vectorA.y = vectorA.z * x_0 - vectorA.x * z_2;
			vectorA.z = vectorA.x * y_1 - vectorA.y * x_0;
	
			return vectorA;
		}
	
		public static bool IsZero(Vector3f vectorA) {
			return vectorA.x == 0 && vectorA.y == 0 && vectorA.z == 0;
		}
	
		public static Vector3f Lerp(Vector3f vectorA, Vector3f target, float alpha) {
			Vector3f r = Mul(vectorA, 1.0f - alpha);
			Add(r, Mul(Cpy(vectorA), alpha));
	
			return r;
		}
	
		public static float Dot(Vector3f vectorA, float x_0, float y_1, float z_2) {
			return vectorA.x * x_0 + vectorA.y * y_1 + vectorA.z * z_2;
		}
	
		public static float Dst2(Vector3f vectorA, Vector3f vectorB) {
			float a = vectorB.x - vectorA.x;
			float b = vectorB.y - vectorA.y;
			float c = vectorB.z - vectorA.z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return a + b + c;
		}
	
		public static float Dst2(Vector3f vectorA, float x_0, float y_1, float z_2) {
			float a = x_0 - vectorA.x;
			float b = y_1 - vectorA.y;
			float c = z_2 - vectorA.z;
	
			a *= a;
			b *= b;
			c *= c;
	
			return a + b + c;
		}
	
		public static Vector3f Scale(Vector3f vectorA, float scalarX,
				float scalarY, float scalarZ) {
			vectorA.x *= scalarX;
			vectorA.y *= scalarY;
			vectorA.z *= scalarZ;
			return vectorA;
		}
	
		public static float AngleBetween(Vector3f vectorA, Vector3f other) {
			float angle;
	
			float dot = Dot(vectorA, other);
	
			float len1 = Len(vectorA);
			float len2 = Len(other);
	
			if (len1 == 0 && len2 == 0) {
				return 0;
			}
	
			angle = (float) System.Math.Acos(dot / (len1 * len2));
	
			return angle;
		}
	
		public static float AngleBetween(Vector3f vectorA, float x_0, float y_1, float z_2) {
			float angle;
	
			float dot = Dot(vectorA, x_0, y_1, z_2);
	
			float len1 = Len(vectorA);
			float len2 = MathUtils.Sqrt(x_0 * x_0 + y_1 * y_1 + z_2 * z_2);
	
			if (len1 == 0 || len2 == 0) {
				return 0;
			}
	
			angle = (float) System.Math.Acos(dot / (len1 * len2));
	
			return angle;
		}
	
		public static float AngleBetweenXY(Vector3f vectorA, float x_0, float y_1) {
			float angle;
	
			float dot = vectorA.x * x_0 + vectorA.y * y_1;
	
			float len1 = MathUtils.Sqrt(vectorA.x * vectorA.x + vectorA.y
					* vectorA.y);
			float len2 = MathUtils.Sqrt(x_0 * x_0 + y_1 * y_1);
	
			if (len1 == 0 || len2 == 0) {
				return 0;
			}
	
			angle = (float) System.Math.Acos(dot / (len1 * len2));
	
			return angle;
		}
	
		public static float AngleBetweenXZ(Vector3f vectorA, float x_0, float z_1) {
			float angle;
	
			float dot = vectorA.x * x_0 + vectorA.z * z_1;
	
			float len1 = MathUtils.Sqrt(vectorA.x * vectorA.x + vectorA.z
					* vectorA.z);
			float len2 = MathUtils.Sqrt(x_0 * x_0 + z_1 * z_1);
	
			if (len1 == 0 || len2 == 0) {
				return 0;
			}
	
			angle = (float) System.Math.Acos(dot / (len1 * len2));
	
			return angle;
		}
	
		public static float AngleBetweenYZ(Vector3f vectorA, float y_0, float z_1) {
			float angle;
	
			float dot = vectorA.y * y_0 + vectorA.z * z_1;
	
			float len1 = MathUtils.Sqrt(vectorA.y * vectorA.y + vectorA.z
					* vectorA.z);
			float len2 = MathUtils.Sqrt(y_0 * y_0 + z_1 * z_1);
	
			if (len1 == 0 || len2 == 0) {
				return 0;
			}
	
			angle = (float) System.Math.Acos(dot / (len1 * len2));
	
			return angle;
		}
	
		public static float[] ToArray(Vector3f vectorA) {
			return new float[] { vectorA.x, vectorA.y, vectorA.z };
		}
	}
}
