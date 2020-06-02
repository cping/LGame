/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.geom;

import java.io.Serializable;

import loon.LSystem;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Vector4f implements Serializable, XYZW {

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

	public final static Vector4f at(float x, float y, float z, float w) {
		return new Vector4f(x, y, z, w);
	}

	public final static Vector4f smoothStep(Vector4f a, Vector4f b, float amount) {
		return new Vector4f(MathUtils.smoothStep(a.x, b.x, amount), MathUtils.smoothStep(a.y, b.y, amount),
				MathUtils.smoothStep(a.z, b.z, amount), MathUtils.smoothStep(a.w, b.w, amount));
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
		return copy().addSelf(x, y, z, w);
	}

	public Vector4f addSelf(float x, float y, float z, float w) {
		return set(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	public Vector4f copy() {
		return new Vector4f(this);
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

	public Vector4f normalize() {
		return copy().normalizeSelf();
	}

	public Vector4f normalizeSelf() {
		float l = length();

		if (l == 0 || l == 1)
			return this;

		return set(x / l, y / l, z / l, w / l);
	}

	public Vector4f normalize3() {
		return copy().normalize3Self();
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
		return copy().lerpSelf(target, alpha);
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

	public Vector4f set(Vector4f v) {
		return set(v.x, v.y, v.z, v.w);
	}

	public float getX() {
		return x;
	}

	public Vector4f setX(float x) {
		this.x = x;
		return this;
	}

	public float getY() {
		return y;
	}

	public Vector4f setY(float y) {
		this.y = y;
		return this;
	}

	@Override
	public float getZ() {
		return z;
	}

	public Vector4f setZ(float z) {
		this.z = z;
		return this;
	}

	@Override
	public float getW() {
		return w;
	}

	public Vector4f setW(float w) {
		this.w = w;
		return this;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		result = prime * result + NumberUtils.floatToIntBits(z);
		result = prime * result + NumberUtils.floatToIntBits(w);
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}
