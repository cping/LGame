/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package loon.core.geom;

import java.io.Serializable;

import loon.utils.MathUtils;


public class Vector3f implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1114108169708351982L;

	public float x;

	public float y;

	public float z;
	
	public Vector3f() {
	}

	public Vector3f(float x, float y, float z) {
		this.set(x, y, z);
	}

	public Vector3f(Vector3f vector) {
		this.set(vector);
	}

	public Vector3f(float[] values) {
		this.set(values[0], values[1], values[2]);
	}

	public Vector3f set(float[] values) {
		return this.set(values[0], values[1], values[2]);
	}

	public Vector3f cpy() {
		return new Vector3f(this);
	}
	
	public Vector3f add(float x, float y, float z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f add(float values) {
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	public Vector3f sub(float x, float y, float z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	public Vector3f sub(float value) {
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	public Vector3f div(float value) {
		float d = 1 / value;
		return this.set(this.x * d, this.y * d, this.z * d);
	}

	public float len() {
		return MathUtils.sqrt(x * x + y * y + z * z);
	}

	public float len2() {
		return x * x + y * y + z * z;
	}

	public boolean idt(Vector3f vector) {
		return x == vector.x && y == vector.y && z == vector.z;
	}

	public float dst(Vector3f vector) {
		float a = vector.x - x;
		float b = vector.y - y;
		float c = vector.z - z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public Vector3f nor() {
		float len = this.len();
		if (len == 0) {
			return this;
		} else {
			return this.div(len);
		}
	}

	public float dot(Vector3f vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	public Vector3f crs(Vector3f vector) {
		return this.set(y * vector.z - z * vector.y, z * vector.x - x
				* vector.z, x * vector.y - y * vector.x);
	}

	public Vector3f crs(float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z,
				this.x * y - this.y * x);
	}

	public boolean isUnit() {
		return this.len() == 1;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public float dst2(Vector3f point) {

		float a = point.x - x;
		float b = point.y - y;
		float c = point.z - z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public float dst2(float x, float y, float z) {
		float a = x - this.x;
		float b = y - this.y;
		float c = z - this.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public float dst(float x, float y, float z) {
		return MathUtils.sqrt(dst2(x, y, z));
	}

	public static Vector3f set(Vector3f vectorA, float x, float y, float z) {
		vectorA.x = x;
		vectorA.y = y;
		vectorA.z = z;
		return vectorA;
	}

	public static Vector3f set(Vector3f vectorA, Vector3f vectorB) {
		return set(vectorA, vectorB.x, vectorB.y, vectorB.z);
	}

	public static Vector3f set(Vector3f vectorA, float[] values) {
		return set(vectorA, values[0], values[1], values[2]);
	}

	public static Vector3f cpy(Vector3f vectorA) {
		Vector3f newSVector = new Vector3f();

		newSVector.x = vectorA.x;
		newSVector.y = vectorA.y;
		newSVector.z = vectorA.z;

		return newSVector;
	}

	public static Vector3f add(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x += vectorB.x;
		vectorA.y += vectorB.y;
		vectorA.z += vectorB.z;

		return vectorA;
	}

	public static Vector3f add(Vector3f vectorA, float x, float y, float z) {
		vectorA.x += x;
		vectorA.y += y;
		vectorA.z += z;

		return vectorA;
	}

	public static Vector3f add(Vector3f vectorA, float value) {
		vectorA.x += value;
		vectorA.y += value;
		vectorA.z += value;

		return vectorA;
	}

	public static Vector3f sub(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x -= vectorB.x;
		vectorA.y -= vectorB.y;
		vectorA.z -= vectorB.z;

		return vectorA;
	}

	public static Vector3f sub(Vector3f vectorA, float x, float y, float z) {
		vectorA.x -= x;
		vectorA.y -= y;
		vectorA.z -= z;

		return vectorA;
	}

	public static Vector3f sub(Vector3f vectorA, float value) {
		vectorA.x -= value;
		vectorA.y -= value;
		vectorA.z -= value;

		return vectorA;
	}

	public static Vector3f mul(Vector3f vectorA, float value) {
		vectorA.x = value * vectorA.x;
		vectorA.y = value * vectorA.y;
		vectorA.z = value * vectorA.z;

		return vectorA;
	}

	public static Vector3f div(Vector3f vectorA, float value) {
		float d = 1 / value;
		vectorA.x = d * vectorA.x;
		vectorA.y = d * vectorA.y;
		vectorA.z = d * vectorA.z;

		return vectorA;
	}

	public static float len(Vector3f vectorA) {
		return MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y
				+ vectorA.z * vectorA.z);
	}

	public static float len2(Vector3f vectorA) {
		return vectorA.x * vectorA.x + vectorA.y * vectorA.y + vectorA.z
				* vectorA.z;
	}

	public static boolean idt(Vector3f vectorA, Vector3f vectorB) {
		return vectorA.x == vectorB.x && vectorA.y == vectorB.y
				&& vectorA.z == vectorB.z;
	}

	public static float dst(Vector3f vectorA, Vector3f vectorB) {
		float a = vectorB.x - vectorA.x;
		float b = vectorB.y - vectorA.y;
		float c = vectorB.z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public static float dst(Vector3f vectorA, float x, float y, float z) {
		float a = x - vectorA.x;
		float b = y - vectorA.y;
		float c = z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public static Vector3f crs(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x = vectorA.y * vectorB.z - vectorA.z * vectorB.y;
		vectorA.y = vectorA.z * vectorB.x - vectorA.x * vectorB.z;
		vectorA.z = vectorA.x * vectorB.y - vectorA.y * vectorB.x;

		return vectorA;
	}

	public static Vector3f crs(Vector3f vectorA, float x, float y, float z) {
		vectorA.x = vectorA.y * z - vectorA.z * y;
		vectorA.y = vectorA.z * x - vectorA.x * z;
		vectorA.z = vectorA.x * y - vectorA.y * x;

		return vectorA;
	}

	public static boolean isZero(Vector3f vectorA) {
		return vectorA.x == 0 && vectorA.y == 0 && vectorA.z == 0;
	}

	public static Vector3f lerp(Vector3f vectorA, Vector3f target, float alpha) {
		Vector3f r = mul(vectorA, 1.0f - alpha);
		add(r, mul(cpy(vectorA), alpha));

		return r;
	}

	public static float dot(Vector3f vectorA, float x, float y, float z) {
		return vectorA.x * x + vectorA.y * y + vectorA.z * z;
	}

	public static float dst2(Vector3f vectorA, Vector3f vectorB) {
		float a = vectorB.x - vectorA.x;
		float b = vectorB.y - vectorA.y;
		float c = vectorB.z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public static float dst2(Vector3f vectorA, float x, float y, float z) {
		float a = x - vectorA.x;
		float b = y - vectorA.y;
		float c = z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public static Vector3f scale(Vector3f vectorA, float scalarX,
			float scalarY, float scalarZ) {
		vectorA.x *= scalarX;
		vectorA.y *= scalarY;
		vectorA.z *= scalarZ;
		return vectorA;
	}

	public static float angleBetween(Vector3f vectorA, Vector3f other) {
		float angle;

		float dot = dot(vectorA, other);

		float len1 = len(vectorA);
		float len2 = len(other);

		if (len1 == 0 && len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetween(Vector3f vectorA, float x, float y, float z) {
		float angle;

		float dot = dot(vectorA, x, y, z);

		float len1 = len(vectorA);
		float len2 = MathUtils.sqrt(x * x + y * y + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenXY(Vector3f vectorA, float x, float y) {
		float angle;

		float dot = vectorA.x * x + vectorA.y * y;

		float len1 = MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.y
				* vectorA.y);
		float len2 = MathUtils.sqrt(x * x + y * y);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenXZ(Vector3f vectorA, float x, float z) {
		float angle;

		float dot = vectorA.x * x + vectorA.z * z;

		float len1 = MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.z
				* vectorA.z);
		float len2 = MathUtils.sqrt(x * x + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenYZ(Vector3f vectorA, float y, float z) {
		float angle;

		float dot = vectorA.y * y + vectorA.z * z;

		float len1 = MathUtils.sqrt(vectorA.y * vectorA.y + vectorA.z
				* vectorA.z);
		float len2 = MathUtils.sqrt(y * y + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float[] toArray(Vector3f vectorA) {
		return new float[] { vectorA.x, vectorA.y, vectorA.z };
	}
	

	public Vector3f set(Vector3f argVec) {
		x = argVec.x;
		y = argVec.y;
		z = argVec.z;
		return this;
	}
	
	public Vector3f set(float argX, float argY, float argZ) {
		x = argX;
		y = argY;
		z = argZ;
		return this;
	}
	
	public Vector3f addLocal(Vector3f argVec) {
		x += argVec.x;
		y += argVec.y;
		z += argVec.z;
		return this;
	}
	
	public Vector3f add(Vector3f argVec) {
		return new Vector3f(x + argVec.x, y + argVec.y, z + argVec.z);
	}
	
	public Vector3f sub(Vector3f argVec) {
		return new Vector3f(x - argVec.x, y - argVec.y, z - argVec.z);
	}
	
	public Vector3f mul(float argScalar) {
		return new Vector3f(x * argScalar, y * argScalar, z * argScalar);
	}
	
	public Vector3f negate() {
		return new Vector3f(-x, -y, -z);
	}
	
	public void setZero() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	@Override
	public Vector3f clone() {
		return new Vector3f(this);
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3f other = (Vector3f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}
	
	public final static float dot(Vector3f a, Vector3f b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	public final static Vector3f cross(Vector3f a, Vector3f b) {
		return new Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}
	
	public final static void crossToOut(Vector3f a, Vector3f b, Vector3f out) {
		final float tempy = a.z * b.x - a.x * b.z;
		final float tempz = a.x * b.y - a.y * b.x;
		out.x = a.y * b.z - a.z * b.y;
		out.y = tempy;
		out.z = tempz;
	}

}
