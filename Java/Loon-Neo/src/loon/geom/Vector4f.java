/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a cpy of
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
 * @version 0.5
 */
package loon.geom;

import java.io.Serializable;

import loon.LSystem;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;
import loon.utils.reply.TChange;

public class Vector4f implements Serializable, XYZW, SetXYZW {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5987567676643500192L;

	private static final Array<Vector4f> _VEC4_CACHE = new Array<Vector4f>();

	public final static Vector4f TMP() {
		Vector4f temp = _VEC4_CACHE.pop();
		if (temp == null) {
			_VEC4_CACHE.add(temp = new Vector4f(0, 0, 0, 0));
		}
		return temp;
	}

	public final static Vector4f at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Vector4f();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 1) {
			try {
				float x = 0;
				float y = 0;
				float z = 0;
				float w = 0;
				if (len >= 2) {
					x = Float.parseFloat(result[0].trim());
					y = Float.parseFloat(result[1].trim());
				}
				if (len >= 3) {
					z = Float.parseFloat(result[2].trim());
				}
				if (len >= 4) {
					w = Float.parseFloat(result[3].trim());
				}
				return new Vector4f(x, y, z, w);
			} catch (Exception ex) {
			}
		}
		return new Vector4f();
	}

	public final static Vector4f[] of(int n) {
		return of(n, 0f);
	}

	public final static Vector4f[] of(int n, float v) {
		Vector4f[] result = new Vector4f[n];
		for (int i = 0; i < n; i++) {
			result[i] = new Vector4f(v);
		}
		return result;
	}

	public final static Vector4f x(float x) {
		return new Vector4f(x, 0f, 0f, 0f);
	}

	public final static Vector4f y(float y) {
		return new Vector4f(0f, y, 0f, 0f);
	}

	public final static Vector4f z(float z) {
		return new Vector4f(0f, 0f, z, 0f);
	}

	public final static Vector4f w(float w) {
		return new Vector4f(0f, 0f, 0f, w);
	}

	public final static boolean isNan(XYZW v) {
		return MathUtils.isNan(v.getX()) || MathUtils.isNan(v.getY()) || MathUtils.isNan(v.getZ())
				|| MathUtils.isNan(v.getW());
	}

	public final static boolean greaterThan(XYZW a, XYZW b) {
		float a1 = MathUtils.mag(a.getX(), a.getY(), a.getZ(), a.getW());
		float b1 = MathUtils.mag(b.getX(), b.getY(), b.getZ(), b.getW());
		return a1 > b1;
	}

	public final static boolean lessThan(XYZW a, XYZW b) {
		float a1 = MathUtils.mag(a.getX(), a.getY(), a.getZ(), a.getW());
		float b1 = MathUtils.mag(b.getX(), b.getY(), b.getZ(), b.getW());
		return a1 < b1;
	}

	public final static boolean equal(XYZW a, XYZW b) {
		float a1 = MathUtils.mag(a.getX(), a.getY(), a.getZ(), a.getW());
		float b1 = MathUtils.mag(b.getX(), b.getY(), b.getZ(), b.getW());
		return a1 == b1;
	}

	public final static boolean greaterThanOrEqual(XYZW a, XYZW b) {
		return greaterThan(a, b) || equal(a, b);
	}

	public final static boolean lessThanOrEqual(XYZW a, XYZW b) {
		return lessThan(a, b) || equal(a, b);
	}

	public final static Vector4f ZERO() {
		return new Vector4f(0);
	}

	public final static Vector4f ONE() {
		return new Vector4f(1);
	}

	public final static Vector4f AXIS_X() {
		return new Vector4f(1, 0, 0, 0);
	}

	public final static Vector4f AXIS_Y() {
		return new Vector4f(0, 1, 0, 0);
	}

	public final static Vector4f AXIS_Z() {
		return new Vector4f(0, 0, 1, 0);
	}

	public final static Vector4f AXIS_W() {
		return new Vector4f(0, 0, 0, 1);
	}

	public final static Vector4f all(float v) {
		return new Vector4f(v, v, v, v);
	}

	public final static Vector4f at(float x, float y, float z, float w) {
		return new Vector4f(x, y, z, w);
	}

	public final static Vector4f smoothStep(Vector4f a, Vector4f b, float amount) {
		return new Vector4f(MathUtils.smoothStep(a.x, b.x, amount), MathUtils.smoothStep(a.y, b.y, amount),
				MathUtils.smoothStep(a.z, b.z, amount), MathUtils.smoothStep(a.w, b.w, amount));
	}

	public static float dot(Vector4f a, Vector4f b) {
		return (a.x * b.x) + (a.y * b.y) + (a.z * b.z) + (a.w * b.w);
	}

	public static float dst(Vector4f a, Vector4f b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		float w = a.w - b.w;

		return MathUtils.sqrt((x * x) + (y * y) + (z * z) + (w * w));
	}

	public static float dst2(Vector4f a, Vector4f b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		float w = a.w - b.w;

		return (x * x) + (y * y) + (z * z) + (w * w);
	}

	public static Vector4f add(Vector4f v1, Vector4f v2) {
		return add(v1, v2, new Vector4f());
	}

	public static Vector4f add(Vector4f v1, Vector4f v2, Vector4f out) {
		out.x = v1.x + v2.x;
		out.y = v1.y + v2.y;
		out.z = v1.z + v2.z;
		out.w = v1.w + v2.w;
		return out;
	}

	public static Vector4f add(Vector4f v1, float x, float y, float z, float w) {
		return add(v1, x, y, z, w, new Vector4f());
	}

	public static Vector4f add(Vector4f v1, float x, float y, float z, float w, Vector4f out) {
		out.x = v1.x + x;
		out.y = v1.y + y;
		out.z = v1.z + z;
		out.w = v1.w + w;
		return out;
	}

	public static Vector4f add(Vector4f v1, float v) {
		return add(v1, v, new Vector4f());
	}

	public static Vector4f add(Vector4f v1, float v, Vector4f out) {
		return add(v1, v, v, v, v, out);
	}

	public static Vector4f sub(Vector4f v1, Vector4f v2) {
		return sub(v1, v2, new Vector4f());
	}

	public static Vector4f sub(Vector4f v1, Vector4f v2, Vector4f out) {
		out.x = v1.x - v2.x;
		out.y = v1.y - v2.y;
		out.z = v1.z - v2.z;
		out.w = v1.w - v2.w;
		return out;
	}

	public static Vector4f sub(Vector4f v1, float x, float y, float z, float w) {
		return sub(v1, x, y, z, w, new Vector4f());
	}

	public static Vector4f sub(Vector4f v1, float x, float y, float z, float w, Vector4f out) {
		out.x = v1.x - x;
		out.y = v1.y - y;
		out.z = v1.z - z;
		out.w = v1.w - w;
		return out;
	}

	public static Vector4f sub(Vector4f v1, float v) {
		return sub(v1, v, new Vector4f());
	}

	public static Vector4f sub(Vector4f v1, float v, Vector4f out) {
		return sub(v1, v, v, v, v, out);
	}

	public static Vector4f mul(Vector4f v1, Vector4f v2) {
		return mul(v1, v2, new Vector4f());
	}

	public static Vector4f mul(Vector4f v1, Vector4f v2, Vector4f out) {
		out.x = v1.x * v2.x;
		out.y = v1.y * v2.y;
		out.z = v1.z * v2.z;
		out.w = v1.w * v2.w;
		return out;
	}

	public static Vector4f mul(Vector4f v1, float x, float y, float z, float w) {
		return mul(v1, x, y, z, w, new Vector4f());
	}

	public static Vector4f mul(Vector4f v1, float x, float y, float z, float w, Vector4f out) {
		out.x = v1.x * x;
		out.y = v1.y * y;
		out.z = v1.z * z;
		out.w = v1.w * w;
		return out;
	}

	public static Vector4f mul(Vector4f v1, float v) {
		return mul(v1, v, new Vector4f());
	}

	public static Vector4f mul(Vector4f v1, float v, Vector4f out) {
		return mul(v1, v, v, v, v, out);
	}

	public static void min(Vector4f a, Vector4f b, Vector4f o) {
		o.x = MathUtils.min(a.x, b.x);
		o.y = MathUtils.min(a.y, b.y);
		o.z = MathUtils.min(a.z, b.z);
		o.w = MathUtils.min(a.w, b.w);
	}

	public static void max(Vector4f a, Vector4f b, Vector4f o) {
		o.x = MathUtils.max(a.x, b.x);
		o.y = MathUtils.max(a.y, b.y);
		o.z = MathUtils.max(a.z, b.z);
		o.w = MathUtils.max(a.w, b.w);
	}

	public static Vector4f moveTowards(Vector4f current, Vector4f target, float maxDistanceDelta) {
		return moveTowards(current, target, maxDistanceDelta, new Vector4f());
	}

	public static Vector4f moveTowards(Vector4f current, Vector4f target, float maxDistanceDelta, Vector4f out) {
		float vector_x = target.x - current.x;
		float vector_y = target.y - current.y;
		float vector_z = target.z - current.z;
		float vector_w = target.w - current.w;
		float sqdist = vector_x * vector_x + vector_y * vector_y + vector_z * vector_z + vector_w * vector_w;
		if (sqdist == 0 || (maxDistanceDelta >= 0 && sqdist <= maxDistanceDelta * maxDistanceDelta)) {
			return target;
		}
		float dist = MathUtils.sqrt(sqdist);
		float newX = current.x + vector_x / dist * maxDistanceDelta;
		float newY = current.y + vector_y / dist * maxDistanceDelta;
		float newZ = current.z + vector_z / dist * maxDistanceDelta;
		float newW = current.w + vector_w / dist * maxDistanceDelta;
		if (out == null) {
			out = new Vector4f(newX, newY, newZ, newW);
		} else {
			out.set(newX, newY, newZ, newW);
		}
		return out;
	}

	public static Vector4f div(Vector4f v1, Vector4f v2) {
		return div(v1, v2, new Vector4f());
	}

	public static Vector4f div(Vector4f v1, Vector4f v2, Vector4f out) {
		out.x = v1.x / v2.x;
		out.y = v1.y / v2.y;
		out.z = v1.z / v2.z;
		out.w = v1.w / v2.w;
		return out;
	}

	public static Vector4f div(Vector4f v1, float x, float y, float z, float w) {
		return div(v1, x, y, z, w, new Vector4f());
	}

	public static Vector4f div(Vector4f v1, float x, float y, float z, float w, Vector4f out) {
		out.x = v1.x / x;
		out.y = v1.y / y;
		out.z = v1.z / z;
		out.w = v1.w / w;
		return out;
	}

	public static Vector4f div(Vector4f v1, float v) {
		return div(v1, v, new Vector4f());
	}

	public static Vector4f div(Vector4f v1, float v, Vector4f out) {
		return div(v1, v, v, v, v, out);
	}

	public static void clamp(Vector4f v, Vector4f min, Vector4f max, Vector4f o) {
		float x = v.x;
		float y = v.y;
		float z = v.z;
		float w = v.w;

		float mineX = min.x;
		float mineY = min.y;
		float mineZ = min.z;
		float mineW = min.w;

		float maxeX = max.x;
		float maxeY = max.y;
		float maxeZ = max.z;
		float maxeW = max.w;

		x = (x > maxeX) ? maxeX : x;
		x = (x < mineX) ? mineX : x;

		y = (y > maxeY) ? maxeY : y;
		y = (y < mineY) ? mineY : y;

		z = (z > maxeZ) ? maxeZ : z;
		z = (z < mineZ) ? mineZ : z;

		w = (w > maxeW) ? maxeW : w;
		w = (w < mineW) ? mineW : w;

		o.x = x;
		o.y = y;
		o.z = z;
		o.w = w;
	}

	public float x, y, z, w;

	public Vector4f() {
		this(0, 0, 0, 0);
	}

	public Vector4f(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	public Vector4f(float v) {
		this(v, v, v, v);
	}

	public Vector4f(XYZW v) {
		set(v);
	}

	public Vector4f(Vector2f v, float z, float w) {
		this(v.getX(), v.getY(), z, w);
	}

	public Vector4f(float x, Vector2f v, float w) {
		this(x, v.getX(), v.getY(), w);
	}

	public Vector4f(float x, float y, Vector2f v) {
		this(x, y, v.getX(), v.getY());
	}

	public Vector4f(Vector3f v, float w) {
		this(v.getX(), v.getY(), v.getZ(), w);
	}

	public Vector4f(float x, Vector3f v) {
		this(x, v.getX(), v.getY(), v.getZ());
	}

	public Vector4f(Vector4f v) {
		this(v.x, v.y, v.z, v.w);
	}

	public Vector4f set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;

		return this;
	}

	public Vector4f add(Vector4f v) {
		return add(v.x, v.y, v.z, v.w);
	}

	public Vector4f add(float x, float y, float z, float w) {
		return cpy().addSelf(x, y, z, w);
	}

	public Vector4f addSelf(float x, float y, float z, float w) {
		return set(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	public Vector4f add(Vector3f v, float w) {
		return add(v.x, v.y, v.z, w);
	}

	public Vector4f addSelf(Vector3f v, float w) {
		return addSelf(v.x, v.y, v.z, w);
	}

	public Vector4f add(float x, Vector3f v) {
		return add(x, v.x, v.y, v.z);
	}

	public Vector4f addSelf(float x, Vector3f v) {
		return addSelf(x, v.x, v.y, v.z);
	}

	public Vector4f add(Vector2f v, float z, float w) {
		return add(v.x, v.y, z, w);
	}

	public Vector4f addSelf(Vector2f v, float z, float w) {
		return addSelf(v.x, v.y, z, w);
	}

	public Vector4f add(Vector2f v1, Vector2f v2) {
		return add(v1.x, v1.y, v2.x, v2.y);
	}

	public Vector4f addSelf(Vector2f v1, Vector2f v2) {
		return addSelf(v1.x, v1.y, v2.x, v2.y);
	}

	public Vector4f add(float x, float y, Vector2f v) {
		return add(x, y, v.x, v.y);
	}

	public Vector4f addSelf(float x, float y, Vector2f v) {
		return addSelf(x, y, v.x, v.y);
	}

	public Vector4f smoothStep(Vector4f v, float amount) {
		return smoothStep(this, v, amount);
	}

	public Vector4f snap(int gridSize) {
		return Vector4f.at(MathUtils.snapFloor(x, gridSize), MathUtils.snapFloor(y, gridSize),
				MathUtils.snapFloor(z, gridSize), MathUtils.snapFloor(w, gridSize));
	}

	public Vector4f subtract(Vector4f v) {
		return add(-v.x, -v.y, -v.z, -v.w);
	}

	public Vector4f subtractSelf(Vector4f v) {
		return addSelf(-v.x, -v.y, -v.z, -v.w);
	}

	public Vector4f subtract(Vector3f v, float w) {
		return subtract(v.x, v.y, v.z, w);
	}

	public Vector4f subtract(float x, float y, float z, float w) {
		return add(-x, -y, -z, -w);
	}

	public Vector4f subtractSelf(Vector3f v, float w) {
		return subtractSelf(v.x, v.y, v.z, w);
	}

	public Vector4f subtractSelf(float x, float y, float z, float w) {
		return addSelf(-x, -y, -z, -w);
	}

	public Vector4f subtract(float x, Vector3f v) {
		return subtract(x, v.x, v.y, v.z);
	}

	public Vector4f subtractSelf(float x, Vector3f v) {
		return subtractSelf(x, v.x, v.y, v.z);
	}

	public Vector4f subtract(Vector2f v, float z, float w) {
		return subtract(v.x, v.y, z, w);
	}

	public Vector4f subtractSelf(Vector2f v, float z, float w) {
		return subtractSelf(v.x, v.y, z, w);
	}

	public Vector4f subtract(Vector2f v1, Vector2f v2) {
		return subtract(v1.x, v1.y, v2.x, v2.y);
	}

	public Vector4f subtractSelf(Vector2f v1, Vector2f v2) {
		return subtractSelf(v1.x, v1.y, v2.x, v2.y);
	}

	public Vector4f subtract(float x, float y, Vector2f v) {
		return subtract(x, y, v.x, v.y);
	}

	public Vector4f subtractSelf(float x, float y, Vector2f v) {
		return subtractSelf(x, y, v.x, v.y);
	}

	public Vector4f scale(float s) {
		return scale(s, s, s, s);
	}

	public Vector4f scale(float sx, float sy, float sz, float sw) {
		return new Vector4f(x * sx, y * sy, z * sz, w * sw);
	}

	public float dot(Vector4f v) {
		return x * v.x + y * v.y + z * v.z + w * v.w;
	}

	public boolean hasSameDirection(Vector4f v) {
		return dot(v) > 0;
	}

	public boolean hasOppositeDirection(Vector4f v) {
		return dot(v) < 0;
	}

	public boolean hasDifferentValues(Vector4f vector) {
		return this.x != vector.x || this.y != vector.y || this.z != vector.z || this.w != vector.w;
	}

	public Vector4f normalize() {
		return cpy().normalizeSelf();
	}

	public Vector4f normalizeSelf() {
		float l = length();

		if (l == 0 || l == 1)
			return this;

		return set(x / l, y / l, z / l, w / l);
	}

	public Vector4f normalize3() {
		return cpy().normalize3Self();
	}

	public Vector4f normalize3Self() {
		float l = MathUtils.sqrt(x * x + y * y + z * z);

		if (l == 0 || l == 1) {
			return this;
		}

		return set(x / l, y / l, z / l, w / l);
	}

	public float length() {
		return MathUtils.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

	public Vector4f negate() {
		return new Vector4f(-x, -y, -z, -w);
	}

	public Vector4f negateSelf() {
		return set(-x, -y, -z, -w);
	}

	public Vector4f multiply(Vector4f v) {
		return scale(v.x, v.y, v.z, v.w);
	}

	public Vector4f multiplySelf(Vector4f v) {
		return scaleSelf(v.x, v.y, v.z, v.w);
	}

	public Vector4f min(Vector4f other) {
		min(this, other, cpy());
		return this;
	}

	public Vector4f max(Vector4f other) {
		max(this, other, cpy());
		return this;
	}

	public Vector4f minSelf(Vector4f other) {
		min(this, other, this);
		return this;
	}

	public Vector4f maxSelf(Vector4f other) {
		max(this, other, this);
		return this;
	}

	public Vector4f scaleSelf(float sx, float sy, float sz, float sw) {
		return set(x * sx, y * sy, z * sz, w * sw);
	}

	public Vector4f translate(float dx, float dy, float dz, float dw) {
		return cpy().translateSelf(dx, dy, dz, dw);
	}

	public Vector4f translateSelf(float dx, float dy, float dz, float dw) {
		return set(this.x + dx, this.y + dy, this.z + dz, this.w + dw);
	}

	public Vector4f lerp(Vector4f target, float alpha) {
		return cpy().lerpSelf(target, alpha);
	}

	public Vector4f lerpSelf(Vector4f target, float alpha) {
		Vector4f temp = Vector4f.TMP();
		scaleSelf(1f - alpha).addSelf(temp.set(target).scaleSelf(alpha));
		return this;
	}

	public Vector4f addSelf(Vector4f v) {
		return addSelf(v.x, v.y, v.z, v.w);
	}

	public Vector4f scaleSelf(float s) {
		return scaleSelf(s, s, s, s);
	}

	public Vector4f set(XYZW v) {
		return set(v.getX(), v.getY(), v.getZ(), v.getW());
	}

	public Vector4f set(Vector4f v) {
		return set(v.x, v.y, v.z, v.w);
	}

	public Vector4f exp() {
		return new Vector4f(MathUtils.exp(this.x), MathUtils.exp(this.y), MathUtils.exp(this.z), MathUtils.exp(this.w));
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public float getW() {
		return w;
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public int z() {
		return (int) z;
	}

	public int w() {
		return (int) w;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public void setW(float w) {
		this.w = w;
	}

	public float getR() {
		return x;
	}

	public Vector4f setR(float r) {
		x = r;
		return this;
	}

	public float getG() {
		return y;
	}

	public Vector4f setG(float g) {
		y = g;
		return this;
	}

	public float getB() {
		return z;
	}

	public Vector4f setB(float b) {
		z = b;
		return this;
	}

	public float getA() {
		return w;
	}

	public Vector4f setA(float a) {
		w = a;
		return this;
	}

	public Vector4f setEmpty() {
		return set(0f);
	}

	public Vector4f set(float v) {
		return set(v, v, v, v);
	}

	public Vector4f set(Vector2f v, float z, float w) {
		return set(v.x, v.y, z, w);
	}

	public Vector4f set(float x, Vector2f v, float w) {
		return set(x, v.x, v.y, w);
	}

	public Vector4f set(float x, float y, Vector2f v) {
		return set(x, y, v.x, v.y);
	}

	public Vector4f set(Vector3f v, float w) {
		return set(v.x, v.y, v.z, w);
	}

	public Vector4f set(float x, Vector3f v) {
		return set(x, v.x, v.y, v.z);
	}

	public Vector4f cpy() {
		return new Vector4f(this);
	}

	public Vector2f getXX() {
		return new Vector2f(x, x);
	}

	public Vector2f getXY() {
		return new Vector2f(x, y);
	}

	public Vector2f getXZ() {
		return new Vector2f(x, z);
	}

	public Vector2f getXW() {
		return new Vector2f(x, w);
	}

	public Vector2f getYX() {
		return new Vector2f(y, x);
	}

	public Vector2f getYY() {
		return new Vector2f(y, y);
	}

	public Vector2f getYZ() {
		return new Vector2f(y, z);
	}

	public Vector2f getYW() {
		return new Vector2f(y, w);
	}

	public Vector2f getZX() {
		return new Vector2f(z, x);
	}

	public Vector2f getZY() {
		return new Vector2f(z, y);
	}

	public Vector2f getZZ() {
		return new Vector2f(z, z);
	}

	public Vector2f getZW() {
		return new Vector2f(z, w);
	}

	public Vector2f getWX() {
		return new Vector2f(w, x);
	}

	public Vector2f getWY() {
		return new Vector2f(w, y);
	}

	public Vector2f getWZ() {
		return new Vector2f(w, z);
	}

	public Vector2f getWW() {
		return new Vector2f(w, w);
	}

	public Vector3f getXXX() {
		return new Vector3f(x, x, x);
	}

	public Vector3f getXXY() {
		return new Vector3f(x, x, y);
	}

	public Vector3f getXXZ() {
		return new Vector3f(x, x, z);
	}

	public Vector3f getXXW() {
		return new Vector3f(x, x, w);
	}

	public Vector3f getXYX() {
		return new Vector3f(x, y, x);
	}

	public Vector3f getXYY() {
		return new Vector3f(x, y, y);
	}

	public Vector3f getXYZ() {
		return new Vector3f(x, y, z);
	}

	public Vector3f getXYW() {
		return new Vector3f(x, y, w);
	}

	public Vector3f getXZX() {
		return new Vector3f(x, z, x);
	}

	public Vector3f getXZY() {
		return new Vector3f(x, z, y);
	}

	public Vector3f getXZZ() {
		return new Vector3f(x, z, z);
	}

	public Vector3f getXZW() {
		return new Vector3f(x, z, w);
	}

	public Vector3f getXWX() {
		return new Vector3f(x, w, x);
	}

	public Vector3f getXWY() {
		return new Vector3f(x, w, y);
	}

	public Vector3f getXWZ() {
		return new Vector3f(x, w, z);
	}

	public Vector3f getXWW() {
		return new Vector3f(x, w, w);
	}

	public Vector3f getYXX() {
		return new Vector3f(y, x, x);
	}

	public Vector3f getYXY() {
		return new Vector3f(y, x, y);
	}

	public Vector3f getYXZ() {
		return new Vector3f(y, x, z);
	}

	public Vector3f getYXW() {
		return new Vector3f(y, x, w);
	}

	public Vector3f getYYX() {
		return new Vector3f(y, y, x);
	}

	public Vector3f getYYY() {
		return new Vector3f(y, y, y);
	}

	public Vector3f getYYZ() {
		return new Vector3f(y, y, z);
	}

	public Vector3f getYYW() {
		return new Vector3f(y, y, w);
	}

	public Vector3f getYZX() {
		return new Vector3f(y, z, x);
	}

	public Vector3f getYZY() {
		return new Vector3f(y, z, y);
	}

	public Vector3f getYZZ() {
		return new Vector3f(y, z, z);
	}

	public Vector3f getYZW() {
		return new Vector3f(y, z, w);
	}

	public Vector3f getYWX() {
		return new Vector3f(y, w, x);
	}

	public Vector3f getYWY() {
		return new Vector3f(y, w, y);
	}

	public Vector3f getYWZ() {
		return new Vector3f(y, w, z);
	}

	public Vector3f getYWW() {
		return new Vector3f(y, w, w);
	}

	public Vector3f getZXX() {
		return new Vector3f(z, x, x);
	}

	public Vector3f getZXY() {
		return new Vector3f(z, x, y);
	}

	public Vector3f getZXZ() {
		return new Vector3f(z, x, z);
	}

	public Vector3f getZXW() {
		return new Vector3f(z, x, w);
	}

	public Vector3f getZYX() {
		return new Vector3f(z, y, x);
	}

	public Vector3f getZYY() {
		return new Vector3f(z, y, y);
	}

	public Vector3f getZYZ() {
		return new Vector3f(z, y, z);
	}

	public Vector3f getZYW() {
		return new Vector3f(z, y, w);
	}

	public Vector3f getZZX() {
		return new Vector3f(z, z, x);
	}

	public Vector3f getZZY() {
		return new Vector3f(z, z, y);
	}

	public Vector3f getZZZ() {
		return new Vector3f(z, z, z);
	}

	public Vector3f getZZW() {
		return new Vector3f(z, z, w);
	}

	public Vector3f getZWX() {
		return new Vector3f(z, w, x);
	}

	public Vector3f getZWY() {
		return new Vector3f(z, w, y);
	}

	public Vector3f getZWZ() {
		return new Vector3f(z, w, z);
	}

	public Vector3f getZWW() {
		return new Vector3f(z, w, w);
	}

	public Vector3f getWXX() {
		return new Vector3f(w, x, x);
	}

	public Vector3f getWXY() {
		return new Vector3f(w, x, y);
	}

	public Vector3f getWXZ() {
		return new Vector3f(w, x, z);
	}

	public Vector3f getWXW() {
		return new Vector3f(w, x, w);
	}

	public Vector3f getWYX() {
		return new Vector3f(w, y, x);
	}

	public Vector3f getWYY() {
		return new Vector3f(w, y, y);
	}

	public Vector3f getWYZ() {
		return new Vector3f(w, y, z);
	}

	public Vector3f getWYW() {
		return new Vector3f(w, y, w);
	}

	public Vector3f getWZX() {
		return new Vector3f(w, z, x);
	}

	public Vector3f getWZY() {
		return new Vector3f(w, z, y);
	}

	public Vector3f getWZZ() {
		return new Vector3f(w, z, z);
	}

	public Vector3f getWZW() {
		return new Vector3f(w, z, w);
	}

	public Vector3f getWWX() {
		return new Vector3f(w, w, x);
	}

	public Vector3f getWWY() {
		return new Vector3f(w, w, y);
	}

	public Vector3f getWWZ() {
		return new Vector3f(w, w, z);
	}

	public Vector3f getWWW() {
		return new Vector3f(w, w, w);
	}

	public Vector2f getRR() {
		return new Vector2f(x, x);
	}

	public Vector2f getRG() {
		return new Vector2f(x, y);
	}

	public Vector2f getRB() {
		return new Vector2f(x, z);
	}

	public Vector2f getRA() {
		return new Vector2f(x, w);
	}

	public Vector2f getGR() {
		return new Vector2f(y, x);
	}

	public Vector2f getGG() {
		return new Vector2f(y, y);
	}

	public Vector2f getGB() {
		return new Vector2f(y, z);
	}

	public Vector2f getGA() {
		return new Vector2f(y, w);
	}

	public Vector2f getBR() {
		return new Vector2f(z, x);
	}

	public Vector2f getBG() {
		return new Vector2f(z, y);
	}

	public Vector2f getBB() {
		return new Vector2f(z, z);
	}

	public Vector2f getBA() {
		return new Vector2f(z, w);
	}

	public Vector2f getAR() {
		return new Vector2f(w, x);
	}

	public Vector2f getAG() {
		return new Vector2f(w, y);
	}

	public Vector2f getAB() {
		return new Vector2f(w, z);
	}

	public Vector2f getAA() {
		return new Vector2f(w, w);
	}

	public Vector3f getRRR() {
		return new Vector3f(x, x, x);
	}

	public Vector3f getRRG() {
		return new Vector3f(x, x, y);
	}

	public Vector3f getRRB() {
		return new Vector3f(x, x, z);
	}

	public Vector3f getRRA() {
		return new Vector3f(x, x, w);
	}

	public Vector3f getRGR() {
		return new Vector3f(x, y, x);
	}

	public Vector3f getRGG() {
		return new Vector3f(x, y, y);
	}

	public Vector3f getRGB() {
		return new Vector3f(x, y, z);
	}

	public Vector3f getRGA() {
		return new Vector3f(x, y, w);
	}

	public Vector3f getRBR() {
		return new Vector3f(x, z, x);
	}

	public Vector3f getRBG() {
		return new Vector3f(x, z, y);
	}

	public Vector3f getRBB() {
		return new Vector3f(x, z, z);
	}

	public Vector3f getRBA() {
		return new Vector3f(x, z, w);
	}

	public Vector3f getRAR() {
		return new Vector3f(x, w, x);
	}

	public Vector3f getRAG() {
		return new Vector3f(x, w, y);
	}

	public Vector3f getRAB() {
		return new Vector3f(x, w, z);
	}

	public Vector3f getRAA() {
		return new Vector3f(x, w, w);
	}

	public Vector3f getGRR() {
		return new Vector3f(y, x, x);
	}

	public Vector3f getGRG() {
		return new Vector3f(y, x, y);
	}

	public Vector3f getGRB() {
		return new Vector3f(y, x, z);
	}

	public Vector3f getGRA() {
		return new Vector3f(y, x, w);
	}

	public Vector3f getGGR() {
		return new Vector3f(y, y, x);
	}

	public Vector3f getGGG() {
		return new Vector3f(y, y, y);
	}

	public Vector3f getGGB() {
		return new Vector3f(y, y, z);
	}

	public Vector3f getGGA() {
		return new Vector3f(y, y, w);
	}

	public Vector3f getGBR() {
		return new Vector3f(y, z, x);
	}

	public Vector3f getGBG() {
		return new Vector3f(y, z, y);
	}

	public Vector3f getGBB() {
		return new Vector3f(y, z, z);
	}

	public Vector3f getGBA() {
		return new Vector3f(y, z, w);
	}

	public Vector3f getGAR() {
		return new Vector3f(y, w, x);
	}

	public Vector3f getGAG() {
		return new Vector3f(y, w, y);
	}

	public Vector3f getGAB() {
		return new Vector3f(y, w, z);
	}

	public Vector3f getGAA() {
		return new Vector3f(y, w, w);
	}

	public Vector3f getBRR() {
		return new Vector3f(z, x, x);
	}

	public Vector3f getBRG() {
		return new Vector3f(z, x, y);
	}

	public Vector3f getBRB() {
		return new Vector3f(z, x, z);
	}

	public Vector3f getBRA() {
		return new Vector3f(z, x, w);
	}

	public Vector3f getBGR() {
		return new Vector3f(z, y, x);
	}

	public Vector3f getBGG() {
		return new Vector3f(z, y, y);
	}

	public Vector3f getBGB() {
		return new Vector3f(z, y, z);
	}

	public Vector3f getBGA() {
		return new Vector3f(z, y, w);
	}

	public Vector3f getBBR() {
		return new Vector3f(z, z, x);
	}

	public Vector3f getBBG() {
		return new Vector3f(z, z, y);
	}

	public Vector3f getBBB() {
		return new Vector3f(z, z, z);
	}

	public Vector3f getBBA() {
		return new Vector3f(z, z, w);
	}

	public Vector3f getBAR() {
		return new Vector3f(z, w, x);
	}

	public Vector3f getBAG() {
		return new Vector3f(z, w, y);
	}

	public Vector3f getBAB() {
		return new Vector3f(z, w, z);
	}

	public Vector3f getBAA() {
		return new Vector3f(z, w, w);
	}

	public Vector3f getARR() {
		return new Vector3f(w, x, x);
	}

	public Vector3f getARG() {
		return new Vector3f(w, x, y);
	}

	public Vector3f getARB() {
		return new Vector3f(w, x, z);
	}

	public Vector3f getARA() {
		return new Vector3f(w, x, w);
	}

	public Vector3f getAGR() {
		return new Vector3f(w, y, x);
	}

	public Vector3f getAGG() {
		return new Vector3f(w, y, y);
	}

	public Vector3f getAGB() {
		return new Vector3f(w, y, z);
	}

	public Vector3f getAGA() {
		return new Vector3f(w, y, w);
	}

	public Vector3f getABR() {
		return new Vector3f(w, z, x);
	}

	public Vector3f getABG() {
		return new Vector3f(w, z, y);
	}

	public Vector3f getABB() {
		return new Vector3f(w, z, z);
	}

	public Vector3f getABA() {
		return new Vector3f(w, z, w);
	}

	public Vector3f getAAR() {
		return new Vector3f(w, w, x);
	}

	public Vector3f getAAG() {
		return new Vector3f(w, w, y);
	}

	public Vector3f getAAB() {
		return new Vector3f(w, w, z);
	}

	public Vector3f getAAA() {
		return new Vector3f(w, w, w);
	}

	public Vector4f random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		this.z = MathUtils.random();
		this.w = MathUtils.random();
		return this;
	}

	public ObservableXYZW<Vector4f> observable(TChange<Vector4f> v) {
		return ObservableXYZW.at(v, this, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector4f other = (Vector4f) obj;
		return equals(other.x, other.y, other.z, other.w);
	}

	public boolean equals(float x1, float y1, float z1, float w1) {
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(x1))
			return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(y1))
			return false;
		if (NumberUtils.floatToIntBits(z) != NumberUtils.floatToIntBits(z1))
			return false;
		if (NumberUtils.floatToIntBits(w) != NumberUtils.floatToIntBits(w1))
			return false;
		return true;
	}

	public boolean epsilonEquals(final Vector4f other, float epsilon) {
		if (other == null)
			return false;
		if (MathUtils.abs(other.x - x) > epsilon)
			return false;
		if (MathUtils.abs(other.y - y) > epsilon)
			return false;
		if (MathUtils.abs(other.z - z) > epsilon)
			return false;
		if (MathUtils.abs(other.w - w) > epsilon)
			return false;
		return true;
	}

	public boolean epsilonEquals(float x, float y, float z, float epsilon) {
		if (MathUtils.abs(x - this.x) > epsilon)
			return false;
		if (MathUtils.abs(y - this.y) > epsilon)
			return false;
		if (MathUtils.abs(z - this.z) > epsilon)
			return false;
		if (MathUtils.abs(w - this.w) > epsilon)
			return false;
		return true;
	}

	public boolean greaterThan(Vector4f o) {
		return greaterThan(this, o);
	}

	public boolean lessThan(Vector4f o) {
		return lessThan(this, o);
	}

	public boolean greaterThanOrEqual(Vector4f o) {
		return greaterThanOrEqual(this, o);
	}

	public boolean lessThanOrEqual(Vector4f o) {
		return lessThanOrEqual(this, o);
	}

	public float len() {
		return MathUtils.sqrt(x * x + y * y + z * z + w * w);
	}

	public static float len2(final float x, final float y, final float z, final float w) {
		return x * x + y * y + z * z + w * w;
	}

	public float len2() {
		return x * x + y * y + z * z + w * w;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && w == 0 && z == 0;
	}

	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		hashCode = prime * LSystem.unite(hashCode, z);
		hashCode = prime * LSystem.unite(hashCode, w);
		return hashCode;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}
